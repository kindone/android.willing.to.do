package org.kindone.willingtodo.pomodorocontrol;

/**
 * Created by kindone on 2017. 1. 2..
 */

public class ControlStopped extends ControlState {
    public ControlStopped(ControlContext context) {
        super(context);
    }

    @Override
    public void start() {
        changeState(new ControlRunning(mContext));
    }

    @Override
    public void pause() {
        // DO NOTHING
    }

    @Override
    public void resume() {
        // DO NOTHING
    }

    @Override
    public void stop() {
        // DO NOTHING
    }
}
