package org.kindone.willingtodo.data;

/**
 * Created by kindone on 2016. 12. 13..
 */

class TimerEntry {

    public final Long id;
    public final Long taskId;
    public final String title;
    public final int durationMin;
    public final Long startedMs;
    public final boolean isPaused;
    public final Long elapsedMs;

    public TimerEntry(Long id, Long taskId, String title, int durationMin, Long startedMs, boolean isPaused, Long elapsedMs)
    {
        this.id = id;
        this.taskId = taskId;
        this.title = title;
        this.durationMin = durationMin;
        this.startedMs = startedMs;
        this.isPaused = isPaused;
        this.elapsedMs = elapsedMs;
    }

    public @Override String toString() {
        return "id = " + id +
                ", taskId = " + taskId +
                ", title = " + title +
                ", durationMin = " + durationMin +
                ", startedMs = " + startedMs +
                ", isPaused = " + isPaused +
                ", elapsedMs = " + elapsedMs;
    }
}
