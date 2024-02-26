package com.cstorage.utils;

import java.util.ArrayList;
import java.util.List;


public class DirUtil {
    public static String regexDir = "(.+\\/)+";
    public static boolean validate(String path) {
        return true;
    }
    public static String getPath(String path) {
        var obj = FileUtil.getObjectName(path);
        return path.replace(obj, "");
    }

    public static List<Pair> getMapOfChildren(String path) {
        var spl = List.of(path.split("/"));
        var map = new ArrayList<Pair>();
        map.add(new Pair("root", null));
        for (int i = 0; i < spl.size(); i++) {
            StringBuilder str = new StringBuilder();
            for (int j = 0; j <= i; j++)
                str.append(spl.get(j)).append("/");
            map.add(new Pair(spl.get(i), str.toString()));
        }
        return map;
    }
}
