package com.shinetech.mvp.sort;

import com.shinetech.mvp.network.UDP.listener.BaseCarHistoricalRouteItem;
import com.shinetech.mvp.network.UDP.resopnsebean.ViolationLogItem;

import java.util.Comparator;

/**
 * Created by ljn on 2017-04-26.
 */
public class ViolationOrdinalComparator implements Comparator<ViolationLogItem> {


    @Override
    public int compare(ViolationLogItem lhs, ViolationLogItem rhs) {
        int ordinal1 = lhs.getOrdinal();
        int ordinal2 = rhs.getOrdinal();

        if(ordinal1>ordinal2){
            return 1;
        }else if(ordinal1<ordinal2){
            return -1;
        }

        return 0;
    }

}
