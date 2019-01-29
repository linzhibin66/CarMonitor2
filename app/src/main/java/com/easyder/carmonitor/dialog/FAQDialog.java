package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.AllFaqBackListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.widget.AllFAQWidget;
import com.easyder.carmonitor.widget.FAQWidget;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-12.
 */
public class FAQDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    private FAQWidget faqWidget;

    private boolean isShowAll = false;

    private View boundView;

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = true;

    private boolean isTouch = false;

    public boolean needRecorve = true;

    public FAQDialog(final Context context) {
        super(context, R.layout.activity_faq, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));
//        super(context, R.layout.activity_faq, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if(isShowAll){
            faqWidget = new AllFAQWidget(FAQWidget.HOT_FAQ, context, getLayout(), new AllFaqBackListener() {
                @Override
                public void onHeadClickItem(int faq_type, String title) {
                    FAQInfoDialog faqInfoDialog = new FAQInfoDialog(context, faq_type, title);
                    faqInfoDialog.show(boundView);
                    if(CarMonitorApplication.isUseSingleDialogMode()){
                        faqInfoDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                needRecorve = true;
                                noAnimationShow(boundView);
                            }
                        });
                        needRecorve = false;
                        dismiss();
                        isTouch = true;
                    }
                }

                @Override
                public void onBack() {
                    needRecorve = true;
                    dismiss();
                    isTouch = true;
                }
            });

        }else {
            faqWidget = new FAQWidget(FAQWidget.HOT_FAQ, context, getLayout(), new LayoutBackListener() {
                @Override
                public void onBack() {
                    needRecorve = true;
                    dismiss();
                    isTouch = true;

                }
            });

        }

        faqWidget.updata();
        faqWidget.setOutmostTouchListener(this);

//        setFocusable(true);

        setALLWindow();

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));
    }

    public void show(View v){
        boundView = v;
        isTouch = false;

        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        getLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open));

    }

    public void noAnimationShow(View v){
        boundView = v;
        isTouch = false;
        show(v, Gravity.LEFT | Gravity.START, 0, 0);
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

    public boolean onKeyBack(){
        boolean b = faqWidget.onKeyBack();
        if(!b && isShowing()){
            dismiss();
            return true;
        }

        return b;
    }
}
