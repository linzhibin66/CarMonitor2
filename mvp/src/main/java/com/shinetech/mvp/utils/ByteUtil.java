package com.shinetech.mvp.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * @author 刘琛慧
 *         date 2015/10/27.
 */
public class ByteUtil {

    public static byte[] shortToBytes(short shortVal) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((shortVal >> 8) & 0xFF);
        bytes[1] = (byte) (shortVal & 0xFF);
        return bytes;
    }

    public static short shortFromBytes(byte[] bytes) {
        return (short) (((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF));
    }

    public static byte[] intToBytes(int intVal) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((intVal >> 24) & 0xFF);
        bytes[1] = (byte) ((intVal >> 16) & 0xFF);
        bytes[2] = (byte) ((intVal >> 8) & 0xFF);
        bytes[3] = (byte) (intVal & 0xFF);
        return bytes;
    }

    public static int intFromBytes(byte[] bytes) {
        int intVal = (bytes[0] & 0xFF) << 24;
        intVal |= (bytes[1] & 0xFF) << 16;
        intVal |= (bytes[2] & 0xFF) << 8;
        intVal |= (bytes[3] & 0xFF);

        return intVal;
    }

    /**
     * 将字符串转换成字节数组，并在字节数组头部加上字符串的长度字节
     *
     * @param strVal
     * @return
     */
    public static byte[] stringToBytes(String strVal) {
        byte[] bytes = null;

        try {
            bytes = strVal.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    /**
     * 去掉字符串字节数组的长度字节,并返回构建好的字符串
     *
     * @param bytes
     * @param index
     * @param len   @return
     */
    public static String stringFromBytes(byte[] bytes, int index, int len) {
        String content = null;
        try {
            content = new String(bytes, index, len, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static String bufferToString(byte[] buffer) {
        return Arrays.toString(buffer);
    }

}
