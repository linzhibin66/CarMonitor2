package com.shinetech.mvp.utils;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/2/23.
 */
public class BaiduMapUtils {


    public static int[] getMapViewScope(BaiduMap mBaiduMap){
        MapStatus mapStatus = mBaiduMap.getMapStatus();

        LatLngBounds bound = mapStatus.bound;
        LatLng northeast = bound.northeast;
        LatLng southwest = bound.southwest;

        int maxLng;
        int minlng;
        int maxlat;
        int minlat;

        if(northeast.latitude>southwest.latitude){
            maxlat = (int)(northeast.latitude * 1E6);
            minlat = (int)(southwest.latitude * 1E6);
        }else{
            maxlat = (int)(southwest.latitude * 1E6);
            minlat = (int)(northeast.latitude * 1E6);
        }

        if(northeast.longitude>southwest.longitude){
            maxLng = (int)(northeast.longitude * 1E6);
            minlng = (int)(southwest.longitude * 1E6);
        }else{
            maxLng = (int)(southwest.longitude * 1E6);
            minlng = (int)(northeast.longitude * 1E6);
        }

        if(maxLng-minlng<3000){
            maxLng+=1500;
            minlng-=1000;
        }
        if(maxlat-minlat<3000){
            maxlat+=1000;
            minlat-=1000;
        }

        maxLng = maxLng - 10000;
        minlng = minlng - 12945;
        maxlat = maxlat - 3000;
        minlat = minlat - 3000;

        int[] mapScope = {minlng, minlat, maxLng, maxlat};

        return mapScope;
    }

    public static int[] getMapViewScope(Activity activity,BaiduMap mBaiduMap){
        Display mDisplay = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        mDisplay.getSize(point);
//      得到界面最大的坐标点
        int max_x = point.x;
        int max_y = point.y;

        if(mBaiduMap == null){
            return new int[]{};
        }

        //将四个角的点转成地图坐标，并获取最大最小坐标范围
        Projection projection = mBaiduMap.getProjection();
        if(projection == null){
            return new int[]{};
        }


            List<LatLng> list = new ArrayList<>();

            LatLng latlng0 = projection.fromScreenLocation(new Point(0, 0));
            list.add(latlng0);

            LatLng latlng2 = projection.fromScreenLocation(new Point(max_x, 0));
            list.add(latlng2);

            LatLng latlng3 = projection.fromScreenLocation(new Point(0, max_y));
            list.add(latlng3);

            LatLng latlng4 = projection.fromScreenLocation(new Point(max_x, max_y));
            list.add(latlng4);

            double maxLng = latlng0.longitude;
            double minlng = latlng0.longitude;
            double maxlat = latlng0.latitude;
            double minlat = latlng0.latitude;


            for(LatLng latlng : list){

                if(maxLng<latlng.longitude){
                    maxLng = latlng.longitude;
                }

                if(maxlat<latlng.latitude){
                    maxlat = latlng.latitude;
                }

                if(minlng>latlng.longitude){
                    minlng = latlng.longitude;
                }

                if(minlat>latlng.latitude){
                    minlat = latlng.latitude;
                }

            }

        int[] mapScope = {(int)(minlng*1E6), (int)(minlat*1E6), (int)(maxLng*1E6), (int)(maxlat*1E6)};

        return mapScope;

    }
}
