package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.OfflineTaskCtrlInterfaces;
import com.easyder.carmonitor.interfaces.UpdataOfflineMapOnDelete;
import com.easyder.carmonitor.widget.OfflineMapDownloadTaskCtrl;
import com.shinetech.mvp.offlineMap.OffLineBaiduMapUtils;
import com.shinetech.mvp.utils.SizeUtils;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-07-10.
 */
public class OfflineTaskCtrlDialog extends BasePopupWindowDialog implements OfflineTaskCtrlInterfaces, View.OnTouchListener {

    private MKOLUpdateElement dateInfo;

    private boolean isTouch = false;

    private UpdataOfflineMapOnDelete mUpdataOfflineMapOnDelete;

    private OffLineBaiduMapUtils mOffLineBaiduMapUtils = OffLineBaiduMapUtils.getInstance();

    private OfflineMapDownloadTaskCtrl mOfflineMapDownloadTaskCtrl;

    public OfflineTaskCtrlDialog(Context context, MKOLUpdateElement dateInfo) {
        super(context, R.layout.offline_task_ctrl_layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));
//        super(context, R.layout.offline_task_ctrl_layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.dateInfo = dateInfo;
        mOfflineMapDownloadTaskCtrl = new OfflineMapDownloadTaskCtrl(dateInfo.status,getLayout(),this);
        mOfflineMapDownloadTaskCtrl.setOutmostTouchListener(this);
        initView();
        setALLWindow();
        setFocusable(true);
        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, mOfflineMapDownloadTaskCtrl.getcontentLayout(), R.anim.pop_up2down_anim));
    }

    private void initView(){
        String cityName = dateInfo.cityName;
        mOfflineMapDownloadTaskCtrl.setTitle(cityName);

        //初始化继续下载和暂停下载
        if(dateInfo.status == MKOLUpdateElement.DOWNLOADING || dateInfo.status == MKOLUpdateElement.WAITING){
            mOfflineMapDownloadTaskCtrl.setCtrl(context.getString(R.string.offline_stop_downlod));
        }else if(dateInfo.status == MKOLUpdateElement.SUSPENDED){
            mOfflineMapDownloadTaskCtrl.setCtrl(context.getString(R.string.offline_restart_downlod));
        }

        //初始化删除
        if(dateInfo.status == MKOLUpdateElement.FINISHED || dateInfo.ratio == 100){
            mOfflineMapDownloadTaskCtrl.setDelete(context.getString(R.string.offline_delete_map, SizeUtils.formatDataSize(dateInfo.serversize)));
        }else{
            mOfflineMapDownloadTaskCtrl.setDelete(context.getString(R.string.offline_delete));
        }

    }

    public void updateView(MKOLUpdateElement dateInfo){
        this.dateInfo = dateInfo;
        initView();
    }


    /**
     * 继续下载和暂停下载 操作
     */
    @Override
    public void onCtrl() {
        if(dateInfo.status == MKOLUpdateElement.DOWNLOADING || dateInfo.status == MKOLUpdateElement.WAITING){
            mOffLineBaiduMapUtils.stop(dateInfo.cityID);
        }else if(dateInfo.status == MKOLUpdateElement.SUSPENDED){
            mOffLineBaiduMapUtils.start(dateInfo.cityID);
        }
        if (mUpdataOfflineMapOnDelete != null){
            mUpdataOfflineMapOnDelete.updata();
        }
        dismiss();
        isTouch = true;
    }

    @Override
    public void onDelete() {
        mOffLineBaiduMapUtils.remove(dateInfo.cityID);

//        if (mUpdataOfflineMapOnDelete != null && (dateInfo.status == MKOLUpdateElement.FINISHED || dateInfo.ratio == 100)){
            mUpdataOfflineMapOnDelete.updata();
//        }

        dismiss();
        isTouch = true;
    }

    @Override
    public void onCancel() {
        dismiss();
        isTouch = true;
    }

    public void setUpdataOfflineMapOnDelete(UpdataOfflineMapOnDelete updata){
        mUpdataOfflineMapOnDelete = updata;
    }

    public void show(View v){
        isTouch = false;
        show(v, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        mOfflineMapDownloadTaskCtrl.getcontentLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_down2up_anim));

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            dismiss();
            isTouch = true;
        }
        return true;
    }


}
