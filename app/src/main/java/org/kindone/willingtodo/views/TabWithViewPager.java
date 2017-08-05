package org.kindone.willingtodo.views;



import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.kindone.willingtodo.ContextViewPagerAdapter;

/**
 * Created by kindone on 2017. 1. 22..
 */

public class TabWithViewPager {
    private static final String TAG = "TabWithViewPager";

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabLayout.OnTabSelectedListener onTabEventListener;

    public static TabWithViewPager FromResources(ViewPager viewPager, TabLayout tabLayout, PagerAdapter adapter) {
        return new TabWithViewPager(viewPager, tabLayout, adapter);
    }

    private TabWithViewPager(ViewPager viewPager, TabLayout tabLayout, PagerAdapter adapter) {

        mTabLayout = tabLayout;
        mViewPager = viewPager;
        mViewPager.setAdapter(adapter);
        initTabLayout();
        // default tab listener
        onTabEventListener = new DoNothingOnTabEventListener();
    }


    private void dispatchOnTabSelectedEvent(TabLayout.Tab tab)
    {
        onTabEventListener.onTabSelected(tab);
    }

    private void dispatchOnTabUnselectedEvent(TabLayout.Tab tab)
    {
        onTabEventListener.onTabUnselected(tab);
    }

    private void dispatchOnTabReselectedEvent(TabLayout.Tab tab)
    {
        onTabEventListener.onTabReselected(tab);
    }


    private void initTabLayout() {

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(new OnTabEventListener());
    }

    public Fragment getCurrentFragment()
    {
        ContextViewPagerAdapter adapter = (ContextViewPagerAdapter) mViewPager.getAdapter();
        return adapter.getItem(getCurrentItemIdx());
    }

    public void setCurrentItemIdx(int idx)
    {
        mViewPager.setCurrentItem(idx);
    }

    public int getCurrentItemIdx()
    {
        return mViewPager.getCurrentItem();
    }

    public void setAdapter(FragmentPagerAdapter adapter)
    {
        mViewPager.setAdapter(adapter);
    }

    public PagerAdapter getAdapter()
    {
        return mViewPager.getAdapter();
    }

    public void setOnTabEventListener(TabLayout.OnTabSelectedListener listener) {
        onTabEventListener = listener;
    }


    class DoNothingOnTabEventListener implements TabLayout.OnTabSelectedListener
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {   }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {   }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {   }
    }

    class OnTabEventListener implements TabLayout.OnTabSelectedListener
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            Log.v(TAG, "tab selected:" +  tab.getPosition());
            setCurrentItemIdx(tab.getPosition());
            dispatchOnTabSelectedEvent(tab);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            Log.v(TAG, "tab unselected:" +  tab.getPosition());
            dispatchOnTabUnselectedEvent(tab);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            Log.v(TAG, "tab reselected:" +  tab.getPosition());
            setCurrentItemIdx(tab.getPosition());
            dispatchOnTabReselectedEvent(tab);
        }
    }


}
