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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.kindone.willingtodo.MainActivity;
import org.kindone.willingtodo.R;
import org.kindone.willingtodo.TaskCreateActivity;
import org.kindone.willingtodo.data.Task;
import org.kindone.willingtodo.data.TaskListItem;
import org.kindone.willingtodo.pomodorotimer.PomodoroTimerService;
import org.kindone.willingtodo.recyclerlist.RecyclerListAdapter;
import org.kindone.willingtodo.recyclerlist.RecyclerListFragment;
import org.kindone.willingtodo.recyclerlist.RecyclerListItem;

public class TaskRecyclerListFragment extends RecyclerListFragment {

    private static String TAG = "TaskRecyclerFr";

    private static final String ARG_CONTEXT_ID = "context_id";

    protected View mIndicator;
    protected boolean mIsPriorityOrdered;

    public static RecyclerListFragment create(long contextId) {
        RecyclerListFragment fragment = new TaskRecyclerListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CONTEXT_ID, contextId);
        fragment.setArguments(args);
        return fragment;
    }

    public TaskRecyclerListFragment() { super();}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // add Task info
        TaskRecyclerViewContextMenuInfo rMenuInfo = (TaskRecyclerViewContextMenuInfo) menuInfo;
        TaskListItem taskItem = (TaskListItem) mListAdapter.getItem(rMenuInfo.itemPosition);
        rMenuInfo.setTask(taskItem.getTask());

        menu.setHeaderTitle(taskItem.getTitle());
        menu.add(Menu.NONE, R.id.action_pomodoro, 1, "Start Pomodoro Timer");
        menu.add(Menu.NONE, R.id.action_move_task, 2, "Move Task to...");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            Log.v(TAG, "visible hint");
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.action_pomodoro:
                TaskRecyclerViewContextMenuInfo rMenuInfo = (TaskRecyclerViewContextMenuInfo) item.getMenuInfo();
                Log.v(TAG, "pomodoro context item selected itemId=" + rMenuInfo.getTask().id);
                startPomodoroTimerService(rMenuInfo.getTask());
                break;
            case R.id.action_move_task:
                Log.v(TAG, "move task context item selected");
                break;
        }
        return true;
    }

    public void startPomodoroTimerService(Task task) {
        Intent intent = new Intent(getActivity(), PomodoroTimerService.class);
        intent.setAction(PomodoroTimerService.ACTION_START);
        intent.putExtra(PomodoroTimerService.ARG_TASK_ID, task.id);
        intent.putExtra(PomodoroTimerService.ARG_TASK_TITLE, task.title);
        intent.putExtra(PomodoroTimerService.ARG_TASK_DURATION_MS, 25*60*1000L);
        getActivity().startService(intent);
    }


    public void refresh(int version) {
        if(mListAdapter != null)
            ((TaskRecyclerListAdapter)mListAdapter).refresh(version);
    }

    public void setPriorityOrdered() {
        mIsPriorityOrdered = true;
        if(isAdded()) {
            setIndicatorRed();
            mIndicator.invalidate();
        }
    }

    public void setWillingnessOrdered() {
        mIsPriorityOrdered = false;
        if(isAdded()) {
            setIndicatorGreen();
            mIndicator.invalidate();
        }
    }

    private void setIndicatorRed() {
        mIndicator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPriority));
    }

    private void setIndicatorGreen() {
        mIndicator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWilling));
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mIndicator = new View(getContext());
        float height = getResources().getDimension(R.dimen.indicator_bar_height);
        mIndicator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height));
        if(mIsPriorityOrdered)
            setIndicatorRed();
        else
            setIndicatorGreen();
        mLayout.addView(mIndicator, 0);

    }

    @Override
    protected RecyclerListAdapter createListAdapter() {
        // recycler list
        Bundle args = getArguments();
        long contextId = args.getLong(ARG_CONTEXT_ID);

        RecyclerListAdapter adapter = new TaskRecyclerListAdapter(contextId, mTaskProvider, mContextProvider, this);
        return adapter;
    }

    public void onUpdateItem(long itemId, RecyclerListItem.Updater updater)
    {
        mListAdapter.onItemUpdate(itemId, updater);
    }

    @Override
    protected void onFloatingButtonClick() {
        startCreateTaskActivity();
    }

    private void startCreateTaskActivity() {
        Intent intent = new Intent(getActivity(), TaskCreateActivity.class);
        getActivity().startActivityForResult(intent, MainActivity.INTENT_CREATE_TASK);
    }

    @Override
    protected ContextMenu.ContextMenuInfo createContextMenuInfo(int position) {
        return new TaskRecyclerViewContextMenuInfo(position);
    }

    class TaskRecyclerViewContextMenuInfo extends RecyclerViewContextMenuInfo {
        private Task task;

        public TaskRecyclerViewContextMenuInfo(int pos) {
            super(pos);
        }

        public void setTask(Task t) {
            task = t;
        }

        public Task getTask() {
            return task;
        }
    }
}
