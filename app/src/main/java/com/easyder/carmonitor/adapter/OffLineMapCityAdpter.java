package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.DownloadMapUtils;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.offlineMap.OffLineBaiduMapUtils;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.SizeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ljn on 2016/11/11.
 */
public class OffLineMapCityAdpter extends BaseExpandableListAdapter {

    /**
     * 所有城市 下载完成
     */
    private final int ALL_CITY_DOWNED = 1;

    /**
     * 所有城市 正在下载
     */
    private final int ALL_CITY_DOWNING = 2;

    /**
     * 所有城市什么都没做
     */
    private final int ALL_CITY_NOTODO = 3;

    private List<MKOLSearchRecord> mGroupData = new ArrayList<>();

    private Context mContext;

    private OffLineBaiduMapUtils mOffLineBaiduMapUtils= OffLineBaiduMapUtils.getInstance();

    private boolean isDebug = false && LogUtils.isDebug;

    private final boolean isUserCity = true;

    private Map<Integer,OffLineMapStatusClickListener> statusClickListeners = new LinkedHashMap<>();

    public OffLineMapCityAdpter(Context context) {
        mContext = context;
    }

    public void setData(List<MKOLSearchRecord> data){

        mGroupData.clear();
        if(data != null && data.size()>0){
            mGroupData.addAll(data);
            notifyDataSetChanged();
        }

    }

