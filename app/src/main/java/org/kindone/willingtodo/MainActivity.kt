package org.kindone.willingtodo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout

import org.kindone.willingtodo.views.TabWithViewPager
import org.kindone.willingtodo.data.Task
import org.kindone.willingtodo.data.TaskListItem
import org.kindone.willingtodo.persistence.ConfigPersistenceProvider
import org.kindone.willingtodo.persistence.sqlite.SqliteHelper
import org.kindone.willingtodo.persistence.PersistenceProvider
import org.kindone.willingtodo.persistence.TaskContextPersistenceProvider
import org.kindone.willingtodo.persistence.TaskPersistenceProvider
import org.kindone.willingtodo.pomodorocontrol.PomodoroControlFragment
import org.kindone.willingtodo.recyclerlist.RecyclerListItem
import org.kindone.willingtodo.recyclerlist.task.TaskRecyclerListFragment


class MainActivity : AppCompatActivity(), PersistenceProvider, PomodoroControlFragment.OnPomodoroControlListener {


    private var mMenu: Menu? = null

    private var mTabWithViewPager: TabWithViewPager? = null

    private var pomodoroControlFragment: PomodoroControlFragment? = null


    private val mPersistenceProvider:SQLPersistenceProvider by lazy {
        SQLPersistenceProvider(this)
    }

    private val mPomodoroServiceStarter = PomodoroServiceStarter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v(TAG, "onCreate")

        if (!handleInvalidActivityCall())
            return

        super.onCreate(savedInstanceState)

        initializeLayout()
        initializeToolbar()
        initializeTabWithViewPager()
        initializePomodoroControl()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.v(TAG, "onCreateOptionsMenu")
        initializeMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.v(TAG, "onOptionsItemSelected")
        val menuId = item.itemId

