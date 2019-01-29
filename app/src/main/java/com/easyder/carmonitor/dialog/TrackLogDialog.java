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
import com.easyder.carmonitor.widget.FAQWidget;
import com.easyder.carmonitor.widget.TrackLogWidget;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-12.
 */
public class TrackLogDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    private TrackLogWidget mTrackLogWidget;
    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private boolean isTouch = false;

    public TrackLogDialog(Context context,TrackActivityPresenter mpresenter) {
        super(context, R.layout.activity_track_log, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));
//        super(context, R.layout.activity_track_log, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        mTrackLogWidget = new TrackLogWidget(context, getLayout(), mpresenter, new LayoutBackListener() {
            @Override
            public void onBack() {
                dismiss();
                isTouch = true;
            }
        });


        mTrackLogWidget.setOutmostTouchListener(this);

        setFocusable(true);

        setALLWindow();

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));
    }

    public void update(){
//        mTrackLogWidget.update();
        setHeight(UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight());
    }

    public void updateLayout(){
        setHeight(UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight());
        update(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight());
    }

    public void setClickListener(TrackLogItemClick listener){
        mTrackLogWidget.setClickListener(listener);
    }

    public void show(View v){
        isTouch = false;
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(mTrackLogWidget != null) {
                    mTrackLogWidget.initAddres();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
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
}
