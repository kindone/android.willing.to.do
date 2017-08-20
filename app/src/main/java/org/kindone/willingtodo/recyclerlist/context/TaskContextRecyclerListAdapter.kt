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

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup

import org.kindone.willingtodo.R
import org.kindone.willingtodo.data.TaskContext
import org.kindone.willingtodo.data.TaskContextListItem
import org.kindone.willingtodo.event.EventListenerMap
import org.kindone.willingtodo.recyclerlist.*
import org.kindone.willingtodo.touchhelper.ItemTouchHelperAdapter
import java.util.*

class TaskContextRecyclerListAdapter(private val mDragStartListener: RecyclerListItemStartDragListener)
    : RecyclerView.Adapter<TaskContextListItemViewHolder>(),
        ItemTouchHelperAdapter, ListEventDispatcher<RecyclerListItem> {

    protected val mItems: MutableList<TaskContextListItem> = ArrayList()

    override var eventListeners: EventListenerMap = mutableMapOf()

    init {

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

    private fun initializeViewHolder(holder: TaskContextListItemViewHolder, position: Int) {
        holder.setListItemId(getItem(position).getId())
        holder.setTitle(getItem(position).getTitle())
        holder.setDragEventListener(mDragStartListener)
    }





    fun onLoadItem(items:List<TaskContext>)
    {
        loadItemsToList(items)
    }

    fun onCreateItem(position: Int, item: RecyclerListItem) {
        insertItemToList(position, item as TaskContextListItem)
        dispatchItemInsertEvent(ListItemInsertEvent(item))
    }

    fun onUpdateItem(updatedItem: RecyclerListItem) {
        val itemInList = findItemById(updatedItem.getId())
        if (itemInList != null) {
            replaceItemInList(itemInList.position, updatedItem as TaskContextListItem)
            dispatchItemUpdateEvent(ListItemUpdateEvent(updatedItem))
            return
        }
    }

    fun onUpdateItem(itemId: Long, updater: RecyclerListItem.Updater) {
        val itemInList = findItemById(itemId)
        if (itemInList != null) {
            val updatedItem = updater.update(itemInList.item) as TaskContextListItem
            replaceItemInList(itemInList.position, updatedItem)
            dispatchItemUpdateEvent(ListItemUpdateEvent(updatedItem))
            return
        }
    }


    override fun onItemDismiss(position: Int) {
        val taskItem = getItem(position)
        removeItemFromList(position)
        dispatchItemRemoveEvent(ListItemRemoveEvent(taskItem))
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val taskItem1 = getItem(fromPosition)
        val taskItem2 = getItem(toPosition)

        swapItemsInList(fromPosition, toPosition)

        dispatchItemSwapEvent(ListItemSwapEvent(taskItem1, taskItem2))

        return true
    }





    private fun loadItemsToList(items:List<TaskContext>)
    {
        mItems.clear()
        for(item in items) {
            mItems.add(TaskContextListItem(item))
        }
        notifyDataSetChanged()
    }

    private fun insertItemToList(position: Int, item: TaskContextListItem) {
        mItems.add(position, item)
        notifyItemInserted(position)
    }

    private fun removeItemFromList(position: Int) {
        mItems.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun replaceItemInList(position: Int, updatedItem: TaskContextListItem) {
        mItems[position] = updatedItem
        notifyItemChanged(position)
    }

    private fun swapItemsInList(pos1: Int, pos2: Int) {
        Collections.swap(mItems, pos1, pos2)
        notifyItemMoved(pos1, pos2)
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
