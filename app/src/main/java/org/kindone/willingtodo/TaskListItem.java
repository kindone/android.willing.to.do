package org.kindone.willingtodo;

/**
 * Created by kindone on 2015. 10. 16..
 */
public class TaskListItem {
    private long id;
    private String title;
    private String category;
    private String deadline;
    private boolean separator;

    public TaskListItem(Task task) {
        this.id = task.id;
        this.title = task.title;
        this.category = task.category;
        this.deadline = task.deadline;
        separator = false;
    }

    public TaskListItem(String separatorName) {
        title = separatorName;
        separator = true;
    }

    public long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getCategory() {
        return category;
    }

    public Task getTask() {
        return new Task(id, title, category, deadline, 0, 0);
    }

    public boolean isSeparator() {
        return separator;
    }
}
