package org.kindone.willingtodo.pomodorotimer

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Created by kindone on 2017. 1. 11..
 */

class StopWatch : TimerContext {

    private var mTimerState: TimerState? = null

    private var mDurationMs: Long = 0

    private var mTickerHandle: ScheduledFuture<*>? = null

    private var mTimerEventListener: TimerEventListener? = null

    private val BUFFER_TIME_MS: Long = 1000

    private val scheduler = Executors.newScheduledThreadPool(1)


    // advanced timer events
    constructor(timerEventListener: TimerEventListener) {
        setInitialTimerState()
        mTimerEventListener = timerEventListener
    }

    // simple tick event only
    constructor(onTick: Runnable) {
        setInitialTimerState()
        mTimerEventListener = createTimerEventListener(onTick)
    }

    fun startTimer(newDurationMs: Long) {
        mDurationMs = newDurationMs
        val changedState = setRunningTimerState()
        startBackgroundTicking(mDurationMs + BUFFER_TIME_MS)

        if (changedState)
            dispatchTimerStartedEvent()
    }

    fun restartTimer(newDurationMs: Long, newStartedTimeMs: Long) {
        setRunningTimerState(newDurationMs, newStartedTimeMs)
        mDurationMs = remainingTimeMs
        startBackgroundTicking(mDurationMs + BUFFER_TIME_MS)
        dispatchTimerStartedEvent()
    }

    fun pauseTimer() {
        val changedState = setPausedTimerState()
        if (changedState)
            dispatchTimerStoppedEvent()
    }

    fun pauseTimer(newRemainingTimeMs: Long) {
        val changedState = setPausedTimerState(newRemainingTimeMs)
        if (changedState)
            dispatchTimerStoppedEvent()
    }

    fun resumeTimer() {
        val changedState = setResumedTimerState()
        startBackgroundTicking(remainingTimeMs + BUFFER_TIME_MS)

        if (changedState)
            dispatchTimerStartedEvent()
    }

    fun stopTimer() {
        val changedState = setStoppedTimerState()
        if (changedState)
            dispatchTimerStoppedEvent()
    }

    fun cleanUp() {
        stopTimer()
        stopBackgroundTicking()
    }


    val isRunning: Boolean
        get() = mTimerState!!.isRunning()

    val isStopped: Boolean
        get() = mTimerState!!.isStopped()


    override fun getDurationMs(): Long {
        return mDurationMs
    }

    val startedTimeMs: Long
        get() = mTimerState!!.getStartedTimeMs()

    val remainingTimeMs: Long
        get() = mTimerState!!.getRemainingTimeMs()

    val remainingTimeStr: String
        get() {
            var str: String =
            synchronized(mTimerState!!) {
                mTimerState!!.getRemainingTimeStr()
            }
            return str
        }


    override fun changeState(state: TimerState) {
        synchronized(mTimerState!!) {
            mTimerState = state
        }
    }


    private fun setInitialTimerState() {
        mTimerState = TimerInitial(this)
    }

    private fun setRunningTimerState(durationMs: Long, startedTimeMs: Long) {
        mTimerState = TimerRunning(this, durationMs, startedTimeMs)
    }

    private fun setRunningTimerState(): Boolean {
        return mTimerState!!.start()
    }

    private fun setPausedTimerState(newRemainingTimeMs: Long): Boolean {
        val wasRunning = mTimerState!!.isRunning()
        mTimerState = TimerPaused(this, newRemainingTimeMs)
        return wasRunning
    }

    private fun setPausedTimerState(): Boolean {
        return mTimerState!!.pause()
    }

    private fun setResumedTimerState(): Boolean {
        return mTimerState!!.resume()
    }

    private fun setStoppedTimerState(): Boolean {
        return mTimerState!!.stop()
    }


    private fun startBackgroundTicking(maxDurationMs: Long) {
        stopBackgroundTicking()
        mTickerHandle = scheduleTimerTick()
        scheduleTickerExpiry(maxDurationMs)
    }

    private fun stopBackgroundTicking() {
        if (isTickerActive) {
            cancelTicker()
        }
    }


    private fun scheduleTimerTick(): ScheduledFuture<*> {
        val scheduledJob = Ticker()
        val initialDelay: Long = 500
        val period: Long = 500
        return scheduler.scheduleAtFixedRate(scheduledJob, initialDelay, period, TimeUnit.MILLISECONDS)
    }

    private fun scheduleTickerExpiry(maxDurationMs: Long) {
        val scheduledJob = ExpireTick()
        scheduler.schedule(scheduledJob, maxDurationMs, TimeUnit.MILLISECONDS)
    }


    private val isTickerActive: Boolean
        get() = mTickerHandle != null && !mTickerHandle!!.isCancelled

    private fun cancelTicker() {
        mTickerHandle!!.cancel(true)
    }


    private fun dispatchTimerStartedEvent() {
        mTimerEventListener!!.onTimerStarted()
    }

    private fun dispatchTimerStoppedEvent() {
        mTimerEventListener!!.onTimerStopped()
    }

    private fun dispatchTimerTickEvent() {
        mTimerEventListener!!.onTick()
    }

    private fun dispatchTimerExpiredEvent() {
        mTimerEventListener!!.onTimerExpired()
    }


    private fun createTimerEventListener(runOnTick: Runnable): TimerEventListener {
        return object : TimerEventListener {

            override fun onTick() {
                runOnTick.run()
            }

            override fun onTimerStarted() {}

            override fun onTimerStopped() {}

            override fun onTimerExpired() {}
        }
    }


    interface TimerEventListener {
        fun onTick()

        fun onTimerStarted()

        fun onTimerStopped()

        fun onTimerExpired()
    }


    internal inner class Ticker : Runnable {
        override fun run() {
            if (!isRunning) {
                stopBackgroundTicking()
            }
            if (remainingTimeMs <= 0) {
                stopTimer()
            }

            dispatchTimerTickEvent()
        }
    }


    internal inner class ExpireTick : Runnable {
        override fun run() {
            stopBackgroundTicking()
            dispatchTimerExpiredEvent()
        }
    }
}
