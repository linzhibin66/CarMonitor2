package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.widget.CarAlarmWidget;
import com.easyder.carmonitor.widget.CarStatusWidget;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017/4/11
 */
public class CarAlarmDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private boolean isTouch = false;

    private CarAlarmWidget carAlarmWidget;

    public CarAlarmDialog(Context context, CarInfoBean newestCarInfo) {
        super(context,R.layout.car_alarm_activity, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));
//        super(context,R.layout.car_alarm_activity, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        carAlarmWidget = new CarAlarmWidget(context, getLayout(),  new LayoutBackListener() {
            @Override
            public void onBack() {
                dismiss();
                isTouch = true;
            }
        });

        setALLWindow();

        setFocusable(true);

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        carAlarmWidget.setTouchOutmost(this);
        carAlarmWidget.updateAlarm(newestCarInfo);

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));
    }

    public void updataUI(CarInfoBean newestCarInfo){
        carAlarmWidget.updateAlarm(newestCarInfo);
        setHeight(UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight());
    }

    public void show(View v){
        isTouch = false;
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        getLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open));

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            dismiss();
            isTouch = true;
        }
        return true;
    }
}
