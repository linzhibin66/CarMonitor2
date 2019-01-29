package com.easyder.carmonitor.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.dialog.OfflineTaskCtrlDialog;
import com.easyder.carmonitor.interfaces.DownLoadManagerItemClickListener;
import com.easyder.carmonitor.interfaces.UpdataOfflineMapOnDelete;
import com.shinetech.mvp.interfaces.OffLineMapListener;
import com.shinetech.mvp.offlineMap.OffLineBaiduMapUtils;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.UIUtils;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ljn on 2016/11/14.
 */
public class OffLineDownLoadManagerWidget implements DownLoadManagerItem.OnFinishListener, View.OnClickListener {

    private View view;

    private LinearLayout download_content_layout;

    private LinearLayout finish_content_layout;

    private TextView downloading_title;

    private TextView download_finish_title;

    private TextView download_nocontent;

    private RelativeLayout download_all_layout;

    private RelativeLayout stop_all_layout;

    private Context mContext;

    private OffLineBaiduMapUtils mOffLineBaiduMapUtils = OffLineBaiduMapUtils.getInstance();

    private UpdataOfflineMapOnDelete mUpdataOfflineMapOnDelete;

    private  DownLoadManagerItemClickListener listener;

    private boolean isDebug = false && LogUtils.isDebug;

    public OffLineDownLoadManagerWidget(Context context) {
        view = View.inflate(context, R.layout.offline_download_manager,null);
        mContext = context;
        mOffLineBaiduMapUtils.setOffLineListener(new OffLineMapListener() {
            @Override
            public void onVersionUpdate() {
                if(isDebug)LogUtils.error("onVersionUpdate ... ");
            }

            @Override
            public void onNewOffLineTask() {
                update();
            }
        });
        initView();
        initItemListener();
        loadDate();
    }

    public View getView(){
        return view;
    }

    private void initView(){
        download_content_layout = (LinearLayout) view.findViewById(R.id.download_content);
        finish_content_layout = (LinearLayout) view.findViewById(R.id.downloaded_content);
        downloading_title = (TextView) view.findViewById(R.id.downloading_title);
        download_finish_title = (TextView) view.findViewById(R.id.download_finish_title);
        download_nocontent = (TextView) view.findViewById(R.id.download_nocontent);
        download_all_layout = (RelativeLayout) view.findViewById(R.id.download_all_layout);
        download_all_layout.setOnClickListener(this);
        stop_all_layout = (RelativeLayout) view.findViewById(R.id.stop_all_layout);
        stop_all_layout.setOnClickListener(this);
    }

    //初始化条目点击事件
    private void initItemListener(){
        if(listener == null) {
            listener = new DownLoadManagerItemClickListener() {
                @Override
                public void onItemClick(View itemLayout, MKOLUpdateElement mItemInfo) {
                    final OfflineTaskCtrlDialog mOfflineTaskCtrlDialog = new OfflineTaskCtrlDialog(mContext, mItemInfo);

                    mOfflineTaskCtrlDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            ConfigurationChangedManager.getInstance().unRegistOffLineMapConfig(mOfflineTaskCtrlDialog);
                        }
                    });

                    if (mUpdataOfflineMapOnDelete != null) {
                        mOfflineTaskCtrlDialog.setUpdataOfflineMapOnDelete(mUpdataOfflineMapOnDelete);
                    }

                    ConfigurationChangedManager.getInstance().registOffLineMapConfig(mOfflineTaskCtrlDialog);
                    mOfflineTaskCtrlDialog.show(itemLayout);
                }
            };
        }
    }

    private void loadDate(){
        List<MKOLUpdateElement> allUpdateInfo = mOffLineBaiduMapUtils.getAllUpdateInfo();

        if(allUpdateInfo == null){
            initContentView();
            return ;
        }

        if(mUpdataOfflineMapOnDelete == null) {
            //操作后刷新状态
            mUpdataOfflineMapOnDelete = new UpdataOfflineMapOnDelete() {
                @Override
                public void updata() {
                    update();
                }
            };
        }

        for(MKOLUpdateElement itemInfo :allUpdateInfo){
            if(itemInfo.ratio != 100){
                DownLoadManagerItem item = new DownLoadManagerItem(mContext, itemInfo,download_content_layout);
                item.setFinishListener(this);
                item.setOnItemClick(listener);
                download_content_layout.addView(item.getView());
//                updataDialog
            }else{
                DownLoadManagerFinishItem downLoadManagerFinishItem = new DownLoadManagerFinishItem(mContext, itemInfo);
                downLoadManagerFinishItem.setOnItemClick(listener);
                finish_content_layout.addView(downLoadManagerFinishItem.getRootView());

            }
        }

        initContentView();

    }

    private void initContentView(){

        if(download_content_layout.getChildCount() == 0){
            downloading_title.setVisibility(View.GONE);
            download_content_layout.setVisibility(View.GONE);
        }else{
            downloading_title.setVisibility(View.VISIBLE);
            download_content_layout.setVisibility(View.VISIBLE);
        }

        if(finish_content_layout.getChildCount() == 0){
            download_finish_title.setVisibility(View.GONE);
            finish_content_layout.setVisibility(View.GONE);
        }else{
            download_finish_title.setVisibility(View.VISIBLE);
            finish_content_layout.setVisibility(View.VISIBLE);
        }

        if((finish_content_layout.getChildCount() == 0) && (download_content_layout.getChildCount() == 0)){
            download_nocontent.setVisibility(View.VISIBLE);
        }else{
            download_nocontent.setVisibility(View.GONE);
        }
    }

    private void update(){
        download_content_layout.removeAllViews();
        finish_content_layout.removeAllViews();
        mOffLineBaiduMapUtils.cleanAllUpdateListener();
        loadDate();
    }

    @Override
    public void OnFinish() {
        update();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case  R.id.download_all_layout:
                mOffLineBaiduMapUtils.downloadAll();
                update();
                break;
            case R.id.stop_all_layout:
                mOffLineBaiduMapUtils.stopAll();
                update();
                break;
        }
    }
}
