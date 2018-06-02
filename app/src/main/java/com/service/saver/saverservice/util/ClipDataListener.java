package com.service.saver.saverservice.util;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.widget.Toast;

import com.service.saver.saverservice.MainTabActivity;
import com.service.saver.saverservice.MyApp;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import needle.Needle;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class ClipDataListener {

    static String twitteraccesstoken = "";//"733406466-r8gIEfFqO0T37LPynGFGtrmtx08MplWlEvc0xpZT";
    static String twitteraccessSecret = "";//"8Uhza0HW7QIGGJMepr6H5RS1CNgeYrZXGld4znKkUuE9N";
    static String consumerKey = "RjUwa94ScJf6qybLuPzq74yPx";
    static String twitterconsumerSecret = "SjTPZsGa2m5KEuhupwbjh1QUCuxCqTatKUVhG8YHxGqy9oeTTV";
    private List<String> listlinks = new ArrayList<>();
    public static Twitter jtwitter;

    public ClipDataListener(ClipboardManager clip) {
        ConfigurationBuilder cb = new ConfigurationBuilder()
                .setIncludeEntitiesEnabled(true)
                .setIncludeMyRetweetEnabled(true)
                .setIncludeExtAltTextEnabled(true)
                .setTweetModeExtended(true)
                .setIncludeEmailEnabled(true);
        jtwitter = new TwitterFactory(cb.build()).getInstance();
        try {
            jtwitter.setOAuthConsumer(consumerKey, twitterconsumerSecret);
            Object twitteraccesstoken = Files.readObject("twitteraccesstoken");
            Object twitteraccessSecret = Files.readObject("twitteraccessSecret");
            if (!(twitteraccessSecret == null || twitteraccesstoken == null))
                jtwitter.setOAuthAccessToken(new AccessToken((String) twitteraccesstoken, (String) twitteraccessSecret));
        } catch (Exception e) {
            e.printStackTrace();

        }
        ClipboardManager.OnPrimaryClipChangedListener listener = () -> {
            getLink(clip);
        };
        clip.addPrimaryClipChangedListener(listener);
        getLink(clip);

    }

    private void getLink(ClipboardManager clipBoard) {
        ClipData clipData = clipBoard.getPrimaryClip();
        if (clipData != null) {
            ClipData.Item item = clipData.getItemAt(0);
            String text = item.getText().toString();
            if (text.contains("//twitter.com/") && text.contains("status")) {
                FancyToast.makeText(MainTabActivity.activity, "Link received", Toast.LENGTH_SHORT, FancyToast.INFO, true);
                if (!checkOnList(listlinks, text)) {
                    saveTweet(text);
                }
            }
        }
    }

    private boolean checkOnList(List<String> lista, String object) {
        for (String s : lista) {
            return s.equals(object);
        }
        return false;
    }

    public void saveTweet(String url) {
        Needle.onBackgroundThread().execute(() -> {
                    try {
                        String split1 = getID(url);
                        if (jtwitter.getOAuthAccessToken() != null) {

                            Status status = jtwitter.showStatus(Long.parseLong(split1));
                            List<MediaEntity> mediaEntities = Arrays.asList(status.getMediaEntities());
                            if (!mediaEntities.isEmpty()) {
                                entites(mediaEntities);
                            }
                        }
                    } catch (TwitterException | IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @SuppressLint("NewApi")
    private void entites(List<MediaEntity> mediaEntities) throws IOException {
        mediaEntities.sort((Comparator.comparingInt(MediaEntity::getVideoAspectRatioHeight)));
        MediaEntity mediaEntity = mediaEntities.get(0);
        if (mediaEntity.getType().equalsIgnoreCase("photo"))
            MyApp.add(mediaEntity.getMediaURL());
        else {
            List<MediaEntity.Variant> videoVariants = Arrays.asList(mediaEntity.getVideoVariants());
            Collections.sort(videoVariants, Comparator.comparingInt(MediaEntity.Variant::getBitrate));
            String[] split = videoVariants.get(videoVariants.size() - 1).getUrl().split("\\?");
            String link = split[0];
            MyApp.add(link);
        }
    }

    private String getID(String url) {
        String[] strings = url.split("/");
        String string = strings[strings.length - 1];
        return string.split("\\?")[0];
    }


    private String getName(String url) {
        String[] split = url.split("/");
        return split[split.length - 1];
    }
}
