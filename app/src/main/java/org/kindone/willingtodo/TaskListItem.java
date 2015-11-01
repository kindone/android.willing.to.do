package org.kindone.willingtodo;

/**
 * Created by kindone on 2015. 10. 16..
 */
public class TaskListItem {
    private String title;
    private String category;
    private boolean separator;

    public TaskListItem(String title, String category) {
        this.title = title;
        this.category = category;
        separator = false;
    }

    public TaskListItem(String separatorName) {
        title = separatorName;
        separator = true;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public boolean isSeparator() {
        return separator;
    }
}
