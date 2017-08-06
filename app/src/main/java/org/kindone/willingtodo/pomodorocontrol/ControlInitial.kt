package org.kindone.willingtodo.pomodorocontrol

/**
 * Created by kindone on 2017. 1. 2..
 */

class ControlInitial(context: ControlContext) : ControlState(context) {

    override fun start() {
        changeState(ControlRunning(mContext))
    }

    override fun pause() {
        // DO_NOTHING
    }

    override fun resume() {
        // DO_NOTHING
    }

    override fun stop() {
        // DO_NOTHING
    }

    override fun getMode(): Int {
        return ControlState.MODE_PLAYING
    }
}
