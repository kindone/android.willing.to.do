/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kindone.willingtodo.recyclerlist.context

import android.content.Intent

import org.kindone.willingtodo.TaskContextCreateActivity
import org.kindone.willingtodo.ManageTaskContextActivity
import org.kindone.willingtodo.data.TaskContext
import org.kindone.willingtodo.recyclerlist.RecyclerListAdapter
import org.kindone.willingtodo.recyclerlist.RecyclerListFragment
import org.kindone.willingtodo.recyclerlist.task.TaskRecyclerListAdapter


class TaskContextRecyclerListFragment : RecyclerListFragment<TaskContext>() {

    fun refresh(version: Int) {
        (mListAdapter as TaskRecyclerListAdapter).refresh(version)
    }

    override fun createListAdapter(): RecyclerListAdapter<*> {
        // recycler list
        val adapter = TaskContextRecyclerListAdapter(mContextProvider!!, this)
        return adapter
    }

    override fun onFloatingButtonClick() {
        startCreateContextActivity()
    }

    private fun startCreateContextActivity() {
        val intent = Intent(activity, TaskContextCreateActivity::class.java)
        activity.startActivityForResult(intent, ManageTaskContextActivity.INTENT_CREATE_TASK_CONTEXT)
    }

    companion object {

        fun create(): RecyclerListFragment<TaskContext> {
            val fragment = TaskContextRecyclerListFragment()
            return fragment
        }
    }
}
