package com.easyder.carmonitor.widget;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.SettingsItemListener;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-13.
 */
public class SettingsWidget implements View.OnClickListener {

    private Context context;

    private View rootView;

    private SettingsItemListener listener;

    /**
     * 退出登陆
     */
    private Button logout_btn;

    /**
     * 推荐给朋友
     */
    private TextView share;

    /**
     * 关于我们
     */
    private TextView about_us;

    /**
     * 离线地图
     */
    private TextView offline_map;


    private TextView road_time_tv;

    /**
     * 路况刷新设置
     */
    private RelativeLayout road_refresh_settings;

    private TextView car_refresh_time_tv;

    /**
     * 车辆刷新设置
     */
    private RelativeLayout car_refresh_settings;

    private RelativeLayout settings_layout_outmost;



    public SettingsWidget(Context context, SettingsItemListener listener) {
        this.context = context;
        this.listener = listener;
        rootView = View.inflate(context, R.layout.activity_settings, null);
        initTitle(rootView);
        initView(rootView);

    }

    public SettingsWidget(Context context, View rootView, SettingsItemListener listener) {
        this.context = context;
        this.listener = listener;
        this.rootView = rootView;
        initTitle(rootView);
        initView(rootView);

    }

    private void initTitle(View rootiew) {

        ImageButton title_back = (ImageButton) rootiew.findViewById(R.id.title_back);
        TextView title_text = (TextView) rootiew.findViewById(R.id.title_text);
        ImageButton title_search = (ImageButton) rootiew.findViewById(R.id.title_search);

        title_text.setText(context.getString(R.string.settings_title));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

        title_search.setVisibility(View.GONE);

        RelativeLayout title_layout = (RelativeLayout) rootiew.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }

    public View getView(){
        return rootView;
    }

    private void initView(View rootview){

        logout_btn = (Button) rootview.findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(this);

        share = (TextView) rootview.findViewById(R.id.share);
        share.setOnClickListener(this);

        about_us = (TextView) rootview.findViewById(R.id.about_us);
        about_us.setOnClickListener(this);

        offline_map = (TextView) rootview.findViewById(R.id.offline_map);
        offline_map.setOnClickListener(this);

        road_time_tv = (TextView) rootview.findViewById(R.id.road_time_tv);
        road_refresh_settings = (RelativeLayout) rootview.findViewById(R.id.road_refresh_settings);
        road_refresh_settings.setOnClickListener(this);

        car_refresh_time_tv = (TextView) rootview.findViewById(R.id.car_refresh_time_tv);
        car_refresh_settings = (RelativeLayout) rootview.findViewById(R.id.car_refresh_settings);
        car_refresh_settings.setOnClickListener(this);

        settings_layout_outmost  = (RelativeLayout) rootview.findViewById(R.id.settings_layout_outmost);

    }

    public void setCarRefreshTime(String time){
        if(car_refresh_time_tv != null) {
            car_refresh_time_tv.setText(time);
        }
    }

    public void setRoadRefreshTime(String time){
        if(road_time_tv != null){
            road_time_tv.setText(time);
        }
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(settings_layout_outmost != null){
            settings_layout_outmost.setOnTouchListener(touchListener);
        }
    }


    @Override
    public void onClick(View v) {

        if(listener == null)
            return;

        switch (v.getId()){

            case R.id.logout_btn:
                listener.onClickLogout();
                break;

            case R.id.share:
                listener.onClickShare();
                break;

            case R.id.about_us:
                listener.onClickAboutUS();
                break;

            case R.id.offline_map:
                listener.onClickOffLineMap();
                break;

            case R.id.road_refresh_settings:
                listener.refreshRoad();
                break;

            case R.id.car_refresh_settings:
                listener.refreshCar();
                break;
        }

    }
}
