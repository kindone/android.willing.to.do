package org.kindone.willingtodo

import android.content.Context
import android.util.Log
import org.kindone.willingtodo.persistence.sqlite.SqliteHelper

/**
 * Created by kindone on 2017. 8. 13..
 */

object SqliteHelperSingleton
{
    private var mSqliteHelper:SqliteHelper? = null

    fun get(context:Context):SqliteHelper {
        if(mSqliteHelper == null) {
            mSqliteHelper = SqliteHelper(context.applicationContext, "test", null)
            Log.v("SQLITEHELPER", "created new instance")
        }

        return mSqliteHelper!!
    }
}