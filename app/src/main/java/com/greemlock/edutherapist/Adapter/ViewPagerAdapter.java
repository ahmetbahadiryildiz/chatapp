package com.greemlock.edutherapist.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFragment;
    private ArrayList<String> mNameList;

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        mFragment = new ArrayList<>();
        mNameList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragment.get(position);
    }

    @Override
    public int getCount() {
        return mFragment.size();
    }


    public void addFragment(Fragment fragment, String header){
        mFragment.add(fragment);
        mNameList.add(header);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mNameList.get(position);
    }

}
