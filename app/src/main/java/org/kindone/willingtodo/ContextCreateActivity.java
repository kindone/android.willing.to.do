package org.kindone.willingtodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class ContextCreateActivity extends AppCompatActivity {

    private static int viewResourceId = R.layout.activity_context_create;
    private static int toolbarResourceId = R.id.toolbar;
    private static int menuResourceId = R.menu.context_create;

    private static int titleEditTextResourceId = R.id.titleEditText;
    private static String CREATE_CONTEXT_TITLE = MainActivity.RESULT_CREATE_CONTEXT_TITLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        initializeMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean processed = processOptionItemSelected(item);
        return processed || super.onOptionsItemSelected(item);
    }

    private void initializeView()
    {
        setContentView(viewResourceId);
        setSupportActionBar((Toolbar) findViewById(toolbarResourceId));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeMenu(Menu menu)
    {
        getMenuInflater().inflate(menuResourceId, menu);
    }

    private boolean processOptionItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_done:
                setActivityResult(RESULT_OK, CREATE_CONTEXT_TITLE, getContextTitle());
                finish();
                return true;
            default:
                return false;
        }
    }

    private void setActivityResult(int status, String key, String value)
    {
        Intent intent = this.getIntent();
        intent.putExtra(key, value);

        if (getParent() == null) {
            setResult(status, intent);
        } else {
            getParent().setResult(status, intent);
        }
    }

    private String getContextTitle() {
        return ((EditText) findViewById(titleEditTextResourceId)).getText().toString();
    }

}
