package com.easyder.carmonitor.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.activity.FAQActiviry;
import com.easyder.carmonitor.activity.FeedbackActivity;
import com.easyder.carmonitor.activity.PushMessageActivity;
import com.easyder.carmonitor.activity.SettingsActivity;
import com.easyder.carmonitor.activity.UseGuideActivity;
import com.easyder.carmonitor.adapter.UserMenuItemAdapter;
import com.easyder.carmonitor.dialog.orderDialog.OrderManagerDialog;
import com.easyder.carmonitor.interfaces.RecoverMarkerInterface;
import com.easyder.carmonitor.presenter.MainActivityPresenter;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.DatagramPacketDefine;
import com.shinetech.mvp.utils.HttpUtils;
import com.shinetech.mvp.utils.UIUtils;

import java.net.URL;

/**
 * Created by ljn on 2017/2/24.
 */
public class UserMenuDialog extends BasePopupWindowDialog implements View.OnTouchListener, AdapterView.OnItemClickListener {

//    private RoundImageView settings_usericon;
    private TextView settings_username;
    private TextView settings_userinfo;
    private LinearLayout car_count_layout;
    private ListView settings_list;

//    private ImageView advertising_image;
    private WebView advertising_web;

    private ScrollView settings_content;
    private RelativeLayout settings_layout_outmost;

    private UserMenuItemAdapter settingsItemAdapter;

    private boolean isTouch = false;

    private View boundView;

    private MainActivityPresenter presenter;

    private boolean isSslError = false;

//    private boolean isloadAdvertisementError = false;

    public UserMenuDialog(Context context,MainActivityPresenter presenter) {
        super(context, R.layout.main_user_menu, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));
//        super(context, R.layout.main_user_menu, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.presenter = presenter;
        initView();
        upData();
        setALLWindow();
        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, settings_content, R.anim.pop_left2right_anim_close));
//        setBackgrund(new ColorDrawable((Color.parseColor("#00000000"))));

    }

    public void updateView(){
        setHeight((UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight()));
    }


    private void initView(){
        View layout = getLayout();

//        settings_usericon = (RoundImageView) layout.findViewById(R.id.settings_usericon);
        settings_username = (TextView) layout.findViewById(R.id.settings_username);
        settings_userinfo = (TextView) layout.findViewById(R.id.settings_userinfo);
        car_count_layout = (LinearLayout) layout.findViewById(R.id.car_count_layout);
        settings_list = (ListView) layout.findViewById(R.id.settings_list);
//        advertising_image = (ImageView) layout.findViewById(R.id.advertising_image);
        advertising_web = (WebView) layout.findViewById(R.id.advertising_web);

        settings_content = (ScrollView) layout.findViewById(R.id.settings_content_layout);
        settings_layout_outmost = (RelativeLayout) layout.findViewById(R.id.settings_layout_outmost);

        settings_layout_outmost.setOnTouchListener(this);

        loadAdvertising();

    }

    /**
     * 加载广告内容
     */
    private void loadAdvertising(){
        URL url = null;
        String url_str = getURL();
//        isloadAdvertisementError = false;

        try {

            advertising_web.setVisibility(View.GONE);

            WebView mWebView = new WebView(context);
            //声明WebSettings子类
            WebSettings webSettings = mWebView.getSettings();

            webSettings.setJavaScriptEnabled(true);
            webSettings.setSupportMultipleWindows(false);

            //设置自适应屏幕，两者合用
            webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
            webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

            if(HttpUtils.isNetWorkConnected()) {
                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            }else {
                webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
            mWebView.setHorizontalScrollBarEnabled(false);
            mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

            mWebView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
//                    System.out.println("onReceivedTitle  title = "+title);
                    // android 6.0 以下通过title获取
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        if (title.contains("404") || title.contains("500") || title.toLowerCase().contains("error")) {
//                            view.loadUrl("about:blank");// 避免出现默认的错误界面
                            advertising_web.setVisibility(View.GONE);
//                            isloadAdvertisementError = true;
                        }
                    }
                }
            });
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
//                    System.out.println("errorCode = "+errorCode);
                    advertising_web.setVisibility(View.GONE);
//                    isloadAdvertisementError = true;
                }

                @Override
                public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                    super.onReceivedHttpError(view, request, errorResponse);
                    //Android 6.0以上判断是否请求失败回掉
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        int statusCode = errorResponse.getStatusCode();
//                        System.out.println("getStatusCode = " + statusCode);
                        advertising_web.setVisibility(View.GONE);
//                        isloadAdvertisementError = true;
                    }
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                   /*
                   //当出现 ssl error 的时候，直接忽略，依旧打开网页。
                    handler.proceed();
                    */
                    super.onReceivedSslError(view, handler, error);
                    isSslError = true;
