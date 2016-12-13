package org.kindone.willingtodo.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.kindone.willingtodo.data.Task;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kindone on 2015. 11. 7..
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbHelper";
    public final String taskTableName = "TASKS";
    public final String timerTableName = "TIMERS";
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


    public List<Task> getPriorityOrderedTasks() {
        return getOrderedTasks("priority");
    }
    public List<Task> getWillingnessOrderedTasks() {
        return getOrderedTasks("willingness");
    }
    public List<Task> getOrderedTasks(String orderedColumnName) {
        SQLiteDatabase db = getReadableDatabase();
        List<Task> tasks = new LinkedList<>();

        Cursor cursor = taskPrimitives.selectAllTasksOrdered(db, orderedColumnName);
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
            SQLiteStatement stmt = db.compileStatement("INSERT INTO " + taskTableName + " (title, category, deadline, priority, willingness) " +
                    "VALUES (?, ?, ?, (SELECT IFNULL(MAX(priority),0) FROM " + taskTableName + ")+1, " +
                    "(SELECT IFNULL(MAX(willingness),0) FROM " + taskTableName + ")+1)");
            stmt.bindAllArgsAsStrings(new String[]{task.title, task.category, task.deadline});

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

            // set id1's priority to id2's priority
            int numRowsAffected = taskPrimitives.copyColumnValue(db, columnName, id1, id2);
            // set id2's priority
            numRowsAffected += taskPrimitives.setIntColumnValue(db, columnName, id2, id1_value);
            if (numRowsAffected == 2)
                db.setTransactionSuccessful();
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
