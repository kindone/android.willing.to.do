package org.kindone.willingtodo.pomodorotimer;

/**
 * Created by kindone on 2017. 1. 1..
 */

public class TimerInitial extends TimerState {

    public TimerInitial(TimerContext context) {
        super(context);
    }

    @Override
    public void start() {
        changeState(new TimerRunning(mContext, mContext.getDurationMs()));
    }

    @Override
    public long getRemainingTimeMs() {
        return mContext.getDurationMs();
    }
}
