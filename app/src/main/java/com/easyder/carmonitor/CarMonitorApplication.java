package com.easyder.carmonitor;

import android.app.Activity;
import android.os.Build;

import com.easyder.carmonitor.activity.MainActivity;
import com.shinetech.mvp.MainApplication;

import java.util.List;

/**
 * Created by ljn on 2017/2/17.
 */
public class CarMonitorApplication extends MainApplication{

    public static final String ROAD_REFRESH_KEY = "road_refresh_key";
    public static final String CAR_REFRESH_KEY = "car_refresh_key";

    /**
     * 百度统计标志
     */
    private boolean isStartBaiduStatistics = false;

    /**
     * 刷新Marker时间
     */
    public static int REFER_MARKER_TIME;

    /**
     * 启用界面模式（false 为弹框模仿界面）
     */
    public static final boolean isUserActivityMode = false;

    //是否使用单一弹框模式（弹框不能叠加显示）
    private static final boolean USE_SINGLE_DIALOG_MODE = true;

    @Override
    public void onCreate() {
        super.onCreate();
        REFER_MARKER_TIME = getPreferences().getInt(CAR_REFRESH_KEY,15) * 1000;



    }

    public boolean isStartBaiduStatistics(){
        return isStartBaiduStatistics;
    }

    public void setIsStartBaiduStatistics(boolean isStartBaiduStatistics) {
        this.isStartBaiduStatistics = isStartBaiduStatistics;
    }

    public void finishAllActivity(List<Activity> activityList){
        for(Activity mActivity : activityList){

            if(mActivity instanceof MainActivity){
                ((MainActivity)mActivity).setKillAppType(false);
                ((MainActivity)mActivity).cleanOpenDialog();
            }

            mActivity.finish();
        }
    }

    public static boolean isUseSingleDialogMode(){
        if(Build.VERSION.SDK_INT < 21){
            return true;
        }
        return false;
    }
}

