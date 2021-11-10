package com.service.saver.saverservice;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.service.saver.saverservice.services.SaverService;
import com.service.saver.saverservice.services.Util;
import com.service.saver.saverservice.twitter.TwitterClient;
import com.service.saver.saverservice.util.ClipDataListener;

import java.util.Objects;

public class MainTabActivity extends AppCompatActivity {

    public static TwitterClient JTWITTER;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static Runnable linkCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        Context baseContext = getBaseContext();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
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
        linkCapture = () -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "Link Capture", Toast.LENGTH_SHORT).show();
            });
        };
        Util.scheduleJob(this);
        SaverService.setOnValidLinkCapture(linkCapture);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainTabActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
