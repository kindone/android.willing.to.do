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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.melnykov.fab.FloatingActionButton

import org.kindone.willingtodo.TaskContextCreateActivity
import org.kindone.willingtodo.ManageTaskContextActivity
import org.kindone.willingtodo.R
import org.kindone.willingtodo.data.TaskContext
import org.kindone.willingtodo.event.EventListenerMap
import org.kindone.willingtodo.persistence.PersistenceProvider
import org.kindone.willingtodo.persistence.TaskContextPersistenceProvider
import org.kindone.willingtodo.persistence.TaskPersistenceProvider
import org.kindone.willingtodo.recyclerlist.*
import org.kindone.willingtodo.recyclerlist.task.TaskRecyclerListAdapter
import org.kindone.willingtodo.touchhelper.SimpleItemTouchHelperCallback


class TaskContextRecyclerListFragment : Fragment(), RecyclerListItemStartDragListener, ListEventDispatcher<TaskContext> {


    override var eventListeners: EventListenerMap = mutableMapOf()

    private var mListItemTouchHelper: ItemTouchHelper? = null

    private var mNewFloatingButton: FloatingActionButton? = null

    private var mTaskPersistenceProvider: TaskPersistenceProvider? = null

    private var mContextProvider: TaskContextPersistenceProvider? = null

    private var mListAdapter: TaskContextRecyclerListAdapter? = null


    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // TODO: remove persistence and replace with event
        initializePersistenceProvider(context!!)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeListAdapter()
        val layout = initializeLayout(view!!)
        initializeRecyclerView(layout)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initializeNewFloatingButton(container!!)
        return LinearLayout(container.context)
    }


    // TODO:
    fun refreshList(version: Int) {
        (mListAdapter as TaskRecyclerListAdapter).refresh(version)
    }

    fun createListAdapter(): TaskContextRecyclerListAdapter {
        // recycler list
        val adapter = TaskContextRecyclerListAdapter(mContextProvider!!, this)
        return adapter
    }

    fun onFloatingButtonClick() {
        startCreateContextActivity()
    }

    private fun startCreateContextActivity() {
        val intent = Intent(activity, TaskContextCreateActivity::class.java)
        activity.startActivityForResult(intent, ManageTaskContextActivity.INTENT_CREATE_TASK_CONTEXT)
    }


    fun createItem(item: RecyclerListItem) {
        mListAdapter!!.onCreateItem(mListAdapter!!.itemCount, item)
    }

    fun updateItem(item: RecyclerListItem) {
        mListAdapter!!.onUpdateItem(item)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        mListItemTouchHelper!!.startDrag(viewHolder)
    }


    private fun initializePersistenceProvider(context: Context)
    {
        try {
            val provider = context as PersistenceProvider?
            mTaskPersistenceProvider = provider!!.taskPersistenceProvider
            mContextProvider = provider.taskContextPersistenceProvider

        } catch (e: ClassCastException) {
            throw ClassCastException(context!!.toString() + " must implement TaskPersistenceProvider")
        }
    }


    private fun initializeListAdapter()
    {
        mListAdapter = createListAdapter()
    }

    private fun initializeLayout(view: View): LinearLayout
    {
        val layout = view as LinearLayout
        layout.orientation = LinearLayout.VERTICAL

        return layout
    }

    private fun initializeRecyclerView(layout: LinearLayout)
    {
        val recyclerView = createMyRecyclerView()

        attachFloatingButtonToRecyclerView(recyclerView)

        initializeTouchHelper(recyclerView)

        layout.addView(recyclerView)

        registerForContextMenu(recyclerView)
    }

    private fun initializeTouchHelper(recyclerView: RecyclerView)
    {
        mListItemTouchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(mListAdapter!!))
        mListItemTouchHelper!!.attachToRecyclerView(recyclerView)
    }


    private fun initializeNewFloatingButton(container: ViewGroup)
    {
        mNewFloatingButton = container.rootView.findViewById(floatingActionButtonResourceId) as FloatingActionButton
        mNewFloatingButton!!.setOnClickListener { onFloatingButtonClick() }
    }




    protected open fun createContextMenuInfo(position: Int): ContextMenu.ContextMenuInfo {
        return RecyclerViewContextMenuInfo(position)
    }



    private fun attachFloatingButtonToRecyclerView(recyclerView: RecyclerView)
    {
        mNewFloatingButton!!.attachToRecyclerView(recyclerView)
    }


    private fun createMyRecyclerView():MyRecyclerListView
    {
        val recyclerView = MyRecyclerListView(context)
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = mListAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        return recyclerView
    }

    internal inner class MyRecyclerListView(context: Context) : RecyclerView(context) {
        private var mContextMenuInfo: ContextMenu.ContextMenuInfo? = null

        override fun getContextMenuInfo(): ContextMenu.ContextMenuInfo {
            return mContextMenuInfo!!
        }

        override fun showContextMenuForChild(originalView: View): Boolean {
            val longPressPosition = getChildAdapterPosition(originalView)
            if (longPressPosition >= 0) {
                mContextMenuInfo = createContextMenuInfo(longPressPosition)
                mListAdapter!!.getItem(longPressPosition)
                return super.showContextMenuForChild(originalView)
            } else
                return false
        }
    }

    open inner class RecyclerViewContextMenuInfo(val itemPosition: Int) : ContextMenu.ContextMenuInfo


    companion object {
        val floatingActionButtonResourceId = R.id.fab

        fun create(): TaskContextRecyclerListFragment {
            val fragment = TaskContextRecyclerListFragment()
            return fragment
        }
    }

}
