package com.shinetech.mvp.network.UDP.presenter;

import android.text.TextUtils;
import android.view.View;

import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskManager;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.interfaces.ResponsePlateNumberListListener;
import com.shinetech.mvp.network.UDP.UDPRequestCtrl;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.CompanyAllCarBaseInfo;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.InfoTool.AllCarListClassify;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarStatusInfoBean;
import com.shinetech.mvp.sort.CarInfoBeanComparator;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.UIUtils;
import com.shinetech.mvp.view.MvpView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ljn on 2017/2/28.
 */
public class AllCarListPresenter extends MvpBasePresenter<BaseVo, MvpView<BaseVo>> {


    private AllCarListClassify mAllCarListClassify;

    private long timeOut = 60*1000;

    private boolean hasResult = false;

    private boolean isOnError = true;

    private boolean isFristLoad = true;

    private boolean cancelTask = false;

    /**
     * 选择要显示的车辆列表
     */
    private Map<String, CarInfoBean> selectCarInfoMapCache = new LinkedHashMap<>();

    public AllCarListPresenter() {

    }

    /**
     * 获取所有车辆信息
     */
    public void getAllCar(){
        UserInfo userInfo = UserInfo.getInstance();

        if(userInfo.isPerson()){
            return;
        }

        //判断是否用响应接口
        if(MainApplication.isUserResponseUDP){
            getResponseAllCar();
            return;
        }

        final List<CarInfoBean> newestCarInfoList = userInfo.getNewestCarInfoList();

        long lastTimeUpdateCarList = userInfo.getLastTimeUpdateCarList();

        long currentTime = System.currentTimeMillis();

        if(newestCarInfoList == null || (userInfo.getPlateNumberList() == null) || newestCarInfoList.size() != userInfo.getPlateNumberList().size() || (currentTime-lastTimeUpdateCarList)>timeOut){

//            未获取车辆信息、车辆信息不全 或 信息已过时
            String companyName = userInfo.getUserName();

            if(!TextUtils.isEmpty(companyName)) {

                CompanyAllCarBaseInfo companyAllCarBaseInfo = new CompanyAllCarBaseInfo(companyName);

                loadData(companyAllCarBaseInfo);
            }

        }else{

            TaskManager.runDBTask(new TaskBean() {
                @Override
                public void run() {

                    final CompanyAllCarBaseInfo companyAllCarBaseInfo = new CompanyAllCarBaseInfo(null);

                    //赋值所有车辆
                    companyAllCarBaseInfo.setCarInfoList(newestCarInfoList);

                    initSelectCarInfoMapCache();

                    //对所有车辆分类
                    classifyCarList(companyAllCarBaseInfo);

                    UIUtils.runInMainThread(new Runnable() {
                        @Override
                        public void run() {

                            //          已近更新的车辆列表，直接回掉
                            if (isViewAttached()) {
                                getView().showContentView(companyAllCarBaseInfo);
                            }
                        }
                    });
                }
            });

        }

    }

    private void getResponseAllCar(){

        if (isViewAttached()) {
            getView().onLoading();
        }

        List<String> plateNumberList = UserInfo.getInstance().getPlateNumberList();

        if(plateNumberList == null){
            //TODO getPlateNumberList

            ResponsePlateNumberListListener responsePlateNumberListListener = new ResponsePlateNumberListListener() {
                @Override
                public void onSuccess(List<String> carList) {
                    getAllCarInfoList(carList);
                }

                @Override
                public void onError(String message) {
                    if (isViewAttached()) {
                        getView().onStopLoading();
                    }
                }
            };

            //添加安装师傅获取车辆列表方式
            if(UserInfo.getInstance().getUserPermission() == UserInfo.INSTALLATIONMASTER) {
                MainPresenter.selectBaseOrder(responsePlateNumberListListener);
            }else {
                MainPresenter.responseUDPgetPlateNumberList(responsePlateNumberListListener);
            }

        }else{
            getAllCarInfoList(plateNumberList);
        }




    }

