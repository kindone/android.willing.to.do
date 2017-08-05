package org.kindone.willingtodo.persistence;

import org.kindone.willingtodo.data.TaskContext;

import java.util.List;

/**
 * Created by kindone on 2016. 12. 22..
 */

public interface TaskContextPersistenceProvider {

    List<TaskContext> getTaskContexts();

    int getModeOfTaskContext(long contextId);

    void setModeOfTaskContext(long contextId, int mode);

    TaskContext createTaskContext(TaskContext context);

    void updateTaskContext(TaskContext context);

    void swapPositionOfTaskContexts(long id1, long id2);

    void deleteTaskContext(long id);

    int getVersion();
}
