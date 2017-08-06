package org.kindone.willingtodo.persistence.sqlite

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.util.Log

import org.kindone.willingtodo.data.TaskContext

/**
 * Created by kindone on 2016. 12. 14..
 */

class ConfigSqliteHelper(internal val tableName: String) {

    fun createTableIfNotExists(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $tableName(_id INTEGER PRIMARY KEY AUTOINCREMENT, key TEXT NOT NULL, value TEXT NOT NULL)")
        db.execSQL("CREATE UNIQUE INDEX " + tableName + "_KEY_IDX ON " + tableName + "(key)")
    }

    fun getCurrentTabIndex(db: SQLiteDatabase): Int {
        return getIntValue(db, "tab_index", 0)
    }

    fun setCurrentTabIndex(db: SQLiteDatabase, index: Int) {
        setRawValue(db, "tab_index", index.toString())
    }

    private fun prepareContentValues(key: String, value: String): ContentValues {
        val values = ContentValues()
        values.put("key", key)
        values.put("value", value)

        return values
    }

    private fun getRawValue(db: SQLiteDatabase, key: String): String? {
        val cursor = db.rawQuery("select value from $tableName where key = ?", arrayOf(key))
        if (cursor.moveToNext()) {
            return cursor.getString(0)
        }
        return null
    }

    private fun getIntValue(db: SQLiteDatabase, key: String, defaultValue: Int): Int {
        val cursor = db.rawQuery("select value from $tableName where key = ?", arrayOf(key))
        if (cursor.moveToNext()) {
            val value = cursor.getString(0)
            return Integer.valueOf(value)!!
        }
        return defaultValue
    }


    private fun setRawValue(db: SQLiteDatabase, key: String, value: String): Int {
        Log.v(TAG, "Set $key = $value")
        val stmt = db.compileStatement("INSERT OR REPLACE INTO $tableName(key,value) values(?, ?)")
        stmt.bindAllArgsAsStrings(arrayOf(key, value))
        val numRowsAffected = stmt.executeUpdateDelete()
        return numRowsAffected
    }

    companion object {
        internal val TAG = "ConfigSqliteHelper"
    }

}
