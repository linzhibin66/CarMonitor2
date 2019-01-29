package com.easyder.carmonitor.dialog.markerShowScheme;

import android.content.Context;
import android.widget.PopupWindow;

import com.baidu.mapapi.map.MapView;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.dialog.CarAlarmDialog;
import com.easyder.carmonitor.dialog.orderDialog.CarCostDialog;
import com.easyder.carmonitor.dialog.CarInfoDialog;
import com.easyder.carmonitor.dialog.CarStatusDialog;
import com.easyder.carmonitor.dialog.TimePickerDialog;
import com.easyder.carmonitor.interfaces.CarCostBackListener;
import com.easyder.carmonitor.interfaces.CarInfoLayoutListener;
import com.easyder.carmonitor.interfaces.MarkerClickListener;
import com.easyder.carmonitor.interfaces.TimePickerEnterListener;
import com.easyder.carmonitor.presenter.MainActivityPresenter;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.utils.ToastUtil;

/**
 * Created by ljn on 2017-04-11.
 */
public class OriginalMarkerScheme extends BaseMarkerScheme{

    //底部车辆信息弹框
    private MarkerDilaog mMarkerDilaog;

    private CarInfoDialog mCarInfoDialog;

    private  CarStatusDialog mCarStatusDialog;

    private CarAlarmDialog mCarAlarmDialog;

    public OriginalMarkerScheme(Context context, MainActivityPresenter presenter) {
        super(context, presenter);
    }

