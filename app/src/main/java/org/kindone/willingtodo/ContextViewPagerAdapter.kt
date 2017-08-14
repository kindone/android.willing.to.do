package org.kindone.willingtodo

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import java.util.ArrayList

/**
 * Created by kindone on 2017. 1. 22..
 */


class ContextViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

    internal inner class AdapterElement(val contextId: Long, val fragment: Fragment, val title: String)

    private val adapterElements = ArrayList<AdapterElement>()

    fun addFragment(contextId: Long, fragment: Fragment, title: String) {
        adapterElements.add(AdapterElement(contextId, fragment, title))
    }



    override fun getCount(): Int {
        return adapterElements.size
    }

    fun clear() {
        adapterElements.clear()
    }



    override fun getPageTitle(position: Int): CharSequence {
        return adapterElements[position].title
    }

    fun getContextId(position: Int): Long {
        return adapterElements[position].contextId
    }

    override fun getItem(position: Int): Fragment {
        return adapterElements[position].fragment
    }

}
