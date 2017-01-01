package org.kindone.willingtodo.persistence;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kindone on 2016. 12. 13..
 */

public class TimerDbPrimitives {
    final static String TAG = "TimerDbPrimitives";
    final String tableName;

    public TimerDbPrimitives(String tableName)
    {
        this.tableName = tableName;
    }

    public void createTableIfNotExists(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + tableName + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task_id INTEGER NOT NULL, " +
                "title TEXT, " +
                "duration_min INTEGER NOT NULL, " +
                "started_ms INTEGER NOT NULL," +
                "elapsed_ms INTEGER NOT NULL," +
                "is_paused BOOLEAN NOT NULL, " +
                "is_active BOOLEAN NOT NULL)");
        db.execSQL("CREATE INDEX " + tableName + "_IDX ON " + tableName + "(is_active)");
    }
}
