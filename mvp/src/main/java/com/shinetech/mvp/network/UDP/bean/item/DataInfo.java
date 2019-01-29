package com.shinetech.mvp.network.UDP.bean.item;

/**
 * @author 刘琛慧
 *         date 2015/12/11.
 */
public class DataInfo {
    public short frameNo; //数据包发送时的帧号
    public short packageTatol; //总包数
    public short packageSerialNumber; //包序号
    public byte[] dataBytes; //数据包的内容
    public short dataLength; //数据包的长度
    public short retryTimes; //数据包重发次数
    public long lastSendTimeMillis; //上一次数据包发送的时间
    private boolean sendSuccess; //第一次发送是否成功了
    public boolean isSend = false; //是否发送了

    public DataInfo(byte[] dataBytes, short dataLength) {
        this.dataBytes = dataBytes;
        this.dataLength = dataLength;
    }

    public synchronized boolean isSendSuccess() {
        return sendSuccess;
    }

    public synchronized void setSendSuccess(boolean sendSuccess) {
        this.sendSuccess = sendSuccess;
    }
}
