package org.kindone.willingtodo.recyclerlist.task

import org.kindone.willingtodo.data.TaskListItem
import org.kindone.willingtodo.recyclerlist.ListEventDispatcher
import org.kindone.willingtodo.recyclerlist.ListItemSwapEvent
import org.kindone.willingtodo.recyclerlist.RecyclerListItem

interface TaskListEventDispatcher<Item : RecyclerListItem> : ListEventDispatcher<Item>
{
    fun setTaskItemSwapPriorityEventListener(listener: (ListItemSwapEvent<Item>) -> Unit) {
        setEventListener(ITEM_SWAP_PRIO_TYPE, listener)
    }

    fun unsetTaskItemSwapPriorityEventListener() {
        unsetEventListener(ITEM_SWAP_PRIO_TYPE)
    }

    fun dispatchTaskItemSwapPriorityEvent(e: ListItemSwapEvent<Item>) {
        dispatchEvent(ITEM_SWAP_PRIO_TYPE, e)
    }

    fun setTaskItemSwapWillingnessEventListener(listener: (ListItemSwapEvent<Item>) -> Unit) {
        setEventListener(ITEM_SWAP_WILL_TYPE, listener)
    }

    fun unsetTaskItemSwapWillingnessEventListener() {
        unsetEventListener(ITEM_SWAP_WILL_TYPE)
    }

    fun dispatchTaskItemSwapWillingnessEvent(e: ListItemSwapEvent<Item>) {
        dispatchEvent(ITEM_SWAP_WILL_TYPE, e)
    }

    companion object {
        val ITEM_SWAP_PRIO_TYPE = "TASK_ITEM_SWAP_PRIO"
        val ITEM_SWAP_WILL_TYPE = "TASK_ITEM_SWAP_WILL"
    }
}