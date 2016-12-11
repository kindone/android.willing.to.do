package org.kindone.willingtodo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import org.kindone.willingtodo.helper.TaskDbHelperProvider
import java.util.*


class MainActivity : AppCompatActivity(), TaskDbHelperProvider, TaskRecyclerListFragment.TaskManipulationListener, TaskRecyclerListAdapter.TaskLoader {

    private val TAG = "MainActivity"


    private val mDbHelper = DbHelper(this, "test", null/*default cursorfactory*/, DbHelper.LatestVersion)

    private var toolbar: Toolbar? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var mTaskRecyclerListFragment: TaskRecyclerListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        viewPager = findViewById(R.id.viewpager) as ViewPager

        setupViewPager(viewPager as ViewPager)

        tabLayout = findViewById(R.id.tabs) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager as ViewPager)

        tabLayout!!.setOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            override fun onTabSelected(tab: TabLayout.Tab) {
                super.onTabSelected(tab)
                val adapter = viewPager!!.adapter as ViewPagerAdapter
                mTaskRecyclerListFragment = adapter.getItem(tab.position) as TaskRecyclerListFragment
                mTaskRecyclerListFragment!!.refresh(mDbHelper.version)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                super.onTabUnselected(tab)
                mTaskRecyclerListFragment = null
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                super.onTabReselected(tab)
                val adapter = viewPager!!.adapter as ViewPagerAdapter
                mTaskRecyclerListFragment = adapter.getItem(tab!!.position) as TaskRecyclerListFragment
                mTaskRecyclerListFragment!!.refresh(mDbHelper.version)
            }
        })

        mTaskRecyclerListFragment = (viewPager!!.adapter as ViewPagerAdapter).getItem(viewPager!!.currentItem) as TaskRecyclerListFragment
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        for (i in 0..2) {
            var stringId = 0
            when (i) {
                0 -> stringId = R.string.title_priority
                1 -> stringId = R.string.title_willingness
                2 -> stringId = R.string.title_awaiting
            }

            adapter.addFragment(TaskRecyclerListFragment.newInstance(stringId), resources.getString(stringId))
        }
        viewPager.adapter = adapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == INTENT_CREATE_TASK) {
            if (resultCode == Activity.RESULT_OK) {
                val title = data.getStringExtra(RESULT_TASK_TITLE)
                val category = data.getStringExtra(RESULT_TASK_CATEGORY)
                mTaskRecyclerListFragment!!.onCreateTask(TaskListItem(Task(0, title, category, "", 0, 0)))

            }
        } else if (requestCode == INTENT_EDIT_TASK) {
            if (resultCode == Activity.RESULT_OK) {
                val position = data.getIntExtra(RESULT_TASK_POSITION, 0)
                val id = data.getLongExtra(RESULT_TASK_ID, 0)
                val title = data.getStringExtra(RESULT_TASK_TITLE)
                val category = data.getStringExtra(RESULT_TASK_CATEGORY)

                mTaskRecyclerListFragment!!.onEditTask(position, Task(id, title, category, "", 0, 0))
            }
        }
    }

    override fun getTaskDbHelper(): DbHelper {
        return mDbHelper
    }

    override fun onTaskCreated(task: Task) {
        mDbHelper.insertTask(task)
    }

    override fun onTaskPrioritySwapped(id1: Long, id2: Long) {
        mDbHelper.swapTaskPriority(id1, id2)
    }

    override fun onTaskWillingnessSwapped(id1: Long, id2: Long) {
        mDbHelper.swapTaskWillingness(id1, id2)
    }

    override fun onTaskRemoved(id: Long) {
        mDbHelper.deleteTask(id)
    }

    override fun onTaskUpdated(id: Long, task: Task) {
        mDbHelper.updateTask(id, task)
    }

    override fun loadTasksOrderedByPriority(): List<Task> {
        return mDbHelper.priorityOrderedTasks
    }

    override fun loadTasksOrderedByWillingness(): List<Task> {
        return mDbHelper.willingnessOrderedTasks
    }

    override fun getVersion(): Int {
        return mDbHelper.version
    }

    val isTimerActive: Boolean
        get() = mDbHelper.activeTimerEntry != null


    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    companion object {
        var INTENT_CREATE_TASK = 1
        var INTENT_EDIT_TASK = 2
        var INTENT_POMODORO = 3

        var RESULT_TASK_POSITION = "TASK_POS"
        var RESULT_TASK_ID = "TASK_ID"
        var RESULT_TASK_TITLE = "TASK_TITLE"
        var RESULT_TASK_CATEGORY = "TASK_CATEGORY"
    }

}
