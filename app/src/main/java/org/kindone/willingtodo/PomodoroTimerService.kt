package org.kindone.willingtodo

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.os.Vibrator
import android.support.v4.app.TaskStackBuilder
import android.support.v7.app.NotificationCompat
import android.util.Log

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class PomodoroTimerService : Service(), Runnable {

    internal var mStartMode = Service.START_STICKY       // indicates how to behave if the service is killed
    internal var mAllowRebind: Boolean = false // indicates whether onRebind should be used
    internal var mThread: Thread? = null
    internal var mLoop: AtomicBoolean
    internal var mIsPaused: AtomicBoolean
    internal var mRemainingMs: AtomicLong
    internal var mMediaPlayer: MediaPlayer? = null
    internal var mOnThreadFinish: Handler? = null

    internal var mTaskTitle: String
    internal var mDurationMin: Int = 0

    init {
        mDurationMin = 25 // default
        mLoop = AtomicBoolean(false)
        mIsPaused = AtomicBoolean(false)
        mRemainingMs = AtomicLong(0)

        mTaskTitle = ""
    }

    override fun onCreate() {
        super.onCreate()
        mOnThreadFinish = Handler()
    }

    override fun run() {

        mLoop.set(true)
        mIsPaused.set(false)

        var remainingTimeStr = (mDurationMin * 1000 * 60).toString()

        val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(this)
        mBuilder.setContentTitle("Pomodoro Timer Active") // TODO: replace with resource
                .setContentText(mTaskTitle).setSmallIcon(R.drawable.fab_shadow)// FIXME
                .setOngoing(true)

        val resultIntent = Intent(this, PomodoroControlActivity::class.java)
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        resultIntent.setAction(PomodoroControlActivity.ACTION_OPEN)

        val stackBuilder = TaskStackBuilder.create(this)

        stackBuilder.addParentStack(PomodoroControlActivity::class.java)

        stackBuilder.addNextIntent(resultIntent)
        // TODO: check
        val resultPendingIntent = stackBuilder.getPendingIntent(
                MainActivity.INTENT_POMODORO,
                PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(resultPendingIntent)

        // if timer expires, terminate notification and quit
        val startTime = System.currentTimeMillis()
        val endTime = startTime + mRemainingMs.get()

        while (System.currentTimeMillis() < endTime && mLoop.get()) {
            // update notification
            val timeMs = endTime - System.currentTimeMillis()
            remainingTimeStr = "%02d:%02d".format(TimeUnit.MILLISECONDS.toMinutes(timeMs), TimeUnit.MILLISECONDS.toSeconds(timeMs) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMs)))

            mBuilder.setContentTitle(mTaskTitle).setContentText(remainingTimeStr)
            notifyManager.notify(NOTIFICATION_ID, mBuilder.build())

            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                Log.w(TAG, "Interval timer interrupted")
            }

        }

        if (mLoop.get())
        // finished
        {
            mOnThreadFinish?.post { stopSound() }
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(500)
            mBuilder.setContentText("Done").setOngoing(false)
            notifyManager.notify(NOTIFICATION_ID, mBuilder.build())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent == null)
            return mStartMode

        val action = intent.action

        Log.d(TAG, "onStartCommand(): action = " + action)

        if (action == ACTION_RUN) {
            mTaskTitle = intent.getStringExtra(ARG_TASK_TITLE)
            mDurationMin = intent.getIntExtra(ARG_DURATION_MIN, 25)
            mRemainingMs.set(intent.getLongExtra(ARG_REMAINING_MS, 0))

            startThread()
            startSound()
        } else if (action == ACTION_CANCEL) {
            stopThread()
            stopSound()
            val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifyManager.cancel(NOTIFICATION_ID)

        } else if (action == ACTION_PAUSE) {
            stopThread()
            stopSound()

            mTaskTitle = intent.getStringExtra(ARG_TASK_TITLE)
            val remainingTimeStr = intent.getStringExtra(ARG_REMAINING_TIME_STR)

            val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mBuilder = NotificationCompat.Builder(this)
            mBuilder.setContentTitle(mTaskTitle).setContentText(remainingTimeStr).setSmallIcon(R.drawable.fab_shadow).setOngoing(false)

            val resultIntent = Intent(this, PomodoroControlActivity::class.java)
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            resultIntent.setAction(PomodoroControlActivity.ACTION_OPEN)

            val stackBuilder = TaskStackBuilder.create(this)

            stackBuilder.addParentStack(PomodoroControlActivity::class.java)

            stackBuilder.addNextIntent(resultIntent)

            val resultPendingIntent = stackBuilder.getPendingIntent(
                    MainActivity.INTENT_POMODORO,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            mBuilder.setContentIntent(resultPendingIntent)

            notifyManager.notify(NOTIFICATION_ID, mBuilder.build())
        }

        return mStartMode
    }

    private fun startThread() {
        stopThread()
        mThread = Thread(this)
        mThread!!.start()

    }

    private fun stopThread() {
        if (mThread != null) {
            mLoop.set(false)
            mThread!!.interrupt()
            try {
                mThread!!.join()
            } catch (e: InterruptedException) {

            }

            mThread = null
        }
    }

    private fun startSound() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.ticking)
        mMediaPlayer!!.isLooping = true
        mMediaPlayer!!.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        mMediaPlayer!!.start()
    }

    private fun stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onUnbind(intent: Intent): Boolean {
        // All clients have unbound with unbindService()
        return mAllowRebind
    }

    override fun onRebind(intent: Intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    override fun onDestroy() {
        stopThread()
    }

    companion object {
        internal var TAG = "PomodoroTimerService"
        internal var ACTION_RUN = "RUN"
        internal var ACTION_CANCEL = "CANCEL"
        internal var ACTION_PAUSE = "PAUSE"
        internal var ARG_DURATION_MIN = "DURATION_MIN"
        internal var ARG_REMAINING_MS = "REMAINING_MS"
        internal var ARG_TASK_TITLE = "TASK_TITLE"
        internal var ARG_REMAINING_TIME_STR = "REMAINING_TIME_STR"

        internal var NOTIFICATION_ID = 1
    }

}