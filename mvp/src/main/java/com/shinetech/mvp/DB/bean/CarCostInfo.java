package com.shinetech.mvp.DB.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-12-12.
 */

/*@Entity(indexes = {@Index(value = "id DESC", unique = true)})*/
public class CarCostInfo {

//    @Id(autoincrement = true)
    private Long id;

    /**
     * 车牌
     */
    private String plateNumber;

    /**
     * 车牌颜色
     */
    private short plateColor;

    /**
     * 车架号
     */
    private String vin;

    /**
     * 总欠费
     */
    private int allArrearage;

    /**
     * 欠费月份
     */
    private int arrearageMonth;

    /**
     * 明细数量
     */
    private int detailCount;

    /**
     * 明细
     */
    /*@ToMany(joinProperties = {
            @JoinProperty(name = "vin", referencedName = "vin")
    })*/
    List<CostDetails> costDetails;

    public CarCostInfo(String plateNumber, short plateColor, String vin,
                       int allArrearage, int arrearageMonth, int detailCount) {
        this.plateNumber = plateNumber;
        this.plateColor = plateColor;
        this.vin = vin;
        this.allArrearage = allArrearage;
        this.arrearageMonth = arrearageMonth;
        this.detailCount = detailCount;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlateNumber() {
        return this.plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public short getPlateColor() {
        return this.plateColor;
    }

    public void setPlateColor(short plateColor) {
        this.plateColor = plateColor;
    }

    public String getVin() {
        return this.vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public int getAllArrearage() {
        return this.allArrearage;
    }

    public void setAllArrearage(int allArrearage) {
        this.allArrearage = allArrearage;
    }

    public int getArrearageMonth() {
        return this.arrearageMonth;
    }

    public void setArrearageMonth(int arrearageMonth) {
        this.arrearageMonth = arrearageMonth;
    }

    public int getDetailCount() {
        return this.detailCount;
    }

    public void setDetailCount(int detailCount) {
        this.detailCount = detailCount;
    }


    public List<CostDetails> getCostDetails() {
        if (costDetails == null) {
            costDetails = new ArrayList();
        }
        return costDetails;
    }

    public void addCostDetails(CostDetails mCostDetails){

        if(costDetails == null){
            costDetails = new ArrayList();
        }

        synchronized(costDetails) {
            if (!costDetails.contains(mCostDetails)) {
                costDetails.add(mCostDetails);
            }
        }

    }
}
