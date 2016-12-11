package org.kindone.willingtodo.helper

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.View

// From Gist : https://gist.github.com/Teovald/cba0aa150e60b727636d
class ContextMenuRecyclerView : RecyclerView {


    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
    }

    private var mContextMenuInfo: ContextMenuInfo? = null

    override fun getContextMenuInfo(): ContextMenuInfo {
        return mContextMenuInfo!!
    }


    override fun showContextMenuForChild(originalView: View): Boolean {
        val longPressPosition = getChildAdapterPosition(originalView)
        if (longPressPosition >= 0) {
            val longPressId = adapter.getItemId(longPressPosition)
            mContextMenuInfo = createContextMenuInfo(longPressPosition, longPressId)
            return super.showContextMenuForChild(originalView)
        }
        return false
    }

    internal fun createContextMenuInfo(position: Int, id: Long): ContextMenuInfo {
        return RecyclerContextMenuInfo(position, id)
    }

    /**
     * Extra menu information provided to the
     * [android.view.View.OnCreateContextMenuListener.onCreateContextMenu]
     * callback when a context menu is brought up for this AdapterView.
     */
    class RecyclerContextMenuInfo(var position: Int, var id: Long) : ContextMenu.ContextMenuInfo

}