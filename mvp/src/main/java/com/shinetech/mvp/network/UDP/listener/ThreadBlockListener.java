package com.shinetech.mvp.network.UDP.listener;

/**
 * Created by ljn on 2017-09-14.
 */

public interface ThreadBlockListener {

    void isBlock();

    boolean checkBlock(long sendTime);
}
