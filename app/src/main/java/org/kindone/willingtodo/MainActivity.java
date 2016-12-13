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

import org.kindone.willingtodo.data.Task;
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
    public static String RESULT_CREATE_TASK_DEADLINE = "RESULT_CREATE_TASK_DEADLINE";

    private DbHelper mDbHelper = new DbHelper((Context) this, "test", null/*default cursorfactory*/, 1/*version*/);
    private TaskRecyclerListFragment mCurrentTaskListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        ViewPager viewPager = initViewPager();
        initLayout(viewPager);
        mCurrentTaskListFragment = (TaskRecyclerListFragment) ((ViewPagerAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private ViewPager initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < 3; i++) {
            int stringId = 0;
            switch (i) {
                case 0:
                    stringId = R.string.title_priority;
                    break;
                case 1:
                    stringId = R.string.title_willingness;
                    break;
                case 2:
                    stringId = R.string.title_awaiting;
                    break;
            }
            adapter.addFragment(TaskRecyclerListFragment.create(stringId), getResources().getString(stringId));
        }
        viewPager.setAdapter(adapter);
        return viewPager;
    }

    private void initLayout(final ViewPager viewPager) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
                mCurrentTaskListFragment = (TaskRecyclerListFragment) adapter.getItem(tab.getPosition());
                mCurrentTaskListFragment.refresh(mDbHelper.getVersion());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mCurrentTaskListFragment = null;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
                mCurrentTaskListFragment = (TaskRecyclerListFragment) adapter.getItem(tab.getPosition());
                mCurrentTaskListFragment.refresh(mDbHelper.getVersion());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_CREATE_TASK) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra(RESULT_CREATE_TASK_TITLE);
                mCurrentTaskListFragment.onCreateTask(new TaskListItem(new Task(0, name, "", "", 0, 0)));
            }
        }
    }

    public DbHelper getTaskDbHelper() {
        return mDbHelper;
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

    public List<Task> loadTasksOrderedByPriority() {
        return mDbHelper.getPriorityOrderedTasks();
    }

    public List<Task> loadTasksOrderedByWillingness() {
        return mDbHelper.getWillingnessOrderedTasks();
    }

    public int getVersion() {
        return mDbHelper.getVersion();
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


    }

}
