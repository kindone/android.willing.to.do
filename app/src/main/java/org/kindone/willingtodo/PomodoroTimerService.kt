package org.kindone.willingtodo

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.os.PowerManager
import android.os.Vibrator
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.util.Log

import org.kindone.willingtodo.pomodorotimer.StopWatch

class PomodoroTimerService : Service() {

    private val mStopWatch: StopWatch
    private var mTitle = ""
    private var mNotifyManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null
    private var mMediaPlayer: MediaPlayer? = null

    init {
        Log.v(TAG, "constructors")
        mStopWatch = StopWatch(object : StopWatch.TimerEventListener {
            override fun onTick() {
                displayTick()
            }

            override fun onTimerStarted() {
                Log.v(TAG, "onTimerStarted")
                startSound()
            }

            override fun onTimerStopped() {
                Log.v(TAG, "onTimerStopped")
                stopSound()
            }

            override fun onTimerExpired() {
                Log.v(TAG, "onTimerExpired")
                vibrate()
            }
        })
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.v(TAG, "onBind")
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val action = intent.action

        prepareNotification()

        if (action == ACTION_START) {
            val taskId = intent.getLongExtra(ARG_TASK_ID, -1)
            mTitle = intent.getStringExtra(ARG_TASK_TITLE)
            val durationMs = intent.getLongExtra(ARG_TASK_DURATION_MS, (25 * 60 * 1000).toLong())
            mStopWatch.startTimer(durationMs)
        } else if (action == ACTION_PAUSE) {
            val remainingTimeMs = intent.getLongExtra(ARG_TASK_REMAINING_TIME_MS, 0)
            mStopWatch.pauseTimer(remainingTimeMs)
        } else if (action == ACTION_STOP) {
            mStopWatch.stopTimer()
            closeNotification()
        } else if (action == ACTION_RESUME) {
            mStopWatch.resumeTimer()
        }

        if (!mStopWatch.isRunning)
            displayTick()

        return super.onStartCommand(intent, flags, startId)
    }

    fun displayTick() {
        Log.v(TAG, "beep: " + mTitle + " : " + mStopWatch.remainingTimeStr)
        mBuilder!!.setContentTitle(mTitle).setContentText(mStopWatch.remainingTimeStr)
        mNotifyManager!!.notify(NOTIFICATION_ID, mBuilder!!.build())
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.v(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent) {
        Log.v(TAG, "onRebind")
        super.onRebind(intent)
    }

    override fun onDestroy() {
        Log.v(TAG, "onDestroy")
        super.onDestroy()
    }

    private fun prepareNotification() {
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mBuilder = NotificationCompat.Builder(this)
        mBuilder!!.setContentTitle("Pomodoro Timer Active") // TODO: replace with resource
                .setContentText(mTitle).setSmallIcon(R.drawable.fab_shadow)// FIXME
                .setOngoing(true)

        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        //        resultIntent.setAction(MainActivity.ACTION_OPEN);

        val stackBuilder = TaskStackBuilder.create(this)

        stackBuilder.addParentStack(MainActivity::class.java)

        stackBuilder.addNextIntent(resultIntent)
        // TODO: check
        val resultPendingIntent = stackBuilder.getPendingIntent(
                MainActivity.INTENT_EDIT_TASK,
                PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder!!.setContentIntent(resultPendingIntent)
    }

    private fun closeNotification() {
        mBuilder!!.setOngoing(false)
        mNotifyManager!!.notify(NOTIFICATION_ID, mBuilder!!.build())
        mNotifyManager!!.cancel(TAG, NOTIFICATION_ID)
    }

    private fun startSound() {
        stopSound()

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

    private fun vibrate() {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(500)
    }

    companion object {

        private val TAG = "PomoService"
        var ACTION_START = "start"
        var ACTION_PAUSE = "pause"
        var ACTION_RESUME = "resume"
        var ACTION_STOP = "stop    "
        var ARG_TASK_ID = "TASK_ID"
        var ARG_TASK_TITLE = "TASK_TITLE"
        var ARG_TASK_DURATION_MS = "TASK_DURATION_MS"
        var ARG_TASK_REMAINING_TIME_MS = "TASK_REMAINING_TIME_MS"

        var NOTIFICATION_ID = 1
    }
}
