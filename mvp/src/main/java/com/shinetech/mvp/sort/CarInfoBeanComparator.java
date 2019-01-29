package com.shinetech.mvp.sort;

import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by Lenovo on 2018-03-26.
 */

public class CarInfoBeanComparator implements Comparator<CarInfoBean> {

    private Map<String,CarInfoBean> selectCar;

    public CarInfoBeanComparator(Map<String, CarInfoBean> selectCar) {
        this.selectCar = selectCar;
    }

    @Override
    public int compare(CarInfoBean o1, CarInfoBean o2) {
        String plateNumber1 = o1.getPlateNumber();
        String plateNumber2 = o2.getPlateNumber();

        if(selectCar != null){
            CarInfoBean carInfoBean1 = selectCar.get(plateNumber1);
            boolean selectCarInfo1 = carInfoBean1 == null?false:true;

            CarInfoBean carInfoBean2 = selectCar.get(plateNumber2);
            boolean selectCarInfo2 = carInfoBean2 == null?false:true;

            if((selectCarInfo1 || selectCarInfo2) && !(selectCarInfo1 && selectCarInfo2)){

                if(selectCarInfo1){
                    return -1;
                }else{
                    return 1;
                }
            }

        }

        return plateNumber1.compareTo(plateNumber2);

    }
}
