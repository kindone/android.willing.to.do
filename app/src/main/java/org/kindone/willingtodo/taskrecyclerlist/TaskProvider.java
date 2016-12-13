package org.kindone.willingtodo.taskrecyclerlist;

import org.kindone.willingtodo.data.Task;

import java.util.List;

/**
 * Created by kindone on 2016. 12. 13..
 */
public interface TaskProvider {
    int getVersion();

    List<Task> loadTasksOrderedByPriority();

    List<Task> loadTasksOrderedByWillingness();
}
