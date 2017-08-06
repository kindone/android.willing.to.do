package org.kindone.willingtodo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

open class TaskFormActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val taskTitle = intent.getStringExtra(ARG_TASK_TITLE)

        initializeView(taskTitle)
    }

    override fun onStop() {
        Log.v(TAG, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.v(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onResume() {
        Log.v(TAG, "onResume")
        forceShowInputMethod()
        super.onResume()
    }

    override fun onPause() {
        Log.v(TAG, "onPause")
        hideInputMethod()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.v(TAG, "onCreateOptionsMenu")
        initializeOptionsMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.v(TAG, "onOptionsItemSelected")
        val processed = processOptionItemSelected(item)
        return processed || super.onOptionsItemSelected(item)
    }


    protected open fun processOptionItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_done -> {
                setActivityResult(RESULT_OK, taskTitle)
                finish()
                return true
            }
            else -> return false
        }
    }


    protected open fun setActivityResult(status: Int, taskTitle: String) {
        val intent = this.intent

        intent.putExtra(ARG_TASK_TITLE, taskTitle)

        if (parent == null) {
            setResult(status, intent)
        } else {
            parent.setResult(status, intent)
        }
    }


    protected fun initializeView(title: String) {
        setContentView(layoutResourceId)
        initializeToolbar()
        taskTitle = title
    }

    protected fun initializeToolbar() {
        val toolbar = findViewById(toolbarResourceId) as Toolbar?
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    protected fun initializeOptionsMenu(menu: Menu) {
        menuInflater.inflate(createTaskMenuItemResourceId, menu)
    }


    protected fun forceShowInputMethod() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    protected fun hideInputMethod() {
        val immhide = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    protected var taskTitle: String
        get() {
            val titleEditText = findViewById(R.id.titleEditText) as EditText?
            return titleEditText!!.text.toString()
        }
        set(text) {
            val titleEditText = findViewById(R.id.titleEditText) as EditText?
            titleEditText!!.setText(text)
        }

    companion object {

        private val TAG = "TaskCreate"

        private val layoutResourceId = R.layout.activity_task_create

        private val toolbarResourceId = R.id.toolbar

        private val createTaskMenuItemResourceId = R.menu.task_create

        public var ARG_TASK_TITLE = MainActivity.RESULT_TASK_TITLE
    }

}
