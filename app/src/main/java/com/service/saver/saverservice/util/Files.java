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
    public static String FILELIST = "Listfile.sss";

    public static <T> void savefile(String filename, T c) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    File file = new File(getCacheDir() + filename);
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    FileOutputStream fos = new FileOutputStream(file);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(c);
                    oos.close();
                    fos.close();

                } catch (Exception e) {
                    Log.e(String.valueOf(Log.ERROR), e.toString());
                }
            }
        }).start();
    }

    public static Object readObject(String filename) {
        try {
            File file = new File(getCacheDir() + filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object object = ois.readObject();
            return object;
        } catch (Exception e) {
            Log.e(String.valueOf(Log.ERROR), e.toString());
        }
        return null;
    }

    public static String getRunningDir() {
        File file = getRunningDirByFile();
        if (!file.mkdirs()) {
            Log.e("WARN", "Directory not created");
        }
        return file.getAbsolutePath();
    }

    public static String getRunningDir(String s) {
        File file = getRunningDirByFile(s);
        if (!file.mkdirs()) {
            Log.e("WARN", "Directory not created");
        }
        return file.getAbsolutePath();
    }

    public static String getCacheDir() {
        File file = getCacheDirByFile();
        if (!file.mkdirs()) {
            Log.e("WARN", "Directory not created");
        }
        return file.getAbsolutePath() + "/";
    }

    public static File getRunningDirByFile() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(), "TweetSaverService");
    }

    public static File getRunningDirByFile(String s) {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(), "TweetSaverService/" + s);
    }

    public static File getCacheDirByFile() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(), "TweetSaverService/cache");
    }

    public static List<File> getfiles(File dir) {
        File listFile[] = dir.listFiles();
        List<File> list = new ArrayList<>();
        if (listFile != null && listFile.length > 0)
            for (int i = 0; i < listFile.length; i++)
                if (!endsWith(listFile[i].getAbsolutePath(), Arrays.asList("/cache", "/.CACHE")))
                    if (listFile[i].isDirectory())
                        list.addAll(getfiles(listFile[i]));
                    else if (!endsWith(listFile[i].getAbsolutePath(), Arrays.asList(".sss", ".tss", ".txt")))
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
