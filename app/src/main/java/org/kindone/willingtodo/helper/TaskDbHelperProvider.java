package org.kindone.willingtodo.helper;

import org.kindone.willingtodo.TaskDbHelper;

/**
 * Created by kindone on 2015. 11. 8..
 */
public interface TaskDbHelperProvider {
    TaskDbHelper getTaskDbHelper();
}
