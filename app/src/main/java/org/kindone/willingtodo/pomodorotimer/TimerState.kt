package org.kindone.willingtodo.pomodorotimer

import java.util.concurrent.TimeUnit

/**
 * Created by kindone on 2017. 1. 1..
 */

abstract class TimerState(internal val mContext: TimerContext) {

    // boolean states whether the state has changed
    abstract fun start(): Boolean

    abstract fun pause(): Boolean

    abstract fun resume(): Boolean

    abstract fun stop(): Boolean

    fun changeState(newState: TimerState) {
        mContext.changeState(newState)
    }


    protected fun getRemainingTimeStr(remainingTime: Long): String {
        val time = if (remainingTime >= 0) remainingTime else 0
        if (time == 0L)
            return "Stopped"
        else
            return mm_ss_formattedRemainingTimeStr(time)
    }

    abstract fun isRunning(): Boolean

    abstract fun isStopped(): Boolean

    fun getRemainingTimeStr(): String
        = getRemainingTimeStr(getRemainingTimeMs())

    abstract fun getRemainingTimeMs(): Long

    abstract fun getStartedTimeMs(): Long

    private fun throwUndefined() {
        throw RuntimeException("undefined change of state")
    }

    private fun mm_ss_formattedRemainingTimeStr(time: Long): String {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)))
    }

    companion object {
        val CHANGED = true
        val UNCHANGED = false
    }
}
