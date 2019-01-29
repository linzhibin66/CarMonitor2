package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.bean.DownLoadImgTask;
import com.easyder.carmonitor.interfaces.DownLoadImgListener;
import com.easyder.carmonitor.presenter.DownLoadAttachmentPresenter;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.AttachmentItemVo;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2018-01-16.
 */

public class AttachmentWidget implements DownLoadImgListener {

    private Context context;

    private View rootView;

    private List<AttachmentItemVo> accessoryData;

    private String orderNumber;

    private String orderName;

    private List<DownLoadImgTask>  taskList = new ArrayList<>();

    public AttachmentWidget(Context context, String orderNumber, String orderName, List<AttachmentItemVo> accessory) {
        this.context = context;

        rootView = View.inflate(context, R.layout.attachment_widget_layout, null);
        this.accessoryData = accessory;
        this.orderNumber = orderNumber;
        this.orderName = orderName;

        initData();

    }

    private void initData(){

        LinearLayout content_layout = (LinearLayout) rootView.findViewById(R.id.attachment_content_layout);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(41));

        for(AttachmentItemVo item :accessoryData){

            AttachmentItemWidget attachmentItemWidget = new AttachmentItemWidget(context, orderNumber, orderName, item, this);

            content_layout.addView(attachmentItemWidget.getItemView(), layoutParams);

        }

    }



    public View getView(){
        return rootView;
    }


    @Override
    public void addTask(String orderName, String orderNumber, String fileType, String fileName, AttachmentItemWidget mAttachmentItemWidget) {

        DownLoadImgTask downLoadImgTask = new DownLoadImgTask(orderName, orderNumber, fileType, fileName, mAttachmentItemWidget);

        if(!taskList.contains(downLoadImgTask)){
            taskList.add(downLoadImgTask);
            //TODO start task
            if(taskList.size() == 1){

                downLoadImg(downLoadImgTask);

            }
        }

        if(taskList.size() > 1){

            //waiting 等待下载
            downLoadImgTask.getmAttachmentItemWidget().waitingDownLoad();
        }


    }

    @Override
    public void onProgress(DownLoadImgTask downLoadImgTask, int total, int currentProgress, short protocol) {
        //更新  downLoadImgTask.getmAttachmentItemWidget() 进度
        downLoadImgTask.getmAttachmentItemWidget().updateProgress(total, currentProgress);

    }

    @Override
    public void onSuccess(DownLoadImgTask downLoadImgTask) {

        taskList.remove(downLoadImgTask);

        if(taskList.size()>0){
            downLoadImg(taskList.get(0));
        }

        downLoadImgTask.getmAttachmentItemWidget().downLoadFinish();

        //更新  downLoadImgTask.getmAttachmentItemWidget() 界面状态

    }

    @Override
    public void onError(DownLoadImgTask downLoadImgTask) {
        taskList.remove(downLoadImgTask);

        if(taskList.size()>0){
            downLoadImg(taskList.get(0));
        }

        downLoadImgTask.getmAttachmentItemWidget().downLoadError();

        //更新  downLoadImgTask.getmAttachmentItemWidget() 界面状态
    }

    private void downLoadImg(DownLoadImgTask downLoadImgTask){
        DownLoadAttachmentPresenter downLoadAttachmentPresenter = new DownLoadAttachmentPresenter(downLoadImgTask, this);
        downLoadAttachmentPresenter.downloadAttachment(downLoadImgTask.getOrderName(), downLoadImgTask.getOrderNumber(), downLoadImgTask.getTileType(), downLoadImgTask.getFileName());
        downLoadImgTask.getmAttachmentItemWidget().downLoading();
    }

    public void clearDownLoadTask(){

    }
}
