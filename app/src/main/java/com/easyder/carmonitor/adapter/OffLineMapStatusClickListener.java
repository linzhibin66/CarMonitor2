package com.easyder.carmonitor.adapter;

import android.graphics.Color;
import android.view.View;

import com.easyder.carmonitor.R;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.interfaces.OffLineStatusListener;
import com.shinetech.mvp.offlineMap.OffLineBaiduMapUtils;
import com.shinetech.mvp.utils.LogUtils;

import java.util.Iterator;
import java.util.Map;


/**
 * Created by lin on 2016/11/15.
 */
public class OffLineMapStatusClickListener implements View.OnClickListener {

    /**
     *  当前数据的唯一标识（与ItemView不一定对应）
     */
    private int cityID;

    /**
     * 数据处理和监听注册对象
     */
    private OffLineBaiduMapUtils mOffLineBaiduMapUtils;

    /**
     * 该ItemView的适配器，用于刷新Item
     */
    private OffLineMapCityAdpter baseAdapter;

    /**
     * 下载状态监听器
     */
    private ItemStatusListener mItemStatusListener;

    /**
     * 清除缓存标记位，是否从缓存中清除ItemStatusListener监听
     */
    public boolean needdestroy = false;

    /**
     * Adapter ItemView
     */
    private View contentView;

    /**
     * 是否是顶层
     */
    private int mGoupID;

    private boolean isDebug = true && LogUtils.isDebug;

    public OffLineMapStatusClickListener(int cityID, OffLineMapCityAdpter baseAdapter, View contentView, int mGoupID) {
        this.cityID = cityID;
        this.baseAdapter = baseAdapter;
        mOffLineBaiduMapUtils = OffLineBaiduMapUtils.getInstance();
        this.contentView = contentView;
        this.mGoupID = mGoupID;
        mItemStatusListener = new ItemStatusListener(contentView, cityID, this);
    }

    /**
     * 从新绑定Item布局，由于ListView滑动后，Item会被复用，导致指定得Item布局与内容不对应，需从新绑定
     * @param contentView
     * @param isGoup
     */
    public void setContentView(View contentView,boolean isGoup){

        this.contentView = contentView;
        this.mGoupID = mGoupID;

        if(mItemStatusListener!=null){
            mItemStatusListener.setItem(contentView);
        }

    }

