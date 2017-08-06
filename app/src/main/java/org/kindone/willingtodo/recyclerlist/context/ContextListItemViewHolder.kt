package org.kindone.willingtodo.recyclerlist.context

import android.view.View
import android.widget.ImageView
import android.widget.TextView

import org.kindone.willingtodo.R
import org.kindone.willingtodo.recyclerlist.RecyclerListItemViewHolder

/**
 * Created by kindone on 2016. 12. 25..
 */

class ContextListItemViewHolder(itemView: View) : RecyclerListItemViewHolder(itemView) {

    private val mTitleView: TextView
    private val mHandleView: ImageView
    private var mItemId: Long = 0

    init {
        mTitleView = itemView.findViewById(R.id.item_title) as TextView
        mHandleView = itemView.findViewById(R.id.handle) as ImageView
    }

    override fun getHandleView(): View {
        return mHandleView
    }

    override fun setListItemId(id: Long) {
        mItemId = id
    }

    override fun getListItemId(): Long {
        return mItemId;
    }


    override fun setTitle(title: String) {
        mTitleView.text = title
    }
}
