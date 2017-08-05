package org.kindone.willingtodo.persistence.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.kindone.willingtodo.data.Task;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kindone on 2016. 12. 13..
 */

public class TaskSqliteHelper {
    final static String TAG = "TaskSqliteHelper";
    final String tableName;

    public TaskSqliteHelper(String tableName) {
        this.tableName = tableName;
    }

    public void createTableIfNotExists(SQLiteDatabase db) {
        createTable(db);
        createIndexes(db);
    }

    public void insertDummyEntries(SQLiteDatabase db) {
       Task dummyTask = new Task("dummy task", 1);
        insertTask(db, dummyTask);
    }

    public long insertTask(SQLiteDatabase db, Task task)
    {
        SQLiteStatement stmt = db.compileStatement("INSERT INTO " + tableName + " (title, context_id, category, deadline, priority, willingness) " +
                "VALUES (?, ?, ?, ?, (SELECT IFNULL(MAX(priority),0) FROM " + tableName + ")+1, " +
                "(SELECT IFNULL(MAX(willingness),0) FROM " + tableName + ")+1)");
        stmt.bindAllArgsAsStrings(new String[]{task.title, String.valueOf(task.contextId), task.category, task.deadline});

        return stmt.executeInsert();
    }

    public List<Task> getPriorityOrderedTasks(SQLiteDatabase db, long contextId)
    {
        return getOrderedTasks(db, "priority", contextId);
    }

    public List<Task> getWillingnessOrderedTasks(SQLiteDatabase db, long contextId)
    {
        return getOrderedTasks(db, "priority", contextId);
    }

    private List<Task> getOrderedTasks(SQLiteDatabase db, String orderedColumnName, long contextId) {
        List<Task> tasks = new LinkedList<>();

        Cursor cursor = selectAllTasksOrdered(db, orderedColumnName, contextId);
        while (cursor.moveToNext()) {
            Task task = getTaskFromCurrentStarCursor(cursor);
            tasks.add(task);
        }
        Log.v(TAG, "getOrderedTasks: size=" + tasks.size());
        return tasks;
    }

    public void updateTask(SQLiteDatabase db, Task task)
    {
        String[] selectionArgs = {String.valueOf(task.id)};
        db.update(tableName, getContentValuesForTask(task), "_id = ?", selectionArgs);
    }

    public void deleteTask(SQLiteDatabase db, long id)
    {
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(tableName, "_id = ?", selectionArgs);
    }

    public void swapPriorityOfTasks(SQLiteDatabase db, long id1, long id2)
    {
        swapTask(db, "priority", id1, id2);
    }

    public void swapWillingnessOfTasks(SQLiteDatabase db, long id1, long id2)
    {
        swapTask(db, "willingness", id1, id2);
    }

    private void swapTask(SQLiteDatabase db, String columnName, long id1, long id2)
    {
        db.beginTransaction();
        // save id1's value
        int id1_value = getIntColumnForTaskId(db, columnName, id1);
        Log.v(TAG, "swapTask val1=" + id1_value);
        int id2_value = getIntColumnForTaskId(db, columnName, id2);
        Log.v(TAG, "swapTask val1=" + id2_value);
        int numRowsAffected = setIntColumnValue(db, columnName, id2, -1);

        // set id1's value to id2's value
        numRowsAffected += setIntColumnValue(db, columnName, id1, id2_value);
        // set id2's value
        numRowsAffected += setIntColumnValue(db, columnName, id2, id1_value);
        if (numRowsAffected == 3 &&
                getIntColumnForTaskId(db, columnName, id1) == id2_value &&
                getIntColumnForTaskId(db, columnName, id2) == id1_value) {
            db.setTransactionSuccessful();
        }
        else
            Log.e(TAG, "swapTask was not done properly. Affected rows=" + numRowsAffected);

        db.endTransaction();
    }

    private Task getTaskFromCurrentStarCursor(Cursor cursor) {
        long id = cursor.getLong(0);
        String title = cursor.getString(1);
        int contextId = cursor.getInt(2);
        int priority = cursor.getInt(3);
        int willingness = cursor.getInt(4);
        String deadline = cursor.getString(5);
        String category = cursor.getString(6);

        return new Task(id, title, contextId, category, deadline, priority, willingness);
    }

    private ContentValues getContentValuesForTask(Task task) {
        ContentValues values = new ContentValues();
        values.put("title", task.title);
        values.put("context_id", task.contextId);
        values.put("priority", task.priority);
        values.put("willingness", task.willingness);
        values.put("category", task.category);
        values.put("deadline", task.deadline);
        values.put("category", task.category);

        return values;
    }

    private int setIntColumnValue(SQLiteDatabase db, String columnName, long targetId, int value) {
        SQLiteStatement stmt = db.compileStatement("UPDATE " + tableName + " SET " + columnName + " = ?" + "WHERE _id = ?");
        stmt.bindAllArgsAsStrings(new String[]{String.valueOf(value), String.valueOf(targetId)});
        int numRowsAffected = stmt.executeUpdateDelete();
        return numRowsAffected;
    }

    private int getIntColumnForTaskId(SQLiteDatabase db, String columnName, long taskId) {
        int value = 0;
        Cursor cursor = db.rawQuery("select " + columnName + " from " + tableName + " where _id = " + String.valueOf(taskId), null);
        if (cursor.moveToNext()) {
            value = cursor.getInt(0);
        }
        return value;
    }

    private Cursor selectAllTasks(SQLiteDatabase db) {
        return db.rawQuery("select * from " + tableName, null);
    }

    private Cursor selectAllTasksOrdered(SQLiteDatabase db, String columnName, long contextId) {
        Cursor cursor = db.rawQuery("select * from " + tableName + " where context_id = " + String.valueOf(contextId) + " order by " + columnName + " desc, _id desc", null);
        return cursor;
    }

    public int getTaskCount(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select count(*) from " + tableName, null);

        if (cursor.moveToNext()) return cursor.getInt(0);
        else return -1;
    }

    public void checkNumTasks(SQLiteDatabase db) {
        List<Task> tasks = new LinkedList<>();

        Cursor cursor = selectAllTasks(db);
        while (cursor.moveToNext()) {
            Task task = getTaskFromCurrentStarCursor(cursor);
            tasks.add(task);
        }

        Log.v(TAG, "count(task)=" + getTaskCount(db));
        Log.v(TAG, "number of tasks=" + tasks.size());

    }

    public void checkTasks(SQLiteDatabase db) {
        List<Task> tasks = new LinkedList<>();

        Cursor cursor = selectAllTasks(db);
        while (cursor.moveToNext()) {
            Task task = getTaskFromCurrentStarCursor(cursor);
            tasks.add(task);
            Log.v(TAG, "Tasks=" + task.toString());
        }

        db.close();
    }

    private void createTable(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + tableName + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " title TEXT NOT NULL," +
                " context_id INTEGER," +
                " priority INTEGER NOT NULL," +
                " willingness INTEGER NOT NULL," +
                " deadline TEXT, category TEXT)");
    }

    private void createIndexes(SQLiteDatabase db)
    {
        db.execSQL("CREATE UNIQUE INDEX " + tableName + "PRIORITY_IDX ON " + tableName + "(context_id, priority)");
        db.execSQL("CREATE UNIQUE INDEX " + tableName + "WILLINGNESS_IDX ON " + tableName + "(context_id, willingness)");
    }
}
