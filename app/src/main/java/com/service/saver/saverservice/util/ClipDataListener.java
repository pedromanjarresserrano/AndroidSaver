package com.service.saver.saverservice.util;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.service.saver.saverservice.MainTabActivity.JTWITTER;

import androidx.annotation.RequiresApi;

import com.service.saver.saverservice.domain.PostLink;
import com.service.saver.saverservice.domain.TempLink;
import com.service.saver.saverservice.sqllite.AdminSQLiteOpenHelper;


public class ClipDataListener {

    public static ClipboardManager CLIP;
    private List<String> listlinks = new ArrayList<>();
    private static Runnable runnable = null;
    private AdminSQLiteOpenHelper db = null;

    public ClipDataListener(Context ctx) {
        if (CLIP == null) {
            CLIP = (ClipboardManager) ctx.getSystemService(CLIPBOARD_SERVICE);
        }
        db = new AdminSQLiteOpenHelper(ctx);
        CLIP.addPrimaryClipChangedListener(() -> getLink());
    }

    public void onValidLinkCapture(Runnable e) {
        if (this.runnable == null)
            this.runnable = e;
    }

    public void getLink() {
        ClipData clipData = CLIP.getPrimaryClip();
        if (clipData != null) {
            ClipData.Item item = clipData.getItemAt(0);
            String text = item.getText().toString();
            if (text.contains("//twitter.com/") && text.contains("status")) {
                if (!checkOnList(text)) {
                    if (runnable != null)
                        runnable.run();
                    JTWITTER.saveTweet(text);
                    //clipData.
                }
            }
        }
    }

    private boolean checkOnList(String url) {
        List<PostLink> postLink = db.getAllPostLinkByParentLink(url);
        Optional<PostLink> first1 = postLink.stream().filter(e -> e.getSave() == false).findFirst();
        if (first1.isPresent()) {
            return true;
        } else {
            List<TempLink> tempLink = db.allTempLinks();
            Optional<TempLink> first = tempLink.stream().filter(e -> e.getParent_url().equalsIgnoreCase(url)).findFirst();
            return first.isPresent();
        }
    }

}
