package org.kindone.willingtodo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.kindone.willingtodo.pomodorotimer.StopWatch;

public class PomodoroTimerService extends Service{

    private static String TAG ="PomoService";
    public static String ACTION_START = "start";
    public static String ACTION_PAUSE = "pause";
    public static String ACTION_RESUME = "resume";
    public static String ACTION_STOP = "stop    ";
    public static String ARG_TASK_ID = "TASK_ID";
    public static String ARG_TASK_TITLE = "TASK_TITLE";
    public static String ARG_TASK_DURATION_MS = "TASK_DURATION_MS";
    public static String ARG_TASK_REMAINING_TIME_MS = "TASK_REMAINING_TIME_MS";

    public static int NOTIFICATION_ID = 1;

    private StopWatch mStopWatch;
    private String mTitle = "";
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private MediaPlayer mMediaPlayer;

    public PomodoroTimerService() {
        Log.v(TAG, "constructors");
        mStopWatch = new StopWatch(new StopWatch.TimerEventListener() {
            @Override
            public void onTick() {
                displayTick();
            }

            @Override
            public void onTimerStarted() {
                Log.v(TAG, "onTimerStarted");
                startSound();
            }

            @Override
            public void onTimerStopped() {
                Log.v(TAG, "onTimerStopped");
                stopSound();
            }

            @Override
            public void onTimerExpired() {
               Log.v(TAG, "onTimerExpired");
               vibrate();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        prepareNotification();

        if(action.equals(ACTION_START))
        {
            long taskId = intent.getLongExtra(ARG_TASK_ID, -1);
            mTitle = intent.getStringExtra(ARG_TASK_TITLE);
            long durationMs = intent.getLongExtra(ARG_TASK_DURATION_MS, 25*60*1000);
            mStopWatch.startTimer(durationMs);
        }
        else if(action.equals(ACTION_PAUSE))
        {
            long remainingTimeMs = intent.getLongExtra(ARG_TASK_REMAINING_TIME_MS, 0);
            mStopWatch.pauseTimer(remainingTimeMs);
        }
        else if(action.equals(ACTION_STOP))
        {
            mStopWatch.stopTimer();
            closeNotification();
        }
        else if(action.equals(ACTION_RESUME))
        {
            mStopWatch.resumeTimer();
        }

        if(!mStopWatch.isRunning())
            displayTick();

        return super.onStartCommand(intent, flags, startId);
    }

    public void displayTick() {
        Log.v(TAG,"beep: " + mTitle + " : " + mStopWatch.getRemainingTimeStr());
        mBuilder.setContentTitle(mTitle).setContentText(mStopWatch.getRemainingTimeStr());
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }

    private void prepareNotification()
    {
        mNotifyManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Pomodoro Timer Active") // TODO: replace with resource
                .setContentText(mTitle).setSmallIcon(R.drawable.fab_shadow)// FIXME
                .setOngoing(true);

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//        resultIntent.setAction(MainActivity.ACTION_OPEN);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        // TODO: check
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                MainActivity.INTENT_EDIT_TASK,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
    }

    private void closeNotification()
    {
        mBuilder.setOngoing(false);
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
        mNotifyManager.cancel(TAG, NOTIFICATION_ID);
    }

    private void startSound() {
        stopSound();

        mMediaPlayer = MediaPlayer.create(this, R.raw.ticking);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.start();
    }

    private void stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }
}
