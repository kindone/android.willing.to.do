package org.kindone.willingtodo.pomodorotimer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PomodoroTimerService extends Service implements TimerContext{

    public static String ACTION_START = "start";
    public static String ACTION_PAUSE = "pause";
    public static String ACTION_RESUME = "resume";
    public static String ACTION_STOP = "stop    ";
    public static String ARG_TASK_ID = "TASK_ID";
    public static String ARG_TASK_TITLE = "TASK_TITLE";
    public static String ARG_TASK_DURATION_MS = "TASK_DURATION_MS";

    private TimerState mTimerState;
    private String mTitle;
    private long mDurationMs;
    ScheduledFuture<?> mBeeperHandle;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public PomodoroTimerService() {
        mTimerState = new TimerInitial(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();
        long taskId = intent.getLongExtra(ARG_TASK_ID, -1);
        mTitle = intent.getStringExtra(ARG_TASK_TITLE);
        mDurationMs = intent.getLongExtra(ARG_TASK_DURATION_MS, 25*60*1000);

        if(action.equals(ACTION_START))
        {
            startTimer();
        }
        else if(action.equals(ACTION_PAUSE))
        {
            pauseTimer();
        }
        else if(action.equals(ACTION_STOP))
        {
            stopTimer();
        }
        else if(action.equals(ACTION_RESUME))
        {
            resumeTimer();
        }

        if(mTimerState.isRunning())
            startTicking(getRemainingTimeMs()+1000);
        else
            displayTick();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void startTimer() {
        mTimerState.start();
    }

    public void pauseTimer() {
        mTimerState.pause();
    }

    public void resumeTimer() {
        mTimerState.resume();
    }

    public void stopTimer() {
        mTimerState.stop();
    }

    public String getRemainingTimeStr() {
        String str;
        synchronized (mTimerState) {
            str = mTimerState.getRemainingTimeStr();
        }
        return str;
    }

    public long getDurationMs() {
        return mDurationMs;
    }

    public long getRemainingTimeMs() {
        return mTimerState.getRemainingTimeMs();
    }

    public void startTicking(long maxDurationMs) {
        mBeeperHandle = scheduler.scheduleAtFixedRate(new Beeper(), 500, 500, TimeUnit.MILLISECONDS);
        scheduler.schedule(new CancelBeeper(), maxDurationMs, TimeUnit.MILLISECONDS);
    }

    public void stopTicking() {
        if(mBeeperHandle != null && !mBeeperHandle.isCancelled())
            mBeeperHandle.cancel(true);
    }

    @Override
    public void changeState(TimerState state) {
        synchronized (mTimerState) {
            mTimerState = state;
        }
    }

    private void displayTick() {
        Log.v("PomodoroTimer","beep: " + mTitle + " : " + getRemainingTimeStr());
    }

    class Beeper implements Runnable {
        public void run() {
            if(getRemainingTimeMs() >= 0)
                displayTick();
            else
                stopTimer();
        }
    }

    class CancelBeeper implements Runnable {
        public void run() {
            mBeeperHandle.cancel(true);
        }
    }


}
