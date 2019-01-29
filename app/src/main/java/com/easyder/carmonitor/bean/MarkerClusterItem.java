package com.easyder.carmonitor.bean;

import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by Lenovo on 2017-11-06.
 */

public class MarkerClusterItem implements ClusterItem{

    private float alpha = 0.7f;

    private LatLng mPosition;

    private String title;

    private BitmapDescriptor markerIcon;

    private float rotate;

    public MarkerClusterItem(LatLng mPosition, String title) {
        this.mPosition = mPosition;
        this.title = title;
    }

    public void setAlpha(float alpha){
        this.alpha = alpha;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public float getAlpha() {
        return alpha;
    }

    @Override
    public float getRotate() {
        return rotate;
    }

    @Override
    public BitmapDescriptor getBitmapDescriptor() {
        return markerIcon;
    }

    public void setRotate(float rotate){
        this.rotate = rotate;
    }

    public void setMarkerIcon(BitmapDescriptor markerIcon){
        this.markerIcon = markerIcon;
    }
}
