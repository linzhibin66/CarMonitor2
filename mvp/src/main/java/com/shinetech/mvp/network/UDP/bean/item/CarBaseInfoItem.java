package com.shinetech.mvp.network.UDP.bean.item;

/**
 * Created by ljn on 2017/2/9.
 */
public class CarBaseInfoItem {

    private String infoName;

    private String infoContent;

    public CarBaseInfoItem(String infoName, String infoContent) {
        this.infoName = infoName;
        this.infoContent = infoContent;
    }

    public CarBaseInfoItem() {
    }

    public String getInfoName() {
        return infoName;
    }

    public void setInfoName(String infoName) {
        this.infoName = infoName;
    }

    public String getInfoContent() {
        return infoContent;
    }

    public void setInfoContent(String infoContent) {
        this.infoContent = infoContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CarBaseInfoItem that = (CarBaseInfoItem) o;

        if (!getInfoName().equals(that.getInfoName())) return false;
        return getInfoContent().equals(that.getInfoContent());

    }

    @Override
    public String toString() {
        return "CarBaseInfoItem{" +
                "infoName='" + infoName + '\'' +
                ", infoContent='" + infoContent + '\'' +
                '}';
    }
}
