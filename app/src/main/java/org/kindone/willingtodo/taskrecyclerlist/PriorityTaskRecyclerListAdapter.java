package org.kindone.willingtodo.taskrecyclerlist;

import org.kindone.willingtodo.data.Task;

import java.util.List;

/**
 * Created by kindone on 2016. 12. 13..
 */

public class PriorityTaskRecyclerListAdapter extends TaskRecyclerListAdapterBase {
    public PriorityTaskRecyclerListAdapter(long contextId, TaskProvider taskProvider,
                                           TaskChangeListener taskChangeListener,
                                           RecyclerListItemStartDragListener dragStartListener)
    {
        super(contextId, taskProvider, taskChangeListener, dragStartListener);
    }

    protected List<Task> loadTasks()
    {
        return loadTasksOrderedByPriority();
    }

    protected void tellTaskSwapped(long itemId1, long itemId2) {
        tellTaskPrioritySwapped(itemId1, itemId2);
    }
}
