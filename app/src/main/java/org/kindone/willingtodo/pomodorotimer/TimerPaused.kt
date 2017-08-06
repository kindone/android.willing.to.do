package org.kindone.willingtodo.pomodorotimer

/**
 * Created by kindone on 2017. 1. 1..
 */

class TimerPaused(context: TimerContext, private val mRemainingMs: Long) : TimerState(context) {


    override fun start(): Boolean {
        resume()
        return TimerState.CHANGED
    }

    override fun pause(): Boolean {
        // DO NOTHING
        return TimerState.UNCHANGED
    }

    override fun resume(): Boolean {
        changeState(TimerRunning(mContext, mRemainingMs))
        return TimerState.CHANGED
    }

    override fun stop(): Boolean {
        changeState(TimerStopped(mContext))
        return TimerState.UNCHANGED
    }

    override fun getRemainingTimeMs(): Long {
        return if (mRemainingMs > 0) mRemainingMs else 0
    }

    override fun getStartedTimeMs(): Long {
        return -1L
    }

    override fun isRunning(): Boolean {
        return false
    }

    override fun isStopped(): Boolean {
        return false
    }
}
