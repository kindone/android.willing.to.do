package org.kindone.willingtodo.persistence;

import junit.framework.TestCase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Created by kindone on 2016. 12. 31..
 */
public class TaskSqliteHelperTest extends TestCase
{
    private SQLiteDatabase mDatabase;
    private String tableName = "tasks";

    @BeforeClass
    public void setUpTestCase() {
        mDatabase = SQLiteDatabase.create(null);
    }


    @AfterClass
    public void tearDownTestCase() {
        mDatabase.close();
        mDatabase = null;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreateTableIfNotExists() throws Exception {

    }

    public void testInsertDummyEntries() throws Exception {

    }

    public void testInsertTask() throws Exception {

    }

    public void testGetTaskFromCurrentStarCursor() throws Exception {

    }

    public void testGetContentValuesForTask() throws Exception {

    }

    public void testSetIntColumnValue() throws Exception {

    }

    public void testGetIntColumnForTaskId() throws Exception {

    }

    public void testSelectAllTasks() throws Exception {

    }

    public void testSelectAllTasksOrdered() throws Exception {

    }

    public void testGetTaskCount() throws Exception {

    }

    public void testCheckNumTasks() throws Exception {

    }

    public void testCheckTasks() throws Exception {

    }

}