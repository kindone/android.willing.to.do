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

class TaskEditActivity : TaskFormActivity() {

    private var mTaskId: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val taskId = intent.getLongExtra(ARG_TASK_ID, -1)
        val taskTitle = intent.getStringExtra(TaskFormActivity.Companion.ARG_TASK_TITLE)

        initializeView(taskTitle)
        initializeTaskId(taskId)
    }


    override fun processOptionItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_done -> {
                setActivityResult(RESULT_OK, mTaskId, taskTitle)
                finish()
                return true
            }
            else -> return false
        }
    }


    private fun setActivityResult(status: Int, taskId: Long, taskTitle: String) {
        val intent = this.intent
        intent.putExtra(ARG_TASK_ID, taskId)
        intent.putExtra(TaskFormActivity.Companion.ARG_TASK_TITLE, taskTitle)

        if (parent == null) {
            setResult(status, intent)
        } else {
            parent.setResult(status, intent)
        }
    }


    private fun initializeTaskId(id: Long) {
        mTaskId = id
    }

    companion object {

        private val ARG_TASK_ID = MainActivity.RESULT_TASK_ID
    }


}
