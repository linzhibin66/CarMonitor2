package com.shinetech.mvp.offlineMap;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.interfaces.OffLineMapListener;
import com.shinetech.mvp.interfaces.OffLineStatusListener;
import com.shinetech.mvp.offlineMap.bean.BaseOfflineItem;
import com.shinetech.mvp.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ljn on 2016/11/10.
 */
public class OffLineBaiduMapUtils implements MKOfflineMapListener {

    private MKOfflineMap mOffline = null;

    private static OffLineBaiduMapUtils mOffLineBaiduMapUtils;

    /**
     * 任务开始 监听回调
     */
    private final int ONSTART = 1;

    /**
     * 有新任务
     */
    private final int NEWTASK = 2;

    /**
     * 任务停止 监听回调
     */
    private final int ONSTOP = 3;

    /**
     * 任务从新开始 监听回调
     */
    private final int ONRESTART = 4;

    /**
     * 任务移除 监听回调
     */
    private final int ONREMOVE = 5;

    /**
     * 任务完成 监听回调
     */
    private final int ONFINISH = 6;

    /**
     * 下载进度监听
     */
    private Map<Integer,BaseOfflineItem> listeners = new HashMap<Integer,BaseOfflineItem>();

    /**
     * 下载任务监听
     */
    private OffLineMapListener mOffLineMapListener;

    /**
     * 任务状态监听
     */
    private Map<Integer,List<OffLineStatusListener>> statusListeners = new HashMap<Integer,List<OffLineStatusListener>>();

    private boolean isDebug = false && LogUtils.isDebug;

