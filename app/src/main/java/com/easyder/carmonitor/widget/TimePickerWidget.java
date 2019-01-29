package com.easyder.carmonitor.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import com.bigkoo.pickerview.lib.NumberPickerView;
import com.easyder.carmonitor.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ljn on 2017-04-24.
 */
public class TimePickerWidget {

    private Context context;

    /**
     * 取消
     */
    private TextView btnCancel;

    /**
     * 标题
     */
    private TextView tvTitle;

    /**
     * 确定
     */
    private TextView btnSubmit;

    public static final int DEFULT_START_YEAR = 1990;
    public static final int DEFULT_END_YEAR = 2100;
    private int startYear = DEFULT_START_YEAR;
    private int endYear = DEFULT_END_YEAR;

    private NumberPickerView yearPickerView;
    private NumberPickerView monthPickerView;
    private NumberPickerView dayPickerView;
    private NumberPickerView hourPickerView;
    private NumberPickerView minPickerView;

    public TimePickerWidget(Context context,View rootView) {
        this.context = context;
        initView(rootView);
    }

    private void initView(View rootView){
        btnCancel = (TextView) rootView.findViewById(R.id.btnCancel);
        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        btnSubmit = (TextView) rootView.findViewById(R.id.btnSubmit);
        yearPickerView= (NumberPickerView) rootView.findViewById(R.id.year);
        monthPickerView= (NumberPickerView) rootView.findViewById(R.id.month);
        dayPickerView= (NumberPickerView) rootView.findViewById(R.id.day);
        hourPickerView= (NumberPickerView) rootView.findViewById(R.id.hour);
        minPickerView= (NumberPickerView) rootView.findViewById(R.id.min);
    }

    private void setData(NumberPickerView picker, int minValue, int maxValue, int value){
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setValue(value);
    }

    public void setPicker(int year ,int month ,int day,int h,int m, boolean isAppointment){
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
        String[] months_little = { "4", "6", "9", "11" };

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int current_year = calendar.get(Calendar.YEAR);
       /* int current_month = calendar.get(Calendar.MONTH);
        int current_day = calendar.get(Calendar.DAY_OF_MONTH);
        int current_hours = calendar.get(Calendar.HOUR_OF_DAY);
        int current_minute = calendar.get(Calendar.MINUTE);*/
        String[] values  = new String[3];

        if(!isAppointment) {
            for(int i= current_year-2; i<=current_year;i++){
                values[i-(current_year-2)] = i+"";
            }
            yearPickerView.setDisplayedValues(values);
            setData(yearPickerView, current_year - 2, current_year, year);
        }else{
            for(int i= current_year; i<=(current_year + 2);i++){
                values[i-(current_year)] = i+"";
            }
            yearPickerView.setDisplayedValues(values);
            setData(yearPickerView, current_year, current_year + 2, year);
        }
        yearPickerView.setHintText(context.getString(R.string.compact_year));

        monthPickerView.setDisplayedValues(context.getResources().getStringArray(R.array.month_display));

       /* if(current_year > year){
            current_month = 12;
        }*/

        setData(monthPickerView, 1, 12, month + 1);
        monthPickerView.setHintText(context.getString(R.string.compact_month));

        dayPickerView.setDisplayedValues(context.getResources().getStringArray(R.array.day_display));


        if (list_big.contains(String.valueOf(month+1))) {
            setData(dayPickerView, 1, 31, day);
        } else if (list_little.contains(String.valueOf(month+1))) {
            setData(dayPickerView, 1, 30, day);
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
            setData(dayPickerView,1,29,day);
            else
            setData(dayPickerView, 1, 28, day);
        }
        dayPickerView.setHintText(context.getString(R.string.compact_day));

        hourPickerView.setDisplayedValues(context.getResources().getStringArray(R.array.hour_display));

        setData(hourPickerView, 0, 23, h);
        hourPickerView.setHintText(context.getString(R.string.compact_hour));

        minPickerView.setDisplayedValues(context.getResources().getStringArray(R.array.minute_display));

        setData(minPickerView, 0, 59, m);
        minPickerView.setHintText(context.getString(R.string.compact_min));

        yearPickerView.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {

                int year_num = newVal;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big
                        .contains(String.valueOf(monthPickerView.getContentByCurrValue()))) {
                    setData(dayPickerView, 1, 31, Integer.parseInt(dayPickerView.getContentByCurrValue()));
                } else if (list_little.contains(String.valueOf(monthPickerView.getContentByCurrValue()))) {
                    setData(dayPickerView, 1, 30, Integer.parseInt(dayPickerView.getContentByCurrValue()));
                } else {
                    if ((year_num % 4 == 0 && year_num % 100 != 0)
                            || year_num % 400 == 0) {
                        setData(dayPickerView, 1, 29, Integer.parseInt(dayPickerView.getContentByCurrValue()) > 29 ? 29 : Integer.parseInt(dayPickerView.getContentByCurrValue()));
                    } else {
                        setData(dayPickerView, 1, 28, Integer.parseInt(dayPickerView.getContentByCurrValue()) > 28 ? 28 : Integer.parseInt(dayPickerView.getContentByCurrValue()));
                    }
                }
            }
        });

        monthPickerView.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                int month_num = newVal;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(month_num))) {
                    setData(dayPickerView, 1, 31, Integer.parseInt(dayPickerView.getContentByCurrValue()));
                } else if (list_little.contains(String.valueOf(month_num))) {
                    setData(dayPickerView, 1, 30, Integer.parseInt(dayPickerView.getContentByCurrValue()));
                } else {
                    if ((Integer.parseInt(yearPickerView.getContentByCurrValue()) % 4 == 0 && (Integer.parseInt(yearPickerView.getContentByCurrValue())) % 100 != 0)
                            || (Integer.parseInt(yearPickerView.getContentByCurrValue())) % 400 == 0) {
                        setData(dayPickerView, 1, 29, Integer.parseInt(dayPickerView.getContentByCurrValue()) > 29 ? 29 : Integer.parseInt(dayPickerView.getContentByCurrValue()));
                    } else {
                        setData(dayPickerView, 1, 28, Integer.parseInt(dayPickerView.getContentByCurrValue()) > 28 ? 28 : Integer.parseInt(dayPickerView.getContentByCurrValue()));
                    }
                }

            }
        });

    }

    public String getTime() {
        StringBuffer sb = new StringBuffer();
        sb.append(yearPickerView.getContentByCurrValue()).append("-")
                .append(monthPickerView.getContentByCurrValue()).append("-")
                .append(dayPickerView.getContentByCurrValue()).append(" ")
                .append(hourPickerView.getContentByCurrValue()).append(":")
                .append(minPickerView.getContentByCurrValue());
        return sb.toString();
    }


}
