package com.easyder.carmonitor.widget;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.activity.AccountExplainActivity;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.shinetech.mvp.utils.HttpUtils;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-12.
 */
public class AccountExplainWidget {

    protected Context context;

    protected View rootView;

    protected LayoutBackListener listener;

    private RelativeLayout useguide_layout_outmost;

    private TextView title_text;

    private WebView useguide_web;

    private ProgressBar progressBar;

    private RelativeLayout useguide_content;

    private RelativeLayout nodata_layout;

    private String urlStr = HttpUtils.HTTPHOST + HttpUtils.ACCOUNTEXPLAIN+ "account_explain.html";


    public AccountExplainWidget(Context context, LayoutBackListener listener) {
        this.context = context;
        this.listener = listener;
        rootView = View.inflate(context, R.layout.activity_useguide, null);
        initTitle(rootView);
        initView(rootView);
    }

    public AccountExplainWidget(Context context, View rootView, LayoutBackListener listener) {
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


        useguide_web = (WebView) rootview.findViewById(R.id.useguide_web);

        progressBar = (ProgressBar) rootview.findViewById(R.id.progressBar);

        useguide_content = (RelativeLayout) rootview.findViewById(R.id.useguide_content);

        nodata_layout = (RelativeLayout) rootview.findViewById(R.id.nodata_layout);


        initWebView();

        useguide_layout_outmost = (RelativeLayout) rootview.findViewById(R.id.useguide_layout_outmost);

        View.OnClickListener refreshWeb = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                useguide_web.loadUrl(urlStr);
            }
        };

        ImageView useguide_nodata_icon = (ImageView) rootview.findViewById(R.id.useguide_nodata_icon);
        TextView nodata_hint = (TextView) rootview.findViewById(R.id.nodata_hint);
        useguide_nodata_icon.setOnClickListener(refreshWeb);
        nodata_hint.setOnClickListener(refreshWeb);

    }

    private void initWebView(){

        //声明WebSettings子类
       WebSettings webSettings = useguide_web.getSettings();

        webSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
         webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        if(HttpUtils.isNetWorkConnected()) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        useguide_web.setHorizontalScrollBarEnabled(false);
        useguide_web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        useguide_web.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                String title = view.getTitle();
                if (title.contains("404") || title.contains("500") || title.toLowerCase().contains("error")) {
//                    System.out.println("onPageFinished title = "+title);
                    showNodata();
                }else{
                    useguide_content.setVisibility(View.VISIBLE);
                    useguide_web.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                showNodata();
//                System.out.println("onReceivedError ");
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

        useguide_web.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (title.contains("404") || title.contains("500") || title.toLowerCase().contains("error")) {
//                        System.out.println("onReceivedTitle title = "+title);
                        showNodata();
                    }
                }
            }
        });

        useguide_web.loadUrl(urlStr);
    }

    private void showNodata(){
        useguide_content.setVisibility(View.GONE);
        nodata_layout.setVisibility(View.VISIBLE);
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(useguide_layout_outmost != null) {
            useguide_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    private void initTitle(View rootiew) {

        ImageButton title_back = (ImageButton) rootiew.findViewById(R.id.title_back);
        title_text = (TextView) rootiew.findViewById(R.id.title_text);
        ImageButton title_search = (ImageButton) rootiew.findViewById(R.id.title_search);

        title_text.setText(context.getString(R.string.account_explain));

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

    public boolean onKeyBack(){
        if( useguide_web.canGoBack()){
            useguide_web.goBack();
            return true;
        }
        return false;
    }


}
