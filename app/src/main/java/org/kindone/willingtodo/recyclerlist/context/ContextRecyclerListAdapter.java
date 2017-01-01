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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.kindone.willingtodo.R;
import org.kindone.willingtodo.data.ContextListItem;
import org.kindone.willingtodo.data.TaskContext;
import org.kindone.willingtodo.persistence.TaskContextProvider;
import org.kindone.willingtodo.recyclerlist.RecyclerListAdapter;
import org.kindone.willingtodo.recyclerlist.RecyclerListItem;
import org.kindone.willingtodo.recyclerlist.RecyclerListItemStartDragListener;
import org.kindone.willingtodo.recyclerlist.RecyclerListItemViewHolder;

import java.util.List;

public class ContextRecyclerListAdapter extends RecyclerListAdapter<ContextListItem> {

    private TaskContextProvider mItemProvider;
    private int mVersion;

    public ContextRecyclerListAdapter(TaskContextProvider contextProvider,
                                      RecyclerListItemStartDragListener dragStartListener) {
        super(dragStartListener);
        mItemProvider = contextProvider;
        init();
        mVersion = contextProvider.getVersion();
    }

    public void init() {
        reloadFromProvider();
    }

    private void reloadFromProvider() {
        mItems.clear();
        List<TaskContext> contexts;

        contexts = loadContexts();

        for (int i = 0; i < contexts.size(); i++) {
            mItems.add(new ContextListItem(contexts.get(i)));
        }
    }

    public void refresh(int version) {
        if (version != mVersion) {
            reloadFromProvider();
            notifyDataSetChanged();
            mVersion = mItemProvider.getVersion();
        }
    }

    @Override
    public RecyclerListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contextlist_item, parent, false);
        RecyclerListItemViewHolder listItemViewHolder = new ContextListItemViewHolder(view);
        return listItemViewHolder;
    }

    protected List<TaskContext> loadContexts() {
        return mItemProvider.getTaskContexts();
    }

    protected RecyclerListItem tellItemCreated(RecyclerListItem item)
    {
        ContextListItem contextListItem = (ContextListItem)item;
        return new ContextListItem(mItemProvider.create(contextListItem.getContext()));
    }

    @Override
    protected void tellItemChanged(RecyclerListItem item) {
        ContextListItem contextListItem = (ContextListItem)item;
        mItemProvider.update(contextListItem.getContext());
    }

    protected void tellItemRemoved(long itemId)
    {
        mItemProvider.delete(itemId);
    }

    protected void tellItemSwapped(long itemId1, long itemId2) {
        mItemProvider.swap(itemId1, itemId2);
    }


}
