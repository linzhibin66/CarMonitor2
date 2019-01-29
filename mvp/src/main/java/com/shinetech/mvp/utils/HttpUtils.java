package com.shinetech.mvp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.shinetech.mvp.utils.UIUtils;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author 刘琛慧
 * @date 2015/6/29
 */
public class HttpUtils {
    /*public static final String RSA_PUBLIC_KEY = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDVzCCAj+gAwIBAgIEGX3O6zANBgkqhkiG9w0BAQsFADBcMQswCQYDVQQGEwJjbjELMAkGA1UE\n" +
            "CBMCZ2QxCzAJBgNVBAcTAmRnMRAwDgYDVQQKEwdlYXN5ZGVyMRAwDgYDVQQLEwdlYXN5ZGVyMQ8w\n" +
            "DQYDVQQDEwZ3YWxrZXIwHhcNMTUwOTEwMDI1NzM4WhcNMjUwNzE5MDI1NzM4WjBcMQswCQYDVQQG\n" +
            "EwJjbjELMAkGA1UECBMCZ2QxCzAJBgNVBAcTAmRnMRAwDgYDVQQKEwdlYXN5ZGVyMRAwDgYDVQQL\n" +
            "EwdlYXN5ZGVyMQ8wDQYDVQQDEwZ3YWxrZXIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB\n" +
            "AQCVdCjGwxpaWVoIy8drPIL66ABfX7x3M1D/dmc10KSKyoazMmw/4whT7cE+1/QbNAuyJi1Fac1h\n" +
            "9QnUqObNWxQpphh+OLJf3uG/jYw9JbReXUenr1Lr7eJdhydlF+uK+h+IKp3iJRnBuIit2M99IqE1\n" +
            "jL6h/AryNFyhDClajKk6lE2vhDsWU2l2KaB3DgwSl8PcmyBHF/E85fZSbLnUslu2+Om19e1Myl0E\n" +
            "AV9sz/eePa0Pcr8emMPtGDbcsZdVk3EvhSrjC6+AHlQEJpWYHAlNnneOikL9VxZoJUucw1jRojpI\n" +
            "hDfXXsNo2xAxoGJykru+P3qTYTEpdkd3xiHVuAKBAgMBAAGjITAfMB0GA1UdDgQWBBTXcjBb9ums\n" +
            "j4VdR/Tp8NBzFIkB6zANBgkqhkiG9w0BAQsFAAOCAQEARFZCOTC1NzTt7kWiFIRkOynRQGYRNXRZ\n" +
            "fewEJOz+JydahbZ6FISEobsoGYDBBZnPa6Zw7Ws8WCBQw+A/RkAHTAAS05YVc4rtI5uRSRRqt35x\n" +
            "HM1IVlJNS0ouYmKT0Xj86hn9/PC7ubCQCKEqaGUxT9oBzE/eAc5IGNEURQb0I14NhNF4/InT+BN4\n" +
            "j4Ql/sk7nmK1SV7r0OVlCTYVAc4Cs9NjsnuZ25WNUjuF5rZeuXYPUjof+RNQZQ7caz4C+hmHeiUw\n" +
            "hCe7WgyuueFzVcFnA/ZBuFeAOxC/WoC64B3+EQERsHBLwI25gnHtwUOU4kku5ITxTsRlX6J0JMg8\n" +
            "UfKjmQ==\n" +
            "-----END CERTIFICATE-----";*/
    //SSL加密公匙
    private static final String RSA_PUBLIC_KEY = "";

    //后台host
    public static final String HTTPHOST = "https://www.shinetech.cn";
    public static final String FAQDIR = "/CarMonitor2/FAQ/";
    public static final String USEGUIDEDIR = "/CarMonitor2/UseGuide/";
    public static final String ACCOUNTEXPLAIN = "/CarMonitor2/AccountExplain/";
    public static final String ADVERTISEMENTDIR = "/CarMonitor2/advertisement/";

    //SSL双端验证客户端保存的私匙文件名
    private static final String CLIENT_RSA_PRIVATE_KEY = "";
    //SSL双端验证客户端保存的私匙密码
    private static final String CLIENT_RSA_PRIVATE_KEY_PASSWORD = "";

