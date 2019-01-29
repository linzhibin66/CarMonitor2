package com.shinetech.mvp.network.UDP.bean.orderBean;

import android.text.TextUtils;

import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2017-12-20.
 */

public class MaintenanceOrderProgress extends BaseVo{

    /**
     * 流程名称
     */
   private String progressName;

    /**
     * 处理人
     */
    private String disposePerson;

    /**
     * 处理时间
     */
    private String disposeTime;

    /**
     * 处理结果
     */
    private byte disposeResult;

    /**
     * 处理意见
     */
    private String disposeOpinion;

    public MaintenanceOrderProgress(String progressName, String disposePerson, String disposeTime, byte disposeResult, String disposeOpinion) {
        this.progressName = progressName;
        this.disposePerson = disposePerson;
        this.disposeTime = disposeTime;
        this.disposeResult = disposeResult;
        this.disposeOpinion = disposeOpinion;
    }

    public MaintenanceOrderProgress() {
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{progressName, disposePerson, disposeTime, disposeResult, disposeOpinion};
    }

    @Override
    public void setProperties(Object[] properties) {
        this.progressName = parseString((byte[]) properties[0]);
        this.disposePerson = parseString((byte[]) properties[1]);
        this.disposeTime = parseString((byte[]) properties[2]);
        this.disposeResult = (byte) properties[3];
        this.disposeOpinion = parseString((byte[]) properties[4]);
    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING, STRING, STRING, BYTE, STRING};
    }

    public String getProgressName() {
        return progressName;
    }

    public void setProgressName(String progressName) {
        this.progressName = progressName;
    }

    public String getDisposePerson() {
        return disposePerson;
    }

    public void setDisposePerson(String disposePerson) {
        this.disposePerson = disposePerson;
    }

    public String getDisposeTime() {
        return disposeTime;
    }

    public void setDisposeTime(String disposeTime) {
        this.disposeTime = disposeTime;
    }

    public byte getDisposeResult() {
        return disposeResult;
    }

    public void setDisposeResult(byte disposeResult) {
        this.disposeResult = disposeResult;
    }

    public String getDisposeOpinion() {
        return disposeOpinion;
    }

    public void setDisposeOpinion(String disposeOpinion) {
        this.disposeOpinion = disposeOpinion;
    }

    @Override
    public String toString() {
        return "MaintenanceOrderProgress{" +
                "progressName='" + progressName + '\'' +
                ", disposePerson='" + disposePerson + '\'' +
                ", disposeTime='" + disposeTime + '\'' +
                ", disposeResult=" + disposeResult +
                ", disposeOpinion='" + disposeOpinion + '\'' +
                '}';
    }
}
