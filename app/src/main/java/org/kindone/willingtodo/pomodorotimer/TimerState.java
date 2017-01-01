package org.kindone.willingtodo.pomodorotimer;

import java.util.concurrent.TimeUnit;

/**
 * Created by kindone on 2017. 1. 1..
 */

public abstract class TimerState {

    final TimerContext mContext;

    public TimerState(TimerContext context) {
        mContext = context;
    }

    public void start() {
        throwUndefined();
    }

    public void pause() {
        throwUndefined();
    }

    public void resume() {
        throwUndefined();
    }

    public void stop() {
        throwUndefined();
    }

    public void changeState(TimerState newState) {
        mContext.changeState(newState);
    }

    private void throwUndefined() {
        throw new RuntimeException("undefined change of state");
    }


    protected String getRemainingTimeStr(long remainingTime) {
        long time = remainingTime >= 0 ? remainingTime : 0;
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }

    public boolean isRunning() {
        return false;
    }


    public String getRemainingTimeStr() {
        return getRemainingTimeStr(getRemainingTimeMs());
    }

    abstract public long getRemainingTimeMs();
}
