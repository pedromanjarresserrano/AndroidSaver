package com.service.saver.saverservice.util;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.service.saver.saverservice.folder.model.FileModel;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import needle.Needle;

public class Files {

    private static final String ABSOLUTE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.#");

    public static String getCacheDir() {
        File file = getCacheDirByFile();
        if (!file.mkdirs()) {
            Log.e("WARN", "Directory not created");
        }
        return file.getAbsolutePath() + "/";
    }

    public static File getRunningDirByFile() {
        return new File(ABSOLUTE_PATH, "SaverService");
    }

    public static File getDir(String s) {
        return new File(s);
    }

    public static String getAbsolutePath() {
        return ABSOLUTE_PATH + "/SaverService/";
    }

    public static File getRunningDirByFile(String s) {
        return new File(ABSOLUTE_PATH, "SaverService/" + s.replaceAll("[^a-zA-Z0-9]", ""));
    }

    public static File getCacheDirByFile() {
        return new File(ABSOLUTE_PATH, "SaverService/cache");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<File> getfiles(File dir) {
        File listFile[] = dir.listFiles();
        List<File> collect = Arrays.asList(listFile)
                .stream()
                .filter(e -> !endsWith(e.getAbsolutePath(), Arrays.asList("/cache", "/.CACHE", ".sss", ".tss", ".txt")))
                .collect(Collectors.toList());
        return collect;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<File> getfiles(String s) {
        return getfiles(new File(s));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static boolean endsWith(String s, List<String> suffixs) {
        return suffixs.stream()
                .filter(e -> s.endsWith(e))
                .findFirst()
                .isPresent();
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return DECIMAL_FORMAT.format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<FileModel> getFilesModel(String location) {
        List<File> fileList = getfiles(location);
        fileList.sort((x, y) -> Long.compare(y.lastModified(), x.lastModified()));
        return fileList.stream().map((f) -> new FileModel(fileList.indexOf(f) + 0L, f.getName(), f.getAbsolutePath(), f.isDirectory(), f.getParent())).collect(Collectors.toList());
    }
}
