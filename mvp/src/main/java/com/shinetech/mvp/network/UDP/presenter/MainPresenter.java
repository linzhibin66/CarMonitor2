package com.shinetech.mvp.network.UDP.presenter;

import android.text.TextUtils;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskCtrl.DBCtrlTask;
import com.shinetech.mvp.DB.bean.CarInfoDB;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.R;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.interfaces.BaseUpdateBaiduMarker;
import com.shinetech.mvp.interfaces.ResponsePlateNumberListListener;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.UDPRequestCtrl;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.CarLatestStatus;
import com.shinetech.mvp.network.UDP.bean.CarListBean;
import com.shinetech.mvp.network.UDP.bean.QueryDesignatedAreaCarInfo;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.BaseOrderInfoVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.SelectOrderByNumberVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.SelectOrderListVo;
import com.shinetech.mvp.network.UDP.listener.RequestAllCarListener;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarBaseInfoBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarListBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarStatusInfoBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseFeedBackBean;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.UIUtils;
import com.shinetech.mvp.view.MvpView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * Created by ljn on 2017/2/21.
 */
public abstract class MainPresenter extends MvpBasePresenter<BaseVo, MvpView<BaseVo>> {

    private final String TAG = "MainPresenter - - - ";

    private static final boolean isDebug = false && LogUtils.isDebug;

    protected BaseUpdateBaiduMarker markerScheme;

    public boolean isShowLoadingFrist = false;

    /**
     * 是否完成当前工单查询
     */
    static boolean  isFinishCurrentOrderSelect = false;

    public MainPresenter(BaseUpdateBaiduMarker markerScheme) {
        this.markerScheme = markerScheme;
    }

    public void setBaiduMap(BaiduMap mBaiduMap){
        if(markerScheme !=null){
            markerScheme.setBaiduMap(mBaiduMap);

        }
    }



    /**
     * 获取自己的车牌（个人）或者 获取所属的车牌列表（企业）;刷新车辆位置信息
     */
    public abstract void getOrRefreshCar();

    /**
     * 旧接口获取车辆的详细信息，无应答
     */
    public void getCarInfo(String plateNumber,ResponseListener mResponseListener){

        CarLatestStatus carLatestStatus = new CarLatestStatus(plateNumber);

        UDPRequestCtrl.getInstance().request(carLatestStatus,mResponseListener);
    }

    /**
     * 新接口获取车辆的详细信息 ，有应答
     * @param plateNumber
     * @param mResponseListener
     */
    public void responseUDPGetCarInfo(String plateNumber,ResponseListener mResponseListener){

        ResponseCarStatusInfoBean responseCarStatusInfoBean = new ResponseCarStatusInfoBean(plateNumber);

        UDPRequestCtrl.getInstance().request(responseCarStatusInfoBean, mResponseListener);
    }

    /**
     * 获取所有车辆信息
     * @param mRequestAllCarListener
     */
    public abstract void getAllCar(RequestAllCarListener mRequestAllCarListener);

    /**
     * 获取车牌列表
     */
    protected void getPlateNumberList(){
        //判断使用哪种UDP接口获取车辆列表
        if(MainApplication.isUserResponseUDP){

            isShowLoadingFrist = true;
            //System.out.println("getOrRefreshCar : isShowLoadingFrist = "+isShowLoadingFrist);
            responseUDPgetPlateNumberList(new ResponsePlateNumberListListener() {
                @Override
                public void onSuccess(List<String> carList) {
                    String plateNumber = getFistLocationCar(carList);

                    //TODO 定位首辆车到地图中
//                    responseUDPGetCarInfo(plateNumber, MainPresenter.this);
                    locationFistCar(plateNumber, MainPresenter.this);
                }

                @Override
                public void onError(String message) {
                    //                   获取车牌列表为null
                    stopLoading();
                    isShowLoadingFrist = false;
                    if(isDebug)LogUtils.error(TAG + message);
                }
            });
        }else{
            oldUDPgetPlateNumberList();
        }

    }

