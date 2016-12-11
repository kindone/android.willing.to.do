package org.kindone.willingtodo.helper

import org.kindone.willingtodo.DbHelper

/**
 * Created by kindone on 2015. 11. 8..
 */
interface TaskDbHelperProvider {
    fun getTaskDbHelper(): DbHelper
}
