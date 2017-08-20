package org.kindone.willingtodo.recyclerlist.context

import org.kindone.willingtodo.event.Event
import org.kindone.willingtodo.event.EventDispatcher

/**
 * Created by kindone on 2017. 8. 20..
 */

class FloatingButtonClickEvent() : Event

interface FloatingButtonClickEventDispatcher : EventDispatcher
{
    fun setFloatingButtonClickEventListener(listener :(FloatingButtonClickEvent) -> Unit)
    {
        setEventListener(FLOATING_BUTTON_CREATE, listener)
    }

    fun unsetFloatingButtonClickEventListener()
    {
        unsetEventListener(FLOATING_BUTTON_CREATE)
    }
    fun dispatchFloatingButtonClickEvent()
    {
        dispatchEvent(FLOATING_BUTTON_CREATE, FloatingButtonClickEvent())
    }

    companion object {
        val FLOATING_BUTTON_CREATE = "FLOATING_BUTTON_CREATE"
    }
}