package org.kindone.willingtodo.persistence;

/**
 * Created by kindone on 2016. 12. 20..
 */

public interface PersistenceProvider {
    int getVersion();
    TaskContextPersistenceProvider getTaskContextPersistenceProvider();
    TaskPersistenceProvider getTaskPersistenceProvider();
    ConfigPersistenceProvider getConfigPersistenceProvider();
}
