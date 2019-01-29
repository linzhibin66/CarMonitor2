package com.easyder.carmonitor.widget;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.CarAlarmAdapter;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.shinetech.mvp.network.UDP.InfoTool.AlarmInfoTool;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-04-11.
 */
public class CarAlarmWidget{

    private Context context;

    private View view;

    private ListView contentLayout;

    private List<CarAlarmItem> items = new ArrayList<>();

    private LayoutBackListener listener;

    private  CarAlarmAdapter mCarAlarmAdapter;

    private RelativeLayout caralarm_layout_outmost;

    public CarAlarmWidget(Context context,LayoutBackListener listener) {
        this.context = context;
        this.listener = listener;
        view = View.inflate(context, R.layout.car_alarm_activity, null);
        contentLayout = (ListView) view.findViewById(R.id.content);
        caralarm_layout_outmost = (RelativeLayout) view.findViewById(R.id.caralarm_layout_outmost);
        initTitle(view);

    }

    public CarAlarmWidget(Context context,View rootView, LayoutBackListener listener) {
        this.context = context;
        this.listener = listener;
        view = rootView;
        contentLayout = (ListView) view.findViewById(R.id.content);
        caralarm_layout_outmost = (RelativeLayout) view.findViewById(R.id.caralarm_layout_outmost);
        initTitle(view);

    }

    public void setTouchOutmost(View.OnTouchListener listener){
        if(caralarm_layout_outmost != null) {
            caralarm_layout_outmost.setOnTouchListener(listener);
        }
    }

    private void initTitle(View rootView) {

        ImageButton title_back = (ImageButton) rootView.findViewById(R.id.title_back);
        TextView title_text = (TextView) rootView.findViewById(R.id.title_text);
        ImageButton title_search = (ImageButton) rootView.findViewById(R.id.title_search);

        title_text.setText(context.getString(R.string.carinfo_alarms));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

        title_search.setVisibility(View.GONE);

        RelativeLayout title_layout = (RelativeLayout) rootView.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }

    public View getView(){
        return view;
    }

    public void updateAlarm(CarInfoBean carinfoBean){
        int alarmType = carinfoBean.getAlarmType();
        final List<String> alarmList = AlarmInfoTool.getAlarmList(alarmType);

        items.clear();

        if(alarmList.size() == 0){
            contentLayout.setVisibility(View.GONE);
            return;
        }

        for(int i = 0; i<alarmList.size(); i++){
            CarAlarmItem carAlarmItem = new CarAlarmItem(CarAlarmItem.ALARM, alarmList.get(i));
            items.add(carAlarmItem);
        }

        if(mCarAlarmAdapter == null) {
            mCarAlarmAdapter = new CarAlarmAdapter(context, items);
            contentLayout.setAdapter(mCarAlarmAdapter);
        }else{
            mCarAlarmAdapter.UpData(items);
        }

    }
}
