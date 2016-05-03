package com.test.trimmer.videoconverter;

import android.os.Environment;

import java.io.File;

/**
 * Created by mark on 30.04.16.
 */
public class Utils {
    public static String findPathByFileName(String fileName) {
        return findFilePath(Environment.getExternalStorageDirectory(), fileName).getPath();
    }

    private static File findFilePath(File where, String find) {
        if (where.getName().equals(find)) {
            return where;
        }

        if (where.isDirectory()) {
            File files[] = where.listFiles();
            if (files != null) {
                for (File file : files) {
                    File found = findFilePath(file, find);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }

        return null;
    }
}
