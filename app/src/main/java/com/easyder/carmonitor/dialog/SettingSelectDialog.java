package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.lib.NumberPickerView;
import com.bigkoo.pickerview.view.WheelTime;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.SettingSelectListener;
import com.easyder.carmonitor.widget.TimePickerWidget;
import com.shinetech.mvp.utils.ToastUtil;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ljn on 2017/2/24.
 */
public class SettingSelectDialog extends BasePopupWindowDialog implements View.OnTouchListener, View.OnClickListener {

    private LinearLayout setting_select_content;

    private RelativeLayout setting_select_layout_outmost;

    private View btnSubmit, btnCancel;

//    TimePickerWidget mTimePickerWidget;

    private TextView tvTitle;

    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";

    private SettingSelectListener selectListener;

    private boolean isTouch = false;

    private NumberPickerView setting_select;

    public SettingSelectDialog(Context context, String[] values, String hint, int value) {
        super(context, R.layout.setting_select_layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initView(values, hint, value);
        setALLWindow();
        setFocusable(true);
        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, setting_select_content, R.anim.pop_up2down_anim));
//        setBackgrund(new ColorDrawable((Color.parseColor("#00000000"))));

    }

    private void initView(String[] values, String hint, int value){
        View layout = getLayout();

        setting_select_content = (LinearLayout) layout.findViewById(R.id.setting_select_content);

        setting_select_layout_outmost = (RelativeLayout) layout.findViewById(R.id.setting_select_layout_outmost);
        setting_select_layout_outmost.setOnTouchListener(this);

        // -----确定和取消按钮
        btnSubmit = layout.findViewById(R.id.btnSubmit);
        btnSubmit.setTag(TAG_SUBMIT);
        btnCancel = layout.findViewById(R.id.btnCancel);
        btnCancel.setTag(TAG_CANCEL);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        //顶部标题
        tvTitle = (TextView) layout.findViewById(R.id.tvTitle);

        // ----时间转轮
        setting_select = (NumberPickerView) layout.findViewById(R.id.setting_select);

        setting_select.setWrapSelectorWheel(false);
        setting_select.setDisplayedValues(values);
        setting_select.setMaxValue(values.length - 1);
        setting_select.setMinValue(0);
        setting_select.setValue(value);
        setting_select.setHintText(hint);

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


    public void show(View v){
        show(v, Gravity.LEFT | Gravity.START, 0, 0);
        //显示内容时，执行以下动画
        setting_select_content.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_down2up_anim));
        isTouch = false;
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
    public void onClick(View v) {

        String tag = (String) v.getTag();
        if (tag.equals(TAG_CANCEL)) {
            dismiss();
            isTouch = true;
            return;
        } else {

            String contentByCurrValue = setting_select.getContentByCurrValue();
//            ToastUtil.showShort("contentByCurrValue : "+contentByCurrValue);

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

}
