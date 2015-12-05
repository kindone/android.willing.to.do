package org.kindone.willingtodo;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.kindone.willingtodo.helper.TaskDbHelperProvider;

import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, TaskDbHelperProvider,
        TaskRecyclerListFragment.TaskManipulationListener,
        TaskRecyclerListAdapter.TaskLoader {

    public static int INTENT_CREATE_TASK = 1;
    public static String RESULT_CREATE_TASK_TITLE = "RESULT_CREATE_TASK_TITLE";
    public static String RESULT_CREATE_TASK_DEADLINE = "RESULT_CREATE_TASK_DEADLINE";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private TaskDbHelper mTaskDbHelper = new TaskDbHelper(this, "test", null/*default cursorfactory*/, 1/*version*/);

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private TaskRecyclerListFragment mTaskRecyclerListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        int stringId = 0;
        switch (position) {
            case 0:
                stringId = R.string.title_inbox;
                break;
            case 1:
                stringId = R.string.title_priority;
                break;
            case 2:
                stringId = R.string.title_willingness;
                break;
        }
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        mTaskRecyclerListFragment = TaskRecyclerListFragment.newInstance(stringId);
        fragmentManager.beginTransaction()
                .replace(R.id.container, mTaskRecyclerListFragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_inbox);
                break;
            case 2:
                mTitle = getString(R.string.title_priority);
                break;
            case 3:
                mTitle = getString(R.string.title_willingness);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_CREATE_TASK) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra(RESULT_CREATE_TASK_TITLE);
                mTaskRecyclerListFragment.onCreateTask(new TaskListItem(new Task(0, name, "", "", 0, 0)));

            }
        }
    }

    public TaskDbHelper getTaskDbHelper() {
        return mTaskDbHelper;
    }

    public void onTaskCreated(Task task) {
        mTaskDbHelper.insertTask(task);
    }

    public void onTaskPrioritySwapped(long id1, long id2) {
        mTaskDbHelper.swapTaskPriority(id1, id2);
    }

    public void onTaskWillingnessSwapped(long id1, long id2) {
        mTaskDbHelper.swapTaskWillingness(id1, id2);
    }

    public void onTaskRemoved(long id) {
        mTaskDbHelper.deleteTask(id);
    }

    public List<Task> loadTasksOrderedByPriority() {
        return mTaskDbHelper.getPriorityOrderedTasks();
    }

    public List<Task> loadTasksOrderedByWillingness() {
        return mTaskDbHelper.getWillingnessOrderedTasks();
    }

}
