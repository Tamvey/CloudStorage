package com.cstorage.utils;

import java.util.regex.Pattern;

public class FileUtil {
    public static boolean validate(String path) {
        return true;
    }
    public static String getObjectName(String path) {
        return path.replaceAll(DirUtil.regexDir, "");
    }
}
