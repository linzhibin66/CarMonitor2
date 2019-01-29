package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.TimePickerEnterListener;
import com.easyder.carmonitor.widget.MyTimePickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ljn on 2017/2/24.
 * 时间选择dialog
 */
public class TimePickerDialog extends BasePopupWindowDialog implements View.OnTouchListener, View.OnClickListener {

//    private LinearLayout timepick_content;
    private RelativeLayout timepicker_outmost;

    /**
     * 取消按钮
     */
    private TextView time_picker_cancel;

    /**
     * 确定按钮
     */
    private TextView time_picker_enter;

    /**
     * 开始时间，年
     */
    private TextView start_year;

    /**
     * 开始时间，月
     */
    private TextView start_month;

    /**
     * 开始时间，日
     */
    private TextView start_day;

    /**
     * 开始时间，时
     */
    private TextView start_hour;

    /**
     * 结束时间，年
     */
    private TextView end_year;

    /**
     * 结束时间，月
     */
    private TextView end_month;

    /**
     * 结束时间，日
     */
    private TextView end_day;

    /**
     * 结束时间，时
     */
    private TextView end_hour;

    /**
     * 报警类型
     */
    private Spinner timepick_alarm_type;

    /**
     * 取消
     */
    private TextView cancel;

    /**
     * 确定
     */
    private TextView enter;

    /**
     * 确定监听
     */
    private TimePickerEnterListener mTimePickerEnterListener;

    /**
     * 开始时间布局
     */
    private  LinearLayout startLayout;

    /**
     * 结束时间布局
     */
    private LinearLayout endLayout;

    /**
     * 标题
     */
    private TextView time_picker_title;

    private Date startDate;

    private Date endDate;

    private int alarmType;

    private View attachView;

    private final long ONE_HOUR = 60*60*1000;

    private MyTimePickerView mMyTimePickerView;


    private boolean isTouch = false;


    public TimePickerDialog(Context context,boolean isTrack) {
        super(context, R.layout.time_picker_dialog_layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initView(isTrack);
        setALLWindow();
        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
//        setPopAmimationListener(new PopAmimationListener(context,timepick_content,R.anim.pop_left2right_anim_close));
//        setBackgrund(new ColorDrawable((Color.parseColor("#00000000"))));

    }

    private void initView(boolean isTrack){
        View layout = getLayout();

//        timepick_content = (LinearLayout) layout.findViewById(R.id.timepick_content);
        timepicker_outmost = (RelativeLayout) layout.findViewById(R.id.timepicker_outmost);

//        timepicker_outmost.setOnTouchListener(this);

        time_picker_title = (TextView) layout.findViewById(R.id.time_picker_title);


        time_picker_cancel = (TextView) layout.findViewById(R.id.time_picker_cancel);
        time_picker_enter = (TextView) layout.findViewById(R.id.time_picker_enter);
        start_year = (TextView) layout.findViewById(R.id.timepicker_start_year);
        start_month = (TextView) layout.findViewById(R.id.timepicker_start_month);
        start_day = (TextView) layout.findViewById(R.id.timepicker_start_day);
        start_hour = (TextView) layout.findViewById(R.id.timepicker_start_hour);
        end_year = (TextView) layout.findViewById(R.id.timepicker_end_year);
        end_month = (TextView) layout.findViewById(R.id.timepicker_end_month);
        end_day = (TextView) layout.findViewById(R.id.timepicker_end_day);
        end_hour = (TextView) layout.findViewById(R.id.timepicker_end_hour);

        timepick_alarm_type = (Spinner) layout.findViewById(R.id.timepick_alarm_type);

        cancel = (TextView) layout.findViewById(R.id.time_picker_cancel);
        enter = (TextView) layout.findViewById(R.id.time_picker_enter);
        cancel.setOnClickListener(this);
        enter.setOnClickListener(this);

        initTime();

        startLayout = (LinearLayout) layout.findViewById(R.id.timepicker_starttime_layout);
        endLayout = (LinearLayout) layout.findViewById(R.id.timepicker_endtime_layout);
        if(!isTrack){
            LinearLayout alarmLayout = (LinearLayout) layout.findViewById(R.id.timepicker_alarm_layout);
            alarmLayout.setVisibility(View.VISIBLE);

        }

        startLayout.setOnClickListener(this);
        endLayout.setOnClickListener(this);

    }

    public void setTitle(String title){
        time_picker_title.setText(title);
    }

    /**
     * 初始化时间
     */
    private void initTime(){

        long currentTimeMillis = System.currentTimeMillis();
        Date start_date = new Date(currentTimeMillis);
        setStartTime(start_date);

        currentTimeMillis  = currentTimeMillis+ONE_HOUR;
        Date end_date = new Date(currentTimeMillis);
        setEndTime(end_date);

    }

    /**
     * 时间转换
     * @param date
     */
    private int[] getTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date == null)
            calendar.setTimeInMillis(System.currentTimeMillis());
        else
            calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);

        return new int[]{year,month,day,hours};

    }

    public void setStartTime(Date date){
        startDate = date;
        int[] time = getTime(date);
        start_year.setText(time[0]+"");
        start_month.setText((time[1]+1)+"");
        start_day.setText(time[2]+"");
        start_hour.setText(time[3]+"");

    }

    public void setEndTime(Date date){
        endDate = date;
        int[] time = getTime(date);
        end_year.setText(time[0]+"");
        end_month.setText((time[1]+1) + "");
        end_day.setText(time[2]+"");
        end_hour.setText(time[3] + "");
    }

    public Date getStartTime(){
        return startDate;
    }

    public Date getEndTime(){
        return endDate;
    }

    public void show(View v){
        attachView = v;
        show(v, Gravity.LEFT | Gravity.START, 0, 0);
        //显示内容时，执行以下动画
//        timepick_content.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_left2right_anim_open));
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

        int id = v.getId();

        switch (id){
            case R.id.time_picker_cancel:
                dismiss();
                isTouch = true;
                break;
            case R.id.time_picker_enter:
                if(mTimePickerEnterListener!=null){
//                    "2017-02-16 10:00:00"
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
                    String startTime = format.format(startDate)+":00:00";
                    String sendTime = format.format(endDate)+":00:00";

                    mTimePickerEnterListener.onEnter(startTime, sendTime, alarmType);
                    dismiss();
                    isTouch = true;
                }
                break;
            case R.id.timepicker_starttime_layout:
                if(mMyTimePickerView == null){
                    mMyTimePickerView = new  MyTimePickerView(context, TimePickerView.Type.YEAR_MONTH_DAY_HOURS);
                }
                mMyTimePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date) {
                        setStartTime(date);
                    }
                });
                mMyTimePickerView.setTitle(context.getString(R.string.start_time));
                mMyTimePickerView.setTime(startDate);
                mMyTimePickerView.show(attachView);
                break;
            case R.id.timepicker_endtime_layout:

                if(mMyTimePickerView == null){
                    mMyTimePickerView = new  MyTimePickerView(context, TimePickerView.Type.YEAR_MONTH_DAY_HOURS);
                }

                mMyTimePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date) {
                        setEndTime(date);
                    }
                });
                mMyTimePickerView.setTitle(context.getString(R.string.end_time));
                mMyTimePickerView.setTime(endDate);
                mMyTimePickerView.show(attachView);
                break;
        }
    }

    public void setOnEnterListeren(TimePickerEnterListener listener){
        mTimePickerEnterListener = listener;
    }
}
