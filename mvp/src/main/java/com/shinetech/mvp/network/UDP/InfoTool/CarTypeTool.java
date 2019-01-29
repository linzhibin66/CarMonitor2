package com.shinetech.mvp.network.UDP.InfoTool;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ljn on 2017-09-18.
 */

public class CarTypeTool {

    private static int PRIVATE_CAR = 0x1001;
    private static int TAXI_CAR = 0x1002;
    private static int BUS_CAR = 0x1003;
    private static int TRUCK_CAR = 0x1004;
    private static int BLUE_TAXT_CAR = 0x1005;
    private static int CDG_TRUCK_CAR = 0x1006;

    private static  Map<Integer,BitmapDescriptor> iconMap = new HashMap<>();

    //地图中车辆类型图标
    public static BitmapDescriptor getCarIcon(String carType){

        Context context = MainApplication.getInstance();

        //私家车
        if(!TextUtils.isEmpty(carType) && carType.equals(context.getString(R.string.private_car))){

            BitmapDescriptor privateCar = iconMap.get(PRIVATE_CAR);
            if(privateCar == null){
                privateCar = BitmapDescriptorFactory.fromResource(R.mipmap.car_private);
                iconMap.put(PRIVATE_CAR, privateCar);
            }

            return privateCar;

            //的士
        }else if(!TextUtils.isEmpty(carType) && (carType.equals(context.getString(R.string.public_taxi)) ||
                carType.equals(context.getString(R.string.prefecture_chartered)) ||
                carType.equals(context.getString(R.string.instructional_car))
        )){

            BitmapDescriptor taxiCar = iconMap.get(TAXI_CAR);
            if(taxiCar == null){
                taxiCar = BitmapDescriptorFactory.fromResource(R.mipmap.car_taxi);
                iconMap.put(TAXI_CAR, taxiCar);
            }

            return taxiCar;

        }else if(!TextUtils.isEmpty(carType) && (carType.equals(context.getString(R.string.taxi_car)) ||
                carType.equals(context.getString(R.string.law_enforcement_vehicles))

        )){

            BitmapDescriptor blueTaxiCar = iconMap.get(BLUE_TAXT_CAR);
            if(blueTaxiCar == null){
                blueTaxiCar = BitmapDescriptorFactory.fromResource(R.mipmap.car_bluetaxi);
                iconMap.put(BLUE_TAXT_CAR, blueTaxiCar);
            }

            return blueTaxiCar;

            //巴士
        }else if(!TextUtils.isEmpty(carType) && (carType.equals(context.getString(R.string.provincial_bus)) ||
                carType.equals(context.getString(R.string.provincial_chartered)) ||
                carType.equals(context.getString(R.string.intercity_chartered)) ||
                carType.equals(context.getString(R.string.prefecture_bus)) ||
                carType.equals(context.getString(R.string.school_bus)) ||
                carType.equals(context.getString(R.string.public_passenger_transport)) ||
                carType.equals(context.getString(R.string.intercity_bus)) ||
                carType.equals(context.getString(R.string.intracounty_chartered)) ||
                carType.equals(context.getString(R.string.intracounty_bus)) ||
                carType.equals(context.getString(R.string.bus_rapid_transit))
        )){


            BitmapDescriptor busCar = iconMap.get(BUS_CAR);
            if(busCar == null){
                busCar = BitmapDescriptorFactory.fromResource(R.mipmap.car_bus);
//                busCar = BitmapDescriptorFactory.fromResource(R.mipmap.car_truck);
                iconMap.put(BUS_CAR, busCar);
            }

            return busCar;

            //货车
        }else if(!TextUtils.isEmpty(carType) && (carType.equals(context.getString(R.string.ordinary_goods_transport)) ||
                        carType.equals(context.getString(R.string.test_car)) ||
                        carType.equals(context.getString(R.string.bulk_truck)) ||
                        carType.equals(context.getString(R.string.truck)) ||
                        carType.equals(context.getString(R.string.machineshop_truck))
                )){

            BitmapDescriptor truckCar = iconMap.get(TRUCK_CAR);
            if(truckCar == null){
                truckCar = BitmapDescriptorFactory.fromResource(R.mipmap.car_truck);
                iconMap.put(TRUCK_CAR, truckCar);
            }

            return truckCar;

        }else if(!TextUtils.isEmpty(carType) && (carType.equals(context.getString(R.string.dangerous_goods_transport)))) {

            BitmapDescriptor cdgTruckCar = iconMap.get(CDG_TRUCK_CAR);
            if(cdgTruckCar == null){
                cdgTruckCar = BitmapDescriptorFactory.fromResource(R.mipmap.car_cdg_truck);
                iconMap.put(CDG_TRUCK_CAR, cdgTruckCar);
            }

            return cdgTruckCar;

        }

        return BitmapDescriptorFactory.fromResource(R.mipmap.car_private);

    }

