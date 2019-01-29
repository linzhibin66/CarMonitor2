package com.shinetech.mvp.offlineMap.bean;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.shinetech.mvp.offlineMap.OffLineBaiduMapUtils;

/**
 * Created by ljn on 2016/11/14.
 */
public abstract class BaseOfflineItem {

    public BaseOfflineItem(MKOLUpdateElement updateInfo) {

        if((updateInfo != null) && (updateInfo.status!= MKOLUpdateElement.FINISHED) ){
            int cityID = updateInfo.cityID;
            OffLineBaiduMapUtils.getInstance().registUpdateListener(cityID,this);
        }
    }

    public abstract void updateView();


}
