package org.kindone.willingtodo.data;

/**
 * Created by kindone on 2015. 11. 7..
 */
public class Task {
    public final long id;
    public final String title;
    public final long contextId;
    public final String category;
    public final String deadline;
    public final int priority;
    public final int willingness;

    public Task(long id, String title, long contextId, String category, String deadline, int priority, int willingness) {
        this.id = id;
        this.title = title;
        this.contextId = contextId;
        this.category = category;
        this.deadline = deadline;
        this.priority = priority;
        this.willingness = willingness;
    }

    public String toString() {
        return "id = " + id +
                ", title = " + title +
                ", contextId = " + contextId +
                ", category = " + category +
                ", deadline = " + deadline +
                ", priority = " + priority +
                ", willingness = " + willingness;
    }
}
