package org.kindone.willingtodo.recyclerlist

import org.kindone.willingtodo.event.Event
import org.kindone.willingtodo.event.EventDispatcher

/**
 * Created by kindone on 2017. 8. 14..
 */


data class ListItemInsertEvent<Item>(val item:Item) : Event
data class ListItemUpdateEvent<Item>(val item:Item) : Event
data class ListItemRemoveEvent<Item>(val item1:Item) : Event
data class ListItemSwapEvent<Item>(val item1:Item, val item2:Item) : Event

interface ListItemInsertEventDispatcher<Item> : EventDispatcher
{
    fun setItemInsertEventListener(listener: (ListItemInsertEvent<Item>) -> Unit) {
        setEventListener(ITEM_INSERT_TYPE, listener)
    }

    fun unsetItemInsertEventListener() {
        unsetEventListener(ITEM_INSERT_TYPE)
    }

    fun dispatchItemInsertEvent(e:ListItemInsertEvent<Item>) {
        dispatchEvent(ITEM_INSERT_TYPE, e)
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

    fun dispatchItemUpdateEvent(e:ListItemUpdateEvent<Item>) {
        dispatchEvent(ITEM_UPDATE_TYPE, e)
    }

    companion object {
        val ITEM_UPDATE_TYPE = "ITEM_UPDATE"
    }
}

interface ListItemRemoveEventDispatcher<Item> : EventDispatcher
{
    fun setItemRemoveEventListener(listener: (ListItemRemoveEvent<Item>) -> Unit) {
        setEventListener(ITEM_REMOVE_TYPE, listener)
    }

    fun unsetItemRemoveEventListener() {
        unsetEventListener(ITEM_REMOVE_TYPE)
    }


    fun dispatchItemRemoveEvent(e:ListItemRemoveEvent<Item>) {
        dispatchEvent(ITEM_REMOVE_TYPE, e)
    }

    companion object {
        val ITEM_REMOVE_TYPE = "ITEM_REMOVE"
    }
}


interface ListItemSwapEventDispatcher<Item> : EventDispatcher
{
    fun setItemSwapEventListener(listener: (ListItemSwapEvent<Item>) -> Unit) {
        setEventListener(ITEM_SWAP_TYPE, listener)
    }

    fun unsetItemSwapEventListener() {
        unsetEventListener(ITEM_SWAP_TYPE)
    }

    fun dispatchItemSwapEvent(e:ListItemSwapEvent<Item>) {
        dispatchEvent(ITEM_SWAP_TYPE, e)
    }

    companion object {
        val ITEM_SWAP_TYPE = "ITEM_SWAP"
    }
}


interface ListEventDispatcher<Item> :
        ListItemInsertEventDispatcher<Item>, ListItemUpdateEventDispatcher<Item>,
        ListItemRemoveEventDispatcher<Item>, ListItemSwapEventDispatcher<Item>
{

}