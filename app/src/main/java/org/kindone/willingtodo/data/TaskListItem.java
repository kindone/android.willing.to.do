package org.kindone.willingtodo.data;

/**
 * Created by kindone on 2015. 10. 16..
 */
public class TaskListItem {
    private Task task;
    private String separatorName;
    private boolean separator;

    public TaskListItem(Task task) {
        this.task = task;
        separator = false;
    }

    public TaskListItem(String separatorName) {
        this.task = null;
        this.separatorName = separatorName;
        separator = true;
    }

    public long getId() {
        assert(!isSeparator());
        return task.id;
    }
    public String getTitle() {
        if(task != null) {
            assert(!isSeparator());
            return task.title;
        }
        else {
            assert(isSeparator());
            return separatorName;
        }
    }
    public String getCategory() {
        assert(!isSeparator());
        return task.category;
    }
    public Task getTask() {
        assert(!isSeparator());
        return task;
    }
    public boolean isSeparator() {
        return separator;
    }
}
