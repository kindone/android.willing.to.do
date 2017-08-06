package org.kindone.willingtodo.recyclerlist

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View

import org.kindone.willingtodo.touchhelper.ItemTouchHelperViewHolder

/**
 * Simple example of a view holder that implements helper.ItemTouchHelperViewHolder and has a
 * "handle" view that initiates a drag event when touched.
 */
abstract class RecyclerListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {

    protected abstract fun getHandleView(): View

    abstract fun setListItemId(id: Long)

    abstract fun getListItemId():Long

    abstract fun setTitle(title: String)

    fun setOnLongClickListener(listener: View.OnLongClickListener) {
        itemView.setOnLongClickListener(listener)
    }

    fun setOnTouchListener(l: View.OnTouchListener) {
        getHandleView().setOnTouchListener(l)
    }

    override fun onItemSelected() {
        itemView.setBackgroundColor(Color.LTGRAY)
    }

    override fun onItemClear() {
        itemView.setBackgroundColor(0)
    }
}
