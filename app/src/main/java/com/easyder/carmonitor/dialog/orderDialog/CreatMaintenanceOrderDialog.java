package com.easyder.carmonitor.dialog.orderDialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.bigkoo.pickerview.TimePickerView;
import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.dialog.MyTimePickerDialog;
import com.easyder.carmonitor.dialog.PopAmimationListener;
import com.easyder.carmonitor.dialog.SettingSelectDialog;
import com.easyder.carmonitor.interfaces.CreatMaintenanceOrderListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.widget.orderManager.CreatMaintenanceOrderWidget;
import com.shinetech.mvp.utils.TimeUtil;
import com.shinetech.mvp.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ljn on 2017-11-20.
 */

public class CreatMaintenanceOrderDialog extends BasePopupWindowDialog implements View.OnTouchListener, CreatMaintenanceOrderListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private boolean isTouch = false;

    public boolean needRecorve = false;

    private CreatMaintenanceOrderWidget mCreatMaintenanceOrder;

    private View bundView;

    private SettingSelectDialog plateColorDialog;

    private MyTimePickerDialog myTimePickerDialog;

    private OrderManagerDialog mOrderManagerDialog;

    public boolean isShowOrderManagerDialog = true;

    public CreatMaintenanceOrderDialog(Context context,OrderManagerDialog mOrderManagerDialog) {
        super(context, R.layout.creat_maintenance_order, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        this.mOrderManagerDialog = mOrderManagerDialog;

        setALLWindow();

        updateFocusable(true);

       /* this.presenter = (AllCarListPresenter) createPresenter();
        presenter.attachView((MvpView) this);*/
        initLayout();


        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));

    }

    private void initLayout(){

        mCreatMaintenanceOrder = new CreatMaintenanceOrderWidget(context, getLayout(), new LayoutBackListener() {
            @Override
            public void onBack() {

                dismiss();
            }
        });

        mCreatMaintenanceOrder.setOutmostTouchListener(this);

        mCreatMaintenanceOrder.setCreatOrderListener(this);

    }

    /**
     * 已创建维修单,当未做提交操作时,剋有进入修改信息或添加附件
     * @param orderNumber
     */
    public void updateOrderInfo(String orderNumber){
        mCreatMaintenanceOrder.initDate(orderNumber);
    }

    public void show(View v){
        bundView = v;
        isTouch = false;
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        getLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open));

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


    @Override
    public void showSelectPlateColorDialog() {

       /* if(plateColorDialog == null) {

            String[] stringArray = context.getResources().getStringArray(R.array.platenumber_color);

            plateColorDialog = new SettingSelectDialog(context, stringArray, "", 0);

            plateColorDialog.setTitle(context.getString(R.string.login_type_platenumber_color));

            plateColorDialog.setOnSelectListener(new SettingSelectListener() {
                @Override
                public void onSelect(String select) {

                    mCreatMaintenanceOrder. updatePlateColor(select);

                }
            });

        }

        String plateColor = mCreatMaintenanceOrder.getPlateColor();

        int currentIndex = 0;

        if(!TextUtils.isEmpty(plateColor)) {

            String[] stringArray = context.getResources().getStringArray(R.array.platenumber_color);

            for (int i = 0; i < stringArray.length; i++) {
                if (stringArray[i].startsWith(plateColor + "")) {
                    currentIndex = i;
                    break;
                }
            }

        }

        plateColorDialog.setindex(currentIndex);

        plateColorDialog.show(bundView);*/

    }

    @Override
    public void showSelectAppointmentTimeDialog() {

        if(myTimePickerDialog == null) {

            myTimePickerDialog = new MyTimePickerDialog(context, TimePickerView.Type.ALL);

            myTimePickerDialog.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date) {
                    String tiemString = TimeUtil.getTiemString(date);
                    mCreatMaintenanceOrder.updateAppointmentTime(tiemString);
                }
            });

            myTimePickerDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ConfigurationChangedManager.getInstance().unRegistConfig(myTimePickerDialog);
                }
            });

            myTimePickerDialog.setTitle(context.getString(R.string.maintenance_order_appointment_time));

            myTimePickerDialog.setIsAppointmentTime(true);

        }

        Date startTime = new Date(System.currentTimeMillis());

        myTimePickerDialog.setTime(startTime);
        myTimePickerDialog.show(bundView);
        ConfigurationChangedManager.getInstance().registConfig(myTimePickerDialog);

    }

    @Override
    public void onSuccessCommitData(String orderNumber) {

//        ToastUtil.showShort("onSuccessCommitData  : "+orderNumber);
        isShowOrderManagerDialog = false;
        dismiss();

        final CommitOrderDialog commitOrderDialog = new CommitOrderDialog(context, orderNumber, new LayoutBackListener() {
            @Override
            public void onBack() {
                if(CarMonitorApplication.isUseSingleDialogMode()){
                    mOrderManagerDialog.show(bundView);
                }else{
                    mOrderManagerDialog.update(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight()));
                }
            }
        });

        commitOrderDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ConfigurationChangedManager.getInstance().unRegistConfig(commitOrderDialog);
            }
        });


        commitOrderDialog.showOrderListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(CarMonitorApplication.isUseSingleDialogMode()){
                    mOrderManagerDialog.show(bundView);

                }else{
                    mOrderManagerDialog.update(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight()));
                }
                commitOrderDialog.dismiss();

            }
        });

        commitOrderDialog.show(bundView);
        ConfigurationChangedManager.getInstance().registConfig(commitOrderDialog);

        //show success layout

    }

    @Override
    public void updateDialogFocusable(boolean b) {
        //是否要取消弹框的焦点，使返回键在Activity中响应
        updateFocusable(b);
    }

    public boolean onKeyBack(){
        return mCreatMaintenanceOrder.onkeyBack();
    }


}
