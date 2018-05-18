package com.service.saver.saverservice.tumblr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.service.saver.saverservice.MyApp;
import com.service.saver.saverservice.R;
import com.service.saver.saverservice.tumblr.util.JumblrHolder;
import com.tumblr.jumblr.types.Post;

import java.net.URL;
import java.util.List;

import needle.Needle;

public class TumblrActivity extends AppCompatActivity {
    JumblrHolder client = new JumblrHolder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tumblr);
        Bundle extras = getIntent().getExtras();
        try {
            String text = (String) getIntent().getExtras().get(Intent.EXTRA_TEXT);
            if (text.contains("tumblr")) {
                URL url = new URL(text);
                String host = url.getHost();
                String path = url.getPath();
                String[] hostsplit = host.split("\\.");
                String[] pathsplit = path.split("/");
                String id = pathsplit[2];
                String blog = hostsplit[0];
                Needle.onBackgroundThread().execute(() -> {
                    Post post = client.blogPost(blog, Long.valueOf(id));
                    List<String> urlFile = JumblrHolder.getUrlFile(post);
                    urlFile.forEach(MyApp::add);
                });

            }
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
