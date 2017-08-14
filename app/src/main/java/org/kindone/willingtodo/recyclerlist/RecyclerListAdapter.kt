package org.kindone.willingtodo.recyclerlist

import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import org.kindone.willingtodo.touchhelper.ItemTouchHelperAdapter

import java.util.ArrayList
import java.util.Collections

/**
 * Created by kindone on 2016. 12. 22..
 */
abstract class RecyclerListAdapter<Item : RecyclerListItem>(protected val mDragStartListener: RecyclerListItemStartDragListener)
    : RecyclerView.Adapter<RecyclerListItemViewHolder>(), ItemTouchHelperAdapter
{

    protected val mItems: MutableList<Item> = ArrayList()

    protected var orderByWillingness: Boolean = false



    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerListItemViewHolder

    override fun onBindViewHolder(holder: RecyclerListItemViewHolder, position: Int) {
        Log.v("bindViewHolder", "count=$itemCount, position=$position")
        initializeViewHolder(holder, position)
    }

    private fun initializeViewHolder(holder: RecyclerListItemViewHolder, position: Int) {
        holder.setListItemId(getItem(position).getId())
        holder.setTitle(getItem(position).getTitle())

        setupDragStartEventOnHolderTouched(holder)
    }

    private fun setupDragStartEventOnHolderTouched(holder: RecyclerListItemViewHolder) {
        holder.setOnTouchListener(View.OnTouchListener { v, event ->
            if (isMotionEventDown(event)) {
                dispatchStartDragEvent(holder)
            }
            false
        })
    }

    private fun isMotionEventDown(event: MotionEvent): Boolean {
        return MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN
    }

    private fun dispatchStartDragEvent(holder: RecyclerListItemViewHolder) {
        mDragStartListener.onStartDrag(holder)
    }

    fun onCreateItem(position: Int, item: RecyclerListItem) {
        val item2 = tellItemCreated(item)
        addItem(position, item2 as Item)
        notifyItemInserted(position)
    }

    fun onUpdateItem(updatedItem: RecyclerListItem) {
        val itemInList = findItemById(updatedItem.getId())
        if (itemInList != null) {
            updateItem(itemInList.position, updatedItem as Item)
            notifyItemChanged(itemInList.position)
            tellItemChanged(updatedItem)
            return
        }
    }

    fun onUpdateItem(itemId: Long, updater: RecyclerListItem.Updater) {
        val itemInList = findItemById(itemId)
        if (itemInList != null) {
            val updatedItem = updater.update(itemInList.item) as Item
            updateItem(itemInList.position, updatedItem)
            notifyItemChanged(itemInList.position)
            tellItemChanged(updatedItem)
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
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val taskItem1 = getItem(fromPosition)
        val taskItem2 = getItem(toPosition)

        swapItems(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        tellItemSwapped(taskItem1.getId(), taskItem2.getId())

        return true
    }


    private fun addItem(position: Int, item: Item) {
        mItems.add(position, item)
    }

    private fun removeItem(position: Int) {
        mItems.removeAt(position)
    }

    private fun updateItem(position: Int, updatedItem: Item) {
        mItems[position] = updatedItem
    }

    private fun swapItems(pos1: Int, pos2: Int) {
        Collections.swap(mItems, pos1, pos2)
    }

    fun getItem(position: Int): Item {
        return mItems[position]
    }

    override fun getItemCount(): Int {
        return mItems.size
    }


    internal inner class FoundItemInList(val item: Item, val position: Int)


    protected abstract fun tellItemCreated(item: RecyclerListItem): RecyclerListItem

    protected abstract fun tellItemChanged(item: RecyclerListItem)

    protected abstract fun tellItemRemoved(itemId: Long)

    protected abstract fun tellItemSwapped(itemId1: Long, itemId2: Long)

}
