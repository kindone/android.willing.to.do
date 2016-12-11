package org.kindone.willingtodo

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText

class TaskEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_edit)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val titleEditText = findViewById(R.id.titleEditText) as EditText
        val categoryEditText = findViewById(R.id.labelEditText) as EditText

        val intent = intent
        titleEditText.setText(intent.getStringExtra(MainActivity.RESULT_TASK_TITLE))
        categoryEditText.setText(intent.getStringExtra(MainActivity.RESULT_TASK_CATEGORY))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.task_create, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_done) {
            val intent = this.intent
            val titleText = findViewById(R.id.titleEditText) as EditText
            val categoryText = findViewById(R.id.labelEditText) as EditText

            //            int pos = intent.getIntExtra(MainActivity.RESULT_TASK_POSITION, 0);/*
            //            int taskId = intent.getIntExtra(MainActivity.RESULT_TASK_ID, 0);
            //            Log.w("TEMP", "task id: " + taskId);
            //
            //            intent.putExtra(MainActivity.RESULT_TASK_ID, taskId);*/
            intent.putExtra(MainActivity.RESULT_TASK_TITLE, titleText.text.toString())
            intent.putExtra(MainActivity.RESULT_TASK_CATEGORY, categoryText.text.toString())


            if (parent == null) {
                setResult(Activity.RESULT_OK, intent)
            } else {
                parent.setResult(Activity.RESULT_OK, intent)
            }
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}
