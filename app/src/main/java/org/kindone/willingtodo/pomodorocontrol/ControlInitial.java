package org.kindone.willingtodo.pomodorocontrol;

/**
 * Created by kindone on 2017. 1. 2..
 */

public class ControlInitial extends ControlState {
    public ControlInitial(ControlContext context) {
        super(context);
    }

    @Override
    public void start() {
        changeState(new ControlRunning(mContext));
    }

    @Override
    public void pause() {
        // DO_NOTHING
    }

    @Override
    public void resume() {
        // DO_NOTHING
    }

    @Override
    public void stop() {
        // DO_NOTHING
    }

    @Override
    public int getMode() {
        return MODE_PLAYING;
    }
}
