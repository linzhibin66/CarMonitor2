package com.shinetech.mvp.network.UDP.InfoTool;

/**
 * @author 刘琛慧
 *         date 2015/12/11.
 */
public class Protocol {

    /**
     * A.3.1　指定范围车辆信息查询 （协议字：0x0101）
     */
    public static final short PROTOCOL_QDACARINFO_BEAT = 0x0101;

    /**
     * A.3.3　用户申请登陆
     */
    public static final short LOGIN_TOKEN_BEAT = 0x0103;

    /**
     * A.3.4　用户申请登陆申请反馈
     */
    public static final short LOGIN_TOKEN_RESULT = 0x0104;

    /**
     * A.3.5　注册用户登陆
     */
    public static final short LOGIN_BEAT = 0x0105;

    /**
     * A.3.6　注册用户登陆反馈
     */
    public static final short LOGIN_RESULT = 0x0106;

    /**
     *  A.3.7　通信保持
     */
    public static final short PROTOCOL_HEART_BEAT = 0x0107;

    /**
     * A.3.8　注册用户退出
     */
    public static final short LOGIN_OUT = 0x0108;

    /**
     * A.4.1　获取车辆列表
     */
    public static final short PROTOCOL_CARLIST_BEAT = 0x0201;

    /**
     * A.4.2　监控车辆列表
     */
    public static final short PROTOCOL_CARLIST_BEAT_RESULT = 0x0202;

    /**
     * A.4.3　查询车辆基本信息
     */
    public static final short PROTOCOL_CARINFO_BEAT = 0x0203;

    /**
     * A.4.4　车辆基本信息
     */
    public static final short PROTOCOL_CARINFO_BEAT_RESULT = 0x0204;


    /**
     * A.4.5　查询车辆最新状态
     */
    public static final short PROTOCOL_CARSTATUS_BEAT = 0x0205;

    /**
     * A.4.6　车辆定位状态
     */
    public static final short PROTOCOL_CARSTATUS_BEAT_RESULT = 0x0206;

    /**
     * A.4.7　查询车辆历史轨迹
     */
    public static final short PROTOCOL_CARHISTORICALROUTE_BEAT = 0x0207;

    /**
     * A.4.8　车辆历史轨迹
     */
    public static final short PROTOCOL_CARHISTORICALROUTE_BEAT_RESULT = 0x0208;

    /**
     * A.4.9　终端操作指令
     */
    public static final short PROTOCOL_OPERATIONSTRUCTION_BEAT = 0x0209;

    /**
     * A.4.10　终端操作结果
     */
    public static final short PROTOCOL_OPERATIONSTRUCTION_BEAT_RESULT = 0x020a;

    /**
     * A.4.11　向终端发送信息
     */
    public static final short PROTOCOL_SENDMESSAGE_BEAT = 0x020b;

    /**
     * A.4.15　查询企业车辆基本信息
     */
    public static final short PROTOCOL_COMPANYALLCARINFO_BEAT = 0x020f;

    /**
     * A.4.16　企业车辆定位状态
     */
    public static final short PROTOCOL_COMPANYALLCARINFO_BEAT_RESULT = 0x0210;

    /**
     * A.4.12　实时路况查询
     */
    public static final short PROTOCOL_TRAFFICSTATUS_BEAT = 0x020c;

    /**
     * A.4.13　路况列表
     */
    public static final short PROTOCOL_TRAFFICSTATUS_BEAT_RESULT = 0x020d;

    /**
     * A.4.14　厂商信息推送
     */
    public static final short PROTOCOL_MESSAGEPUSH_BEAT_RESULT = 0x020e;


    //---------------------------------------------------------------- Response UDP Protocol ----------------------------------------------------------------------


    /**
     * A.3.1　接收确认应答（协议字：0x0000）
     */
    public static final short RESPONSE_ACK_BEAT = 0x0000;


    /**
     * A.3.2　用户申请登陆
     */
    public static final short RESPONSE_LOGIN_TOKEN_BEAT = 0x0001;

    /**
     * A.3.3　用户申请登陆申请反馈
     */
    public static final short RESPONSE_LOGIN_TOKEN_RESULT = 0x0002;

    /**
     * A.3.4　注册用户登陆
     */
    public static final short RESPONSE_LOGIN_BEAT = 0x0003;

    /**
     * A.3.5　注册用户登陆反馈
     */
    public static final short RESPONSE_LOGIN_RESULT = 0x0004;

    /**
     *  A.3.6　通信保持
     */
    public static final short RESPONSE_PROTOCOL_HEART_BEAT = 0x0005;

    /**
     * A.3.4　注册用户登陆
     */
    public static final short RESPONSE_LOGIN_OUT_BEAT = 0x0006;

    /**
     * A.3.8　获取车辆列表 （协议字：0x0007）
     */
    public static final short RESPONSE_PROTOCOL_CARLIST_BEAT = 0x0007;

    /**
     * 获取车辆列表结果协议字（此协议字为自定义结果协议字用于区分0x0007与0x0009操作结果） （协议字：0x1000）
     */
    public static final short RESPONSE_PROTOCOL_CARLIST_BEAT_RESULT = 0x1000;

