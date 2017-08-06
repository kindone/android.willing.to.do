package org.kindone.willingtodo.pomodorocontrol

/**
 * Created by kindone on 2017. 1. 2..
 */

class ControlRunning(context: ControlContext) : ControlState(context) {

    override fun start() {
        // DO NOTHING
    }

    override fun pause() {
        changeState(ControlPaused(mContext))
    }

    override fun resume() {
        // DO NOTHING
    }

    override fun stop() {
        changeState(ControlStopped(mContext))
    }

    override fun getMode(): Int {
        return ControlState.MODE_PAUSED
    }
}
