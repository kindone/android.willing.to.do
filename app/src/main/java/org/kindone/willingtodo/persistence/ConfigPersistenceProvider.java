package org.kindone.willingtodo.persistence;

import org.kindone.willingtodo.data.TaskContext;

import java.util.List;

/**
 * Created by kindone on 2016. 12. 22..
 */

public interface ConfigPersistenceProvider {

    int getTabIndex();

    void saveTabIndex(int index);

    int getVersion();
}
