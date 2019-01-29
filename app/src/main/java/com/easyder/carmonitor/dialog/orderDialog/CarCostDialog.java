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
import com.easyder.carmonitor.interfaces.CarCostBackListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.widget.carCost.CarCostWidget;
import com.shinetech.mvp.DB.bean.CarCostInfo;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-12-11.
 */

public class CarCostDialog extends BasePopupWindowDialog implements View.OnTouchListener{

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private boolean isTouch = false;

    private View bundView;

    public boolean needRecorve = false;

    private CarCostWidget mCarCostWidget;

    private CarCostBackListener listener;

    private String plateNumber;

    public CarCostDialog(Context context, String plateNumber, CarCostBackListener listener) {
        super(context, R.layout.car_cost_layout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));
        this.plateNumber = plateNumber;
        this.listener = listener;
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

        mCarCostWidget = new CarCostWidget(this,context, plateNumber, getLayout(), listener);

        mCarCostWidget.setOutmostTouchListener(this);

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
}
