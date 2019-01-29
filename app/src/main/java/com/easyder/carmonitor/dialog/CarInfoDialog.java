package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.CarInfoLayoutListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.widget.CarInfoWidget;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017/4/07
 */
public class CarInfoDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private CarInfoWidget carInfoWidget;

    private boolean isTouch = false;

    public boolean needRecover = true;

    public CarInfoDialog(Context context,CarInfoBean newestCarInfo,String adrees,CarInfoLayoutListener listener) {
        super(context,R.layout.carinfo_activity, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));
//        super(context,R.layout.carinfo_activity, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        carInfoWidget = new CarInfoWidget(context, getLayout(),  listener);

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        setALLWindow();

        setFocusable(true);

        carInfoWidget.upData(newestCarInfo, adrees);
        carInfoWidget.setOutmostTouchListener(this);

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));
    }

    public void updataUI(CarInfoBean newestCarInfo,String adrees){
        carInfoWidget.cleanlayout();
        carInfoWidget.upData(newestCarInfo, adrees);
        setHeight(UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight());
    }

    public void show(View v){
        isTouch = false;
        needRecover = true;
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        getLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open));

    }

    public void noAnimationShow(View v){
        isTouch = false;
        needRecover = true;
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
