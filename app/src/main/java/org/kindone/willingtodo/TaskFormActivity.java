package org.kindone.willingtodo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class TaskFormActivity extends AppCompatActivity {

    private static final String TAG = "TaskCreate";

    private static int layoutResourceId = R.layout.activity_task_create;

    private static int toolbarResourceId = R.id.toolbar;

    private static int createTaskMenuItemResourceId = R.menu.task_create;

    protected static String ARG_TASK_TITLE = MainActivity.RESULT_TASK_TITLE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String taskTitle = intent.getStringExtra(ARG_TASK_TITLE);

        initializeView(taskTitle);
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume");
        forceShowInputMethod();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        hideInputMethod();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(TAG, "onCreateOptionsMenu");
        initializeOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG, "onOptionsItemSelected");
        boolean processed = processOptionItemSelected(item);
        return processed || super.onOptionsItemSelected(item);
    }



    protected boolean processOptionItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_done:
                setActivityResult(RESULT_OK, getTaskTitle());
                finish();
                return true;
            default:
                return false;
        }
    }


    protected void setActivityResult(int status, String taskTitle)
    {
        Intent intent = this.getIntent();

        intent.putExtra(ARG_TASK_TITLE, taskTitle);

        if (getParent() == null) {
            setResult(status, intent);
        } else {
            getParent().setResult(status, intent);
        }
    }


    protected void initializeView(String title)
    {
        setContentView(layoutResourceId);
        initializeToolbar();
        setTaskTitle(title);
    }

    protected void initializeToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(toolbarResourceId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void initializeOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(createTaskMenuItemResourceId, menu);
    }


    protected void forceShowInputMethod()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    protected void hideInputMethod()
    {
        InputMethodManager immhide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    protected void setTaskTitle(String text) {
        EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
        titleEditText.setText(text);
    }

    protected String getTaskTitle() {
        EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
        return titleEditText.getText().toString();
    }

}
