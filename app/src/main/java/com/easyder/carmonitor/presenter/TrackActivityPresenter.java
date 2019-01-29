package com.easyder.carmonitor.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.baidu.mapapi.common.SysOSUtil;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.bean.TrackLogItem;
import com.easyder.carmonitor.interfaces.TrackDataListener;
import com.easyder.carmonitor.interfaces.TrackPlayListener;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.CarHistoricalRouteBean;
import com.shinetech.mvp.network.UDP.bean.item.CarHistoricalRouteItem;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.listener.BaseCarHistoricalRouteItem;
import com.shinetech.mvp.network.UDP.presenter.TrackPresenter;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarHistoricalRouteBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarHistoricalRouteItem;
import com.shinetech.mvp.sort.TrackListComparator;
import com.shinetech.mvp.sort.TrackListOrdinalComparator;
import com.shinetech.mvp.utils.CoordinateUtil;
import com.shinetech.mvp.utils.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ljn on 2017-04-25.
 */
public class TrackActivityPresenter extends TrackPresenter{

    private List<BaseCarHistoricalRouteItem> historiCarInfoList = new ArrayList<>();

    private List<LatLng> historiLatLngList = new ArrayList<>();

    private int TIME_INTERVAL = 1000;

    private final int TRACK_PLAY = 0;

    private final int TRACK_STOP = 1;

    private final int TRACK_RESET = 2;

    private int currentIndex = 0;

    private int currentStatus = TRACK_STOP;

    private Marker carMarker;

    private TrackPlayListener listener;

    private Thread mThread;

    private MoveLoopTask moveLoopTask;

    private double trackPlayOldAngle = 0;

    private TrackDataListener mTrackDataListener;

    private final boolean isDebug = false && LogUtils.isDebug;

    private int totalMileage = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;

