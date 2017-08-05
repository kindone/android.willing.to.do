package org.kindone.willingtodo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import org.kindone.willingtodo.views.TabWithViewPager;
import org.kindone.willingtodo.data.Task;
import org.kindone.willingtodo.data.TaskContext;
import org.kindone.willingtodo.data.TaskListItem;
import org.kindone.willingtodo.persistence.ConfigPersistenceProvider;
import org.kindone.willingtodo.persistence.sqlite.SqliteHelper;
import org.kindone.willingtodo.persistence.PersistenceProvider;
import org.kindone.willingtodo.persistence.TaskContextPersistenceProvider;
import org.kindone.willingtodo.persistence.TaskPersistenceProvider;
import org.kindone.willingtodo.pomodorocontrol.PomodoroControlFragment;
import org.kindone.willingtodo.recyclerlist.RecyclerListItem;
import org.kindone.willingtodo.recyclerlist.task.TaskRecyclerListFragment;

import java.util.List;


public class MainActivity extends AppCompatActivity
        implements PersistenceProvider, PomodoroControlFragment.OnPomodoroControlListener {

    private static String TAG = "MainActivity";


    private static int mainLayoutResourceId = R.layout.activity_main;

    private static int mainBodyResourceId = R.id.main_body;

    private static int mainFooterResourceId = R.id.main_bottom;

    private static int sortByPriorityMenuResourceId = R.id.action_sort_by_priority;

    private static int sortByWillingnessMenuResourceId = R.id.action_sort_by_willingness;


    private static long defaultPomodoroDurationMs = 25*60*1000L;


    public static final int INTENT_CREATE_TASK = 1;

    public static final int INTENT_EDIT_TASK = 2;

    public static final int INTENT_MANAGE_CONTEXT = 3;


    public static String RESULT_TASK_TITLE = "RESULT_TASK_TITLE";

    public static String RESULT_TASK_ID = "RESULT_TASK_ID";

    public static String RESULT_CREATE_TASK_CONTEXT_ID = "RESULT_CREATE_CONTEXT_ID";

    public static String RESULT_CREATE_TASK_DEADLINE = "RESULT_CREATE_TASK_DEADLINE";

    public static String RESULT_CREATE_CONTEXT_TITLE = "RESULT_CREATE_CONTEXT_TITLE";


    public static String PREF_FILENAME = "mainActivity.pref";


    private static String STATE_TAB_POSITION = "UI.TAB_POSITION";



    private Menu mMenu;

    private TabWithViewPager mTabWithViewPager;

    PomodoroControlFragment mPomodoroControl;


    private SQLPersistenceProvider mPersistenceProvider = new SQLPersistenceProvider(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");

        if(!handleInvalidActivityCall())
            return;

        super.onCreate(savedInstanceState);

        initializeLayout();
        initializeToolbar();
        initializeTabWithViewPager(getSavedTabPosition(savedInstanceState));
        initializePomodoroControl();
    }


    @Override
    protected void onStart() {
        Log.v(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.v(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        saveState();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume");
        loadState();
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(TAG, "onCreateOptionsMenu");
        initializeMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG, "onOptionsItemSelected");
        int menuId = item.getItemId();

        switch(menuId)
        {
            case R.id.action_manage_contexts:
                startManageContextActivity();
                return true;
            case R.id.action_sort_by_priority:
                sortListByPriority();
                return true;
            case R.id.action_sort_by_willingness:
                sortListByWillingness();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.v(TAG, "onContextItemSelected");
        switch (item.getItemId()) {
            case R.id.action_pomodoro:
                startPomodoroTimer(item);
                break;
            case R.id.action_move_task:
                // TODO
                Log.v(TAG, "move task context item selected");
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "onActivityResult");

        if(isActivityResultOk(resultCode))
            processActivityResult(requestCode, data);
    }



    @Override
    public void onResumePomodoroTimer() {
        resumePomodoroTimerService();
    }

    @Override
    public void onPausePomodoroTimer(long remainingTimeMs) {
        pausePomodoroTimerService(remainingTimeMs);
    }

    @Override
    public void onStopPomodoroTimer() {
        stopPomodoroTimerService();
    }

    @Override
    public void onShowPomodoroControl() {
        showPomodoroMiniControl();
    }

    @Override
    public void onHidePomodoroControl() {
        hidePomodoroMiniControl();
    }

    @Override
    public void onClosePomodoroControl() {
        hidePomodoroMiniControl();
        stopPomodoroTimerService();
    }


    public void showPomodoroMiniControl()
    {
        View footer = (View)findViewById(mainFooterResourceId);
        footer.setVisibility(View.VISIBLE);
    }

    public void hidePomodoroMiniControl()
    {
        expandMainBody();
        View footer = (View)findViewById(R.id.main_bottom);
        footer.setVisibility(View.INVISIBLE);
    }




    private void initializeTabWithViewPager(int savedTabPosition) {
        ViewPager viewPager = getViewPager();
        TabLayout tabLayout = getTabLayout();

        ContextViewPagerAdapter viewPagerAdapter = new ContextViewPagerAdapter(getSupportFragmentManager());
        loadContextViewPagerAdapterContent(viewPagerAdapter);

        mTabWithViewPager = TabWithViewPager.FromResources(viewPager, tabLayout, viewPagerAdapter);
        mTabWithViewPager.setOnTabEventListener(new TabWithViewPagerTabEventListener());
        mTabWithViewPager.setCurrentItemIdx(savedTabPosition);
    }


    private void setTaskContextModeForCurrentTaskList(int mode)
    {
        TaskRecyclerListFragment currentTaskListFragment = (TaskRecyclerListFragment) mTabWithViewPager.getCurrentFragment();

        if(mode == SqliteHelper.MODE_PRIORITY)
            currentTaskListFragment.setPriorityOrdered(mPersistenceProvider.getTaskPersistenceProvider().getVersion());
        else if(mode == SqliteHelper.MODE_WILLINGNESS)
            currentTaskListFragment.setWillingnessOrdered(mPersistenceProvider.getTaskPersistenceProvider().getVersion());
    }

    private int getModeOfTaskContext(long taskContextId)
    {
        return mPersistenceProvider.getTaskContextPersistenceProvider().getModeOfTaskContext(taskContextId);
    }

    private void saveTabIdx(int idx)
    {
        mPersistenceProvider.getConfigPersistenceProvider().saveTabIndex(idx);
    }

    private int loadTabIdx()
    {
        return mPersistenceProvider.getConfigPersistenceProvider().getTabIndex();
    }


    private boolean isActivityResultOk(int resultCode)
    {
        return resultCode == RESULT_OK;
    }

    private void processActivityResult(int requestCode, Intent data)
    {
        switch(requestCode)
        {
            case INTENT_CREATE_TASK:
                createTask(data);
                break;
            case INTENT_EDIT_TASK:
                updateTask(data);
                break;
            default:
                Log.e(TAG, "processActivityResult: undefined requestCode = " + requestCode);
        }
    }



    private void loadContextViewPagerAdapterContent(ContextViewPagerAdapter adapter)
    {
        adapter.clear();
        List<TaskContext> taskContexts = mPersistenceProvider.getTaskContextPersistenceProvider().getTaskContexts();

        for (TaskContext taskContext : taskContexts) {
            adapter.addFragment(taskContext.id, TaskRecyclerListFragment.create(taskContext.id), taskContext.name);
        }
    }

    private int getSavedTabPosition(Bundle savedInstanceState)
    {
        return loadTabIdx();
    }

    private void loadFromSavedBundle()
    {
//        int savedTabPosition = 0;
//        if (savedInstanceState != null) {
//            savedTabPosition = savedInstanceState.getInt(STATE_TAB_POSITION);
//        }
//        return savedTabPosition;
    }

    private boolean handleInvalidActivityCall()
    {
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                Log.w(TAG, "Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return false;
            }
        }
        return true;
    }


    private void initializeLayout()
    {
        setContentView(mainLayoutResourceId);
    }

    private void initializeToolbar() {
        Toolbar toolbar = getToolbar();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        setMenu(menu);
    }

    private void initializePomodoroControl()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        setPomodoroControl(PomodoroControlFragment.create());
        fragmentTransaction.add(mainFooterResourceId, getPomodoroControl());
        fragmentTransaction.commit();
    }

    private void setMenu(Menu menu)
    {
        mMenu = menu;
    }

    private void setPomodoroControl(PomodoroControlFragment pomodoroControlFragment)
    {
        mPomodoroControl = pomodoroControlFragment;
    }

    private PomodoroControlFragment getPomodoroControl()
    {
        return mPomodoroControl;
    }



    private void sortListByPriority() {
        Log.i(TAG, "Mode Priority");
        saveTaskContextMode(getCurrentContextId(), SqliteHelper.MODE_PRIORITY);
        setPriorityAndWillingnessButton(SqliteHelper.MODE_PRIORITY);
        getCurrentTaskListFragment().setPriorityOrdered(mPersistenceProvider.getVersion());
    }

    private void sortListByWillingness() {
        Log.i(TAG, "Mode Willingness");
        saveTaskContextMode(getCurrentContextId(), SqliteHelper.MODE_WILLINGNESS);
        setPriorityAndWillingnessButton(SqliteHelper.MODE_WILLINGNESS);
        getCurrentTaskListFragment().setWillingnessOrdered(mPersistenceProvider.getVersion());
    }


    private void startManageContextActivity() {
        Intent intent = new Intent(this, ManageContextActivity.class);
        startActivityForResult(intent, INTENT_MANAGE_CONTEXT);
    }

    private void createTask(Intent data) {
        String title = data.getStringExtra(RESULT_TASK_TITLE);
        long contextId = getCurrentContextId();
        getCurrentTaskListFragment().onCreateItem(new TaskListItem(new Task(0, title, contextId, "", "", 0, 0)));
    }

    private Task getTaskFromResult(Intent data)
    {
        String title = data.getStringExtra(RESULT_TASK_TITLE);
        long contextId = getCurrentContextId();
        return new Task(0, title, contextId, "", "", 0, 0);
    }

    private void updateTask(Intent data) {
        long id = data.getLongExtra(RESULT_TASK_ID, -1);
        final String title = data.getStringExtra(RESULT_TASK_TITLE);
        long contextId = getCurrentContextId();
        if(id == -1L || title == null)
            throw new RuntimeException("Intent did not correctly include required parameter RESULT_TASK_ID");

        getCurrentTaskListFragment().onUpdateItem(id, new RecyclerListItem.Updater() {
            @Override
            public RecyclerListItem update(RecyclerListItem item) {
                Task task = ((TaskListItem)item).getTask();
                return new TaskListItem(new Task(task.id, title, task.contextId,
                        task.category, task.deadline, task.priority, task.willingness));
            }
        });
    }

    private void startPomodoroTimer(MenuItem item) {
        TaskRecyclerListFragment.TaskRecyclerViewContextMenuInfo rMenuInfo = (TaskRecyclerListFragment.TaskRecyclerViewContextMenuInfo) item.getMenuInfo();
        Log.v(TAG, "pomodoro context item selected itemId=" + rMenuInfo.getTask().id);

        startPomodoroTimerService(rMenuInfo.getTask());
        startPomodoroControl(rMenuInfo.getTask());
    }

    public void setPriorityAndWillingnessButton(int mode) {
        if(mode == SqliteHelper.MODE_PRIORITY) {
            toggleSortByPriorityMenu(false);
            toggleSortByWillingnessMenu(true);
        }
        else {
            toggleSortByPriorityMenu(true);
            toggleSortByWillingnessMenu(false);
        }
    }

    private void toggleSortByPriorityMenu(boolean enabled)
    {
        // FIXME: can be called when mMenu is not ready?
        if(mMenu == null)
            return;
        MenuItem item = mMenu.findItem(sortByPriorityMenuResourceId);
        item.setEnabled(enabled);
    }

    private void toggleSortByWillingnessMenu(boolean enabled)
    {
        // FIXME: can be called when mMenu is not ready?
        if(mMenu == null)
            return;
        MenuItem item = mMenu.findItem(sortByWillingnessMenuResourceId);
        item.setEnabled(enabled);
    }


    private void expandMainBody()
    {
        // FIXME: is this needed? what about the counterpart? (showing footer)
        View body = (View)findViewById(mainBodyResourceId);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 0, 0);
        body.setLayoutParams(lp);
    }


    private void startPomodoroControl(Task task) {
        showPomodoroMiniControl();
        mPomodoroControl.startTimer(task,  defaultPomodoroDurationMs);
    }

    private void startPomodoroTimerService(Task task) {
        Intent intent = createIntentForPomodoroTimerServiceStart(task);
        startService(intent);
    }

    private void resumePomodoroTimerService() {
        Intent intent = createIntentForPomodoroTimerServiceResume();
        startService(intent);
    }

    private void pausePomodoroTimerService(long remainingTimeMs) {
        Intent intent = createIntentForPomodoroTimerServicePause(remainingTimeMs);
        startService(intent);
    }

    private void stopPomodoroTimerService() {
        Intent intent = createIntentForPomodoroTimerServiceStop();
        startService(intent);
    }


    private Intent createIntentForPomodoroTimerServiceStart(Task task)
    {
        Intent intent = new Intent(this, PomodoroTimerService.class);
        intent.setAction(PomodoroTimerService.ACTION_START);
        intent.putExtra(PomodoroTimerService.ARG_TASK_ID, task.id);
        intent.putExtra(PomodoroTimerService.ARG_TASK_TITLE, task.title);
        intent.putExtra(PomodoroTimerService.ARG_TASK_DURATION_MS, defaultPomodoroDurationMs);
        return intent;
    }

    private Intent createIntentForPomodoroTimerServiceResume()
    {
        Intent intent = new Intent(this, PomodoroTimerService.class);
        intent.setAction(PomodoroTimerService.ACTION_RESUME);
        return intent;
    }

    private Intent createIntentForPomodoroTimerServicePause(long remainingTimeMs)
    {
        Intent intent = new Intent(this, PomodoroTimerService.class);
        intent.setAction(PomodoroTimerService.ACTION_PAUSE);
        intent.putExtra(PomodoroTimerService.ARG_TASK_REMAINING_TIME_MS, remainingTimeMs);
        return intent;
    }

    private Intent createIntentForPomodoroTimerServiceStop()
    {
        Intent intent = new Intent(this, PomodoroTimerService.class);
        intent.setAction(PomodoroTimerService.ACTION_STOP);
        return intent;
    }

    private void saveState() {
        SharedPreferences.Editor prefs = getSharedPreferences(PREF_FILENAME, MODE_PRIVATE).edit();
        writeTabPosition(prefs);
        prefs.commit();
    }

    private void loadState() {
        SharedPreferences prefs = getSharedPreferences(PREF_FILENAME, MODE_PRIVATE);
        setTabPosition(readTabPosition(prefs));
    }

    private void writeTabPosition(SharedPreferences.Editor prefs)
    {
        if(mTabWithViewPager != null)
            prefs.putInt(STATE_TAB_POSITION, mTabWithViewPager.getCurrentItemIdx());
    }

    private int readTabPosition(SharedPreferences prefs)
    {
        return prefs.getInt(STATE_TAB_POSITION, 0);
    }


    private TaskRecyclerListFragment getCurrentTaskListFragment()
    {
        return (TaskRecyclerListFragment) mTabWithViewPager.getCurrentFragment();
    }

    public long getCurrentContextId() {
        ContextViewPagerAdapter adapter = (ContextViewPagerAdapter) mTabWithViewPager.getAdapter();
        return adapter.getContextId(mTabWithViewPager.getCurrentItemIdx());
    }


    private void saveTaskContextMode(long taskContextId, int mode)
    {
        mPersistenceProvider.getTaskContextPersistenceProvider().setModeOfTaskContext(taskContextId, mode);
    }

    private int getTaskContextMode(long taskContextId)
    {
        return mPersistenceProvider.getTaskContextPersistenceProvider().getModeOfTaskContext(taskContextId);
    }




    private TabLayout getTabLayout()
    {
        return (TabLayout) findViewById(R.id.tabs);
    }

    private Toolbar getToolbar()
    {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    private ViewPager getViewPager()
    {
        return (ViewPager) findViewById(R.id.viewpager);
    }

    private void setTabPosition(int tabPosition)
    {
        mTabWithViewPager.setCurrentItemIdx(tabPosition);
    }

    class TabWithViewPagerTabEventListener implements TabLayout.OnTabSelectedListener
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            ContextViewPagerAdapter viewPagerAdapter = (ContextViewPagerAdapter) mTabWithViewPager.getAdapter();
            long taskContextId = viewPagerAdapter.getContextId(tab.getPosition());
            int mode = getModeOfTaskContext(taskContextId);
            setPriorityAndWillingnessButton(mode);
            setTaskContextModeForCurrentTaskList(mode);
            saveTabIdx(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {   }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            onTabSelected(tab);
        }
    }


    @Override
    public TaskContextPersistenceProvider getTaskContextPersistenceProvider() {
        return mPersistenceProvider.getTaskContextPersistenceProvider();
    }

    @Override
    public TaskPersistenceProvider getTaskPersistenceProvider() {
        return mPersistenceProvider.getTaskPersistenceProvider();
    }

    @Override
    public ConfigPersistenceProvider getConfigPersistenceProvider() {
        return mPersistenceProvider.getConfigPersistenceProvider();
    }

    @Override
    public int getVersion() {
        return mPersistenceProvider.getVersion();
    }
}
