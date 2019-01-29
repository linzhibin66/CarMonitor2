package com.shinetech.mvp.utils;

/**
 * Created by Lenovo on 2016/11/14.
 */
public class SizeUtils {

    public static String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    public static String formatDataSize(long size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else{
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }
}
