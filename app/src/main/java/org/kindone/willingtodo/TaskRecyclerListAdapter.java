/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kindone.willingtodo;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.kindone.willingtodo.helper.ItemTouchHelperAdapter;
import org.kindone.willingtodo.helper.ItemTouchHelperViewHolder;
import org.kindone.willingtodo.helper.OnStartDragListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Simple RecyclerView.Adapter that implements ItemTouchHelperAdapter to respond to move and
 * dismiss events from a {@link android.support.v7.widget.helper.ItemTouchHelper}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class TaskRecyclerListAdapter extends RecyclerView.Adapter<TaskRecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private static final String TAG = "TaskRecyclerListAdapter";

    private final List<TaskListItem> mItems = new ArrayList<>();

    private final OnStartDragListener mDragStartListener;

    public TaskRecyclerListAdapter(Context context, OnStartDragListener dragStartListener, int mode) {
        mDragStartListener = dragStartListener;
        int dummy_array_id = 0;

        if (mode == R.string.title_inbox)
            dummy_array_id = R.array.inbox_dummy;
        else if (mode == R.string.title_priority)
            dummy_array_id = R.array.priority_dummy;
        else if (mode == R.string.title_willingness)
            dummy_array_id = R.array.willingness_dummy;

        List<String> titles = Arrays.asList(context.getResources().getStringArray(dummy_array_id));

        for (int i = 0; i < titles.size(); i++) {
            mItems.add(new TaskListItem(titles.get(i)));
        }

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasklist_item, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.titleView.setText(mItems.get(position).getTitle());

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Simple example of a view holder that implements helper.ItemTouchHelperViewHolder and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView titleView;
        public final ImageView handleView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.item_title);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
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
}
