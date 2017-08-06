package org.kindone.willingtodo.persistence.sqlite

import android.database.sqlite.SQLiteDatabase

/**
 * Created by kindone on 2016. 12. 13..
 */

class TimerSqliteHelper(internal val tableName: String) {

    fun createTableIfNotExists(db: SQLiteDatabase) {
        createTable(db)
        createIndex(db)
    }

    private fun createTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE " + tableName + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task_id INTEGER NOT NULL, " +
                "title TEXT, " +
                "duration_min INTEGER NOT NULL, " +
                "started_ms INTEGER NOT NULL," +
                "elapsed_ms INTEGER NOT NULL," +
                "is_paused BOOLEAN NOT NULL, " +
                "is_active BOOLEAN NOT NULL)")
    }

    private fun createIndex(db: SQLiteDatabase) {
        db.execSQL("CREATE INDEX " + tableName + "_IDX ON " + tableName + "(is_active)")
    }

    companion object {
        internal val TAG = "TimerSqliteHelper"
    }
}
