package org.kindone.willingtodo.event

/**
 * Created by kindone on 2017. 8. 14..
 */

typealias EventListener<T> = (T) -> Unit
typealias EventListenerMap = MutableMap<String, EventListener<*>>

open interface EventDispatcher
{
    var eventListeners: EventListenerMap


    fun <E : Event> setEventListener(key:String, listener: (E) -> Unit) {
        eventListeners.put(key, listener)
    }

    fun unsetEventListener(key:String) {
        eventListeners.remove(key)
    }

    fun <E: Event>dispatchEvent(key:String, event:E) {
        if(eventListeners.get(key) != null) {
            val eventListener = eventListeners.get(key)
            eventListener!!(event)
        }
    }
}

class SampleEvent() : Event

class SampleEventDispatcher : EventDispatcher
{
    override var eventListeners: EventListenerMap = mutableMapOf()

    fun setSampleEventListener(listener: (SampleEvent) -> Unit) {
        setEventListener(SAMPLE_EVENT_TYPE, listener)
    }

    fun unsetSampleEventListener() {
        unsetEventListener(SAMPLE_EVENT_TYPE)
    }

    fun dispatchSampleEvent(e:SampleEvent) {
        dispatchEvent(SAMPLE_EVENT_TYPE, e)
    }

    companion object {
        val SAMPLE_EVENT_TYPE = "SAMPLE"
    }
}