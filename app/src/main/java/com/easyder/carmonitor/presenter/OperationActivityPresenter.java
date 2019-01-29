package com.easyder.carmonitor.presenter;

import com.shinetech.mvp.network.UDP.presenter.OperationPresenter;

/**
 * Created by ljn on 2017-08-04.
 */
public class OperationActivityPresenter extends OperationPresenter{

    /**
     * 发送信息
     */
    public static final byte INSTRUCT_SEND_MESSAGE = 0x00;

    /**
     * 设防
     */
    public static final byte INSTRUCT_FORTIFY_ON = 0x01;

    /**
     * 撤防
     */
    public static final byte INSTRUCT_FORTIFY_OFF = 0x02;

    /**
     * 开车门
     */
    public static final byte INSTRUCT_OPEN_CARDOOR = 0x05;

    /**
     * 锁车门
     */
    public static final byte INSTRUCT_LOCK_CARDOOR = 0x06;

    /**
     * 鸣喇叭
     */
    public static final byte INSTRUCT_HORN_ON = 0x07;

    /**
     * 复位终端密码
     */
    public static final byte INSTRUCT_RESET_PWD = 0x08;

    /**
     * 关喇叭
     */
    public static final byte INSTRUCT_HORN_OFF = 0x09;

}
