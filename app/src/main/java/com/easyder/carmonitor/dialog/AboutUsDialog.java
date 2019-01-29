package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.RecoverMarkerInterface;
import com.easyder.carmonitor.widget.AboutUsWidget;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-28.
 */
public class AboutUsDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private AboutUsWidget aboutUsWidget;

    private View bundView;

    private boolean isTouch = false;

    private RecoverMarkerInterface mRecoverMarkerInterface;

    public boolean needRecorve = false;

    public AboutUsDialog(Context context) {
//        super(context, R.layout.activity_about_us, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        super(context, R.layout.activity_about_us, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));


        aboutUsWidget = new AboutUsWidget(context, getLayout(), new LayoutBackListener() {
            @Override
            public void onBack() {
                needRecorve = true;
                dismiss();
                isTouch = true;
            }
        });

        aboutUsWidget.setContactUsClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ContactUsDialog contactUsDialog = new ContactUsDialog(AboutUsDialog.this.context);
                contactUsDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ConfigurationChangedManager.getInstance().unRegistConfig(contactUsDialog);
                        if(mRecoverMarkerInterface != null){
                            mRecoverMarkerInterface.todoRecoverMarker();
                        }
                    }
                });
                contactUsDialog.show(bundView);
                ConfigurationChangedManager.getInstance().registConfig(contactUsDialog);
                needRecorve = false;
                dismiss();

            }
        });

        aboutUsWidget.setOutmostTouchListener(this);
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
        bundView = v;
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

    public void setRecoverMarkerInterface(RecoverMarkerInterface inface){
        mRecoverMarkerInterface = inface;
    }
}
