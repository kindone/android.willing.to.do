package org.kindone.willingtodo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.TextView
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class PomodoroControlActivity : AppCompatActivity(), Runnable {

    private val mDbHelper = DbHelper(this, "test", null/*default cursorfactory*/, DbHelper.LatestVersion)

    private var mPlayControlButton: TextView? = null
    private var mCancelButton: TextView? = null
    private var mTimeTextView: TextView? = null
    private var mTitleTextView: TextView? = null
    private var mThread: Thread? = null
    private var mLoop: AtomicBoolean? = null
    private var mIsPaused: AtomicBoolean? = null

    private var mStartedMs: AtomicLong? = null
    private var mRemainingMs: AtomicLong? = null
    private var mDurationMin: Int = 0

    private var mState: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro_control)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mCancelButton = findViewById(R.id.cancelTextView) as TextView
        mPlayControlButton = findViewById(R.id.playControlTextView) as TextView
        val self = this
        mCancelButton!!.setOnClickListener {
            val timer = mDbHelper.activeTimerEntry
            if (timer != null) {
                mDbHelper.expireTimerEntry(timer)

                // STOP Notification
                val intent = Intent(self, PomodoroTimerService::class.java)
                intent.setAction(PomodoroTimerService.ACTION_CANCEL)
                startService(intent)

                finish()
            }
        }

        mPlayControlButton!!.setOnClickListener {
            when (mState) {
                STATE_EMPTY, STATE_FINISHED, STATE_PAUSED -> {
                    resume()
                    mState = STATE_RUNNING
                }

                STATE_RUNNING -> {
                    pause()
                    mState = STATE_PAUSED
                    updateDisplay()
                }
            }
        }

        mTimeTextView = findViewById(R.id.timeTextView) as TextView
        mTitleTextView = findViewById(R.id.titleText) as TextView

        mLoop = AtomicBoolean(false)
        mIsPaused = AtomicBoolean(false)
        mStartedMs = AtomicLong(0)
        mRemainingMs = AtomicLong(0)

        // start the timer
        val intent = intent
        if (intent.action == ACTION_START) {
            if (mDbHelper.activeTimerEntry != null) {
                mDbHelper.cleanupTimers()
            }
            val taskId = intent.getLongExtra(ARG_TASK_ID, 0)
            val durationMin = intent.getIntExtra(ARG_DURATION_MIN, 0)
            val taskTitle = intent.getStringExtra(ARG_TASK_TITLE)
            val startMs = System.currentTimeMillis()
            mDbHelper.insertTimerEntry(TimerEntry(0, taskId, taskTitle, durationMin, startMs, false, 0))
            mState = STATE_INITIAL
        }

        Log.d(TAG, "onCreate()")
    }

    override fun onStart() {
        super.onStart()

        // stop thread if exists
        stopThread()

        val timer = mDbHelper.activeTimerEntry
        // timer exists
        if (timer != null) {
            Log.v(TAG, "onStart(): timer = { " + timer.toString() + " }")

            // recalculate remaining time
            mDurationMin = timer.durationMin
            mTitleTextView!!.text = timer.title

            // timer running
            if (!timer.isPaused) {
                resume()
                mState = STATE_RUNNING
            } else {
                if (timer.durationMin * 60 * 1000 > timer.elapsedMs) {
                    pause()
                    mRemainingMs!!.set(mDurationMin * 1000 * 60 - timer.elapsedMs)
                    mState = STATE_PAUSED
                } else {
                    pause()
                    mState = STATE_FINISHED
                }
            }
        } else
            mState = STATE_EMPTY// timer does not exist

        if (mState != STATE_RUNNING)
            updateDisplay()
    }

    override fun onStop() {
        stopThread()
        super.onStop()
        Log.d(TAG, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }


    private fun resume() {
        var timer = mDbHelper.activeTimerEntry
        if (timer != null) {
            if (mState != STATE_RUNNING) {
                if (timer.isPaused)
                    timer = mDbHelper.resumeTimerEntry(timer)
                mRemainingMs!!.set(mDurationMin * 1000 * 60 - timer.elapsedMs)
                mStartedMs!!.set(timer.startedMs)
                Log.v(TAG, "resume(): mRemainingMs = " + mRemainingMs!!.get() + ", mStartedMs = " + mStartedMs!!.get())

                val intent = Intent(this, PomodoroTimerService::class.java)
                intent.setAction(PomodoroTimerService.ACTION_RUN)
                intent.putExtra(PomodoroTimerService.ARG_TASK_TITLE, timer.title)
                intent.putExtra(PomodoroTimerService.ARG_DURATION_MIN, timer.durationMin)
                intent.putExtra(PomodoroTimerService.ARG_REMAINING_MS, mStartedMs!!.get() + mRemainingMs!!.get() - System.currentTimeMillis())
                startService(intent)
            }
        }

        startThread()
        Log.d(TAG, "resume()")
    }

    private fun pause() {
        var timer = mDbHelper.activeTimerEntry
        if (timer != null) {
            if (mState == STATE_RUNNING) {
                if (!timer.isPaused)
                    timer = mDbHelper.pauseTimerEntry(timer)
                mRemainingMs!!.set(mDurationMin * 1000 * 60 - timer.elapsedMs)
                Log.v(TAG, "pause(): mRemainingMs = " + mRemainingMs!!.get() + ", mStartedMs = " + mStartedMs!!.get())

                val intent = Intent(this, PomodoroTimerService::class.java)
                intent.setAction(PomodoroTimerService.ACTION_PAUSE)
                intent.putExtra(PomodoroTimerService.ARG_TASK_TITLE, timer.title)
                intent.putExtra(PomodoroTimerService.ARG_DURATION_MIN, timer.durationMin)
                intent.putExtra(PomodoroTimerService.ARG_REMAINING_MS, mRemainingMs!!.get())
                intent.putExtra(PomodoroTimerService.ARG_REMAINING_TIME_STR, getTimeStr(mRemainingMs!!.get()))
                startService(intent)
            }
        }
        stopThread()
        Log.d(TAG, "pause()")
    }

    private fun updateDisplay() {

        when (mState) {
            STATE_EMPTY -> mPlayControlButton!!.text = "Start"
            STATE_FINISHED -> mPlayControlButton!!.text = "Restart"
            STATE_PAUSED -> mPlayControlButton!!.text = "Resume"
            STATE_RUNNING -> mPlayControlButton!!.text = "Pause"
        }

        if (mState == STATE_RUNNING) {
            val endTime = mStartedMs!!.get() + mRemainingMs!!.get()

            if (System.currentTimeMillis() < endTime) {
                // updateDisplay notification
                val timeMs = endTime - System.currentTimeMillis()
                val remainingTimeStr = getTimeStr(timeMs)

                mTimeTextView!!.text = remainingTimeStr
            } else
                mTimeTextView!!.text = "00:00"
        } else if (mState == STATE_FINISHED) {
            mTimeTextView!!.text = "Done"
        } else {
            // paused, ...

            if (mRemainingMs!!.get() > 0) {
                // updateDisplay notification
                val timeMs = mRemainingMs!!.get()
                val remainingTimeStr = getTimeStr(timeMs)

                mTimeTextView!!.text = remainingTimeStr
            } else
                mTimeTextView!!.text = "00:00"
        }

    }


    override fun run() {
        Log.v(TAG, "Entering run()")
        mLoop!!.set(true)
        mIsPaused!!.set(false)

        val endTime = mStartedMs!!.get() + mRemainingMs!!.get()
        //mState = STATE_RUNNING;

        while (mLoop!!.get() && System.currentTimeMillis() < endTime) {
            runOnUiThread { updateDisplay() }

            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                Log.w(TAG, "Interval timer interrupted")
            }

        }

        // finished state
        if (mLoop!!.get()) {
            mState = STATE_FINISHED
            val timer = mDbHelper.activeTimerEntry
            mDbHelper.pauseTimerEntry(timer!!)
        }

        runOnUiThread { updateDisplay() }

        Log.v(TAG, "Exiting run(): mLoop = " + mLoop!!.get() + ", mState = " + mState)
    }

    private fun startThread() {
        stopThread()

        mThread = Thread(this)
        mThread!!.start()

    }

    private fun stopThread() {
        if (mThread != null) {
            mLoop!!.set(false)
            mThread!!.interrupt()
            try {
                mThread!!.join()
            } catch (e: InterruptedException) {

            }

            mThread = null
        }
    }

    private fun getTimeStr(timeMs: Long): String {
        return "%02d:%02d".format(TimeUnit.MILLISECONDS.toMinutes(timeMs), TimeUnit.MILLISECONDS.toSeconds(timeMs) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMs)))
    }

    companion object {

        private val TAG = "PomodoroControlActivity"
        val INTENT_OPEN = 1
        val ACTION_START = "START"
        val ACTION_OPEN = "OPEN"
        val ARG_DURATION_MIN = "DURATION_MIN"
        val ARG_TASK_TITLE = "TASK_TITLE"
        val ARG_TASK_ID = "TASK_ID"

        private val STATE_INITIAL = -1
        private val STATE_EMPTY = 0
        private val STATE_RUNNING = 1
        private val STATE_PAUSED = 2
        private val STATE_FINISHED = 3
    }
}
