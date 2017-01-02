package org.kindone.willingtodo.pomodorocontrol;

/**
 * Created by kindone on 2017. 1. 2..
 */

abstract public class ControlState {
    final ControlContext mContext;

    public ControlState(ControlContext context) {
        mContext = context;
    }

    abstract public void start();

    abstract public void pause();

    abstract public void resume();

    abstract public void stop();

    public void changeState(ControlState newState) {
        mContext.changeState(newState);
    }

    private void throwUndefined() {
        throw new RuntimeException("undefined change of state");
    }
}
