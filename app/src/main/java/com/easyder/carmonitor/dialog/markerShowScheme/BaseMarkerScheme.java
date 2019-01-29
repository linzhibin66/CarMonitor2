package com.easyder.carmonitor.dialog.markerShowScheme;

import android.content.Context;
import android.widget.PopupWindow;

import com.baidu.mapapi.map.MapView;
import com.easyder.carmonitor.presenter.MainActivityPresenter;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

/**
 * Created by ljn on 2017-04-11.
 */
public abstract class BaseMarkerScheme {


    protected Context context;

    protected MainActivityPresenter presenter;

    public BaseMarkerScheme(Context context, MainActivityPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    public abstract void dismissMarkerDialog();

    public abstract boolean isShowMarkerDialog();

    public abstract boolean updateMarkerDialogInfo(CarInfoBean mCarInfoBean);

    public abstract boolean showMarkerDialog(MapView bmapView, CarInfoBean carInfoBean, PopupWindow.OnDismissListener listener);

    public abstract void onResume();

    public abstract void onPause();

    public abstract  void revoverShow();

    public abstract String getPlateNumber();

    public abstract boolean isMarkerCarInfoChange(CarInfoBean mCarInfoBean);

}