    public void locationFistCar(final String plateNumber, final MainPresenter presenter){
        if(MainApplication.isUseCarInfoDB) {

            DBCtrlTask.getInstance().runTask(new TaskBean() {
                @Override
                public void run() {

                    int newestCarInfoCount = UserInfo.getInstance().getNewestCarInfoCount();

                    if(newestCarInfoCount>0){
                        responseUDPGetCarInfo(plateNumber,presenter);
                        return ;
                    }

                    List<CarInfoDB> carInfoDBs = DBManager.querySelectCarInfoList(UserInfo.getInstance().getUserName(), null);

                    if((carInfoDBs == null) || (carInfoDBs.size() == 0)){
                        responseUDPGetCarInfo(plateNumber,presenter);
                        return ;
                    }

                    for(CarInfoDB mCarInfoDB : carInfoDBs){

                        CarInfoBean carInfoBean = mCarInfoDB.toCarInfoBean();
                        UserInfo.getInstance().addNewestVarInfo(carInfoBean);

                    }

                    UIUtils.runInMainThread(new Runnable() {

                        @Override
                        public void run() {
                            CarInfoBean newestCarInfo = UserInfo.getInstance().getNewestCarInfo(plateNumber);
                            ResponseCarStatusInfoBean responseCarStatusInfoBean = new ResponseCarStatusInfoBean(newestCarInfo.getPlateNumber());
                            responseCarStatusInfoBean.setLocationTime(newestCarInfo.getLocationTime());
                            responseCarStatusInfoBean.setResultlng(newestCarInfo.getLng());
                            responseCarStatusInfoBean.setResultlat(newestCarInfo.getLat());
                            responseCarStatusInfoBean.setResultSpeed(newestCarInfo.getSpeed());
                            responseCarStatusInfoBean.setResultGNSSSpeed(newestCarInfo.getgNSSSpeed());
                            responseCarStatusInfoBean.setResultOrientation(newestCarInfo.getOrientation());
                            responseCarStatusInfoBean.setResultAltitude(newestCarInfo.getAltitude());
                            responseCarStatusInfoBean.setResultMileage(newestCarInfo.getMileage());
                            responseCarStatusInfoBean.setResultOilMass(newestCarInfo.getOilMass());
                            responseCarStatusInfoBean.setResultStatus(newestCarInfo.getStatus());
                            responseCarStatusInfoBean.setResultAlarmType(newestCarInfo.getAlarmType());
                            responseCarStatusInfoBean.setViolationCount(newestCarInfo.getViolationCount());
                            responseCarStatusInfoBean.setViolationList(newestCarInfo.getViolationList());

                            onSuccess(LoadResult.STATUS_SUCCESS.setDataVo(responseCarStatusInfoBean));
                        }
                    });


                }
            });


        }else{

            responseUDPGetCarInfo(plateNumber, presenter);

        }
    }

