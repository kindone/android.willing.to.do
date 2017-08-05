package org.kindone.willingtodo.pomodorotimer;

/**
 * Created by kindone on 2017. 1. 1..
 */

public class TimerRunning extends TimerState {

    final private long mDurationMs;
    final private long mStartedMs;

    public TimerRunning(TimerContext context, long durationMs) {
        super(context);
        mStartedMs = System.currentTimeMillis();
        mDurationMs = durationMs;
    }

    public TimerRunning(TimerContext context, long durationMs, long startedMs) {
        super(context);
        mStartedMs = startedMs;
        mDurationMs = durationMs;
    }

    @Override
    public boolean pause() {
        long elapsedMs = System.currentTimeMillis() - mStartedMs;
        long remainingMs = mDurationMs - elapsedMs;
        changeState(new TimerPaused(mContext, remainingMs));
        return CHANGED;
    }

    @Override
    public boolean start() {
        // DO NOTHING
        return UNCHANGED;
    }

    @Override
    public boolean resume() {
        // DO NOTHING
        return UNCHANGED;
    }

    @Override
    public boolean stop() {
        changeState(new TimerStopped(mContext));
        return CHANGED;
    }

    @Override
    public long getRemainingTimeMs() {
        long elapsedMs = System.currentTimeMillis() - mStartedMs;
        long remainingMs = mDurationMs - elapsedMs;
        return remainingMs > 0 ? remainingMs : 0;
    }

    public long getStartedTimeMs() { return mStartedMs; }

    public boolean isRunning() {
        return true;
    }

    public boolean isStopped() {
        return false;
    }
}
