package com.easyder.carmonitor.bean;

import com.easyder.carmonitor.widget.orderManager.AttachmentItemWidget;

/**
 * Created by Lenovo on 2018-02-09.
 */

public class DownLoadImgTask {

    private String orderName;

    private String orderNumber;

    private String tileType;

    private String fileName;

    private AttachmentItemWidget mAttachmentItemWidget;

    public DownLoadImgTask(String orderName, String orderNumber, String tileType, String fileName, AttachmentItemWidget mAttachmentItemWidget) {
        this.orderName = orderName;
        this.orderNumber = orderNumber;
        this.tileType = tileType;
        this.fileName = fileName;
        this.mAttachmentItemWidget = mAttachmentItemWidget;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTileType() {
        return tileType;
    }

    public void setTileType(String tileType) {
        this.tileType = tileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public AttachmentItemWidget getmAttachmentItemWidget() {
        return mAttachmentItemWidget;
    }

    public void setmAttachmentItemWidget(AttachmentItemWidget mAttachmentItemWidget) {
        this.mAttachmentItemWidget = mAttachmentItemWidget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownLoadImgTask that = (DownLoadImgTask) o;

        if (getOrderName() != null ? !getOrderName().equals(that.getOrderName()) : that.getOrderName() != null)
            return false;
        if (getOrderNumber() != null ? !getOrderNumber().equals(that.getOrderNumber()) : that.getOrderNumber() != null)
            return false;
        if (getTileType() != null ? !getTileType().equals(that.getTileType()) : that.getTileType() != null)
            return false;
        if (getFileName() != null ? !getFileName().equals(that.getFileName()) : that.getFileName() != null)
            return false;
        return getmAttachmentItemWidget() != null ? getmAttachmentItemWidget().equals(that.getmAttachmentItemWidget()) : that.getmAttachmentItemWidget() == null;

    }

}
