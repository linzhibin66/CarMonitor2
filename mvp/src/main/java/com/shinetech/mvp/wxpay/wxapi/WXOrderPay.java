package com.shinetech.mvp.wxpay.wxapi;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.R;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.utils.DeviceUtils;
import com.shinetech.mvp.utils.HttpUtils;
import com.shinetech.mvp.utils.TimeUtil;
import com.shinetech.mvp.utils.ToastUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WXOrderPay {

    private com.github.wxpay.sdk.WXPay wxpay;
    private WXPayConfigImpl config;
    private String out_trade_no;
    private String total_fee;

    public WXOrderPay() throws Exception {
        config = WXPayConfigImpl.getInstance();
        wxpay = new WXPay(config);
        total_fee = "1";
        // out_trade_no = "201701017496748980290321";

        out_trade_no = creatOrderNo();
    }

    public String getSign(Map<String, String> reqData) throws Exception {
        return WXPayUtil.generateSignature(reqData, config.getKey(), WXPayConstants.SignType.HMACSHA256);
    }

    private String creatOrderNo(){

        String tiemOrderString = TimeUtil.getTiemOrderString(new Date(System.currentTimeMillis()));
        Random rand = new Random();
        String count = "";
        for(int i = 0; i<10;i++){
            int index = rand.nextInt(61);//【0-9】 、 【A-Z】 和 【a-z】
            if(index>=10 && index <= 35){
                count = count+(char)((index-10)+'A');
            }else if(index > 35){
                count = count+(char)((index-35)+'a');
            }else{
                count = count + index;
            }

        }
        return tiemOrderString+count;
    }

    public String getOrderNo(){
        return out_trade_no;
    }

    /**
     * app支付  下单
     */
    public  Map<String, String> doUnifiedOrder(String chargeType,String fee, String plateNumber) {

//        out_trade_no ="201804041450123tRBEDlzRV";//订单号
        out_trade_no = creatOrderNo();
        total_fee = fee;
        System.out.println("out_trade_no  : " + out_trade_no);

        HashMap<String, String> data = new HashMap<String, String>();

        data.put("body", "车掌控("+plateNumber+")-"+chargeType);
        data.put("out_trade_no", out_trade_no);//应用内定义（当前系统时间加随机序列来生成订单号）

        data.put("fee_type", "CNY");
        data.put("sign_type", "HMAC-SHA256");
        data.put("total_fee", fee);
        data.put("spbill_create_ip", DeviceUtils.getIPAddress(MainApplication.getInstance()));
        data.put("notify_url", "https://www.shinetech.cn/wspay/result.html");

        data.put("trade_type", "APP");

            try {
                Map<String, String> r = wxpay.unifiedOrder(data);
//                System.out.println("doUnifiedOrder = = = = = :   "+r);
                return r;
            } catch (Exception e) {
                e.printStackTrace();
            }

        return null;
    }

    public void doOrderClose() {
        System.out.println("关闭订单");
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", out_trade_no);
        try {
            Map<String, String> r = wxpay.closeOrder(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> doOrderQuery(String orderNo) {
//        System.out.println("查询订单");
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", orderNo);
//        data.put("transaction_id", "4008852001201608221962061594");
        try {
            Map<String, String> r = wxpay.orderQuery(data);
            System.out.println(r);
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void doOrderReverse() {
        System.out.println("撤销");
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", out_trade_no);
//        data.put("transaction_id", "4008852001201608221962061594");
        try {
            Map<String, String> r = wxpay.reverse(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 长链接转短链接
     * 测试成功
     */
    public void doShortUrl() {
        String long_url = "weixin://wxpay/bizpayurl?pr=etxB4DY";
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("long_url", long_url);
        try {
            Map<String, String> r = wxpay.shortUrl(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退款
     * 已测试
     */
    public void doRefund() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", out_trade_no);
        data.put("out_refund_no", out_trade_no);
        data.put("total_fee", total_fee);
        data.put("refund_fee", total_fee);
        data.put("refund_fee_type", "CNY");
        data.put("op_user_id", config.getMchID());

        try {
            Map<String, String> r = wxpay.refund(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询退款
     * 已经测试
     */
    public void doRefundQuery() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("out_refund_no", out_trade_no);
        try {
            Map<String, String> r = wxpay.refundQuery(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对账单下载
     * 已测试
     */
    public void doDownloadBill() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("bill_date", "20161102");
        data.put("bill_type", "ALL");
        try {
            Map<String, String> r = wxpay.downloadBill(data);
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doGetSandboxSignKey() throws Exception {
        WXPayConfigImpl config = WXPayConfigImpl.getInstance();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("mch_id", config.getMchID());
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        String sign = WXPayUtil.generateSignature(data, config.getKey());
        data.put("sign", sign);
        com.github.wxpay.sdk.WXPay wxPay = new com.github.wxpay.sdk.WXPay(config);
        String result = wxPay.requestWithoutCert("https://api.mch.weixin.qq.com/sandbox/pay/getsignkey", data, 10000, 10000);
        System.out.println(result);
    }

}
