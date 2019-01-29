package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.widget.swipemenulistview.BaseSwipListAdapter;
import com.shinetech.mvp.DB.bean.PushMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-05-18.
 */
public class PushMessageAdapter extends BaseSwipListAdapter {

    private List<PushMessage> mData = new ArrayList<>();

    private Context context;

    public PushMessageAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<PushMessage> data){
        mData.clear();
        if(data!= null){
            mData.addAll(data);
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        if(mData.size()>position){
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;

        if(convertView != null){
            mViewHolder = (ViewHolder) convertView.getTag();
        }else{
            mViewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.push_message_item, null);
            mViewHolder.message_time = (TextView) convertView.findViewById(R.id.message_send_time);
            mViewHolder.message_type = (TextView) convertView.findViewById(R.id.message_type);
            mViewHolder.message_content = (TextView) convertView.findViewById(R.id.message_content);
            convertView.setTag(mViewHolder);

        }

        PushMessage pushMessage = mData.get(position);

        mViewHolder.message_time.setText(pushMessage.getSendTime());
        mViewHolder.message_type.setText(pushMessage.getMessageType());
        mViewHolder.message_content.setText(pushMessage.getMessage());

        return convertView;
    }

    class ViewHolder{
        TextView message_time;
        TextView message_type;
        TextView message_content;
    }
}
