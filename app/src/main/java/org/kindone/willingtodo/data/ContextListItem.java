package org.kindone.willingtodo.data;

import org.kindone.willingtodo.recyclerlist.RecyclerListItem;

/**
 * Created by kindone on 2016. 12. 25..
 */

public class ContextListItem implements RecyclerListItem{
    private TaskContext context;

    public ContextListItem(TaskContext context) {
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

    public TaskContext getContext() {

        return context;
    }
}