//                    System.out.println("onPageFinished getTitle = " + view.getTitle() + "SslError : "+error.toString());
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
//                    System.out.println("onPageFinished getTitle = " + view.getTitle() + "url : "+url);
//                    view.
//                    防止加载缓存中的404 或错误页面
                    String title = view.getTitle();
                    if (isSslError || title.contains("404") || title.contains("500") || title.toLowerCase().contains("error")) {
                        advertising_web.setVisibility(View.GONE);
//                        isloadAdvertisementError = true;
                    }else{

                        toloadData();
                    }
                }
            });

            mWebView.loadUrl(url_str);


        } catch (Exception e) {

            advertising_web.setVisibility(View.GONE);
        }
    }

    private String getURL(){

        return HttpUtils.HTTPHOST + HttpUtils.ADVERTISEMENTDIR+getFileName();
    }

    private String getFileName(){

        String url_str = "";

        byte loginType = UserInfo.getInstance().getLoginType();

        switch (loginType){
            case DatagramPacketDefine.USER_TYPE:
                url_str ="advertisement_personage.png";
                break;
            case DatagramPacketDefine.GROUP_TYPE:
                url_str ="advertisement_group.png";
                break;
            case DatagramPacketDefine.COMPANY_TYPE:
                url_str = "advertisement_company.png";
                break;
            case DatagramPacketDefine.GIS_TYPE:
                url_str = "advertisement_gis.png";
                break;
        }
        return url_str;
    }

    public void toloadData(){

        //声明WebSettings子类
        WebSettings webSettings = advertising_web.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(false);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        if(HttpUtils.isNetWorkConnected()) {
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        advertising_web.setHorizontalScrollBarEnabled(false);
        advertising_web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        String content = "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"></head><body><img src=\""+HttpUtils.ADVERTISEMENTDIR+getFileName()+"\"/></body></html>";

        advertising_web.loadDataWithBaseURL(HttpUtils.HTTPHOST, content, "text/html", "utf-8", null);

        advertising_web.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                advertising_web.loadUrl("javascript:var imgs=document.getElementsByTagName('img');for(var i=0;i<imgs.length;i++){imgs[i].style.width='100%';};void(0);");
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        if(!isloadAdvertisementError) {
                            advertising_web.setVisibility(View.VISIBLE);
//                        }
                    }
                },1000);

            }
        });
    }

    public void updateMessage(){
        if(settingsItemAdapter != null){
            settingsItemAdapter.notifyDataSetChanged();
        }

    }

    public void upData(){

        String userName = UserInfo.getInstance().getUserName();
        settings_username.setText(TextUtils.isEmpty(userName) ? "" : userName);

        //设置车辆数量
        if(!UserInfo.getInstance().isPerson()){
            car_count_layout.setVisibility(View.VISIBLE);
            String count="";

            if(UserInfo.getInstance().getPlateNumberList()==null){
                count = "0";
            }else{
                count = UserInfo.getInstance().getPlateNumberList().size()+"";
            }

            settings_userinfo.setText(count);
/*
            String userinfo = context.getString(R.string.settings_userinfo, count);

            int index = userinfo.indexOf(count);

            SpannableStringBuilder style=new SpannableStringBuilder(userinfo);

            //数量字体修改颜色
            style.setSpan(new ForegroundColorSpan(0xFF3698DA), index, index+count.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            style.setSpan(new RelativeSizeSpan(1.3f), index, index+count.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.setSpan(new StyleSpan(Typeface.BOLD), index, index+count.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            settings_userinfo.setText(style);*/
        }else{
            car_count_layout.setVisibility(View.GONE);
//            settings_userinfo.setVisibility(View.GONE);
        }

        //items
        String[] stringArray ;
        if(UserInfo.getInstance().getUserPermission() == UserInfo.USER){
            stringArray = context.getResources().getStringArray(R.array.settings_items_user);
        }else {
            stringArray = context.getResources().getStringArray(R.array.settings_items);
        }

        int[] icon = new int[]{R.mipmap.icon_guide,
                                R.mipmap.icon_help,
                                R.mipmap.icon_advice,
                                R.mipmap.icon_message,
                                R.mipmap.orderinfo_cion,
                                R.mipmap.icon_setting
        };

        settingsItemAdapter = new UserMenuItemAdapter(context, stringArray,icon);

        settings_list.setAdapter(settingsItemAdapter);

        settings_list.setOnItemClickListener(this);

    }

    public void show(View v){
        boundView = v;
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        settings_content.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_left2right_anim_open));
        isTouch = false;
        loadAdvertising();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            dismiss();
            presenter.recoverMarkerDialog();
            isTouch = true;
        }
        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
