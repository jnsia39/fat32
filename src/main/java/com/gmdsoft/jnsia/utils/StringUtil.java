package com.gmdsoft.jnsia.utils;

public class StringUtil {
    public static String trimRight(String fileName) {
        if (fileName.startsWith("."))
            return fileName;

        int lastDotIndex = fileName.lastIndexOf('.');

        return (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex);
    }

    public static String trimLastPath(String pathName) {
        int lastDotIndex = pathName.lastIndexOf('/');

        return (lastDotIndex == -1) ? pathName : pathName.substring(0, lastDotIndex);
    }

    public static boolean isDotOrDoubleDot(String name) {
        return name.equals(".") || name.equals("..");
    }
}
