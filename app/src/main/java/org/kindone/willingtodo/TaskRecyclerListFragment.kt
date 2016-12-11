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
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.melnykov.fab.FloatingActionButton
import org.kindone.willingtodo.helper.ContextMenuRecyclerView
import org.kindone.willingtodo.helper.OnStartDragListener
import org.kindone.willingtodo.helper.SimpleItemTouchHelperCallback

/**
 * @author Paul Burke (ipaulpro)
 */
class TaskRecyclerListFragment : Fragment(), OnStartDragListener {

    private val mMode: String? = null
    private var mItemTouchHelper: ItemTouchHelper? = null
    private var mNewButton: FloatingActionButton? = null
    private var mPomodoroBar: RelativeLayout? = null
    private var mClearButton: View? = null
    private var mCallback: TaskManipulationListener? = null
    private var mAdapter: TaskRecyclerListAdapter? = null
    private val mListVersion: Int = 0

    fun refresh(version: Int) {
        mAdapter!!.refresh(version)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = activity as TaskManipulationListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement OnHeadlineSelectedListener")
        }

    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mNewButton = container!!.rootView.findViewById(R.id.fab_new_task) as FloatingActionButton
        mPomodoroBar = container.rootView.findViewById(R.id.bar_pomodoro) as RelativeLayout
        mClearButton = container.rootView.findViewById(R.id.clear)

        val view = ContextMenuRecyclerView(container.context)
        val params: ViewGroup.LayoutParams

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val mode = args.getInt(ARG_MODE)

        mAdapter = TaskRecyclerListAdapter(activity, this, mode)

        val llm = LinearLayoutManager(activity)

        val recyclerView = view as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.setAdapter(mAdapter)
        recyclerView.setLayoutManager(llm)

        registerForContextMenu(view) //context menu

        // floating action button
        mNewButton!!.attachToRecyclerView(recyclerView)
        mNewButton!!.setOnClickListener {
            val intent = Intent(activity, TaskCreateActivity::class.java)
            activity.startActivityForResult(intent, MainActivity.INTENT_CREATE_TASK)
        }

        mPomodoroBar!!.setOnClickListener {
            val intent = Intent(activity, PomodoroControlActivity::class.java)
            intent.setAction(PomodoroControlActivity.ACTION_OPEN)
            activity.startActivityForResult(intent, PomodoroControlActivity.INTENT_OPEN)
        }

        mClearButton!!.setOnClickListener { mPomodoroBar!!.layoutParams = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0) }

        val callback = SimpleItemTouchHelperCallback(mAdapter!!)

        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper!!.attachToRecyclerView(recyclerView)
    }


    override fun onStart() {
        super.onStart()

        if ((activity as MainActivity).isTimerActive) {
            mPomodoroBar!!.layoutParams = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    (96 * resources.displayMetrics.density + 0.5f).toInt())
        } else {
            mPomodoroBar!!.layoutParams = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0)
        }

        Log.d(TAG, "onStart()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        // inflate menu
        val inflater = activity.menuInflater
        inflater.inflate(R.menu.item_context, menu)
        val taskTitle = mAdapter!!.getTaskTitle((menuInfo as ContextMenuRecyclerView.RecyclerContextMenuInfo).position)
        menu.setHeaderTitle(taskTitle)
    }


    override fun onContextItemSelected(item: MenuItem?): Boolean {

        if (userVisibleHint) {
            val info = item!!.menuInfo as ContextMenuRecyclerView.RecyclerContextMenuInfo

            when (item.itemId) {
                R.id.pomodoro_long -> {
                    val activityIntent = Intent(context, PomodoroControlActivity::class.java)
                    activityIntent.setAction(PomodoroControlActivity.ACTION_START)
                    activityIntent.putExtra(PomodoroControlActivity.ARG_TASK_ID, mAdapter!!.getTaskId(info.position))
                    activityIntent.putExtra(PomodoroControlActivity.ARG_TASK_TITLE, mAdapter!!.getTaskTitle(info.position))
                    activityIntent.putExtra(PomodoroControlActivity.ARG_DURATION_MIN, 25)
                    context.startActivity(activityIntent)

                }
                R.id.pomodoro_short -> run {
                    val activityIntent = Intent(context, PomodoroControlActivity::class.java)
                    activityIntent.setAction(PomodoroControlActivity.ACTION_START)
                    activityIntent.putExtra(PomodoroControlActivity.ARG_TASK_ID, mAdapter!!.getTaskId(info.position))
                    activityIntent.putExtra(PomodoroControlActivity.ARG_TASK_TITLE, mAdapter!!.getTaskTitle(info.position))
                    activityIntent.putExtra(PomodoroControlActivity.ARG_DURATION_MIN, 5)
                    context.startActivity(activityIntent)
                }
                else -> {
                }
            }
            return true
        } else
            return false
    }

    fun onCreateTask(item: TaskListItem) {
        mAdapter!!.onItemCreate(0, item)
    }

    fun onEditTask(position: Int, task: Task) {
        mAdapter!!.onItemUpdate(position, task)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        mItemTouchHelper!!.startDrag(viewHolder)
    }


    internal interface TaskManipulationListener {
        fun onTaskCreated(task: Task)

        fun onTaskPrioritySwapped(id1: Long, id2: Long)

        fun onTaskWillingnessSwapped(id1: Long, id2: Long)

        fun onTaskRemoved(id: Long)

        fun onTaskUpdated(id: Long, task: Task)
    }

    companion object {

        private val TAG = "TaskRecyclerListFragme"
        private val ARG_MODE = "mode"

        fun newInstance(mode: Int): TaskRecyclerListFragment {
            val fragment = TaskRecyclerListFragment()
            val args = Bundle()
            args.putInt(ARG_MODE, mode)

            fragment.arguments = args
            return fragment
        }
    }
}
