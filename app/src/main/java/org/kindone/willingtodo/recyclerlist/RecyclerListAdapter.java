package org.kindone.willingtodo.recyclerlist;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.kindone.willingtodo.touchhelper.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kindone on 2016. 12. 22..
 */
public abstract class RecyclerListAdapter<Item extends RecyclerListItem> extends RecyclerView.Adapter<RecyclerListItemViewHolder> implements ItemTouchHelperAdapter {
    protected final List<Item> mItems = new ArrayList<>();
    protected final RecyclerListItemStartDragListener mDragStartListener;

    protected boolean orderByWillingness;

    public RecyclerListAdapter(RecyclerListItemStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
    }

    abstract public RecyclerListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(final RecyclerListItemViewHolder holder, int position) {
        Log.v("bindViewHolder", "count=" + mItems.size() + ", position=" + position);
        holder.setId(mItems.get(position).getId());
        holder.setTitle(mItems.get(position).getTitle());

        // Start a drag whenever the handle view it touched
        holder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    public void onItemCreate(int position, RecyclerListItem item) {
        RecyclerListItem item2 = tellItemCreated(item);
        mItems.add(position, (Item)item2);
        notifyItemInserted(position);
    }

    public void onItemUpdate(RecyclerListItem updatedItem) {
        for(int pos = 0; pos < mItems.size();++pos)
        {
            Item it = mItems.get(pos);
            if(it.getId() == updatedItem.getId())
            {
                mItems.set(pos, (Item) updatedItem);
                notifyItemChanged(pos);
                tellItemChanged((Item) updatedItem);
                return;
            }
        }
    }

    public void onItemUpdate(long itemId, RecyclerListItem.Updater updater) {
        for(int pos = 0; pos < mItems.size();++pos)
        {
            Item it = mItems.get(pos);
            if(it.getId() == itemId)
            {
                Item updatedItem = (Item)updater.update(it);
                mItems.set(pos, updatedItem);
                notifyItemChanged(pos);
                tellItemChanged(updatedItem);
                return;
            }
        }
    }

    @Override
    public void onItemDismiss(int position) {
        Item taskItem = mItems.get(position);
        mItems.remove(position);
        notifyItemRemoved(position);
        tellItemRemoved(taskItem.getId());
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        Item taskItem1 = mItems.get(fromPosition);
        Item taskItem2 = mItems.get(toPosition);

        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        tellItemSwapped(taskItem1.getId(), taskItem2.getId());

        return true;
    }

    public Item getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    abstract protected RecyclerListItem tellItemCreated(RecyclerListItem item);

    abstract protected void tellItemChanged(RecyclerListItem item);

    abstract protected void tellItemRemoved(long itemId);

    abstract protected void tellItemSwapped(long itemId1, long itemId2);

}
