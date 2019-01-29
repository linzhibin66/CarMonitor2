package com.easyder.carmonitor.interfaces;

import com.easyder.carmonitor.bean.DownLoadImgTask;
import com.easyder.carmonitor.widget.orderManager.AttachmentItemWidget;

/**
 * Created by Lenovo on 2018-02-09.
 */

public interface DownLoadImgListener {

    void addTask(String orderName, String orderNumber, String fileType, String fileName, AttachmentItemWidget mAttachmentItemWidget);

    void onProgress(DownLoadImgTask downLoadImgTask, int total, int currentProgress, short protocol);

    void onSuccess(DownLoadImgTask downLoadImgTask);

    void onError(DownLoadImgTask downLoadImgTask);
}
