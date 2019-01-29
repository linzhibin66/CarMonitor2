package com.shinetech.mvp.interfaces;

/**
 * Created by ljn on 2018-04-04.
 */

public interface WxPayViewListener {

    void onError(String message);
    void onStart(String orderNo);
}
