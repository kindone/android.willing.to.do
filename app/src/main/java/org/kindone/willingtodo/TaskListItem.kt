package org.kindone.willingtodo

/**
 * Created by kindone on 2015. 10. 16..
 */
class TaskListItem {
    val id: Long
    var title: String
        private set
    val category: String
    private val deadline: String
    var isSeparator: Boolean = false
        private set

    constructor(task: Task) {
        this.id = task.id
        this.title = task.title
        this.category = task.category
        this.deadline = task.deadline
        isSeparator = false
    }

    constructor(separatorName: String) {
        id = 0
        title = separatorName
        category = ""
        deadline = ""
        isSeparator = true
    }

    val task: Task
        get() = Task(id, title, category, deadline, 0, 0)
}
