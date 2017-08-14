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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup

import org.kindone.willingtodo.MainActivity
import org.kindone.willingtodo.R
import org.kindone.willingtodo.TaskCreateActivity
import org.kindone.willingtodo.data.Task
import org.kindone.willingtodo.data.TaskListItem
import org.kindone.willingtodo.recyclerlist.RecyclerListAdapter
import org.kindone.willingtodo.recyclerlist.RecyclerListFragment
import org.kindone.willingtodo.recyclerlist.RecyclerListItem

class TaskRecyclerListFragment : RecyclerListFragment() {

    protected var mIndicator: View? = null

    protected var mIsPriorityOrdered: Boolean = false



    override fun onAttach(context: Context?) {
        Log.v(TAG, "onAttach")
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.v(TAG, "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        Log.v(TAG, "onCreateContextMenu")

        // add Task info
        val rMenuInfo = menuInfo as TaskRecyclerViewContextMenuInfo
        val taskItem = mListAdapter!!.getItem(rMenuInfo.itemPosition) as TaskListItem
        rMenuInfo.task = taskItem.task

        menu.setHeaderTitle(taskItem.getTitle())
        menu.add(Menu.NONE, R.id.action_pomodoro, 1, "Start Pomodoro Timer")
        menu.add(Menu.NONE, R.id.action_move_task, 2, "Move Task to...")
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v(TAG, "onViewCreated")

        mIndicator = View(context)
        val height = resources.getDimension(R.dimen.indicator_bar_height)
        mIndicator!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height.toInt())
        if (mIsPriorityOrdered)
            setIndicatorRed()
        else
            setIndicatorGreen()

        mLayout!!.addView(mIndicator, 0)
    }



    fun refresh(version: Int) {
        if (mListAdapter != null)
            (mListAdapter as TaskRecyclerListAdapter).refresh(version)
    }

    fun setPriorityOrdered(version: Int) {
        mIsPriorityOrdered = true
        if (isAdded) {
            setIndicatorRed()
            mIndicator!!.invalidate()
        }
        refresh(version)
    }

    fun setWillingnessOrdered(version: Int) {
        mIsPriorityOrdered = false
        if (isAdded) {
            setIndicatorGreen()
            mIndicator!!.invalidate()
        }
        refresh(version)
    }

    private fun setIndicatorRed() {
        mIndicator!!.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPriority))
    }

    private fun setIndicatorGreen() {
        mIndicator!!.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWilling))
    }


    override fun createListAdapter(): RecyclerListAdapter<*> {
        // recycler list
        val args = arguments
        val contextId = args.getLong(ARG_CONTEXT_ID)

        val adapter = TaskRecyclerListAdapter(contextId, mTaskPersistenceProvider!!, mContextProvider!!, this)
        return adapter
    }

    fun updateItem(itemId: Long, updater: RecyclerListItem.Updater) {
        mListAdapter!!.onUpdateItem(itemId, updater)
    }

    override fun onFloatingButtonClick() {
        startCreateTaskActivity()
    }

    private fun startCreateTaskActivity() {
        val intent = Intent(activity, TaskCreateActivity::class.java)
        activity.startActivityForResult(intent, MainActivity.INTENT_CREATE_TASK)
    }

    override fun createContextMenuInfo(position: Int): ContextMenu.ContextMenuInfo {
        return TaskRecyclerViewContextMenuInfo(position)
    }

    inner class TaskRecyclerViewContextMenuInfo(pos: Int) : RecyclerListFragment.RecyclerViewContextMenuInfo(pos) {
        var task: Task? = null
    }

    companion object {

        private val TAG = "TaskRecyclerFr"

        private val ARG_CONTEXT_ID = "context_id"

        fun create(contextId: Long): RecyclerListFragment {
            val fragment = TaskRecyclerListFragment()
            val args = Bundle()
            args.putLong(ARG_CONTEXT_ID, contextId)
            fragment.arguments = args
            return fragment
        }
    }
}
