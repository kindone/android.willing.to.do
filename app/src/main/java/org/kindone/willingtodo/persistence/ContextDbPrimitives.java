package org.kindone.willingtodo.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import org.kindone.willingtodo.data.Task;
import org.kindone.willingtodo.data.TaskContext;

/**
 * Created by kindone on 2016. 12. 14..
 */

public class ContextDbPrimitives {
    final static String TAG = "ContextDbPrimitives";
    final String tableName;

    public ContextDbPrimitives(String tableName) {
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

    public TaskContext getContextFromCurrentStarCursor(Cursor cursor) {
        long id = cursor.getLong(0);
        String name = cursor.getString(1);
        int position = cursor.getInt(2);
        int mode = cursor.getInt(3);

        return new TaskContext(id, name, position, mode);
    }

    public Cursor selectAllContextsOrderedByPosition(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from " + tableName + " order by position asc, _id desc", null);
        return cursor;
    }

    public ContentValues getContentValuesForContext(TaskContext context) {
        ContentValues values = new ContentValues();
        values.put("name", context.name);
        values.put("position", context.position);
        values.put("mode", context.mode);

        return values;
    }

    public int getMode(SQLiteDatabase db, long id) {
        Cursor cursor = db.rawQuery("select mode from " + tableName + " where _id = " + String.valueOf(id), null);
        if(cursor.moveToNext()) {
          return cursor.getInt(0);
        }
        return 0; // default
    }

    public void setMode(SQLiteDatabase db, long id, int mode) {
        String[] selectionArgs = {String.valueOf(id)};

        ContentValues values = new ContentValues();
        values.put("mode", mode);
        db.update(tableName, values, "_id = ?", selectionArgs);

    }
}