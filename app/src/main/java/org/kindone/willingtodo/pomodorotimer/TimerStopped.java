package org.kindone.willingtodo.pomodorotimer;

/**
 * Created by kindone on 2017. 1. 1..
 */

public class TimerStopped extends TimerState {

    public TimerStopped(TimerContext context) {
        super(context);
    }

    public void start() {
        changeState(new TimerRunning(mContext, mContext.getDurationMs()));
    }

    public void stop() {
        // DO NOTHING
    }

    public void resume() {
        // DO NOTHING
    }

    public void pause() {
        // DO NOTHING
    }

    @Override
    public long getRemainingTimeMs() {
        return 0;
    }
}
