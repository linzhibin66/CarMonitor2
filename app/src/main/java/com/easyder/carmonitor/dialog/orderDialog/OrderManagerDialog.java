package com.easyder.carmonitor.dialog.orderDialog;

import android.app.ActionBar;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.dialog.PopAmimationListener;
import com.easyder.carmonitor.interfaces.AcceptOrderDialogListener;
import com.easyder.carmonitor.interfaces.CreatOrderSearchListener;
import com.easyder.carmonitor.interfaces.EnterDialogListener;
import com.easyder.carmonitor.widget.orderManager.AcceptOrderDialogWidget;
import com.easyder.carmonitor.widget.orderManager.CreatOrderSearchPlateNumberWidget;
import com.easyder.carmonitor.widget.orderManager.EnterDialogWidget;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.OrderInfoShowContentInterfaces;
import com.easyder.carmonitor.presenter.MainActivityPresenter;
import com.easyder.carmonitor.widget.orderManager.OrderInfoWidget;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.AcceptOrderVo;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2017-11-10.
 */

public class OrderManagerDialog extends BasePopupWindowDialog implements View.OnTouchListener, OrderInfoShowContentInterfaces {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private boolean isTouch = false;

    public boolean needRecorve = false;

    private OrderInfoWidget orderInfoWidget;

    private View bundView;

    private int orderInfoType = OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE;

    private CreatOrderSearchPlateNumberWidget searchPlateNumberWidget;

