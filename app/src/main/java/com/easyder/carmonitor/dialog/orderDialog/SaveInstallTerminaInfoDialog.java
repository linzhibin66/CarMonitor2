package com.easyder.carmonitor.dialog.orderDialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.dialog.PopAmimationListener;
import com.easyder.carmonitor.interfaces.SaveInstallTermianinfoLayoutBackListener;
import com.easyder.carmonitor.widget.orderManager.SaveInstallTerminaInfoWidget;
import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.network.UDP.bean.orderBean.AttachmentItemVo;
import com.shinetech.mvp.utils.UIUtils;

import java.util.List;

/**
 * Created by ljn on 2017-12-07.
 */

public class SaveInstallTerminaInfoDialog extends BasePopupWindowDialog implements View.OnTouchListener{

    private Context context;

    private InstallTerminalnfo mInstallTerminalnfo;

    private boolean isTouch = false;

    private View bundView;

    public boolean needRecorve = false;

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private int installOrderStatus;

    private String orderName;

    private List<AttachmentItemVo> attachmentItemList;

    public SaveInstallTerminaInfoDialog(Context context, String orderName, InstallTerminalnfo mInstallTerminalnfo, int installOrderStatus, List<AttachmentItemVo> attachmentItemList) {
        super(context, R.layout.install_order_terminainfo_layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));
        this.installOrderStatus = installOrderStatus;
        this.context = context;
        this.mInstallTerminalnfo = mInstallTerminalnfo;
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

        SaveInstallTerminaInfoWidget saveInstallTerminaInfoWidget = new SaveInstallTerminaInfoWidget(context, getLayout(), installOrderStatus, orderName, mInstallTerminalnfo, attachmentItemList, new SaveInstallTermianinfoLayoutBackListener() {
            @Override
            public void onBack() {
                dismiss();
            }

            @Override
            public void updateDialogFocusable(boolean focusable) {
                updateFocusable(focusable);
            }
        });

        saveInstallTerminaInfoWidget.setOutmostTouchListener(this);

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
}