    @Override
    public int getGroupCount() {
        return mGroupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if((mGroupData.size()-1)<groupPosition){
            if(isDebug) LogUtils.debug("getGroup fail : groupPosition = " + groupPosition + ";  GroupData Max Position : " + (mGroupData.size() - 1));
            return 0;
        }

        MKOLSearchRecord mkolSearchRecord = mGroupData.get(groupPosition);

        if(mkolSearchRecord==null){
            if(isDebug) LogUtils.debug("getChildrenCount fail : mkolSearchRecord is null ");
            return 0;
        }

        ArrayList<MKOLSearchRecord> childCities = mkolSearchRecord.childCities;
        if(childCities!=null && childCities.size()>0){
            return childCities.size();
        }

        if(isDebug) LogUtils.debug("childCities is null or childCities size is 0");

        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if((mGroupData.size()-1)<groupPosition){
            if(isDebug) LogUtils.debug("getGroup fail : groupPosition = " + groupPosition + ";  GroupData Max Position : " + (mGroupData.size() - 1));
            return null;
        }

        return mGroupData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        if((mGroupData.size()-1)<groupPosition){
            if(isDebug) LogUtils.debug("getChild fail : groupPosition = " + groupPosition + ";  GroupData Max Position : " + (mGroupData.size() - 1));
            return null;
        }

        MKOLSearchRecord mkolSearchRecord = mGroupData.get(groupPosition);

        if(mkolSearchRecord==null){
            if(isDebug) LogUtils.debug("getChild fail : mkolSearchRecord is null ");
            return null;
        }

        ArrayList<MKOLSearchRecord> childCities = mkolSearchRecord.childCities;
        if(childCities!=null && childCities.size()>0){

            if((childCities.size()-1)<childPosition){
                if(isDebug) LogUtils.debug("getChild fail : childPosition = " + childPosition + ";  childCities Max Position : " + (childCities.size() - 1));
                return null;
            }

            return childCities.get(childPosition);
        }
        if(isDebug) LogUtils.debug("childCities is null or childCities size is 0");
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        GroupViewHolder mGroupViewHolder;
        if(convertView==null){
            convertView = View.inflate(mContext, R.layout.offlinemap_item_group, null);
            mGroupViewHolder = new GroupViewHolder();
            mGroupViewHolder.cityName = (TextView) convertView.findViewById(R.id.group_city_name);
            mGroupViewHolder.group_download_status_text = (TextView) convertView.findViewById(R.id.group_download_status_text);
            mGroupViewHolder.group_download_status = (ImageView) convertView.findViewById(R.id.group_download_status);
            convertView.setTag(mGroupViewHolder);
        }else{
            mGroupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        final MKOLSearchRecord group = (MKOLSearchRecord) getGroup(groupPosition);
//        ArrayList<MKOLSearchRecord> childCities = group.childCities;



        //判断是否是省，包含子城市的，是一省份显示
//        cityType  城市类型0:全国；1：省份；2：城市,如果是省份，可以通过childCities得到子城市列表
//        if(childCities!=null && childCities.size()>0){
        if(group.cityType == 1){

            mGroupViewHolder.group_download_status.setOnClickListener(null);
            mGroupViewHolder.group_download_status.setClickable(false);
            mGroupViewHolder.group_download_status_text.setVisibility(View.GONE);

            if(isUserCity) {
                mGroupViewHolder.group_download_status.setVisibility(View.VISIBLE);
                if(isExpanded){
                    mGroupViewHolder.group_download_status.setImageResource(R.mipmap.circle_arrow_up);
                }else{
                    mGroupViewHolder.group_download_status.setImageResource(R.mipmap.circle_arrow_down);
                }
            }else{
                mGroupViewHolder.group_download_status.setVisibility(View.VISIBLE);
                // 移除点击事件
                OffLineMapStatusClickListener offLineMapStatusClickListener = statusClickListeners.get(group.cityID);
                if(offLineMapStatusClickListener!=null && offLineMapStatusClickListener.needdestroy){
                    statusClickListeners.remove(group.cityID);
                }else if(offLineMapStatusClickListener!=null){
                    offLineMapStatusClickListener.setContentView(convertView,true);
                    statusClickListeners.put(group.cityID, offLineMapStatusClickListener);
                }
            }

        }else{
            mGroupViewHolder.group_download_status.setVisibility(View.VISIBLE);

            MKOLUpdateElement updateInfo = mOffLineBaiduMapUtils.getUpdateInfo(group.cityID);
            //判断是否是未下载状态，updateInfo == null 就是未下载
            if(updateInfo==null){


                mGroupViewHolder.group_download_status.setImageResource(R.mipmap.btn_download);
                mGroupViewHolder.group_download_status_text.setVisibility(View.VISIBLE);
                mGroupViewHolder.group_download_status_text.setText(SizeUtils.formatDataSize(group.size));
                mGroupViewHolder.group_download_status_text.setTextColor(Color.parseColor("#8C8C8C"));

                //设置点击事件
                OffLineMapStatusClickListener offLineMapStatusClickListener = statusClickListeners.get(group.cityID);
                if(offLineMapStatusClickListener==null){
                    offLineMapStatusClickListener = new OffLineMapStatusClickListener(group.cityID,this,convertView,groupPosition);
                    statusClickListeners.put(group.cityID, offLineMapStatusClickListener);
                }else{
                    offLineMapStatusClickListener.setContentView(convertView,true);
                    statusClickListeners.put(group.cityID, offLineMapStatusClickListener);
                }
                mGroupViewHolder.group_download_status.setClickable(true);
                mGroupViewHolder.group_download_status.setOnClickListener(offLineMapStatusClickListener);
            }else{
                mGroupViewHolder.group_download_status.setClickable(false);
                mGroupViewHolder.group_download_status_text.setVisibility(View.VISIBLE);
                mGroupViewHolder.group_download_status_text.setText(DownloadMapUtils.getDownLoadStaus(updateInfo.status) + "  " + SizeUtils.formatDataSize(group.size));
                if(MKOLUpdateElement.FINISHED == updateInfo.status  || updateInfo.ratio == 100 ){
                    mGroupViewHolder.group_download_status_text.setTextColor(Color.parseColor("#B3B3B3"));
                    mGroupViewHolder.group_download_status.setImageResource(R.mipmap.btn_downloaded);
                }else{
                    mGroupViewHolder.group_download_status_text.setTextColor(Color.parseColor("#E51C23"));
                    mGroupViewHolder.group_download_status.setImageResource(R.mipmap.btn_download);
                }
                mGroupViewHolder.group_download_status.setOnClickListener(null);

                // 移除点击事件
                OffLineMapStatusClickListener offLineMapStatusClickListener = statusClickListeners.get(group.cityID);

                if(updateInfo.status == MKOLUpdateElement.FINISHED || updateInfo.ratio == 100 ){
                    if(offLineMapStatusClickListener != null) {
                        statusClickListeners.remove(group.cityID);
                    }
                }else{

                    if(offLineMapStatusClickListener!=null){
                        offLineMapStatusClickListener.setContentView(convertView, true);
                    }else if(offLineMapStatusClickListener==null){
                        offLineMapStatusClickListener = new OffLineMapStatusClickListener(group.cityID,this,convertView,groupPosition);
                    }

                    statusClickListeners.put(group.cityID, offLineMapStatusClickListener);
                    offLineMapStatusClickListener.registStatusListener();
                }

            }

        }

        if(group!=null){
            mGroupViewHolder.cityName.setText(group.cityName);
            if(isDebug)System.out.println(group.cityName + " = "+group.cityID);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildViewHolder mChildViewHolder;
        if(convertView==null){
            convertView = View.inflate(mContext, R.layout.offlinemap_item_child, null);
            mChildViewHolder = new ChildViewHolder();
            mChildViewHolder.cityName = (TextView) convertView.findViewById(R.id.child_city_name);
            mChildViewHolder.download_status_text = (TextView) convertView.findViewById(R.id.child_download_status_text);
            mChildViewHolder.child_download_status = (ImageView) convertView.findViewById(R.id.child_download_status);
            convertView.setTag(mChildViewHolder);
        }else{
            mChildViewHolder = (ChildViewHolder) convertView.getTag();
        }

        final MKOLSearchRecord child = (MKOLSearchRecord) getChild(groupPosition, childPosition);

        if(child!=null){
            mChildViewHolder.cityName.setText(child.cityName);
            if(isDebug)System.out.println("<item>"+child.cityName+"</item>");

            MKOLUpdateElement updateInfo = mOffLineBaiduMapUtils.getUpdateInfo(child.cityID);

            if(updateInfo!=null){

                    mChildViewHolder.download_status_text.setVisibility(View.VISIBLE);
                    mChildViewHolder.download_status_text.setText(DownloadMapUtils.getDownLoadStaus(updateInfo.status) + "  " + SizeUtils.formatDataSize(child.size));
                    if(MKOLUpdateElement.FINISHED == updateInfo.status  || updateInfo.ratio == 100 ){
                        mChildViewHolder.download_status_text.setTextColor(Color.parseColor("#B3B3B3"));
                        mChildViewHolder.child_download_status.setImageResource(R.mipmap.btn_downloaded);
                    }else{
                        mChildViewHolder.download_status_text.setTextColor(Color.parseColor("#E51C23"));
                        mChildViewHolder.child_download_status.setImageResource(R.mipmap.btn_download);
                    }
                    mChildViewHolder.child_download_status.setOnClickListener(null);

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
                            offLineMapStatusClickListener = new OffLineMapStatusClickListener(child.cityID,this,convertView,groupPosition);
                        }

                        statusClickListeners.put(child.cityID, offLineMapStatusClickListener);
                        offLineMapStatusClickListener.registStatusListener();
                    }

            }else{

                if(child.cityID >=5 && child.cityID <= 31){

                        switch (checkAllCityStatus(groupPosition)){
                            case ALL_CITY_DOWNED:
                                mChildViewHolder.child_download_status.setImageResource(R.mipmap.btn_downloaded);
                                mChildViewHolder.download_status_text.setVisibility(View.VISIBLE);
                                mChildViewHolder.download_status_text.setText(DownloadMapUtils.getDownLoadStaus(MKOLUpdateElement.FINISHED) +"  "+ SizeUtils.formatDataSize(child.size));
                                mChildViewHolder.download_status_text.setTextColor(Color.parseColor("#B3B3B3"));
                            break;
                            case ALL_CITY_DOWNING:
                                mChildViewHolder.child_download_status.setImageResource(R.mipmap.btn_download);
                                mChildViewHolder.download_status_text.setVisibility(View.VISIBLE);
                                mChildViewHolder.download_status_text.setText(DownloadMapUtils.getDownLoadStaus(MKOLUpdateElement.DOWNLOADING) + "  " +SizeUtils.formatDataSize(child.size));
                                mChildViewHolder.download_status_text.setTextColor(Color.parseColor("#E51C23"));
                                break;
                            default:
                                mChildViewHolder.child_download_status.setImageResource(R.mipmap.btn_download);
                                mChildViewHolder.download_status_text.setVisibility(View.VISIBLE);
                                mChildViewHolder.download_status_text.setText(SizeUtils.formatDataSize(child.size));
                                mChildViewHolder.download_status_text.setTextColor(Color.parseColor("#8C8C8C"));
                                break;
                        }

                }else {
                    mChildViewHolder.download_status_text.setVisibility(View.VISIBLE);
                    mChildViewHolder.download_status_text.setText(SizeUtils.formatDataSize(child.size));
                    mChildViewHolder.download_status_text.setTextColor(Color.parseColor("#8C8C8C"));
                    mChildViewHolder.child_download_status.setImageResource(R.mipmap.btn_download);
                }

                //设置点击事件
                OffLineMapStatusClickListener offLineMapStatusClickListener = statusClickListeners.get(child.cityID);
                if(offLineMapStatusClickListener==null){
                    offLineMapStatusClickListener = new OffLineMapStatusClickListener(child.cityID, this, convertView, groupPosition);
                    statusClickListeners.put(child.cityID,offLineMapStatusClickListener);
                }else{
                    offLineMapStatusClickListener.setContentView(convertView, false);
                    statusClickListeners.put(child.cityID, offLineMapStatusClickListener);
                }

                mChildViewHolder.child_download_status.setOnClickListener(offLineMapStatusClickListener);
            }
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public Map<Integer,OffLineMapStatusClickListener> getStatusListeners(){
        return statusClickListeners;
    }

    /**
     *
     * @return true is downloaded
     */
    public int checkAllCityStatus(int groupPosition){

        MKOLSearchRecord mkolSearchRecord = mGroupData.get(groupPosition);
        ArrayList<MKOLSearchRecord> childCities = mkolSearchRecord.childCities;
        if(childCities != null && childCities.size()>0){
            int downingCount = 0;
            int downedCount = 0;
            for (MKOLSearchRecord record :childCities){
                if(record.cityID != mkolSearchRecord.cityID){
                    MKOLUpdateElement updateInfo = mOffLineBaiduMapUtils.getUpdateInfo(record.cityID);

                    if(updateInfo == null){
                        return ALL_CITY_NOTODO;
                    }

                    if((MKOLUpdateElement.DOWNLOADING == updateInfo.status || MKOLUpdateElement.WAITING == updateInfo.status) && updateInfo.ratio != 100){
                        downingCount++;
                    }else if(MKOLUpdateElement.FINISHED == updateInfo.status || updateInfo.ratio == 100){
                        downedCount++;
                    }else{
                        return ALL_CITY_NOTODO;
                    }
                }
            }

            if(downedCount == (childCities.size()-1)){
                return ALL_CITY_DOWNED;
            }else if((downedCount + downingCount) == (childCities.size()-1)){
                return ALL_CITY_DOWNING;
            }
        }

        return ALL_CITY_NOTODO;
    }

    public static class GroupViewHolder{
        public TextView cityName;
        public ImageView group_download_status;
        public TextView group_download_status_text;
    }

    public static class ChildViewHolder{
        TextView cityName;
        ImageView child_download_status;
        TextView download_status_text;
    }

}
