package org.kindone.willingtodo.pomodorotimer

/**
 * Created by kindone on 2017. 1. 1..
 */

class TimerStopped(context: TimerContext) : TimerState(context) {


    override fun start(): Boolean {
        changeStateToRunningWithInheritedDuration()
        return TimerState.CHANGED
    }

    override fun stop(): Boolean {
        // DO NOTHING
        return TimerState.UNCHANGED
    }

    override fun resume(): Boolean {
        // DO NOTHING
        return TimerState.UNCHANGED
    }

    override fun pause(): Boolean {
        // DO NOTHING
        return TimerState.UNCHANGED
    }

    override fun isRunning(): Boolean {
        return false
    }

    override fun isStopped(): Boolean {
        return true
    }

    override fun getRemainingTimeMs(): Long {
        return 0
    }

    override fun getStartedTimeMs(): Long {
        return -1L
    }

    private fun changeStateToRunningWithInheritedDuration() {
        changeState(TimerRunning(mContext, mContext.getDurationMs()))
    }
}
