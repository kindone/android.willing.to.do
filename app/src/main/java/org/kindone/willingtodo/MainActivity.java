package org.kindone.willingtodo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.kindone.willingtodo.data.Task;
import org.kindone.willingtodo.data.TaskContext;
import org.kindone.willingtodo.data.TaskListItem;
import org.kindone.willingtodo.persistence.DbHelper;
import org.kindone.willingtodo.taskrecyclerlist.TaskChangeListener;
import org.kindone.willingtodo.taskrecyclerlist.TaskProvider;
import org.kindone.willingtodo.taskrecyclerlist.TaskRecyclerListFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements TaskChangeListener, TaskProvider {

    public static int INTENT_CREATE_TASK = 1;
    public static String RESULT_CREATE_TASK_TITLE = "RESULT_CREATE_TASK_TITLE";
    public static String RESULT_CREATE_TASK_CONTEXT_ID = "RESULT_CREATE_CONTEXT_ID";
    public static String RESULT_CREATE_TASK_DEADLINE = "RESULT_CREATE_TASK_DEADLINE";

    private DbHelper mDbHelper = new DbHelper((Context) this, "test", null/*default cursorfactory*/, 1/*version*/);
    private TaskRecyclerListFragment mCurrentTaskListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        ViewPager viewPager = initViewPager();
        initTabLayout(viewPager);
        mCurrentTaskListFragment = (TaskRecyclerListFragment) ((ContextViewPagerAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private ViewPager initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ContextViewPagerAdapter adapter = (ContextViewPagerAdapter) viewPager.getAdapter();
        if(adapter == null)
            adapter = new ContextViewPagerAdapter(getSupportFragmentManager());

        adapter.clear();

        List<TaskContext> taskContexts = mDbHelper.getTaskContexts();

        for (TaskContext taskContext : taskContexts) {
            adapter.addFragment(taskContext.id, TaskRecyclerListFragment.create(taskContext.id), taskContext.name);
        }

        viewPager.setAdapter(adapter);
        return viewPager;
    }

    private void initTabLayout(final ViewPager viewPager) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                ContextViewPagerAdapter adapter = (ContextViewPagerAdapter) viewPager.getAdapter();
                mCurrentTaskListFragment = (TaskRecyclerListFragment) adapter.getItem(tab.getPosition());
                mCurrentTaskListFragment.refresh(mDbHelper.getVersion());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mCurrentTaskListFragment = null;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                ContextViewPagerAdapter adapter = (ContextViewPagerAdapter) viewPager.getAdapter();
                mCurrentTaskListFragment = (TaskRecyclerListFragment) adapter.getItem(tab.getPosition());
                mCurrentTaskListFragment.refresh(mDbHelper.getVersion());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_manage_contexts) {
            // TODO: start 'manage contexts' activity
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
                long contextId = getCurrentContextId();
                mCurrentTaskListFragment.onCreateTask(new TaskListItem(new Task(0, name, contextId, "", "", 0, 0)));
            }
        }
    }

    public long getCurrentContextId() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ContextViewPagerAdapter adapter = (ContextViewPagerAdapter) viewPager.getAdapter();
        return adapter.getContextId(viewPager.getCurrentItem());
    }

    public void onTaskCreated(Task task) {
        mDbHelper.insertTask(task);
    }

    public void onTaskPrioritySwapped(long id1, long id2) {
        mDbHelper.swapTaskPriority(id1, id2);
    }

    public void onTaskWillingnessSwapped(long id1, long id2) {
        mDbHelper.swapTaskWillingness(id1, id2);
    }

    public void onTaskRemoved(long id) {
        mDbHelper.deleteTask(id);
    }

    public List<Task> loadTasksOrderedByPriority(long contextId) {
        return mDbHelper.getPriorityOrderedTasks(contextId);
    }

    public List<Task> loadTasksOrderedByWillingness(long contextId) {
        return mDbHelper.getWillingnessOrderedTasks(contextId);
    }

    public int getMode(long contextId) {
        return mDbHelper.getContextMode(contextId);
    }

    public void setMode(long contextId, int mode) {
        mDbHelper.setContextMode(contextId, mode);
    }

    public int getVersion() {
        return mDbHelper.getVersion();
    }


    class ContextViewPagerAdapter extends FragmentPagerAdapter {
        class AdapterElement {
            public final long contextId;
            public final Fragment fragment;
            public final String title;
            AdapterElement(long contextId, Fragment fragment, String title) {
                this.contextId = contextId;
                this.fragment = fragment;
                this.title = title;
            }
        }
        private final List<AdapterElement> list = new ArrayList<>();

        public ContextViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position).fragment;
        }

        public String getTitle(int position) { return list.get(position).title; }

        public long getContextId(int position) { return list.get(position).contextId; }

        @Override
        public int getCount() {
            return list.size();
        }

        public void clear() {
            list.clear();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position).title;
        }

        public void addFragment(long contextId, Fragment fragment, String title) {
            list.add(new AdapterElement(contextId, fragment, title));
        }
    }

}
