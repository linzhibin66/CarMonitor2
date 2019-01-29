package com.easyder.carmonitor.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.activity.LoginActivity;
import com.easyder.carmonitor.activity.OffLineMapActivity;
import com.easyder.carmonitor.interfaces.RecoverMarkerInterface;
import com.easyder.carmonitor.interfaces.SettingSelectListener;
import com.easyder.carmonitor.interfaces.SettingsItemListener;
import com.easyder.carmonitor.widget.SettingsWidget;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.UDPHeartBeatConnect;
import com.shinetech.mvp.network.UDP.listener.HeartBeatListener;
import com.shinetech.mvp.utils.ShareUtils;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-04-13.
 */
public class SettingsDialog extends BasePopupWindowDialog implements SettingsItemListener, View.OnTouchListener {

    private View bundView;

    private SettingsWidget settingsWidget;

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private boolean debug = false;

    private boolean isTouch = false;

    public boolean needRecorve = true;

    private RecoverMarkerInterface mRecoverMarkerInterface;

    public SettingsDialog(Context context) {
        super(context, R.layout.activity_settings, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));
//        super(context, R.layout.activity_settings, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        settingsWidget = new SettingsWidget(context, getLayout(), this);
        settingsWidget.setOutmostTouchListener(this);

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        initView();

        setALLWindow();

