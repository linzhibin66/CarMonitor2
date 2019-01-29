package com.easyder.carmonitor.presenter;

import android.support.v4.view.PagerAdapter;

import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.presenter.MaintenanceOrderPresenter;

/**
 * Created by ljn on 2017-11-10.
 */

public class InstallOrderActivityPresenter extends MaintenanceOrderPresenter {


    private PagerAdapter maintenanceOrderAdapter;

    @Override
    public void onSuccess(LoadResult successResult) {
        super.onSuccess(successResult);



    }

    private void creatPagerAdapter(){
//        maintenanceOrderAdapter


    }

}
