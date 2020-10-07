package com.service.saver.saverservice;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper;
import com.service.saver.saverservice.tumblr.util.TumblrClient;
import com.service.saver.saverservice.twitter.TwitterClient;
import com.service.saver.saverservice.util.ClipDataListener;

import java.util.Objects;

public class MainTabActivity extends AppCompatActivity {


    public static TumblrClient JTUMBLR;
    public static TwitterClient JTWITTER;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

     private AdminSQLiteOpenHelper db = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        if (JTUMBLR == null)
            JTUMBLR = new TumblrClient(this);
        if (JTWITTER == null)
            JTWITTER = new TwitterClient(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        NavHostFragment fragmentById = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main_nav);
        assert fragmentById != null;
        NavController navController = fragmentById.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        ClipDataListener clipDataListener = new ClipDataListener((ClipboardManager) getSystemService(CLIPBOARD_SERVICE));
        clipDataListener.onValidLinkCapture(() -> {
            Toast.makeText(this, "Link Capture", Toast.LENGTH_SHORT).show();
        });
        //     db = new AdminSQLiteOpenHelper(this.getBaseContext());

        // List<UserLink> allUserLinks = db.allUserLinks();
        //  System.out.println(allUserLinks.toString());
      /*  for (String e : test) {
            UserLink user = new UserLink();
            user.setUsername(e);
            UserLink userLink = db.getUserLink(e);
            if (userLink == null)
                db.agregarUserLink(user);
        }*/
        //ActivityCompat.requestPermissions(MainTabActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission_group.STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was grantd, yay! Do the
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


}
