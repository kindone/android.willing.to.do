package org.kindone.willingtodo.recyclerlist

import android.content.Context
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

import org.kindone.willingtodo.R
import org.kindone.willingtodo.event.EventListenerMap
import org.kindone.willingtodo.persistence.PersistenceProvider
import org.kindone.willingtodo.persistence.TaskContextPersistenceProvider
import org.kindone.willingtodo.persistence.TaskPersistenceProvider
import org.kindone.willingtodo.touchhelper.SimpleItemTouchHelperCallback


/**
 * Created by kindone on 2016. 12. 22..
 */
abstract class RecyclerListFragment<Item> : Fragment(), RecyclerListItemStartDragListener, ListEventDispatcher<Item> {

    override var eventListeners: EventListenerMap = mutableMapOf()


    protected var mItemTouchHelper: ItemTouchHelper? = null

    protected var mNewFloatingButton: FloatingActionButton? = null

    protected var mTaskPersistenceProvider: TaskPersistenceProvider? = null

    protected var mContextProvider: TaskContextPersistenceProvider? = null

    protected var mListAdapter: RecyclerListAdapter<*>? = null

    protected var mLayout:LinearLayout? = null


    override fun onAttach(context: Context?) {
        super.onAttach(context)

        initializePersistenceProvider(context!!)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeListAdapter()
        val layout = initializeLayout(view!!)
        initializeRecyclerView(layout)
        mLayout = layout
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initializeNewFloatingButton(container!!)
        return LinearLayout(container.context)
    }


    fun createItem(item: RecyclerListItem) {
        mListAdapter!!.onCreateItem(mListAdapter!!.itemCount, item)
    }

    fun updateItem(item: RecyclerListItem) {
        mListAdapter!!.onUpdateItem(item)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        mItemTouchHelper!!.startDrag(viewHolder)
    }



    private fun initializePersistenceProvider(context:Context)
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

    private fun initializeLayout(view:View):LinearLayout
    {
        val layout = view as LinearLayout
        layout.orientation = LinearLayout.VERTICAL

        return layout
    }

    private fun initializeRecyclerView(layout:LinearLayout)
    {
        val recyclerView = createMyRecyclerView()

        attachFloatingButtonToRecyclerView(recyclerView)

        initializeTouchHelper(recyclerView)

        layout.addView(recyclerView)

        registerForContextMenu(recyclerView)
    }

    private fun initializeTouchHelper(recyclerView:RecyclerView)
    {
        mItemTouchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(mListAdapter!!))
        mItemTouchHelper!!.attachToRecyclerView(recyclerView)
    }


    private fun initializeNewFloatingButton(container: ViewGroup)
    {
        mNewFloatingButton = container.rootView.findViewById(floatingActionButtonResourceId) as FloatingActionButton
        mNewFloatingButton!!.setOnClickListener { onFloatingButtonClick() }
    }



    private fun createMyRecyclerView():MyRecyclerListView
    {
        val recyclerView = MyRecyclerListView(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = mListAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return recyclerView
    }

    protected open fun createContextMenuInfo(position: Int): ContextMenu.ContextMenuInfo {
        return RecyclerViewContextMenuInfo(position)
    }



    private fun attachFloatingButtonToRecyclerView(recyclerView: RecyclerView)
    {
        mNewFloatingButton!!.attachToRecyclerView(recyclerView)
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



    protected abstract fun createListAdapter(): RecyclerListAdapter<*>

    protected abstract fun onFloatingButtonClick()



    open inner class RecyclerViewContextMenuInfo(val itemPosition: Int) : ContextMenu.ContextMenuInfo


    companion object {
        val floatingActionButtonResourceId = R.id.fab
    }
}
