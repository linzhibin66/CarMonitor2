package com.shinetech.mvp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.shinetech.mvp.MainApplication;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by ljn on 2017-05-09.
 */
public class ShareUtils {

    private static String TENCENT_APP_ID = "100429666";

    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String WX_APP_ID = "wx8fd252202f7ff598";//"wx51c3a25555e74ed7";
//    wx8fd252202f7ff598

    private static String share_content = "车掌控Hi-Cars掌上实时监控您的车辆当前的位置、状态，以及进行跟踪及轨迹回放功能，从此不再担心车辆安全问题@广东华盈光达科技有限公司，http://www.shinetech.cn";

    private static String TARGET_URL= "https://shinetech.cn/wxmenu/car_download.html";


    public static  void shareQzone(Activity activity, IUiListener listener){

        Tencent mTencent = Tencent.createInstance(TENCENT_APP_ID, activity);
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "车掌控");//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, share_content);//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, TARGET_URL);//必填
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, new ArrayList<String>());
        mTencent.shareToQzone(activity, params,listener);
    }

    public static void shareQQ(Activity activity, IUiListener listener){

        Tencent mTencent = Tencent.createInstance(TENCENT_APP_ID, activity);
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "车掌控");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  share_content);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  TARGET_URL);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "车掌控");
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
        mTencent.shareToQQ(activity, params, listener);
    }

    public static void shareWX(Context context,int drawable_id){

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = TARGET_URL;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "车掌控";
        msg.description = share_content;
        msg.thumbData = bmpToByteArray(BitmapFactory.decodeResource(context.getResources(),drawable_id),true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");

        req.message = msg;
        //发送到聊天界面
        req.scene = SendMessageToWX.Req.WXSceneSession;
        //发送到朋友圈
//        req.scene = SendMessageToWX.Req.WXSceneTimeline;

       /* WXTextObject textObject = new WXTextObject();
        textObject.text = share_content;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = share_content;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;*/

        MainApplication.getInstance().getWXApi().sendReq(req);
    }
    public static void shareWXmoments(Context context,int drawable_id){

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = TARGET_URL;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "车掌控";
        msg.description = share_content;
        msg.thumbData = bmpToByteArray(BitmapFactory.decodeResource(context.getResources(),drawable_id),true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");

        req.message = msg;
        //发送到聊天界面
//        req.scene = SendMessageToWX.Req.WXSceneSession;
        //发送到朋友圈
        req.scene = SendMessageToWX.Req.WXSceneTimeline;

        MainApplication.getInstance().getWXApi().sendReq(req);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
