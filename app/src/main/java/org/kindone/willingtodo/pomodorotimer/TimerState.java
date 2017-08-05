package org.kindone.willingtodo.pomodorotimer;

import java.util.concurrent.TimeUnit;

/**
 * Created by kindone on 2017. 1. 1..
 */

public abstract class TimerState {

    final TimerContext mContext;
    final protected static boolean CHANGED = true;
    final protected static boolean UNCHANGED = false;

    public TimerState(TimerContext context) {
        mContext = context;
    }

    // boolean states whether the state has changed
    abstract public boolean start();

    abstract public boolean pause();

    abstract public boolean resume();

    abstract public boolean stop();

    public void changeState(TimerState newState) {
        mContext.changeState(newState);
    }


    protected String getRemainingTimeStr(long remainingTime) {
        long time = remainingTime >= 0 ? remainingTime : 0;
        if(time == 0)
            return "Stopped";
        else
            return mm_ss_formattedRemainingTimeStr(time);
    }

    abstract public boolean isRunning();

    abstract public boolean isStopped();

    public String getRemainingTimeStr() {
        return getRemainingTimeStr(getRemainingTimeMs());
    }

    abstract public long getRemainingTimeMs();

    public long getStartedTimeMs() { return -1L; }

    private void throwUndefined() {
        throw new RuntimeException("undefined change of state");
    }

    private String mm_ss_formattedRemainingTimeStr(long time)
    {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }
}
