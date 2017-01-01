package org.kindone.willingtodo.pomodorotimer;

/**
 * Created by kindone on 2017. 1. 1..
 */

public interface TimerContext {
    long getDurationMs();
    void changeState(TimerState state);
}
