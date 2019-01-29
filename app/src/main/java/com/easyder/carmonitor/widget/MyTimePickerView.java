package com.easyder.carmonitor.widget;

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
import com.bigkoo.pickerview.view.WheelTime;
import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.dialog.PopAmimationListener;
import com.easyder.carmonitor.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ljn on 2017/2/24.
 */
public class MyTimePickerView extends BasePopupWindowDialog implements View.OnTouchListener, View.OnClickListener {

    private LinearLayout pickerview_content;

    private RelativeLayout pickerview_layout_outmost;

    private View btnSubmit, btnCancel;

    WheelTime wheelTime;

    private TextView tvTitle;

    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";

    private TimePickerView.OnTimeSelectListener timeSelectListener;

    private boolean isTouch = false;


    public MyTimePickerView(Context context , TimePickerView.Type type) {
        super(context, R.layout.my_pickerview_time, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initView(type);
        setALLWindow();
        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, pickerview_content, R.anim.pop_up2down_anim));
//        setBackgrund(new ColorDrawable((Color.parseColor("#00000000"))));

    }

    private void initView(TimePickerView.Type type){
        View layout = getLayout();

        pickerview_content = (LinearLayout) layout.findViewById(R.id.pickerview_content);

        pickerview_layout_outmost = (RelativeLayout) layout.findViewById(R.id.pickerview_layout_outmost);
        pickerview_layout_outmost.setOnTouchListener(this);

        // -----确定和取消按钮
        btnSubmit = layout.findViewById(com.bigkoo.pickerview.R.id.btnSubmit);
        btnSubmit.setTag(TAG_SUBMIT);
        btnCancel = layout.findViewById(com.bigkoo.pickerview.R.id.btnCancel);
        btnCancel.setTag(TAG_CANCEL);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        //顶部标题
        tvTitle = (TextView) layout.findViewById(com.bigkoo.pickerview.R.id.tvTitle);
        // ----时间转轮
        final View timepickerview = layout.findViewById(com.bigkoo.pickerview.R.id.timepicker);
        wheelTime = new WheelTime(timepickerview, type);

        //默认选中当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        wheelTime.setStartYear(year-3);
        wheelTime.setEndYear(year);

        wheelTime.setPicker(year, month, day, hours, minute);
        wheelTime.setYearCyclic(false);

    }

    public void setTitle(String title){
        tvTitle.setText(title);
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic
     */
    public void setCyclic(boolean cyclic) {
        wheelTime.setCyclic(cyclic);
    }

    /**
     * 设置选中时间
     * @param date
     */
    public void setTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date == null)
            calendar.setTimeInMillis(System.currentTimeMillis());
        else
            calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelTime.setPicker(year, month, day, hours, minute);
    }

    public void show(View v){
        show(v, Gravity.LEFT | Gravity.START, 0, 0);
        //显示内容时，执行以下动画
        pickerview_content.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_down2up_anim));
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
            return;
        } else {
            if (timeSelectListener != null) {
                try {
                    Date date = WheelTime.dateFormat.parse(wheelTime.getTime());
                    timeSelectListener.onTimeSelect(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            dismiss();
            return;
        }

    }

    public void setOnTimeSelectListener(TimePickerView.OnTimeSelectListener timeSelectListener) {
        this.timeSelectListener = timeSelectListener;
    }
}
