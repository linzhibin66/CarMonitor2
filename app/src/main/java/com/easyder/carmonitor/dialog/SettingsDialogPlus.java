package com.easyder.carmonitor.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bigkoo.pickerview.lib.NumberPickerView;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.SettingSelectListener;

/**
 * Created by ljn on 2017-06-06.
 */
public class SettingsDialogPlus extends Dialog implements View.OnClickListener, View.OnTouchListener {

    private RelativeLayout setting_select_layout_outmost;

    private View btnSubmit, btnCancel;

    private TextView tvTitle;

    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";

    private SettingSelectListener selectListener;

    private boolean isTouch = false;

    private NumberPickerView setting_select;

    public SettingsDialogPlus(Context context) {
        super(context, R.style.popupDialog);
        init();

    }

    public SettingsDialogPlus(Context context, int themeResId) {
        super(context, themeResId);
        init();

    }

    private void init(){
        setContentView(R.layout.setting_select_layout);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        getWindow().setWindowAnimations(R.style.popwindow_anim_upanddown);
    }

    public void initView(String[] values, String hint, int value){

        setting_select_layout_outmost = (RelativeLayout) findViewById(R.id.setting_select_layout_outmost);
        setting_select_layout_outmost.setBackgroundResource(R.color.transparent);
        setting_select_layout_outmost.setOnTouchListener(this);

        // -----确定和取消按钮
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setTag(TAG_SUBMIT);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setTag(TAG_CANCEL);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        //顶部标题
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        // ----时间转轮
        setting_select = (NumberPickerView) findViewById(R.id.setting_select);

        setting_select.setWrapSelectorWheel(false);
        setting_select.setDisplayedValues(values);
        setting_select.setMaxValue(values.length - 1);
        setting_select.setMinValue(0);
        setting_select.setValue(value);
        setting_select.setHintText(hint);

    }

    @Override
    public void show() {
        findViewById(R.id.setting_select_content).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.pop_down2up_anim));
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
                SettingsDialogPlus.super.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        findViewById(R.id.setting_select_content).startAnimation(animation);

    }

    /**
     * 设置显示位置
     * @param index
     */
    public void setindex(int index){
        setting_select.setValue(index);
    }

    public void setTitle(String title){
        tvTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_CANCEL)) {
            dismiss();
            isTouch = true;
            return;
        } else {

            String contentByCurrValue = setting_select.getContentByCurrValue();

            if (selectListener != null) {
                selectListener.onSelect(contentByCurrValue);
            }
            dismiss();
            isTouch = true;
            return;
        }
    }

    public void setOnSelectListener(SettingSelectListener selectListener) {
        this.selectListener = selectListener;
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
