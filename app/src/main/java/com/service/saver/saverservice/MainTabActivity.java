package com.service.saver.saverservice;

import android.content.ClipboardManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.widget.Toast;

import com.service.saver.saverservice.folder.FileModelFragment;
import com.service.saver.saverservice.tumblr.util.TumblrClient;
import com.service.saver.saverservice.twitter.TwitterClient;
import com.service.saver.saverservice.util.ClipDataListener;

import java.util.ArrayList;
import java.util.List;

public class MainTabActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    public static TumblrClient client;
    public static TwitterClient jtwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        if (client == null)
            client = new TumblrClient(this);
        if (jtwitter == null)
            jtwitter = new TwitterClient(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFrag(CommonFragment.Companion.newInstance(), "Home", R.drawable.ic_home);
        mSectionsPagerAdapter.addFrag(new FileModelFragment(), "Folder", R.drawable.ic_photo_library);
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
        TabLayout tabLayout = findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);
        ClipDataListener clipDataListener = new ClipDataListener((ClipboardManager) getSystemService(CLIPBOARD_SERVICE));
        clipDataListener.onValidLinkCapture(() -> {
            Toast.makeText(this, "Link Capture", Toast.LENGTH_SHORT).show();
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final List<Integer> mFragmentDrawableList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public void addFrag(Fragment fragment, String title, int drawable) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            mFragmentDrawableList.add(drawable);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            Drawable drawable = getResources().getDrawable(mFragmentDrawableList.get(position), null);
            SpannableStringBuilder sb = new SpannableStringBuilder("  " + mFragmentTitleList.get(position)); // space added before text for convenience
            try {
                drawable.setBounds(5, 5, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE);
                sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sb;
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
}