            switch (what){
                case TRACK_PLAY:
                    //开始播放轨迹回掉
                    if(currentStatus != TRACK_PLAY && listener != null){
                        listener.onPlay();
                    }

                    currentStatus = TRACK_PLAY;
                    //播放完成，停止播放，设置会开始位置
                    if(currentIndex>= historiLatLngList.size()){
                       sendEmptyMessage(TRACK_STOP);
                        currentIndex = 0;
                        return;
                    }

                    //跟新车辆位置，并播放滑动效果
                    final LatLng latLng = historiLatLngList.get(currentIndex);

                    if(carMarker != null) {

                        carMarker.setPosition(latLng);


//                    TODO start move loop
                        if(historiLatLngList.size()> currentIndex+1) {

                            final LatLng endlatLng = historiLatLngList.get(currentIndex + 1);

                            //停止上一段滑动效果
                            if(moveLoopTask != null) {
                                moveLoopTask.setExit(true);
                            }

                            //创建当前滑动效果任务
                            moveLoopTask = new MoveLoopTask();
                            moveLoopTask.setLatLng(latLng,endlatLng);

                            try {
                                //中断上一段滑动效果线程
                                if (mThread != null) {
                                    mThread.interrupt();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            //创建当前滑动效果线程
                            mThread = new Thread(moveLoopTask);

                            mThread.start();

                        }

                    }

                    if(listener != null){
                        BaseCarHistoricalRouteItem carHistoricalRouteItem = null;

                        if(historiCarInfoList.size()>currentIndex) {
                            carHistoricalRouteItem = historiCarInfoList.get(currentIndex);
                        }

                        listener.onTrackChangeProgress(historiLatLngList.size(), currentIndex, carHistoricalRouteItem);

                    }


                    currentIndex++;
                    sendEmptyMessageDelayed(TRACK_PLAY,TIME_INTERVAL);
                    break;
                case TRACK_STOP:
                    currentStatus = TRACK_STOP;
                    mHandler.removeMessages(TRACK_PLAY);
                    if(listener != null){
                        listener.onTrackStop();
                    }
                    break;
                case TRACK_RESET:
                    currentStatus = TRACK_RESET;
                    currentIndex = 0;
                    if(listener != null){
                        listener.onTrackReset();
                    }
                    break;
            }
        }
    };

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                if(isDebug)System.out.println("Double.MAX_VALUE  Angle = "+0);
                return 0;
            } else {
                if(isDebug)System.out.println("Double.MAX_VALUE  Angle = "+180);
                return 180;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint, double oldAngle) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            return oldAngle;
            /*if (toPoint.latitude > fromPoint.latitude) {
                System.out.println("Double.MAX_VALUE  Angle = "+0);
                return 0;
            } else {
                System.out.println("Double.MAX_VALUE  Angle = "+180);
                return 180;
            }*/
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    /**
     * 算取斜率
     */
    private double getSlope(int startIndex) {
        if ((startIndex + 1) >= historiLatLngList.size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = historiLatLngList.get(startIndex);
        LatLng endPoint = historiLatLngList.get(startIndex + 1);
        return getSlope(startPoint, endPoint);
    }

    /**
     * 算斜率
     */
    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
        return slope;

    }

    /**
     * 根据点和斜率算取截距
     */
    private double getInterception(double slope, LatLng point) {

        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    private static final double DISTANCE = 0.0001;
    /**
     * 计算x方向每次移动的距离
     */
    private double getXMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    @Override
    public void onSuccess(LoadResult successResult) {

        BaseVo dataVo = successResult.getDataVo();
        if(dataVo!=null && dataVo instanceof CarHistoricalRouteBean) {
            CarHistoricalRouteBean mCarHistoricalRouteBean = (CarHistoricalRouteBean) dataVo;
            boolean isend = mCarHistoricalRouteBean.isend();

            if (!isend){
                return;
            }

            if(isDebug) {
                mCarHistoricalRouteBean.toString();
            }

            List<CarHistoricalRouteItem> historiCarInfo = mCarHistoricalRouteBean.getHistoriCarInfo();

            // 重新设置车辆信息列表
            historiCarInfoList.clear();
            historiCarInfoList.addAll(historiCarInfo);

            //对车辆信息列表按时间排序
            TrackListComparator mTrackListComparator = new TrackListComparator();
            Collections.sort(historiCarInfoList, mTrackListComparator);

            updateLatLngList();



        }else if(dataVo!=null && dataVo instanceof ResponseCarHistoricalRouteBean){

            ResponseCarHistoricalRouteBean mResponseCarHistoricalRouteBean = (ResponseCarHistoricalRouteBean) dataVo;

            List<ResponseCarHistoricalRouteItem> resultHistoricalInfo = mResponseCarHistoricalRouteBean.getResultHistoricalInfo();

            // 重新设置车辆信息列表
            historiCarInfoList.clear();
            historiCarInfoList.addAll(resultHistoricalInfo);

            TrackListOrdinalComparator trackListOrdinalComparator = new TrackListOrdinalComparator();
            Collections.sort(historiCarInfoList, trackListOrdinalComparator);

            if(resultHistoricalInfo.size()>0) {
                ResponseCarHistoricalRouteItem responseCarHistoricalRouteItem = (ResponseCarHistoricalRouteItem) historiCarInfoList.get(historiCarInfoList.size() - 1);
                int relativelyMileage = responseCarHistoricalRouteItem.getRelativelyMileage();
                totalMileage = relativelyMileage;
            }

            updateLatLngList();

        }

        super.onSuccess(successResult);
    }

    private void updateLatLngList(){
        //重新赋值轨迹线路坐标列表
        historiLatLngList.clear();
        for(BaseCarHistoricalRouteItem item : historiCarInfoList){
            //根据坐标解析地址
            double lng = item.getLng() / 1E6;
            double lat = item.getLat() / 1E6;

            LatLng latLng = CoordinateUtil.gps84_to_bd09(lat, lng);
            //System.out.println(item.toString());
            historiLatLngList.add(latLng);

        }
    }


    public void getCarHistory(String platenumber, String startTime, String endTime, TrackDataListener mTrackDataListener) {
        this.mTrackDataListener = mTrackDataListener;
        getCarHistory(platenumber, startTime, endTime);
    }

    @Override
    public void onError(LoadResult errorResult) {
        if(errorResult == LoadResult.NO_DATA){
            if(mTrackDataListener != null){
                mTrackDataListener.noData();
            }
        }else{
            super.onError(errorResult);
        }
    }

    /**
     * 获取地图中轨迹线路坐标列表
     * @return
     */
    public List<LatLng> getpolyLines(){
        return historiLatLngList;
    }

    /**
     * 获取对应位置点的车辆信息
     * @param index
     * @return
     */
    public BaseCarHistoricalRouteItem getHistoriCarInfo(int index){
        if(historiCarInfoList.size()>index){
            return historiCarInfoList.get(index);
        }else{
            return null;
        }
    }

    /**
     * 地图车辆
     * @param mMarker
     */
    public void setMarker(Marker mMarker){
        carMarker = mMarker;
    }

    /**
     * 播放轨迹
     */
    public void trackPlay(){
        trackPlayOldAngle = 0;
        mHandler.sendEmptyMessage(TRACK_PLAY);
    }

    /**
     * 停止播放
     */
    public void trackStop(){
        mHandler.sendEmptyMessage(TRACK_STOP);
    }

    /**
     * 复位播放位置
     */
    public void trackReset(){
        trackPlayOldAngle = 0;
        mHandler.sendEmptyMessage(TRACK_RESET);
    }

    public String setPlaySpeed(Context context){
      if(TIME_INTERVAL == 2000){
            TIME_INTERVAL = 500;
        }else{
          TIME_INTERVAL = TIME_INTERVAL*2;
      }

        return getPlaySpeed(context);

    }

    public String getPlaySpeed(Context context){
        switch (TIME_INTERVAL){
            case 500:
                return context.getString(R.string.track_play_speed_fase);
            case 1000:
                return context.getString(R.string.track_play_speed_middle);
            case 2000:
                return context.getString(R.string.track_play_speed_slow);
        }

        return context.getString(R.string.track_play_speed_middle);
    }
    /**
     * 刷新Marker位置和方向
     * @param index
     */
    public void trackSeekIndex(int index){
        if(historiLatLngList.size()>index) {
            currentIndex = index;
            LatLng latLng = historiLatLngList.get(currentIndex);
            if (carMarker != null) {

                carMarker.setPosition(latLng);

                BaseCarHistoricalRouteItem carHistoricalRouteItem = historiCarInfoList.get(currentIndex);
                if(carHistoricalRouteItem != null) {
                    carMarker.setRotate(360 - carHistoricalRouteItem.getOrientation());

                }
            }
        }
    }

    public boolean isPlay(){
        return currentStatus == TRACK_PLAY;
    }

    public void setTrackPlayListener(TrackPlayListener listener){
        this.listener = listener;
    }

    public void onDestroy(){
        mHandler.removeMessages(TRACK_PLAY);
        mHandler.removeMessages(TRACK_STOP);
        mHandler.removeMessages(TRACK_RESET);
        mHandler = null;
    }

    class MoveLoopTask implements Runnable{

        private LatLng startLatLng;
        private LatLng endLatLng;
        private boolean isExit = false;

      public MoveLoopTask setLatLng(LatLng startLatLng, LatLng endLatLng){
            this.startLatLng = startLatLng;
          this.endLatLng = endLatLng;
          return this;
      }

        public void setExit(boolean isExit){
            this.isExit = isExit;
        }

        @Override
        public void run() {

            if(mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // refresh marker's rotate
                        float angle = (float) getAngle(startLatLng, endLatLng, trackPlayOldAngle);
                        trackPlayOldAngle = angle;
//                        System.out.println("get  Angle = " + angle);
                        carMarker.setRotate(angle);
                    }
                });
            }

            double slope = getSlope(startLatLng, endLatLng);
            //是不是正向的标示（向上设为正向）
            boolean isReverse = (startLatLng.latitude > endLatLng.latitude);

            double intercept = getInterception(slope, startLatLng);

            double xMoveDistance = isReverse ? getXMoveDistance(slope)
                    : -1 * getXMoveDistance(slope);

            double tempCount =   Math.abs(Math.abs(startLatLng.latitude -endLatLng.latitude)/xMoveDistance);

            int count = 0;
            if(tempCount > (int)tempCount){
                count = (int) tempCount+1;
            }else{
                count = (int) tempCount;
            }

            for (double j = startLatLng.latitude;!((j > endLatLng.latitude)^ isReverse); j = j - xMoveDistance) {

                if(isExit){
                    return;
                }

                LatLng latLng2 = null;
                if (slope != Double.MAX_VALUE) {
                    latLng2 = new LatLng(j, (j - intercept) / slope);
                } else {
                    latLng2 = new LatLng(j, startLatLng.longitude);
                }

                final LatLng finalLatLng = latLng2;
                if(mHandler != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // refresh marker's position
                            carMarker.setPosition(finalLatLng);
                        }
                    });
                }
                try {
//                    System.out.println("ciount = "+count+"   time = "+(TIME_INTERVAL/(count+1)));
                    Thread.sleep((TIME_INTERVAL / (count + 1)));
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    public HashMap<Integer, ArrayList<TrackLogItem>> getHistoriLog(){
        HashMap<Integer, ArrayList<TrackLogItem>> historiLog = new HashMap<>();
        if(historiCarInfoList.size() <= 0){
            return historiLog;
        }

        String dateTime = "";

        boolean isSameDate = true;

        int historLogCount = 0;

        TrackLogItem current = null;

        //遍历轨迹日志
        for(int i = 0; i<historiCarInfoList.size(); i++){
            BaseCarHistoricalRouteItem item = historiCarInfoList.get(i);

            String locationTime = item.getLocationTime();

            String[] split = locationTime.split(" ");

            String tempTime = "";

            if(split!= null && split.length>0){
                tempTime = split[0];
            }

            if(TextUtils.isEmpty(dateTime)){
                isSameDate = true;
                dateTime = tempTime;
            }else{
                if(dateTime.equals(tempTime)){
                    isSameDate = true;
                }else{
                    isSameDate = false;
                    dateTime = tempTime;
                }
            }

            if(!isSameDate){
                historLogCount++;
            }

            //判断是否停留
            if(item.getmGNSSSpeed() == 0) {
                ArrayList<TrackLogItem> carHistoricalRouteItems = historiLog.get(historLogCount);

                if (carHistoricalRouteItems == null) {
                    carHistoricalRouteItems = new ArrayList<>();
                    current = TrackLogItem.creatItem(item);
                    carHistoricalRouteItems.add(current);
                } else {
                    current = carHistoricalRouteItems.get(carHistoricalRouteItems.size() - 1);
                    if (!current.isStop()) {
                        current = TrackLogItem.creatItem(item);
                        carHistoricalRouteItems.add(current);
                    }
                }

                current.setIsStop(true);

                //2017-06-16 08:58:26
                String old_locationTime = current.getLocationTime();
                String new_locationTime;
                if(historiCarInfoList.size()>(i+1)){
                    new_locationTime = historiCarInfoList.get(i+1).getLocationTime();
                }else {
                    new_locationTime = item.getLocationTime();
                }

//                if(debug)LogUtils.error("old_locationTime = "+old_locationTime + "   new_locationTime = "+new_locationTime );

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date parse = simpleDateFormat.parse(old_locationTime);
                    long oldTime = parse.getTime();
                    Date parse1 = simpleDateFormat.parse(new_locationTime);
                    long newTime = parse1.getTime();
                    long stopTime = newTime - oldTime;
                    String str_time = getstopTime(stopTime);
                    current.setStopTime(str_time);
//                    if(debug)LogUtils.error("setStopTime = "+str_time);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                historiLog.put(historLogCount,carHistoricalRouteItems);
            }else{

                //添加到指定的记录中
                current = TrackLogItem.creatItem(item);
                current.setIsStop(false);//默认值是false
                ArrayList<TrackLogItem> carHistoricalRouteItems = historiLog.get(historLogCount);

                if(carHistoricalRouteItems == null){
                    carHistoricalRouteItems = new ArrayList<>();
                }
                carHistoricalRouteItems.add(current);

                historiLog.put(historLogCount,carHistoricalRouteItems);
            }


        }

        return historiLog;

    }

    private String getstopTime(long time){
        String buffer = "";

        long temp = time/1000;

        int ss = (int) (temp%60);

        int min = (int) (temp/60%60);

        int hout = (int) (temp/60/60);

        if(ss>0){
            buffer = (ss>=10?  ":" + ss :":0" + ss);
        }else{
            buffer = ":00";
        }

        if(min>0){
            buffer = (min>=10? ":"+min:":0"+min)+buffer;
        }else{
            buffer = ":00"+ buffer;
        }

        if(hout>0){
            buffer = (hout>=10? ":"+hout:":0"+hout)+buffer;
        }else{
            buffer = ":00"+ buffer;
        }

        return buffer;
    }

    public int getTotalMileage() {
        return totalMileage;
    }
}
