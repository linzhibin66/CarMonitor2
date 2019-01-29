package com.shinetech.mvp.network.UDP.InfoTool;

import com.shinetech.mvp.network.UDP.DatagramPacketDefine;
import com.shinetech.mvp.utils.MD5Tool;

/**
 * Created by ljn on 2017/2/14.
 */
public class LoginTool {

    private static byte[] sendApprove(byte[] user, byte approve[], byte md5Pwd[]) {
        byte[] theData = new byte[user.length + approve.length + md5Pwd.length];
        int length = 0;

        System.arraycopy(user, 0, theData, length, user.length);
        length +=user.length;

        System.arraycopy(approve, 0, theData, length, approve.length);
        length +=approve.length;

        System.arraycopy(md5Pwd, 0, theData, length, md5Pwd.length);

        return theData;
    }

    public static String getToken(byte loginType, byte[] mUserName, byte[] mToken, String pws){
        byte[] pwss;
        if(loginType ==  DatagramPacketDefine.GIS_TYPE){
            pwss = sendApprove(mUserName, mToken, MD5Tool.encodingSting(pws).getBytes());
        }else{
            pwss = sendApprove(mUserName, mToken, MD5Tool.encodingSting(pws + pws).getBytes());
        }

        return MD5Tool.getMessageDigest(pwss);
    }

    public static String getEncodingToken(byte[] mUserName, byte[] mToken, String pws){

        byte[] pwss;
        pwss = sendApprove(mUserName, mToken, pws.getBytes());
        return MD5Tool.getMessageDigest(pwss);
    }
}
