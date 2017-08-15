package org.kindone.willingtodo.recyclerlist

import org.kindone.willingtodo.event.Event
import org.kindone.willingtodo.event.EventDispatcher
import org.kindone.willingtodo.event.EventListenerMap

/**
 * Created by kindone on 2017. 8. 14..
 */


data class ListItemInsertEvent<Item>(val item:Item) : Event
data class ListItemUpdateEvent<Item>(val item:Item) : Event
data class ListItemSwapEvent<Item>(val item1:Item, val item2:Item) : Event

interface ListItemInsertEventDispatcher<Item> : EventDispatcher
{
    fun setItemInsertEventListener(listener: (ListItemInsertEvent<Item>) -> Unit) {
        setEventListener(ITEM_INSERT_TYPE, listener)
    }

    fun unsetItemInsertEventListener() {
        unsetEventListener(ITEM_INSERT_TYPE)
    }

    companion object {
        val ITEM_INSERT_TYPE = "ITEM_INSERT"
    }
}

interface ListItemUpdateEventDispatcher<Item> : EventDispatcher
{
    fun setItemUpdateEventListener(listener: (ListItemUpdateEvent<Item>) -> Unit) {
        setEventListener(ITEM_UPDATE_TYPE, listener)
    }

    fun unsetItemUpdateEventListener() {
        unsetEventListener(ITEM_UPDATE_TYPE)
    }

    companion object {
        val ITEM_UPDATE_TYPE = "ITEM_UPDATE"
    }
}

interface ListEventDispatcher<Item> : EventDispatcher
{
    fun setItemInsertEventListener(listener: (ListItemInsertEvent<Item>) -> Unit) {
        setEventListener(ITEM_INSERT_TYPE, listener)
    }

    fun unsetItemInsertEventListener() {
        unsetEventListener(ITEM_INSERT_TYPE)
    }

    fun setItemUpdateEventListener(listener: (ListItemUpdateEvent<Item>) -> Unit) {
        setEventListener(ITEM_UPDATE_TYPE, listener)
    }

    fun unsetItemUpdateEventListener() {
        unsetEventListener(ITEM_UPDATE_TYPE)
    }

    fun dispatchItemInsertEvent(e:ListItemInsertEvent<Item>) {
        dispatchEvent(ITEM_INSERT_TYPE, e)
    }

    fun dispatchItemUpdateEvent(e:ListItemUpdateEvent<Item>) {
        dispatchEvent(ITEM_UPDATE_TYPE, e)
    }

    companion object {
        val ITEM_INSERT_TYPE = "ITEM_INSERT"
        val ITEM_UPDATE_TYPE = "ITEM_UPDATE"
    }
}