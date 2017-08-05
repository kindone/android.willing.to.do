package org.kindone.willingtodo.pomodorotimer;

/**
 * Created by kindone on 2017. 1. 1..
 */

public class TimerInitial extends TimerState {

    public TimerInitial(TimerContext context) {
        super(context);
    }

    @Override
    public boolean start() {
        changeState(new TimerRunning(mContext, mContext.getDurationMs()));
        return CHANGED;
    }

    @Override
    public boolean stop() {
        // do nothing
        return UNCHANGED;
    }

    @Override
    public boolean pause() {
        // do nothing
        return UNCHANGED;
    }

    @Override
    public boolean resume() {
        // do nothing
        return UNCHANGED;
    }

    @Override
    public long getRemainingTimeMs() {
        return mContext.getDurationMs();
    }

    public boolean isRunning() {
        return false;
    }

    public boolean isStopped() {
        return false;
    }
}
