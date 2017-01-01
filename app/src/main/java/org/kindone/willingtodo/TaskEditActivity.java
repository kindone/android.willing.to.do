package org.kindone.willingtodo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class TaskEditActivity extends AppCompatActivity {

    private long mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mId = intent.getLongExtra(MainActivity.RESULT_TASK_ID, -1);
        setTaskTitle(intent.getStringExtra(MainActivity.RESULT_TASK_TITLE));
    }

    @Override
    protected void onResume() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        super.onResume();
    }

    @Override
    protected void onPause() {
        InputMethodManager immhide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_done) {
            setResult();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setResult()
    {
        Intent intent = this.getIntent();
        intent.putExtra(MainActivity.RESULT_TASK_ID, mId);
        intent.putExtra(MainActivity.RESULT_TASK_TITLE, getTaskTitle());

        if (getParent() == null) {
            setResult(RESULT_OK, intent);
        } else {
            getParent().setResult(RESULT_OK, intent);
        }
        finish();
    }

    private void setTaskTitle(String text) {
        EditText titleText = (EditText) findViewById(R.id.titleEditText);
        titleText.setText(text);
    }

    private String getTaskTitle() {
        EditText titleText = (EditText) findViewById(R.id.titleEditText);
        return titleText.getText().toString();
    }

}
