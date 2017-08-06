package org.kindone.willingtodo.recyclerlist

/**
 * Created by kindone on 2016. 12. 18..
 */

interface RecyclerListItem {
    fun getId(): Long
    fun getTitle(): String

    interface Updater {
        fun update(item: RecyclerListItem): RecyclerListItem
    }
}
