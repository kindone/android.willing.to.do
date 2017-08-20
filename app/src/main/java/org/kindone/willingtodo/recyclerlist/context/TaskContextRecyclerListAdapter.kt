/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kindone.willingtodo.recyclerlist.context

import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import org.kindone.willingtodo.R
import org.kindone.willingtodo.data.TaskContextListItem
import org.kindone.willingtodo.data.TaskContext
import org.kindone.willingtodo.event.EventListenerMap
import org.kindone.willingtodo.persistence.TaskContextPersistenceProvider
import org.kindone.willingtodo.recyclerlist.*
import org.kindone.willingtodo.touchhelper.ItemTouchHelperAdapter
import java.util.*

class TaskContextRecyclerListAdapter(private val mTaskContextProvider: TaskContextPersistenceProvider,
                                     private val mDragStartListener: RecyclerListItemStartDragListener)
    : RecyclerView.Adapter<TaskContextListItemViewHolder>(),
        ItemTouchHelperAdapter, ListEventDispatcher<RecyclerListItem> {

    protected val mItems: MutableList<TaskContextListItem> = ArrayList()

    override var eventListeners: EventListenerMap = mutableMapOf()

    private var mVersion: Int = 0


    init {
        initialize()
        mVersion = mTaskContextProvider.version
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskContextListItemViewHolder {
        val listItemView = LayoutInflater.from(parent.context).inflate(R.layout.contextlist_item, parent, false)
        val listItemViewHolder = TaskContextListItemViewHolder(listItemView)
        Log.v("createViewHolder", "count=$itemCount")
        return listItemViewHolder
    }

    override fun onBindViewHolder(holder: TaskContextListItemViewHolder, position: Int) {
        Log.v("bindViewHolder", "count=$itemCount, position=$position")
        initializeViewHolder(holder, position)
    }


    fun initialize() {
        reloadFromProvider()
    }

    private fun reloadFromProvider() {
        mItems.clear()
        val contexts: List<TaskContext> = loadContextsFromProvider()

        for (context in contexts) {
            mItems.add(TaskContextListItem(context))
        }
    }

    fun refresh(version: Int) {
        if (version != mVersion) {
            reloadFromProvider()
            notifyDataSetChanged()
            mVersion = mTaskContextProvider.version
        }
    }

    private fun loadContextsFromProvider(): List<TaskContext> {
        return mTaskContextProvider.taskContexts
    }

    fun tellItemCreated(item: RecyclerListItem): RecyclerListItem {
        val taskContextListItem = item as TaskContextListItem
        return TaskContextListItem(mTaskContextProvider.createTaskContext(taskContextListItem.taskContext))
    }

    fun tellItemChanged(item: RecyclerListItem) {
        val taskContextListItem = item as TaskContextListItem
        mTaskContextProvider.updateTaskContext(taskContextListItem.taskContext)
    }

    fun tellItemRemoved(itemId: Long) {
        mTaskContextProvider.deleteTaskContext(itemId)
    }

    fun tellItemSwapped(itemId1: Long, itemId2: Long) {
        mTaskContextProvider.swapPositionOfTaskContexts(itemId1, itemId2)
    }


    private fun initializeViewHolder(holder: TaskContextListItemViewHolder, position: Int) {
        holder.setListItemId(getItem(position).getId())
        holder.setTitle(getItem(position).getTitle())
        holder.setDragEventListener(mDragStartListener)
    }


    fun onCreateItem(position: Int, item: RecyclerListItem) {
        val itemWithProperId = tellItemCreated(item)
        addItem(position, itemWithProperId as TaskContextListItem)
        notifyItemInserted(position)

//        notifyItemRangeChanged(position, mItems.size);
        dispatchItemInsertEvent(ListItemInsertEvent(item))
    }

    fun onUpdateItem(updatedItem: RecyclerListItem) {
        val itemInList = findItemById(updatedItem.getId())
        if (itemInList != null) {
            updateItem(itemInList.position, updatedItem as TaskContextListItem)
            notifyItemChanged(itemInList.position)
            tellItemChanged(updatedItem)
            dispatchItemUpdateEvent(ListItemUpdateEvent(updatedItem))
            return
        }
    }

    fun onUpdateItem(itemId: Long, updater: RecyclerListItem.Updater) {
        val itemInList = findItemById(itemId)
        if (itemInList != null) {
            val updatedItem = updater.update(itemInList.item) as TaskContextListItem
            updateItem(itemInList.position, updatedItem)
            notifyItemChanged(itemInList.position)
            tellItemChanged(updatedItem)
            dispatchItemUpdateEvent(ListItemUpdateEvent(updatedItem))
            return
        }
    }

    private fun findItemById(itemId: Long): FoundItemInList? {
        for (pos in 0..itemCount - 1) {
            val item = getItem(pos)
            if (item.getId() == itemId) {
                return FoundItemInList(item, pos)
            }
        }

        return null
    }


    override fun onItemDismiss(position: Int) {
        val taskItem = getItem(position)
        removeItem(position)
        notifyItemRemoved(position)
        tellItemRemoved(taskItem.getId())
        dispatchItemRemoveEvent(ListItemRemoveEvent(taskItem))
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val taskItem1 = getItem(fromPosition)
        val taskItem2 = getItem(toPosition)

        swapItems(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        tellItemSwapped(taskItem1.getId(), taskItem2.getId())
        dispatchItemSwapEvent(ListItemSwapEvent(taskItem1, taskItem2))

        return true
    }


    private fun addItem(position: Int, item: TaskContextListItem) {
        mItems.add(position, item)
    }

    private fun removeItem(position: Int) {
        mItems.removeAt(position)
    }

    private fun updateItem(position: Int, updatedItem: TaskContextListItem) {
        mItems[position] = updatedItem
    }

    private fun swapItems(pos1: Int, pos2: Int) {
        Collections.swap(mItems, pos1, pos2)
    }

    fun getItem(position: Int): TaskContextListItem {
        return mItems[position]
    }

    override fun getItemId(position: Int): Long {
        return mItems[position].getId()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    internal inner class FoundItemInList(val item: TaskContextListItem, val position: Int)

}
