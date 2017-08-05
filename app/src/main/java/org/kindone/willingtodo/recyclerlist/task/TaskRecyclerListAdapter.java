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

package org.kindone.willingtodo.recyclerlist.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.kindone.willingtodo.R;
import org.kindone.willingtodo.data.Task;
import org.kindone.willingtodo.data.TaskListItem;
import org.kindone.willingtodo.persistence.TaskContextPersistenceProvider;
import org.kindone.willingtodo.persistence.TaskPersistenceProvider;
import org.kindone.willingtodo.recyclerlist.RecyclerListAdapter;
import org.kindone.willingtodo.recyclerlist.RecyclerListItem;
import org.kindone.willingtodo.recyclerlist.RecyclerListItemStartDragListener;
import org.kindone.willingtodo.recyclerlist.RecyclerListItemViewHolder;

import java.util.List;

public class TaskRecyclerListAdapter extends RecyclerListAdapter<TaskListItem> {

    private TaskPersistenceProvider mItemProvider;
    private TaskContextPersistenceProvider mContextProvider;
    private int mVersion;
    private final long contextId;

    public TaskRecyclerListAdapter(long contextId, TaskPersistenceProvider taskPersistenceProvider,
                                   TaskContextPersistenceProvider contextProvider,
                                   RecyclerListItemStartDragListener dragStartListener) {
        super(dragStartListener);
        this.contextId = contextId;
        mItemProvider = taskPersistenceProvider;
        mContextProvider = contextProvider;
        init();
        mVersion = mItemProvider.getVersion();
    }

    public void init() {
        reloadFromProvider();
    }

    private void reloadFromProvider() {
        mItems.clear();
        List<Task> tasks;

        orderByWillingness = mContextProvider.getModeOfTaskContext(contextId) == 1;
        tasks = loadTasks();

        for (int i = 0; i < tasks.size(); i++) {
            mItems.add(new TaskListItem(tasks.get(i)));
        }
    }

    public void refresh(int version) {
        if (version != mVersion) {
            reloadFromProvider();
            notifyDataSetChanged();
            mVersion = mItemProvider.getVersion();
        }
    }

    public void reorderByPriority() {
        mContextProvider.setModeOfTaskContext(contextId, 0);
    }

    public void reorderByWillingness() {
        mContextProvider.setModeOfTaskContext(contextId, 1);
    }

    @Override
    public RecyclerListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasklist_item, parent, false);
        RecyclerListItemViewHolder listItemViewHolder = new TaskListItemViewHolder(view);
        listItemViewHolder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //TODO
                return false;
            }
        });
        return listItemViewHolder;
    }


    protected List<Task> loadTasks() {
        if(orderByWillingness)
            return loadTasksOrderedByWillingness();
        else
            return loadTasksOrderedByPriority();
    }

    protected List<Task> loadTasksOrderedByPriority() {
        return mItemProvider.getTasksOfContextOrderedByPriority(contextId);
    }

    protected List<Task> loadTasksOrderedByWillingness() {
        return mItemProvider.getTasksOfContextOrderedByWillingness(contextId);
    }

    protected RecyclerListItem tellItemCreated(RecyclerListItem item)
    {
        TaskListItem taskListItem = (TaskListItem)item;
        mItemProvider.createTask(taskListItem.getTask());
        return item; // TODO
    }

    @Override
    protected void tellItemChanged(RecyclerListItem item) {
        TaskListItem taskListItem = (TaskListItem)item;
        mItemProvider.updateTask(taskListItem.getTask());
    }

    protected void tellItemRemoved(long taskId)
    {
        mItemProvider.deleteTask(taskId);
    }

    protected void tellItemSwapped(long itemId1, long itemId2) {
        if(orderByWillingness)
            tellTaskWillingnessSwapped(itemId1, itemId2);
        else
            tellTaskPrioritySwapped(itemId1, itemId2);
    }

    protected void tellTaskPrioritySwapped(long itemId1, long itemId2)
    {
        mItemProvider.swapPriorityOfTasks(itemId1, itemId2);
    }

    protected void tellTaskWillingnessSwapped(long itemId1, long itemId2)
    {
        mItemProvider.swapWillingnessOfTasks(itemId1, itemId2);
    }

}