        when (menuId) {
            R.id.action_manage_contexts -> {
                startManageContextActivity()
                return true
            }
            R.id.action_sort_by_priority -> {
                sortListByPriority()
                return true
            }
            R.id.action_sort_by_willingness -> {
                sortListByWillingness()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // item context menu
    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.v(TAG, "onContextItemSelected")
        when (item.itemId) {
            R.id.action_pomodoro -> startPomodoroTimerForItem(item)
            R.id.action_move_task ->
                // TODO
                Log.v(TAG, "move task context item selected")
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultIntent)

        Log.v(TAG, "onActivityResult")

        if(resultIntent != null && isActivityResultOk(resultCode))
            processActivityResult(requestCode, resultIntent!!)
    }


    override fun onStart() {
        Log.v(TAG, "onStart")
        super.onStart()
    }

    override fun onRestart() {
        Log.v(TAG, "onRestart")
        super.onRestart()
    }

    override fun onStop() {
        Log.v(TAG, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.v(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onPause() {
        Log.v(TAG, "onPause")

        saveState()

        super.onPause()
    }

    override fun onResume() {
        Log.v(TAG, "onResume")

        loadState()

//        setTabPosition(loadTabIdx())

        super.onResume()
    }


    /** --> interface for OnPomodoroControlListener **/

    override fun onResumePomodoroTimer() {
        mPomodoroServiceStarter.resumePomodoroTimerService()
    }

    override fun onPausePomodoroTimer(remainingTimeMs: Long) {
        mPomodoroServiceStarter.pausePomodoroTimerService(remainingTimeMs)
    }

    override fun onStopPomodoroTimer() {
        mPomodoroServiceStarter.stopPomodoroTimerService()
    }

    override fun onClosePomodoroControl() {
        hidePomodoroMiniControl()
        mPomodoroServiceStarter.stopPomodoroTimerService()
    }

    /** <-- OnPomodoroControlListener **/


    override fun onShowPomodoroControl() {
        showPomodoroMiniControl()
    }

    override fun onHidePomodoroControl() {
        hidePomodoroMiniControl()
    }

    private fun showPomodoroMiniControl() {
        findViewById(mainFooterResourceId)!!.visibility = View.VISIBLE
    }

    private fun hidePomodoroMiniControl() {
        expandMainBody()
        findViewById(R.id.main_bottom)!!.visibility = View.INVISIBLE
    }


    private fun setTaskContextModeForCurrentTaskList(mode: Int) {
        val currentTaskListFragment = mTabWithViewPager!!.currentFragment as TaskRecyclerListFragment

        if (mode == SqliteHelper.MODE_PRIORITY)
            currentTaskListFragment.setPriorityOrdered(mPersistenceProvider.taskPersistenceProvider.version)
        else if (mode == SqliteHelper.MODE_WILLINGNESS)
            currentTaskListFragment.setWillingnessOrdered(mPersistenceProvider.taskPersistenceProvider.version)
    }


    private fun saveTabIdx(idx: Int) {
        mPersistenceProvider.configPersistenceProvider.saveTabIndex(idx)
    }

    private fun loadTabIdx(): Int {
        return mPersistenceProvider.configPersistenceProvider.tabIndex
    }


    private fun processActivityResult(requestCode: Int, intent: Intent) {
        when (requestCode) {
            INTENT_CREATE_TASK -> processCreateTaskResult(intent)
            INTENT_EDIT_TASK -> processUpdateTaskResult(intent)
            else -> Log.e(TAG, "processActivityResult: undefined requestCode = " + requestCode)
        }
    }


    private fun handleInvalidActivityCall(): Boolean {
        if (!isTaskRoot) {
            val intent = intent
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN == intent.action) {
                Log.w(TAG, "Main Activity is not the root.  Finishing Main Activity instead of launching.")
                finish()
                return false
            }
        }
        return true
    }


    /** --> Initialization **/

    private fun initializeLayout() {
        setContentView(mainLayoutResourceId)
    }

    private fun initializeToolbar() {
        val toolbar = toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initializeMenu(menu: Menu) {
        menuInflater.inflate(R.menu.main, menu)
        setMenu(menu)
    }

    private fun initializePomodoroControl() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        pomodoroControlFragment = PomodoroControlFragment.create()
        fragmentTransaction.add(mainFooterResourceId, pomodoroControlFragment)
        fragmentTransaction.commit()
    }

    private fun initializeTabWithViewPager() {
        val viewPager = viewPager
        val tabLayout = tabLayout

        val viewPagerAdapter = ContextViewPagerAdapter(supportFragmentManager)
        loadContextViewPagerAdapterContent(viewPagerAdapter)

        mTabWithViewPager = TabWithViewPager.FromResources(viewPager, tabLayout, viewPagerAdapter)
        mTabWithViewPager!!.setOnTabEventListener(TabWithViewPagerTabEventListener())

        setTabPosition(loadTabIdx())
    }

    private fun loadContextViewPagerAdapterContent(adapter: ContextViewPagerAdapter) {
        adapter.clear()
        val taskContexts = mPersistenceProvider.taskContextPersistenceProvider.taskContexts

        for (taskContext in taskContexts) {
            adapter.addFragment(taskContext.id, TaskRecyclerListFragment.create(taskContext.id), taskContext.name)
        }
    }

    /** <-- Initialization **/




    private fun sortListByPriority() {
        Log.i(TAG, "Mode Priority")
        saveTaskContextMode(getCurrentContextId(), SqliteHelper.MODE_PRIORITY)
        setPriorityAndWillingnessButton(SqliteHelper.MODE_PRIORITY)
        currentTaskListFragment.setPriorityOrdered(mPersistenceProvider.getVersion())
    }

    private fun sortListByWillingness() {
        Log.i(TAG, "Mode Willingness")
        saveTaskContextMode(getCurrentContextId(), SqliteHelper.MODE_WILLINGNESS)
        setPriorityAndWillingnessButton(SqliteHelper.MODE_WILLINGNESS)
        currentTaskListFragment.setWillingnessOrdered(mPersistenceProvider.getVersion())
    }


    private fun setPriorityAndWillingnessButton(mode: Int) {
        if (mode == SqliteHelper.MODE_PRIORITY) {
            toggleSortByPriorityMenu(false)
            toggleSortByWillingnessMenu(true)
        } else {
            toggleSortByPriorityMenu(true)
            toggleSortByWillingnessMenu(false)
        }
    }

    private fun toggleSortByPriorityMenu(enabled: Boolean) {
        // FIXME: can be called when mMenu is not ready?
        if (mMenu == null)
            return
        val item = mMenu!!.findItem(sortByPriorityMenuResourceId)
        item.isEnabled = enabled
    }

    private fun toggleSortByWillingnessMenu(enabled: Boolean) {
        // FIXME: can be called when mMenu is not ready?
        if (mMenu == null)
            return
        val item = mMenu!!.findItem(sortByWillingnessMenuResourceId)
        item.isEnabled = enabled
    }



    private fun processCreateTaskResult(resultIntent: Intent) {
        val title = resultIntent.getStringExtra(RESULT_TASK_TITLE)
        val contextId = getCurrentContextId()

        currentTaskListFragment.createItem(TaskListItem(Task(title, contextId)))
    }

    private fun processUpdateTaskResult(resultIntent: Intent) {
        val id = resultIntent.getLongExtra(RESULT_TASK_ID, -1)
        val title = resultIntent.getStringExtra(RESULT_TASK_TITLE)

        if (id == -1L || title == null)
            throw RuntimeException("Intent did not correctly include required parameter RESULT_TASK_ID")

        val titleUpdater = object : RecyclerListItem.Updater {
            override fun update(item: RecyclerListItem): RecyclerListItem {
                val task = (item as TaskListItem).task
                return TaskListItem(Task(task.id, title, task.contextId,
                        task.category, task.deadline, task.priority, task.willingness))
            }
        }

        currentTaskListFragment.updateItem(id, titleUpdater)
    }



    private fun startManageContextActivity() {
        val intent = Intent(this, ManageContextActivity::class.java)
        startActivityForResult(intent, INTENT_MANAGE_CONTEXT)
    }

    private fun startPomodoroTimerForItem(item: MenuItem) {
        val rMenuInfo = item.menuInfo as TaskRecyclerListFragment.TaskRecyclerViewContextMenuInfo
        Log.v(TAG, "pomodoro context item selected itemId=" + rMenuInfo.task!!.id)

        mPomodoroServiceStarter.startPomodoroTimerService(rMenuInfo.task!!)
        startPomodoroControl(rMenuInfo.task!!)
    }

    private fun startPomodoroControl(task: Task) {
        showPomodoroMiniControl()
        pomodoroControlFragment!!.startTimer(task, defaultPomodoroDurationMs)
    }


    private fun saveState() {
        val prefs = getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE).edit()
        writeTabPosition(prefs)
        prefs.commit()
    }

    private fun loadState() {
        val prefs = getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE)
        setTabPosition(readTabPosition(prefs))
    }



    private fun writeTabPosition(prefs: SharedPreferences.Editor) {
        if (mTabWithViewPager != null)
            prefs.putInt(STATE_TAB_POSITION, mTabWithViewPager!!.currentItemIdx)
    }

    private fun readTabPosition(prefs: SharedPreferences): Int {
        return prefs.getInt(STATE_TAB_POSITION, 0)
    }


    private fun saveTaskContextMode(taskContextId: Long, mode: Int) {
        mPersistenceProvider.taskContextPersistenceProvider.setModeOfTaskContext(taskContextId, mode)
    }

    private fun getTaskContextMode(taskContextId: Long): Int {
        return mPersistenceProvider.taskContextPersistenceProvider.getModeOfTaskContext(taskContextId)
    }



    private fun expandMainBody() {
        // FIXME: is this needed? what about the counterpart? (showing footer)
        val lp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT)
        lp.setMargins(0, 0, 0, 0)
        findViewById(mainBodyResourceId)!!.layoutParams = lp
    }


