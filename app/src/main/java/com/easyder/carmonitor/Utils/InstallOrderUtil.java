package com.easyder.carmonitor.Utils;

import android.text.TextUtils;

import com.shinetech.mvp.DB.bean.InstallTerminalnfo;

/**
 * Created by Lenovo on 2018-02-13.
 */

public class InstallOrderUtil {


    public static String getTitle(InstallTerminalnfo installTerminalnfo){

        String plateNumber = installTerminalnfo.getPlateNumber();
        if(!TextUtils.isEmpty(plateNumber)){
            return plateNumber;
        }

        String vin = installTerminalnfo.getVin();
        if(!TextUtils.isEmpty(vin)){
            return vin;
        }

        String terminalID = installTerminalnfo.getTerminalID();
        if(!TextUtils.isEmpty(terminalID)){
            return terminalID;
        }

        String simCard = installTerminalnfo.getSimCard();
        if(!TextUtils.isEmpty(simCard)){
            return simCard;
        }


        return "未知";

    }


}
