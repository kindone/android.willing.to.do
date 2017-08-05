package org.kindone.willingtodo.persistence.sqlite;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kindone on 2016. 12. 13..
 */

public class TimerSqliteHelper {
    final static String TAG = "TimerSqliteHelper";
    final String tableName;

    public TimerSqliteHelper(String tableName)
    {
        this.tableName = tableName;
    }

    public void createTableIfNotExists(SQLiteDatabase db)
    {
        createTable(db);
        createIndex(db);
    }

    private void createTable(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + tableName + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task_id INTEGER NOT NULL, " +
                "title TEXT, " +
                "duration_min INTEGER NOT NULL, " +
                "started_ms INTEGER NOT NULL," +
                "elapsed_ms INTEGER NOT NULL," +
                "is_paused BOOLEAN NOT NULL, " +
                "is_active BOOLEAN NOT NULL)");
    }

    private void createIndex(SQLiteDatabase db)
    {
        db.execSQL("CREATE INDEX " + tableName + "_IDX ON " + tableName + "(is_active)");
    }
}
