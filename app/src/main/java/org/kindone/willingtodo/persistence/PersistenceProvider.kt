package org.kindone.willingtodo.persistence

/**
 * Created by kindone on 2016. 12. 20..
 */

interface PersistenceProvider {
    fun getVersion(): Int
    val taskContextPersistenceProvider: TaskContextPersistenceProvider
    val taskPersistenceProvider: TaskPersistenceProvider
    val configPersistenceProvider: ConfigPersistenceProvider
}
