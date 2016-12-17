package org.kindone.willingtodo.taskrecyclerlist;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.kindone.willingtodo.R;
import org.kindone.willingtodo.touchhelper.ItemTouchHelperViewHolder;

/**
 * Simple example of a view holder that implements helper.ItemTouchHelperViewHolder and has a
 * "handle" view that initiates a drag event when touched.
 */
public class TaskListItemViewHolder extends RecyclerView.ViewHolder implements
        ItemTouchHelperViewHolder {

    private final TextView mTitleView;
    private final ImageView mHandleView;

    public TaskListItemViewHolder(View itemView) {
        super(itemView);
        mTitleView = (TextView) itemView.findViewById(R.id.item_title);
        mHandleView = (ImageView) itemView.findViewById(R.id.handle);
    }

    public void setTitle(String title)
    {
        mTitleView.setText(title);
    }

    public void setOnTouchListener(View.OnTouchListener l) {
        mHandleView.setOnTouchListener(l);
    }

    @Override
    public void onItemSelected() {
        itemView.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onItemClear() {
        itemView.setBackgroundColor(0);
    }
}
