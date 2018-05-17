package com.service.saver.saverservice;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.service.saver.saverservice.util.Files;

import java.util.ArrayList;
import java.util.List;

import static com.service.saver.saverservice.util.Files.FILELIST;

public class MyApp extends Application {

    private static List<String> files = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        List<String> list = (List<String>) Files.readObject(FILELIST);
        Fresco.initialize(this);

        if (list != null) {
            files.addAll(list);
        }

    }

    public static List<String> getFiles() {
        return files;
    }

    public static boolean add(String s) {
        boolean add = files.add(s);
        Files.savefile(FILELIST, files);
        return add;
    }

    public static boolean remove(String o) {
        boolean remove = files.remove(o);
        Files.savefile(FILELIST, files);
        return remove;
    }
}
