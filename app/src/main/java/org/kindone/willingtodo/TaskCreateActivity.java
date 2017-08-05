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

public class TaskCreateActivity extends TaskFormActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String taskTitle = intent.getStringExtra(ARG_TASK_TITLE);

        initializeView(taskTitle);
    }


    @Override
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


    @Override
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



}
