package com.easyder.carmonitor.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.FAQAdapter;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.shinetech.mvp.utils.HttpUtils;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-12.
 */
public class FAQWidget {

    protected Context context;

    protected View rootView;

    protected ExpandableListView faq_list;

    private Button faq_contact;

    protected LayoutBackListener listener;

    private RelativeLayout faq_layout_outmost;

    private TextView title_text;

    private WebView faq_web;

    private ProgressBar progressBar;

    private int type;

    public static final int LOGIN_FAQ = 0;

    public static final int DEVICE_FAQ = 1;

    public static final int LOCATION_FAQ = 2;

    public static final int ALARM_FAQ = 3;

    public static final int TRACK_FAQ = 4;

    public static final int ORDER_FAQ = 5;

    public static final int HOT_FAQ = 6;

    private boolean isloadUrl = true;

    private RelativeLayout nodata_layout;

    private String urlStr = HttpUtils.HTTPHOST + HttpUtils.FAQDIR + "commom_probleam.html";


    public FAQWidget(int type, Context context, LayoutBackListener listener) {
        this.type = type;
        this.context = context;
        this.listener = listener;
        rootView = View.inflate(context, R.layout.activity_faq, null);
        initTitle(rootView);
        initView(rootView);
    }

    public FAQWidget(int type, Context context, View rootView, LayoutBackListener listener) {
        this.type = type;
        this.context = context;
        this.listener = listener;
        this.rootView = rootView;
        initTitle(rootView);
        initView(rootView);
    }

    public View getView(){
        return rootView;
    }

    private void initView(View rootview){

        faq_list = (ExpandableListView) rootview.findViewById(R.id.faq_list);

        if(isloadUrl) {
            faq_list.setVisibility(View.GONE);

            faq_web = (WebView) rootview.findViewById(R.id.faq_web);

            progressBar = (ProgressBar) rootview.findViewById(R.id.progressBar);

            nodata_layout = (RelativeLayout) rootview.findViewById(R.id.nodata_layout);
            nodata_layout.setVisibility(View.GONE);

            View.OnClickListener refreshWeb = new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    faq_web.loadUrl(urlStr);
                }
            };

            ImageView useguide_nodata_icon = (ImageView) rootview.findViewById(R.id.useguide_nodata_icon);
            TextView nodata_hint = (TextView) rootview.findViewById(R.id.nodata_hint);
            useguide_nodata_icon.setOnClickListener(refreshWeb);
            nodata_hint.setOnClickListener(refreshWeb);

            initWebView();

        }

        // 联系客服
        faq_contact = (Button) rootview.findViewById(R.id.faq_contact);

        faq_layout_outmost = (RelativeLayout) rootview.findViewById(R.id.faq_layout_outmost);

    }

    private void initWebView(){

        //声明WebSettings子类
        WebSettings webSettings = faq_web.getSettings();

        webSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        if(HttpUtils.isNetWorkConnected()) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        //webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //不使用缓存\

        faq_web.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                String title = view.getTitle();
                if (title.contains("404") || title.contains("500") || title.toLowerCase().contains("error")) {
//                    System.out.println("onPageFinished " + title);
                    showNodata();
                }else{
                    nodata_layout.setVisibility(View.GONE);
                    faq_web.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                System.out.println("onReceivedError ");
                showNodata();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                //Android 6.0以上判断是否请求失败回掉
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Uri url = request.getUrl();
                    if(urlStr.equals(url.toString())) {
                        showNodata();
//                        System.out.println("onReceivedHttpError ");
                    }
                }
            }

        });

        faq_web.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("404") || title.contains("500") || title.toLowerCase().contains("error")) {
//                        System.out.println("onReceivedTitle " + title);
                        showNodata();
                    }
                }
            }
        });

        faq_web.loadUrl(urlStr);
    }

    private void showNodata(){
        nodata_layout.setVisibility(View.VISIBLE);
        faq_web.setVisibility(View.GONE);
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(faq_layout_outmost != null) {
            faq_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    private void initTitle(View rootiew) {

        ImageButton title_back = (ImageButton) rootiew.findViewById(R.id.title_back);
        title_text = (TextView) rootiew.findViewById(R.id.title_text);
        ImageButton title_search = (ImageButton) rootiew.findViewById(R.id.title_search);

        title_text.setText(context.getString(R.string.faq_title));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

        title_search.setVisibility(View.GONE);

        RelativeLayout title_layout = (RelativeLayout) rootView.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }

    public void setTitle(String title){
        if(title_text != null) {
            title_text.setText(title);
        }
    }

    public void updata(){

        if(isloadUrl) {
            faq_web.loadUrl(urlStr);
            return;
        }

        String[] faqPsroblem = getFaqPsroblemOfType();
        String[] faqResult = getFaqResultOfType();

        FAQAdapter faqAdapter = new FAQAdapter(faqPsroblem, faqResult, context);

        faq_list.setAdapter(faqAdapter);
        View footView = View.inflate(context, R.layout.faq_footview, null);
        faq_list.addFooterView(footView);
    }

    public void updataNotFoot(){
        String[] faqPsroblem = getFaqPsroblemOfType();
        String[] faqResult = getFaqResultOfType();

        FAQAdapter faqAdapter = new FAQAdapter(faqPsroblem, faqResult, context);

        faq_list.setAdapter(faqAdapter);
       /* View footView = View.inflate(context, R.layout.faq_footview, null);
        faq_list.addFooterView(footView);*/
    }

    private String[] getFaqPsroblemOfType(){
        switch (type){
            case LOGIN_FAQ:

            case DEVICE_FAQ:

            case LOCATION_FAQ:

            case ALARM_FAQ:

            case TRACK_FAQ:

            case ORDER_FAQ:

            case HOT_FAQ:

            default:
            return new String[]{"登录密码忘了，提示“账户或密码错误”怎么办？","查看轨迹拉直线是什么原因？","设备为什么显示离线？","车开走了，但车还静止在原地实什么原因？",/*"登录密码忘了，提示“账户或密码错误”怎么办？","查看轨迹拉直线是什么原因？","设备为什么显示离线？","车开走了，但车还静止在原地实什么原因？","登录密码忘了，提示“账户或密码错误”怎么办？","查看轨迹拉直线是什么原因？","设备为什么显示离线？","车开走了，但车还静止在原地实什么原因？"*/};
        }
//        return null;
    }

    private String[] getFaqResultOfType(){
        switch (type){
            case LOGIN_FAQ:

            case DEVICE_FAQ:

            case LOCATION_FAQ:

            case ALARM_FAQ:

            case TRACK_FAQ:

            case ORDER_FAQ:

            case HOT_FAQ:

            default:
               return new String[]{"登录密码忘了，提示“账户或密码错误”怎么办","查看轨迹拉直线是什么原因","设备为什么显示离线","车开走了，但车还静止在原地实什么原因",/*"登录密码忘了，提示“账户或密码错误”怎么办","查看轨迹拉直线是什么原因","设备为什么显示离线","车开走了，但车还静止在原地实什么原因","登录密码忘了，提示“账户或密码错误”怎么办","查看轨迹拉直线是什么原因","设备为什么显示离线","车开走了，但车还静止在原地实什么原因"*/};
        }
//        return null;
    }

    public boolean onKeyBack(){
        if(isloadUrl && faq_web.canGoBack()){
            faq_web.goBack();
            return true;
        }
        return false;
    }


}
