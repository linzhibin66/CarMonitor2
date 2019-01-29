package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.widget.CarStatusWidget;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017/4/07
 */
public class CarStatusDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private CarStatusWidget carstatusWidget;

    private boolean isTouch = false;

    public CarStatusDialog(Context context, CarInfoBean newestCarInfo) {
        super(context,R.layout.carstatus_activity, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));
//        super(context,R.layout.carstatus_activity, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        carstatusWidget = new CarStatusWidget(context, getLayout(),  new LayoutBackListener() {
            @Override
            public void onBack() {
                dismiss();
                isTouch = true;
            }
        });

        carstatusWidget.setPlateNumber(newestCarInfo.getPlateNumber());

        setALLWindow();

        setFocusable(true);

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }
        carstatusWidget.updateAllStatus(newestCarInfo.getStatus());
        carstatusWidget.setOutmostTouchListener(this);

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));
    }

    public void updataUI(CarInfoBean newestCarInfo){
        carstatusWidget.updateAllStatus(newestCarInfo.getStatus());
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
