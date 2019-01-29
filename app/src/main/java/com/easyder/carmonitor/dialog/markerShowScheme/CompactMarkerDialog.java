package com.easyder.carmonitor.dialog.markerShowScheme;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.mapapi.map.MapView;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.interfaces.CompactMarkerListener;
import com.easyder.carmonitor.widget.marker.MarkerAlarmWidget;
import com.easyder.carmonitor.widget.marker.MarkerCarInfoWidget;
import com.easyder.carmonitor.widget.marker.MarkerOperationWidget;
import com.easyder.carmonitor.widget.marker.MarkerTrackWidget;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

import java.util.List;

/**
 * Created by ljn on 2017-04-12.
 */
public class CompactMarkerDialog extends BasePopupWindowDialog implements RadioGroup.OnCheckedChangeListener {
    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    RadioGroup marker_ctrl;

    private View currentView;

    private CarInfoBean mCarInfoBean;

    private MarkerCarInfoWidget markerCarInfoWidget;

    private MarkerAlarmWidget markerAlarmWidget;

    private MarkerTrackWidget markertrackWidget;

    private MarkerOperationWidget markerOperationWidget;

    private LinearLayout rootView;

    private MapView bundView;

    private RadioButton marker_ctrl_carinfo;

    private RadioButton marker_ctrl_warning;

    private RadioButton marker_ctrl_track;

    private RadioButton marker_ctrl_operation;

    private RadioButton oldChecked;

    private CompactMarkerListener mCompactMarkerListener;

    private LinearLayout.LayoutParams layoutParams;


    public CompactMarkerDialog(Context context, CarInfoBean mCarInfoBean) {
        super(context, R.layout.compact_marker_dialog, new ViewGroup.LayoutParams(getDisplayWidth(context), ViewGroup.LayoutParams.WRAP_CONTENT));

        this.mCarInfoBean = mCarInfoBean;

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }
        
        initView();

