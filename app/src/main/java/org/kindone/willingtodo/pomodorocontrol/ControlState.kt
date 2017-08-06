package org.kindone.willingtodo.pomodorocontrol

/**
 * Created by kindone on 2017. 1. 2..
 */

abstract class ControlState(protected val mContext: ControlContext) {

    abstract fun start()

    abstract fun pause()

    abstract fun resume()

    abstract fun stop()

    fun changeState(newState: ControlState) {
        mContext.changeState(newState)
    }

    private fun throwUndefined() {
        throw RuntimeException("undefined change of state")
    }

    abstract fun getMode(): Int

    companion object {
        val MODE_STOPPED = 1
        val MODE_PAUSED = 1
        val MODE_PLAYING = 1
    }
}
