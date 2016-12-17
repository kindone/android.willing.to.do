package org.kindone.willingtodo.data;

/**
 * Created by kindone on 2016. 12. 13..
 */

public class TaskContext {
    public final long id;
    public final String name;
    public final int position;
    public final int mode;

    public TaskContext(long id, String name, int position, int mode) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.mode = mode;
    }

}
