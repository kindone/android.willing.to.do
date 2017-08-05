package org.kindone.willingtodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.kindone.willingtodo.data.TaskContextListItem;
import org.kindone.willingtodo.data.TaskContext;
import org.kindone.willingtodo.persistence.ConfigPersistenceProvider;
import org.kindone.willingtodo.persistence.PersistenceProvider;
import org.kindone.willingtodo.persistence.TaskContextPersistenceProvider;
import org.kindone.willingtodo.persistence.TaskPersistenceProvider;
import org.kindone.willingtodo.recyclerlist.context.ContextRecyclerListFragment;
import org.kindone.willingtodo.recyclerlist.RecyclerListFragment;


public class ManageContextActivity extends AppCompatActivity
        implements PersistenceProvider {

    public static String TAG = "ManageContextAc";
    public static int INTENT_CREATE_CONTEXT = 1;
    public static String RESULT_CREATE_TASK_CONTEXT_ID = "RESULT_CREATE_CONTEXT_ID";
    public static String RESULT_CREATE_TASK_DEADLINE = "RESULT_CREATE_TASK_DEADLINE";
    public static String RESULT_CREATE_CONTEXT_TITLE = "RESULT_CREATE_CONTEXT_TITLE";


    private RecyclerListFragment mCurrentListFragment;
    private SQLPersistenceProvider mPersistenceProvider = new SQLPersistenceProvider(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_context);
        initToolbar();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mCurrentListFragment = ContextRecyclerListFragment.create();
        fragmentTransaction.add(R.id.content_manage_context, mCurrentListFragment);
        fragmentTransaction.commit();

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // TODO
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_CREATE_CONTEXT) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra(RESULT_CREATE_CONTEXT_TITLE);
                mCurrentListFragment.onCreateItem(new TaskContextListItem(new TaskContext(0, name, 0, 0)));
            }
        }

    }

    @Override
    public TaskContextPersistenceProvider getTaskContextPersistenceProvider() {
        return mPersistenceProvider.getTaskContextPersistenceProvider();
    }

    @Override
    public TaskPersistenceProvider getTaskPersistenceProvider() {
        return null;
    }

    @Override
    public ConfigPersistenceProvider getConfigPersistenceProvider() {
        return null;
    }

    @Override
    public int getVersion() {
        return mPersistenceProvider.getVersion();
    }
}