//                使用指南
                /*final UseGuideDialog mUseGuideDialog = new UseGuideDialog(context);
                mUseGuideDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ConfigurationChangedManager.getInstance().unRegistConfig(mUseGuideDialog);
                        if(mUseGuideDialog.needRecorve) {
                            presenter.recoverMarkerDialog();
                        }
                        removeInDialogManager(mUseGuideDialog);
                    }
                });

                mUseGuideDialog.show(boundView);
                ConfigurationChangedManager.getInstance().registConfig(mUseGuideDialog);
                addToDialogManager(mUseGuideDialog);*/

                dismiss();
                Intent guideintent = new Intent(context, UseGuideActivity.class);
                context.startActivity(guideintent);
                ((Activity)context).overridePendingTransition(R.anim.pop_right2left_anim_open, R.anim.pop_right2left_anim_close);
                isTouch = true;

                break;
            case 1:

                if(CarMonitorApplication.isUserActivityMode){

                    Intent intent = new Intent(context, FAQActiviry.class);
                    context.startActivity(intent);
                    dismiss();
                    isTouch = true;
                }else{
//                  常见问题
                    final FAQDialog mFAQDialog = new FAQDialog(context);
                    mFAQDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            ConfigurationChangedManager.getInstance().unRegistConfig(mFAQDialog);
                            if(mFAQDialog.needRecorve) {
                                presenter.recoverMarkerDialog();
                            }
                            removeInDialogManager(mFAQDialog);

                        }
                    });
                    mFAQDialog.show(boundView);
                    ConfigurationChangedManager.getInstance().registConfig(mFAQDialog);
                    addToDialogManager(mFAQDialog);
                    dismiss();
                    isTouch = true;
                }
                break;
            case 2:

                if(CarMonitorApplication.isUserActivityMode){
                    Intent intent = new Intent(context, FeedbackActivity.class);
                    context.startActivity(intent);
                    dismiss();
                    isTouch = true;
                }else {
//                意见反馈
                    final FeedbackDialog mFeedbackDialog = new FeedbackDialog(context, presenter);
                    mFeedbackDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            ConfigurationChangedManager.getInstance().unRegistConfig(mFeedbackDialog);
                            removeInDialogManager(mFeedbackDialog);
                            presenter.recoverMarkerDialog();
                        }
                    });
                    mFeedbackDialog.show(boundView);
                    ConfigurationChangedManager.getInstance().registConfig(mFeedbackDialog);
                    addToDialogManager(mFeedbackDialog);
                    dismiss();
                    isTouch = true;
                }

                break;
            case 3:
                if(CarMonitorApplication.isUserActivityMode){
                    Intent intent = new Intent(context, PushMessageActivity.class);
                    context.startActivity(intent);
                    dismiss();
                    isTouch = true;
                }else {
//                系统消息
                    final PushMessageDialog mPushMessageDialog = new PushMessageDialog(context);
                    mPushMessageDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            ConfigurationChangedManager.getInstance().unRegistConfig(mPushMessageDialog);
                            removeInDialogManager(mPushMessageDialog);
                            presenter.recoverMarkerDialog();
                        }
                    });
                    mPushMessageDialog.show(boundView);
                    ConfigurationChangedManager.getInstance().registConfig(mPushMessageDialog);
                    addToDialogManager(mPushMessageDialog);
                    dismiss();
                    isTouch = true;
                }
                break;
            case 4:
                //工单管理
                final OrderManagerDialog orderManagerDialog = new OrderManagerDialog(context);
                orderManagerDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ConfigurationChangedManager.getInstance().unRegistConfig(orderManagerDialog);
                        if(orderManagerDialog.needRecorve){
                            presenter.recoverMarkerDialog();
                        }
                        removeInDialogManager(orderManagerDialog);

                    }
                });
                orderManagerDialog.show(boundView);
                ConfigurationChangedManager.getInstance().registConfig(orderManagerDialog);
                addToDialogManager(orderManagerDialog);
                dismiss();
                isTouch = true;
                break;
            case 5:

                if(CarMonitorApplication.isUserActivityMode){
                    Intent intent = new Intent(context, SettingsActivity.class);
                    context.startActivity(intent);
                    dismiss();
                    isTouch = true;
                }else {
//                设置
                    final SettingsDialog mSettingsDialog = new SettingsDialog(context);
                    mSettingsDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            ConfigurationChangedManager.getInstance().unRegistConfig(mSettingsDialog);
                            if(mSettingsDialog.needRecorve){
                                presenter.recoverMarkerDialog();
                            }
                            removeInDialogManager(mSettingsDialog);

                        }
                    });
                    mSettingsDialog.setRecoverMarkerInterface(new RecoverMarkerInterface() {
                        @Override
                        public void todoRecoverMarker() {
                            presenter.recoverMarkerDialog();
                        }
                    });
                    mSettingsDialog.show(boundView);
                    ConfigurationChangedManager.getInstance().registConfig(mSettingsDialog);
                    addToDialogManager(mSettingsDialog);
                    dismiss();
                    isTouch = true;
                }

                break;


        }

    }

    private void removeInDialogManager(BasePopupWindowDialog dialog){
        presenter.removeDailogOfManager(dialog);
    }

    public void addToDialogManager(BasePopupWindowDialog dialog){
        presenter.addDialogOfManager(dialog);
    }
}
