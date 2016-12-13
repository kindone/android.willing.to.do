package org.kindone.willingtodo.persistence;

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

public class TaskDbPrimitives {
    final static String TAG = "TaskDbPrimitives";
    final String tableName;

    public TaskDbPrimitives(String tableName)
    {
        this.tableName = tableName;
    }

    public void createTableIfNotExists(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + tableName + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " title TEXT NOT NULL, priority INTEGER NOT NULL, willingness INTEGER NOT NULL, deadline TEXT, category TEXT)");
    }

    public void insertDummyEntries(SQLiteDatabase db)
    {
        SQLiteStatement stmt = db.compileStatement("INSERT INTO " + tableName + " (title, category, deadline, priority, willingness) " +
                "VALUES (?, ?, ?, (SELECT IFNULL(MAX(priority),0) FROM " + tableName + ")+1, " +
                "(SELECT IFNULL(MAX(willingness),0) FROM " + tableName + ")+1)");

        stmt.bindAllArgsAsStrings(new String[]{"existing task", "", ""});
        stmt.executeInsert();
    }

    public Task getTaskFromCurrentStarCursor(Cursor cursor) {
        int id = cursor.getInt(0);
        String title = cursor.getString(1);
        int priority = cursor.getInt(2);
        int willingness = cursor.getInt(3);
        String deadline = cursor.getString(4);
        String category = cursor.getString(5);

        return new Task(id, title, category, deadline, priority, willingness);
    }

    public ContentValues getContentValuesForTask(Task task)
    {
        ContentValues values = new ContentValues();
        values.put("title", task.title);
        values.put("category", task.category);
        values.put("deadline", task.deadline);
        return values;
    }

    public int copyColumnValue(SQLiteDatabase db, String columnName, long target, long src)
    {
        // id1's priority = id2's priority
        SQLiteStatement stmt = db.compileStatement("UPDATE " + tableName + " SET " + columnName + " = (SELECT " + columnName + " FROM " + tableName + " WHERE _id = ?)" +
                "WHERE _id = ?");
        stmt.bindAllArgsAsStrings(new String[]{String.valueOf(target), String.valueOf(src)});
        int numRowsAffected = stmt.executeUpdateDelete();
        return numRowsAffected;
    }

    public int setIntColumnValue(SQLiteDatabase db, String columnName, long target, int value)
    {
        SQLiteStatement stmt = db.compileStatement("UPDATE " + tableName + " SET " + columnName + " = ?" +
                "WHERE _id = ?");
        stmt.bindAllArgsAsStrings(new String[]{String.valueOf(value), String.valueOf(target)});
        int numRowsAffected = stmt.executeUpdateDelete();
        return numRowsAffected;
    }

    public int getIntColumnForTaskId(SQLiteDatabase db, String columnName, long taskId)
    {
        int value = 0;
        Cursor cursor = db.rawQuery("select " + columnName + " from " + tableName + " where _id = " + String.valueOf(taskId), null);
        while (cursor.moveToNext()) {
            value = cursor.getInt(0);
        }
        return value;
    }

    public Cursor selectAllTasks(SQLiteDatabase db) {
        return db.rawQuery("select * from " + tableName, null);
    }

    public Cursor selectAllTasksOrdered(SQLiteDatabase db, String columnName) {
        Cursor cursor = db.rawQuery("select * from " + tableName + " order by " + columnName + " desc, _id desc", null);
        return cursor;
    }

    public int getTaskCount(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select count(*) from " + tableName, null);

        if (cursor.moveToNext())
            return cursor.getInt(0);
        else
            return -1;
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
}
