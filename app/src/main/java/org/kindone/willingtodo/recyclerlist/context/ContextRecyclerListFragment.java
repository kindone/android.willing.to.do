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

package org.kindone.willingtodo.recyclerlist.context;

import android.content.Intent;

import org.kindone.willingtodo.ContextCreateActivity;
import org.kindone.willingtodo.ManageContextActivity;
import org.kindone.willingtodo.recyclerlist.RecyclerListAdapter;
import org.kindone.willingtodo.recyclerlist.RecyclerListFragment;
import org.kindone.willingtodo.recyclerlist.task.TaskRecyclerListAdapter;


public class ContextRecyclerListFragment extends RecyclerListFragment {

    public static RecyclerListFragment create() {
        RecyclerListFragment fragment = new ContextRecyclerListFragment();
        return fragment;
    }

    public ContextRecyclerListFragment() { super();}

    public void refresh(int version) {
        ((TaskRecyclerListAdapter)mListAdapter).refresh(version);
    }

    @Override
    protected RecyclerListAdapter createListAdapter() {
        // recycler list
        RecyclerListAdapter adapter = new ContextRecyclerListAdapter(mContextProvider, this);
        return adapter;
    }

    @Override
    protected void onFloatingButtonClick() {
        startCreateContextActivity();
    }

    private void startCreateContextActivity() {
        Intent intent = new Intent(getActivity(), ContextCreateActivity.class);
        getActivity().startActivityForResult(intent, ManageContextActivity.INTENT_CREATE_CONTEXT);
    }
}
