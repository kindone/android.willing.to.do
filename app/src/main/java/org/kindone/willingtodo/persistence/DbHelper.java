package org.kindone.willingtodo.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.kindone.willingtodo.data.Task;
import org.kindone.willingtodo.data.TaskContext;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kindone on 2015. 11. 7..
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbHelper";
    public final String contextTableName = "CONTEXTS";
    public final String taskTableName = "TASKS";
    public final String timerTableName = "TIMERS";
    private ContextDbPrimitives contextPrimitives = new ContextDbPrimitives(contextTableName);
    private TaskDbPrimitives taskPrimitives = new TaskDbPrimitives(taskTableName);
    private TimerDbPrimitives timerDbPrimitives = new TimerDbPrimitives(timerTableName);

    private final int LatestVersion = 2;
    private int mVersion; //state propagation

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int dbVersion) {
        super(context, name, factory, dbVersion);
        mVersion = 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        contextPrimitives.createTableIfNotExists(db);
        contextPrimitives.insertDefaultEntries(db);
        taskPrimitives.createTableIfNotExists(db);
        taskPrimitives.insertDummyEntries(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDbVersion, int newDbVersion) {
        if (oldDbVersion == 1)
            timerDbPrimitives.createTableIfNotExists(db);
    }

    public int getVersion() {
        return mVersion;
    }
    private void increaseVersion() { mVersion ++; }

    public List<TaskContext> getTaskContexts() {
        SQLiteDatabase db = getReadableDatabase();
        List<TaskContext> contexts = new LinkedList<TaskContext>();
        Cursor cursor = contextPrimitives.selectAllContextsOrderedByPosition(db);
        while (cursor.moveToNext()) {
            TaskContext context = contextPrimitives.getContextFromCurrentStarCursor(cursor);
            contexts.add(context);
        }
        return contexts;
    }

    public void insertTaskContext(TaskContext context) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            SQLiteStatement stmt = db.compileStatement("INSERT INTO " + taskTableName + " (name, position) " +
                    "VALUES (?, ?) FROM " + taskTableName);
            stmt.bindAllArgsAsStrings(new String[]{context.name, String.valueOf(context.position)});

            long row = stmt.executeInsert();
            Log.e(TAG, "insert result: rowId=" + row);
        } catch (Exception e) {
            Log.e(TAG, "insertTaskContext error: message=" + e.getMessage());
        } finally {
            db.close();
            increaseVersion();
        }
    }

    public void deleteTaskContext(long id) {
        SQLiteDatabase db = getWritableDatabase();

        String[] selectionArgs = {String.valueOf(id)};

        try {
            db.delete(contextTableName, "_id = ?", selectionArgs);
        } finally {
            db.close();
            increaseVersion();
        }

        Log.v(TAG, "deleteTaskContext");
    }

    public void updateTaskContext(long contextId, TaskContext context) {
        SQLiteDatabase db = getWritableDatabase();
        String[] selectionArgs = {String.valueOf(contextId)};

        try {
            db.update(contextTableName, contextPrimitives.getContentValuesForContext(context), "_id = ?", selectionArgs);
        } finally {
            db.close();
            increaseVersion();
        }
    }

    public int getContextMode(long contextId) {
        SQLiteDatabase db = getReadableDatabase();
        return contextPrimitives.getMode(db, contextId);
    }

    public void setContextMode(long contextId, int mode) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            contextPrimitives.setMode(db, contextId, mode);
        } finally {
            db.close();
            increaseVersion();
        }
    }

    public List<Task> getPriorityOrderedTasks(long contextId) {
        return getOrderedTasks("priority", contextId);
    }
    public List<Task> getWillingnessOrderedTasks(long contextId) {
        return getOrderedTasks("willingness", contextId);
    }
    public List<Task> getOrderedTasks(String orderedColumnName, long contextId) {
        SQLiteDatabase db = getReadableDatabase();
        List<Task> tasks = new LinkedList<>();

        Cursor cursor = taskPrimitives.selectAllTasksOrdered(db, orderedColumnName, contextId);
        while (cursor.moveToNext()) {
            Task task = taskPrimitives.getTaskFromCurrentStarCursor(cursor);
            tasks.add(task);
        }
        Log.v(TAG, "getOrderedTasks: size=" + tasks.size());
        return tasks;
    }

    public void insertTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            SQLiteStatement stmt = db.compileStatement("INSERT INTO " + taskTableName + " (title, context_id, category, deadline, priority, willingness) " +
                    "VALUES (?, ?, ?, ?, (SELECT IFNULL(MAX(priority),0) FROM " + taskTableName + ")+1, " +
                    "(SELECT IFNULL(MAX(willingness),0) FROM " + taskTableName + ")+1)");
            stmt.bindAllArgsAsStrings(new String[]{task.title, String.valueOf(task.contextId), task.category, task.deadline});

            long row = stmt.executeInsert();
            Log.e(TAG, "insert result: rowId=" + row);
        } catch (Exception e) {
            Log.e(TAG, "insertTask error: message=" + e.getMessage());
        } finally {
            db.close();
            increaseVersion();
        }

        taskPrimitives.checkNumTasks(getReadableDatabase());
    }

    public void swapTaskPriority(long id1, long id2) {
        swapTask("priority", id1, id2);
    }

    public void swapTaskWillingness(long id1, long id2) {
        swapTask("willingness", id1, id2);
    }

    private void swapTask(String columnName, long id1, long id2) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            // save id1's priority
            int id1_value = taskPrimitives.getIntColumnForTaskId(db, columnName, id1);
            Log.v(TAG, "swapTask val1=" + id1_value);
            int id2_value = taskPrimitives.getIntColumnForTaskId(db, columnName, id2);
            Log.v(TAG, "swapTask val1=" + id2_value);
            int numRowsAffected = taskPrimitives.setIntColumnValue(db, columnName, id2, -1);

            // set id1's priority to id2's priority
            numRowsAffected += taskPrimitives.setIntColumnValue(db, columnName, id1, id2_value);
            // set id2's priority
            numRowsAffected += taskPrimitives.setIntColumnValue(db, columnName, id2, id1_value);
            if (numRowsAffected == 3 &&
                    taskPrimitives.getIntColumnForTaskId(db, columnName, id1) !=
                    taskPrimitives.getIntColumnForTaskId(db, columnName, id2)) {
                db.setTransactionSuccessful();
            }
            else
                Log.e(TAG, "swapTask was not done properly. Affected rows=" + numRowsAffected);
        } catch (Exception e) {
            Log.v(TAG, "swapTask error: Exception=" + e.toString());
            throw e;
        } finally {
            db.endTransaction();
            db.close();
            increaseVersion();
        }

        taskPrimitives.checkTasks(getReadableDatabase());
        Log.v(TAG, "swapTask");
    }

    // update
    public void updateTask(long id, Task task) {
        SQLiteDatabase db = getWritableDatabase();
        String[] selectionArgs = {String.valueOf(id)};

        try {
            db.update(taskTableName, taskPrimitives.getContentValuesForTask(task), "_id = ?", selectionArgs);
        } finally {
            db.close();
            increaseVersion();
        }
    }

    // delete
    public void deleteTask(long id) {
        SQLiteDatabase db = getWritableDatabase();

        String[] selectionArgs = {String.valueOf(id)};

        try {
            db.delete(taskTableName, "_id = ?", selectionArgs);
        } finally {
            db.close();
            increaseVersion();
        }

        Log.v(TAG, "deleteTask");
    }
}
