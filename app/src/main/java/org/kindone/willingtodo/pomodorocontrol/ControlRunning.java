package org.kindone.willingtodo.pomodorocontrol;

/**
 * Created by kindone on 2017. 1. 2..
 */

public class ControlRunning extends ControlState {
    public ControlRunning(ControlContext context) {
        super(context);
    }

    @Override
    public void start() {
        // DO NOTHING
    }

    @Override
    public void pause() {
        changeState(new ControlPaused(mContext));
    }

    @Override
    public void resume() {
        // DO NOTHING
    }

    @Override
    public void stop() {
        changeState(new ControlStopped(mContext));
    }

    @Override
    public int getMode() {
        return MODE_PAUSED;
    }
}
