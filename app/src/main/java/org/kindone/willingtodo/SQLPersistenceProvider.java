package org.kindone.willingtodo;

import android.content.Context;

import org.kindone.willingtodo.data.Task;
import org.kindone.willingtodo.data.TaskContext;
import org.kindone.willingtodo.persistence.ConfigPersistenceProvider;
import org.kindone.willingtodo.persistence.sqlite.SqliteHelper;
import org.kindone.willingtodo.persistence.PersistenceProvider;
import org.kindone.willingtodo.persistence.TaskContextPersistenceProvider;
import org.kindone.willingtodo.persistence.TaskPersistenceProvider;

import java.util.List;

/**
 * Created by kindone on 2016. 12. 20..
 */
public class SQLPersistenceProvider implements PersistenceProvider {

    final TaskPersistenceProvider mTaskPersistenceProvider;
    final TaskContextPersistenceProvider mTaskContextPersistenceProvider;
    final SqliteHelper mDbHelper;
    final ConfigPersistenceProvider mConfigPersistenceProvider;

    SQLPersistenceProvider(Context context) {
        mDbHelper = new SqliteHelper(context, "test", null/*default cursorfactory*/);

        mTaskPersistenceProvider = new TaskPersistenceProvider() {
            @Override
            public int getVersion() {
                return mDbHelper.getVersion();
            }

            public List<Task> getTasksOfContextOrderedByPriority(long contextId) {
                return mDbHelper.getPriorityOrderedTasks(contextId);
            }

            public List<Task> getTasksOfContextOrderedByWillingness(long contextId) {
                return mDbHelper.getWillingnessOrderedTasks(contextId);
            }

            public Task createTask(Task task) {
                mDbHelper.insertTask(task);
                return task;
            }

            @Override
            public void updateTask(Task task) {
                mDbHelper.updateTask(task.id, task);
            }

            public void swapPriorityOfTasks(long id1, long id2) {
                mDbHelper.swapPriorityOfTasks(id1, id2);
            }

            public void swapWillingnessOfTasks(long id1, long id2) {
                mDbHelper.swapWillingnessOfTasks(id1, id2);
            }

            public void deleteTask(long id) {
                mDbHelper.deleteTask(id);
            }
        };

        mTaskContextPersistenceProvider = new TaskContextPersistenceProvider() {
            @Override
            public List<TaskContext> getTaskContexts() {
                return mDbHelper.getTaskContexts();
            }

            @Override
            public int getModeOfTaskContext(long contextId) {
                return mDbHelper.getContextMode(contextId);
            }

            @Override
            public void setModeOfTaskContext(long contextId, int mode) {
                mDbHelper.setContextMode(contextId, mode);
            }

            @Override
            public TaskContext createTaskContext(TaskContext context) {
                return mDbHelper.insertTaskContext(context);
            }

            @Override
            public void updateTaskContext(TaskContext context) {
                mDbHelper.updateTaskContext(context);
            }

            @Override
            public void swapPositionOfTaskContexts(long id1, long id2) {
                mDbHelper.swapPositionOfTaskContexts(id1, id2);
            }

            @Override
            public void deleteTaskContext(long id) {
                mDbHelper.deleteTaskContext(id);
            }

            @Override
            public int getVersion() {
                return mDbHelper.getVersion();
            }
        };

        mConfigPersistenceProvider = new ConfigPersistenceProvider() {
            @Override
            public int getTabIndex() {
                return mDbHelper.getCurrentTabIndex();
            }

            @Override
            public void saveTabIndex(int index) {
                mDbHelper.setCurrentTabIndex(index);
            }

            @Override
            public int getVersion() {
                return mDbHelper.getVersion();
            }
        };
    }

    @Override
    public TaskPersistenceProvider getTaskPersistenceProvider() {
        return mTaskPersistenceProvider;
    }

    @Override
    public TaskContextPersistenceProvider getTaskContextPersistenceProvider() { return mTaskContextPersistenceProvider; }

    @Override
    public ConfigPersistenceProvider getConfigPersistenceProvider() {
        return mConfigPersistenceProvider;
    }

    @Override
    public int getVersion() {
        return mDbHelper.getVersion();
    }
}
