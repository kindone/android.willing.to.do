package org.kindone.willingtodo.recyclerlist;

/**
 * Created by kindone on 2016. 12. 18..
 */

public interface RecyclerListItem {
    long getId();
    String getTitle();

    public interface Updater {
        RecyclerListItem update(RecyclerListItem item);
    }
}
