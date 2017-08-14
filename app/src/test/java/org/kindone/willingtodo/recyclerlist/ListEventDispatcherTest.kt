package org.kindone.willingtodo.recyclerlist

import junit.framework.TestCase
import org.kindone.willingtodo.data.Task
import org.kindone.willingtodo.event.EventListenerMap

/**
 * Created by kindone on 2017. 8. 14..
 */
class ListEventDispatcherTest : TestCase() {

    class TaskListEventDispatcher : ListEventDispatcher<Task>
    {
        override var eventListeners: EventListenerMap = mutableMapOf()
    }

    fun testSetItemInsertEventListener() {

        val dispatcher = TaskListEventDispatcher()
        dispatcher.setItemInsertEventListener { e ->
            println("INSERT: " + e.item.title)
        }
        dispatcher.setItemUpdateEventListener { e ->
            println("UPDATE: " + e.item.title)
        }

        dispatcher.dispatchItemInsertEvent(ListItemInsertEvent(Task("sample task1", 0)))
        dispatcher.dispatchItemInsertEvent(ListItemInsertEvent(Task("sample task2", 0)))
        dispatcher.dispatchItemUpdateEvent(ListItemUpdateEvent(Task("sample task3", 0)))
    }

    fun testUnsetItemInsertEventListener() {
        val dispatcher = TaskListEventDispatcher()
        dispatcher.setItemInsertEventListener { e ->
            println("INSERT: " + e.item.title)
        }

        dispatcher.dispatchItemInsertEvent(ListItemInsertEvent(Task("sample task1", 0)))
        dispatcher.unsetItemInsertEventListener()
        dispatcher.dispatchItemInsertEvent(ListItemInsertEvent(Task("sample task2", 0)))
    }

}