package com.shinetech.mvp.network.UDP.bean.item;

import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/2/7.
 */
public class UDPTask {

    private BaseVo dataVo;

    private List<DataInfo> dataInfoList;

    private ResponseListener mResponseListener;

    /**
     * 发送应答包接收时间或最后一个数据包接收时间（分包发送的协议）
     */
    private long responseTime;

    public boolean isUseFrameNoKey = false;

    /**
     * 拆分的数据包个数
     */
//    private int packageTotal = 1;

    /**
     * 发送包的帧号列表
     */
    private List<Short> frameNoList = new ArrayList<>();

    /**
     * 是否接收完数据
     */
    private boolean isResponseFinish = false;

    public UDPTask() {
    }

    public UDPTask(BaseVo dataVo, List<DataInfo> dataInfoList, ResponseListener mResponseListener) {
        this.dataVo = dataVo;
        this.dataInfoList = dataInfoList;
        this.mResponseListener = mResponseListener;
    }

    public BaseVo getDataVo() {
        return dataVo;
    }

    public void setDataVo(BaseVo dataVo) {
        this.dataVo = dataVo;
    }

    public List<DataInfo> getDataInfo() {
        return dataInfoList;
    }

    public void setDataInfo(List<DataInfo> dataInfoList) {
        this.dataInfoList = dataInfoList;
    }

    public ResponseListener getmResponseListener() {
        return mResponseListener;
    }

    public void setmResponseListener(ResponseListener mResponseListener) {
        this.mResponseListener = mResponseListener;
    }

    public boolean isResponseFinish() {
        return isResponseFinish;
    }

    public void setIsResponseFinish(boolean isResponseFinish) {
        this.isResponseFinish = isResponseFinish;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    /*public int getPackageTotal() {
        return packageTotal;
    }

    public void setPackageTotal(int packageTotal) {
        this.packageTotal = packageTotal;
    }*/

    public List<Short> getFrameNoList() {
        return frameNoList;
    }

    public void addFrameNoList(Short frameNo) {
        if(!frameNoList.contains(frameNo)){
            frameNoList.add(frameNo);
        }
    }

    public void removeFrameNo(Short frameNo){

    }
}
