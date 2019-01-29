package com.shinetech.mvp.network.UDP;

import com.shinetech.mvp.network.UDP.bean.CarBaseInfoBean;
import com.shinetech.mvp.network.UDP.bean.CarHistoricalRouteBean;
import com.shinetech.mvp.network.UDP.bean.CarLatestStatus;
import com.shinetech.mvp.network.UDP.bean.CarListBean;
import com.shinetech.mvp.network.UDP.bean.CompanyAllCarBaseInfo;
import com.shinetech.mvp.network.UDP.bean.LoginOutBean;
import com.shinetech.mvp.network.UDP.bean.QueryDesignatedAreaCarInfo;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.presenter.LoginPresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarBaseInfoBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarHistoricalRouteBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarListBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarStatusInfoBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseViolationLogBean;
import com.shinetech.mvp.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ljn on 2017/2/6.
 */
public class UDPTest {

    public static void getCarList(){
        UDPRequestCtrl.getInstance().request(new CarListBean(), new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {
                CarListBean dataVo = (CarListBean) successResult.getDataVo();
                LogUtils.error("getCarList Success  ： " + dataVo.toString());

                List<String> carList = dataVo.getCarList();
                if(carList!=null && carList.size()>0){
                    getCarInfo(carList.get(0));
                    getCarstatus(carList.get(0));

//                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String s_date = "2017-02-16 10:00:00";
                    String e_date = "2017-02-16 11:00:00";
                    getCarHistory(carList.get(0),s_date,e_date);
                }

            }

            @Override
            public void onError(LoadResult errorResult) {
                switch (errorResult){
                    case ERROR:
                        LogUtils.error("getCarList error");
                        break;
                    case TIME_OUT:
                        LogUtils.error("getCarList time out");
                        break;
                    case NO_INTERNET_CONNECT:
                        LogUtils.error("getCarList no internet connect");
                        break;
                }
            }
        });
    }

    public static void LoginOut(){
        LoginOutBean loginOutBean = new LoginOutBean();
        UDPRequestCtrl.getInstance().request(loginOutBean, null);
    }

    public static void LoginUser(){

        LoginPresenter mLoginPersenter = new LoginPresenter();
        mLoginPersenter.login("东莞市翔运小汽车出租有限公司(一通）",  "123456", DatagramPacketDefine.COMPANY_TYPE,true);
       /* ResponseUserAuthentication userAuthentication = new ResponseUserAuthentication(DatagramPacketDefine.COMPANY_TYPE, "东莞市翔运小汽车出租有限公司(一通）");
        UDPTaskManager.getInstance().request(userAuthentication, new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {
                BaseVo dataVo = successResult.getDataVo();
                if(dataVo!=null){
                    ResponseUserAuthentication mUserAuthentication = (ResponseUserAuthentication) dataVo;
                    byte tokenResult = mUserAuthentication.getTokenResult();
                    if(0x01 == tokenResult){

                        String pws = "123456";

                        byte[] pwss = sendApprove(mUserAuthentication.getResultUserName(), mUserAuthentication.getToken(), MD5Tool.encodingSting(pws + pws).getBytes());
                        String loginToken = MD5Tool.getMessageDigest(pwss);

                        ResponseLoginBean loginBean = new ResponseLoginBean(mUserAuthentication.getLoginType(), mUserAuthentication.getUserName(), loginToken);

                        UDPTaskManager.getInstance().request(loginBean, new ResponseListener() {
                            @Override
                            public void onSuccess(LoadResult successResult) {
                                BaseVo dataVo1 = successResult.getDataVo();
                                if(dataVo1!=null){
                                    ResponseLoginBean mLoginBean = (ResponseLoginBean) dataVo1;
                                    byte loginResult = mLoginBean.getLoginResult();
                                    if(0x01 == loginResult){
                                        System.out.println("ResponseLoginBean success......................");

                                        getCarList();
                                    }
                                }

                            }

                            @Override
                            public void onError(LoadResult errorResult) {
                                System.out.println("ResponseLoginBean error......................");
                            }
                        });

                    }
                }
            }

            @Override
            public void onError(LoadResult errorResult) {
                System.out.println("userAuthentication error......................");
            }
        });*/

    }

    public static byte[] sendApprove(byte[] user, byte approve[], byte md5Pwd[]) {
        byte[] theData = new byte[user.length + approve.length + md5Pwd.length];
        int length = 0;

        System.arraycopy(user, 0, theData, length, user.length);
        length +=user.length;

        System.arraycopy(approve, 0, theData, length, approve.length);
        length +=approve.length;

        System.arraycopy(md5Pwd, 0, theData, length, md5Pwd.length);

        return theData;
    }

    private static void getCarInfo(String platenumber){
        UDPRequestCtrl.getInstance().request(new CarBaseInfoBean(platenumber), new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {

                BaseVo dataVo = successResult.getDataVo();
                if (dataVo != null) {
                    CarBaseInfoBean mCarInfoBean = (CarBaseInfoBean) dataVo;

                    LogUtils.error("getCarList Success  ： PlateNumber" + mCarInfoBean.getResultPlateNumber() + "\ninfo : " + mCarInfoBean.toString());
                }

            }

            @Override
            public void onError(LoadResult errorResult) {
                System.out.println("getCarInfo error......................");
            }
        });
    }

    public static void getCarstatus(String platenumber){

        CarLatestStatus carLatestStatus = new CarLatestStatus(platenumber);

        UDPRequestCtrl.getInstance().request(carLatestStatus, new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {

                BaseVo dataVo = successResult.getDataVo();
                if(dataVo!=null){
                    CarLatestStatus mCarLatestStatus = (CarLatestStatus) dataVo;
                    String s = mCarLatestStatus.toString();
                    System.out.println("getCarstatus success    " + s);
                   /* int resultStatus = mCarLatestStatus.getResultStatus();
                    CarStatusInfoTool carStatusInfoTool = new CarStatusInfoTool(MainApplication.getInstance(), resultStatus);
                    String carStatusString = "";
                    for(int i = 0; i<16;i++){
                        String statusName = carStatusInfoTool.getStatusName(i);
                        String status = carStatusInfoTool.getStatus(i);
                        carStatusString += statusName+":"+status + "  ;  ";
                    }

                    int resultAlarmType = mCarLatestStatus.getResultAlarmType();
                    List<String> alarmList = AlarmInfoTool.getAlarmList(resultAlarmType);
                    String alarms = "";
                    for(String alarm : alarmList){
                        alarms+= alarm+", ";
                    }*/

                }

            }

            @Override
            public void onError(LoadResult errorResult) {

            }
        });

    }

    public static void getCarHistory(String platenumber, String startTime, String endTime){

        CarHistoricalRouteBean carHistoricalRouteBean = new CarHistoricalRouteBean(startTime, endTime, platenumber);


        UDPRequestCtrl.getInstance().request(carHistoricalRouteBean, new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {
                BaseVo dataVo = successResult.getDataVo();
                if(dataVo!=null){
                    CarHistoricalRouteBean mCarHistoricalRouteBean = (CarHistoricalRouteBean) dataVo;
//                    mCarHistoricalRouteBean.toString();
                    if(mCarHistoricalRouteBean.isend()){
                        System.out.println("getCarHistory - - - - - - - - - -  - - - - - - - - - -  - - - - - - - - - -  - - - - - - - - - -  - - - - - - - - - - end ");
                        mCarHistoricalRouteBean.toString();

//                        getCompanyAllCarInfo();
                        getDesignatedAreaCarInfo();
                    }
                }

            }

            @Override
            public void onError(LoadResult errorResult) {

            }
        });
    }

    public static void getCompanyAllCarInfo(){
        String companyName = "东莞市翔运小汽车出租有限公司";

        UDPRequestCtrl.getInstance().request(new CompanyAllCarBaseInfo(companyName), new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {
                BaseVo dataVo = successResult.getDataVo();
                if(dataVo!=null){
                    CompanyAllCarBaseInfo mCompanyAllCarBaseInfo = (CompanyAllCarBaseInfo) dataVo;
                    mCompanyAllCarBaseInfo.toString();
                }

            }

            @Override
            public void onError(LoadResult errorResult) {

                System.out.println("getCompanyAllCarInfo error - - - - - - - - - -  - - - - - - - - - -  - - - - - - - - - -  - - - - - - - - - -  - - - - - - - - - -  ");
            }
        });
    }

    public static void getDesignatedAreaCarInfo(){

        int min_longitude=113761309;
        int min_latitude=23017059;
        int max_longitude=113789174;
        int max_latitude=23062911;
        QueryDesignatedAreaCarInfo queryDesignatedAreaCarInfo = new QueryDesignatedAreaCarInfo(min_longitude, min_latitude, max_longitude, max_latitude);

        UDPRequestCtrl.getInstance().request(queryDesignatedAreaCarInfo, new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {
                BaseVo dataVo = successResult.getDataVo();
                if(dataVo !=null){
                    QueryDesignatedAreaCarInfo mQueryDesignatedAreaCarInfo = (QueryDesignatedAreaCarInfo) dataVo;
                    mQueryDesignatedAreaCarInfo.toString();
                }

            }

            @Override
            public void onError(LoadResult errorResult) {

            }
        });

    }

    public static void getCarBaseListResponse(){
        long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1=new Date(time);
        String operationInfo = "ALLCarBaseInfoList "+format.format(d1);
        ResponseCarListBean mResponseCarListBean = new ResponseCarListBean(operationInfo);
        UDPRequestCtrl.getInstance().request(mResponseCarListBean, new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {
                BaseVo dataVo = successResult.getDataVo();
                if(dataVo !=null){
                    ResponseCarListBean mResponseCarListBean = (ResponseCarListBean) dataVo;
                    mResponseCarListBean.toString();
                }else{
                    LogUtils.error("getCarBaseListResponse dataVo is null  ");
                }

            }

            @Override
            public void onError(LoadResult errorResult) {
                LogUtils.error("getCarBaseListResponse onError : "+errorResult.getMessage());
            }
        });
    }

    public static void getCarBaseResponse(){
        ResponseCarBaseInfoBean responseCarBaseInfoBean = new ResponseCarBaseInfoBean("蓝 粤SYH537");

        UDPRequestCtrl.getInstance().request(responseCarBaseInfoBean, new ResponseListener() {

            @Override
            public void onSuccess(LoadResult successResult) {
                BaseVo dataVo = successResult.getDataVo();
                if(dataVo !=null){
                    ResponseCarBaseInfoBean mResponseCarBaseInfoBean = (ResponseCarBaseInfoBean) dataVo;
                    System.out.println(mResponseCarBaseInfoBean.toString());
                }else{
                    LogUtils.error("getCarBaseListResponse dataVo is null  ");
                }
            }

            @Override
            public void onError(LoadResult errorResult) {
                LogUtils.error("getCarBaseListResponse onError : "+errorResult.getMessage());
            }
        });
    }

    public static void getCarStatusInfoResponse(){
        ResponseCarStatusInfoBean responseCarStatusInfoBean = new ResponseCarStatusInfoBean("蓝 粤SYH537");

        UDPRequestCtrl.getInstance().request(responseCarStatusInfoBean, new ResponseListener() {

            @Override
            public void onSuccess(LoadResult successResult) {
                BaseVo dataVo = successResult.getDataVo();
                if(dataVo !=null){
                    ResponseCarStatusInfoBean mResponseCarStatusInfoBean = (ResponseCarStatusInfoBean) dataVo;
                    System.out.println(mResponseCarStatusInfoBean.toString());
                }else{
                    LogUtils.error("getCarBaseListResponse dataVo is null  ");
                }
            }

            @Override
            public void onError(LoadResult errorResult) {
                LogUtils.error("getCarBaseListResponse onError : "+errorResult.getMessage());
            }
        });
    }

    public static void getCarHistoricalResponse(){
        String platenumber = "蓝 粤SW482J";

        long time=System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1=new Date(time);
        String operationInfo = "CarHistorical "+format.format(d1);

        ResponseCarHistoricalRouteBean mResponseCarHistoricalRouteBean = new ResponseCarHistoricalRouteBean(operationInfo, format.format(new Date((time - (60 * 60 * 1000)))), format.format(d1),platenumber);


        UDPRequestCtrl.getInstance().request(mResponseCarHistoricalRouteBean, new ResponseListener() {

            @Override
            public void onSuccess(LoadResult successResult) {
                BaseVo dataVo = successResult.getDataVo();
                if(dataVo !=null){
                    ResponseCarHistoricalRouteBean mResponseCarHistoricalRouteBean = (ResponseCarHistoricalRouteBean) dataVo;
                    System.out.println(mResponseCarHistoricalRouteBean.toString());
                }else{
                    LogUtils.error("getCarHistoricalResponse dataVo is null  ");
                }
            }

            @Override
            public void onError(LoadResult errorResult) {
                LogUtils.error("getCarHistoricalResponse onError : "+errorResult.getMessage());
            }
        });
    }

    public static void getViolationLogResponse(){
        String platenumber = "蓝 粤SU014R";

        long time=System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1=new Date(time);
        String operationInfo = "ViolationLog "+format.format(d1);

        ResponseViolationLogBean mResponseViolationLogBean = new ResponseViolationLogBean(operationInfo, format.format(new Date((time - (14 * 60 * 60 * 1000)))), format.format(d1),platenumber);


        UDPRequestCtrl.getInstance().request(mResponseViolationLogBean, new ResponseListener() {

            @Override
            public void onSuccess(LoadResult successResult) {
                BaseVo dataVo = successResult.getDataVo();
                if(dataVo !=null){
                    ResponseViolationLogBean mResponseViolationLogBean = (ResponseViolationLogBean) dataVo;
                    System.out.println(mResponseViolationLogBean.toString());
                }else{
                    LogUtils.error("getCarHistoricalResponse dataVo is null  ");
                }
            }

            @Override
            public void onError(LoadResult errorResult) {
                LogUtils.error("getCarHistoricalResponse onError : "+errorResult.getMessage());
            }
        });
    }
}
