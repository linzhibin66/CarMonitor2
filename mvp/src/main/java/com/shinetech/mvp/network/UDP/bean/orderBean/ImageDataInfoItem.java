package com.shinetech.mvp.network.UDP.bean.orderBean;

import com.shinetech.mvp.network.UDP.bean.BaseVo;

import java.util.Arrays;

/**
 * Created by ljn on 2017-12-28.
 */

public class ImageDataInfoItem extends BaseVo {

    /**
     * 工单号
     */
    private String  orderNumber;

    /**
     * 图片类型
     * 1 报修图片（故障图片）; 2 维修图片  ; 3 评价图片
     */
    private byte imgType;

    /**
     *  照片编号;  从1开始
     */
    private byte imgId;

    /**
     * 当前照片总包数
     */
    private short packageTotal;

    /**
     * 当前照片包序号
     */
    private short packageNumber;

    /**
     * 照片数据
     */
    private byte[] data;

    @Override
    public Object[] getProperties() {
        return new Object[0];
    }

    @Override
    public void setProperties(Object[] properties) {
        this.orderNumber = parseString((byte[]) properties[0]);
        this.imgType = (byte) properties[1];
        this.imgId = (byte) properties[2];
        this.packageTotal = (short) properties[3];
        this.packageNumber = (short) properties[4];
        this.data = (byte[]) properties[5];

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING, BYTE, BYTE, SHORT, SHORT, DATA};
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public byte getImgType() {
        return imgType;
    }

    public void setImgType(byte imgType) {
        this.imgType = imgType;
    }

    public byte getImgId() {
        return imgId;
    }

    public void setImgId(byte imgId) {
        this.imgId = imgId;
    }

    public short getPackageTotal() {
        return packageTotal;
    }

    public void setPackageTotal(short packageTotal) {
        this.packageTotal = packageTotal;
    }

    public short getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(short packageNumber) {
        this.packageNumber = packageNumber;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageDataInfoItem that = (ImageDataInfoItem) o;

        if (getImgType() != that.getImgType()) return false;
        if (getImgId() != that.getImgId()) return false;
        if (getPackageTotal() != that.getPackageTotal()) return false;
        if (getPackageNumber() != that.getPackageNumber()) return false;
        if (getOrderNumber() != null ? !getOrderNumber().equals(that.getOrderNumber()) : that.getOrderNumber() != null)
            return false;
        return Arrays.equals(getData(), that.getData());

    }

    @Override
    public int hashCode() {
        int result = getOrderNumber() != null ? getOrderNumber().hashCode() : 0;
        result = 31 * result + (int) getImgType();
        result = 31 * result + (int) getImgId();
        result = 31 * result + (int) getPackageTotal();
        result = 31 * result + (int) getPackageNumber();
        result = 31 * result + Arrays.hashCode(getData());
        return result;
    }
}
