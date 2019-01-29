package com.shinetech.mvp.interfaces;

import java.util.Map;

/**
 * Created by ljn on 2018-04-08.
 */

public interface CheckWXPayOrderListener {

    void onResult(String orderNo, Map<String, String> result);
}
