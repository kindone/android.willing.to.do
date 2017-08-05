package org.kindone.willingtodo.pomodorotimer;

/**
 * Created by kindone on 2017. 1. 1..
 */

public class TimerStopped extends TimerState {

    public TimerStopped(TimerContext context) {
        super(context);
    }

    @Override
    public boolean start() {
        changeStateToRunningWithInheritedDuration();
        return CHANGED;
    }

    @Override
    public boolean stop() {
        // DO NOTHING
        return UNCHANGED;
    }

    @Override
    public boolean resume() {
        // DO NOTHING
        return UNCHANGED;
    }

    @Override
    public boolean pause() {
        // DO NOTHING
        return UNCHANGED;
    }

    public boolean isRunning() {
        return false;
    }

    public boolean isStopped() { return true; }

    @Override
    public long getRemainingTimeMs() {
        return 0;
    }

    private void changeStateToRunningWithInheritedDuration() {
        changeState(new TimerRunning(mContext, mContext.getDurationMs()));
    }
}
