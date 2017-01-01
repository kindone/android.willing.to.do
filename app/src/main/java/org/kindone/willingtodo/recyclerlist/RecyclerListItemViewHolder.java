package org.kindone.willingtodo.recyclerlist;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.kindone.willingtodo.touchhelper.ItemTouchHelperViewHolder;

/**
 * Simple example of a view holder that implements helper.ItemTouchHelperViewHolder and has a
 * "handle" view that initiates a drag event when touched.
 */
abstract public class RecyclerListItemViewHolder extends RecyclerView.ViewHolder implements
        ItemTouchHelperViewHolder {


    public RecyclerListItemViewHolder(View itemView) {
        super(itemView);
    }

    abstract protected View getHandleView();

    abstract public void setId(long id);

    abstract public void setTitle(String title);

    public void setOnLongClickListener(View.OnLongClickListener listener) {
        itemView.setOnLongClickListener(listener);
    }

    public void setOnTouchListener(View.OnTouchListener l) {
        getHandleView().setOnTouchListener(l);
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
