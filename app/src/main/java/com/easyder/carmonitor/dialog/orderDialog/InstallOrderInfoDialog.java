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
import com.easyder.carmonitor.interfaces.InstallChangeStatusListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.presenter.MainActivityPresenter;
import com.easyder.carmonitor.widget.orderManager.AcceptOrderDialogWidget;
import com.easyder.carmonitor.widget.orderManager.InstallOrderInfoWidget;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by Lenovo on 2017-12-05.
 */

public class InstallOrderInfoDialog extends BasePopupWindowDialog implements View.OnTouchListener, InstallChangeStatusListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private boolean isTouch = false;

    private View bundView;

    public boolean needRecorve = false;

    private InstallOrderBaseInfo installOrderitem;

    private InstallOrderInfoWidget mInstallOrderInfoWidget;

    public InstallOrderInfoDialog(Context context ,InstallOrderBaseInfo installOrderitem) {
        super(context, R.layout.install_order_info, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));

        this.installOrderitem = installOrderitem;

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

        mInstallOrderInfoWidget = new InstallOrderInfoWidget(context, getLayout(), installOrderitem, new LayoutBackListener() {
            @Override
            public void onBack() {
                dismiss();
            }
        });

        mInstallOrderInfoWidget.setInstallChangeStatusListener(this);
        mInstallOrderInfoWidget.setOutmostTouchListener(this);


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

    public void noAnimationShow(View v){
        bundView = v;
        isTouch = false;
        show(v, Gravity.LEFT | Gravity.START, 0, 0);
    }

    public boolean onKeyBack(){
        boolean b = mInstallOrderInfoWidget.onKeyBack();

        if(b){
            updateFocusable(true);
        }
        return b;
    }

    @Override
    public void acceptOrder(final InstallOrderBaseInfo installOrderitem) {

        AcceptOrderDialogWidget<BaseVo> mAcceptOrderDialogWidget = new AcceptOrderDialogWidget<>(context, new AcceptOrderDialogListener() {
            @Override
            public void OnCancel() {
                updateFocusable(true);
                mInstallOrderInfoWidget.dismissHintLayout();
            }

            @Override
            public BaseVo OnLoad() {
                return null;
            }

            @Override
            public void OnSuccess() {
                updateFocusable(true);
                mInstallOrderInfoWidget.dismissHintLayout();
                mInstallOrderInfoWidget.updata(installOrderitem);

            }
        });

        mAcceptOrderDialogWidget.setTitle(context.getString(R.string.enter_to_acceptorder));
        mAcceptOrderDialogWidget.setHint(context.getString(R.string.enter_to_acceptorder_hint));
        mInstallOrderInfoWidget.addHint(mAcceptOrderDialogWidget.getView());

        updateFocusable(false);

    }

    @Override
    public void upLoadDialogFocusable(boolean focusable) {
        updateFocusable(focusable);
    }

   /* @Override
    public void upLoadInstallResult(final InstallOrderBaseInfo installOrderitem) {

        AcceptOrderDialogWidget<BaseVo> mpLoadInstallDialogWidget = new AcceptOrderDialogWidget<>(context, new AcceptOrderDialogListener() {
            @Override
            public void OnCancel() {
                updateFocusable(true);
                mInstallOrderInfoWidget.dismissHintLayout();
            }

            @Override
            public BaseVo OnLoad() {
                return null;
            }

            @Override
            public void OnSuccess() {
                updateFocusable(true);
                mInstallOrderInfoWidget.dismissHintLayout();
                mInstallOrderInfoWidget.updata(installOrderitem);

            }
        });

        mpLoadInstallDialogWidget.setTitle(context.getString(R.string.enter_to_upload_install_info));
        mpLoadInstallDialogWidget.setHint(context.getString(R.string.enter_to_end_install_hint));
        mInstallOrderInfoWidget.addHint(mpLoadInstallDialogWidget.getView());

        updateFocusable(false);

    }*/

    @Override
    public void showInstallCarList(InstallOrderBaseInfo installOrderitem) {

       /* final InstallOrderCarListDialog mInstallOrderCarListDialog = new InstallOrderCarListDialog(context,installOrderitem);
        mInstallOrderCarListDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ConfigurationChangedManager.getInstance().unRegistConfig(mInstallOrderCarListDialog);
                MainActivityPresenter.removeDailogOfManager(mInstallOrderCarListDialog);
                if(CarMonitorApplication.isUseSingleDialogMode()) {
                    noAnimationShow(bundView);
                }
                mInstallOrderInfoWidget.updata(InstallOrderInfoDialog.this.installOrderitem);
            }
        });

        ConfigurationChangedManager.getInstance().registConfig(mInstallOrderCarListDialog);
        MainActivityPresenter.addDialogOfManager(mInstallOrderCarListDialog);
        mInstallOrderCarListDialog.show(bundView);
        if(CarMonitorApplication.isUseSingleDialogMode()) {
            dismiss();
        }*/

    }
}
