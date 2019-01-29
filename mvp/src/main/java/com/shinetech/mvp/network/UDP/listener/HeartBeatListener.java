package com.shinetech.mvp.network.UDP.listener;

/**
 * Created by Lenovo on 2016/11/7.
 */
public interface HeartBeatListener {

    /**
     * 心跳连接发送失败
     */
    void sendToFail();

    /**
     *达到2次失败，回调Error，退出登录。(1分钟左右服务器无响应，认为与服务器断开连接)
     */
    void sendToError();
}
