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

import android.view.LayoutInflater
import android.view.ViewGroup

import org.kindone.willingtodo.R
import org.kindone.willingtodo.data.TaskContextListItem
import org.kindone.willingtodo.data.TaskContext
import org.kindone.willingtodo.persistence.TaskContextPersistenceProvider
import org.kindone.willingtodo.recyclerlist.RecyclerListAdapter
import org.kindone.willingtodo.recyclerlist.RecyclerListItem
import org.kindone.willingtodo.recyclerlist.RecyclerListItemStartDragListener
import org.kindone.willingtodo.recyclerlist.RecyclerListItemViewHolder

class TaskContextRecyclerListAdapter(private val mItemProvider: TaskContextPersistenceProvider,
                                     dragStartListener: RecyclerListItemStartDragListener) : RecyclerListAdapter<TaskContextListItem>(dragStartListener) {
    private var mVersion: Int = 0

    init {
        init()
        mVersion = mItemProvider.version
    }

    fun init() {
        reloadFromProvider()
    }

    private fun reloadFromProvider() {
        mItems.clear()
        val contexts: List<TaskContext>

        contexts = loadContexts()

        for (context in contexts) {
            mItems.add(TaskContextListItem(context))
        }
    }

    fun refresh(version: Int) {
        if (version != mVersion) {
            reloadFromProvider()
            notifyDataSetChanged()
            mVersion = mItemProvider.version
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contextlist_item, parent, false)
        val listItemViewHolder = TaskContextListItemViewHolder(view)
        return listItemViewHolder
    }

    protected fun loadContexts(): List<TaskContext> {
        return mItemProvider.taskContexts
    }

    override fun tellItemCreated(item: RecyclerListItem): RecyclerListItem {
        val taskContextListItem = item as TaskContextListItem
        return TaskContextListItem(mItemProvider.createTaskContext(taskContextListItem.taskContext))
    }

    override fun tellItemChanged(item: RecyclerListItem) {
        val taskContextListItem = item as TaskContextListItem
        mItemProvider.updateTaskContext(taskContextListItem.taskContext)
    }

    override fun tellItemRemoved(itemId: Long) {
        mItemProvider.deleteTaskContext(itemId)
    }

    override fun tellItemSwapped(itemId1: Long, itemId2: Long) {
        mItemProvider.swapPositionOfTaskContexts(itemId1, itemId2)
    }


}
