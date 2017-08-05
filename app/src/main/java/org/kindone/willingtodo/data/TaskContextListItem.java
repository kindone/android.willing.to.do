package org.kindone.willingtodo.data;

import org.kindone.willingtodo.recyclerlist.RecyclerListItem;

/**
 * Created by kindone on 2016. 12. 25..
 */

public class TaskContextListItem implements RecyclerListItem{
    private TaskContext context;

    public TaskContextListItem(TaskContext context) {
        this.context = context;
    }


    @Override
    public long getId() {
        return context.id;
    }

    @Override
    public String getTitle() {
        return context.name;
    }

    public TaskContext getTaskContext() {
        return context;
    }
}
