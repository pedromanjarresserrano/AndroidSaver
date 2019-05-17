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

    public static String getCacheDir() {
        File file = getCacheDirByFile();
        if (!file.mkdirs()) {
            Log.e("WARN", "Directory not created");
        }
        return file.getAbsolutePath() + "/";
    }

    public static File getRunningDirByFile() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(), "SaverService");
    }

    public static File getRunningDirByFile(String s) {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(), "SaverService/" + s.replaceAll("[^a-zA-Z0-9]", ""));
    }

    public static File getCacheDirByFile() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(), "SaverService/cache");
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
