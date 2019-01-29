package com.easyder.carmonitor.dialog.orderDialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.dialog.PopAmimationListener;
import com.easyder.carmonitor.interfaces.AcceptOrderDialogListener;
import com.easyder.carmonitor.interfaces.MaintenanceChangeStatusListener;
import com.easyder.carmonitor.presenter.MainActivityPresenter;
import com.easyder.carmonitor.widget.orderManager.AcceptOrderDialogWidget;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;
import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.widget.orderManager.MaintenanceOrderInfoWidget;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.AcceptOrderVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.AttachmentItemVo;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

import java.util.List;

/**
 * Created by Lenovo on 2017-11-10.
 */

public class MaintenanceOrderInfoDialog extends BasePopupWindowDialog implements View.OnTouchListener, MaintenanceChangeStatusListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private boolean isTouch = false;

    public boolean needRecorve = false;

    private boolean isDebug = false && LogUtils.isDebug;

    private MaintenanceOrderInfoWidget orderInfoWidget;

    private View bundView;

    private BaseOrderInfoDB baseOrderInfoDB;

    private int maintenanceType;

    public MaintenanceOrderInfoDialog(Context context, int maintenanceType, BaseOrderInfoDB baseOrderInfoDB) {
        super(context, R.layout.maintenance_order_info, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        this.maintenanceType = maintenanceType;
        this.baseOrderInfoDB = baseOrderInfoDB;

        setALLWindow();

        setFocusable(true);

       /* this.presenter = (AllCarListPresenter) createPresenter();
        presenter.attachView((MvpView) this);*/
        initLayout();

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));

    }

    private void initLayout(){

        orderInfoWidget = new MaintenanceOrderInfoWidget(context, maintenanceType, baseOrderInfoDB, getLayout(), new LayoutBackListener() {
            @Override
            public void onBack() {
                dismiss();
            }
        });

        orderInfoWidget.setOutmostTouchListener(this);
        orderInfoWidget.setMaintenanceChangeStatusListener(this);

    }

    public void show(View v){
        bundView = v;
        isTouch = false;
        needRecorve = true;
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
    public void acceptOrder(final BaseOrderInfoDB baseOrderInfoDB) {

        AcceptOrderDialogWidget<BaseVo> mAcceptOrderDialogWidget = new AcceptOrderDialogWidget<>(context, new AcceptOrderDialogListener() {
            @Override
            public void OnCancel() {
                updateFocusable(true);
                orderInfoWidget.dismissHintLayout();
            }

            @Override
            public BaseVo OnLoad() {
                AcceptOrderVo acceptOrderVo = new AcceptOrderVo(baseOrderInfoDB.getOrderName(), baseOrderInfoDB.getOrderNumber(), (byte) 2, "");
                return acceptOrderVo;
            }

            @Override
            public void OnSuccess() {
                /*
                updateFocusable(true);
                maintenanceOrderItem.setStatus(MaintenanceOrderInfoBean.PROCESSED_STATUS);
                DBManager.insertMaintenanceOrderInfoBean(maintenanceOrderItem);
                orderInfoWidget.dismissHintLayout();
                */
                orderInfoWidget.updata();

            }
        });

        mAcceptOrderDialogWidget.setTitle(context.getString(R.string.enter_to_acceptorder));
        mAcceptOrderDialogWidget.setHint(context.getString(R.string.enter_to_acceptorder_hint));
        orderInfoWidget.addHint(mAcceptOrderDialogWidget.getView());

        updateFocusable(false);

    }

    @Override
    public void upLoadMaintenanceResult(final BaseOrderInfoDB baseOrderInfoDB, List<String> attachmentPathList) {

        final UpLoadMaintenanceResultDialog mUpLoadMaintenanceResultDialog = new UpLoadMaintenanceResultDialog(context, baseOrderInfoDB, attachmentPathList);
        mUpLoadMaintenanceResultDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ConfigurationChangedManager.getInstance().unRegistConfig(mUpLoadMaintenanceResultDialog);
                MainActivityPresenter.removeDailogOfManager(mUpLoadMaintenanceResultDialog);
                updateFocusable(true);
                orderInfoWidget.updata();
                //isShowOrderManagerDialog 标志防止进入成功页面是跳到列表界面
                if(CarMonitorApplication.isUseSingleDialogMode()) {
                    noAnimationShow(bundView);
                }
            }
        });
        MainActivityPresenter.addDialogOfManager(mUpLoadMaintenanceResultDialog);
        ConfigurationChangedManager.getInstance().registConfig(mUpLoadMaintenanceResultDialog);
        mUpLoadMaintenanceResultDialog.show(bundView);
        updateFocusable(false);
        if(CarMonitorApplication.isUseSingleDialogMode()) {
            needRecorve = false;
            dismiss();
        }
    }

    /**
     * 评价界面
     * @param baseOrderInfoDB
     * @param plateNumber
     */
    @Override
    public void showMaintenanceOrderValuation(BaseOrderInfoDB baseOrderInfoDB, String plateNumber) {
//        ToastUtil.showShort("showMaintenanceOrderValuation level = "+maintenanceOrderItem.getLevel());

        final MaintenanceOrderValuationDialog maintenanceOrderValuationDialog = new MaintenanceOrderValuationDialog(context, baseOrderInfoDB, plateNumber);
        maintenanceOrderValuationDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ConfigurationChangedManager.getInstance().unRegistConfig(maintenanceOrderValuationDialog);
                MainActivityPresenter.removeDailogOfManager(maintenanceOrderValuationDialog);
                orderInfoWidget.updata();
                //isShowOrderManagerDialog 标志防止进入成功页面是跳到列表界面
                if(CarMonitorApplication.isUseSingleDialogMode()) {
                    noAnimationShow(bundView);
                }
            }
        });
        MainActivityPresenter.addDialogOfManager(maintenanceOrderValuationDialog);
        ConfigurationChangedManager.getInstance().registConfig(maintenanceOrderValuationDialog);
        maintenanceOrderValuationDialog.show(bundView);

        if(CarMonitorApplication.isUseSingleDialogMode()) {
            needRecorve = false;
            dismiss();
        }

    }

    @Override
    public void showInstallCarList(int orderStatus, String orderName, String orderNumber, DecodeUDPDataTool.OrderContentListItemData orderContentListItemData, List<AttachmentItemVo> attachmentItemList) {

        final InstallOrderCarListDialog mInstallOrderCarListDialog = new InstallOrderCarListDialog(context,orderStatus, orderName, orderNumber, orderContentListItemData, attachmentItemList);
        mInstallOrderCarListDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ConfigurationChangedManager.getInstance().unRegistConfig(mInstallOrderCarListDialog);
                MainActivityPresenter.removeDailogOfManager(mInstallOrderCarListDialog);

                    orderInfoWidget.updata();
                if(isDebug)System.out.println("showInstallCarList --- updata");

                if(CarMonitorApplication.isUseSingleDialogMode() && !mInstallOrderCarListDialog.isShowItemInfo()) {
                    noAnimationShow(bundView);
                }
            }
        });

        ConfigurationChangedManager.getInstance().registConfig(mInstallOrderCarListDialog);
        MainActivityPresenter.addDialogOfManager(mInstallOrderCarListDialog);
        mInstallOrderCarListDialog.show(bundView);
        if(CarMonitorApplication.isUseSingleDialogMode()) {
            needRecorve = false;
            dismiss();
        }

    }

    public void noAnimationShow(View v){
        bundView = v;
        isTouch = false;
        needRecorve = true;
        show(v, Gravity.LEFT | Gravity.START, 0, 0);
    }

    public boolean onKeyBack(){
        boolean b = orderInfoWidget.onKeyBack();

        if(b){
            updateFocusable(true);
        }
        return b;
    }

    public boolean isNeedRecorve(){
        return needRecorve;
    }
}
