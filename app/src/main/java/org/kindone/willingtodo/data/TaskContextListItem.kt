package org.kindone.willingtodo.data

import org.kindone.willingtodo.recyclerlist.RecyclerListItem

/**
 * Created by kindone on 2016. 12. 25..
 */

class TaskContextListItem(val taskContext: TaskContext) : RecyclerListItem {

    override fun getId(): Long {
        return taskContext.id
    }

    override fun getTitle(): String {
        return taskContext.name
    }
}
