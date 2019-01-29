package com.shinetech.mvp.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * Created by ljn on 2017/2/8.
 */
public class MD5Tool {

    public static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();

            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }

            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String encodingPwd(String pwd){
        try {
            byte[] passwordBytes = (pwd + pwd).getBytes("GBK");
            String encodpwd = getMessageDigest(passwordBytes);
            return encodpwd;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encodingSting(String s){
        byte[] bytes = s.getBytes();
        String messageDigest = getMessageDigest(bytes);
        return messageDigest;
    }

    private static String getString(byte[] b){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i ++){
            sb.append(b[i]);
        }
        return sb.toString();
    }
}
