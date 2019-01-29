package com.easyder.carmonitor.interfaces;

/**
 * Created by ljn on 2017-04-21.
 */
public interface SendMessagebackListener {

    void onBack();

    void commit(String message);
}
