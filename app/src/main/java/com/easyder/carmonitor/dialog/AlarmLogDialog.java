package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.TrackLogItemClick;
import com.easyder.carmonitor.presenter.TrackActivityPresenter;
import com.easyder.carmonitor.widget.AlarmLogWidget;
import com.easyder.carmonitor.widget.TrackLogWidget;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-12.
 */
public class AlarmLogDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    private AlarmLogWidget mAlarmLogWidget;
    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private boolean isTouch = false;

    public AlarmLogDialog(String platenumber, String startTime, String endTime, Context context) {
//        super(context, R.layout.activity_alarm_log, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        super(context, R.layout.activity_alarm_log, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));

        mAlarmLogWidget = new AlarmLogWidget(platenumber , startTime, endTime,context,getLayout(), new LayoutBackListener() {
            @Override
            public void onBack() {
                dismiss();
                isTouch = true;
            }
        });

        /*context, getLayout(), mpresenter, new LayoutBackListener() {
            @Override
            public void onBack() {
                dismiss();
            }
        });*/


        mAlarmLogWidget.setOutmostTouchListener(this);

//        setFocusable(true);

        setALLWindow();

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));
    }

    public void show(View v){
        isTouch = false;
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open);
        getLayout().startAnimation(animation);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            dismiss();
            isTouch = true;
        }
        return true;
    }
    public boolean onKeyBack(){
        boolean dismissFilter = mAlarmLogWidget.onKeyBack();
        if(!dismissFilter && isShowing()){
            dismiss();
            return true;
        }
        return dismissFilter;
    }

}
