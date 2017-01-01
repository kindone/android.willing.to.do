package org.kindone.willingtodo.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.kindone.willingtodo.data.TaskContext;

/**
 * Created by kindone on 2016. 12. 14..
 */

public class ConfigDbPrimitives {
    final static String TAG = "ConfigDbPrimitives";
    final String tableName;

    public ConfigDbPrimitives(String tableName) {
        this.tableName = tableName;
    }

    public void createTableIfNotExists(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + tableName + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + " key TEXT NOT NULL, value TEXT NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX " + tableName + "_KEY_IDX ON " + tableName + "(key)");
    }

    public ContentValues getContentValuesForContext(String key, String value) {
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("value", value);

        return values;
    }

    public String getRawValue(SQLiteDatabase db, String key) {
        Cursor cursor = db.rawQuery("select value from " + tableName + " where key = ?", new String[]{key});
        if(cursor.moveToNext()) {
            return cursor.getString(0);
        }
        return null;
    }

    public int getIntValue(SQLiteDatabase db, String key, int defaultValue) {
        Cursor cursor = db.rawQuery("select value from " + tableName + " where key = ?", new String[]{key});
        if(cursor.moveToNext()) {
            String value = cursor.getString(0);
            return Integer.valueOf(value);
        }
        return defaultValue;
    }


    public int setValue(SQLiteDatabase db, String key, String value) {
        Log.v(TAG, "Set " + key + " = " + value);
        SQLiteStatement stmt = db.compileStatement("INSERT OR REPLACE INTO " + tableName + "(key,value) values(?, ?)");
        stmt.bindAllArgsAsStrings(new String[]{key, value});
        int numRowsAffected = stmt.executeUpdateDelete();
        return numRowsAffected;
    }

}