    private void getAllCarInfoList(final List<String> carList){
        int newestCarInfoCount = UserInfo.getInstance().getNewestCarInfoCount();
        //已经获取到所有的车辆状态信息
        if(carList.size() == newestCarInfoCount){

            upData();

        }else{
            //获取所有的车辆状态信息
            isOnError = true;

            TaskBean taskBean = new TaskBean() {
                @Override
                public void run() {

                    List<String> mplateNumber = new ArrayList<>(carList);

                    if(mplateNumber.size() == 0){
                        return;
                    }

                    int currentIndex = 0;
                    String plateNumber = null;

                    int timeOutCount = 0;

                    UserInfo mUserInfo = UserInfo.getInstance();
                    do {

                        if(cancelTask){
                            return;
                        }

                        if(!isOnError) {
                            currentIndex++;
                            isOnError = true;
                        }

                        plateNumber = mplateNumber.get(currentIndex);

                        CarInfoBean newestCarInfo = mUserInfo.getNewestCarInfo(plateNumber);

                        if(newestCarInfo != null){
                            isOnError = false;
                            continue;
                        }

                        hasResult = false;

                        ResponseCarStatusInfoBean responseCarStatusInfoBean = new ResponseCarStatusInfoBean(plateNumber);

                        UDPRequestCtrl.getInstance().request(responseCarStatusInfoBean, new ResponseListener() {
                            @Override
                            public void onSuccess(LoadResult successResult) {
                                BaseVo dataVo = successResult.getDataVo();
                                if (dataVo instanceof ResponseCarStatusInfoBean) {
                                   // ResponseCarStatusInfoBean mResponseCarStatusInfoBean = (ResponseCarStatusInfoBean) dataVo;
                                    isOnError = false;
                                }
                                hasResult = true;
                            }

                            @Override
                            public void onError(LoadResult errorResult) {
                                hasResult = true;
                                isOnError = true;
                            }
                        });

                         long sendTime = System.currentTimeMillis();
                        /*(System.currentTimeMillis()- sendTime < 1000)  &&*/

                        //等待 当请求完成 、 请求超时 、 列表滑动操作 或 解析失败 等情况退出等待。
                        while (!hasResult && !cancelTask && (System.currentTimeMillis()- sendTime < 1000));

                        if((!hasResult && (System.currentTimeMillis()- sendTime < 1000)) || isOnError){
                            timeOutCount++;

                            //超过3次，不再获取定位信息
                            if(timeOutCount == 3){
                                isOnError = false;
                            }
                        }else{
                            timeOutCount = 0;
                        }

                    } while((mplateNumber.size()-1)>currentIndex);

                    //获取到所有的车辆状态信息，跟新界面
                    upData();
                }

            };

            taskBean.setType("getAllCarInfo");

            TaskManager.runDBTask(taskBean);
        }
    }

    /**
     * 更新数据到界面中
     */
    private void upData(){
        TaskManager.runDBTask(new TaskBean() {
            @Override
            public void run() {

                final CompanyAllCarBaseInfo companyAllCarBaseInfo = new CompanyAllCarBaseInfo(null);

                List<CarInfoBean> newestCarInfoList = UserInfo.getInstance().getNewestCarInfoList();

                //赋值所有车辆
                companyAllCarBaseInfo.setCarInfoList(newestCarInfoList);

                initSelectCarInfoMapCache();

                //对所有车辆分类
                classifyCarList(companyAllCarBaseInfo);

                UIUtils.runInMainThread(new Runnable() {
                    @Override
                    public void run() {

                        //          已近更新的车辆列表，直接回掉
                        if (isViewAttached()) {
                            getView().showContentView(companyAllCarBaseInfo);
                            getView().onStopLoading();
                        }
                    }
                });
            }
        });
    }

    public void updataSelectCarInfoMapCache(){

        UserInfo userInfo = UserInfo.getInstance();

        List<String> selectCarList = userInfo.getSelectCarList();

        if(selectCarInfoMapCache.size() != selectCarList.size()) {

            for (String plateNumber : selectCarList) {
                CarInfoBean newestCarInfo = userInfo.getNewestCarInfo(plateNumber);
                if (newestCarInfo != null) {
                    selectCarInfoMapCache.put(newestCarInfo.getPlateNumber(), newestCarInfo);
                }
            }
        }

    }

    private void initSelectCarInfoMapCache(){

//      TODO init selectCarInfoMapCache DB To cache
        //已经初始化过了，就不进行重新赋值（防止刷掉修改了但未提交的操作）
        if(isFristLoad) {
            isFristLoad = false;
            if (selectCarInfoMapCache.size() != 0) {
                return;
            }

            UserInfo userInfo = UserInfo.getInstance();

            List<String> selectCarList = userInfo.getSelectCarList();

            for (String plateNumber : selectCarList) {
                CarInfoBean newestCarInfo = userInfo.getNewestCarInfo(plateNumber);
                if (newestCarInfo != null) {
                    selectCarInfoMapCache.put(newestCarInfo.getPlateNumber(), newestCarInfo);
                }
            }
        }

    }


