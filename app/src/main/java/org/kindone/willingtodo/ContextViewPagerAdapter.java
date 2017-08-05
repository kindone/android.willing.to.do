package org.kindone.willingtodo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kindone on 2017. 1. 22..
 */


public class ContextViewPagerAdapter extends FragmentPagerAdapter {
    class AdapterElement {
        public final long contextId;
        public final Fragment fragment;
        public final String title;
        AdapterElement(long contextId, Fragment fragment, String title) {
            this.contextId = contextId;
            this.fragment = fragment;
            this.title = title;
        }
    }
    private final List<AdapterElement> adapterElements = new ArrayList<>();

    public ContextViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    public void addFragment(long contextId, Fragment fragment, String title) {
        adapterElements.add(new AdapterElement(contextId, fragment, title));
    }

    @Override
    public int getCount() {
        return adapterElements.size();
    }

    public void clear() {
        adapterElements.clear();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return adapterElements.get(position).title;
    }


    public long getContextId(int position) {
        return adapterElements.get(position).contextId;
    }

    @Override
    public Fragment getItem(int position) {
        return adapterElements.get(position).fragment;
    }


}
