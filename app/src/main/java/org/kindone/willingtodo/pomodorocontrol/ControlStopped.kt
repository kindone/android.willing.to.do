package org.kindone.willingtodo.pomodorocontrol

/**
 * Created by kindone on 2017. 1. 2..
 */

class ControlStopped(context: ControlContext) : ControlState(context) {

    override fun start() {
        changeState(ControlRunning(mContext))
    }

    override fun pause() {
        // DO NOTHING
    }

    override fun resume() {
        // DO NOTHING
    }

    override fun stop() {
        // DO NOTHING
    }

    override fun getMode(): Int {
        return ControlState.MODE_PLAYING
    }
}