        setAnimationStyle(R.style.popwindow_anim_upanddown);

    }

    public void show(View v) {
//        setFocusable(true);
        bundView = (MapView) v;
        super.show(v, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0/*UIUtils.dip2px(7)*/);
    }
    
    private void initView(){
        rootView = (LinearLayout) getLayout();

        marker_ctrl_carinfo = (RadioButton) rootView.findViewById(R.id.marker_ctrl_carinfo);
        marker_ctrl_warning = (RadioButton) rootView.findViewById(R.id.marker_ctrl_warning);
        marker_ctrl_track = (RadioButton) rootView.findViewById(R.id.marker_ctrl_track);
        marker_ctrl_operation = (RadioButton) rootView.findViewById(R.id.marker_ctrl_operation);

        marker_ctrl = (RadioGroup) rootView.findViewById(R.id.marker_ctrl);
        resetCheck();

        currentView = getCarinfoView();
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(UIUtils.dip2px(5), 0, UIUtils.dip2px(5), UIUtils.dip2px(3));
        rootView.addView(currentView, layoutParams);

    }

    public void resetCheck(){
        marker_ctrl.check(R.id.marker_ctrl_carinfo);
        resetRadioButton(marker_ctrl_carinfo);
        marker_ctrl.setOnCheckedChangeListener(this);
    }

    public String getPlateNumber(){
        if(mCarInfoBean != null) {
            return mCarInfoBean.getPlateNumber();
        }
        return null;
    }

    public void revoverShow(){
        try {
            if (bundView != null) {
                show(bundView);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置点击事件监听
     * @param mCompactMarkerListener
     */
    public void setListener(CompactMarkerListener mCompactMarkerListener){
        this.mCompactMarkerListener = mCompactMarkerListener;

        if(markerCarInfoWidget != null){
            markerCarInfoWidget.setClickListener(mCompactMarkerListener);
        }

        if(markerAlarmWidget != null){
            markerAlarmWidget.setCommitListener(mCompactMarkerListener);
        }

        if(markertrackWidget != null){
            markertrackWidget.setCommitListener(mCompactMarkerListener);
        }

        if(markerOperationWidget != null){
            markerOperationWidget.setOperationClick(mCompactMarkerListener);
        }
    }

    /**
     * CarinfoView
     * @return
     */
    private View getCarinfoView(){

        updateCarinfoView();

        return markerCarInfoWidget.getLayout();
    }

    /**
     * init CarinfoView
     */
    private void updateCarinfoView(){

        if(markerCarInfoWidget == null) {
            markerCarInfoWidget = new MarkerCarInfoWidget(context, mCarInfoBean);
            markerCarInfoWidget.setClickListener(mCompactMarkerListener);
        } else {
            markerCarInfoWidget.updateViewInfo(mCarInfoBean);
        }

    }

    /**
     * 警报 界面  AlarmView
     * @return
     */
    private View getAlarmView(){

        updateAlarmView();

        return markerAlarmWidget.getView();

    }

    /**
     * init AlarmView
     */
    private void updateAlarmView(){

        if(markerAlarmWidget == null){
            markerAlarmWidget = new MarkerAlarmWidget(context,mCarInfoBean,bundView);
            markerAlarmWidget.setCommitListener(mCompactMarkerListener);
        }else{
            markerAlarmWidget.updateInfo(mCarInfoBean);
        }
    }

    /**
     * 轨迹 界面  TrackView
     * @return
     */
    private View getTrackView(){

        updateTrackView();

        return markertrackWidget.getView();

    }

    /**
     * init TrackView
     */
    private void updateTrackView(){

        if(markertrackWidget == null){
            markertrackWidget = new MarkerTrackWidget(context,mCarInfoBean,bundView);
            markertrackWidget.setCommitListener(mCompactMarkerListener);
        }else{
            markertrackWidget.updateInfo(mCarInfoBean);
        }
    }

    /**
     * 操作 界面  TrackView
     * @return
     */
    private View getOperationView(){

        updateOperationView();

        return markerOperationWidget.getView();

    }

    /**
     * init TrackView
     */
    private void updateOperationView(){

        if(markerOperationWidget == null){
            markerOperationWidget = new MarkerOperationWidget(context,mCarInfoBean,bundView);
            markerOperationWidget.setOperationClick(mCompactMarkerListener);
        }else{
            markerOperationWidget.updateInfo(mCarInfoBean);
        }
    }



    public void update(CarInfoBean mCarInfoBean){
        this.mCarInfoBean = mCarInfoBean;
        switch (marker_ctrl.getCheckedRadioButtonId()){
            case R.id.marker_ctrl_carinfo :
                updateCarinfoView();
                break;
            case R.id.marker_ctrl_warning:
                updateAlarmView();
                break;
            case R.id.marker_ctrl_track:
                updateTrackView();
                break;
            case R.id.marker_ctrl_operation:
                updateOperationView();
                break;

        }

    }

    public boolean isMarkerCarInfoChange(CarInfoBean carInfoBean){

        String plateNumber = markerCarInfoWidget.getplateNumberOfView();
        String locationTimeOfView = markerCarInfoWidget.getLocationTimeOfView();
        if((!TextUtils.isEmpty(plateNumber)) && plateNumber.equals(carInfoBean.getPlateNumber()) && (!locationTimeOfView.equals(carInfoBean.getLocationTime()))) {
            return true;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.marker_ctrl_carinfo:
//                详情
                rootView.removeView(currentView);

                currentView = getCarinfoView();
                rootView.addView(currentView,layoutParams);

                resetRadioButton(marker_ctrl_carinfo);

                break;
            case R.id.marker_ctrl_warning:
//              警报
                rootView.removeView(currentView);
                currentView = getAlarmView();
                rootView.addView(currentView,layoutParams);

                resetRadioButton(marker_ctrl_warning);
//                ToastUtil.showShort("warning");
                break;
            case R.id.marker_ctrl_track:
                rootView.removeView(currentView);
                currentView = getTrackView();
                rootView.addView(currentView,layoutParams);

                resetRadioButton(marker_ctrl_track);
//                ToastUtil.showShort("track");
//              轨迹
                break;
            case R.id.marker_ctrl_operation:
                rootView.removeView(currentView);
                currentView = getOperationView();
                rootView.addView(currentView,layoutParams);

                resetRadioButton(marker_ctrl_operation);
//                ToastUtil.showShort("operation");
//              操作
                break;
        }

    }

    private void resetRadioButton(RadioButton mRadioButton){
        if(oldChecked != null) {
            oldChecked.setTypeface(Typeface.DEFAULT);
            oldChecked.setTextColor(Color.parseColor("#FF919191"));
        }
//        mRadioButton.setTypeface(Typeface.DEFAULT_BOLD);
        mRadioButton.setTextColor(Color.parseColor("#FF333333"));
        oldChecked = mRadioButton;
    }

}