    private  Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch(what){
                case ONSTART:
                    int cityid = (int) msg.obj;
                    synchronized (OffLineBaiduMapUtils.this){
                        //回调开始状态监听
                        List<OffLineStatusListener> offLineStatusListeners = statusListeners.get(cityid);
                        if (offLineStatusListeners!=null && offLineStatusListeners.size()>0){
                            for(OffLineStatusListener sl : offLineStatusListeners){
                                sl.onStart();
                            }
                        }
                    }

                break;
                case ONRESTART:
                    int cityId = (int) msg.obj;
                    synchronized (OffLineBaiduMapUtils.this){
                        //回调开始状态监听
                        List<OffLineStatusListener> olsl = statusListeners.get(cityId);
                        if (olsl!=null && olsl.size()>0){
                            for(OffLineStatusListener sl : olsl){
                                sl.onReStart();
                            }
                        }
                    }

                    break;
                case ONSTOP:
                    int stop_cityId = (int) msg.obj;
                    synchronized (OffLineBaiduMapUtils.this){
                        //回调停止状态监听
                        List<OffLineStatusListener> stopListener = statusListeners.get(stop_cityId);
                        if (stopListener!=null && stopListener.size()>0){
                            for(OffLineStatusListener sl : stopListener){
                                sl.onStop();
                            }
                        }
                    }


                    break;
                case ONREMOVE:
                    int remove_cityId = (int) msg.obj;
                    synchronized (OffLineBaiduMapUtils.this){
                        //回调停止状态监听
                        List<OffLineStatusListener> reomveListener = statusListeners.get(remove_cityId);
                        if (reomveListener!=null && reomveListener.size()>0){
                            if(isDebug)LogUtils.debug("reomveListener size = " +reomveListener.size());
                            Iterator<OffLineStatusListener> iterator = reomveListener.iterator();
                            int coumt = 0;
                            while(iterator.hasNext()){
                                if(isDebug)LogUtils.debug("exe coumt = "+ coumt +" listener");
                                OffLineStatusListener osl = iterator.next();
                                boolean isRemove = osl.onRemove();
                                if(isRemove){
                                    iterator.remove();
                                }
                            }
                        }

                    }


                    break;
                case ONFINISH:
                    int finish_cityId = (int) msg.obj;
                    synchronized (OffLineBaiduMapUtils.this){
                        //回调停止状态监听
                        List<OffLineStatusListener> finishListener = statusListeners.get(finish_cityId);
                        if (finishListener!=null && finishListener.size()>0){
                            if(isDebug)LogUtils.debug("finishListener size = " +finishListener.size());
                            Iterator<OffLineStatusListener> iterator = finishListener.iterator();
                            int coumt = 0;
                            while(iterator.hasNext()){
                                if(isDebug)LogUtils.debug("exe coumt = "+ coumt +" listener");
                                OffLineStatusListener osl = iterator.next();
                                boolean isremove = osl.onFinish();
                                if(isremove){
                                    iterator.remove();
                                }
                                coumt++;
                            }
                        }
                    }
                    break;
                case NEWTASK:

                    if(mOffLineMapListener!=null){
                        mOffLineMapListener.onNewOffLineTask();
                    }

                    break;
                default:
                break;

            }

        }
    };

    private OffLineBaiduMapUtils() {
        initOffLineMapTools();
    }

    public static OffLineBaiduMapUtils getInstance(){
        if(mOffLineBaiduMapUtils == null) {
            mOffLineBaiduMapUtils = new OffLineBaiduMapUtils();
        }
        return mOffLineBaiduMapUtils;
    }

    private void initOffLineMapTools(){
        mOffline = new MKOfflineMap();
        mOffline.init(this);
    }

    /**
     * 注册下载进度监听器
     * @param cityID
     * @param baseOfflineItem
     */
    public void registUpdateListener(int cityID,BaseOfflineItem baseOfflineItem){
        synchronized (this){

            listeners.put(cityID, baseOfflineItem);

        }
    }

    /**
     * 注销下载进度监听器
     * @param cityID
     */
    public void unRegistUpdateListener(int cityID){
        synchronized (this){

            listeners.remove(cityID);

        }
    }

    /**
     * 清除所有下载进度监听
     */
    public void cleanAllUpdateListener(){
        synchronized (this){

            listeners.clear();

        }
    }

    /**
     * 新任务监听
     * @param mOffLineMapListener
     */
    public void setOffLineListener(OffLineMapListener mOffLineMapListener){
        this.mOffLineMapListener = mOffLineMapListener;

    }

    public void addStatusListener(int cityID,OffLineStatusListener statuslistener){
        synchronized (this){
            List<OffLineStatusListener> offLineStatusListeners = statusListeners.get(cityID);
            if(offLineStatusListeners==null){
                offLineStatusListeners = new ArrayList<>();
                offLineStatusListeners.add(statuslistener);
            }else{
                boolean contains = offLineStatusListeners.contains(statuslistener);
                if(!contains){
                    offLineStatusListeners.add(statuslistener);
                }
            }

            statusListeners.put(cityID, offLineStatusListeners);
        }

    }

    public void removeStatusListener(int cityID,OffLineStatusListener statuslistener){
        synchronized (this){

            List<OffLineStatusListener> offLineStatusListeners = statusListeners.get(cityID);
            if(offLineStatusListeners==null){
                return ;
            }

            offLineStatusListeners.remove(statuslistener);

            if(offLineStatusListeners.size()==0){
                statusListeners.remove(cityID);
            }else{
                statusListeners.put(cityID,offLineStatusListeners);
            }
        }
    }

    /**
     * 清除全部状态监听
     */
    public void cleanAllStatusListener(){
        synchronized (this){
            statusListeners.clear();
        }
    }

    /**
     * 获取热闹城市列表
     * @return
     */
    public List<MKOLSearchRecord> getHotCityList(){
        return mOffline.getHotCityList();
    }

    /**
     * 获取所有支持离线地图的城市
     * @return
     */
    public List<MKOLSearchRecord> getOfflineCityList(){
        return mOffline.getOfflineCityList();
    }

    /**
     * 获取已下过的离线地图信息
     * @return
     */
    public List<MKOLUpdateElement> getAllUpdateInfo(){
        if(mOffline != null) {
            return mOffline.getAllUpdateInfo();
        }else{
            return null;
        }
    }

    public MKOLUpdateElement getUpdateInfo(int cityID){
        return mOffline.getUpdateInfo(cityID);
    }

    /**
     * 搜索离线城市
     * @param city
     * @return
     */
    public List<MKOLSearchRecord> getSearchCity(String city){
        return mOffline.searchCity(city);
    }

    public void downloadAll(){

        List<MKOLUpdateElement> allUpdateInfo = getAllUpdateInfo();

        if(allUpdateInfo == null){
            return;
        }

        for(MKOLUpdateElement mMKOLUpdateElement : allUpdateInfo){
            if((mMKOLUpdateElement.ratio < 100) && mMKOLUpdateElement.status != MKOLUpdateElement.DOWNLOADING){
                start(mMKOLUpdateElement.cityID);
            }
        }
    }

    public void stopAll(){
        List<MKOLUpdateElement> allUpdateInfo = getAllUpdateInfo();

        if(allUpdateInfo == null){
            return;
        }

        for(MKOLUpdateElement mMKOLUpdateElement : allUpdateInfo){
            if((mMKOLUpdateElement.ratio < 100) && mMKOLUpdateElement.status != MKOLUpdateElement.FINISHED){
                stop(mMKOLUpdateElement.cityID);
            }
        }
    }

    /**
     * 开始下载
     * @param cityid
     */
    public boolean start(int cityid){

        boolean neetToCall = false;

        MKOLUpdateElement update = mOffline.getUpdateInfo(cityid);

        if(update == null && mOffLineMapListener!=null){
            neetToCall = true;
        }

        boolean succesToStart = mOffline.start(cityid);

        if(succesToStart && neetToCall){

            Message message = Message.obtain();
            message.what = NEWTASK;
            mHandler.sendMessage(message);

        }

        if (succesToStart && update==null){
            Message message = Message.obtain();
            message.what = ONSTART;
            message.obj = cityid;
            mHandler.sendMessage(message);
        }else if(update!=null && succesToStart){
            Message message = Message.obtain();
            message.what = ONRESTART;
            message.obj = cityid;
            mHandler.sendMessage(message);
        }

        return succesToStart;
    }
    /**
     * 暂停下载
     *
     * @param cityid
     */
    public boolean stop(int cityid) {

        boolean pause = mOffline.pause(cityid);
        if (pause){
            Message message = Message.obtain();
            message.what = ONSTOP;
            message.obj = cityid;
            mHandler.sendMessage(message);
        }
        return pause;
    }

    /**
     * 删除离线地图
     *
     * @param cityid
     */
    public boolean remove(int cityid) {

        boolean remove = mOffline.remove(cityid);

        synchronized (this){

            listeners.remove(cityid);

        }

        if(remove){
            Message message = Message.obtain();
            message.what = ONREMOVE;
            message.obj = cityid;
            mHandler.sendMessage(message);
        }
        return remove;
    }

    /**
     * 大小转换
     * @param size
     * @return
     */
    public String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    /**
     * 退出时，销毁离线地图模块
     */
    public void onDestroy() {
        mOffline.destroy();

    }

    public void clean(){
        cleanAllUpdateListener();
        cleanAllStatusListener();
        if(mOffLineMapListener!=null){
            mOffLineMapListener = null;
        }
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {

        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                // 处理下载进度更新提示
                if (update != null) {
//                    LogUtils.debug("cityID : "+update.cityID + "  cityName : "+update.cityName+"  ratio :"+update.ratio+"  serverSize : "+update.serversize +"  download size :"+ update.size );
                    if(isDebug)LogUtils.debug("cityID : "+update.cityID + "  cityName : "+update.cityName+"update status : " + update.status);
                }

                //TODO  下载进度监听
                BaseOfflineItem baseOfflineItem = listeners.get(state);

                if(baseOfflineItem!=null){
                    baseOfflineItem.updateView();

                }

                if(isDebug)LogUtils.debug("update status : " + update.status);
                if(update.ratio == 100){

                    String offlineMaps = MainApplication.getPreferences().getString(MainApplication.OFFLINEMAPS, "");
                    if(!offlineMaps.contains(update.cityName)){
                        offlineMaps = offlineMaps + "&"+update.cityName;
                    }

                    MainApplication.getPreferences().edit().putString(MainApplication.OFFLINEMAPS, offlineMaps).commit();

                    synchronized (OffLineBaiduMapUtils.this){

                        unRegistUpdateListener(state);

                    }

                    Message message = Message.obtain();
                    message.what = ONFINISH;
                    message.obj = state;
                    mHandler.sendMessage(message);

                }


            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Log.d("OfflineDemo", String.format("add offlinemap coumt:%d", state));

                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);
                if(mOffLineMapListener!=null){
                    mOffLineMapListener.onVersionUpdate();
                }

                break;
            default:
                break;
        }

    }
}
