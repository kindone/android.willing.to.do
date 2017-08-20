package org.kindone.willingtodo.recyclerlist.context

import org.kindone.willingtodo.event.Event
import org.kindone.willingtodo.event.EventDispatcher

/**
 * Created by kindone on 2017. 8. 20..
 */


class ViewCreateEvent() : Event


interface ViewCreateEventDispatcher : EventDispatcher
{
    fun setViewCreateEventListener(listener :(ViewCreateEvent) -> Unit)
    {
        setEventListener(VIEW_CREATE, listener)
    }

    fun unsetViewCreateEventListener()
    {
        unsetEventListener(VIEW_CREATE)
    }
    fun dispatchViewCreateEvent(e:ViewCreateEvent)
    {
        dispatchEvent(VIEW_CREATE, e)
    }

    companion object {
        val VIEW_CREATE = "VIEW_CREATE"
    }
}