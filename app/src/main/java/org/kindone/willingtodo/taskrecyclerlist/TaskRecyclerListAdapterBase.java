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

package org.kindone.willingtodo.taskrecyclerlist;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.kindone.willingtodo.R;
import org.kindone.willingtodo.data.Task;
import org.kindone.willingtodo.data.TaskListItem;
import org.kindone.willingtodo.helper.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple RecyclerView.Adapter that implements ItemTouchHelperAdapter to respond to move and
 * dismiss events from a {@link android.support.v7.widget.helper.ItemTouchHelper}.
 *
 * @author Paul Burke (ipaulpro)
 */
public abstract class TaskRecyclerListAdapterBase extends RecyclerView.Adapter<TaskListItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final List<TaskListItem> mItems = new ArrayList<>();
    private final RecyclerListItemStartDragListener mDragStartListener;
    private final TaskChangeListener mTaskChangeListener;
    private TaskProvider mTaskProvider;
    private int mVersion;

    public TaskRecyclerListAdapterBase(TaskProvider taskProvider,
                                       TaskChangeListener taskChangeListener,
                                       RecyclerListItemStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
        mTaskProvider = (TaskProvider) taskProvider;
        mTaskChangeListener = (TaskChangeListener) taskChangeListener;
        init();
    }

    public void init() {
        reloadFromProvider();
    }

    private void reloadFromProvider() {
        mItems.clear();
        List<Task> tasks;

        tasks = loadTasks();

        for (int i = 0; i < tasks.size(); i++) {
            mItems.add(new TaskListItem(tasks.get(i)));
        }

        mVersion = mTaskProvider.getVersion();
    }

    public void refresh(int version) {
        if (version != mVersion) {
            reloadFromProvider();
            notifyDataSetChanged();
        }
    }

    @Override
    public TaskListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasklist_item, parent, false);
        TaskListItemViewHolder listItemViewHolder = new TaskListItemViewHolder(view);
        return listItemViewHolder;
    }

    @Override
    public void onBindViewHolder(final TaskListItemViewHolder holder, int position) {
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

    public void onItemCreate(int position, TaskListItem item) {
        mItems.add(position, item);
        notifyItemInserted(position);
        tellTaskCreated(item.getTask());
    }

    @Override
    public void onItemDismiss(int position) {
        TaskListItem taskItem = mItems.get(position);
        mItems.remove(position);
        notifyItemRemoved(position);
        tellTaskRemoved(taskItem.getId());
    }


    public boolean onItemMove(int fromPosition, int toPosition) {
        TaskListItem taskItem1 = mItems.get(fromPosition);
        TaskListItem taskItem2 = mItems.get(toPosition);

        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        tellTaskSwapped(taskItem1.getId(), taskItem2.getId());

        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    protected abstract List<Task> loadTasks();

    protected List<Task> loadTasksOrderedByPriority() {
        return mTaskProvider.loadTasksOrderedByPriority();
    }

    protected List<Task> loadTasksOrderedByWillingness() {
        return mTaskProvider.loadTasksOrderedByWillingness();
    }

    private void tellTaskCreated(Task task)
    {
        mTaskChangeListener.onTaskCreated(task);
    }

    private void tellTaskRemoved(long taskId)
    {
        mTaskChangeListener.onTaskRemoved(taskId);
    }

    protected abstract void tellTaskSwapped(long itemId1, long itemId2);

    protected void tellTaskPrioritySwapped(long itemId1, long itemId2)
    {
        mTaskChangeListener.onTaskPrioritySwapped(itemId1, itemId2);
    }

    protected void tellTaskWillingnessSwapped(long itemId1, long itemId2)
    {
        mTaskChangeListener.onTaskWillingnessSwapped(itemId1, itemId2);
    }

}
