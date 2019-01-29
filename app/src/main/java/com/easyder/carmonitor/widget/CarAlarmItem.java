package com.easyder.carmonitor.widget;

/**
 * Created by ljn on 2017-04-11.
 */
public class CarAlarmItem{

    public final static int ALARM = 0x1;
    public final static int EXCEPTION = 0x2;

    private int type;

    private String attribute;

    public CarAlarmItem(int type, String attribute) {
        this.type = type;
        this.attribute = attribute;
    }

    public CarAlarmItem() {
    }

    public int getType() {
        return type;
    }

    public String getAttribute() {
        return attribute;
    }
}
