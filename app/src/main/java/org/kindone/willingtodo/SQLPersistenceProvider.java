package org.kindone.willingtodo;

import android.content.Context;

import org.kindone.willingtodo.data.Task;
import org.kindone.willingtodo.data.TaskContext;
import org.kindone.willingtodo.persistence.ConfigProvider;
import org.kindone.willingtodo.persistence.SqliteHelper;
import org.kindone.willingtodo.persistence.PersistenceProvider;
import org.kindone.willingtodo.persistence.TaskContextProvider;
import org.kindone.willingtodo.persistence.TaskProvider;

import java.util.List;

/**
 * Created by kindone on 2016. 12. 20..
 */
class SQLPersistenceProvider implements PersistenceProvider {

    final TaskProvider mTaskProvider;
    final TaskContextProvider mTaskContextProvider;
    final SqliteHelper mDbHelper;
    final ConfigProvider mConfigProvider;

    SQLPersistenceProvider(Context context) {
        mDbHelper = new SqliteHelper(context, "test", null/*default cursorfactory*/);

        mTaskProvider = new TaskProvider() {
            @Override
            public int getVersion() {
                return mDbHelper.getVersion();
            }

            public List<Task> getTasksOrderedByPriority(long contextId) {
                return mDbHelper.getPriorityOrderedTasks(contextId);
            }

            public List<Task> getTasksOrderedByWillingness(long contextId) {
                return mDbHelper.getWillingnessOrderedTasks(contextId);
            }

            public Task create(Task task) {
                mDbHelper.insertTask(task);
                return task;
            }

            @Override
            public void update(Task task) {
                mDbHelper.updateTask(task.id, task);
            }

            public void swapPriority(long id1, long id2) {
                mDbHelper.swapTaskPriority(id1, id2);
            }

            public void swapWillingness(long id1, long id2) {
                mDbHelper.swapTaskWillingness(id1, id2);
            }

            public void delete(long id) {
                mDbHelper.deleteTask(id);
            }
        };

        mTaskContextProvider = new TaskContextProvider() {
            @Override
            public List<TaskContext> getTaskContexts() {
                return mDbHelper.getTaskContexts();
            }

            @Override
            public int getMode(long contextId) {
                return mDbHelper.getContextMode(contextId);
            }

            @Override
            public void setMode(long contextId, int mode) {
                mDbHelper.setContextMode(contextId, mode);
            }

            @Override
            public TaskContext create(TaskContext context) {
                return mDbHelper.insertTaskContext(context);
            }

            @Override
            public void update(TaskContext context) {
                mDbHelper.updateTaskContext(context.id, context);
            }

            @Override
            public void swap(long id1, long id2) {
                mDbHelper.swapTaskContext(id1, id2);
            }

            @Override
            public void delete(long id) {
                mDbHelper.deleteTaskContext(id);
            }

            @Override
            public int getVersion() {
                return mDbHelper.getVersion();
            }
        };

        mConfigProvider = new ConfigProvider() {
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
    public TaskProvider getTaskProvider() {
        return mTaskProvider;
    }

    @Override
    public TaskContextProvider getContextProvider() { return mTaskContextProvider; }

    @Override
    public ConfigProvider getConfigProvider() {
        return mConfigProvider;
    }

    @Override
    public int getVersion() {
        return mDbHelper.getVersion();
    }
}
