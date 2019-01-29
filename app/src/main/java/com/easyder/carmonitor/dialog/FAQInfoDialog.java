package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.presenter.MainActivityPresenter;
import com.easyder.carmonitor.widget.AllFAQWidget;
import com.easyder.carmonitor.widget.FAQWidget;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-12.
 */
public class FAQInfoDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    private FAQWidget faqWidget;

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private boolean isTouch = false;

    public FAQInfoDialog(Context context, int type, String title) {
        super(context, R.layout.activity_faq, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));
//        super(context, R.layout.activity_faq, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        faqWidget = new FAQWidget(type,context, getLayout(), new LayoutBackListener() {
            @Override
            public void onBack() {
                dismiss();
                isTouch = true;
            }
        });

        faqWidget.setTitle(title);

        faqWidget.updataNotFoot();
        faqWidget.setOutmostTouchListener(this);
        setFocusable(true);

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
