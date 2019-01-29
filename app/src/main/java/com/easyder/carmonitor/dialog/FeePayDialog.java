package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.FeePayBackListener;
import com.easyder.carmonitor.interfaces.OperationListener;
import com.easyder.carmonitor.interfaces.SendMessagebackListener;
import com.easyder.carmonitor.presenter.OperationActivityPresenter;
import com.easyder.carmonitor.widget.FeePayWidget;
import com.easyder.carmonitor.widget.SendMessageWidget;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-28.
 */
public class FeePayDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;


    private String plateNumber;

    private View bundView;

    public boolean needRecover = true;

    private boolean isTouch = false;

    private FeePayWidget mFeePayWidget;

    public FeePayDialog(Context context, String plateNumber) {
        super(context, R.layout.feepay_layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));

        this.plateNumber = plateNumber;

        mFeePayWidget = new FeePayWidget(context, plateNumber, getLayout(), new FeePayBackListener() {
            @Override
            public void onBack() {
                dismiss();
            }
        });

        mFeePayWidget.setOutmostTouchListener(this);

        setALLWindow();
        setFocusable(true);

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));
    }

    public void show(View v){
        isTouch = false;
        needRecover = true;
        bundView = v;
        setHeight(UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight());
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        getLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open));

    }

    public void noAnimationShow(View v){
        bundView = v;
        isTouch = false;
        show(v, Gravity.LEFT | Gravity.START, 0, 0);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            needRecover = true;
            dismiss();
            isTouch = true;
        }
        return true;
    }
}
