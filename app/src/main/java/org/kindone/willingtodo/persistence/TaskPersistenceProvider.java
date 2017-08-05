package org.kindone.willingtodo.persistence;

import org.kindone.willingtodo.data.Task;

import java.util.List;

/**
 * Created by kindone on 2016. 12. 13..
 */
public interface TaskPersistenceProvider {
    int getVersion();

    List<Task> getTasksOfContextOrderedByPriority(long contextId);

    List<Task> getTasksOfContextOrderedByWillingness(long contextId);

    Task createTask(Task task);

    void updateTask(Task task);

    void swapPriorityOfTasks(long id1, long id2);

    void swapWillingnessOfTasks(long id1, long id2);

    void deleteTask(long id);
}
