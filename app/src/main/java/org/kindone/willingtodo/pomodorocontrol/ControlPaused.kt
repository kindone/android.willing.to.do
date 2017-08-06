package org.kindone.willingtodo.pomodorocontrol

/**
 * Created by kindone on 2017. 1. 2..
 */

class ControlPaused(context: ControlContext) : ControlState(context) {

    override fun start() {
        resume()
    }

    override fun pause() {
        // DO_NOTHING
    }

    override fun resume() {
        changeState(ControlRunning(mContext))
    }

    override fun stop() {
        changeState(ControlStopped(mContext))
    }

    override fun getMode(): Int {
        return ControlState.MODE_PLAYING
    }
}
