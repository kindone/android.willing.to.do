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
import org.kindone.willingtodo.recyclerlist.context.ContextRecyclerListFragment
import org.kindone.willingtodo.recyclerlist.RecyclerListFragment


class ManageContextActivity : AppCompatActivity(), PersistenceProvider {


    private var mCurrentListFragment: RecyclerListFragment? = null
    private val mPersistenceProvider = SQLPersistenceProvider(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_context)
        initToolbar()

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        mCurrentListFragment = ContextRecyclerListFragment.create()
        fragmentTransaction.add(R.id.content_manage_context, mCurrentListFragment)
        fragmentTransaction.commit()

    }

    private fun initToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        // TODO
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == INTENT_CREATE_CONTEXT) {
            if (resultCode == RESULT_OK) {
                val name = data.getStringExtra(RESULT_CREATE_CONTEXT_TITLE)
                mCurrentListFragment!!.createItem(TaskContextListItem(TaskContext(0, name, 0, 0)))
            }
        }
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

        var TAG = "ManageContextAc"
        var INTENT_CREATE_CONTEXT = 1
        var RESULT_CREATE_TASK_CONTEXT_ID = "RESULT_CREATE_CONTEXT_ID"
        var RESULT_CREATE_TASK_DEADLINE = "RESULT_CREATE_TASK_DEADLINE"
        var RESULT_CREATE_CONTEXT_TITLE = "RESULT_CREATE_CONTEXT_TITLE"
    }
}
