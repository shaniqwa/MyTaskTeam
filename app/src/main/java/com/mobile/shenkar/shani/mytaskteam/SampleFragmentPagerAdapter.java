package com.mobile.shenkar.shani.mytaskteam;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Shani on 12/16/15.
 */
public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "All", "Done" };
    private Context context;
    private List<PageFragment> lst;
    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        lst = new ArrayList<PageFragment>();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        PageFragment temp =PageFragment.newInstance(position + 1);
        lst.add(temp);
        return temp;
    }

    public void refreshAll() {
        for (PageFragment p : lst) {
            p.check();
        }
    }

    public void sortByDate(){
        for (PageFragment p : lst) {
            p.sortListbyDate();
        }
    }

    public void sortByPriority(){
        for (PageFragment p : lst) {
            p.sortListbyPriority();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}


