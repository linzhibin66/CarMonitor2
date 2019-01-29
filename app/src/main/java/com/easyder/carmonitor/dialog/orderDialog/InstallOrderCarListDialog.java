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
import com.easyder.carmonitor.interfaces.InstallOrderCarInfoChangeListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.presenter.MainActivityPresenter;
import com.easyder.carmonitor.widget.orderManager.InstallOrderCarListWidget;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;
import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.bean.orderBean.AttachmentItemVo;
import com.shinetech.mvp.utils.UIUtils;

import java.util.List;

/**
 * Created by ljn on 2017-12-06.
 */

public class InstallOrderCarListDialog extends BasePopupWindowDialog implements View.OnTouchListener, InstallOrderCarInfoChangeListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private boolean isTouch = false;

    private View bundView;

    public boolean needRecorve = false;

    private DecodeUDPDataTool.OrderContentListItemData orderContentListItemData;

    private InstallOrderCarListWidget mInstallOrderCarListWidget;

    private String orderNumber;

    private int orderStatus;

    private boolean isshowItemInfo = false;

    private String orderName;

    //附件信息
    private List<AttachmentItemVo> attachmentItemList;


    public InstallOrderCarListDialog(Context context, int orderStatus, String orderName, String orderNumber, DecodeUDPDataTool.OrderContentListItemData orderContentListItemData, List<AttachmentItemVo> attachmentItemList) {
        super(context, R.layout.install_order_carlist_layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));

        this.orderContentListItemData = orderContentListItemData;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.orderName = orderName;
        this.attachmentItemList = attachmentItemList;

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        setALLWindow();
        setFocusable(true);

        initLayout();

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));

    }

    private void initLayout(){

        mInstallOrderCarListWidget = new InstallOrderCarListWidget(context, getLayout(), orderStatus, orderNumber, orderContentListItemData, attachmentItemList, new LayoutBackListener() {
            @Override
            public void onBack() {
                dismiss();
            }
        });

        mInstallOrderCarListWidget.setOutmostTouchListener(this);
        mInstallOrderCarListWidget.setInstallOrderCarInfoChangeListener(this);

    }

    public void show(View v){
        isshowItemInfo = false;
        bundView = v;
        isTouch = false;
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        getLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open));

    }

    public void noAnimationShow(View v){
        isshowItemInfo = false;
        bundView = v;
        isTouch = false;
        show(v, Gravity.LEFT | Gravity.START, 0, 0);
        update(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight()));
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
    public void ShowToTerminalnfo(int status, InstallTerminalnfo mInstallTerminalnfo, List<AttachmentItemVo> attachmentItemList) {

        if(isshowItemInfo){
            return;
        }

        isshowItemInfo = true;
        final SaveInstallTerminaInfoDialog mSaveInstallTerminaInfoDialog = new SaveInstallTerminaInfoDialog(context,orderName, mInstallTerminalnfo, status, attachmentItemList);
        mSaveInstallTerminaInfoDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ConfigurationChangedManager.getInstance().unRegistConfig(mSaveInstallTerminaInfoDialog);
                MainActivityPresenter.removeDailogOfManager(mSaveInstallTerminaInfoDialog);
                updateFocusable(true);
                mInstallOrderCarListWidget.cleanSearchContent();
//                mInstallOrderCarListWidget.updata("");
                //isShowOrderManagerDialog 标志防止进入成功页面是跳到列表界面
                if(CarMonitorApplication.isUseSingleDialogMode()) {
                    noAnimationShow(bundView);
                }else{
                    isshowItemInfo = false;
                    update(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight()));
                }
            }
        });
        MainActivityPresenter.addDialogOfManager(mSaveInstallTerminaInfoDialog);
        ConfigurationChangedManager.getInstance().registConfig(mSaveInstallTerminaInfoDialog);
        mSaveInstallTerminaInfoDialog.show(bundView);
        updateFocusable(false);
        if(CarMonitorApplication.isUseSingleDialogMode()) {
            dismiss();
        }
    }

    public boolean isShowItemInfo(){
        return isshowItemInfo;
    }
}
