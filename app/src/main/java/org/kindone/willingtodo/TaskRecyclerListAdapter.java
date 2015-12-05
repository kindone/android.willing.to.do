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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.kindone.willingtodo.helper.ItemTouchHelperAdapter;
import org.kindone.willingtodo.helper.ItemTouchHelperViewHolder;
import org.kindone.willingtodo.helper.OnStartDragListener;
import org.kindone.willingtodo.helper.TaskDbHelperProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple RecyclerView.Adapter that implements ItemTouchHelperAdapter to respond to move and
 * dismiss events from a {@link android.support.v7.widget.helper.ItemTouchHelper}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class TaskRecyclerListAdapter extends RecyclerView.Adapter<TaskRecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final List<TaskListItem> mItems = new ArrayList<>();
    private final OnStartDragListener mDragStartListener;
    private final TaskRecyclerListFragment.TaskManipulationListener mTaskManipulationListener;
    private final int mMode;

    public TaskRecyclerListAdapter(Context context, OnStartDragListener dragStartListener, int mode) {
        mDragStartListener = dragStartListener;
        mMode = mode;

        TaskLoader taskLoader = (TaskLoader) context;
        List<Task> tasks = new LinkedList<>();

        if (mMode == R.string.title_priority)
            tasks = taskLoader.loadTasksOrderedByPriority();
        else if (mMode == R.string.title_willingness)
            tasks = taskLoader.loadTasksOrderedByWillingness();
        else
            tasks = taskLoader.loadTasksOrderedByPriority();

        mTaskManipulationListener = (TaskRecyclerListFragment.TaskManipulationListener) context;

        TaskDbHelperProvider taskDbHelperProvider = (TaskDbHelperProvider) context;
        TaskDbHelper taskDbHelper = taskDbHelperProvider.getTaskDbHelper();

        taskDbHelper.getPriorityOrderedTasks();

        for (int i = 0; i < tasks.size(); i++) {
            mItems.add(new TaskListItem(tasks.get(i)));
        }

        Log.w("TSKRECYADAPTER", "loaded tasks from db");
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

    public void onItemCreate(int position, TaskListItem item) {
        mItems.add(position, item);
        notifyItemInserted(position);
        mTaskManipulationListener.onTaskCreated(item.getTask());
    }

    @Override
    public void onItemDismiss(int position) {
        TaskListItem taskItem = mItems.get(position);
        mItems.remove(position);
        notifyItemRemoved(position);
        mTaskManipulationListener.onTaskRemoved(taskItem.getId());
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        TaskListItem taskItem1 = mItems.get(fromPosition);
        TaskListItem taskItem2 = mItems.get(toPosition);

        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        if (mMode == R.string.title_priority)
            mTaskManipulationListener.onTaskPrioritySwapped(taskItem1.getId(), taskItem2.getId());
        else if (mMode == R.string.title_willingness)
            mTaskManipulationListener.onTaskWillingnessSwapped(taskItem1.getId(), taskItem2.getId());

        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    interface TaskLoader {
        List<Task> loadTasksOrderedByPriority();

        List<Task> loadTasksOrderedByWillingness();
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
