package com.easyder.carmonitor.widget;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-28.
 */
public class AboutUsWidget {

    private Context context;

    private LayoutBackListener listener;

    private View rootView;

    private TextView contact_us;

    private RelativeLayout about_layout_outmost;

    public AboutUsWidget(Context context, LayoutBackListener listener) {
        this.context = context;
        this.listener = listener;
        rootView = View.inflate(context, R.layout.activity_about_us, null);
        initTitle(rootView);
        initView();
    }

    public AboutUsWidget(Context context, View rootView, LayoutBackListener listener) {
        this.context = context;
        this.listener = listener;
        this.rootView = rootView;
        initTitle(this.rootView);
        initView();
    }

    public View getView(){
        return rootView;
    }

    private void initTitle(View rootview){

        ImageButton title_back = (ImageButton) rootview.findViewById(R.id.title_back);
        TextView title_text = (TextView) rootview.findViewById(R.id.title_text);
        ImageButton title_search = (ImageButton) rootview.findViewById(R.id.title_search);

        title_text.setText(context.getString(R.string.about_title));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

        title_search.setVisibility(View.GONE);

        RelativeLayout title_layout = (RelativeLayout) rootview.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }

    public void initView(){
        TextView app_version = (TextView) rootView.findViewById(R.id.app_version_string);
        TextView app_name = (TextView) rootView.findViewById(R.id.app_name);
//        TextView publish_time = (TextView) rootView.findViewById(R.id.publish_time);
//        TextView official_web = (TextView) rootView.findViewById(R.id.official_web);
        TextView wechat_search_car = (TextView) rootView.findViewById(R.id.wechat_search_car);

        contact_us = (TextView) rootView.findViewById(R.id.contact_us);
        about_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.about_layout_outmost);

        wechat_search_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        app_name.setText(context.getString(R.string.app_name) + " "+getVersionName());
        app_version.setText(getVersionName());
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(about_layout_outmost != null) {
            about_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    public void setContactUsClick(View.OnClickListener clickListener){
        contact_us.setOnClickListener(clickListener);
    }

    public String getVersionName(){
        try {
            String pkName = context.getPackageName();
            PackageManager packageManager = context.getPackageManager();

            String versionName = packageManager.getPackageInfo(
                    pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {
        }
        return "2.0";
    }
}