    public OrderManagerDialog(Context context) {
        super(context, R.layout.activity_order_info, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        setALLWindow();

        setFocusable(true);

       /* this.presenter = (AllCarListPresenter) createPresenter();
        presenter.attachView((MvpView) this);*/
        initLayout();

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));

    }

    private void initLayout(){

        if(UserInfo.getInstance().getUserPermission() == UserInfo.USER){
            orderInfoType = OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE;
        }else {
            orderInfoType = OrderInfoWidget.INSTALL_ORDER_TYPE;
        }

        orderInfoWidget = new OrderInfoWidget(orderInfoType, context, getLayout(), new LayoutBackListener() {
            @Override
            public void onBack() {
                dismiss();
                needRecorve = true;
            }
        });

        orderInfoWidget.setOutmostTouchListener(this);

        orderInfoWidget.setOrderInterfaces(this);
    }

    public void show(View v){
        bundView = v;
        isTouch = false;
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        getLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open));

    }

    public void noAnimationShow(View v){
        bundView = v;
        isTouch = false;
        orderInfoWidget.initInfo();
        show(v, Gravity.LEFT | Gravity.START, 0, 0);
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

    /**
     * 维修单信息界面
     * @param baseOrderInfoDB
     */
    @Override
    public void showMaintenanceOrderInfo(BaseOrderInfoDB baseOrderInfoDB) {
//        ToastUtil.showShort("showMaintenanceOrderInfo");
        final MaintenanceOrderInfoDialog maintenanceOrderInfoDialog = new MaintenanceOrderInfoDialog(context, orderInfoType, baseOrderInfoDB);
        maintenanceOrderInfoDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                updateFocusable(true);
                MainActivityPresenter.removeDailogOfManager(maintenanceOrderInfoDialog);
                ConfigurationChangedManager.getInstance().unRegistConfig(maintenanceOrderInfoDialog);
                orderInfoWidget.updata();
                if(CarMonitorApplication.isUseSingleDialogMode() && maintenanceOrderInfoDialog.isNeedRecorve()) {
                    noAnimationShow(bundView);
                }else{
                    update(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight()));
                }
            }
        });

        MainActivityPresenter.addDialogOfManager(maintenanceOrderInfoDialog);
        ConfigurationChangedManager.getInstance().registConfig(maintenanceOrderInfoDialog);
        maintenanceOrderInfoDialog.show(bundView);
        updateFocusable(false);
        if(CarMonitorApplication.isUseSingleDialogMode()) {
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
            dismiss();
        }

    }

    /**
     * 创建维修点界面
     */
    @Override
    public void ShowCreatMaintenanceOrder(String orderNumber) {
//        ToastUtil.showShort("ShowCreatMaintenanceOrder ");

        final CreatMaintenanceOrderDialog mCreatMaintenanceOrderDialog = new CreatMaintenanceOrderDialog(context,this);
        mCreatMaintenanceOrderDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ConfigurationChangedManager.getInstance().unRegistConfig(mCreatMaintenanceOrderDialog);
                MainActivityPresenter.removeDailogOfManager(mCreatMaintenanceOrderDialog);
                updateFocusable(true);
                orderInfoWidget.updata();
                //isShowOrderManagerDialog 标志防止进入成功页面是跳到列表界面
                if(CarMonitorApplication.isUseSingleDialogMode() && mCreatMaintenanceOrderDialog.isShowOrderManagerDialog) {
                    noAnimationShow(bundView);
                }else{
                    update(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight()));
                }
            }
        });
        MainActivityPresenter.addDialogOfManager(mCreatMaintenanceOrderDialog);
        ConfigurationChangedManager.getInstance().registConfig(mCreatMaintenanceOrderDialog);
        if(!TextUtils.isEmpty(orderNumber)){
            mCreatMaintenanceOrderDialog.updateOrderInfo(orderNumber);
        }
        mCreatMaintenanceOrderDialog.show(bundView);
        updateFocusable(false);
        if(CarMonitorApplication.isUseSingleDialogMode()) {
            dismiss();
        }
    }

    /**
     * 删除工单提示框
     * @param baseOrderInfoDB
     */
    @Override
    public void deleteOrder(final BaseOrderInfoDB baseOrderInfoDB) {


        EnterDialogWidget mEnterDialogWidget = new EnterDialogWidget(context, new EnterDialogListener() {
            @Override
            public void OnCancel() {
                updateFocusable(true);
                orderInfoWidget.dismissHintLayout();
            }

            @Override
            public void OnEnter() {
                updateFocusable(true);
                if(baseOrderInfoDB.getOrderStatus() == 1) {
                    DBManager.deleteBaseOrder(baseOrderInfoDB);
                    orderInfoWidget.dismissHintLayout();
                    orderInfoWidget.updata();
                }else if(baseOrderInfoDB.getDataInfoId()>=0){
                    orderInfoWidget.deleteOrder(baseOrderInfoDB);
                }

            }
        });

        mEnterDialogWidget.setTitle(context.getString(R.string.enter_to_delete));
        mEnterDialogWidget.setHint(context.getString(R.string.enter_to_delete_hint));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        orderInfoWidget.addHint(mEnterDialogWidget.getView(),params);

        updateFocusable(false);

    }

    /**
     * 接单操作
     * @param baseOrderInfoDB
     */
    @Override
    public void acceptOrder(final BaseOrderInfoDB baseOrderInfoDB) {
        ToastUtil.showShort("acceptOrder");
        AcceptOrderDialogWidget<BaseVo> mAcceptOrderDialogWidget = new AcceptOrderDialogWidget<>(context, new AcceptOrderDialogListener() {
            @Override
            public void OnCancel() {
                updateFocusable(true);
                orderInfoWidget.dismissHintLayout();
            }

            @Override
            public BaseVo OnLoad() {
                return new AcceptOrderVo(baseOrderInfoDB.getOrderName(), baseOrderInfoDB.getOrderNumber(), (byte)1, null);
            }

            @Override
            public void OnSuccess() {
                updateFocusable(true);
                orderInfoWidget.dismissHintLayout();
                orderInfoWidget.updata();

            }
        });

        mAcceptOrderDialogWidget.setTitle(context.getString(R.string.enter_to_acceptorder));
        mAcceptOrderDialogWidget.setHint(context.getString(R.string.enter_to_acceptorder_hint));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        orderInfoWidget.addHint(mAcceptOrderDialogWidget.getView(), params);

        updateFocusable(false);
    }

    /**
     * 上传维修结果
     * @param baseOrderInfoDB
     */
    @Override
    public void upLoadMaintenanceResult(BaseOrderInfoDB baseOrderInfoDB) {
        ToastUtil.showShort("upLoadMaintenanceResult");
        final UpLoadMaintenanceResultDialog mUpLoadMaintenanceResultDialog = new UpLoadMaintenanceResultDialog(context,baseOrderInfoDB,new ArrayList<String>());
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
            dismiss();
        }
    }

    /*@Override
    public void acceptInstallOrder(InstallOrderBaseInfo installOrderitem) {
        ToastUtil.showShort("acceptInstallOrder OrderNumber = "+installOrderitem.getOrderNumber());
    }*/

    @Override
    public void upLoadinstallResult(InstallOrderBaseInfo installOrderitem) {
        ToastUtil.showShort("upLoadinstallResult OrderNumber = "+installOrderitem.getOrderNumber());




    }

    @Override
    public void showInstallOrderInfo(InstallOrderBaseInfo installOrderitem) {
//        ToastUtil.showShort("showInstallOrderInfo OrderNumber = "+installOrderitem.getOrderNumber());

        final InstallOrderInfoDialog mInstallOrderInfoDialog = new InstallOrderInfoDialog(context, installOrderitem);

        mInstallOrderInfoDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                updateFocusable(true);
                MainActivityPresenter.removeDailogOfManager(mInstallOrderInfoDialog);
                ConfigurationChangedManager.getInstance().unRegistConfig(mInstallOrderInfoDialog);
                if(CarMonitorApplication.isUseSingleDialogMode()) {
                    noAnimationShow(bundView);
                }
            }
        });

        MainActivityPresenter.addDialogOfManager(mInstallOrderInfoDialog);
        ConfigurationChangedManager.getInstance().registConfig(mInstallOrderInfoDialog);
        mInstallOrderInfoDialog.show(bundView);
        updateFocusable(false);
        if(CarMonitorApplication.isUseSingleDialogMode()) {
            dismiss();
        }
    }

    @Override
    public void showSearchPlateNumberLayout(String searchStr, final CreatOrderSearchListener listener){
        if(searchPlateNumberWidget == null) {
            searchPlateNumberWidget = new CreatOrderSearchPlateNumberWidget(context, listener);

            if(!TextUtils.isEmpty(searchStr)){
                searchPlateNumberWidget.updataSearch(searchStr);
            }
        }else{
            searchPlateNumberWidget.updataSearch(searchStr);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        orderInfoWidget.getHintLayoutParams().setMargins(0,UIUtils.getStatusBarHeight()+UIUtils.dip2px(57)+UIUtils.dip2px(40),0,0);
        orderInfoWidget.addHint(searchPlateNumberWidget.getView(), params);
    }

    @Override
    public void dismissHintLayout() {
        if(orderInfoWidget != null) {
            orderInfoWidget.dismissHintLayout();
        }
    }

    public boolean onKeyBack(){
        boolean b = orderInfoWidget.onKeyBack();

        if(b){
            updateFocusable(true);
        }
        return b;
    }
}
