package org.kindone.willingtodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.kindone.willingtodo.helper.TaskDbHelperProvider;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements TaskDbHelperProvider,
        TaskRecyclerListFragment.TaskManipulationListener,
        TaskRecyclerListAdapter.TaskLoader {

    public static int INTENT_CREATE_TASK = 1;
    public static String RESULT_CREATE_TASK_TITLE = "RESULT_CREATE_TASK_TITLE";
    public static String RESULT_CREATE_TASK_DEADLINE = "RESULT_CREATE_TASK_DEADLINE";

    private TaskDbHelper mTaskDbHelper = new TaskDbHelper(this, "test", null/*default cursorfactory*/, 1/*version*/);

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TaskRecyclerListFragment mTaskRecyclerListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
                mTaskRecyclerListFragment = (TaskRecyclerListFragment) adapter.getItem(tab.getPosition());
                mTaskRecyclerListFragment.refresh(mTaskDbHelper.getVersion());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mTaskRecyclerListFragment = null;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
                mTaskRecyclerListFragment = (TaskRecyclerListFragment) adapter.getItem(tab.getPosition());
                mTaskRecyclerListFragment.refresh(mTaskDbHelper.getVersion());
            }
        });

        mTaskRecyclerListFragment = (TaskRecyclerListFragment) ((ViewPagerAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
    }

    private void setupViewPager(ViewPager viewPager) {
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

            adapter.addFragment(TaskRecyclerListFragment.newInstance(stringId), getResources().getString(stringId));
        }
        viewPager.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_CREATE_TASK) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra(RESULT_CREATE_TASK_TITLE);
                mTaskRecyclerListFragment.onCreateTask(new TaskListItem(new Task(0, name, "", "", 0, 0)));
                tabLayout.getSelectedTabPosition();

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

    public int getVersion() {
        return mTaskDbHelper.getVersion();
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
