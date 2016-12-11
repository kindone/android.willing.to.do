package org.kindone.willingtodo

/**
 * Created by kindone on 2015. 11. 7..
 */
class Task(val id: Long, val title: String, val category: String, val deadline: String, val priority: Int, val willingness: Int) {

    override fun toString(): String {
        return "id = $id, title = $title, category = $category, deadline = $deadline, priority = $priority, willingness = $willingness"
    }
}
