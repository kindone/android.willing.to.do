package org.kindone.willingtodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class TaskCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        intent.putExtra(MainActivity.RESULT_CREATE_TASK_TITLE, getTaskTitle());
        intent.putExtra(MainActivity.RESULT_CREATE_TASK_CONTEXT_ID, 1L);

        if (getParent() == null) {
            setResult(RESULT_OK, intent);
        } else {
            getParent().setResult(RESULT_OK, intent);
        }
        finish();
    }

    private String getTaskTitle() {
        EditText titleText = (EditText) findViewById(R.id.titleEditText);
        return titleText.getText().toString();
    }

}
