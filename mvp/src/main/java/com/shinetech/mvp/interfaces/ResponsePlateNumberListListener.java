package com.shinetech.mvp.interfaces;

import java.util.List;

/**
 * Created by ljn on 2017-07-27.
 */
public interface ResponsePlateNumberListListener {

    void onSuccess(List<String> carList);

    void onError(String message);
}
