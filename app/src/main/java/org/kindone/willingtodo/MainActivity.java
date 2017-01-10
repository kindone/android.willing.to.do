package org.kindone.willingtodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import org.kindone.willingtodo.data.Task;
import org.kindone.willingtodo.data.TaskContext;
import org.kindone.willingtodo.data.TaskListItem;
import org.kindone.willingtodo.persistence.ConfigProvider;
import org.kindone.willingtodo.persistence.SqliteHelper;
import org.kindone.willingtodo.persistence.PersistenceProvider;
import org.kindone.willingtodo.persistence.TaskContextProvider;
import org.kindone.willingtodo.persistence.TaskProvider;
import org.kindone.willingtodo.pomodorocontrol.PomodoroControlFragment;
import org.kindone.willingtodo.pomodorotimer.PomodoroTimerService;
import org.kindone.willingtodo.recyclerlist.RecyclerListItem;
import org.kindone.willingtodo.recyclerlist.task.TaskRecyclerListFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements PersistenceProvider {

    public static String TAG = "MainActivity";
    public static int INTENT_CREATE_TASK = 1;
    public static int INTENT_EDIT_TASK = 2;
    public static int INTENT_MANAGE_CONTEXT = 3;
    public static String RESULT_TASK_TITLE = "RESULT_TASK_TITLE";
    public static String RESULT_TASK_ID = "RESULT_TASK_ID";
    public static String RESULT_CREATE_TASK_CONTEXT_ID = "RESULT_CREATE_CONTEXT_ID";
    public static String RESULT_CREATE_TASK_DEADLINE = "RESULT_CREATE_TASK_DEADLINE";
    public static String RESULT_CREATE_CONTEXT_TITLE = "RESULT_CREATE_CONTEXT_TITLE";


    private TaskRecyclerListFragment mCurrentTaskListFragment;
    private Menu mMenu;
    private ViewPager mViewPager;
    private SQLPersistenceProvider mPersistenceProvider = new SQLPersistenceProvider(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        mViewPager = initViewPager();
        initTabLayout(mViewPager);

        initPomodoroControl();
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

        List<TaskContext> taskContexts = mPersistenceProvider.getContextProvider().getTaskContexts();

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
                Log.v(TAG, "tab selected:" +  tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
                ContextViewPagerAdapter adapter = (ContextViewPagerAdapter) viewPager.getAdapter();
                mCurrentTaskListFragment = (TaskRecyclerListFragment) adapter.getItem(tab.getPosition());
                mCurrentTaskListFragment.refresh(mPersistenceProvider.getTaskProvider().getVersion());
                // FIXME: better solution?
                int mode = mPersistenceProvider.getContextProvider().getMode(adapter.getContextId(tab.getPosition()));
                setPriorityAndWillingnessButton(mode);
                if(mode == SqliteHelper.MODE_PRIORITY)
                    mCurrentTaskListFragment.setPriorityOrdered();
                else if(mode == SqliteHelper.MODE_WILLINGNESS)
                    mCurrentTaskListFragment.setWillingnessOrdered();
                mPersistenceProvider.getConfigProvider().saveTabIndex(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mCurrentTaskListFragment = null;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
                mPersistenceProvider.getConfigProvider().saveTabIndex(tab.getPosition());
            }
        });
    }

    private void initPomodoroControl()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PomodoroControlFragment pomodoro = PomodoroControlFragment.create();
        fragmentTransaction.add(R.id.main_bottom, pomodoro);
        fragmentTransaction.commit();

    }

    public void showPomodoroMiniControl()
    {
        View body = (View)findViewById(R.id.main_body);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        float height = getResources().getDimension(R.dimen.pomodoro_mini_control_height);
        lp.setMargins(0, 0, 0, (int)height);
        body.setLayoutParams(lp);

        View footer = (View)findViewById(R.id.main_bottom);
        footer.setVisibility(View.VISIBLE);
    }

    public void hidePomodoroMiniControl()
    {
        View body = (View)findViewById(R.id.main_body);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 0, 0);
        body.setLayoutParams(lp);

        View footer = (View)findViewById(R.id.main_bottom);
        footer.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onResume() {
        Log.v(TAG, "resumed: "+ mPersistenceProvider.getConfigProvider().getTabIndex());
        mViewPager.setCurrentItem(mPersistenceProvider.getConfigProvider().getTabIndex());
        mCurrentTaskListFragment = (TaskRecyclerListFragment) ((ContextViewPagerAdapter) mViewPager.getAdapter()).getItem(mViewPager.getCurrentItem());
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_manage_contexts) {
            Intent intent = new Intent(this, ManageContextActivity.class);
            startActivityForResult(intent, INTENT_MANAGE_CONTEXT);
            return true;
        }
        else if(id == R.id.action_sort_by_priority) {
            Log.i(TAG, "Mode Priority");
            mPersistenceProvider.getContextProvider().setMode(getCurrentContextId(), SqliteHelper.MODE_PRIORITY);
            mCurrentTaskListFragment.refresh(mPersistenceProvider.getVersion());
            setPriorityAndWillingnessButton(SqliteHelper.MODE_PRIORITY);
            mCurrentTaskListFragment.setPriorityOrdered();
            return true;
        }
        else if(id == R.id.action_sort_by_willingness) {
            Log.i(TAG, "Mode Willingness");
            mPersistenceProvider.getContextProvider().setMode(getCurrentContextId(), SqliteHelper.MODE_WILLINGNESS);
            mCurrentTaskListFragment.refresh(mPersistenceProvider.getVersion());
            setPriorityAndWillingnessButton(SqliteHelper.MODE_WILLINGNESS);
            mCurrentTaskListFragment.setWillingnessOrdered();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setPriorityAndWillingnessButton(int mode) {
        if(mMenu == null)
            return;
        MenuItem pItem = mMenu.findItem(R.id.action_sort_by_priority);
        MenuItem wItem = mMenu.findItem(R.id.action_sort_by_willingness);
        if(mode == SqliteHelper.MODE_PRIORITY) {
            pItem.setVisible(false);
            wItem.setVisible(true);
        }
        else {
            pItem.setVisible(true);
            wItem.setVisible(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_CREATE_TASK) {
            if (resultCode == RESULT_OK) {
                String title = data.getStringExtra(RESULT_TASK_TITLE);
                long contextId = getCurrentContextId();
                mCurrentTaskListFragment.onCreateItem(new TaskListItem(new Task(0, title, contextId, "", "", 0, 0)));
            }
        }
        else if(requestCode == INTENT_EDIT_TASK) {
            if (resultCode == RESULT_OK) {
                long id = data.getLongExtra(RESULT_TASK_ID, -1);
                final String title = data.getStringExtra(RESULT_TASK_TITLE);
                long contextId = getCurrentContextId();
                if(id == -1L || title == null)
                    throw new RuntimeException("Intent did not correctly include required parameter RESULT_TASK_ID");

                mCurrentTaskListFragment.onUpdateItem(id, new RecyclerListItem.Updater() {
                    @Override
                    public RecyclerListItem update(RecyclerListItem item) {
                        Task task = ((TaskListItem)item).getTask();
                        return new TaskListItem(new Task(task.id, title, task.contextId,
                                task.category, task.deadline, task.priority, task.willingness));
                    }
                });
            }
        }

    }

    public long getCurrentContextId() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ContextViewPagerAdapter adapter = (ContextViewPagerAdapter) viewPager.getAdapter();
        return adapter.getContextId(viewPager.getCurrentItem());
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

    @Override
    public TaskContextProvider getContextProvider() {
        return mPersistenceProvider.getContextProvider();
    }

    @Override
    public TaskProvider getTaskProvider() {
        return mPersistenceProvider.getTaskProvider();
    }

    @Override
    public ConfigProvider getConfigProvider() {
        return mPersistenceProvider.getConfigProvider();
    }

    @Override
    public int getVersion() {
        return mPersistenceProvider.getVersion();
    }

}