    /**
     * 显示Marker的详细信息
     *
     * @param bmapView
     * @param carInfoBean 车辆信息
     * @return
     */
    @Override
    public boolean showMarkerDialog(final MapView bmapView, CarInfoBean carInfoBean, final PopupWindow.OnDismissListener listener) {

        if (mMarkerDilaog == null) {

            mMarkerDilaog = new MarkerDilaog(context, carInfoBean);
            mMarkerDilaog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if(listener!=null) {
                        listener.onDismiss();
//                        resetCurrentClickMarker();
                    }
                    removeInDialogManager(mMarkerDilaog);
                }
            });
            mMarkerDilaog.setClickListener(new MarkerClickListener() {
                @Override
                public void clickWarning(CarInfoBean mCarInfoBean) {
                    ToastUtil.showShort("clickWarning");
                    showTrackDialog(bmapView, false, context.getString(R.string.alarm_title), new TimePickerEnterListener() {
                        @Override
                        public void onEnter(String startTime, String endTime, int alarmType) {
                            ToastUtil.showShort("startTime = " + startTime + "    endTime = " + endTime + "    alarmType = " + alarmType);
                        }
                    });

                }

                @Override
                public void clickTrack(CarInfoBean mCarInfoBean) {
                    ToastUtil.showShort("clickTrack");
                    showTrackDialog(bmapView, true, context.getString(R.string.track_title), new TimePickerEnterListener() {
                        @Override
                        public void onEnter(String startTime, String endTime, int alarmType) {
                            ToastUtil.showShort("startTime = " + startTime + "    endTime = " + endTime + "    alarmType = " + alarmType);
                        }
                    });
                }

                @Override
                public void clickOperation(CarInfoBean mCarInfoBean) {
                    ToastUtil.showShort("clickOperation");
                }

                @Override
                public void onLayoutClick(CarInfoBean mCarInfoBean) {
                    ToastUtil.showShort("onLayoutClick");
                   /* Intent intent = new Intent(context, CarInfoActivity.class);
                    CarInfoBean carInfo = mMarkerDilaog.getCarInfoBean();
                    intent.putExtra(CarInfoActivity.CARINFO, carInfo.getPlateNumber());
                    mMarkerDilaog.getAdress();
                    intent.putExtra(CarInfoActivity.ADRESS, mMarkerDilaog.getAdress());
                    context.startActivity(intent);
                    mMarkerDilaog.dismiss();*/

                    if(mCarInfoDialog == null) {
                        mCarInfoDialog = new CarInfoDialog(context, mCarInfoBean, mMarkerDilaog.getAdress(), new CarInfoLayoutListener() {

                            @Override
                            public void OnClickMoreStatus(CarInfoBean carInfoBean) {
                                if(mCarStatusDialog == null) {
                                    mCarStatusDialog = new CarStatusDialog(context, carInfoBean);
                                    mCarStatusDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                        @Override
                                        public void onDismiss() {
                                            removeInDialogManager(mCarStatusDialog);
                                        }
                                    });
                                }else{
                                    mCarStatusDialog.updataUI(carInfoBean);
                                }
                                if(!mCarStatusDialog.isShowing()) {
                                    mCarStatusDialog.show(bmapView);
                                    addToDialogManager(mCarStatusDialog);
                                }
                            }

                            @Override
                            public void OnClickAlarm(CarInfoBean carInfoBean) {

                                if(mCarAlarmDialog == null) {
                                    mCarAlarmDialog = new CarAlarmDialog(context, carInfoBean);
                                    mCarAlarmDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                        @Override
                                        public void onDismiss() {
                                            removeInDialogManager(mCarAlarmDialog);
                                        }
                                    });
                                }else{
                                    mCarAlarmDialog.updataUI(carInfoBean);
                                }

                                if(!mCarAlarmDialog.isShowing()) {
                                    mCarAlarmDialog.show(bmapView);
                                    addToDialogManager(mCarAlarmDialog);
                                }

                            }

                            @Override
                            public void OnClickCarcost(CarInfoBean carInfoBean) {

                                final CarCostDialog mCarCostDialog = new CarCostDialog(context, carInfoBean.getPlateNumber(), new CarCostBackListener() {
                                    @Override
                                    public void onBack(BasePopupWindowDialog dialog) {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onShowFeePay(String plateNumber) {

                                    }
                                });
                                mCarCostDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                        @Override
                                        public void onDismiss() {
                                            removeInDialogManager(mCarCostDialog);
                                        }
                                    });


                                if(!mCarCostDialog.isShowing()) {
                                    mCarCostDialog.show(bmapView);
                                    addToDialogManager(mCarCostDialog);
                                }

                            }

                            @Override
                            public void onBack() {
                                if(mCarInfoDialog.isShowing()) {
                                    mCarInfoDialog.dismiss();
                                }
                            }
                        });
                        mCarInfoDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                removeInDialogManager(mCarInfoDialog);
                            }
                        });
                    }else{
                        mCarInfoDialog.updataUI(mCarInfoBean, mMarkerDilaog.getAdress());
                    }
                    if(!mCarInfoDialog.isShowing()) {
                        mCarInfoDialog.show(bmapView);
                        addToDialogManager(mCarInfoDialog);
                    }
                }
            });

        } else {
            mMarkerDilaog.updateViewInfo(carInfoBean);
        }

        if(!mMarkerDilaog.isShowing()) {
            mMarkerDilaog.show(bmapView);
            addToDialogManager(mMarkerDilaog);
        }
        return true;

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void revoverShow() {
        if(mMarkerDilaog != null) {
            mMarkerDilaog.revoverShow();
        }
    }

    @Override
    public String getPlateNumber() {
        return mMarkerDilaog.getPlateNumber();
    }

    private void removeInDialogManager(BasePopupWindowDialog dialog){
        presenter.removeDailogOfManager(dialog);
    }

    public void addToDialogManager(BasePopupWindowDialog dialog){
        presenter.addDialogOfManager(dialog);
    }

    @Override
    public boolean updateMarkerDialogInfo(CarInfoBean mCarInfoBean) {
        if (mMarkerDilaog != null && mMarkerDilaog.isShowing() && mMarkerDilaog.isMarkerCarInfoChange(mCarInfoBean)) {

            mMarkerDilaog.updateViewInfo(mCarInfoBean);
            return true;
        }
        return false;
    }

    @Override
    public boolean isMarkerCarInfoChange(CarInfoBean mCarInfoBean) {
        if (mMarkerDilaog != null){
            return mMarkerDilaog.isMarkerCarInfoChange(mCarInfoBean);
        }
        return false;
    }

    /**
     * 退出Marker弹框
     */
    @Override
    public void dismissMarkerDialog() {
        if (isShowMarkerDialog()) {
            mMarkerDilaog.dismiss();
        }
    }

    /**
     * Marker弹框是否显示
     *
     * @return
     */
    @Override
    public boolean isShowMarkerDialog() {
        return (mMarkerDilaog != null && mMarkerDilaog.isShowing());
    }

    /**
     * 显示时间选择框
     *
     * @param bmapView
     * @param isTrack
     * @param title
     * @param listener
     */
    private void showTrackDialog(final MapView bmapView, boolean isTrack, String title, TimePickerEnterListener listener) {

        TimePickerDialog mTimePicker = new TimePickerDialog(context, isTrack);

        mTimePicker.setOnEnterListeren(listener);

        mTimePicker.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                if (bmapView != null) {
                    bmapView.onResume();
                }
            }
        });
        if (bmapView != null) {
            bmapView.onPause();
        }
        mTimePicker.setTitle(title);
        mTimePicker.show(bmapView);
    }

}
