package com.shinetech.mvp.sort;

import com.shinetech.mvp.network.UDP.bean.item.ResponseInfo;
import com.shinetech.mvp.network.UDP.listener.BaseCarHistoricalRouteItem;

import java.util.Comparator;

/**
 * Created by ljn on 2017-04-26.
 */
public class ResponseInfoComparator implements Comparator<ResponseInfo> {


    @Override
    public int compare(ResponseInfo lhs, ResponseInfo rhs) {
        int ordinal1 = lhs.packetSerialNumber;
        int ordinal2 = rhs.packetSerialNumber;

        if(ordinal1>ordinal2){
            return 1;
        }else if(ordinal1<ordinal2){
            return -1;
        }

        return 0;
    }

}
