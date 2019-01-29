package com.easyder.carmonitor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.easyder.carmonitor.interfaces.CarInfoLayoutListener;
import com.easyder.carmonitor.widget.CarInfoWidget;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;


public class CarInfoActivity extends Activity {

    private CarInfoWidget carInfoWidget;

    public static final String CARINFO = "carinfo";

    public static final String ADRESS = "carinfo_adress";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        carInfoWidget = new CarInfoWidget(this, new CarInfoLayoutListener() {
            @Override
            public void OnClickMoreStatus(CarInfoBean carInfoBean) {

            }

            @Override
            public void OnClickAlarm(CarInfoBean carInfoBean) {

            }

            @Override
            public void OnClickCarcost(CarInfoBean carInfoBean) {

            }

            @Override
            public void onBack() {
                finish();
            }
        });

        setContentView(carInfoWidget.getView());

        Intent intent = getIntent();

        String plateNumber = intent.getStringExtra(CARINFO);
        String adrees = intent.getStringExtra(ADRESS);

        CarInfoBean newestCarInfo = UserInfo.getInstance().getNewestCarInfo(plateNumber);

        carInfoWidget.upData(newestCarInfo, adrees);

    }
}

