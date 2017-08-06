package org.kindone.willingtodo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText

class ContextCreateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        initializeMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val processed = processOptionItemSelected(item)
        return processed || super.onOptionsItemSelected(item)
    }

    private fun initializeView() {
        setContentView(viewResourceId)
        setSupportActionBar(findViewById(toolbarResourceId) as Toolbar?)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initializeMenu(menu: Menu) {
        menuInflater.inflate(menuResourceId, menu)
    }

    private fun processOptionItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_done -> {
                setActivityResult(RESULT_OK, CREATE_CONTEXT_TITLE, contextTitle)
                finish()
                return true
            }
            else -> return false
        }
    }

    private fun setActivityResult(status: Int, key: String, value: String) {
        val intent = this.intent
        intent.putExtra(key, value)

        if (parent == null) {
            setResult(status, intent)
        } else {
            parent.setResult(status, intent)
        }
    }

    private val contextTitle: String
        get() = (findViewById(titleEditTextResourceId) as EditText).text.toString()

    companion object {

        private val viewResourceId = R.layout.activity_context_create
        private val toolbarResourceId = R.id.toolbar
        private val menuResourceId = R.menu.context_create

        private val titleEditTextResourceId = R.id.titleEditText
        private val CREATE_CONTEXT_TITLE = MainActivity.RESULT_CREATE_CONTEXT_TITLE
    }

}
