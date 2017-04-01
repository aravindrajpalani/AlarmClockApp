package com.sleep.shortsleepalarm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sleep.shortsleepalarm.R;
import com.sleep.shortsleepalarm.listeners.CreateAlarmOpened;
import com.sleep.shortsleepalarm.listeners.FromHomeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aravindraj on 9/6/2016.
 */
public class HomeFragment extends Fragment implements CreateAlarmOpened {
    ViewPager viewPager;
    TabLayout tabLayout;
    FromHomeListener lissy;


    public void setListener(FromHomeListener fromlis) {
        this.lissy = fromlis;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        AlarmFragment afrag = new AlarmFragment();
        afrag.setListener(this);
        SleepAlarmFragment safrag = new SleepAlarmFragment();
        safrag.setListener(this);
        adapter.addFragment(afrag, "Alarm");
        adapter.addFragment(safrag, "Sleep Alarm");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onCreateAlarmOpenedListener() {
        lissy.onFromHomeListener();

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Everyday Alarm Clock");
    }
}
