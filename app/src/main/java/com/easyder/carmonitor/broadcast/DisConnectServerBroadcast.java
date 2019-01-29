package com.easyder.carmonitor.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.activity.LoginActivity;
import com.easyder.carmonitor.activity.MainActivity;
import com.shinetech.mvp.MainApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-05-05.
 */
public class DisConnectServerBroadcast extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(MainApplication.DISCONNECT_SERVICE)){

            List<Activity> list = new ArrayList<>();
            List<Activity> activityList = CarMonitorApplication.getInstance().getActivityList();
            list.addAll(activityList);

            Intent mIntent = new Intent(context, LoginActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);

            ((CarMonitorApplication)CarMonitorApplication.getInstance()).finishAllActivity(list);

        }

    }
}
