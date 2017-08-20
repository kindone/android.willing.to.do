package org.kindone.willingtodo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

import org.kindone.willingtodo.data.TaskContextListItem
import org.kindone.willingtodo.data.TaskContext
import org.kindone.willingtodo.persistence.ConfigPersistenceProvider
import org.kindone.willingtodo.persistence.PersistenceProvider
import org.kindone.willingtodo.persistence.TaskContextPersistenceProvider
import org.kindone.willingtodo.persistence.TaskPersistenceProvider
import org.kindone.willingtodo.recyclerlist.context.TaskContextRecyclerListFragment
import org.kindone.willingtodo.recyclerlist.RecyclerListItem


class ManageTaskContextActivity : AppCompatActivity(), PersistenceProvider {

    private var mRecyclerListFragment: TaskContextRecyclerListFragment? = null
    private val mPersistenceProvider = SQLPersistenceProvider(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeLayout()
        initializeToolbar()
        initializeListFragment()
        initializeListFragmentEventListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent) {
        super.onActivityResult(requestCode, resultCode, resultIntent)

        if (requestCode == INTENT_CREATE_TASK_CONTEXT && resultCode == RESULT_OK) {
            processCreateTaskContextResult(resultIntent)
        }
    }


    private fun initializeLayout()
    {
        setContentView(R.layout.activity_manage_context)
    }

    private fun initializeToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initializeListFragment()
    {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        mRecyclerListFragment = TaskContextRecyclerListFragment.create()
        fragmentTransaction.add(fragmentResourceId, mRecyclerListFragment)
        fragmentTransaction.commit()
    }

    private fun initializeListFragmentEventListener()
    {
        mRecyclerListFragment!!.setItemRemoveEventListener { e -> removeItemInPersistence(e.item.getId()) }
        mRecyclerListFragment!!.setItemSwapEventListener { e -> swapItemsInPersistence(e.item1.getId(), e.item2.getId()) }
        mRecyclerListFragment!!.setViewCreateEventListener { loadListFragmentContent() }
        mRecyclerListFragment!!.setFloatingButtonClickEventListener { startCreateContextActivity() }
    }

    private fun loadListFragmentContent()
    {
        mRecyclerListFragment!!.loadItems(taskContextPersistenceProvider.taskContexts)
    }

    private fun startCreateContextActivity() {
        val intent = Intent(this, TaskContextCreateActivity::class.java)
        startActivityForResult(intent, INTENT_CREATE_TASK_CONTEXT)
    }

    fun createItemInPersistence(item: RecyclerListItem): TaskContextListItem {
        val taskContextListItem = item as TaskContextListItem
        return TaskContextListItem(taskContextPersistenceProvider!!.createTaskContext(taskContextListItem.taskContext))
    }

    fun updateItemInPersistence(item: RecyclerListItem) {
        val taskContextListItem = item as TaskContextListItem
        taskContextPersistenceProvider!!.updateTaskContext(taskContextListItem.taskContext)
    }

    fun removeItemInPersistence(itemId: Long) {
        taskContextPersistenceProvider!!.deleteTaskContext(itemId)
    }

    fun swapItemsInPersistence(itemId1: Long, itemId2: Long) {
        taskContextPersistenceProvider!!.swapPositionOfTaskContexts(itemId1, itemId2)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        // TODO
        return super.onOptionsItemSelected(item)
    }

    private fun processCreateTaskContextResult(resultIntent:Intent)
    {
        val title = resultIntent.getStringExtra(RESULT_CREATE_TASK_CONTEXT_TITLE)
        createItem(title)
    }

    private fun createItem(title:String)
    {
        val taskContextListItem = TaskContextListItem(TaskContext(0, title, 0, 0))
        val taskContextListItemWithProperId = createItemInPersistence(taskContextListItem)
        mRecyclerListFragment!!.createItem(taskContextListItemWithProperId)
    }

    override val taskContextPersistenceProvider: TaskContextPersistenceProvider
     get() = mPersistenceProvider.taskContextPersistenceProvider


    override val taskPersistenceProvider: TaskPersistenceProvider
        get() = mPersistenceProvider.taskPersistenceProvider

    override val configPersistenceProvider: ConfigPersistenceProvider
        get() = mPersistenceProvider.configPersistenceProvider

    override fun getVersion(): Int {
       return mPersistenceProvider.getVersion()
    }


    companion object {

        val TAG = "ManageContextAc"

        val INTENT_CREATE_TASK_CONTEXT = 1

        val RESULT_CREATE_TASK_CONTEXT_ID = "RESULT_CREATE_CONTEXT_ID"

        val RESULT_CREATE_TASK_CONTEXT_TITLE = "RESULT_CREATE_TASK_CONTEXT_TITLE"

        val fragmentResourceId = R.id.content_manage_context
    }
}
