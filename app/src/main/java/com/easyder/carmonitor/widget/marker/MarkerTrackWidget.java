package com.easyder.carmonitor.widget.marker;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.bigkoo.pickerview.TimePickerView;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.dialog.MyTimePickerDialog;
import com.easyder.carmonitor.interfaces.CompactMarkerListener;
import com.easyder.carmonitor.widget.MyTimePickerView;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ljn on 2017-04-14.
 */
public class MarkerTrackWidget implements View.OnClickListener {

    private Context context;

    private CarInfoBean mCarInfoBean;

    private View rootView;

    private  TextView compact_start_time;

    private  TextView compact_end_time;

    private  Button compact_commit;

    private MapView bundView;

    private MyTimePickerDialog myTimePickerDialog;

    private CompactMarkerListener mListener;

    private final long ONE_HOUR = 60*60*1000;

    private Date startDate;

    private Date endDate;

    public MarkerTrackWidget(Context context, CarInfoBean mCarInfoBean, MapView bundView) {
        this.context = context;
        this.mCarInfoBean = mCarInfoBean;
        this.bundView = bundView;

        initView();

    }

    public void updateInfo(CarInfoBean mCarInfoBean){
        this.mCarInfoBean = mCarInfoBean;
        initTimne();
    }

    public  View getView(){
        return rootView;
    }

    private void initView(){

        rootView = View.inflate(context, R.layout.marker_widget_alarm_layout, null);

        compact_start_time = (TextView) rootView.findViewById(R.id.compact_start_time);
        compact_start_time.setOnClickListener(this);

        compact_end_time = (TextView) rootView.findViewById(R.id.compact_end_time);
        compact_end_time.setOnClickListener(this);

        View line_2 = rootView.findViewById(R.id.line_2);
        line_2.setVisibility(View.GONE);

        initTimne();

        compact_commit = (Button) rootView.findViewById(R.id.compact_commit);
        compact_commit.setText(context.getString(R.string.track_title));
        compact_commit.setOnClickListener(this);

    }

    /**
     * 初始化时间
     */
    private void initTimne(){

        //初始化开始时间
        long currentTimeMillis = System.currentTimeMillis();
        startDate = new Date(currentTimeMillis-ONE_HOUR);
        String startTime = getTiemString(startDate);
        compact_start_time.setText(startTime);

        //初始化结束时间
        endDate = new Date(currentTimeMillis);
        String endTime = getTiemString(endDate);
        compact_end_time.setText(endTime);

    }

    private String getTiemString(Date mDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(mDate);
    }

    public void setCommitListener(CompactMarkerListener mListener){
        this.mListener = mListener;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.compact_start_time:

                if(myTimePickerDialog == null) {
                    myTimePickerDialog = new MyTimePickerDialog(context, TimePickerView.Type.ALL);
                }
                    myTimePickerDialog.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date) {
                            startDate = date;
                            String startTime = getTiemString(startDate);
                            compact_start_time.setText(startTime);

                        }
                    });

                myTimePickerDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ConfigurationChangedManager.getInstance().unRegistConfig(myTimePickerDialog);
                    }
                });

                myTimePickerDialog.setTitle(context.getString(R.string.start_time));
                myTimePickerDialog.setTime(startDate);
                myTimePickerDialog.show(bundView);
                ConfigurationChangedManager.getInstance().registConfig(myTimePickerDialog);

                break;

            case R.id.compact_end_time:

                if(myTimePickerDialog == null) {
                    myTimePickerDialog = new MyTimePickerDialog(context, TimePickerView.Type.ALL);
                }
                myTimePickerDialog.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date) {
                        endDate = date;
                        String endTime = getTiemString(endDate);
                        compact_end_time.setText(endTime);
                        ConfigurationChangedManager.getInstance().unRegistConfig(myTimePickerDialog);
                    }
                });

                myTimePickerDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ConfigurationChangedManager.getInstance().unRegistConfig(myTimePickerDialog);
                    }
                });

                myTimePickerDialog.setTitle(context.getString(R.string.end_time));
                myTimePickerDialog.setTime(endDate);
                myTimePickerDialog.show(bundView);
                ConfigurationChangedManager.getInstance().registConfig(myTimePickerDialog);

                break;

            case R.id.compact_commit:
                if(mListener != null){
                    mListener.onTrackCommit(mCarInfoBean, compact_start_time.getText().toString(), compact_end_time.getText().toString());
                }

                break;
        }

    }

}
