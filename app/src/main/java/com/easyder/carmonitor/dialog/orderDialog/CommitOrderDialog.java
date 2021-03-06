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
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.RecoverMarkerInterface;
import com.easyder.carmonitor.widget.orderManager.CreatMaintenanceOrderSuccessWidget;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-28.
 */
public class CommitOrderDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private CreatMaintenanceOrderSuccessWidget mCreatMaintenanceOrderSuccessWidget;

    private View bundView;

    private boolean isTouch = false;

    private RecoverMarkerInterface mRecoverMarkerInterface;

    private String orderNumber;

    private LayoutBackListener mLayoutBackListener;

    public CommitOrderDialog(Context context,String orderNumber,LayoutBackListener mLayoutBackListener) {
        super(context, R.layout.creat_maintenance_order_success, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));

        this.orderNumber = orderNumber;

        this.mLayoutBackListener = mLayoutBackListener;

        mCreatMaintenanceOrderSuccessWidget = new CreatMaintenanceOrderSuccessWidget(context, getLayout(), new LayoutBackListener() {
            @Override
            public void onBack() {
                if(CommitOrderDialog.this.mLayoutBackListener != null) {
                    CommitOrderDialog.this.mLayoutBackListener.onBack();
                }
                isTouch = true;
                dismiss();
            }
        });

        mCreatMaintenanceOrderSuccessWidget.setOrderNumber(this.orderNumber);

        mCreatMaintenanceOrderSuccessWidget.setOutmostTouchListener(this);
        setALLWindow();
        setFocusable(true);

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));
    }


    public void showOrderListener(View.OnClickListener showListlistener){
        mCreatMaintenanceOrderSuccessWidget.setOnShowOrderListClickListener(showListlistener);
    }

    public void show(View v){
        isTouch = false;
        bundView = v;
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        getLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open));

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            if(CommitOrderDialog.this.mLayoutBackListener != null) {
                CommitOrderDialog.this.mLayoutBackListener.onBack();
            }
            isTouch = true;
            dismiss();
        }
        return true;
    }

}
