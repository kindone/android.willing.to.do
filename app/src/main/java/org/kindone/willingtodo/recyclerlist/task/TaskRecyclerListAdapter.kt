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

package org.kindone.willingtodo.recyclerlist.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.kindone.willingtodo.R
import org.kindone.willingtodo.data.Task
import org.kindone.willingtodo.data.TaskListItem
import org.kindone.willingtodo.persistence.TaskContextPersistenceProvider
import org.kindone.willingtodo.persistence.TaskPersistenceProvider
import org.kindone.willingtodo.recyclerlist.RecyclerListAdapter
import org.kindone.willingtodo.recyclerlist.RecyclerListItem
import org.kindone.willingtodo.recyclerlist.RecyclerListItemStartDragListener
import org.kindone.willingtodo.recyclerlist.RecyclerListItemViewHolder

class TaskRecyclerListAdapter(private val contextId: Long, private val mItemProvider: TaskPersistenceProvider,
                              private val mContextProvider: TaskContextPersistenceProvider,
                              dragStartListener: RecyclerListItemStartDragListener) : RecyclerListAdapter<TaskListItem>(dragStartListener) {
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
        val tasks: List<Task>

        orderByWillingness = mContextProvider.getModeOfTaskContext(contextId) == 1
        tasks = loadTasks()

        for (i in tasks.indices) {
            mItems.add(TaskListItem(tasks[i]))
        }
    }

    fun refresh(version: Int) {
        if (version != mVersion) {
            reloadFromProvider()
            notifyDataSetChanged()
            mVersion = mItemProvider.version
        }
    }

    fun reorderByPriority() {
        mContextProvider.setModeOfTaskContext(contextId, 0)
    }

    fun reorderByWillingness() {
        mContextProvider.setModeOfTaskContext(contextId, 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tasklist_item, parent, false)
        val listItemViewHolder = TaskListItemViewHolder(view)
        listItemViewHolder.setOnLongClickListener(View.OnLongClickListener {
            //TODO
            false
        })
        return listItemViewHolder
    }


    protected fun loadTasks(): List<Task> {
        if (orderByWillingness)
            return loadTasksOrderedByWillingness()
        else
            return loadTasksOrderedByPriority()
    }

    protected fun loadTasksOrderedByPriority(): List<Task> {
        return mItemProvider.getTasksOfContextOrderedByPriority(contextId)
    }

    protected fun loadTasksOrderedByWillingness(): List<Task> {
        return mItemProvider.getTasksOfContextOrderedByWillingness(contextId)
    }

    override fun tellItemCreated(item: RecyclerListItem): RecyclerListItem {
        val taskListItem = item as TaskListItem
        mItemProvider.createTask(taskListItem.task)
        return item // TODO
    }

    override fun tellItemChanged(item: RecyclerListItem) {
        val taskListItem = item as TaskListItem
        mItemProvider.updateTask(taskListItem.task)
    }

    override fun tellItemRemoved(taskId: Long) {
        mItemProvider.deleteTask(taskId)
    }

    override fun tellItemSwapped(itemId1: Long, itemId2: Long) {
        if (orderByWillingness)
            tellTaskWillingnessSwapped(itemId1, itemId2)
        else
            tellTaskPrioritySwapped(itemId1, itemId2)
    }

    protected fun tellTaskPrioritySwapped(itemId1: Long, itemId2: Long) {
        mItemProvider.swapPriorityOfTasks(itemId1, itemId2)
    }

    protected fun tellTaskWillingnessSwapped(itemId1: Long, itemId2: Long) {
        mItemProvider.swapWillingnessOfTasks(itemId1, itemId2)
    }

}
