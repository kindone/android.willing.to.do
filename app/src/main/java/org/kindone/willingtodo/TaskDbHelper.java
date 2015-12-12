package org.kindone.willingtodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kindone on 2015. 11. 7..
 */
public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "TaskDbHelper";
    public final String tableName = "TASKS";
    private int mVersion; //state propagation TODO: confusing name

    public TaskDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mVersion = 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + tableName + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " title TEXT NOT NULL, priority INTEGER NOT NULL, willingness INTEGER NOT NULL, deadline TEXT, category TEXT)");

        SQLiteStatement stmt = db.compileStatement("INSERT INTO " + tableName + " (title, category, deadline, priority, willingness) " +
                "VALUES (?, ?, ?, (SELECT IFNULL(MAX(priority),0) FROM " + tableName + ")+1, " +
                "(SELECT IFNULL(MAX(willingness),0) FROM " + tableName + ")+1)");

        stmt.bindAllArgsAsStrings(new String[]{"existing task", "", ""});
        stmt.executeInsert();

//        stmt.bindAllArgsAsStrings(new String[]{"existing task2", "", ""});
//        stmt.executeInsert();
    }

    public int getVersion() {
        return mVersion;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public List<Task> getPriorityOrderedTasks() {
        return getOrderedTasks("priority");
    }

    public List<Task> getWillingnessOrderedTasks() {
        return getOrderedTasks("willingness");
    }

    public List<Task> getOrderedTasks(String columnName) {
        SQLiteDatabase db = getReadableDatabase();
        List<Task> tasks = new LinkedList<>();

        Cursor cursor = db.rawQuery("select * from " + tableName + " order by " + columnName + " desc, _id desc", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            int priority = cursor.getInt(2);
            int willingness = cursor.getInt(3);
            String deadline = cursor.getString(4);
            String category = cursor.getString(5);
            tasks.add(new Task(id, title, category, deadline, priority, willingness));
        }

        Log.v(TAG, "getOrderedTasks: " + tasks.size());

        return tasks;
    }

    public void insertTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            SQLiteStatement stmt = db.compileStatement("INSERT INTO " + tableName + " (title, category, deadline, priority, willingness) " +
                    "VALUES (?, ?, ?, (SELECT IFNULL(MAX(priority),0) FROM " + tableName + ")+1, " +
                    "(SELECT IFNULL(MAX(willingness),0) FROM " + tableName + ")+1)");
            stmt.bindAllArgsAsStrings(new String[]{task.title, task.category, task.deadline});

            long row = stmt.executeInsert();
            Log.e(TAG, "insert result: " + row);
        } catch (Exception e) {
            Log.e(TAG, "insertTask error: " + e.getMessage());
        } finally {
            db.close();
            mVersion++;
        }

        checkNumTasks();
    }

    public void swapTaskPriority(long id1, long id2) {
        swapTask("priority", id1, id2);
    }

    public void swapTaskWillingness(long id1, long id2) {
        swapTask("willingness", id1, id2);
    }

    public void swapTask(String columnName, long id1, long id2) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            // save id1's priority
            int id1_value = 0;
            Cursor cursor = db.rawQuery("select " + columnName + " from " + tableName + " where _id = " + String.valueOf(id1), null);
            while (cursor.moveToNext()) {
                id1_value = cursor.getInt(0);
            }

            Log.v(TAG, "swapTask val1:" + id1_value);

            // id1's priority = id2's priority
            SQLiteStatement stmt = db.compileStatement("UPDATE " + tableName + " SET " + columnName + " = (SELECT " + columnName + " FROM " + tableName + " WHERE _id = ?)" +
                    "WHERE _id = ?");
            stmt.bindAllArgsAsStrings(new String[]{String.valueOf(id2), String.valueOf(id1)});
            int numRowsAffected = stmt.executeUpdateDelete();

            // set id2's priority
            stmt = db.compileStatement("UPDATE " + tableName + " SET " + columnName + " = ?" +
                    "WHERE _id = ?");
            stmt.bindAllArgsAsStrings(new String[]{String.valueOf(id1_value), String.valueOf(id2)});
            numRowsAffected += stmt.executeUpdateDelete();
            if (numRowsAffected == 2)
                db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.v(TAG, "swapTask error:" + e.toString());
            throw e;
        } finally {
            db.endTransaction();
            db.close();
            mVersion++;
        }

        checkTasks();
        Log.v(TAG, "swapTask");

    }

    // update
    public void updateTask(long id, Task task) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", task.title);
        values.put("category", task.category);
        values.put("deadline", task.deadline);

        String[] selectionArgs = {String.valueOf(id)};

        try {
            db.update(tableName, values, "_id = ?", selectionArgs);
        } finally {
            db.close();
        }
    }

    // delete
    public void deleteTask(long id) {
        SQLiteDatabase db = getWritableDatabase();

        String[] selectionArgs = {String.valueOf(id)};

        try {
            db.delete(tableName, "_id = ?", selectionArgs);
        } finally {
            db.close();
            mVersion++;
        }

        Log.v(TAG, "deleteTask");
    }

    private void checkNumTasks() {
        SQLiteDatabase db = getReadableDatabase();
        List<Task> tasks = new LinkedList<>();

        Cursor cursor = db.rawQuery("select * from " + tableName, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            int priority = cursor.getInt(2);
            int willingness = cursor.getInt(3);
            String deadline = cursor.getString(4);
            String category = cursor.getString(5);
            tasks.add(new Task(id, title, category, deadline, priority, willingness));
        }

        cursor = db.rawQuery("select count(*) from " + tableName, null);
        if (cursor.moveToNext())
            Log.v(TAG, "count(task): " + cursor.getInt(0));

        Log.v(TAG, "number of tasks: " + tasks.size());

    }

    private void checkTasks() {
        SQLiteDatabase db = getReadableDatabase();
        List<Task> tasks = new LinkedList<>();

        Cursor cursor = db.rawQuery("select * from " + tableName, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            int priority = cursor.getInt(2);
            int willingness = cursor.getInt(3);
            String deadline = cursor.getString(4);
            String category = cursor.getString(5);
            Task task = new Task(id, title, category, deadline, priority, willingness);
            tasks.add(task);
            Log.v(TAG, "Tasks: " + task.toString());
        }

        db.close();

    }

}
