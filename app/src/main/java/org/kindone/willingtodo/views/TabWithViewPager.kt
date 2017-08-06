package org.kindone.willingtodo.views


import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log

import org.kindone.willingtodo.ContextViewPagerAdapter

/**
 * Created by kindone on 2017. 1. 22..
 */

class TabWithViewPager private constructor(private val mViewPager: ViewPager, private val mTabLayout: TabLayout, adapter: PagerAdapter) {
    private var onTabEventListener: TabLayout.OnTabSelectedListener? = null

    init {
        mViewPager.adapter = adapter
        initTabLayout()
        // default tab listener
        onTabEventListener = DoNothingOnTabEventListener()
    }


    private fun dispatchOnTabSelectedEvent(tab: TabLayout.Tab) {
        onTabEventListener!!.onTabSelected(tab)
    }

    private fun dispatchOnTabUnselectedEvent(tab: TabLayout.Tab) {
        onTabEventListener!!.onTabUnselected(tab)
    }

    private fun dispatchOnTabReselectedEvent(tab: TabLayout.Tab) {
        onTabEventListener!!.onTabReselected(tab)
    }


    private fun initTabLayout() {

        mTabLayout.setupWithViewPager(mViewPager)
        mTabLayout.setOnTabSelectedListener(OnTabEventListener())
    }

    val currentFragment: Fragment
        get() {
            val adapter = mViewPager.adapter as ContextViewPagerAdapter
            return adapter.getItem(currentItemIdx)
        }

    var currentItemIdx: Int
        get() = mViewPager.currentItem
        set(idx) {
            mViewPager.currentItem = idx
        }

    fun setAdapter(adapter: FragmentPagerAdapter) {
        mViewPager.adapter = adapter
    }

    val adapter: PagerAdapter
        get() = mViewPager.adapter

    fun setOnTabEventListener(listener: TabLayout.OnTabSelectedListener) {
        onTabEventListener = listener
    }


    internal inner class DoNothingOnTabEventListener : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {}

        override fun onTabUnselected(tab: TabLayout.Tab) {}

        override fun onTabReselected(tab: TabLayout.Tab) {}
    }

    internal inner class OnTabEventListener : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            Log.v(TAG, "tab selected:" + tab.position)
            currentItemIdx = tab.position
            dispatchOnTabSelectedEvent(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            Log.v(TAG, "tab unselected:" + tab.position)
            dispatchOnTabUnselectedEvent(tab)
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            Log.v(TAG, "tab reselected:" + tab.position)
            currentItemIdx = tab.position
            dispatchOnTabReselectedEvent(tab)
        }
    }

    companion object {
        private val TAG = "TabWithViewPager"

        fun FromResources(viewPager: ViewPager, tabLayout: TabLayout, adapter: PagerAdapter): TabWithViewPager {
            return TabWithViewPager(viewPager, tabLayout, adapter)
        }
    }


}
