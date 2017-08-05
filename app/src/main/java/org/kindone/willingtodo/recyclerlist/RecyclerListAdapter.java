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
        Log.v("bindViewHolder", "count=" + getItemCount() + ", position=" + position);
        initializeViewHolder(holder, position);
    }

    private void initializeViewHolder(RecyclerListItemViewHolder holder, int position)
    {
        initializeHolder(holder, position);
        setupTouchListener(holder);
    }

    private void initializeHolder(RecyclerListItemViewHolder holder, int position)
    {
        holder.setId(getItem(position).getId());
        holder.setTitle(getItem(position).getTitle());
    }

    private void setupTouchListener(RecyclerListItemViewHolder holder)
    {
        setupDragStartEventOnHolderTouched(holder);
    }

    private void setupDragStartEventOnHolderTouched(final RecyclerListItemViewHolder holder)
    {
        holder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isMotionEventDown(event)) {
                    dispatchStartDragEvent(holder);
                }
                return false;
            }
        });
    }

    private boolean isMotionEventDown(MotionEvent event)
    {
        return MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN;
    }

    private void dispatchStartDragEvent(RecyclerListItemViewHolder holder)
    {
        mDragStartListener.onStartDrag(holder);
    }

    public void onCreateItem(int position, RecyclerListItem item) {
        RecyclerListItem item2 = tellItemCreated(item);
        addItem(position, (Item)item2);
        notifyItemInserted(position);
    }

    public void onUpdateItem(RecyclerListItem updatedItem) {
        FoundItemInList itemInList = findItemById(updatedItem.getId());
        if(itemInList != null)
        {
            updateItem(itemInList.position, (Item) updatedItem);
            notifyItemChanged(itemInList.position);
            tellItemChanged(updatedItem);
            return;
        }
    }

    public void onUpdateItem(long itemId, RecyclerListItem.Updater updater) {
        FoundItemInList itemInList = findItemById(itemId);
        if(itemInList != null)
        {
            Item updatedItem = (Item)updater.update(itemInList.item);
            updateItem(itemInList.position, updatedItem);
            notifyItemChanged(itemInList.position);
            tellItemChanged(updatedItem);
            return;
        }
    }

    private FoundItemInList findItemById(long itemId)
    {
        for(int pos = 0; pos < getItemCount();++pos) {
            Item item = getItem(pos);
            if (item.getId() == itemId) {
                return new FoundItemInList(item, pos);
            }
        }

        return null;
    }

    class FoundItemInList
    {
        public final Item item;
        public final int position;

        public FoundItemInList(Item item, int position)
        {
            this.item = item;
            this.position = position;
        }
    }

    @Override
    public void onItemDismiss(int position) {
        Item taskItem = getItem(position);
        removeItem(position);
        notifyItemRemoved(position);
        tellItemRemoved(taskItem.getId());
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        Item taskItem1 = getItem(fromPosition);
        Item taskItem2 = getItem(toPosition);

        swapItems(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        tellItemSwapped(taskItem1.getId(), taskItem2.getId());

        return true;
    }



    private void addItem(int position, Item item)
    {
        mItems.add(position, (Item)item);
    }

    private void removeItem(int position) {
        mItems.remove(position);
    }

    private void updateItem(int position, Item updatedItem)
    {
        mItems.set(position, updatedItem);
    }

    private void swapItems(int pos1, int pos2)
    {
        Collections.swap(mItems, pos1, pos2);
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
