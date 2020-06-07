package com.service.saver.saverservice.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Files {

    private static final String absolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();

    public static String getCacheDir() {
        File file = getCacheDirByFile();
        if (!file.mkdirs()) {
            Log.e("WARN", "Directory not created");
        }
        return file.getAbsolutePath() + "/";
    }

    public static File getRunningDirByFile() {
        return new File(absolutePath, "SaverService");
    }

    public static File getDir(String s) {
        return new File(s);
    }

    public static String getAbsolutePath() {
        return absolutePath + "/SaverService/";
    }

    public static File getRunningDirByFile(String s) {
        return new File(absolutePath, "SaverService/" + s.replaceAll("[^a-zA-Z0-9]", ""));
    }

    public static File getCacheDirByFile() {
        return new File(absolutePath, "SaverService/cache");
    }

    public static List<File> getfiles(File dir) {
        File listFile[] = dir.listFiles();
        List<File> list = new ArrayList<>();
        if (listFile != null && listFile.length > 0)
            for (int i = 0; i < listFile.length; i++)
                if (!endsWith(listFile[i].getAbsolutePath(), Arrays.asList("/cache", "/.CACHE")))
                    if (!endsWith(listFile[i].getAbsolutePath(), Arrays.asList(".sss", ".tss", ".txt")))
                        list.add(listFile[i]);
        return list;
    }


    public static List<File> getfiles(String s) {
        return getfiles(new File(s));
    }

    private static boolean endsWith(String s, List<String> suffixs) {
        for (String ends : suffixs) {
            if (s.endsWith(ends))
                return true;
        }
        return false;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
