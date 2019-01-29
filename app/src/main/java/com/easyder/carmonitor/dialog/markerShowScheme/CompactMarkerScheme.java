package com.easyder.carmonitor.dialog.markerShowScheme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.PopupWindow;

import com.baidu.mapapi.map.MapView;
import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.activity.TrackActivity;
import com.easyder.carmonitor.dialog.AlarmLogDialog;
import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.dialog.CarAlarmDialog;
import com.easyder.carmonitor.dialog.FeePayDialog;
import com.easyder.carmonitor.dialog.orderDialog.CarCostDialog;
import com.easyder.carmonitor.dialog.CarInfoDialog;
import com.easyder.carmonitor.dialog.CarStatusDialog;
import com.easyder.carmonitor.dialog.OperationPwdDialog;
import com.easyder.carmonitor.dialog.SendMessageDialog;
import com.easyder.carmonitor.interfaces.CarCostBackListener;
import com.easyder.carmonitor.interfaces.CarInfoLayoutListener;
import com.easyder.carmonitor.interfaces.CompactMarkerListener;
import com.easyder.carmonitor.interfaces.OperationListener;
import com.easyder.carmonitor.presenter.MainActivityPresenter;
import com.easyder.carmonitor.presenter.OperationActivityPresenter;
import com.easyder.carmonitor.wxapi.WXPayEntryActivity;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;

/**
 * Created by ljn on 2017-04-11.
 */
public class CompactMarkerScheme extends BaseMarkerScheme{

    private CompactMarkerDialog mCompactMarkerDialog;

    private CarInfoDialog mCarInfoDialog;

    private CarStatusDialog mCarStatusDialog;

    private CarAlarmDialog mCarAlarmDialog;

    private AlarmLogDialog mAlarmLogDialog;

    private OperationActivityPresenter mOperationPresenter;

    public static final String MARKER_PLATENUMBER = "marker_plateNumber";
    public static final String MARKER_START_TIME = "marker_start_time";
    public static final String MARKER_END_TIME = "marker_end_time";


    public CompactMarkerScheme(Context context, MainActivityPresenter presenter) {
        super(context, presenter);

    }

    @Override
    public void dismissMarkerDialog() {
        if(isShowMarkerDialog()){
            mCompactMarkerDialog.dismiss();
        }

    }

    @Override
    public String getPlateNumber(){

        if(mCompactMarkerDialog == null){
            return "";
        }

        return mCompactMarkerDialog.getPlateNumber();
    }

    @Override
    public boolean isShowMarkerDialog() {
        return (mCompactMarkerDialog != null && mCompactMarkerDialog.isShowing());
    }

    @Override
    public boolean updateMarkerDialogInfo(CarInfoBean mCarInfoBean) {

        if (mCompactMarkerDialog != null && mCompactMarkerDialog.isShowing() && mCompactMarkerDialog.isMarkerCarInfoChange(mCarInfoBean)) {

            mCompactMarkerDialog.update(mCarInfoBean);
            return true;
        }
        return false;
    }

    @Override
    public boolean isMarkerCarInfoChange(CarInfoBean mCarInfoBean) {
        if(mCompactMarkerDialog!= null){
            return mCompactMarkerDialog.isMarkerCarInfoChange(mCarInfoBean);
        }
        return false;
    }