    /**
     * A.3.9　列表数量 （协议字：0x0008)
     */
    public static final short RESPONSE_SIZE_BEAT = 0x0008;


    /**
     * A.3.10　查询车辆基本信息 （协议字：0x0009)
     */
    public static final short RESPONSE_PROTOCOL_CARINFO_BEAT = 0x0009;

    /**
     *A.3.11　车辆基本信息 （协议字：0x000a)
     */
    public static final short RESPONSE_PROTOCOL_CARINFO_BEAT_RESULT = 0x000a;

    /**
     * A.3.12　查询车辆最新状态 （协议字：0x000b)
     */
    public static final short RESPONSE_PROTOCOL_CARSTATUS_BEAT = 0x000b;

    /**
     * A.3.13　车辆定位状态 （协议字：0x000c)
     */
    public static final short RESPONSE_PROTOCOL_CARSTATUS_BEAT_RESULT = 0x000c;

    /**
     * A.3.14　查询车辆历史轨迹 （协议字：0x000d)
     */
    public static final short RESPONSE_PROTOCOL_CARHISTORICALROUTE_BEAT = 0x000d;

    /**
     * A.3.15　车辆历史轨迹 （协议字：0x000e)
     */
    public static final short RESPONSE_PROTOCOL_CARHISTORICALROUTE_BEAT_RESULT = 0x000e;

    /**
     * A.3.16　终端操作指令 （协议字：0x000f)
     */
    public static final short RESPONSE_PROTOCOL_OPERATIONSTRUCTION_BEAT = 0x000f;

    /**
     * A.3.17　终端操作结果 （协议字：0x0010)
     */
    public static final short RESPONSE_PROTOCOL_OPERATIONSTRUCTION_BEAT_RESULT = 0x0010;

    /**
     * A.3.18　向终端发送信息 （协议字：0x0011)
     */
    public static final short RESPONSE_PROTOCOL_SENDMESSAGE_BEAT = 0x0011;

    /**
     * A.3.19　厂商信息推送 （协议字：0x0012)
     */
    public static final short RESPONSE_PROTOCOL_MESSAGEPUSH_BEAT_RESULT = 0x0012;

    /**
     * A.3.20　查询车辆违规日志（协议字：0x0013）
     */
    public static final short RESPONSE_PROTOCOL_VIOLATIONLOG_BEAT = 0x0013;

    /**
     * A.3.21　车辆违规日志（协议字：0x0014）
     */
    public static final short RESPONSE_PROTOCOL_VIOLATIONLOG_BEAT_RESULT = 0x0014;

    /**
     * A.3.22　意见反馈 （协议字：0x0015）
     */
    public static final short RESPONSE_PROTOCOL_FEEDBACK_BEAT = 0x0015;

    /**
     * A.3.23　查询车辆费用（协议字：0x0016
     */
    public static final short RESPONSE_PROTOCOL_SELECT_CARCOST_BEAT = 0x0016;

    /**
     * A.3.24　车辆费用结果（协议字：0x0017）
     */
    public static final short RESPONSE_PROTOCOL_CAR_ALL_COST_RESULT = 0x0017;

    /**
     * A.3.25　车辆费用-项目明细（协议字：0x0018）
     */
    public static final short RESPONSE_PROTOCOL_CARCOST_INFO_BEAT = 0x0018;

    /**
     * A.3.26　查询工单（协议字：0x0019）
     */
    public static final short RESPONSE_PROTOCOL_SELECT_ORDER_BEAT = 0x0019;

    /**
     * A.3.27　工单列表（协议字:0x001A）
     */
    public static final short RESPONSE_PROTOCOL_SELECT_ORDER_BEAT_RESULT = 0x001A;

    /**
     * A.3.28　工单(协议字:0x001B)
     */
    public static final short RESPONSE_PROTOCOL_SELECT_ORDER_INFO_BEAT_RESULT = 0x001B;

    /**
     * A.3.29　工单操作结果 (协议字:0x001C)
     */
    public static final short RESPONSE_PROTOCOL_ORDER_CTRL_BEAT_RESULT = 0x001C;

    /**
     * A.3.34　工单操作（协议字：0x0021）
     */
    public static final short RESPONSE_PROTOCOL_ORDER_CTRL_BEAT = 0x0021;

    /**
     * A.3.35　工单师傅接单（协议字：0x0022）
     */
    public static final short RESPONSE_PROTOCOL_ACCEPT_ORDER_BEAT = 0x0022;

    /**
     * A.3.36　工单师傅安装完成（协议字：0x0023）
     */
    public static final short RESPONSE_PROTOCOL_INSTALL_FINISH_BEAT = 0x0023;

    /**
     * A.3.37　工单师傅维修完成（协议字：0x0024）
     */
    public static final short RESPONSE_PROTOCOL_MAINTENANCE_FINISH_BEAT = 0x0024;

    /**
     * A.3.38　工单客户申请维修（协议字：0x0025）
     */
    public static final short RESPONSE_PROTOCOL_MAINTENANCE_APPLY_BEAT = 0x0025;

    /**
     * A.3.39　工单客户评价（协议字：0x0026）
     */
    public static final short RESPONSE_PROTOCOL_CUSTOMER_EVALUATION_BEAT = 0x0026;
}
