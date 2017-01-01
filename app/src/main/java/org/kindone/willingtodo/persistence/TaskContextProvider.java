package org.kindone.willingtodo.persistence;

import org.kindone.willingtodo.data.TaskContext;

import java.util.List;

/**
 * Created by kindone on 2016. 12. 22..
 */

public interface TaskContextProvider {

    List<TaskContext> getTaskContexts();

    int getMode(long contextId);

    void setMode(long contextId, int mode);

    TaskContext create(TaskContext context);

    void update(TaskContext context);

    void swap(long id1, long id2);

    void delete(long id);

    int getVersion();
}
