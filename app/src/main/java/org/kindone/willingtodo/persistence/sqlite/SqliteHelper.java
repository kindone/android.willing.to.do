package org.kindone.willingtodo.persistence.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.kindone.willingtodo.data.Task;
import org.kindone.willingtodo.data.TaskContext;

import java.util.List;

/**
 * Created by kindone on 2015. 11. 7..
 */
public class SqliteHelper extends SQLiteOpenHelper {

    private static final String TAG = "SqliteHelper";
    public static final int MODE_PRIORITY = 0;
    public static final int MODE_WILLINGNESS = 1;
    public static final String contextTableName = "CONTEXTS";
    public static final String taskTableName = "TASKS";
    public static final String timerTableName = "TIMERS";
    public static final String configTableName = "CONFIGS";

    private TaskContextSqliteHelper taskContextSqliteHelper = new TaskContextSqliteHelper(contextTableName);
    private TaskSqliteHelper taskPrimitives = new TaskSqliteHelper(taskTableName);
    private TimerSqliteHelper timerSqliteHelper = new TimerSqliteHelper(timerTableName);
    private ConfigSqliteHelper configSqliteHelper = new ConfigSqliteHelper(configTableName);

    private static int LatestVersion = 2;
    private int mVersion; //state propagation

    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, LatestVersion);
        mVersion = 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        initializeTaskContextTable(db);
        initializeTaskTable(db);
    }

    private void initializeTaskContextTable(SQLiteDatabase db)
    {
        taskContextSqliteHelper.createTableIfNotExists(db);
        taskContextSqliteHelper.insertDefaultEntries(db);
    }

    private void initializeTaskTable(SQLiteDatabase db)
    {
        taskPrimitives.createTableIfNotExists(db);
        taskPrimitives.insertDummyEntries(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDbVersion, int newDbVersion) {
        if(oldDbVersion == 1)
            configSqliteHelper.createTableIfNotExists(db);
        else if (oldDbVersion == 2)
            timerSqliteHelper.createTableIfNotExists(db);
    }

    public int getVersion() {
        return mVersion;
    }
    private void increaseVersion() { mVersion ++; }

    public List<TaskContext> getTaskContexts() {
        SQLiteDatabase db = getReadableDatabase();
        return taskContextSqliteHelper.getTaskContexts(db);
    }

    public TaskContext insertTaskContext(TaskContext context) {
        SQLiteDatabase db = getWritableDatabase();
        long rowId = 0L;
        try {
            rowId = taskContextSqliteHelper.insertTaskContext(db, context);
            Log.e(TAG, "insert result: rowId=" + rowId);

        } catch (Exception e) {
            Log.e(TAG, "insertTaskContext error: message=" + e.getMessage());
        } finally {
            db.close();
            increaseVersion();

            /** check **/
            SQLiteDatabase db2 = getReadableDatabase();
            TaskContext newContext = taskContextSqliteHelper.getTaskContextByRowId(db2, rowId);
            if(newContext == null)
                throw new RuntimeException("task context not properly inserted");

            return newContext;

        }
    }

    public void deleteTaskContext(long id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            taskContextSqliteHelper.deleteTaskContext(db, id);
        } finally {
            db.close();
            increaseVersion();
        }

        Log.v(TAG, "deleteTaskContext");
    }

    public void updateTaskContext(TaskContext context) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            taskContextSqliteHelper.updateTaskContext(db, context);
        } finally {
            db.close();
            increaseVersion();
        }
    }

    public int getContextMode(long contextId) {
        SQLiteDatabase db = getReadableDatabase();
        return taskContextSqliteHelper.getModeOfTaskContext(db, contextId);
    }

    public void setContextMode(long contextId, int mode) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            taskContextSqliteHelper.setModeOfTaskContext(db, contextId, mode);
        } finally {
            db.close();
            increaseVersion();
        }
    }

    public void swapPositionOfTaskContexts(long id1, long id2) {
        SQLiteDatabase db = getWritableDatabase();

        try {
           taskContextSqliteHelper.swapPositionOfTaskContexts(db, id1, id2);
        } catch (Exception e) {
            Log.v(TAG, "swapContext error: Exception=" + e.toString());
            throw e;
        } finally {
            db.close();
            increaseVersion();
        }
    }

    public List<Task> getPriorityOrderedTasks(long contextId) {
        SQLiteDatabase db = getReadableDatabase();
        return taskPrimitives.getPriorityOrderedTasks(db, contextId);
    }

    public List<Task> getWillingnessOrderedTasks(long contextId) {
        SQLiteDatabase db = getReadableDatabase();
        return taskPrimitives.getWillingnessOrderedTasks(db, contextId);
    }

    public void insertTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            long row = taskPrimitives.insertTask(db, task);
            Log.e(TAG, "insert result: rowId=" + row);
        } catch (Exception e) {
            Log.e(TAG, "insertTask error: message=" + e.getMessage());
        } finally {
            db.close();
            increaseVersion();
        }

        taskPrimitives.checkNumTasks(getReadableDatabase());
    }

    public void swapPriorityOfTasks(long id1, long id2) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            taskPrimitives.swapPriorityOfTasks(db, id1, id2);
        } catch (Exception e) {
            Log.v(TAG, "swapTask error: Exception=" + e.toString());
            throw e;
        } finally {
            db.close();
            increaseVersion();
        }

        taskPrimitives.checkTasks(getReadableDatabase());
        Log.v(TAG, "swapTask");
    }

    public void swapWillingnessOfTasks(long id1, long id2) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            taskPrimitives.swapWillingnessOfTasks(db, id1, id2);
        } catch (Exception e) {
            Log.v(TAG, "swapTask error: Exception=" + e.toString());
            throw e;
        } finally {
            db.close();
            increaseVersion();
        }

        taskPrimitives.checkTasks(getReadableDatabase());
        Log.v(TAG, "swapTask");
    }


    // updateTask
    public void updateTask(long id, Task task) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            taskPrimitives.updateTask(db, task);
        } finally {
            db.close();
            increaseVersion();
        }
    }

    // deleteTask
    public void deleteTask(long id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            taskPrimitives.deleteTask(db, id);
        } finally {
            db.close();
            increaseVersion();
        }

        Log.v(TAG, "deleteTask: id=" + id);
    }

    public int getCurrentTabIndex() {
        SQLiteDatabase db = getReadableDatabase();
        int tabIndex = configSqliteHelper.getCurrentTabIndex(db);
        db.close();
        return tabIndex;
    }

    public void setCurrentTabIndex(int index) {
        SQLiteDatabase db = getWritableDatabase();
        configSqliteHelper.setCurrentTabIndex(db, index);
        db.close();
    }
}
