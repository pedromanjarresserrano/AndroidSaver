package com.service.saver.saverservice;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.widget.Toast;

import com.service.saver.saverservice.domain.UserLink;
import com.service.saver.saverservice.folder.FileModelFragment;
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper;
import com.service.saver.saverservice.tumblr.util.TumblrClient;
import com.service.saver.saverservice.twitter.TwitterClient;
import com.service.saver.saverservice.twitter.TwitterPostFragment;
import com.service.saver.saverservice.util.ClipDataListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainTabActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    public static TumblrClient client;
    public static TwitterClient jtwitter;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private List<String> test = Arrays.asList(
            "bigblackcockzac",
            "suprshok",
            "GangBang_Heaven",
            "GangBangOrgyX",
            "gangbandxxx",
            "wifefuckedhard",
            "manofpleasure",
            "DP_DAP_Fan",
            "dp_fan1",
            "penetrationdou",
            "groupsexfreak",
            "illicit69kitty",
            "LongerDeeper",
            "bbcgoldclub",
            "Jessica58697455",
            "BlackXStallions",
            "BBCTARGET",
            "BBCTARGET",
            "CutteeLil",
            "londonrae__",
            "HWFantasies",
            "asslickballs",
            "I_R_propaganda",
            "cansugrupsever",
            "EastCoastSwag18",
            "latinas999",
            "InterracialTrip",
            "BreedMeDaddyNow",
            "jcwildin",
            "blackisbette",
            "hashtag",
            "bbcslut",
            "DCBlkStallion",
            "NakedTL",
            "breedingqueenxo",
            "curvydrachel4",
            "danishBBClover",
            "Nikki4BBC1",
            "TooHotLouLou",
            "KarlRayne",
            "rimming_rocks",
            "BestRimjobPics",
            "TiffaniTeaseXXX",
            "TheDaddyPanda",
            "ElleAtTheEssex",
            "ScarletteBunny",
            "LongerDeeper",
            "jaidenwestX",
            "HotWifeRules69",
            "polinafus",
            "Jessica58697455",
            "lnterracialLust",
            "white_kinky",
            "siswet",
            "AssReFocus",
            "ActionProjeX",
            "LucyCatOfficial",
            "xxxfreaknasty2",
            "DestinationKat",
            "blacktowhitenet",
            "candyxop8",
            "DaiIySexVid",
            "bbcluvssluts",
            "whiteslut4bbc",
            "CANDYKPR",
            "lnterracialLust",
            "ariettaadamsxxx",
            "Moh_BBC",
            "AlenaCroftXXX",
            "blackbull124",
            "Analgeddon_",
            "buffaloswingcpl",
            "thebrittanyxoxo",
            "Mrs_DarkCuckold",
            "BlacOnWhite69",
            "CirenV",
            "missddoll1",
            "hotwife3bbc",
            "missddoll1",
            "BlackMilk_69x",
            "Secretcuckold11",
            "SAVAGE_SLUT",
            "ProCumtributes",
            "Pash1991",
            "BIackedTv",
            "Secretcuckold11",
            "pornmansion4",
            "degeneratexxxxx",
            "Amber_JayneXX",
            "Gatita_krystal1",
            "girlwspadetatoo",
            "LilFreakDaChamp",
            "IRisLoveNBWO",
            "DCBlkStallion",
            "kinkywaveIR",
            "BRichXXX",
            "RealCurvesLA",
            "toy4blck",
            "PrincssSparkles",
            "CULOMBIANAS1",
            "InterracialEuro",
            "devin0209",
            "cuckold4s",
            "Galaxy100Galaxy",
            "KitPlavi",
            "Elin17xxx",
            "IRconfes",
            "fucktheshitout2",
            "jcmex28",
            "Black_Cockxx",
            "XMotherOfSighsX",
            "DrunkOnBBC",
            "missluna2019",
            "SuccubusReborn",
            "PoisonxGoddess",
            "James00412717",
            "uragokkun",
            "adrianorice",
            "CumshotPornVids",
            "coverthebitch",
            "CumSlutCentralx",
            "Kianna_Dior",
            "freakyboy_xxx",
            "CumshotsVids",
            "MilfsnCum",
            "CumAllOver1",
            "LoveHugeLoads",
            "HotFacials",
            "SwallowMiniClip",
            "Orazio_Sw_",
            "newsperminator",
            "pornoperv69",
            "cumchampion",
            "PrazerMaior",
            "R_sidney_V",
            "cumswallowclips",
            "blondefart",
            "BlackAlphaKing",
            "Stonerock991",
            "anal_life69",
            "mssnewbooty",
            "Augusttaylorxxx"
    );
    private AdminSQLiteOpenHelper db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        if (client == null)
            client = new TumblrClient(this);
        if (jtwitter == null)
            jtwitter = new TwitterClient(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFrag(CommonFragment.Companion.newInstance(), "Home", R.drawable.ic_home);
        mSectionsPagerAdapter.addFrag(new FileModelFragment(), "Folder", R.drawable.ic_photo_library);
        mSectionsPagerAdapter.addFrag(new TwitterPostFragment(), "Twitter", R.drawable.ic_photo_library);
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
        TabLayout tabLayout = findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);
        ClipDataListener clipDataListener = new ClipDataListener((ClipboardManager) getSystemService(CLIPBOARD_SERVICE));
        clipDataListener.onValidLinkCapture(() -> {
            Toast.makeText(this, "Link Capture", Toast.LENGTH_SHORT).show();
        });
        db = new AdminSQLiteOpenHelper(this.getBaseContext());

        List<UserLink> allUserLinks = db.allUserLinks();
        System.out.println(allUserLinks.toString());
        for (String e : test) {
            UserLink user = new UserLink();
            user.setUsername(e);
            UserLink userLink = db.getUserLink(e);
            if (userLink == null)
                db.agregarUserLink(user);
        }
        //ActivityCompat.requestPermissions(MainTabActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission_group.STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainTabActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