    /**
     * 新接口获取车辆列表，有应答的UDP
     */
    public static void responseUDPgetPlateNumberList(final ResponsePlateNumberListListener mResponsePlateNumberListListener){

        if(UserInfo.getInstance().getUserPermission() == UserInfo.INSTALLATIONMASTER){
            selectBaseOrder(mResponsePlateNumberListListener);
            return;
        }

        long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1=new Date(time);
        String operationInfo = "ALLCarBaseInfoList "+format.format(d1);
        UDPRequestCtrl.getInstance().request(new ResponseCarListBean(operationInfo), new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {
                BaseVo dataVo = successResult.getDataVo();
                if(dataVo !=null && dataVo instanceof ResponseCarListBean){

                    ResponseCarListBean mResponseCarListBean = (ResponseCarListBean) dataVo;

                    //获取车辆基础信息列表
                    List<ResponseCarBaseInfoBean> responseCarBaseInfoList = mResponseCarListBean.getResponseCarBaseInfoList();

                        //创建车牌列表
                        List<String> carList = new ArrayList<>();

                    if(responseCarBaseInfoList != null && responseCarBaseInfoList.size()>0) {
                        //UserInfo.getInstance().setCarBaseInfoList(responseCarBaseInfoList);

                        //存储车辆基础信息列表 和 赋值车牌列表
                        for (ResponseCarBaseInfoBean item : responseCarBaseInfoList) {

                            String resultPlateNumber = item.getResultPlateNumber();

                            //存储车辆基础信息列表
                            UserInfo.getInstance().addCarBaseInfoItem(item);

                            if (!carList.contains(resultPlateNumber)) {
                                carList.add(resultPlateNumber);
                            }
                        }
                    }

                    if (carList != null && carList.size() > 0) {
                        //TODO 车牌列表，存储到用户个人信息中
                        UserInfo.getInstance().setPlateNumberList(carList);

                        mResponsePlateNumberListListener.onSuccess(carList);

                    } else {
                        mResponsePlateNumberListListener.onError("getPlateNumberList carList is null");

                    }
                }
            }

            @Override
            public void onError(LoadResult errorResult) {
                //LogUtils.error(TAG + "   onError Message : " + errorResult.getMessage());
                mResponsePlateNumberListListener.onError(errorResult.getMessage());
            }
        });
    }

    /**
     * 旧接口获取车辆列表，无应答的UPD
     */
    protected void oldUDPgetPlateNumberList(){
        UDPRequestCtrl.getInstance().request(new CarListBean(), new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {

                BaseVo dataVo = successResult.getDataVo();

                if (dataVo instanceof CarListBean) {

                    CarListBean mCarListBean = (CarListBean) dataVo;

                    List<String> carList = mCarListBean.getCarList();

                    if (carList != null && carList.size() > 0) {
                        //TODO 车牌列表，存储到用户个人信息中
                        UserInfo.getInstance().setPlateNumberList(carList);

                        String plateNumber = getFistLocationCar(carList);

                        //TODO 定位首辆车到地图中
                        getCarInfo(plateNumber, MainPresenter.this);
                    } else {
                        //                   获取车牌列表为null
                        stopLoading();
                        if(isDebug)LogUtils.error(TAG + " getPlateNumberList carList is null");
                    }
                }
            }

            @Override
            public void onError(LoadResult errorResult) {
                stopLoading();
            }
        });
    }


    /**
     *
     * @param plateNumberList
     * @return
     */
    protected String getFistLocationCar(List<String> plateNumberList){
        List<String> selectCarList = UserInfo.getInstance().getSelectCarList();

        if(selectCarList.size() == 0){
            return plateNumberList.get(0);
        }

        for(int i = 0; i<selectCarList.size();i++){

            String plateNumber = selectCarList.get(i);

            if(plateNumberList.contains(plateNumber)){
                return plateNumber;
            }
        }

        return plateNumberList.get(0);
    }

    public void getMapCars(int minLng, int minLat, int maxLng, int maxLat,boolean isShowLoading){
        QueryDesignatedAreaCarInfo queryDesignatedAreaCarInfo = new QueryDesignatedAreaCarInfo(minLng, minLat, maxLng, maxLat);

        if(isShowLoading){
            loadData(queryDesignatedAreaCarInfo);
        }else {
            UDPRequestCtrl.getInstance().request(queryDesignatedAreaCarInfo, this);
        }
    }



    @Override
    public void onSuccess(LoadResult successResult) {
        super.onSuccess(successResult);

//        BaseVo dataVo = successResult.getDataVo();

    }

    @Override
    public void onError(LoadResult errorResult) {
        super.onError(errorResult);
        isShowLoadingFrist = false;


    }

    private void stopLoading(){
        if (isViewAttached()) {
            getView().onStopLoading();
        }
    }


    /**
     * 更新所有Marker到地图
     * @param carInfoList
     */
    public void updateMarkers(Collection<CarInfoBean> carInfoList){
        if(markerScheme !=null){
            markerScheme.updateMarkers(carInfoList);
        }
    }

    /**
     * 更新单个Marker图标
     * @param mCarInfoBean 车辆信息
     * @param mapViewScope 当前地图可视区域范围  【0-3】分别为 minLng, minLat, maxLng, maxLat
     */
    public void updateMarker(CarInfoBean mCarInfoBean, int[] mapViewScope){
        if(markerScheme !=null){
            markerScheme.updateMarker(mCarInfoBean, mapViewScope);
        }
    }

    public void setCurrentClickMarker(Marker mMarker){
        if(markerScheme !=null){
            markerScheme.setCurrentClickMarker(mMarker);
        }
    }

    public void setCurrentClickMarker(String plateNumber){
        if(markerScheme !=null){
            markerScheme.setCurrentClickMarker(plateNumber);
        }
    }

    public void resetCurrentClickMarker(){
        if(markerScheme !=null){
            markerScheme.resetCurrentClickMarker();
        }
    }

    public void addmakerOptions(LatLng latLng, String plateNumber, short orientation){
        if(markerScheme !=null){
            markerScheme.addmakerOptions(latLng, plateNumber, orientation);
        }
    }

    public void cleanUNLockCar(){
        if(markerScheme !=null){
            markerScheme.cleanUNLockCar();
        }
    }

    public void feedbackMessage(String message,ResponseListener mResponseListener){

        ResponseFeedBackBean responseFeedBackBean = new ResponseFeedBackBean(message, UserInfo.getInstance().getUserName());

        UDPRequestCtrl.getInstance().request(responseFeedBackBean,mResponseListener);
    }



    /**
     * 查询工单
     */
    public static void selectBaseOrder(final ResponsePlateNumberListListener mResponsePlateNumberListListener){
        byte wayToQuery = 5;
        String queryCondition = UserInfo.getInstance().getUserName();

        //查询该用户所有进行中的工单
        SelectOrderListVo selectOrderListVo = new SelectOrderListVo(wayToQuery, queryCondition, (byte)0);

        UDPRequestCtrl.getInstance().request(selectOrderListVo, new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {

                BaseVo dataVo = successResult.getDataVo();

                if(dataVo instanceof SelectOrderListVo){
                    SelectOrderListVo mSelectOrderListVo = (SelectOrderListVo) dataVo;
                    getCarList(mResponsePlateNumberListListener,mSelectOrderListVo);

                }

            }

            @Override
            public void onError(LoadResult errorResult) {
                mResponsePlateNumberListListener.onError(errorResult.getMessage());
            }
        });
    }

    private static void getCarList(final ResponsePlateNumberListListener mResponsePlateNumberListListener, final SelectOrderListVo mSelectOrderListVo){
        DBCtrlTask.getInstance().runTask(new TaskBean() {
            @Override
            public void run() {

                //创建车牌列表
                final List<String> carList = new ArrayList<>();


                //遍历所有工单，获取车辆列表
                List<BaseOrderInfoVo> orderInfoList = mSelectOrderListVo.getOrderInfoList();
                Iterator<BaseOrderInfoVo> iterator = orderInfoList.iterator();

                //遍历所有工单
                while(iterator.hasNext()) {

                    isFinishCurrentOrderSelect = true;

                    BaseOrderInfoVo mBaseOrderInfoVo = iterator.next();
                    String orderNumber = mBaseOrderInfoVo.getOrderNumber();

                    String orderName = mBaseOrderInfoVo.getOrderName();

                    //查询维修单
                    SelectOrderByNumberVo selectOrderByNumberVo = new SelectOrderByNumberVo(orderNumber, orderName);

                    //根据工单号获取工单信息
                    UDPRequestCtrl.getInstance().request(selectOrderByNumberVo, new ResponseListener() {
                        @Override
                        public void onSuccess(LoadResult successResult) {
                            isFinishCurrentOrderSelect = false;

                            BaseVo dataVo = successResult.getDataVo();
                            if(!(dataVo instanceof SelectOrderByNumberVo)) {
                                return;
                            }

                            SelectOrderByNumberVo mSelectOrderByNumberVo = (SelectOrderByNumberVo) dataVo;

                            //获取请求结果
                            byte orderCtrlResult = mSelectOrderByNumberVo.getOrderCtrlResult();
                            if(orderCtrlResult == 0){
                                return;
                            }

                            //获取工单内容列表
                            byte[] contentList = mSelectOrderByNumberVo.getContentList();

                            String resultOrderName = mSelectOrderByNumberVo.getResultOrderName();
                            //区分工单类型
                            if(resultOrderName.equals(MainApplication.getInstance().getString(R.string.order_name_maintenance))) {

                                //维修单获取车牌号码
                                DecodeUDPDataTool.OrderContentListItemData orderContentListItemData = DecodeUDPDataTool.decodeItemByDataName(contentList,
                                        MainApplication.getInstance().getString(R.string.order_content_maintenance_apply));

                                if (orderContentListItemData != null && orderContentListItemData.hasContent()) {

                                    String plateNumber = orderContentListItemData.getFistValue(MainApplication.getInstance().getString(R.string.plate_number));

                                    /*if (debug) */System.out.println(" - - - - Maintenance - - - - get Car List plateNumber = " + plateNumber);

                                    if(!TextUtils.isEmpty(plateNumber) && !carList.contains(plateNumber)) {
                                        carList.add(plateNumber);
                                    }
                                }


                            }else if(resultOrderName.equals(MainApplication.getInstance().getString(R.string.order_name_install))){

                                //安装单获取车牌号码列表
                                DecodeUDPDataTool.OrderContentListItemData orderContentListItemData = DecodeUDPDataTool.decodeItemByDataName(contentList,
                                        MainApplication.getInstance().getString(R.string.order_content_install_record));

                                int size = orderContentListItemData.getSize();

                                if(size == 0){
                                    return;
                                }

                                for(int i = 0; i<size; i++){
                                    String plateNumber = orderContentListItemData.getValue(MainApplication.getInstance().getString(R.string.plate_number), i);

                                    /*if (debug)*/ System.out.println(" - - - - install - - - - get Car List plateNumber = " + plateNumber);
                                    if(!TextUtils.isEmpty(plateNumber) && !carList.contains(plateNumber)) {
                                        carList.add(plateNumber);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(LoadResult errorResult) {

                            isFinishCurrentOrderSelect = false;
                        }
                    });

                    while(isFinishCurrentOrderSelect);
                }

                UIUtils.runInMainThread(new Runnable() {

                    @Override
                    public void run() {

                        if (carList != null && carList.size() > 0) {
                            //TODO 车牌列表，存储到用户个人信息中
                            UserInfo.getInstance().setPlateNumberList(carList);

                            mResponsePlateNumberListListener.onSuccess(carList);

                        } else {
                            mResponsePlateNumberListListener.onError("getPlateNumberList carList is null");

                        }
                    }
                });

            }
        });

    }



}
