package com.shinetech.mvp.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author 刘琛慧
 *         date 2015/9/15.
 */
public class ToastUtil {

    private static Toast mToast;

    public static void showShort(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLong(Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    public static void showShort(String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    public static void showLong(String message) {
        showToast( message, Toast.LENGTH_LONG);
    }


    private static void showToast(Context context, String message, int type) {
        Toast.makeText(context, message, type).show();
    }

    private static void showToast(String message, int type){
        if(mToast==null){
            mToast = Toast.makeText(UIUtils.getContext(),message,type);
        }else{
            mToast.setText(message);
            mToast.setDuration(type);
        }
        mToast.show();
    }

}
