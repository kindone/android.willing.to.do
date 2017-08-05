package org.kindone.willingtodo.pomodorotimer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by kindone on 2017. 1. 11..
 */

public class StopWatch implements TimerContext{

    private TimerState mTimerState;

    private long mDurationMs;

    private ScheduledFuture<?> mTickerHandle;

    private TimerEventListener mTimerEventListener = null;

    private final long BUFFER_TIME_MS = 1000;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);


    // advanced timer events
    public StopWatch(TimerEventListener timerEventListener) {
        setInitialTimerState();
        mTimerEventListener = timerEventListener;
    }

    // simple tick event only
    public StopWatch(Runnable onTick) {
        setInitialTimerState();
        mTimerEventListener = createTimerEventListener(onTick);
    }

    public void startTimer(long newDurationMs) {
        mDurationMs = newDurationMs;
        boolean changedState = setRunningTimerState();
        startBackgroundTicking(mDurationMs + BUFFER_TIME_MS);

        if(changedState)
            dispatchTimerStartedEvent();
    }

    public void restartTimer(long newDurationMs, long newStartedTimeMs) {
        setRunningTimerState(newDurationMs, newStartedTimeMs);
        mDurationMs = getRemainingTimeMs();
        startBackgroundTicking(mDurationMs + BUFFER_TIME_MS);
        dispatchTimerStartedEvent();
    }

    public void pauseTimer() {
        boolean changedState = setPausedTimerState();
        if(changedState)
            dispatchTimerStoppedEvent();
    }

    public void pauseTimer(long newRemainingTimeMs) {
        boolean changedState = setPausedTimerState(newRemainingTimeMs);
        if(changedState)
            dispatchTimerStoppedEvent();
    }

    public void resumeTimer() {
        boolean changedState = setResumedTimerState();
        startBackgroundTicking(getRemainingTimeMs() + BUFFER_TIME_MS);

        if(changedState)
            dispatchTimerStartedEvent();
    }

    public void stopTimer() {
        boolean changedState = setStoppedTimerState();
        if(changedState)
            dispatchTimerStoppedEvent();
    }

    public void cleanUp() {
        stopTimer();
        stopBackgroundTicking();
    }



    public boolean isRunning() { return mTimerState.isRunning();}

    public boolean isStopped() { return mTimerState.isStopped();}



    public long getDurationMs() {
        return mDurationMs;
    }

    public long getStartedTimeMs() {
        return mTimerState.getStartedTimeMs();
    }

    public long getRemainingTimeMs() {
        return mTimerState.getRemainingTimeMs();
    }

    public String getRemainingTimeStr() {
        String str;
        synchronized (mTimerState) {
            str = mTimerState.getRemainingTimeStr();
        }
        return str;
    }



    @Override
    public void changeState(TimerState state) {
        synchronized (mTimerState) {
            mTimerState = state;
        }
    }



    private void setInitialTimerState()
    {
        mTimerState = new TimerInitial(this);
    }

    private void setRunningTimerState(long durationMs, long startedTimeMs)
    {
        mTimerState = new TimerRunning(this, durationMs, startedTimeMs);
    }

    private boolean setRunningTimerState()
    {
        return mTimerState.start();
    }

    private boolean setPausedTimerState(long newRemainingTimeMs)
    {
        boolean wasRunning = mTimerState.isRunning();
        mTimerState = new TimerPaused(this, newRemainingTimeMs);
        return wasRunning;
    }

    private boolean setPausedTimerState()
    {
        return mTimerState.pause();
    }

    private boolean setResumedTimerState()
    {
        return mTimerState.resume();
    }

    private boolean setStoppedTimerState()
    {
        return mTimerState.stop();
    }


    private void startBackgroundTicking(long maxDurationMs) {
        stopBackgroundTicking();
        mTickerHandle = scheduleTimerTick();
        scheduleTickerExpiry(maxDurationMs);
    }

    private void stopBackgroundTicking() {
        if(isTickerActive()) {
            cancelTicker();
        }
    }



    private ScheduledFuture<?> scheduleTimerTick()
    {
        Runnable scheduledJob = new Ticker();
        long initialDelay = 500;
        long period = 500;
        return scheduler.scheduleAtFixedRate(scheduledJob, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    private void scheduleTickerExpiry(long maxDurationMs)
    {
        Runnable scheduledJob = new ExpireTick();
        scheduler.schedule(scheduledJob, maxDurationMs, TimeUnit.MILLISECONDS);
    }



    private boolean isTickerActive()
    {
        return mTickerHandle != null && !mTickerHandle.isCancelled();
    }

    private void cancelTicker()
    {
        mTickerHandle.cancel(true);
    }



    private void dispatchTimerStartedEvent()
    {
        mTimerEventListener.onTimerStarted();
    }

    private void dispatchTimerStoppedEvent()
    {
        mTimerEventListener.onTimerStopped();
    }

    private void dispatchTimerTickEvent()
    {
        mTimerEventListener.onTick();
    }

    private void dispatchTimerExpiredEvent()
    {
        mTimerEventListener.onTimerExpired();
    }


    private TimerEventListener createTimerEventListener(final Runnable runOnTick)
    {
        return new TimerEventListener() {

            @Override
            public void onTick() {
                runOnTick.run();
            }

            @Override
            public void onTimerStarted() { }

            @Override
            public void onTimerStopped() { }

            @Override
            public void onTimerExpired() { }
        };
    }


    public interface TimerEventListener {
        void onTick();

        void onTimerStarted();

        void onTimerStopped();

        void onTimerExpired();
    }


    class Ticker implements Runnable {
        public void run() {
            if(!isRunning()) {
                stopBackgroundTicking();
            }
            if(getRemainingTimeMs() <= 0)
            {
                stopTimer();
            }

            dispatchTimerTickEvent();
        }
    }


    class ExpireTick implements Runnable {
        public void run() {
            stopBackgroundTicking();
            dispatchTimerExpiredEvent();
        }
    }
}
