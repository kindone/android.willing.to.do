package org.kindone.willingtodo.data

/**
 * Created by kindone on 2015. 11. 7..
 */
class Task(val id: Long, val title: String,
           val contextId: Long, val category: String,
           val deadline: String, val priority: Int, val willingness: Int) {

    constructor(title: String, contextId: Long) : this(0, title, contextId, "", "", 0, 0) {}

    override fun toString(): String {
        return "id = " + id +
                ", title = " + title +
                ", contextId = " + contextId +
                ", category = " + category +
                ", deadline = " + deadline +
                ", priority = " + priority +
                ", willingness = " + willingness
    }
}
