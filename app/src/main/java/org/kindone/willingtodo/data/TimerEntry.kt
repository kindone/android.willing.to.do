package org.kindone.willingtodo.data

/**
 * Created by kindone on 2016. 12. 13..
 */

class TimerEntry(val id: Long, val taskId: Long,
                          val title: String, val durationMin: Int, val startedMs: Long,
                          val isPaused: Boolean, val elapsedMs: Long) {

    override fun toString(): String {
        return "id = " + id +
                ", taskId = " + taskId +
                ", title = " + title +
                ", durationMin = " + durationMin +
                ", startedMs = " + startedMs +
                ", isPaused = " + isPaused +
                ", elapsedMs = " + elapsedMs
    }
}
