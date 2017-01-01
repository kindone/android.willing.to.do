package org.kindone.willingtodo.pomodorotimer;

/**
 * Created by kindone on 2017. 1. 1..
 */

public class TimerRunning extends TimerState {

    final private long mRemainingMs;
    final private long mStartedMs;

    public TimerRunning(TimerContext context, long remainingMs) {
        super(context);
        mStartedMs = System.currentTimeMillis();
        mRemainingMs = remainingMs;
    }

    public void pause() {
        long elapsedMs = System.currentTimeMillis() - mStartedMs;
        long remainingMs = mRemainingMs - elapsedMs;
        changeState(new TimerPaused(mContext, remainingMs));
    }

    public void start() {
        // DO NOTHING
    }

    public void resume() {
        // DO NOTHING
    }

    public void stop() {
        changeState(new TimerStopped(mContext));
    }

    @Override
    public long getRemainingTimeMs() {
        long elapsedMs = System.currentTimeMillis() - mStartedMs;
        long remainingMs = mRemainingMs - elapsedMs;
        return remainingMs > 0 ? remainingMs : 0;
    }

    public boolean isRunning() {
        return true;
    }
}