    @Override
    public boolean showMarkerDialog(final MapView bmapView, CarInfoBean carInfoBean, final PopupWindow.OnDismissListener listener) {

        if(mCompactMarkerDialog == null){
            mCompactMarkerDialog = new CompactMarkerDialog(context,carInfoBean);
            mCompactMarkerDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if(listener!= null) {
                        listener.onDismiss();
                    }
                    removeInDialogManager(mCompactMarkerDialog);
                }
            });

            mCompactMarkerDialog.setListener(new CompactMarkerListener() {
                @Override
                public void onLayoutClick(CarInfoBean mCarInfoBean, String mAdress) {
                    showCarInfo(bmapView,mCarInfoBean,mAdress);
                }

                @Override
                public void onAlarmCommit(CarInfoBean mCarInfoBean, String startTime, String endTime, String type) {
//                    ToastUtil.showShort("plateNumber = "+mCarInfoBean.getPlateNumber()+"  start tiem = "+startTime + "  end time = "+endTime+ "  type = "+ type);
                    showAlarmLog(bmapView, mCarInfoBean.getPlateNumber(), startTime, endTime);
                }

                @Override
                public void onTrackCommit(CarInfoBean mCarInfoBean, String startTime, String endTime) {
//                    ToastUtil.showShort("plateNumber = " + mCarInfoBean.getPlateNumber() + "  start tiem = " + startTime + "  end time = " + endTime);
                    Intent intent = new Intent(context, TrackActivity.class);
                    intent.putExtra(MARKER_PLATENUMBER, mCarInfoBean.getPlateNumber());
                    intent.putExtra(MARKER_START_TIME, startTime);
                    intent.putExtra(MARKER_END_TIME, endTime);
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.pop_right2left_anim_open, R.anim.pop_right2left_anim_close);
                    presenter.markerDialogToBackground();
                }

                @Override
                public void onOperationClick(String message, String successHint, String errorHint, final String plateNumber, final byte instruct) {

                    showOperatuibPwdDialog(bmapView, message, successHint, errorHint, plateNumber, instruct);
                }
            });

        }else{
            mCompactMarkerDialog.update(carInfoBean);
        }

        if(!mCompactMarkerDialog.isShowing()){

            mCompactMarkerDialog.resetCheck();
            mCompactMarkerDialog.show(bmapView);
            addToDialogManager(mCompactMarkerDialog);
        }

        return true;
    }

    @Override
    public void onPause(){
        if(mCompactMarkerDialog!= null && mCompactMarkerDialog.isShowing()){
            mCompactMarkerDialog.setAnimationStyle(0);
            mCompactMarkerDialog.uodatePopupWindow();
        }
    }

    @Override
    public void revoverShow() {
        if(mCompactMarkerDialog!= null){
            mCompactMarkerDialog.revoverShow();
            addToDialogManager(mCompactMarkerDialog);
        }
    }

    @Override
    public void onResume(){
        if(mCompactMarkerDialog!= null){
            mCompactMarkerDialog.setAnimationStyle(R.style.popwindow_anim_upanddown);
            mCompactMarkerDialog.uodatePopupWindow();
        }

    }

    private void removeInDialogManager(BasePopupWindowDialog dialog){
        presenter.removeDailogOfManager(dialog);
    }

    public void addToDialogManager(BasePopupWindowDialog dialog){
        presenter.addDialogOfManager(dialog);
    }

    private void showAlarmLog(final MapView bmapView, String plateNumber, String startTime, String endTime){

//        if(mAlarmLogDialog == null){
            mAlarmLogDialog = new AlarmLogDialog(plateNumber, startTime, endTime, context);
//        }else{
//            mAlarmLogDialog.u
//        }
        mAlarmLogDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ConfigurationChangedManager.getInstance().unRegistConfig(mAlarmLogDialog);
                removeInDialogManager(mAlarmLogDialog);
                presenter.recoverMarkerDialog();
            }
        });
        ConfigurationChangedManager.getInstance().registConfig(mAlarmLogDialog);
        mAlarmLogDialog.show(bmapView);
        addToDialogManager(mAlarmLogDialog);
        presenter.markerDialogToBackground();
    }

    /**
     * 显示详细的车辆信息
     * @param bmapView
     * @param mCarInfoBean
     * @param mAdress
     */
    private void showCarInfo(final MapView bmapView, final CarInfoBean mCarInfoBean, String mAdress){

       /* Intent intent = new Intent(context, CarInfoActivity.class);
        CarInfoBean carInfo = mMarkerDilaog.getCarInfoBean();
        intent.putExtra(CarInfoActivity.CARINFO, carInfo.getPlateNumber());
        mMarkerDilaog.getAdress();
        intent.putExtra(CarInfoActivity.ADRESS, mMarkerDilaog.getAdress());
        context.startActivity(intent);
        mMarkerDilaog.dismiss();*/

        if(mCarInfoDialog == null) {
            mCarInfoDialog = new CarInfoDialog(context, mCarInfoBean, mAdress, new CarInfoLayoutListener() {
                @Override
                public void OnClickMoreStatus(CarInfoBean carInfoBean) {
                    mCarInfoDialog.needRecover = false;
                    showCarStatusInfo(bmapView, carInfoBean);
                    if(CarMonitorApplication.isUseSingleDialogMode()){
                        mCarInfoDialog.dismiss();
                    }
                }

                @Override
                public void OnClickAlarm(CarInfoBean carInfoBean) {
                    mCarInfoDialog.needRecover = false;
                    showCarAlarmInfo(bmapView, carInfoBean);
                    if(CarMonitorApplication.isUseSingleDialogMode()){
                        mCarInfoDialog.dismiss();
                    }
                }

                @Override
                public void OnClickCarcost(CarInfoBean carInfoBean) {
                    mCarInfoDialog.needRecover = false;
                    showCarcost(bmapView, carInfoBean);
                    if(CarMonitorApplication.isUseSingleDialogMode()){
                        mCarInfoDialog.dismiss();
                    }
                }

                @Override
                public void onBack() {
                    if(mCarInfoDialog.isShowing()) {
                        mCarInfoDialog.needRecover = true;
                        mCarInfoDialog.dismiss();
                    }
                }
            });

            mCarInfoDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ConfigurationChangedManager.getInstance().unRegistConfig(mCarInfoDialog);
                    if(mCarInfoDialog.needRecover) {
                        revoverShow();
                    }
                    removeInDialogManager(mCarInfoDialog);
                }
            });

        }else{
            mCarInfoDialog.updataUI(mCarInfoBean, mAdress);
        }
        if(!mCarInfoDialog.isShowing()) {
            ConfigurationChangedManager.getInstance().registConfig(mCarInfoDialog);
            mCarInfoDialog.show(bmapView);
            addToDialogManager(mCarInfoDialog);
            presenter.markerDialogToBackground();
        }
    }

    private void showOperatuibPwdDialog(final View bmapView, String message, String successHint, String errorHint, final String plateNumber, final byte instruct){

        if(instruct == OperationActivityPresenter.INSTRUCT_SEND_MESSAGE){
             /*   Intent intent = new Intent(context, SendMessageActivity.class);
                intent.putExtra(SendMessageActivity.MESSAGE_PLATENUMBER, plateNumber);
                context.startActivity(intent);*/

            final SendMessageDialog mSendMessageDialog = new SendMessageDialog(context,plateNumber);

            mSendMessageDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ConfigurationChangedManager.getInstance().unRegistConfig(mSendMessageDialog);
                    if(mSendMessageDialog.needRecover) {
                        revoverShow();
                    }
                    removeInDialogManager(mSendMessageDialog);
                }
            });

            if(!mSendMessageDialog.isShowing()) {
                ConfigurationChangedManager.getInstance().registConfig(mSendMessageDialog);
                mSendMessageDialog.show(bmapView);
                addToDialogManager(mSendMessageDialog);
                presenter.markerDialogToBackground();
            }

            return;
        }

        final OperationPwdDialog mHornOffOperationPwdDialog = new OperationPwdDialog(context, instruct, message, successHint, errorHint,
                new OperationListener() {
                    @Override
                    public void toDoOperation(OperationPwdDialog mOperationDialog, ResponseListener mResponseListener) {

                            mOperationDialog.needRecover = true;
                            if(mOperationPresenter == null){
                                mOperationPresenter = new OperationActivityPresenter();
                            }
                            mOperationPresenter.sendInstructResponse(mOperationDialog.getInstruct(), plateNumber, mResponseListener);
                        }
                });

        mHornOffOperationPwdDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(mHornOffOperationPwdDialog.needRecover) {
                    revoverShow();
                }
            }
        });
        mHornOffOperationPwdDialog.showOperationPwd(bmapView);
        mHornOffOperationPwdDialog.needRecover = true;
        presenter.markerDialogToBackground();
    }

    /**
     * 显示详细的车辆状态信息
     * @param bmapView
     * @param carInfoBean
     */
    private void showCarStatusInfo(final MapView bmapView, CarInfoBean carInfoBean){
        if(mCarStatusDialog == null) {
            mCarStatusDialog = new CarStatusDialog(context, carInfoBean);
            mCarStatusDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ConfigurationChangedManager.getInstance().unRegistConfig(mCarStatusDialog);
                    if(CarMonitorApplication.isUseSingleDialogMode()){
                        mCarInfoDialog.noAnimationShow(bmapView);
                    }

                    removeInDialogManager(mCarStatusDialog);
                }
            });
        }else{
            mCarStatusDialog.updataUI(carInfoBean);
        }
        if(!mCarStatusDialog.isShowing()) {
            ConfigurationChangedManager.getInstance().registConfig(mCarStatusDialog);
            mCarStatusDialog.show(bmapView);
            addToDialogManager(mCarStatusDialog);
        }
    }

    /**
     * 显示详细的车辆报警信息
     * @param bmapView
     * @param carInfoBean
     */
    private void showCarAlarmInfo(final MapView bmapView, CarInfoBean carInfoBean){
        if(mCarAlarmDialog == null) {
            mCarAlarmDialog = new CarAlarmDialog(context, carInfoBean);
            mCarAlarmDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ConfigurationChangedManager.getInstance().unRegistConfig(mCarAlarmDialog);
                    if(CarMonitorApplication.isUseSingleDialogMode()){
                        mCarInfoDialog.noAnimationShow(bmapView);
                    }
                    removeInDialogManager(mCarAlarmDialog);
                }
            });
        }else{
            mCarAlarmDialog.updataUI(carInfoBean);
        }

        if(!mCarAlarmDialog.isShowing()) {
            ConfigurationChangedManager.getInstance().registConfig(mCarAlarmDialog);
            mCarAlarmDialog.show(bmapView);
            addToDialogManager(mCarAlarmDialog);
        }
    }


    private void showCarcost(final MapView bmapView, CarInfoBean carInfoBean){
            final CarCostDialog mCarCostDialog = new CarCostDialog(context, carInfoBean.getPlateNumber(), new CarCostBackListener() {


                @Override
                public void onBack(BasePopupWindowDialog dialog) {
                    dialog.dismiss();
                }

                @Override
                public void onShowFeePay(String plateNumber) {

                    boolean showActivity = true;
                    if(showActivity) {
                        Intent intent = new Intent(context, WXPayEntryActivity.class);
                        intent.putExtra("plateNumber", plateNumber);
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.pop_right2left_anim_open, R.anim.pop_right2left_anim_close);
                    }else{

                        final FeePayDialog feePayDialog = new FeePayDialog(context, plateNumber);
                        feePayDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                ConfigurationChangedManager.getInstance().unRegistConfig(feePayDialog);
                                if (CarMonitorApplication.isUseSingleDialogMode()) {
                                    feePayDialog.noAnimationShow(bmapView);
                                }
                                removeInDialogManager(feePayDialog);
                            }
                        });


                        if (!feePayDialog.isShowing()) {
                            ConfigurationChangedManager.getInstance().registConfig(feePayDialog);
                            feePayDialog.show(bmapView);
                            addToDialogManager(feePayDialog);
                        }
                    }


                }
            });


            mCarCostDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ConfigurationChangedManager.getInstance().unRegistConfig(mCarCostDialog);
                    if(CarMonitorApplication.isUseSingleDialogMode()){
                        mCarInfoDialog.noAnimationShow(bmapView);
                    }
                    removeInDialogManager(mCarCostDialog);
                }
            });

        if(!mCarCostDialog.isShowing()) {
            ConfigurationChangedManager.getInstance().registConfig(mCarCostDialog);
            mCarCostDialog.show(bmapView);
            addToDialogManager(mCarCostDialog);
        }
    }
}