  /*  private static final OkHttpClient okHttpClient = new OkHttpClient();

    static {
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(UIUtils.getContext()), CookiePolicy.ACCEPT_ALL));
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    *//**
     * 网络请求，如果callback为空，那么执行同步网络请求会阻塞当前的请求线程直到网络请求完成，
     * 如果callback不为空，那么网络是异步请求会在子线程中执行
     *
     * @param url
     * @param params
     * @param callback
     * @return
     *//*
    public static Response request(String url, Map<String, String> params, final Callback callback) {
        FormEncodingBuilder formEncodingBuilder = null;
        if (params != null) {
            formEncodingBuilder = new FormEncodingBuilder();
            for (Map.Entry<String, String> paramSet : params.entrySet()) {
                formEncodingBuilder.add(paramSet.getKey(), paramSet.getValue());
            }

        }

        return doRequest(url, formEncodingBuilder == null ? null : formEncodingBuilder.build(), callback);
    }


    *//**
     * 文件上传
     *
     * @param url
     * @param params   需要上传的参数和文件
     * @param callback
     * @return
     *//*
    public static Response upload(String url, Map<String, ?> params, Callback callback) {
        return doRequest(url, parseParams(params), callback);
    }

    *//**
     * @param url
     * @param params
     * @param callback
     * @return
     *//*
    public static boolean download(String url, Map<String, String> params, Callback callback) {

        return false;
    }

    public static Response doRequest(String url, RequestBody requestBody, Callback callback) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("请求的URL不能为空！");
        }

        Response response = null;
        Request.Builder requestBuilder = new Request.Builder().url(url).tag(url);
        if (requestBody != null) {
            requestBuilder.post(requestBody);
        }

        if (callback != null) {
            okHttpClient.newCall(requestBuilder.build()).enqueue(callback);
        } else {
            try {
                response = okHttpClient.newCall(requestBuilder.build()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    public static RequestBody parseParams(Map<String, ?> params) {
        if (params != null) {
            MultipartBuilder multipartBuilder = new MultipartBuilder();
            for (Map.Entry<String, ?> paramSet : params.entrySet()) {
                if (paramSet.getValue() instanceof File) {
                    File file = (File) paramSet.getValue();
                    multipartBuilder.addFormDataPart(paramSet.getKey(), file.getName(),
                            RequestBody.create(MediaType.parse("application/octet-stream"), file));
                } else {
                    multipartBuilder.addFormDataPart(paramSet.getKey(), paramSet.getValue().toString());
                }
            }

            return multipartBuilder.build();
        }

        return null;
    }

    */

    /**
     * 检查网络是否已经连接
     *
     * @return
     */
    public static boolean isNetWorkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) UIUtils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return mNetworkInfo != null && mNetworkInfo.isConnected();
    }

    /**
     * 初始化单端
     *
     * @param okHttpClient
     *//*
    public static void initSingleCertificationSSL(OkHttpClient okHttpClient) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(initPublicKeyStore());
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    *//**
     * 初始化公匙keyStore
     *
     * @return 初始化成功后的公匙keyStore或者Null
     *//*
    public static KeyStore initPublicKeyStore() {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", certificateFactory.generateCertificate(new StringBufferInputStream(RSA_PUBLIC_KEY)));
            return keyStore;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    */

    /**
     * SSL双端验证keyStore初始化配置
     *
     * @param //okHttpClient 需要配置SSL双端验证的OkHttpClient
     * @param //context      获取私匙keyStore文件的Context
     *//*
    public static void initDoubleCertificationSSL(OkHttpClient okHttpClient, Context context) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.
                    getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(initPublicKeyStore());

            //安卓只支持BKS格式的KeyStore,不支持使用KeyTool或者openSSL生成的JKS格式的KeyStore
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(context.getAssets().open(CLIENT_RSA_PRIVATE_KEY), CLIENT_RSA_PRIVATE_KEY_PASSWORD.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, CLIENT_RSA_PRIVATE_KEY_PASSWORD.toCharArray());

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/


    static class MyCookieHandler extends CookieHandler {

        @Override
        public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
            return null;
        }

        @Override
        public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
            new CookieManager();
        }
    }
}
