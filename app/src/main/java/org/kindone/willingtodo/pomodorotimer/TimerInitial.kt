package org.kindone.willingtodo.pomodorotimer

/**
 * Created by kindone on 2017. 1. 1..
 */

class TimerInitial(context: TimerContext) : TimerState(context) {

    override fun start(): Boolean {
        changeState(TimerRunning(mContext, mContext.getDurationMs()))
        return TimerState.CHANGED
    }

    override fun stop(): Boolean {
        // do nothing
        return TimerState.UNCHANGED
    }

    override fun pause(): Boolean {
        // do nothing
        return TimerState.UNCHANGED
    }

    override fun resume(): Boolean {
        // do nothing
        return TimerState.UNCHANGED
    }

    override fun getRemainingTimeMs(): Long {
        return mContext.getDurationMs()
    }

    override fun isRunning(): Boolean {
        return false
    }

    override fun getStartedTimeMs(): Long {
        return -1L
    }

    override fun isStopped(): Boolean {
        return false
    }
}
