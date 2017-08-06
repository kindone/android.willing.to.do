package org.kindone.willingtodo.recyclerlist.task

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import org.kindone.willingtodo.MainActivity
import org.kindone.willingtodo.R
import org.kindone.willingtodo.TaskEditActivity
import org.kindone.willingtodo.recyclerlist.RecyclerListItemViewHolder

/**
 * Simple example of a view holder that implements helper.ItemTouchHelperViewHolder and has a
 * "handle" view that initiates a drag event when touched.
 */
class TaskListItemViewHolder(itemView: View) : RecyclerListItemViewHolder /*implements View.OnCreateContextMenuListener*/(itemView) {

    private val mTitleView: TextView
    private val mHandleView: ImageView
    private var mItemId: Long = 0

    init {
        mTitleView = itemView.findViewById(itemTitleViewResourceId) as TextView
        mHandleView = itemView.findViewById(handleViewResourceId) as ImageView
        initializeOnClickListener(itemView);
        //        itemView.setOnCreateContextMenuListener(this);
    }

    private fun initializeOnClickListener(itemView:View)
    {
        itemView.setOnClickListener {
            Log.v("TaskListItemVH", "clicked")
            startEditTaskActivity()
        }
    }

    private fun startEditTaskActivity()
    {
        val intent = Intent(itemView.context, TaskEditActivity::class.java)
        intent.putExtra(MainActivity.RESULT_TASK_ID, mItemId)
        intent.putExtra(MainActivity.RESULT_TASK_TITLE, mTitleView.text)
        (itemView.context as Activity).startActivityForResult(intent, MainActivity.INTENT_EDIT_TASK)
    }

    //    @Override
    //    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    //        menu.setHeaderTitle(getTitle());
    //        menu.add(Menu.NONE, R.id.action_pomodoro, 1, "Start Pomodoro Timer");
    //        menu.add(Menu.NONE, R.id.action_move_task, 2, "Move Task to...");
    //    }

    override fun getHandleView(): View {
        return mHandleView
    }

    override fun setListItemId(id: Long) {
        mItemId = id
    }

    override fun getListItemId():Long {
        return mItemId
    }

    override fun setTitle(title: String) {
        mTitleView.text = title
    }

    val title: String
        get() = mTitleView.text.toString()

    companion object {
        val itemTitleViewResourceId = R.id.item_title
        val handleViewResourceId = R.id.handle
    }
}
