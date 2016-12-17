package org.kindone.willingtodo.taskrecyclerlist;

import org.kindone.willingtodo.data.Task;

import java.util.List;

/**
 * Created by kindone on 2016. 12. 13..
 */

public class WillingnessTaskRecyclerListAdapter extends TaskRecyclerListAdapterBase {
    public WillingnessTaskRecyclerListAdapter(long contextId, TaskProvider taskProvider,
                                              TaskChangeListener taskChangeListener,
                                              RecyclerListItemStartDragListener dragStartListener)
    {
        super(contextId, taskProvider, taskChangeListener, dragStartListener);
    }

    protected List<Task> loadTasks()
    {
        return loadTasksOrderedByWillingness();
    }

    protected void tellTaskSwapped(long itemId1, long itemId2) {
        tellTaskWillingnessSwapped(itemId1, itemId2);
    }
}
