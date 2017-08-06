package org.kindone.willingtodo.pomodorotimer

/**
 * Created by kindone on 2017. 1. 1..
 */

class TimerRunning : TimerState {

    private val mDurationMs: Long
    private val mStartedMs: Long

    constructor(context: TimerContext, durationMs: Long) : super(context) {
        mStartedMs = System.currentTimeMillis()
        mDurationMs = durationMs
    }

    constructor(context: TimerContext, durationMs: Long, startedMs: Long) : super(context) {
        mStartedMs = startedMs
        mDurationMs = durationMs
    }

    override fun pause(): Boolean {
        val elapsedMs = System.currentTimeMillis() - mStartedMs
        val remainingMs = mDurationMs - elapsedMs
        changeState(TimerPaused(mContext, remainingMs))
        return TimerState.CHANGED
    }

    override fun start(): Boolean {
        // DO NOTHING
        return TimerState.UNCHANGED
    }

    override fun resume(): Boolean {
        // DO NOTHING
        return TimerState.UNCHANGED
    }

    override fun stop(): Boolean {
        changeState(TimerStopped(mContext))
        return TimerState.CHANGED
    }

    override fun getRemainingTimeMs(): Long {
        val elapsedMs = System.currentTimeMillis() - mStartedMs
        val remainingMs = mDurationMs - elapsedMs
        return if (remainingMs > 0) remainingMs else 0
    }

    override fun getStartedTimeMs(): Long {
        return mStartedMs
    }

    override fun isRunning(): Boolean {
        return true
    }

    override fun isStopped(): Boolean {
        return false
    }
}