    private fun setTabPosition(tabPosition: Int) {
        mTabWithViewPager!!.currentItemIdx = tabPosition
    }

    private inner class TabWithViewPagerTabEventListener : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            val viewPagerAdapter = mTabWithViewPager!!.adapter as ContextViewPagerAdapter
            val taskContextId = viewPagerAdapter.getContextId(tab.position)
            updateModeChange(getTaskContextMode(taskContextId))
            saveTabIdx(tab.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}

        override fun onTabReselected(tab: TabLayout.Tab) {
            onTabSelected(tab)
        }
    }

    private fun updateModeChange(mode:Int) {
        setPriorityAndWillingnessButton(mode)
        setTaskContextModeForCurrentTaskList(mode)
    }


    /** View Accessor **/

    private val tabLayout: TabLayout
        get() = findViewById(R.id.tabs) as TabLayout

    private val toolbar: Toolbar
        get() = findViewById(R.id.toolbar) as Toolbar

    private val viewPager: ViewPager
        get() = findViewById(R.id.viewpager) as ViewPager

    private val currentTaskListFragment: TaskRecyclerListFragment
        get() = mTabWithViewPager!!.currentFragment as TaskRecyclerListFragment



    /** Persistence Provider **/

    override val taskContextPersistenceProvider: TaskContextPersistenceProvider
        get() = mPersistenceProvider.taskContextPersistenceProvider

    override val taskPersistenceProvider: TaskPersistenceProvider
        get() = mPersistenceProvider.taskPersistenceProvider

    override val configPersistenceProvider: ConfigPersistenceProvider
        get() = mPersistenceProvider.configPersistenceProvider

    override fun getVersion(): Int {
        return mPersistenceProvider.getVersion()
    }


    /** Miscellaneous **/

    private fun getCurrentContextId(): Long
    {
        val adapter = mTabWithViewPager!!.adapter as ContextViewPagerAdapter
        return adapter.getContextId(mTabWithViewPager!!.currentItemIdx)
    }

    private fun setMenu(menu: Menu) {
        mMenu = menu
    }

    private fun isActivityResultOk(resultCode: Int): Boolean {
        return resultCode == RESULT_OK
    }

    companion object {

        private val TAG = "MainActivity"


        private val mainLayoutResourceId = R.layout.activity_main

        private val mainBodyResourceId = R.id.main_body

        private val mainFooterResourceId = R.id.main_bottom

        private val sortByPriorityMenuResourceId = R.id.action_sort_by_priority

        private val sortByWillingnessMenuResourceId = R.id.action_sort_by_willingness


        val defaultPomodoroDurationMs = 25 * 60 * 1000L


        val INTENT_CREATE_TASK = 1

        val INTENT_EDIT_TASK = 2

        val INTENT_MANAGE_CONTEXT = 3


        var RESULT_TASK_TITLE = "RESULT_TASK_TITLE"

        var RESULT_TASK_ID = "RESULT_TASK_ID"

        var RESULT_CREATE_TASK_CONTEXT_ID = "RESULT_CREATE_CONTEXT_ID"

        var RESULT_CREATE_TASK_DEADLINE = "RESULT_CREATE_TASK_DEADLINE"

        var RESULT_CREATE_CONTEXT_TITLE = "RESULT_CREATE_CONTEXT_TITLE"


        var PREF_FILENAME = "mainActivity.pref"


        private val STATE_TAB_POSITION = "UI.TAB_POSITION"
    }
}
