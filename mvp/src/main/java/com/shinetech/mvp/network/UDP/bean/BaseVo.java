package com.shinetech.mvp.network.UDP.bean;

import com.shinetech.mvp.network.UDP.bean.orderBean.OrderCtrlResultVo;
import com.shinetech.mvp.utils.ByteUtil;

/**
 * @author 刘琛慧
 *         date 2015/10/13.
 */
public abstract class BaseVo {
    //参数对应的数据类型常量
    public static final short BYTE = 0x1;
    public static final short SHORT = 0x2;
    public static final short INT = 0x4;
    public static final short LIST = 0x8;
//    public static final short BYTEARRAY = 0x10;
    public static final short DATA = 0x20;
    public static final short STRING = 0xFF;

    /**
     * 请求没有响应
     */
    public static final short NO_RESPONSE = 0xF;

    /**
     * 请求有响应结果
     */
    public static final short HAS_RESPONSE = 0xE;
    public static final byte RESULT_FAILURE = 0x0; //失败结果
    public static final byte RESULT_SUCCESS = 0x1; //成功结果
    public short requestProtocolHead; //请求协议头
    protected short responseProtocolHead; //响应协议头,默认为空，即值为请求协议头加一，否则为赋值的协议头
/*    private short packageTatol = 1; //总包数
    private short packageSerialNumber = 1; //包序号*/
    public short requestType = HAS_RESPONSE; //请求类型
    public boolean hasMultiResponsePacket; //有多个返回数据包
    protected boolean isHasOrderCtrlResult = false;

    private boolean isDisposeOrderCtrlResult = false;//是否解析了工单操作结果

    private boolean isReceiveOtherData = true; //是否接收完其他数据包（用于一对多协议包的情况，发送一个协议，接收多种协议数据包）;使用时，需要初始化为false。

    /**
     * 是否可以分包发送数据
     */
    private boolean canSubPackageSend = false;

    /**
     * 是否是分包接收数据
     */
    private boolean isSubPackageReveive = false;

    /**
     * 设防
     */
    public static final byte FORTIFY = 0x01;

    /**
     * 撤防
     */
    public static final byte WITHDRAWFORTIFY = 0x02;

    /**
     * 锁车
     */
    public static final byte LOCKCAR = 0x03;

    /**
     * 解锁
     */
    public static final byte UNLOCK = 0x04;

    /**
     * 开车门
     */
    public static final byte OPENCARDOOR = 0x05;

    /**
     * 锁车门
     */
    public  static final byte  LOCKCARDOOR = 0x06;

    /**
     * 鸣喇叭（寻车）
     */
    public static final byte HORNBLOWING = 0x07;

    /**
     * 复位终端密码
     */
    public static final byte RESETTERMINALPWD = 0x08;

    /**
     * 关喇叭
     */
    public static final byte HORNOFF = 0x09;


    /**
     * 获取接口请求需要的数据的属性
     *
     * @return 接口请求参数的数组
     */
    public abstract Object[] getProperties();

    /**
     * 设置接口响应的数据解析
     *
     * @param properties 解码成功后的属性
     */
    public abstract void setProperties(Object[] properties);


    public static String parseString(byte[] property) {
        return ByteUtil.stringFromBytes(property, 0, property.length);
    }

    /**
     * @return 需要解析属性的类型数组，返回的属性的类型数组一定要按照协议的熟顺序排列
     */
    public abstract short[] getDataTypes();

    /**
     * @param index getDataTypes()方法返回的属性数组中LIST元素的下标
     * @return 下标为index的List集合中元素的属性集合
     */
    public short[] getElementDataTypes(int index) {
        if (index < 0 || index >= getDataTypes().length) {
            throw new IllegalArgumentException("非法的下标：" + index);
        }

        return null;
    }

    /**
     * @param elementProperties List集合中元素的属性集合
     * @param index             当前List在getDataTypes()返回的数组中的下标位置
     */
    public void addListElement(Object[] elementProperties, int index) {
    }

    public short getResponseProtocolHead() {
        return responseProtocolHead;
    }

    public boolean disposeOrderCtrlResult(BaseVo mBaseVo){
        return false;
    }

    public boolean isHasOrderCtrlResult(){
        return isHasOrderCtrlResult;
    }

    public boolean isDisposeOrderCtrlResult() {
        return isDisposeOrderCtrlResult;
    }

    public void setDisposeOrderCtrlResult(boolean disposeOrderCtrlResult) {
        isDisposeOrderCtrlResult = disposeOrderCtrlResult;
    }

    public boolean isReceiveOtherData() {
        return isReceiveOtherData;
    }

    public void setReceiveOtherData(boolean receiveOtherData) {
        isReceiveOtherData = receiveOtherData;
    }

    public void setOrderCtrlResultValue(BaseVo mBaseVo){
    }

    public boolean isCanSubPackageSend() {
        return canSubPackageSend;
    }

    public void setCanSubPackageSend(boolean canSubPackageSend) {
        this.canSubPackageSend = canSubPackageSend;
    }

    public boolean isSubPackageReveive() {
        return isSubPackageReveive;
    }

    public void setSubPackageReveive(boolean isSubPackageReveive) {
        this.isSubPackageReveive = isSubPackageReveive;
    }

    /* public short getPackageTatol() {
        return packageTatol;
    }

    public void setPackageTatol(short packageTatol) {
        this.packageTatol = packageTatol;
    }

    public short getPackageSerialNumber() {
        return packageSerialNumber;
    }

    public void setPackageSerialNumber(short packageSerialNumber) {
        this.packageSerialNumber = packageSerialNumber;
    }*/
}
