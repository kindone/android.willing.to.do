package org.kindone.willingtodo.data;

import org.kindone.willingtodo.recyclerlist.RecyclerListItem;

/**
 * Created by kindone on 2015. 10. 16..
 */
public class TaskListItem implements RecyclerListItem{
    private Task task;

    public TaskListItem(Task task) {
        this.task = task;
    }

    public long getId() {

        return task.id;
    }
    public String getTitle() {
        return task.title;
    }

    public Task getTask() {

        return task;
    }
}
