package com.shinetech.mvp.network.UDP.bean.item;

import java.util.Arrays;

/**
 * @author 刘琛慧
 *         date 2015/12/11.
 */
public class ResponseInfo implements Cloneable{
    /**
     * 数据接受成功
     */
    public static final short RECEIVE_SUCCESS = 0x1;

    /**
     * 接受应答数据包
     */
    public static final short ACK_PACKET_RECEIVED = 0x2;

    /**
     * 返回数据包协议头错误
     */
    public static final short PROTOCOL_HEAD_ERROR = 0x3;

    /**
     * 数据包长度校验不通过
     */
    public static final short INVALID_DATA_LENGTH = 0x4;

    /**
     * 重复帧号的数据包
     */
    public static final short REPEAT_PACKET_RECEIVED = 0x5;

    /**
     * 心跳连接包
     */
    public static final short HEART_BEAT_PACKET_RECEIVED = 0x6;


    public int resultStatusCode; //数据解析状态结果
    public short receiveFrameNo; //接手数据帧号
    public short packetTotal; //总包数
    public short packetSerialNumber; //包序号
    public short dataLength; //数据包的长度
    public short protocol; //协议号
    public byte[] dataBytes = new byte[1400]; //数据包内容
    public int packetLength; //完成数据包长度（包括帧头尾）

    @Override
    public Object clone(){

        ResponseInfo mResponseInfo = null;
        try {
            mResponseInfo = (ResponseInfo) super.clone();

            byte[] newDataBytes = new byte[1400];

            System.arraycopy(dataBytes, 0, newDataBytes, 0, newDataBytes.length);

            mResponseInfo.dataBytes = newDataBytes;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }


        return mResponseInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResponseInfo that = (ResponseInfo) o;

        if (packetTotal != that.packetTotal) return false;
        if (packetSerialNumber != that.packetSerialNumber) return false;
        if (dataLength != that.dataLength) return false;
        if (protocol != that.protocol) return false;
        if (packetLength != that.packetLength) return false;
        return Arrays.equals(dataBytes, that.dataBytes);

    }
}
