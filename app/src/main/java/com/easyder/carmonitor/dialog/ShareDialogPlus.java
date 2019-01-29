package com.easyder.carmonitor.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.lib.NumberPickerView;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.SettingSelectListener;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.ShareUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

/**
 * Created by ljn on 2017-06-06.
 */
public class ShareDialogPlus extends Dialog implements View.OnClickListener, View.OnTouchListener {

    private RelativeLayout share_layout_outmost;

    private View btnCancel;

    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";

    private boolean isTouch = false;

    private boolean isDebug = false && LogUtils.isDebug;

    private Context context;

    public ShareDialogPlus(Context context) {
        super(context, R.style.popupDialog);
        this.context = context;
        init();

    }

    public ShareDialogPlus(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        init();

    }

    private void init(){
        setContentView(R.layout.share_layout);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        getWindow().setWindowAnimations(R.style.popwindow_anim_upanddown);
        initView();
    }

    private void initView(){

        share_layout_outmost = (RelativeLayout) findViewById(R.id.share_layout_outmost);
        share_layout_outmost.setBackgroundResource(R.color.transparent);
        share_layout_outmost.setOnTouchListener(this);

        // -----确定和取消按钮
        btnCancel = findViewById(R.id.share_cancel);
        btnCancel.setTag(TAG_CANCEL);
        btnCancel.setOnClickListener(this);

        LinearLayout share_wechat = (LinearLayout) findViewById(R.id.share_wechat);
        share_wechat.setOnClickListener(this);

        LinearLayout share_wechatmoments = (LinearLayout) findViewById(R.id.share_wechatmoments);
        share_wechatmoments.setOnClickListener(this);

       /* LinearLayout share_sinaweibo = (LinearLayout) findViewById(R.id.share_sinaweibo);
        share_sinaweibo.setOnClickListener(this);*/

        LinearLayout share_qq = (LinearLayout) findViewById(R.id.share_qq);
        share_qq.setOnClickListener(this);

        LinearLayout share_qzone = (LinearLayout) findViewById(R.id.share_qzone);
        share_qzone.setOnClickListener(this);

        LinearLayout share_copy_link = (LinearLayout) findViewById(R.id.share_copy_link);
        share_copy_link.setOnClickListener(this);

    }

    @Override
    public void show() {
        findViewById(R.id.share_content).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.pop_down2up_anim));
        super.show();
    }

    @Override
    public void dismiss() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.pop_up2down_anim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ShareDialogPlus.super.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        findViewById(R.id.share_content).startAnimation(animation);

    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (TAG_CANCEL.equals(tag)) {
            if(!isTouch){
                dismiss();
                isTouch = true;
            }
            return;
        }

        switch (v.getId()){
            case R.id.share_wechat:
                ShareUtils.shareWX(context, R.mipmap.ic_launcher);
                break;
            case R.id.share_wechatmoments:
                ShareUtils.shareWXmoments(context, R.mipmap.ic_launcher);
                break;
            case R.id.share_sinaweibo:
                break;
            case R.id.share_qq:
                ShareUtils.shareQQ((Activity) context, new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        if(isDebug)System.out.println(" shareQQ IUiListener onComplete : " + o.toString());
                    }

                    @Override
                    public void onError(UiError uiError) {
                        if(isDebug)System.out.println(" shareQQ IUiListener onError code : " + uiError.errorCode + " , msg : " + uiError.errorMessage + " , detail : " + uiError.errorDetail);
                    }

                    @Override
                    public void onCancel() {
                        if(isDebug)System.out.println(" shareQQ IUiListener onCancel . . . . . ");
                    }
                });
                break;
            case R.id.share_qzone:
                //分享类型 QZONE
                ShareUtils.shareQzone((Activity) context, new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        if(isDebug)System.out.println(" shareQzone IUiListener onComplete : " + o.toString());
                    }

                    @Override
                    public void onError(UiError uiError) {
                        if(isDebug)System.out.println(" shareQzone IUiListener onError code : " + uiError.errorCode + " , msg : " + uiError.errorMessage + " , detail : " + uiError.errorDetail);
                    }

                    @Override
                    public void onCancel() {
                        if(isDebug)System.out.println(" shareQzone IUiListener onCancel . . . . . ");
                    }
                });
                break;
            case R.id.share_copy_link:
                break;
        }
        if(!isTouch){
            dismiss();
            isTouch = true;
        }
    }

   @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            dismiss();
            isTouch = true;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(isTouch){
                return true;
            }
            isTouch = true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