    @Override
    public void onSuccess(final LoadResult successResult) {

        final BaseVo dataVo = successResult.getDataVo();

        if(dataVo!=null && dataVo instanceof CompanyAllCarBaseInfo){

            TaskManager.runDBTask(new TaskBean() {
                @Override
                public void run() {

                    CompanyAllCarBaseInfo mCompanyAllCarBaseInfo = (CompanyAllCarBaseInfo) dataVo;

                    initSelectCarInfoMapCache();

                    //对所有车辆分类
                    classifyCarList(mCompanyAllCarBaseInfo);


                    UIUtils.runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            AllCarListPresenter.super.onSuccess(successResult);
                        }
                    });
                }
            });

        }else{
            super.onSuccess(successResult);
        }

    }

    /**
     * 分类车辆信息
     * @param mCompanyAllCarBaseInfo
     */
    private void classifyCarList(CompanyAllCarBaseInfo mCompanyAllCarBaseInfo){

        List<CarInfoBean> carInfoList = mCompanyAllCarBaseInfo.getCarInfoList();

        CarInfoBeanComparator mCarInfoBeanComparator = new CarInfoBeanComparator(selectCarInfoMapCache);
        Collections.sort(carInfoList, mCarInfoBeanComparator);

        if (mAllCarListClassify == null) {
            mAllCarListClassify = new AllCarListClassify(carInfoList);
        } else {
            mAllCarListClassify.setAllcarList(carInfoList);
        }

        //将全部车辆分类
        mAllCarListClassify.classifyCar();

    }

    @Override
    public void onError(LoadResult errorResult) {
        super.onError(errorResult);
    }

    public AllCarListClassify getmAllCarListClassify(){
        return mAllCarListClassify;
    }

    public boolean isSelectCar(String plateNumber){
        return selectCarInfoMapCache.get(plateNumber) == null? false : true;
    }

    /**
     * 添加要显示在地图中的车辆
     * @param mCarInfoBean
     * @return
     */
    public boolean  addCarToShow(CarInfoBean mCarInfoBean){
        if(mCarInfoBean != null && selectCarInfoMapCache.get(mCarInfoBean.getPlateNumber()) == null){
            synchronized (this){
                CarInfoBean newestCarInfo = UserInfo.getInstance().getNewestCarInfo(mCarInfoBean.getPlateNumber());
                if(newestCarInfo == null){
                    selectCarInfoMapCache.put(mCarInfoBean.getPlateNumber(),mCarInfoBean);
                }else{
                    selectCarInfoMapCache.put(newestCarInfo.getPlateNumber(),newestCarInfo);
                }
            }
            return true;
        }else{
            LogUtils.error("addCarToShow is exist");
            return false;
        }
    }

    /**
     * 从要显示的车辆列表中获取对应的车辆信息
     * @param plateNumber
     * @return
     */
    public CarInfoBean getSelectCarInfo(String plateNumber){
        return selectCarInfoMapCache.get(plateNumber);
    }

    /**
     * 获取要显示的车辆数量
     * @return
     */
    public int getSelectCarInfoCount(){
        return selectCarInfoMapCache.size();
    }


    /**
     * 移除要显示在地图中的车辆
     * @param mCarInfoBean
     * @return
     */
    public boolean removeSelectCarInfo(CarInfoBean mCarInfoBean){
        if(mCarInfoBean !=null && selectCarInfoMapCache.get(mCarInfoBean.getPlateNumber()) != null){

            synchronized (this){
                return selectCarInfoMapCache.remove(mCarInfoBean.getPlateNumber()) == null? false : true;

            }
        }else{
            LogUtils.error("removeSelectCarInfo is no exist");
            return false;
        }
    }



    public void celanSelectCarAll(){
        synchronized (this){
            selectCarInfoMapCache.clear();
        }
    }

    public void setCancelTask(boolean cancelTask) {
        this.cancelTask = cancelTask;
    }

    public Map<String,CarInfoBean> getSelectCarInfoMapCache(){
        return selectCarInfoMapCache;
    }
}
