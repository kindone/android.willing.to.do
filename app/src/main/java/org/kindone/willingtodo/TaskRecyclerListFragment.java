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

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import org.kindone.willingtodo.helper.OnStartDragListener;
import org.kindone.willingtodo.helper.SimpleItemTouchHelperCallback;

/**
 * @author Paul Burke (ipaulpro)
 */
public class TaskRecyclerListFragment extends Fragment implements OnStartDragListener {

    private static final String ARG_MODE = "mode";

    private String mMode;
    private ItemTouchHelper mItemTouchHelper;
    private FloatingActionButton mNewButton;
    private TaskManipulationListener mCallback;
    private TaskRecyclerListAdapter mAdapter;

    public TaskRecyclerListFragment() {
    }

    public static TaskRecyclerListFragment newInstance(int mode) {
        TaskRecyclerListFragment fragment = new TaskRecyclerListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODE, mode);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (TaskManipulationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mNewButton = (FloatingActionButton) container.findViewById(R.id.fab);
        return new RecyclerView(container.getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        int mode = args.getInt(ARG_MODE);

        mAdapter = new TaskRecyclerListAdapter(getActivity(), this, mode);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(llm);

        // floating action button
        mNewButton.attachToRecyclerView(recyclerView);
        mNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TaskCreateActivity.class);
                getActivity().startActivityForResult(intent, MainActivity.INTENT_CREATE_TASK);
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);

        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

    }

    public void onCreateTask(TaskListItem item) {
        mAdapter.onItemCreate(0, item);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    interface TaskManipulationListener {
        void onTaskCreated(Task task);

        void onTaskPrioritySwapped(long id1, long id2);

        void onTaskWillingnessSwapped(long id1, long id2);

        void onTaskRemoved(long id);
    }
}
