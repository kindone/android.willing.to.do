package org.kindone.willingtodo.data

import org.kindone.willingtodo.recyclerlist.RecyclerListItem

/**
 * Created by kindone on 2015. 10. 16..
 */
class TaskListItem(val task: Task) : RecyclerListItem {

    override fun getId(): Long {
        return task.id
    }

    override fun getTitle(): String {
        return task.title
    }
}
