package com.easyder.carmonitor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.dialog.SettingSelectDialog;
import com.easyder.carmonitor.interfaces.SettingSelectListener;
import com.easyder.carmonitor.interfaces.SettingsItemListener;
import com.easyder.carmonitor.widget.SettingsWidget;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.UDPHeartBeatConnect;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.ShareUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-05-23.
 */
public class SettingsActivity extends BaseActivity implements SettingsItemListener, View.OnTouchListener {


    private SettingsWidget settingsWidget;

    private boolean isTouch = false;

    private boolean isDebug = false && LogUtils.isDebug;;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsWidget = new SettingsWidget(this, this);
        settingsWidget.setOutmostTouchListener(this);

        setContentView(settingsWidget.getView());

        initView();
    }

    private void initView(){
        String road_time = getString(R.string.settings_no_road_refresh);
        int road_refresh_time = CarMonitorApplication.getPreferences().getInt(CarMonitorApplication.ROAD_REFRESH_KEY, 0);
        if(road_refresh_time != 0){
            road_time = road_refresh_time+getString(R.string.settings_road_refresh_unit);
        }
        settingsWidget.setRoadRefreshTime(road_time);

        int car_refresh_time = CarMonitorApplication.getPreferences().getInt(CarMonitorApplication.CAR_REFRESH_KEY, 15);
        settingsWidget.setCarRefreshTime(car_refresh_time + getString(R.string.settings_car_refresh_unit));
    }

    @Override
    protected int getView() {
        return 0;
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return null;
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void showContentView(BaseVo dataVo) {

    }

    @Override
    public void onStopLoading() {

    }

    @Override
    public void onBack() {
        finish();
    }

    @Override
    public void onClickLogout() {
        UDPHeartBeatConnect.getInstance().onExit();

        List<Activity> list = new ArrayList<>();
        List<Activity> activityList = CarMonitorApplication.getInstance().getActivityList();
        list.addAll(activityList);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
//        overridePendingTransition(R.anim.pop_right2left_anim_open, R.anim.pop_right2left_anim_close);

        ((CarMonitorApplication)CarMonitorApplication.getInstance()).finishAllActivity(list);

    }

    @Override
    public void onClickAboutUS() {

        Intent intent = new Intent(this,AboutUsActivity.class);
        startActivity(intent);
//        overridePendingTransition(R.anim.pop_right2left_anim_open, R.anim.pop_right2left_anim_close);

    }

    @Override
    public void onClickShare() {

        //分享类型 QZONE
        ShareUtils.shareQzone(this, new IUiListener() {
            @Override
            public void onComplete(Object o) {

                if(isDebug)System.out.println(" IUiListener onComplete : " + o.toString());

            }

            @Override
            public void onError(UiError uiError) {
                if(isDebug)System.out.println(" IUiListener onError code : " + uiError.errorCode + " , msg : " + uiError.errorMessage + " , detail : " + uiError.errorDetail);

            }

            @Override
            public void onCancel() {
                if(isDebug)System.out.println(" IUiListener onCancel . . . . . ");
            }
        });


        // QQ
        /*ShareUtils.shareQQ((Activity)context,new IUiListener() {
            @Override
            public void onComplete(Object o) {

                System.out.println(" IUiListener onComplete : "+o.toString());

            }

            @Override
            public void onError(UiError uiError) {
                System.out.println(" IUiListener onError code : " + uiError.errorCode + " , msg : " + uiError.errorMessage + " , detail : " + uiError.errorDetail);

            }

            @Override
            public void onCancel() {
                System.out.println(" IUiListener onCancel . . . . . ");
            }
        });*/

    }

    @Override
    public void onClickOffLineMap() {

        Intent intent = new Intent(this, OffLineMapActivity.class);
        startActivity(intent);
//        overridePendingTransition(R.anim.pop_right2left_anim_open, R.anim.pop_right2left_anim_close);
        finish();

    }

    @Override
    public void refreshRoad() {

        String[] stringArray = getResources().getStringArray(R.array.road_refresh_settings_display);

        int anInt = CarMonitorApplication.getPreferences().getInt(CarMonitorApplication.ROAD_REFRESH_KEY, 0);

        int index = 0;
        for(int i = 0; i<stringArray.length; i++){
            if(stringArray[i].startsWith(anInt + "")){
                index = i;
                break;
            }
        }

        SettingSelectDialog mSettingSelectDialog = new SettingSelectDialog(this, stringArray, "", index);
        mSettingSelectDialog.setTitle(this.getString(R.string.road_refresh_settings));
        mSettingSelectDialog.setOnSelectListener(new SettingSelectListener() {
            @Override
            public void onSelect(String select) {
                int roadRefreshTiem = getRoadRefreshTiem(select);
                CarMonitorApplication.getPreferences().edit().putInt(CarMonitorApplication.ROAD_REFRESH_KEY, roadRefreshTiem).commit();
                settingsWidget.setRoadRefreshTime(select);
            }
        });
        mSettingSelectDialog.show(settingsWidget.getView());

    }

    @Override
    public void refreshCar() {
        String[] stringArray = getResources().getStringArray(R.array.car_refresh_settings_display);

        int anInt = CarMonitorApplication.getPreferences().getInt(CarMonitorApplication.CAR_REFRESH_KEY, 15);

        int index = 0;
        for(int i = 0; i<stringArray.length; i++){
            if(stringArray[i].startsWith(anInt+"")){
                index = i;
                break;
            }
        }

        SettingSelectDialog mSettingSelectDialog = new SettingSelectDialog(this, stringArray, "", index);
        mSettingSelectDialog.setTitle(getString(R.string.car_refresh_settings));
        mSettingSelectDialog.setOnSelectListener(new SettingSelectListener() {
            @Override
            public void onSelect(String select) {
                int carRefreshTime = getCarRefreshTime(select);
                CarMonitorApplication.getPreferences().edit().putInt(CarMonitorApplication.CAR_REFRESH_KEY, carRefreshTime).commit();
                CarMonitorApplication.REFER_MARKER_TIME = carRefreshTime * 1000;
                settingsWidget.setCarRefreshTime(select);

            }
        });
        mSettingSelectDialog.show(settingsWidget.getView());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            finish();
            isTouch = true;
        }
        return true;
    }

    public int getCarRefreshTime(String select) {

        String time = select.replace(getString(R.string.settings_car_refresh_unit), "");

        try {
            return Integer.parseInt(time);
        }catch (Exception e){
            return 15;
        }
    }

    public int getRoadRefreshTiem(String select){
        if("".equals(select)){
//            ToastUtil.showShort("road refresh time = "+0);
            return 0;
        } else {
            String time = select.replace(getString(R.string.settings_road_refresh_unit),"");
//            ToastUtil.showShort("road refresh time = "+time);
            try {
                return Integer.parseInt(time);

            }catch (Exception e){
                return 0;
            }
        }
    }
}