    //列表中的车辆类型图标
    public static int getListCarIcon(String carType){


        Context context = MainApplication.getInstance();

        //私家车
        if(!TextUtils.isEmpty(carType) && carType.equals(context.getString(R.string.private_car))){

            return R.mipmap.icon_allcar_car;

            //的士
        }else if(!TextUtils.isEmpty(carType) && (carType.equals(context.getString(R.string.public_taxi)) ||
                carType.equals(context.getString(R.string.prefecture_chartered)) ||
                carType.equals(context.getString(R.string.instructional_car)) ||
                carType.equals(context.getString(R.string.taxi_car)) ||
                carType.equals(context.getString(R.string.law_enforcement_vehicles))
        )){

            return R.mipmap.icon_taxi;

            //巴士
        }else if(!TextUtils.isEmpty(carType) && (carType.equals(context.getString(R.string.provincial_bus)) ||
                carType.equals(context.getString(R.string.provincial_chartered)) ||
                carType.equals(context.getString(R.string.intercity_chartered)) ||
                carType.equals(context.getString(R.string.prefecture_bus)) ||
                carType.equals(context.getString(R.string.school_bus)) ||
                carType.equals(context.getString(R.string.public_passenger_transport)) ||
                carType.equals(context.getString(R.string.intercity_bus)) ||
                carType.equals(context.getString(R.string.intracounty_chartered)) ||
                carType.equals(context.getString(R.string.intracounty_bus)) ||
                carType.equals(context.getString(R.string.bus_rapid_transit))
        )){

            return R.mipmap.icon_bus;

            //货车
        }else if(!TextUtils.isEmpty(carType) && (carType.equals(context.getString(R.string.dangerous_goods_transport)) ||
                carType.equals(context.getString(R.string.ordinary_goods_transport)) ||
                carType.equals(context.getString(R.string.test_car)) ||
                carType.equals(context.getString(R.string.bulk_truck)) ||
                carType.equals(context.getString(R.string.truck)) ||
                carType.equals(context.getString(R.string.machineshop_truck))
        )) {

            return R.mipmap.icon_truck;
        }

        return R.mipmap.icon_allcar_car;


    }

    public static BitmapDescriptor getDefCarIcon(){
        return BitmapDescriptorFactory.fromResource(R.mipmap.car_private);
    }

    //车辆信息中的车辆类型图标
    public static int getMarkerInfoCarIcon(String carType){


        Context context = MainApplication.getInstance();

        //私家车
        if(!TextUtils.isEmpty(carType) && carType.equals(context.getString(R.string.private_car))){

            return R.mipmap.marker_info_car;

            //的士
        }else if(!TextUtils.isEmpty(carType) && (carType.equals(context.getString(R.string.public_taxi)) ||
                carType.equals(context.getString(R.string.prefecture_chartered)) ||
                carType.equals(context.getString(R.string.instructional_car)) ||
                carType.equals(context.getString(R.string.taxi_car)) ||
                carType.equals(context.getString(R.string.law_enforcement_vehicles))
        )){

            return R.mipmap.marker_info_taxi;

            //巴士
        }else if(!TextUtils.isEmpty(carType) && (carType.equals(context.getString(R.string.provincial_bus)) ||
                carType.equals(context.getString(R.string.provincial_chartered)) ||
                carType.equals(context.getString(R.string.intercity_chartered)) ||
                carType.equals(context.getString(R.string.prefecture_bus)) ||
                carType.equals(context.getString(R.string.school_bus)) ||
                carType.equals(context.getString(R.string.public_passenger_transport)) ||
                carType.equals(context.getString(R.string.intercity_bus)) ||
                carType.equals(context.getString(R.string.intracounty_chartered)) ||
                carType.equals(context.getString(R.string.intracounty_bus)) ||
                carType.equals(context.getString(R.string.bus_rapid_transit))
        )){

            return R.mipmap.marker_info_bus;

            //货车
        }else if(!TextUtils.isEmpty(carType) && (carType.equals(context.getString(R.string.dangerous_goods_transport)) ||
                carType.equals(context.getString(R.string.ordinary_goods_transport)) ||
                carType.equals(context.getString(R.string.test_car)) ||
                carType.equals(context.getString(R.string.bulk_truck)) ||
                carType.equals(context.getString(R.string.truck)) ||
                carType.equals(context.getString(R.string.machineshop_truck))
        )) {

            return R.mipmap.marker_info_truck;
        }

        return R.mipmap.marker_info_car;


    }

}
