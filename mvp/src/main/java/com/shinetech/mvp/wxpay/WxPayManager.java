package com.shinetech.mvp.wxpay;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.github.wxpay.sdk.WXPayUtil;
import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskCtrl.DBCtrlTask;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.R;
import com.shinetech.mvp.interfaces.CheckWXPayOrderListener;
import com.shinetech.mvp.interfaces.WxPayViewListener;
import com.shinetech.mvp.utils.HttpUtils;
import com.shinetech.mvp.utils.MD5Tool;
import com.shinetech.mvp.utils.ShareUtils;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.wxpay.wxapi.Constants;
import com.shinetech.mvp.wxpay.wxapi.Util;
import com.shinetech.mvp.wxpay.wxapi.WXModel;
import com.shinetech.mvp.wxpay.wxapi.WXOrderPay;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by ljn on 2018-04-02.
 */

public class WxPayManager {

    private Context context;

    private IWXAPI api;

    private WxPayViewListener listener;

    private WXOrderPay mWXOrderPay;

    private static WxPayManager instance;

    private String orderNo;

    private WxPayManager() {
        this.context = MainApplication.getInstance();
//        api = WXAPIFactory.createWXAPI(context, "wxb4ba3c02aa476ea1");
        api = WXAPIFactory.createWXAPI(context, null);
        api.registerApp(ShareUtils.WX_APP_ID);
    }

    public static WxPayManager getInstance(){
        if(instance == null){

            synchronized (MainApplication.getInstance()) {
                if (instance == null) {
                    instance = new WxPayManager();
                }
            }
        }
        return instance;
    }

    public void setPayListener(WxPayViewListener listener){
        this.listener = listener;
    }


    public void createOrder(final String chargeType,final String fee, final String plateNumber){

        DBCtrlTask.getInstance().runTask(new TaskBean() {
            @Override
            public void run() {
                Map<String, String> resultData = null;
                try {

                    if(!HttpUtils.isNetWorkConnected()){
                        if(listener != null){
                            listener.onError(MainApplication.getInstance().getString(R.string.not_network_connected));
                        }
                        return;
                    }


                    mWXOrderPay = new WXOrderPay();

                    resultData = mWXOrderPay.doUnifiedOrder(chargeType, fee, plateNumber);

                    orderNo = mWXOrderPay.getOrderNo();

                    if(listener != null){
                        listener.onStart(orderNo);
                    }

                    if((resultData == null) || (resultData != null && "FAIL".equals(resultData.get("return_code")))){
                        String errorinfo ;
                        if(resultData != null){
                            errorinfo = resultData.get("return_msg");
                        }else{
                            errorinfo = context.getString(R.string.create_feepay_order_error);
                        }

                        System.out.println("返回错误 : "+errorinfo);

                        if(listener != null){
                            listener.onError(errorinfo);
                        }
                        return;
                    }else  if((resultData == null) || (resultData != null && "FAIL".equals(resultData.get("result_code")))){

                        String errorinfo ;
                        if(resultData != null){
                            errorinfo = resultData.get("err_code_des");
                        }else{
                            errorinfo = context.getString(R.string.create_feepay_order_error);
                        }

                        System.out.println("返回错误 : "+errorinfo);

                        if(listener != null){
                            listener.onError(errorinfo);
                        }
                        return;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    if(listener != null){
                        listener.onError("WXOrderPay error");
                    }
                }

                //调起微信，进行支付操作
                pay(resultData);
//                play();
            }
        });

    }

    public String getOrderNo(){
        return orderNo;
    }


    private void pay(Map<String, String> content){

        PayReq req = new PayReq();
        req.appId			= content.get("appid");
        req.partnerId		= content.get("mch_id");//商户
        req.prepayId		= content.get("prepay_id");
        req.nonceStr		= WXPayUtil.generateUUID();

        long currentTimeMillis = System.currentTimeMillis()/1000;
        req.timeStamp		=  currentTimeMillis+"";//时间戳 秒值

        req.packageValue	= "Sign=WXPay";

        HashMap<String, String> data = new HashMap<String, String>();
        data.put("appid", req.appId);
        data.put("noncestr", req.nonceStr);
        data.put("package", req.packageValue);
        data.put("partnerid", req.partnerId);
        data.put("prepayid", req.prepayId);
        data.put("timestamp", req.timeStamp);
//        System.out.println("pay = = = = = :   "+data);
        try {
            req.sign = mWXOrderPay.getSign(data);
            //					Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
//            System.out.println("正常调起支付");
//        api.registerApp("wx8fd252202f7ff598");
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            api.sendReq(req);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void checkOrder(final String orderNo, final CheckWXPayOrderListener listener){

        DBCtrlTask.getInstance().runTask(new TaskBean() {

            @Override
            public void run() {
                Map<String, String> result = mWXOrderPay.doOrderQuery(orderNo);
                if(listener != null){
                    listener.onResult(orderNo, result);
                }
            }
        });
    }


}