        setFocusable(true);

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));
    }

    private void initView(){
        String road_time = context.getString(R.string.settings_no_road_refresh);
        int road_refresh_time = CarMonitorApplication.getPreferences().getInt(CarMonitorApplication.ROAD_REFRESH_KEY, 0);
        if(road_refresh_time != 0){
            road_time = road_refresh_time+context.getString(R.string.settings_road_refresh_unit);
        }
        settingsWidget.setRoadRefreshTime(road_time);

        int car_refresh_time = CarMonitorApplication.getPreferences().getInt(CarMonitorApplication.CAR_REFRESH_KEY, 15);
        settingsWidget.setCarRefreshTime(car_refresh_time + context.getString(R.string.settings_car_refresh_unit));
    }

    public void show(View v){
        bundView = v;
        isTouch = false;
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        getLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open));

    }

    public void noAnimationShow(View v){
        bundView = v;
        isTouch = false;
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
    }

    @Override
    public void onBack() {
        needRecorve = true;
        dismiss();
    }

    @Override
    public void onClickLogout() {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show(bundView);

        UserInfo.getInstance().cleanHistoryUserPwd();
        UDPHeartBeatConnect.getInstance().exit(new HeartBeatListener() {
            @Override
            public void sendToFail() {
            }

            @Override
            public void sendToError() {

                progressDialog.dismiss();

                needRecorve = false;
                noAmimationDismiss();

                List<Activity> list = new ArrayList<>();
                List<Activity> activityList = CarMonitorApplication.getInstance().getActivityList();
                list.addAll(activityList);

                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);

                ((CarMonitorApplication) CarMonitorApplication.getInstance()).finishAllActivity(list);
            }
        });
    }


    @Override
    public void onClickAboutUS() {
        final AboutUsDialog mAboutUsDialog = new AboutUsDialog(context);
        mAboutUsDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ConfigurationChangedManager.getInstance().unRegistConfig(mAboutUsDialog);
                if(mAboutUsDialog.needRecorve && mRecoverMarkerInterface!= null){
                    mRecoverMarkerInterface.todoRecoverMarker();
                }
            }
        });
        mAboutUsDialog.setRecoverMarkerInterface(mRecoverMarkerInterface);
        mAboutUsDialog.show(bundView);
        ConfigurationChangedManager.getInstance().registConfig(mAboutUsDialog);
       /* if(CarMonitorApplication.isUseSingleDialogMode()) {
            mAboutUsDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    needRecorve = true;
                    noAnimationShow(bundView);
                }
            });
            needRecorve = false;
            dismiss();
            isTouch = true;
        }*/
        needRecorve = false;
        dismiss();
        isTouch = true;

    }

    @Override
    public void onClickShare() {
        if(debug)ToastUtil.showShort("onClickShare");
        ShareDialogPlus mShareDialogPlus = new ShareDialogPlus(context);
        mShareDialogPlus.show();
//        ShareUtils.shareWXmoments(context, R.mipmap.ic_launcher);

    }

    @Override
    public void onClickOffLineMap() {
        Intent intent = new Intent(context, OffLineMapActivity.class);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.pop_right2left_anim_open, R.anim.pop_right2left_anim_close);
        needRecorve = false;
        dismiss();
        isTouch = true;
    }

    @Override
    public void refreshRoad() {

        String[] stringArray = context.getResources().getStringArray(R.array.road_refresh_settings_display);

        int anInt = CarMonitorApplication.getPreferences().getInt(CarMonitorApplication.ROAD_REFRESH_KEY, 0);

        int index = 0;
        for(int i = 0; i<stringArray.length; i++){
            if(stringArray[i].startsWith(anInt + "")){
                index = i;
                break;
            }
        }

        SettingSelectDialog mSettingSelectDialog = new SettingSelectDialog(context, stringArray, "", index);
        mSettingSelectDialog.setTitle(context.getString(R.string.road_refresh_settings));
        mSettingSelectDialog.setOnSelectListener(new SettingSelectListener() {
            @Override
            public void onSelect(String select) {
                int roadRefreshTiem = getRoadRefreshTiem(select);
                CarMonitorApplication.getPreferences().edit().putInt(CarMonitorApplication.ROAD_REFRESH_KEY, roadRefreshTiem).commit();
                settingsWidget.setRoadRefreshTime(select);
            }
        });
        mSettingSelectDialog.show(bundView);
    }

    @Override
    public void refreshCar() {

        String[] stringArray = context.getResources().getStringArray(R.array.car_refresh_settings_display);

        int anInt = CarMonitorApplication.getPreferences().getInt(CarMonitorApplication.CAR_REFRESH_KEY, 15);

        int index = 0;
        for(int i = 0; i<stringArray.length; i++){
            if(stringArray[i].startsWith(anInt+"")){
                index = i;
                break;
            }
        }

        if(debug) {

            SettingSelectDialog mSettingSelectDialog = new SettingSelectDialog(context, stringArray, "", index);
            mSettingSelectDialog.setTitle(context.getString(R.string.car_refresh_settings));
            mSettingSelectDialog.setOnSelectListener(new SettingSelectListener() {
                @Override
                public void onSelect(String select) {
                    int carRefreshTime = getCarRefreshTime(select);
                    CarMonitorApplication.getPreferences().edit().putInt(CarMonitorApplication.CAR_REFRESH_KEY, carRefreshTime).commit();
                    CarMonitorApplication.REFER_MARKER_TIME = carRefreshTime * 1000;
                    settingsWidget.setCarRefreshTime(select);

                }
            });
            mSettingSelectDialog.show(bundView);
        }else{
            SettingsDialogPlus settingsDialogPlus = new SettingsDialogPlus(context);
            settingsDialogPlus.initView(stringArray, "", index);
            settingsDialogPlus.setTitle(context.getString(R.string.car_refresh_settings));
            settingsDialogPlus.setOnSelectListener(new SettingSelectListener() {
                @Override
                public void onSelect(String select) {
                    int carRefreshTime = getCarRefreshTime(select);
                    CarMonitorApplication.getPreferences().edit().putInt(CarMonitorApplication.CAR_REFRESH_KEY, carRefreshTime).commit();
                    CarMonitorApplication.REFER_MARKER_TIME = carRefreshTime * 1000;
                    settingsWidget.setCarRefreshTime(select);

                }
            });

            settingsDialogPlus.show();
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            needRecorve = true;
            dismiss();
            isTouch = true;
        }
        return true;
    }

    public int getCarRefreshTime(String select){

        String time = select.replace(context.getString(R.string.settings_car_refresh_unit), "");

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
        }else{
            String time = select.replace(context.getString(R.string.settings_road_refresh_unit),"");
//            ToastUtil.showShort("road refresh time = "+time);
            try {
                return Integer.parseInt(time);

            }catch (Exception e){
                return 0;
            }
        }
    }

    public void setRecoverMarkerInterface(RecoverMarkerInterface inface){
        mRecoverMarkerInterface = inface;
    }




}
