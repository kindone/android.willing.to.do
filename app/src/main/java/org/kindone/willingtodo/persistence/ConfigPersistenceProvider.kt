package org.kindone.willingtodo.persistence

import org.kindone.willingtodo.data.TaskContext

/**
 * Created by kindone on 2016. 12. 22..
 */

interface ConfigPersistenceProvider {

    val tabIndex: Int

    fun saveTabIndex(index: Int)

    val version: Int
}
