package com.shinetech.mvp.network.UDP;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.scheme.base.BaseUDPTaskManager;
import com.shinetech.mvp.network.UDP.scheme.ResponseUDPTaskManager;
import com.shinetech.mvp.network.UDP.scheme.UDPTaskManager;

/**
 * Created by ljn on 2017-06-05.
 */
public class UDPRequestCtrl {

    private static UDPRequestCtrl mUDPRequestCtrl;

    /**
     * 是否使用有应答的UDP接口
     */
    private boolean hasResponseUDP =  MainApplication.isUserResponseUDP;

    private BaseUDPTaskManager mUDPTaskManager;


    private UDPRequestCtrl() {
        if(hasResponseUDP){
            mUDPTaskManager = new ResponseUDPTaskManager();
        }else{
            mUDPTaskManager = new UDPTaskManager();
        }
    }

    public static UDPRequestCtrl getInstance() {
        if (mUDPRequestCtrl == null){
            synchronized (MainApplication.getInstance()) {
                if (mUDPRequestCtrl == null) {
                    mUDPRequestCtrl = new UDPRequestCtrl();
                }
            }
        }
        return mUDPRequestCtrl;
    }

    public void request(BaseVo dataVo,  ResponseListener responseListener){
        mUDPTaskManager.request(dataVo,responseListener);
    }

    public void exit(){
        mUDPTaskManager.exit();
    }


}
