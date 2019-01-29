package com.shinetech.mvp.wxpay.wxapi;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;
import com.shinetech.mvp.utils.ShareUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class WXPayConfigImpl extends WXPayConfig {

    private byte[] certData;
    private static WXPayConfigImpl INSTANCE;

    private WXPayConfigImpl() throws Exception{
        // 证书文件(微信商户平台-账户设置-API安全-API证书-下载证书) 不做撤销和退款操作，不保存证书到app中(关闭了WXPay 中checkWXPayConfig关于证书的判断)
       /* String certPath = "D://CERT/common/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();*/
    }

    public static WXPayConfigImpl getInstance() throws Exception{
        if (INSTANCE == null) {
            synchronized (WXPayConfigImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WXPayConfigImpl();
                }
            }
        }
        return INSTANCE;
    }

    public String getAppID() {
//        return "wx8fd252202f7ff598";//正式
        return ShareUtils.WX_APP_ID;
//        return "wxab8acb865bb1637e";//测试
//        return "wxb4ba3c02aa476ea1";
    }

    public String getMchID() {
        return "1501880881";//正式
//        return "11473623";//测试
//        return "1900006771";
    }

    public String getKey() {
        return "59f02a3dcd232480294ffdefbbf43f53";//正式
//        return "2ab9071b06b9f739b950ddb41db2690d";//测试
    }

    public InputStream getCertStream() {
        /*ByteArrayInputStream certBis;
        certBis = new ByteArrayInputStream(this.certData);
        return certBis;*/
        return null;
    }


    public int getHttpConnectTimeoutMs() {
        return 2000;
    }

    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    public IWXPayDomain getWXPayDomain() {
        return WXPayDomainSimpleImpl.instance();
    }

    public String getPrimaryDomain() {
        return "api.mch.weixin.qq.com";
    }

    public String getAlternateDomain() {
        return "api2.mch.weixin.qq.com";
    }

    @Override
    public int getReportWorkerNum() {
        return 1;
    }

    @Override
    public int getReportBatchSize() {
        return 2;
    }
}