    public void setNeedDestroy(boolean needDestroy){
        this.needdestroy = needDestroy;
        if(baseAdapter != null) {
            baseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {

        //下载全国基础包
      /*  MKOLUpdateElement updateInfo1 = mOffLineBaiduMapUtils.getUpdateInfo(1);

        if(updateInfo1==null){
            mOffLineBaiduMapUtils.start(1);
            baseAdapter.notifyDataSetChanged();
        }*/
        //判断是否下载所有城市
        if(cityID >= 5 && cityID <= 31){

            if(baseAdapter == null){
                return;
            }

            Map<Integer, OffLineMapStatusClickListener> statusListeners = baseAdapter.getStatusListeners();
            Iterator<Map.Entry<Integer, OffLineMapStatusClickListener>> iterator = statusListeners.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<Integer, OffLineMapStatusClickListener> next = iterator.next();
                OffLineMapStatusClickListener listener = next.getValue();
                if(listener.cityID != cityID && !listener.needdestroy && listener.mGoupID == mGoupID){
                    listener.registStatusListener();
                }
            }
            mOffLineBaiduMapUtils.start(cityID);
            if(baseAdapter != null) {
                baseAdapter.notifyDataSetChanged();
            }
        }else {

            //注册下载状态监听
            if (isDebug) LogUtils.debug("OffLineMapStatusClickListener regist cityID" + cityID);
            mOffLineBaiduMapUtils.addStatusListener(cityID, mItemStatusListener);

            mOffLineBaiduMapUtils.start(cityID);
        }
    }

    public void registStatusListener(){
        mOffLineBaiduMapUtils.addStatusListener(cityID,mItemStatusListener);
    }

    class ItemStatusListener extends OffLineStatusListener {

        private View item;

        private OffLineBaiduMapUtils mOffLineBaiduMapUtils;

        private int cityID;

//        private boolean isGoup;

        private OffLineMapStatusClickListener mOffLineMapStatusClickListener;

        public ItemStatusListener(View item,int cityID/*,boolean isGoup*/,OffLineMapStatusClickListener mOffLineMapStatusClickListener) {
            this.cityID =cityID;
            this.mOffLineMapStatusClickListener = mOffLineMapStatusClickListener;
            mOffLineBaiduMapUtils = OffLineBaiduMapUtils.getInstance();
            setItem(item/*, isGoup*/);
        }

        /**
         * 从新绑定Item布局，由于ListView滑动后，Item会被复用，导致指定得Item布局与内容不对应，需从新绑定
         * @param item
         *
         */
        public void setItem(View item/*,boolean isGoup*/){
            this.item = item;
//            this.isGoup = isGoup;
        }

        @Override
        public void onStart() {
            if(isDebug)LogUtils.debug("onStart  cityID" + cityID);
//            if(isGoup){
            Object tag = item.getTag();
            if(tag instanceof OffLineMapCityAdpter.GroupViewHolder){
                OffLineMapCityAdpter.GroupViewHolder mGroupViewHolder = (OffLineMapCityAdpter.GroupViewHolder) tag;
                if(mGroupViewHolder.group_download_status_text.getVisibility() != View.VISIBLE) {
                    mGroupViewHolder.group_download_status_text.setVisibility(View.VISIBLE);
                }
                String status = mGroupViewHolder.group_download_status_text.getText().toString();
                String size = getSize(status);
                mGroupViewHolder.group_download_status_text.setText(MainApplication.getInstance().getString(R.string.downloading) + "  "+size);
                mGroupViewHolder.group_download_status_text.setTextColor(Color.parseColor("#E51C23"));

//                mGroupViewHolder.group_download_status.setImageResource(R.mipmap.btn_downloaded);
                mGroupViewHolder.group_download_status.setOnClickListener(null);
            }else{
                OffLineMapCityAdpter.ChildViewHolder mChildViewHolder = (OffLineMapCityAdpter.ChildViewHolder) tag;
                if(mChildViewHolder.download_status_text.getVisibility() != View.VISIBLE){
                    mChildViewHolder.download_status_text.setVisibility(View.VISIBLE);
                }
                String status = mChildViewHolder.download_status_text.getText().toString();
                String size = getSize(status);
                mChildViewHolder.download_status_text.setText(MainApplication.getInstance().getString(R.string.downloading) + "  "+size);
                mChildViewHolder.download_status_text.setTextColor(Color.parseColor("#E51C23"));

//                mChildViewHolder.child_download_status.setImageResource(R.mipmap.btn_downloaded);
                mChildViewHolder.child_download_status.setOnClickListener(null);
            }
        }

        @Override
        public boolean onFinish() {
            if(isDebug)LogUtils.debug("onFinish  cityID" + cityID);
            Object tag = item.getTag();
            if(tag instanceof OffLineMapCityAdpter.GroupViewHolder){
                OffLineMapCityAdpter.GroupViewHolder mGroupViewHolder = (OffLineMapCityAdpter.GroupViewHolder) tag;
                if(mGroupViewHolder.group_download_status_text.getVisibility() != View.VISIBLE) {
                    mGroupViewHolder.group_download_status_text.setVisibility(View.VISIBLE);
                }
                String status = mGroupViewHolder.group_download_status_text.getText().toString();
                String size = getSize(status);
                mGroupViewHolder.group_download_status_text.setText(MainApplication.getInstance().getString(R.string.downloaded) + "  "+size);
                mGroupViewHolder.group_download_status_text.setTextColor(Color.parseColor("#B3B3B3"));

                mGroupViewHolder.group_download_status.setImageResource(R.mipmap.btn_downloaded);

            }else{
                OffLineMapCityAdpter.ChildViewHolder mChildViewHolder = (OffLineMapCityAdpter.ChildViewHolder) tag;
                if(mChildViewHolder.download_status_text.getVisibility() != View.VISIBLE){
                    mChildViewHolder.download_status_text.setVisibility(View.VISIBLE);
                }
                String status = mChildViewHolder.download_status_text.getText().toString();
                String size = getSize(status);
                mChildViewHolder.download_status_text.setText(MainApplication.getInstance().getString(R.string.downloaded)+"  "+size);
                mChildViewHolder.download_status_text.setTextColor(Color.parseColor("#B3B3B3"));

                mChildViewHolder.child_download_status.setImageResource(R.mipmap.btn_downloaded);

            }

            //TODO OffLineMapStatusClickListener 对象的销毁标记
            mOffLineMapStatusClickListener.setNeedDestroy(true);

            //TODO 返回true 移除掉当前状态监听对象
            return true;
        }

        @Override
        public boolean onRemove() {
//            if(isGoup){
            Object tag = item.getTag();
            if(tag instanceof OffLineMapCityAdpter.GroupViewHolder){
                OffLineMapCityAdpter.GroupViewHolder mGroupViewHolder = (OffLineMapCityAdpter.GroupViewHolder) tag;
               /* if(mGroupViewHolder.group_download_status_text.getVisibility() == View.VISIBLE) {
                    mGroupViewHolder.group_download_status_text.setVisibility(View.GONE);
                }*/
                mGroupViewHolder.group_download_status_text.setVisibility(View.VISIBLE);
                String status = mGroupViewHolder.group_download_status_text.getText().toString();
                String size = getSize(status);
                mGroupViewHolder.group_download_status_text.setText(size);
                mGroupViewHolder.group_download_status_text.setTextColor(Color.parseColor("#8C8C8C"));

                mGroupViewHolder.group_download_status.setImageResource(R.mipmap.btn_download);
                mGroupViewHolder.group_download_status.setOnClickListener(mOffLineMapStatusClickListener);


            }else{
                OffLineMapCityAdpter.ChildViewHolder mChildViewHolder = (OffLineMapCityAdpter.ChildViewHolder) tag;
                /*if(mChildViewHolder.download_status_text.getVisibility() == View.VISIBLE){
                    mChildViewHolder.download_status_text.setVisibility(View.GONE);
                }*/
                mChildViewHolder.download_status_text.setVisibility(View.VISIBLE);
                String status = mChildViewHolder.download_status_text.getText().toString();
                String size = getSize(status);
                mChildViewHolder.download_status_text.setText(size);
                mChildViewHolder.download_status_text.setTextColor(Color.parseColor("#8C8C8C"));

                mChildViewHolder.child_download_status.setImageResource(R.mipmap.btn_download);
                mChildViewHolder.child_download_status.setOnClickListener(mOffLineMapStatusClickListener);
            }


            return true;
        }

        @Override
        public void onStop() {
            if(isDebug)LogUtils.debug("onStop  cityID" + cityID);
//            if(isGoup){
            Object tag = item.getTag();
            if(tag instanceof OffLineMapCityAdpter.GroupViewHolder){
                OffLineMapCityAdpter.GroupViewHolder mGroupViewHolder = (OffLineMapCityAdpter.GroupViewHolder) tag;
                if(mGroupViewHolder.group_download_status_text.getVisibility() != View.VISIBLE) {
                    mGroupViewHolder.group_download_status_text.setVisibility(View.VISIBLE);
                }

                String status = mGroupViewHolder.group_download_status_text.getText().toString();
                String size = getSize(status);

                mGroupViewHolder.group_download_status_text.setText(MainApplication.getInstance().getString(R.string.download_stop) + "  " + size);
                mGroupViewHolder.group_download_status_text.setTextColor(Color.parseColor("#E51C23"));

                mGroupViewHolder.group_download_status.setOnClickListener(mOffLineMapStatusClickListener);
            }else{
                OffLineMapCityAdpter.ChildViewHolder mChildViewHolder = (OffLineMapCityAdpter.ChildViewHolder) tag;
                if(mChildViewHolder.download_status_text.getVisibility() != View.VISIBLE){
                    mChildViewHolder.download_status_text.setVisibility(View.VISIBLE);
                }
                String status = mChildViewHolder.download_status_text.getText().toString();
                String size = getSize(status);
                mChildViewHolder.download_status_text.setText(MainApplication.getInstance().getString(R.string.download_stop) + "  " + size);
                mChildViewHolder.download_status_text.setTextColor(Color.parseColor("#E51C23"));

                mChildViewHolder.child_download_status.setOnClickListener(mOffLineMapStatusClickListener);
            }
        }

        @Override
        public void onReStart() {
            if(isDebug)LogUtils.debug("onReStart  cityID" + cityID);
//            if(isGoup){
            Object tag = item.getTag();
            if(tag instanceof OffLineMapCityAdpter.GroupViewHolder){
                OffLineMapCityAdpter.GroupViewHolder mGroupViewHolder = (OffLineMapCityAdpter.GroupViewHolder) tag;
                if(mGroupViewHolder.group_download_status_text.getVisibility() != View.VISIBLE) {
                    mGroupViewHolder.group_download_status_text.setVisibility(View.VISIBLE);
                }

                String status = mGroupViewHolder.group_download_status_text.getText().toString();
                String size = getSize(status);

                mGroupViewHolder.group_download_status_text.setText(MainApplication.getInstance().getString(R.string.downloading) + "  " +size);

                mGroupViewHolder.group_download_status.setOnClickListener(null);
            }else{
                OffLineMapCityAdpter.ChildViewHolder mChildViewHolder = (OffLineMapCityAdpter.ChildViewHolder) tag;
                if(mChildViewHolder.download_status_text.getVisibility() != View.VISIBLE){
                    mChildViewHolder.download_status_text.setVisibility(View.VISIBLE);
                }

                String status = mChildViewHolder.download_status_text.getText().toString();
                String size = getSize(status);

                mChildViewHolder.download_status_text.setText(MainApplication.getInstance().getString(R.string.downloading) + "  "+ size);
                mChildViewHolder.child_download_status.setOnClickListener(null);
            }
        }
    }

    private String getSize(String status){
        String[] split = status.split("  ");
        if(split.length == 1){
            return split[0];
        }else{
            return split[1];
        }
    }
}
