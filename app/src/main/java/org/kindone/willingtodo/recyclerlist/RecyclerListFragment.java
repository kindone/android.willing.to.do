package org.kindone.willingtodo.recyclerlist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.melnykov.fab.FloatingActionButton;

import org.kindone.willingtodo.R;
import org.kindone.willingtodo.persistence.PersistenceProvider;
import org.kindone.willingtodo.persistence.TaskContextProvider;
import org.kindone.willingtodo.persistence.TaskProvider;
import org.kindone.willingtodo.touchhelper.SimpleItemTouchHelperCallback;

/**
 * Created by kindone on 2016. 12. 22..
 */
abstract public class RecyclerListFragment extends Fragment implements RecyclerListItemStartDragListener {
    protected LinearLayout mLayout;
    protected ItemTouchHelper mItemTouchHelper;
    protected FloatingActionButton mNewFloatingButton;
    protected TaskProvider mTaskProvider;
    protected TaskContextProvider mContextProvider;
    protected RecyclerListAdapter mListAdapter;


    public RecyclerListFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            PersistenceProvider provider = (PersistenceProvider) context;
            mTaskProvider = provider.getTaskProvider();
            mContextProvider = provider.getContextProvider();

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TaskProvider");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListAdapter = createListAdapter();

        mLayout = (LinearLayout) view;
        mLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        RecyclerView recyclerView = new MyRecyclerListView(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mListAdapter);
        recyclerView.setLayoutManager(llm);

        // floating action button
        mNewFloatingButton.attachToRecyclerView(recyclerView);
        mNewFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFloatingButtonClick();
            }
        });

        mItemTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(mListAdapter));
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        mLayout.addView(recyclerView);

        registerForContextMenu(recyclerView);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mNewFloatingButton = (FloatingActionButton) container.getRootView().findViewById(R.id.fab);

        return new LinearLayout(container.getContext());
    }

    public void onCreateItem(RecyclerListItem item) {
        mListAdapter.onItemCreate(mListAdapter.getItemCount(), item);
    }

    public void onUpdateItem(RecyclerListItem item) {
        mListAdapter.onItemUpdate(item);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    abstract protected RecyclerListAdapter createListAdapter();

    abstract protected void onFloatingButtonClick();

    class MyRecyclerListView extends RecyclerView {
        private ContextMenu.ContextMenuInfo mContextMenuInfo;


        public MyRecyclerListView(Context context) {
            super(context);
        }

        @Override
        protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
            return mContextMenuInfo;
        }

        @Override
        public boolean showContextMenuForChild(View originalView) {
            int longPressPosition = getChildAdapterPosition(originalView);
            if (longPressPosition >= 0) {
                mContextMenuInfo = createContextMenuInfo(longPressPosition);
                mListAdapter.getItem(longPressPosition);
                return super.showContextMenuForChild(originalView);
            }
            else
                return false;
        }
    }

    protected ContextMenu.ContextMenuInfo createContextMenuInfo(int position) {
        return new RecyclerViewContextMenuInfo(position);
    }

    protected class RecyclerViewContextMenuInfo implements ContextMenu.ContextMenuInfo {
        final public int itemPosition;

        public RecyclerViewContextMenuInfo(int pos) {
            itemPosition = pos;
        }
    }

}
