package org.kindone.willingtodo.recyclerlist

import org.kindone.willingtodo.event.Event

/**
 * Created by kindone on 2017. 8. 14..
 */

data class RecyclerListItemCreateEvent(val item:RecyclerListItem) : Event

data class RecyclerListItemChangeEvent(val item:RecyclerListItem) : Event

data class RecyclerListItemRemoveEvent(val itemId:Long) : Event

data class RecyclerListItemSwapEvent(val itemId1:Long, val itemId2:Long) : Event