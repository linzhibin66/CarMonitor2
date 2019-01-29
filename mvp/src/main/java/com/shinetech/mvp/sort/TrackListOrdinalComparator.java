package com.shinetech.mvp.sort;

import com.shinetech.mvp.network.UDP.listener.BaseCarHistoricalRouteItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by ljn on 2017-04-26.
 */
public class TrackListOrdinalComparator implements Comparator<BaseCarHistoricalRouteItem> {


    @Override
    public int compare(BaseCarHistoricalRouteItem lhs, BaseCarHistoricalRouteItem rhs) {
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
