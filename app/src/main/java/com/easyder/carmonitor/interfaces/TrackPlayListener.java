package com.easyder.carmonitor.interfaces;

import com.shinetech.mvp.network.UDP.listener.BaseCarHistoricalRouteItem;

/**
 * Created by ljn on 2017-04-26.
 */
public interface TrackPlayListener {

    void onTrackChangeProgress(int max, int index, BaseCarHistoricalRouteItem carinfo);

    void onTrackStop();

    void onTrackReset();

    void onPlay();
}
