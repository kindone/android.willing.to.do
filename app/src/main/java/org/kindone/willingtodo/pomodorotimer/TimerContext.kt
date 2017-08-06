package org.kindone.willingtodo.pomodorotimer

/**
 * Created by kindone on 2017. 1. 1..
 */

interface TimerContext {
    fun getDurationMs(): Long
    fun changeState(state: TimerState)
}
