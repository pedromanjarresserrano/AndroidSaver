package com.service.saver.saverservice;

import android.app.NotificationManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.service.saver.saverservice.folder.FileModelFragment;
import com.service.saver.saverservice.util.ClipDataListener;

import java.util.ArrayList;
import java.util.List;

public class MainTabActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFrag(CommonFragment.Companion.newInstance(), "Home");
        mSectionsPagerAdapter.addFrag(new FileModelFragment(), "Folder");
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
        TabLayout tabLayout = findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);
        mNotifyManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        new ClipDataListener((ClipboardManager) getSystemService(CLIPBOARD_SERVICE));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
}
