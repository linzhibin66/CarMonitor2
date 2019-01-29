package com.shinetech.mvp.interfaces;

/**
 * Created by Lenovo on 2016/11/14.
 */
public interface OffLineMapListener {

    /**
     * 离线地图有新版本
     */
    void onVersionUpdate();

    /**
     * 有新下载任务
     */
    void  onNewOffLineTask();
}
