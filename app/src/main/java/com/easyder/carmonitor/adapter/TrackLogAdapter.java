package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.bean.TrackLogItem;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-06-19.
 */
public class TrackLogAdapter extends BaseAdapter{

    private List<TrackLogItem> data = new ArrayList<>();

    private Context context;

    public TrackLogAdapter(List<TrackLogItem> data, Context context) {

        this.data.clear();
        this.data.addAll(data);
        this.context = context;
    }

    public void updata(List<TrackLogItem> data){

        this.data.clear();
        this.data.addAll(data);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        if(data.size()>position){
            return data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder = null;

        TrackLogItem trackLogItem = data.get(position);
        if(trackLogItem.isSplitLine()){

            TextView mTextView = new TextView(context);

            String locationTime = trackLogItem.getLocationTime();
            String[] split = locationTime.split(" ");
            if(split!= null && split.length>0) {
                mTextView.setText(split[0]);
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
                mTextView.setTextColor(Color.parseColor("#FF919191"));
                mTextView.setTypeface(Typeface.DEFAULT_BOLD);
                mTextView.setGravity(Gravity.CENTER);
            }
            convertView = mTextView;
            convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(29)));
            convertView.setBackgroundResource(R.color.track_log_content_bg);
        }else{

            if(convertView == null) {
                 mViewHolder = new ViewHolder();
                convertView = initViewHolder(mViewHolder);
            }else{
                mViewHolder = (ViewHolder) convertView.getTag();
                if(mViewHolder == null){
                    mViewHolder = new ViewHolder();
                    convertView = initViewHolder(mViewHolder);
                }
            }
            byte b = trackLogItem.getmGNSSSpeed();
            //显示速度
            mViewHolder.track_log_speed.setText(b+"km/h");
            //设置状态
            if(!trackLogItem.isStop()){
                mViewHolder.track_acc_status.setImageResource(R.mipmap.icon_run);
                mViewHolder.track_log_speed.setTextColor(context.getResources().getColor(R.color.track_log_item_speed_color));
                //隐藏停留时间
                mViewHolder.track_log_stoptime.setVisibility(View.GONE);

            }else{
                mViewHolder.track_acc_status.setImageResource(R.mipmap.icon_stop);
                mViewHolder.track_log_speed.setTextColor(context.getResources().getColor(R.color.track_log_item_stoptime_color));
                //显示停留时间
                mViewHolder.track_log_stoptime.setVisibility(View.VISIBLE);
                mViewHolder.track_log_stoptime.setText(context.getResources().getString(R.string.track_log_stoptime) + trackLogItem.getStopTime());
            }

            if((data.size()-1) >= position+1){
                TrackLogItem trackLogItem1 = data.get(position + 1);
                if(trackLogItem1.isSplitLine()){
                    mViewHolder.track_log_line.setVisibility(View.GONE);
//                    System.out.println("track_log_line gone   time :"+ trackLogItem.getLocationTime());
                }else{
                    mViewHolder.track_log_line.setVisibility(View.VISIBLE);
//                    System.out.println("track_log_line visible   time :"+ trackLogItem.getLocationTime());
                }
            }else{
                mViewHolder.track_log_line.setVisibility(View.GONE);
//                System.out.println("track_log_line gone   time :"+ trackLogItem.getLocationTime());
            }


            //设置地址
            String locationTime = trackLogItem.getLocationTime();
            String[] split = locationTime.split(" ");
            if(split!= null && split.length>1) {
                mViewHolder.track_log_time.setText(split[1]);
            }else{
                mViewHolder.track_log_time.setText(locationTime);
            }

            if(TextUtils.isEmpty(trackLogItem.getAdress())){
                mViewHolder.track_log_addres.setText("");
            }else{
                mViewHolder.track_log_addres.setText(trackLogItem.getAdress());
            }
        }
        return convertView;
    }

    private View initViewHolder(ViewHolder mViewHolder){
        View convertView;
        convertView = View.inflate(context, R.layout.track_log_item, null);
        mViewHolder.track_log_speed = (TextView) convertView.findViewById(R.id.track_log_speed);
        mViewHolder.track_log_time = (TextView) convertView.findViewById(R.id.track_log_time);
        mViewHolder.track_acc_status = (ImageView) convertView.findViewById(R.id.track_acc_status);
        mViewHolder.track_log_addres = (TextView) convertView.findViewById(R.id.track_log_addres);
        mViewHolder.track_log_line = convertView.findViewById(R.id.track_log_line);
        mViewHolder.track_log_stoptime = (TextView) convertView.findViewById(R.id.track_log_stoptime);
        convertView.setTag(mViewHolder);
        return convertView;
    }

    public class ViewHolder{
        public TextView track_log_speed;
        public TextView track_log_time;
        public TextView track_log_addres;
        public TextView track_log_stoptime;
        public ImageView track_acc_status;
        public View track_log_line;
    }
}
