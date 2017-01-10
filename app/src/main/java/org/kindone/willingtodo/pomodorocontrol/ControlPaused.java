package org.kindone.willingtodo.pomodorocontrol;

/**
 * Created by kindone on 2017. 1. 2..
 */

public class ControlPaused extends ControlState {
    public ControlPaused(ControlContext context) {
        super(context);
    }

    @Override
    public void start() {
        resume();
    }

    @Override
    public void pause() {
        // DO_NOTHING
    }

    @Override
    public void resume() {
        changeState(new ControlRunning(mContext));
    }

    @Override
    public void stop() {
        changeState(new ControlStopped(mContext));
    }

    @Override
    public int getMode() {
        return MODE_PLAYING;
    }
}
