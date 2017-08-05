package org.kindone.willingtodo.pomodorotimer;

/**
 * Created by kindone on 2017. 1. 1..
 */

public class TimerPaused extends TimerState {
    final private long mRemainingMs;

    public TimerPaused(TimerContext context, long remainingMs) {
        super(context);
        mRemainingMs = remainingMs;
    }

    @Override
    public boolean start() {
        resume();
        return CHANGED;
    }

    @Override
    public boolean pause() {
        // DO NOTHING
        return UNCHANGED;
    }

    @Override
    public boolean resume() {
        changeState(new TimerRunning(mContext, mRemainingMs));
        return CHANGED;
    }

    @Override
    public boolean stop() {
        changeState(new TimerStopped(mContext));
        return UNCHANGED;
    }

    @Override
    public long getRemainingTimeMs() {
        return mRemainingMs > 0 ? mRemainingMs : 0;
    }

    public boolean isRunning() {
        return false;
    }

    public boolean isStopped() {
        return false;
    }
}
