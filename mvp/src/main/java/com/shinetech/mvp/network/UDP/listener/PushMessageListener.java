package com.shinetech.mvp.network.UDP.listener;

import com.shinetech.mvp.DB.bean.PushMessage;

/**
 * Created by ljn on 2017-05-18.
 */
public interface PushMessageListener {

    void receivePushMessage(PushMessage newPushMessage, int noReadcount);
}
