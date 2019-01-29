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
import com.shinetech.mvp.network.UDP.resopnsebean.ViolationLogItem;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-06-19.
 */
public class AlarmLogAdapter extends BaseAdapter{

    private List<ViolationLogItem> data = new ArrayList<>();

    private Context context;

    public AlarmLogAdapter(List<ViolationLogItem> data, Context context) {

        this.data.clear();
        this.data.addAll(data);
        this.context = context;
    }

    public void updata(List<ViolationLogItem> data){

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

        ViolationLogItem mViolationLogItem = data.get(position);
        if(TextUtils.isEmpty(mViolationLogItem.getPlateNumber())){
            TextView textView = new TextView(context);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
            textView.setTextColor(Color.parseColor("#FF919191"));
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setGravity(Gravity.CENTER);
            textView.setText(mViolationLogItem.getViolationTime());
            convertView = textView;
            convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(29)));
            convertView.setBackgroundResource(R.color.track_log_content_bg);
        }else {

            if (convertView == null) {
                mViewHolder = new ViewHolder();
                convertView = initViewHolder(mViewHolder);
            } else {
                Object tag = convertView.getTag();

                if (tag == null) {
                    mViewHolder = new ViewHolder();
                    convertView = initViewHolder(mViewHolder);
                } else {
                    mViewHolder = (ViewHolder) tag;
                }
            }

            String violationName = mViolationLogItem.getViolationName();

            mViewHolder.alarm_log_name.setText(violationName);

            String violationTime = mViolationLogItem.getViolationTime();

            String time = getTime(violationTime);

            mViewHolder.alarm_log_time.setText(time);

            if(TextUtils.isEmpty(mViolationLogItem.getAdress())){
                mViewHolder.alarm_log_addres.setText("");
            }else{
                mViewHolder.alarm_log_addres.setText(mViolationLogItem.getAdress());
            }

            //判断是否是日期分类的最后一条信息
            Object item = getItem(position + 1);

            if(item != null){
                ViolationLogItem nextItem = (ViolationLogItem) item;

                if(TextUtils.isEmpty(nextItem.getPlateNumber())){
                    mViewHolder.alarm_log_line.setVisibility(View.GONE);
                }else{
                    mViewHolder.alarm_log_line.setVisibility(View.VISIBLE);
                }
            }

        }

        return convertView;
    }

    private String getTime(String violationTime){

        String[] split = violationTime.split(" ");

        if(split != null && split.length>1){
            return split[1];
        }

        return violationTime;



    }

    private View initViewHolder(ViewHolder mViewHolder){
        View convertView;
        convertView = View.inflate(context, R.layout.alarm_log_item, null);
        mViewHolder.alarm_log_name = (TextView) convertView.findViewById(R.id.alarm_log_name);
        mViewHolder.alarm_log_time = (TextView) convertView.findViewById(R.id.alarm_log_time);
        mViewHolder.alarm_status = (ImageView) convertView.findViewById(R.id.alarm_status);
        mViewHolder.alarm_log_addres = (TextView) convertView.findViewById(R.id.alarm_log_addres);
        mViewHolder.alarm_log_line = convertView.findViewById(R.id.alarm_log_line);
        convertView.setTag(mViewHolder);
        return convertView;
    }

    public class ViewHolder{
        public TextView alarm_log_name;
        public TextView alarm_log_time;
        public TextView alarm_log_addres;
        public ImageView alarm_status;
        public View alarm_log_line;
    }
}
