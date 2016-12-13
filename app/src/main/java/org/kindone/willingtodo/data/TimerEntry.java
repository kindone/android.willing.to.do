package org.kindone.willingtodo.data;

/**
 * Created by kindone on 2016. 12. 13..
 */

class TimerEntry {

    Long id;
    Long taskId;
    String title;
    int durationMin;
    Long startedMs;
    boolean isPaused;
    Long elapsedMs;

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

    public Long getId() { return id; }
    public Long getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public int getDurationMin() { return durationMin; }
    public Long getStartedMs() { return startedMs; }
    public boolean getIsPaused() { return isPaused; }
    public Long getElapsedMs() { return elapsedMs; }

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
