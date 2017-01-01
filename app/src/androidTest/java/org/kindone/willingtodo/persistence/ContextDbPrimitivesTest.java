package org.kindone.willingtodo.persistence;

import android.database.sqlite.SQLiteDatabase;

import junit.framework.TestCase;

/**
 * Created by kindone on 2016. 12. 31..
 */
public class ContextDbPrimitivesTest extends TestCase {
    private SQLiteDatabase mDatabase;
    private static String tableName = "contexts";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mDatabase = SQLiteDatabase.create(null);
    }

    @Override
    public void tearDown() throws Exception {
        if(mDatabase != null)
        {
            mDatabase.close();
        }
        super.tearDown();
    }

    public void testCreateTableIfNotExists() throws Exception {
        ContextDbPrimitives pr = new ContextDbPrimitives(tableName);
        pr.createTableIfNotExists(mDatabase);
    }

    public void testInsertDefaultEntries() throws Exception {
        ContextDbPrimitives pr = new ContextDbPrimitives(tableName);
        pr.createTableIfNotExists(mDatabase);
        pr.insertDefaultEntries(mDatabase);
    }

    public void testInsertContext() throws Exception {

    }

    public void testGetContextFromCurrentStarCursor() throws Exception {

    }

    public void testSelectAllContextsOrderedByPosition() throws Exception {

    }

    public void testSelectContextByRowId() throws Exception {

    }

    public void testGetContentValuesForContext() throws Exception {

    }

    public void testGetMode() throws Exception {

    }

    public void testSetMode() throws Exception {

    }

    public void testSetIntColumnValue() throws Exception {

    }

    public void testGetIntColumnValue() throws Exception {

    }

}