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

package org.kindone.willingtodo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.kindone.willingtodo.helper.ItemTouchHelperAdapter
import org.kindone.willingtodo.helper.ItemTouchHelperViewHolder
import org.kindone.willingtodo.helper.OnStartDragListener
import org.kindone.willingtodo.helper.TaskDbHelperProvider
import java.util.*

/**
 * Simple RecyclerView.Adapter that implements ItemTouchHelperAdapter to respond to move and
 * dismiss events from a [android.support.v7.widget.helper.ItemTouchHelper].

 * @author Paul Burke (ipaulpro)
 */
class TaskRecyclerListAdapter(private val mContext: Context, private val mDragStartListener: OnStartDragListener, private val mMode: Int) : RecyclerView.Adapter<TaskRecyclerListAdapter.ItemViewHolder>(), ItemTouchHelperAdapter {

    private val mItems = ArrayList<TaskListItem>()
    private val mTaskManipulationListener: TaskRecyclerListFragment.TaskManipulationListener
    private val mTaskLoader: TaskLoader
    private var mVersion: Int = 0


    init {
        mTaskLoader = mContext as TaskLoader
        mTaskManipulationListener = mTaskLoader as TaskRecyclerListFragment.TaskManipulationListener

        init()

    }

    fun init() {
        mItems.clear()
        var tasks: List<Task> = LinkedList()

        if (mMode == R.string.title_priority)
            tasks = mTaskLoader.loadTasksOrderedByPriority()
        else if (mMode == R.string.title_willingness)
            tasks = mTaskLoader.loadTasksOrderedByWillingness()
        else
        // TODO: awaiting list
            tasks = mTaskLoader.loadTasksOrderedByPriority()

        val taskDbHelperProvider = mTaskLoader as TaskDbHelperProvider
        val dbHelper = taskDbHelperProvider.getTaskDbHelper()

        dbHelper.priorityOrderedTasks

        for (i in tasks.indices) {
            mItems.add(TaskListItem(tasks[i]))
        }

        mVersion = mTaskLoader.getVersion()

    }

    fun refresh(version: Int) {
        if (version != mVersion) {
            init()
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tasklist_item, parent, false)

        val itemViewHolder = ItemViewHolder(view)

        return itemViewHolder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]

        holder.titleView.text = item.title
        holder.labelView.text = item.category

        if (position == 0) {
            holder.titleView.setTextColor(ContextCompat.getColor(mContext, R.color.color_priority_high))
        } else
            holder.titleView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))


        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener { v, event ->
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                mDragStartListener.onStartDrag(holder)
            }
            false
        }

        holder.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(v: View): Boolean {
                val intent = Intent(v.context, TaskEditActivity::class.java)
                intent.putExtra(MainActivity.RESULT_TASK_POSITION, position)
                intent.putExtra(MainActivity.RESULT_TASK_ID, item.id)
                intent.putExtra(MainActivity.RESULT_TASK_TITLE, item.title)
                intent.putExtra(MainActivity.RESULT_TASK_CATEGORY, item.category)
                (v.context as Activity).startActivityForResult(intent, MainActivity.INTENT_EDIT_TASK)
                return false
            }
        })
    }

    fun onItemCreate(position: Int, item: TaskListItem) {
        mItems.add(position, item)
        notifyItemInserted(position)
        mTaskManipulationListener.onTaskCreated(item.task)
    }

    override fun onItemDismiss(position: Int) {
        val taskItem = mItems[position]
        mItems.removeAt(position)
        notifyItemRemoved(position)
        mTaskManipulationListener.onTaskRemoved(taskItem.id)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val taskItem1 = mItems[fromPosition]
        val taskItem2 = mItems[toPosition]

        Collections.swap(mItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        if (mMode == R.string.title_priority)
            mTaskManipulationListener.onTaskPrioritySwapped(taskItem1.id, taskItem2.id)
        else if (mMode == R.string.title_willingness)
            mTaskManipulationListener.onTaskWillingnessSwapped(taskItem1.id, taskItem2.id)
        else {
            // TODO: awaiting
        }

        val a = Math.min(fromPosition, toPosition)
        val b = Math.max(fromPosition, toPosition)
        notifyItemRangeChanged(a, b - a + 1)

        return true
    }

    fun onItemUpdate(position: Int, task: Task) {
        val newTaskItem = TaskListItem(task)
        mItems[position] = newTaskItem
        notifyItemChanged(position)
        mTaskManipulationListener.onTaskUpdated(task.id, task)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun getTaskId(position: Int): Long {
        // TODO: manage exceptional cases
        val item = mItems[position]
        return item.id
    }

    fun getTaskTitle(position: Int): String {
        // TODO: manage exceptional cases
        val item = mItems[position]
        return item.title
    }

    internal interface TaskLoader {
        fun getVersion(): Int

        fun loadTasksOrderedByPriority(): List<Task>

        fun loadTasksOrderedByWillingness(): List<Task>
    }

    /**
     * Simple example of a view holder that implements helper.ItemTouchHelperViewHolder and has a
     * "handle" view that initiates a drag event when touched.
     */
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder, View.OnClickListener {


        val titleView: TextView
        val handleView: ImageView
        val labelView: TextView

        private var listener: OnItemClickListener? = null
        private val taskId: Long = 0

        init {
            itemView.setOnClickListener(this)
            titleView = itemView.findViewById(R.id.item_title) as TextView
            handleView = itemView.findViewById(R.id.handle) as ImageView
            labelView = itemView.findViewById(R.id.item_category) as TextView
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }

        override fun onClick(v: View) {

            if (listener != null)
                listener!!.onItemClick(itemView)
        }

        fun setOnItemClickListener(listener: OnItemClickListener) {
            this.listener = listener
        }

    }

    interface OnItemClickListener {

        fun onItemClick(v: View): Boolean
    }


}
