package org.kindone.willingtodo.recyclerlist.context

import android.graphics.Color
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import org.kindone.willingtodo.R
import org.kindone.willingtodo.recyclerlist.RecyclerListItemStartDragListener
import org.kindone.willingtodo.recyclerlist.RecyclerListItemViewHolder
import org.kindone.willingtodo.touchhelper.ItemTouchHelperViewHolder

/**
 * Created by kindone on 2016. 12. 25..
 */

class TaskContextListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {

    private val mTitleView: TextView
    private val mHandleView: ImageView
    private var mItemId: Long = 0

    init {
        mTitleView = itemView.findViewById(R.id.item_title) as TextView
        mHandleView = itemView.findViewById(R.id.handle) as ImageView
    }

    fun getHandleView(): View {
        return mHandleView
    }

    fun setListItemId(id: Long) {
        mItemId = id
    }

    fun setTitle(title: String) {
        mTitleView.text = title
    }

    fun setOnLongClickListener(listener: View.OnLongClickListener) {
        itemView.setOnLongClickListener(listener)
    }


    fun setDragEventListener(mDragStartListener:RecyclerListItemStartDragListener) {
        setOnTouchListener(View.OnTouchListener { v, event ->
            if (isMotionEventDown(event)) {
                mDragStartListener.onStartDrag(this)
            }
            false
        })
    }

    private fun setOnTouchListener(l: View.OnTouchListener) {
        getHandleView().setOnTouchListener(l)
    }

    private fun isMotionEventDown(event: MotionEvent): Boolean {
        return MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN
    }

    override fun onItemSelected() {
        itemView.setBackgroundColor(Color.LTGRAY)
    }

    override fun onItemClear() {
        itemView.setBackgroundColor(0)
    }
}
