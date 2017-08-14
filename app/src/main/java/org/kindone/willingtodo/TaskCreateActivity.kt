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

class TaskCreateActivity : TaskFormActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskTitle =
                if(intent.hasExtra(TaskFormActivity.ARG_TASK_TITLE))
                    intent.getStringExtra(TaskFormActivity.ARG_TASK_TITLE)
                else
                    ""

        initializeView(taskTitle)
    }


    override fun processOptionItemSelected(item: MenuItem): Boolean {
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


    override fun setActivityResult(status: Int, taskTitle: String) {
        val intent = this.intent

        intent.putExtra(TaskFormActivity.ARG_TASK_TITLE, taskTitle)

        if (parent == null) {
            setResult(status, intent)
        } else {
            parent.setResult(status, intent)
        }
    }


}
