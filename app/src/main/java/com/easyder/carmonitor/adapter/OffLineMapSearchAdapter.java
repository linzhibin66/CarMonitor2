package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.DownloadMapUtils;
import com.shinetech.mvp.offlineMap.OffLineBaiduMapUtils;
import com.shinetech.mvp.utils.SizeUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ljn on 2017-07-10.
 */
public class OffLineMapSearchAdapter extends BaseAdapter{

    private List<MKOLSearchRecord> mData = new ArrayList<>();

    private Context context;

    private OffLineBaiduMapUtils mOffLineBaiduMapUtils;

    private Map<Integer,OffLineMapStatusClickListener> statusClickListeners = new LinkedHashMap<>();

    public OffLineMapSearchAdapter(List<MKOLSearchRecord> mData, Context context) {
        this.mData.clear();
        this.mData.addAll(mData);
        this.context = context;
        mOffLineBaiduMapUtils = OffLineBaiduMapUtils.getInstance();
    }

    public void update(List<MKOLSearchRecord> list){
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {

        if(mData.size()>position) {
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


        OffLineMapCityAdpter.GroupViewHolder mGroupViewHolder;
        if(convertView==null){
            convertView = View.inflate(context, R.layout.offlinemap_item_group, null);
            mGroupViewHolder = new OffLineMapCityAdpter.GroupViewHolder();
            mGroupViewHolder.cityName = (TextView) convertView.findViewById(R.id.group_city_name);
            mGroupViewHolder.group_download_status_text = (TextView) convertView.findViewById(R.id.group_download_status_text);
            mGroupViewHolder.group_download_status = (ImageView) convertView.findViewById(R.id.group_download_status);
            convertView.setTag(mGroupViewHolder);
        }else{
            mGroupViewHolder = (OffLineMapCityAdpter.GroupViewHolder) convertView.getTag();
        }

        final MKOLSearchRecord child = mData.get(position);

        if(child!=null){
            mGroupViewHolder.cityName.setText(child.cityName);
//            System.out.println("<item>"+child.cityName+"</item>");

            MKOLUpdateElement updateInfo = mOffLineBaiduMapUtils.getUpdateInfo(child.cityID);

            if(updateInfo!=null){

                mGroupViewHolder.group_download_status_text.setVisibility(View.VISIBLE);
                mGroupViewHolder.group_download_status_text.setText(DownloadMapUtils.getDownLoadStaus(updateInfo.status) + "  " + SizeUtils.formatDataSize(child.size));
                if(MKOLUpdateElement.FINISHED == updateInfo.status  || updateInfo.ratio == 100 ){
                    mGroupViewHolder.group_download_status_text.setTextColor(Color.parseColor("#B3B3B3"));
                    mGroupViewHolder.group_download_status.setImageResource(R.mipmap.btn_downloaded);
                }else{
                    mGroupViewHolder.group_download_status_text.setTextColor(Color.parseColor("#E51C23"));
                    mGroupViewHolder.group_download_status.setImageResource(R.mipmap.btn_download);
                }
                mGroupViewHolder.group_download_status.setOnClickListener(null);

                // 移除点击事件
                OffLineMapStatusClickListener offLineMapStatusClickListener = statusClickListeners.get(child.cityID);

                if(updateInfo.status == MKOLUpdateElement.FINISHED || updateInfo.ratio == 100 ){
                    if(offLineMapStatusClickListener != null) {
                        statusClickListeners.remove(child.cityID);
                    }
                }else{

                    if(offLineMapStatusClickListener!=null){
                        offLineMapStatusClickListener.setContentView(convertView, true);
                    }else if(offLineMapStatusClickListener==null){
                        offLineMapStatusClickListener = new OffLineMapStatusClickListener(child.cityID,null,convertView,child.cityID);
                    }

                    statusClickListeners.put(child.cityID, offLineMapStatusClickListener);
                    offLineMapStatusClickListener.registStatusListener();
                }

            }else{
                mGroupViewHolder.group_download_status_text.setVisibility(View.VISIBLE);
                mGroupViewHolder.group_download_status_text.setText(SizeUtils.formatDataSize(child.size));
                mGroupViewHolder.group_download_status_text.setTextColor(Color.parseColor("#8C8C8C"));
                mGroupViewHolder.group_download_status.setImageResource(R.mipmap.btn_download);

                //设置点击事件
                OffLineMapStatusClickListener offLineMapStatusClickListener = statusClickListeners.get(child.cityID);
                if(offLineMapStatusClickListener==null){
                    offLineMapStatusClickListener = new OffLineMapStatusClickListener(child.cityID, null, convertView, child.cityID);
                    statusClickListeners.put(child.cityID,offLineMapStatusClickListener);
                }else{
                    offLineMapStatusClickListener.setContentView(convertView, false);
                    statusClickListeners.put(child.cityID, offLineMapStatusClickListener);
                }

                mGroupViewHolder.group_download_status.setOnClickListener(offLineMapStatusClickListener);
            }
        }


        return convertView;
    }
}
