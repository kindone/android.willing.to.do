package org.kindone.willingtodo.persistence.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.kindone.willingtodo.data.Task;
import org.kindone.willingtodo.data.TaskContext;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kindone on 2016. 12. 14..
 */

public class TaskContextSqliteHelper {
    final static String TAG = "TaskContextSqliteHelper";
    final String tableName;

    public TaskContextSqliteHelper(String tableName) {
        this.tableName = tableName;
    }

    public void createTableIfNotExists(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + tableName + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + " name TEXT NOT NULL, position INTEGER, mode INTEGER)");
        db.execSQL("CREATE UNIQUE INDEX " + tableName + "_POSITION_IDX ON " + tableName + "(position)");
    }

    public void insertDefaultEntries(SQLiteDatabase db) {
        SQLiteStatement stmt = db.compileStatement("INSERT INTO " + tableName + " (name, position, mode) " + "VALUES (?, ?, ?)");
        stmt.bindAllArgsAsStrings(new String[]{"Work", "0", "0"});
        stmt.executeInsert();
    }

    public long insertTaskContext(SQLiteDatabase db, TaskContext context) {

        SQLiteStatement stmt = db.compileStatement("INSERT INTO " + tableName + " (name, position) " +
                "VALUES (?, (SELECT IFNULL(MAX(position),0) FROM " + tableName + ")+1)");
        stmt.bindAllArgsAsStrings(new String[]{context.name});

        return stmt.executeInsert();
    }

    public List<TaskContext> getTaskContexts(SQLiteDatabase db) {
        List<TaskContext> contexts = new LinkedList<TaskContext>();
        Cursor cursor = selectAllContextsOrderedByPosition(db);
        while (cursor.moveToNext()) {
            TaskContext context = getTaskContextFromCurrentStarCursor(cursor);
            contexts.add(context);
        }
        return contexts;
    }

    public TaskContext getTaskContextByRowId(SQLiteDatabase db, long id) {
        Cursor cursor = selectContextByRowId(db, id);
        if (cursor.moveToNext()) {
            TaskContext context = getTaskContextFromCurrentStarCursor(cursor);
            return context;
        }
        return null;
    }

    public void updateTaskContext(SQLiteDatabase db, TaskContext taskContext)
    {
        String[] selectionArgs = {String.valueOf(taskContext.id)};
        db.update(tableName, getContentValuesForContext(taskContext), "_id = ?", selectionArgs);
    }

    public void deleteTaskContext(SQLiteDatabase db, long id)
    {
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(tableName, "_id = ?", selectionArgs);
    }

    public void swapPositionOfTaskContexts(SQLiteDatabase db, long id1, long id2)
    {
        db.beginTransaction();
        // save id1's value
        int id1_value = getIntColumnValue(db, "position", id1);
        Log.v(TAG, "swapContext val1=" + id1_value);
        int id2_value = getIntColumnValue(db, "position", id2);
        Log.v(TAG, "swapContext val2=" + id2_value);
        int numRowsAffected = setIntColumnValue(db, "position", id2, -1);

        // set id1's value to id2's value
        numRowsAffected += setIntColumnValue(db, "position", id1, id2_value);
        // set id2's value
        numRowsAffected += setIntColumnValue(db, "position", id2, id1_value);
        if (numRowsAffected == 3 &&
                getIntColumnValue(db, "position", id1) == id2_value &&
                getIntColumnValue(db, "position", id2) == id1_value) {
            db.setTransactionSuccessful();
        }
        else
            Log.e(TAG, "swapContext was not done properly. Affected rows=" + numRowsAffected);
    }

    public int getModeOfTaskContext(SQLiteDatabase db, long id) {
        Cursor cursor = db.rawQuery("select mode from " + tableName + " where _id = " + String.valueOf(id), null);
        if(cursor.moveToNext()) {
          return cursor.getInt(0);
        }
        return 0; // default
    }

    public void setModeOfTaskContext(SQLiteDatabase db, long id, int mode) {
        String[] selectionArgs = {String.valueOf(id)};

        ContentValues values = new ContentValues();
        values.put("mode", mode);
        db.update(tableName, values, "_id = ?", selectionArgs);
    }


    private TaskContext getTaskContextFromCurrentStarCursor(Cursor cursor) {
        long id = cursor.getLong(0);
        String name = cursor.getString(1);
        int position = cursor.getInt(2);
        int mode = cursor.getInt(3);
        return new TaskContext(id, name, position, mode);
    }

    private Cursor selectAllContextsOrderedByPosition(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from " + tableName + " order by position asc, _id desc", null);
        return cursor;
    }

    private Cursor selectContextByRowId(SQLiteDatabase db, long id) {
        Cursor cursor = db.rawQuery("select * from " + tableName + " where rowid = "
                + String.valueOf(id), null);
        return cursor;
    }

    private ContentValues getContentValuesForContext(TaskContext context) {
        ContentValues values = new ContentValues();
        values.put("name", context.name);
        values.put("position", context.position);
        values.put("mode", context.mode);

        return values;
    }


    private int setIntColumnValue(SQLiteDatabase db, String columnName, long targetId, int value) {
        SQLiteStatement stmt = db.compileStatement("UPDATE " + tableName + " SET " + columnName + " = ?" + "WHERE _id = ?");
        stmt.bindAllArgsAsStrings(new String[]{String.valueOf(value), String.valueOf(targetId)});
        int numRowsAffected = stmt.executeUpdateDelete();
        return numRowsAffected;
    }

    private int getIntColumnValue(SQLiteDatabase db, String columnName, long taskId) {
        int value = 0;
        Cursor cursor = db.rawQuery("select " + columnName + " from " + tableName + " where _id = " + String.valueOf(taskId), null);
        if (cursor.moveToNext()) {
            value = cursor.getInt(0);
        }
        return value;
    }
}