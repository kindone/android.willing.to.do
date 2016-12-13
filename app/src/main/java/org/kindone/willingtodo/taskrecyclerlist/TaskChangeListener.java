package org.kindone.willingtodo.taskrecyclerlist;

import org.kindone.willingtodo.data.Task;

/**
 * Created by kindone on 2016. 12. 13..
 */
public interface TaskChangeListener {
    void onTaskCreated(Task task);

    void onTaskPrioritySwapped(long id1, long id2);

    void onTaskWillingnessSwapped(long id1, long id2);

    void onTaskRemoved(long id);
}
