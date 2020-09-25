package com.service.saver.saverservice.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import static com.service.saver.saverservice.MainTabActivity.jtwitter;


public class ClipDataListener {

    private List<String> listlinks = new ArrayList<>();
    private Runnable runnable = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ClipDataListener(ClipboardManager clip) {
        ClipboardManager.OnPrimaryClipChangedListener listener = () -> {
            getLink(clip);
        };
        clip.addPrimaryClipChangedListener(listener);
        getLink(clip);

    }

    public void onValidLinkCapture(Runnable e) {
        this.runnable = e;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getLink(ClipboardManager clipBoard) {
        ClipData clipData = clipBoard.getPrimaryClip();
        if (clipData != null) {
            ClipData.Item item = clipData.getItemAt(0);
            String text = item.getText().toString();
            if (text.contains("//twitter.com/") && text.contains("status")) {
                if (!checkOnList(listlinks, text)) {
                    if (runnable != null)
                        runnable.run();
                    jtwitter.saveTweet(text);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean checkOnList(List<String> lista, String object) {
        return lista.stream().filter(e->e.equals(object)).findFirst().isPresent();
    }


}
