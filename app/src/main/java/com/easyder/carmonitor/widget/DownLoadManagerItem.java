package com.easyder.carmonitor.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.dialog.OfflineTaskCtrlDialog;
import com.easyder.carmonitor.interfaces.DownLoadManagerItemClickListener;
import com.easyder.carmonitor.interfaces.UpdataOfflineMapOnDelete;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.interfaces.OffLineStatusListener;
import com.shinetech.mvp.offlineMap.OffLineBaiduMapUtils;
import com.shinetech.mvp.offlineMap.bean.BaseOfflineItem;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.SizeUtils;
import com.shinetech.mvp.utils.ToastUtil;

/**
 * Created by ljn on 2016/11/14.
 */
public class DownLoadManagerItem extends BaseOfflineItem implements View.OnClickListener {

    private View itemLayout;

    private TextView city_name;

//    private ImageButton stop_or_start;

    private TextView ratio;

    private ProgressBar progressBar;

    private TextView city_size;

    private MKOLUpdateElement mItemInfo;

    private int cityID;

    private LinearLayout content_layout;

    private OnFinishListener listener;

    private boolean isDebug = false && LogUtils.isDebug;

    private DownLoadManagerItemClickListener clickListener;

    private Context context;

    public DownLoadManagerItem(Context context, MKOLUpdateElement updateInfo, LinearLayout content_layout) {
        super(updateInfo);
        this.context = context;
        itemLayout  = View.inflate(context, R.layout.download_manager_item, null);
        mItemInfo = updateInfo;
        itemLayout.setOnClickListener(this);
        if(isDebug)LogUtils.debug(mItemInfo.cityName + " item status "+ mItemInfo.status);
        cityID = updateInfo.cityID;
        this.content_layout = content_layout;
        initView();
        initDate();

//        initListener();
    }

    public View getView(){
        return itemLayout;
    }

    public void initView(){
        city_size = (TextView) itemLayout.findViewById(R.id.city_size);
        city_name = (TextView) itemLayout.findViewById(R.id.city_name);
        ratio = (TextView) itemLayout.findViewById(R.id.ratio);
        progressBar = (ProgressBar) itemLayout.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        city_size.setText(SizeUtils.formatDataSize(mItemInfo.serversize));
       /* stop_or_start = (ImageButton) itemLayout.findViewById(R.id.stop_or_start);
        stop_or_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemInfo = OffLineBaiduMapUtils.getInstance().getUpdateInfo(cityID);
                if(mItemInfo.status == MKOLUpdateElement.DOWNLOADING){
                    OffLineBaiduMapUtils.getInstance().stop(cityID);
                }else if(mItemInfo.status == MKOLUpdateElement.SUSPENDED){
                    OffLineBaiduMapUtils.getInstance().start(cityID);
                }
            }
        });*/
    }

    private void initDate(){
        city_name.setText(mItemInfo.cityName);
        progressBar.setProgress(mItemInfo.ratio);

        if(mItemInfo.ratio < 100){
//            stop_or_start.setImageResource(R.mipmap.pause);
        }else{
           /* stop_or_start.setImageResource(R.mipmap.play);
            stop_or_start.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);*/
            if(listener!=null){

                listener.OnFinish();
                listener = null;
            }
        }
        if(mItemInfo.status == MKOLUpdateElement.DOWNLOADING) {
            ratio.setText(getDownLoadStaus(mItemInfo.status) + mItemInfo.ratio + "%");
            ratio.setTextColor(Color.parseColor("#FF2084FD"));
        }else{
            ratio.setText(getDownLoadStaus(mItemInfo.status)+ mItemInfo.ratio + "%");
            ratio.setTextColor(Color.parseColor("#FFFB4F60"));
        }
    }

    @Override
    public void updateView() {
        mItemInfo = OffLineBaiduMapUtils.getInstance().getUpdateInfo(cityID);
        initDate();
    }

   /* private void initListener(){
        OffLineBaiduMapUtils.getInstance().addStatusListener(cityID, new OffLineStatusListener() {

            @Override
            public void onStop() {
                if (debug) LogUtils.debug("DownLoadManagerItem  onStop  cityID : " + cityID);
//                stop_or_start.setImageResource(R.mipmap.play);
            }

            @Override
            public void onReStart() {
                if (debug) LogUtils.debug("DownLoadManagerItem  onReStart  cityID : " + cityID);
//                stop_or_start.setImageResource(R.mipmap.pause);
            }

            @Override
            public boolean onRemove() {
                if (debug) LogUtils.debug("DownLoadManagerItem  onRemove  cityID : " + cityID);
                content_layout.removeView(itemLayout);
                return true;
            }

        });
    }*/

    public void setFinishListener(OnFinishListener mOnFinishListener){
        this.listener = mOnFinishListener;
    }

    public void setOnItemClick(DownLoadManagerItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        clickListener.onItemClick(v, mItemInfo);
    }

    public interface OnFinishListener{
        void OnFinish();
    }

    private String getDownLoadStaus(int status){
        switch(status){
            case MKOLUpdateElement.DOWNLOADING:
                return MainApplication.getInstance().getString(R.string.downloading);
            case MKOLUpdateElement.WAITING:
                return MainApplication.getInstance().getString(R.string.download_wait);
            case MKOLUpdateElement.SUSPENDED:
                return MainApplication.getInstance().getString(R.string.download_stop);
            case MKOLUpdateElement.eOLDSFormatError:
                return MainApplication.getInstance().getString(R.string.dataerror_download_again);
            case MKOLUpdateElement.FINISHED:
                return MainApplication.getInstance().getString(R.string.downloaded);
            default:
                if(isDebug)LogUtils.error("getDownLoadStaus status : "+status);
                return MainApplication.getInstance().getString(R.string.undefined);

        }
    }
}
