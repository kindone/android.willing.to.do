package org.kindone.willingtodo.persistence;

import org.kindone.willingtodo.persistence.TaskContextProvider;
import org.kindone.willingtodo.persistence.TaskProvider;

/**
 * Created by kindone on 2016. 12. 20..
 */

public interface PersistenceProvider {
    int getVersion();
    TaskContextProvider getContextProvider();
    TaskProvider getTaskProvider();
    ConfigProvider getConfigProvider();
}
