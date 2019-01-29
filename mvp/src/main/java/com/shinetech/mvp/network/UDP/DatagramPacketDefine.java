package com.shinetech.mvp.network.UDP;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/1/25.
 */
public class DatagramPacketDefine {

    public static final byte FRAME_HEAD = 0x7E; //数据包的帧头
    public static final byte FRAME_END = 0x7F; //数据包的挣尾

    /**
     * 个人登录类型
     */
    public static final byte USER_TYPE = 0x01;

    /**
     * 分组登录类型
     */
    public static final byte GROUP_TYPE = 0x00;

    /**
     * 公司登录类型
     */
    public static final byte COMPANY_TYPE = 0x02;
    /**
     * 企业登录类型
     */
    public static final byte GIS_TYPE = 0x03;

    /**
     * A.3.8　获取车辆列表 操作码
     */
    public static final int OPERATION_CODE_CARLIST = 100;

    /**
     * A.3.15　车辆历史轨迹 操作码
     */
    public static int OPERATION_CODE_HISTORICAL_ROUTE_ = 101;

    /**
     * A.3.20　查询车辆违规日志 操作码
     */
    public static int OPERATION_CODE_VIOLATIONLOG_ROUTE_ = 102;

    //应答数据包buffer
    public static final byte[] ACK_PACKET = new byte[]{
            FRAME_HEAD, //帧头
            0x00, 0x00, //帧号
            0x00, 0x02, //数据包长度
            0x00, 0x00, //应答协议字
            FRAME_END //帧尾
    };


   /* //心跳包发送buffer
    public static final byte[] HEART_BEAT_BUFFER = new byte[]{
            FRAME_HEAD, //帧头
            0x00, 0x00, //帧号
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, //用户手机号
            0x00, 0x02, //数据包长度
            0x01, 0x07, //心跳连接协议字
            FRAME_END //帧尾
    };

    //应答数据包buffer
    public static final byte[] ACK_PACKET = new byte[]{
            FRAME_HEAD, //帧头
            0x00, 0x00, //帧号
            0x00, 0x02, //数据包长度
            0x00, 0x00, //应答协议字
            FRAME_END //帧尾
    };

    public static final byte[] LOGINOUT_PACKET = new byte[]{
            FRAME_HEAD, //帧头
            0x00, 0x00, //帧号
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, //用户手机号
            0x00, 0x02, //数据包长度
            0x01, 0x08, // 用户退出协议字
            FRAME_END //帧尾
    };*/
}
