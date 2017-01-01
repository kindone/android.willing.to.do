package org.kindone.willingtodo.persistence;

import org.kindone.willingtodo.data.Task;

import java.util.List;

/**
 * Created by kindone on 2016. 12. 13..
 */
public interface TaskProvider {
    int getVersion();

    List<Task> getTasksOrderedByPriority(long contextId);

    List<Task> getTasksOrderedByWillingness(long contextId);

    Task create(Task task);

    void update(Task task);

    void swapPriority(long id1, long id2);

    void swapWillingness(long id1, long id2);

    void delete(long id);
}
