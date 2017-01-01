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

    public void start() {
        resume();
    }

    public void pause() {
        // DO NOTHING
    }

    public void resume() {
        changeState(new TimerRunning(mContext, mRemainingMs));
    }
    public void stop() {
        changeState(new TimerStopped(mContext));
    }

    @Override
    public long getRemainingTimeMs() {
        return mRemainingMs > 0 ? mRemainingMs : 0;
    }
}
