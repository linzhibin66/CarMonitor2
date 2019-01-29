package com.easyder.carmonitor.dialog.orderDialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.bigkoo.pickerview.TimePickerView;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.dialog.MyTimePickerDialog;
import com.easyder.carmonitor.dialog.PopAmimationListener;
import com.easyder.carmonitor.dialog.SettingSelectDialog;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.SettingSelectListener;
import com.easyder.carmonitor.interfaces.UpLoadMaintenanceListener;
import com.easyder.carmonitor.widget.orderManager.UpLoadMaintenanceResultWidget;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.shinetech.mvp.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ljn on 2017-11-28.
 */

public class UpLoadMaintenanceResultDialog extends BasePopupWindowDialog implements View.OnTouchListener, UpLoadMaintenanceListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private boolean isTouch = false;

    public boolean needRecorve = false;

    private UpLoadMaintenanceResultWidget mUpLoadMaintenanceResultWidget;

    private View bundView;

//    private MyTimePickerDialog myTimePickerDialog;

    private SettingSelectDialog mSettingSelectDialog;

//    public boolean isShowOrderManagerDialog = true;

    private BaseOrderInfoDB baseOrderInfoDB;

    private List<String> attachmentPathList;

    public UpLoadMaintenanceResultDialog(Context context , BaseOrderInfoDB baseOrderInfoDB, List<String> attachmentPathList /*, OrderManagerDialog mOrderManagerDialog*/) {
        super(context, R.layout.upload_maintenance_result, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));
        this.attachmentPathList = attachmentPathList;
        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        this.baseOrderInfoDB = baseOrderInfoDB;

//        this.mOrderManagerDialog = mOrderManagerDialog;

        setALLWindow();

        updateFocusable(true);

       /* this.presenter = (AllCarListPresenter) createPresenter();
        presenter.attachView((MvpView) this);*/
        initLayout();

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));

    }

    private void initLayout(){

        mUpLoadMaintenanceResultWidget = new UpLoadMaintenanceResultWidget(context, baseOrderInfoDB, attachmentPathList, getLayout(), new LayoutBackListener() {
            @Override
            public void onBack() {

                dismiss();
            }
        });

        mUpLoadMaintenanceResultWidget.setOutmostTouchListener(this);
        mUpLoadMaintenanceResultWidget.setUpLoadListener(this);

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
    public void showSelectTermainStatus(int index) {

        String[] stringArray = context.getResources().getStringArray(R.array.termain_status_list);

//        platenumber_color_tv.setText(stringArray[0]);

        if(mSettingSelectDialog == null) {

            mSettingSelectDialog = new SettingSelectDialog(context, stringArray, "", 0);

            mSettingSelectDialog.setTitle(context.getString(R.string.upload_maintenance_result_termain_status));

            mSettingSelectDialog.setOnSelectListener(new SettingSelectListener() {
                @Override
                public void onSelect(String select) {

                    mUpLoadMaintenanceResultWidget.UpDataTermainStatus(select);

                }
            });

            mSettingSelectDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ConfigurationChangedManager.getInstance().unRegistConfig(mSettingSelectDialog);
                }
            });
        }

        mSettingSelectDialog.setindex(index);
        mSettingSelectDialog.show(bundView);
        ConfigurationChangedManager.getInstance().registConfig(mSettingSelectDialog);

    }

    @Override
    public void updateDialogFocusable(boolean focusable) {
        updateFocusable(focusable);
    }

    public boolean onKeyBack(){
        return mUpLoadMaintenanceResultWidget.OnKeyBack();
    }


}
