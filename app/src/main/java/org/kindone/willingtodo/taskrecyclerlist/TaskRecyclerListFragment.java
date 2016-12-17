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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import org.kindone.willingtodo.MainActivity;
import org.kindone.willingtodo.R;
import org.kindone.willingtodo.TaskCreateActivity;
import org.kindone.willingtodo.data.TaskListItem;
import org.kindone.willingtodo.touchhelper.SimpleItemTouchHelperCallback;

/**
 * @author Paul Burke (ipaulpro)
 */
public class TaskRecyclerListFragment extends Fragment implements RecyclerListItemStartDragListener {

    private static final String ARG_CONTEXT_ID = "context_id";
    private static final String ARG_MODE = "mode";

    private ItemTouchHelper mItemTouchHelper;
    private FloatingActionButton mNewFloatingButton;
    private TaskChangeListener mTaskChangeListener;
    private TaskProvider mTaskProvider;
    private TaskRecyclerListAdapterBase mListAdapter;

    public static TaskRecyclerListFragment create(long contextId) {
        TaskRecyclerListFragment fragment = new TaskRecyclerListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CONTEXT_ID, contextId);
        fragment.setArguments(args);
        return fragment;
    }

    public TaskRecyclerListFragment() { super();}

    public void refresh(int version) {
        mListAdapter.refresh(version);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mTaskChangeListener = (TaskChangeListener) context;
            mTaskProvider = (TaskProvider) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mNewFloatingButton = (FloatingActionButton) container.getRootView().findViewById(R.id.fab);
        return new RecyclerView(container.getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        long contextId = args.getLong(ARG_CONTEXT_ID);

        mListAdapter = new TaskRecyclerListAdapterBase(contextId, mTaskProvider, mTaskChangeListener,
                    (RecyclerListItemStartDragListener)this);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mListAdapter);
        recyclerView.setLayoutManager(llm);

        // floating action button
        mNewFloatingButton.attachToRecyclerView(recyclerView);
        mNewFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateTaskActivity();
            }
        });

        mItemTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(mListAdapter));
        mItemTouchHelper.attachToRecyclerView(recyclerView);

    }

    public void onCreateTask(TaskListItem item) {
        mListAdapter.onItemCreate(0, item);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private void startCreateTaskActivity() {
        Intent intent = new Intent(getActivity(), TaskCreateActivity.class);
        getActivity().startActivityForResult(intent, MainActivity.INTENT_CREATE_TASK);
    }

}
