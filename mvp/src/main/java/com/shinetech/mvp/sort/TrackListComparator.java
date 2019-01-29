package com.shinetech.mvp.sort;

import com.shinetech.mvp.network.UDP.bean.item.CarHistoricalRouteItem;
import com.shinetech.mvp.network.UDP.listener.BaseCarHistoricalRouteItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by ljn on 2017-04-26.
 */
public class TrackListComparator implements Comparator<BaseCarHistoricalRouteItem> {


    @Override
    public int compare(BaseCarHistoricalRouteItem lhs, BaseCarHistoricalRouteItem rhs) {
        String locationTime = lhs.getLocationTime();
        String locationTime2 = rhs.getLocationTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = dateFormat.parse(locationTime);
            Date date2 = dateFormat.parse(locationTime2);
            if(date1.getTime()>date2.getTime()){
                return 1;
            }else{
                return -1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
