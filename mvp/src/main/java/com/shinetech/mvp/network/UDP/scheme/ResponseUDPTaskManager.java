package com.shinetech.mvp.network.UDP.scheme;

import android.text.TextUtils;

import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.CarInfoDB;
import com.shinetech.mvp.DB.bean.CreateMaintenanceInfoDB;
import com.shinetech.mvp.DB.bean.PushMessage;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.R;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.SocketUtils;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.bean.item.DataInfo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.item.ResponseInfo;
import com.shinetech.mvp.network.UDP.bean.item.UDPTask;
import com.shinetech.mvp.network.UDP.bean.orderBean.BaseOrderInfoVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.CarAllCostVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.CarCostItemInfoVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.CarCostVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.OrderCtrlResultVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.SelectOrderByNumberVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.SelectOrderListVo;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.resopnsebean.AckPacket;
import com.shinetech.mvp.network.UDP.resopnsebean.ListCountBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarBaseInfoBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarHistoricalRouteBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarHistoricalRouteItem;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarListBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarStatusInfoBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseMessagePushBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseViolationLogBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ViolationLogItem;
import com.shinetech.mvp.network.UDP.scheme.base.BaseParsedUDPDataTool;
import com.shinetech.mvp.network.UDP.scheme.base.BaseUDPTaskManager;
import com.shinetech.mvp.sort.ResponseInfoComparator;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.TimeUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 应答式 UDP 任务管理类
 * Created by ljn on 2017-06-05.
 */
public class ResponseUDPTaskManager extends BaseUDPTaskManager {

    /**
     * 需响应的数据包
     */
    protected final LinkedHashMap<Short, UDPTask> responseDataQueue = new LinkedHashMap<>();

    /**
     * 普通接收任务线程
     */
    Thread mThread;

    /**
     * 心跳接收任务线程
     */
    Thread mHeartBeatThread;

    Thread mBaseReceiveThread;

    private short currentSubPackageProtocol = -1;

    /**
     * 响应超时任务线程
     */
    Thread mResponseTimeOutThread;

    /**
     * 需处理的数据包（阻塞型，当无任务时为了阻塞线程，提高流畅度减少GC）
     */
    protected final BlockingDeque<ResponseInfo> receiveDataQueue = new LinkedBlockingDeque<>();

    /**
     * 心跳接收包队列（对心跳包做单独处理，避免接收数据包多而导致心跳回应慢）
     */
    protected final BlockingDeque<ResponseInfo> receiveHeartBeatQueue = new LinkedBlockingDeque<>();



    protected final BlockingDeque<ResponseInfo> receivePackageBeatQueue = new LinkedBlockingDeque<>();

    /**
     * 超时任务队列（服务器有对发送应答，但无数据响应或数据发送不全）
     */
    protected final BlockingDeque<UDPTask> responseTimeOutQueue = new LinkedBlockingDeque<>();



    public ResponseUDPTaskManager() {
        super(new ResponseUDPDataTool());

    }

    @Override
    protected void saveUnFinishTask(int index, UDPTask mUDPTask) {
        synchronized (unFinishDataQueue) {
            try {
                //必须在unFinishProtocolQueue之前添加，否则会出现找不到的bug
                mUDPTask.isUseFrameNoKey = true;

                List<DataInfo> dataInfoList = mUDPTask.getDataInfo();

                if(dataInfoList == null || dataInfoList.size() == 0){
                    return;
                }

                if(index == -1) {

                    for(DataInfo dataInfo : dataInfoList){

                        unFinishDataQueue.put(dataInfo.frameNo, mUDPTask);
                        unFinishProtocolQueue.putLast(dataInfo.frameNo);
                    }
                }else{

                    if(dataInfoList.size()>index){

                        DataInfo dataInfo = dataInfoList.get(index);

                        unFinishDataQueue.put(dataInfo.frameNo, mUDPTask);
                        unFinishProtocolQueue.putLast(dataInfo.frameNo);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void saveRequestTask(int index, UDPTask mUDPTask) {

        try {
//            isUseFrameNoKey
            List<DataInfo> dataInfoList = mUDPTask.getDataInfo();
            if(dataInfoList!= null && dataInfoList.size()>index){
                DataInfo dataInfo = dataInfoList.get(index);
                requestedDataQueue.put(dataInfo.frameNo, mUDPTask);
                requestedProtocolQueue.putLast(dataInfo.frameNo);

//                if(debug)System.out.println("requestedDataQueue .size "+ requestedDataQueue.size() + "   frameNo ： "+ dataInfo.frameNo);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

    private UDPTask removeRequestTask(short frameNo){
        try{
            requestedProtocolQueue.remove(frameNo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return requestedDataQueue.remove(frameNo);
    }

    @Override
    protected void receiveData(ResponseInfo mResponseInfo) {
        try {

//            if(mResponseInfo == null){
                mResponseInfo = new ResponseInfo();
//            }

//          获取接收服务器返回的数据包
            DatagramPacket datagramPacket = SocketUtils.getInstance().receiveData(mResponseInfo);

            if(datagramPacket == null){
                return;
            }

            //            System.out.println("       receiveData   datagramPacket : "+datagramPacket.getLength());
            mResponseInfo.packetLength = datagramPacket.getLength();

            /*byte[] tempBuffer = new byte[mResponseInfo.packetLength];
            System.arraycopy(datagramPacket.getData(), 0, tempBuffer, 0, datagramPacket.getLength());
            System.out.println("      receiveData   datagramPacket : "+mBaseParsedUDPDataTool.bytes2HexString(tempBuffer));*/
            receivePackageBeatQueue.put(mResponseInfo);
//            receivePackageBeatQueue.put((ResponseInfo) mResponseInfo.clone());

            baseReceiveDisposeTask();

        } catch (IOException e) {
            //通知数据发送线程重发数据
            e.printStackTrace();
            try {
                SocketUtils.getInstance().connect();
            }catch (Exception e2){
                e2.printStackTrace();
            }

            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 心跳数据包的统一处理线程
     */
    private void baseReceiveDisposeTask(){
        if(mBaseReceiveThread == null){

            mBaseReceiveThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!isExit){
                        ResponseInfo mResponseInfo = null;
                        try {
                            mResponseInfo = receivePackageBeatQueue.takeFirst();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(mResponseInfo == null){
                            continue;
                        }

                        try {

                            //      对返回的数据包数据的解析，存储到ResponseInfo中
                            mBaseParsedUDPDataTool.decodeResponseData(mResponseInfo, mResponseInfo.dataBytes, mResponseInfo.packetLength);

                            //心跳接收包和应答包独立线程处理（防止由于后台发送数据过多，应答或心跳包处理时间被推后导致发送失败的问题）
                            if(mResponseInfo.protocol == Protocol.RESPONSE_PROTOCOL_HEART_BEAT || mResponseInfo.protocol == Protocol.RESPONSE_ACK_BEAT){
                                receiveHeartBeatQueue.put((ResponseInfo) mResponseInfo.clone());
                            }else{
                                receiveDataQueue.put((ResponseInfo) mResponseInfo.clone());
                            }

                            // 如果是车辆状态信息，对信息进行保持
//                        if (mResponseInfo.protocol == Protocol.RESPONSE_PROTOCOL_CARSTATUS_BEAT_RESULT){
        //                    System.out.println("       receiveData   protocol : "+mResponseInfo.protocol);
        //                }
//                            System.out.println("       receiveData   mResponseInfo protocol = " + mResponseInfo.protocol +"   receiveFrameNo = " + mResponseInfo.receiveFrameNo );


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        startReceiveHeartBeatDisposeTask();

                        startReceiveDataDisposeTask();

                        startResponseTimeOutTask();

                    }

                    mBaseReceiveThread = null;

                }
            });

            mBaseReceiveThread.start();
        }
    }

    /**
     * 心跳数据包的统一处理线程
     */
    private void startReceiveHeartBeatDisposeTask(){
        if(mHeartBeatThread == null){

            mHeartBeatThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!isExit){
                        disposeReceiveHeartBeat();
                    }

                    mHeartBeatThread = null;

                }
            });

            mHeartBeatThread.start();
        }
    }


    /**
     * 非心跳数据包的统一处理线程
     */
    private void startReceiveDataDisposeTask(){
        if(mThread == null){

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!isExit){
                        disposeReceiveData();
                    }

                    mThread = null;

                }
            });

            mThread.start();
        }

    }

    private void startResponseTimeOutTask(){

        if(mResponseTimeOutThread == null){
            mResponseTimeOutThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!isExit){
                        disposeResponseTimeOut();
                    }
                    mResponseTimeOutThread = null;
                }
            });

            mResponseTimeOutThread.start();
        }
    }

    /**
     * 心跳数据包处理
     */
    private void disposeReceiveHeartBeat(){
        ResponseInfo mResponseInfo = null;
        try {

            mResponseInfo = receiveHeartBeatQueue.takeFirst();

            if(mResponseInfo == null){
                return;
            }

//            System.out.println("disposeReceiveHeartBeat   mResponseInfo : "+mBaseParsedUDPDataTool.bytes2HexString(mResponseInfo.dataBytes));

            //System.out.println("disposeReceiveHeartBeat   mResponseInfo protocol : " + mResponseInfo.protocol + "  receiveFrameNo : " + mResponseInfo.receiveFrameNo);

            disposeReceiveData(mResponseInfo);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收到的数据包，统一处理
     */
    private void disposeReceiveData(){

        ResponseInfo mResponseInfo = null;
        try {

            mResponseInfo = receiveDataQueue.takeFirst();

            if(mResponseInfo == null){
                return;
            }

//            System.out.println("disposeReceiveData   mResponseInfo : "+mBaseParsedUDPDataTool.bytes2HexString(mResponseInfo.dataBytes));

//            System.out.println("disposeReceiveData   mResponseInfo protocol : " + mResponseInfo.protocol + "  receiveFrameNo : " + mResponseInfo.receiveFrameNo);

            disposeReceiveData(mResponseInfo);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void disposeResponseTimeOut(){

        UDPTask udpTask = null;
        int size = responseTimeOutQueue.size();
        if(size == 0){
            try {
                //获取第一个，是为了让线程阻塞
                udpTask = responseTimeOutQueue.takeFirst();
                responseTimeOutQueue.addFirst(udpTask);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        long sleepTime = 30 *1000;

        for(int i = 0; i <size ; i++) {

            try {
                udpTask = responseTimeOutQueue.takeFirst();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (udpTask == null || udpTask.isResponseFinish()) {
                continue;
            }

            long responseTime = udpTask.getResponseTime();

            long currentTimeMillis = System.currentTimeMillis();

            long elapsedTime = currentTimeMillis - responseTime;

            if (elapsedTime > (30 * 1000)) {
                //TODO time out
                BaseVo dataVo = udpTask.getDataVo();

                ResponseListener responseListener = udpTask.getmResponseListener();

                //如果心跳通信有应答，就说明通信正常，不必等待后台发送通信保持包才认为通信正常
                if(dataVo != null && dataVo.getResponseProtocolHead() == Protocol.RESPONSE_PROTOCOL_HEART_BEAT){
                    if(udpTask.getDataInfo()!= null && udpTask.getDataInfo().size()>0 && udpTask.getDataInfo().get(0).isSendSuccess()){
                        continue;
                    }
                }

                if(currentSubPackageProtocol != -1 && currentSubPackageProtocol == dataVo.getResponseProtocolHead()){
                    List<ResponseInfo> responseInfos = responseSubpackageDataQueue.get(dataVo.getResponseProtocolHead());
                    if(responseInfos!= null && responseInfos.size()>0){
                        responseInfos.clear();
                        responseSubpackageDataQueue.put(dataVo.getResponseProtocolHead(), responseInfos);
                        LogUtils.error("ResponseTimeOut  protocol = "+dataVo.getResponseProtocolHead()+"- - - - - - - - - - - - - - responseInfos size = "+ responseSubpackageDataQueue.get(dataVo.getResponseProtocolHead()).size());
                    }
                }

                responseDataQueue.remove(dataVo.getResponseProtocolHead());

                if (dataVo != null && responseListener != null) {
                    dispatchResult(LoadResult.TIME_OUT.setDataVo(dataVo), responseListener);
                }

                //LogUtils.error("- - - - - - - - - - - - - - - - - - - - - - disposeResponseTimeOut  time out  ResponseProtocolHead = " + dataVo.getResponseProtocolHead() + "   frameNo = "+udpTask.getDataInfo().frameNo);

            } else {
                responseTimeOutQueue.addLast(udpTask);
                long time = (30 * 1000) - elapsedTime;
                if(sleepTime>time){
                    sleepTime = time;
                }
            }
        }

        //避免睡眠30S，直接阻塞去
        if(responseTimeOutQueue.size() == 0){
            return;
        }

        try {
            //LogUtils.error("- - - - - - - - - - - - - - - - - - - - - - disposeResponseTimeOut  sleep time = "+sleepTime);
            Thread.currentThread().sleep(sleepTime);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据数据状态，处理接收的数据
     * @param mResponseInfo
     */
    private void disposeReceiveData(ResponseInfo mResponseInfo){
        try {
            switch(mResponseInfo.resultStatusCode){

                case ResponseInfo.REPEAT_PACKET_RECEIVED:
                    if(isDebug)LogUtils.error("重复数据包");
                    DatagramPacket dataReceivedPacket = createAckPacket(mResponseInfo.receiveFrameNo);
                    synchronized (MainApplication.getInstance()) {
                        SocketUtils.getInstance().sendPacket(dataReceivedPacket);
                    }
                    return;

                case ResponseInfo.ACK_PACKET_RECEIVED:

                    if(isDebug)LogUtils.error("接受应答数据包");

                    //TODO 从超时任务列表中移除，并将任务存储到响应数据包列表
                    short receiveFrameNo = mResponseInfo.receiveFrameNo;
                    UDPTask mUDPTask = removeRequestTask(receiveFrameNo);

                    if(mUDPTask == null) {
                        return;
                    }

                    DataInfo mDataInfo = null;
                    boolean isreceiveEnd = true;
                    int progress = 0;
                    //获取对应的dataInfo
                    List<DataInfo> dataInfoList = mUDPTask.getDataInfo();
                    if(dataInfoList!= null){
                        for(DataInfo item : dataInfoList){

                            if(item.frameNo == receiveFrameNo){
                                mDataInfo = item;
                                //设置发送成功，使发送超时过滤掉该任务
                                mDataInfo.setSendSuccess(true);
                            }

                            if(item.isSendSuccess()){
                                progress++;
                            }

                            isreceiveEnd = isreceiveEnd && item.isSendSuccess();

                        }
                    }

                    //特殊处理意见反馈应答
                    BaseVo dataVo = mUDPTask.getDataVo();
                    if(dataVo != null && dataVo.getResponseProtocolHead() == Protocol.RESPONSE_PROTOCOL_FEEDBACK_BEAT){


                        System.out.println("disposeReceiveData 意见反馈  receiveFrameNo = " + receiveFrameNo);
                        //回掉成功
                        ResponseListener responseListener = mUDPTask.getmResponseListener();

                        dispatchResult(LoadResult.STATUS_SUCCESS.setDataVo(dataVo), responseListener);

                        break;
                    }

//                    System.out.println("  刷新进度 ---> 进度值 : "+progress +"   ， 总大小 : "+  dataInfoList.size() );

                    if(isreceiveEnd) {
                         if(isDebug)System.out.println("  receive End mResponseInfo.receiveFrameNo : " + mResponseInfo.receiveFrameNo);

                        responseDataQueue.put(mUDPTask.getDataVo().getResponseProtocolHead(), mUDPTask);

                        try {
                            mUDPTask.setResponseTime(System.currentTimeMillis());
                            responseTimeOutQueue.put(mUDPTask);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //TODO updata  progress 刷新进度   progress 进度值， 总大小 dataInfoList.size()


                    return;

                case ResponseInfo.HEART_BEAT_PACKET_RECEIVED:

                    if(isDebug)LogUtils.error("心跳链接");
                    DatagramPacket ackPacket =createAckPacket(mResponseInfo.receiveFrameNo);
                    synchronized (MainApplication.getInstance()) {
                        SocketUtils.getInstance().sendPacket(ackPacket);
                    }

                    //从响应任务缓存中获取对应的任务
                    callBackSuccess(mResponseInfo);
                    //TODO timer 60s (倒计时60s，60s后还无下个心跳包接收视为断开连接)
                    return;

                case ResponseInfo.INVALID_DATA_LENGTH:
                    if(isDebug)LogUtils.error("数据包长度校验失败，数据包被丢弃");
                    DatagramPacket dataErrorPacket = createAckPacket(mResponseInfo.receiveFrameNo);
                    synchronized (MainApplication.getInstance()) {
                        SocketUtils.getInstance().sendPacket(dataErrorPacket);
                    }
                    //从响应任务缓存中获取对应的任务
                    callBackDataError(mResponseInfo.receiveFrameNo);

                    return;

                case ResponseInfo.RECEIVE_SUCCESS:
//                    if(debug)LogUtils.error("接收数据成功 发送应答   receiveFrameNo = " +mResponseInfo.receiveFrameNo);
                    DatagramPacket successAckPacket = createAckPacket(mResponseInfo.receiveFrameNo);
                    synchronized (MainApplication.getInstance()) {
                        SocketUtils.getInstance().sendPacket(successAckPacket);
                    }

                    if(mResponseInfo.packetTotal == 1) {
                        //从响应任务缓存中获取对应的任务
                        callBackSuccess(mResponseInfo);
                    }else{
                        //TODO 分包处理
//                        System.out.println("分包处理 - - - - -  currentSubPackageProtocol = "+currentSubPackageProtocol+"  - - - - - - - - - - packetTotal = " +mResponseInfo.packetTotal +"    packetSerialNumber = "+ mResponseInfo.packetSerialNumber);

                        if(currentSubPackageProtocol == -1){
                            currentSubPackageProtocol = mResponseInfo.protocol;
                        }

                        List<ResponseInfo> responseInfos = responseSubpackageDataQueue.get(mResponseInfo.protocol);

                        if(responseInfos == null || responseInfos.size() == 0){
                            responseInfos = new ArrayList<>();
                            responseInfos.add(mResponseInfo);
                        }else {
                            if (!responseInfos.contains(mResponseInfo)) {
                                responseInfos.add(mResponseInfo);
                            }
                        }

//                        System.out.println("分包处理 - - - - - - - - - - - responseInfos.size = "+responseInfos.size());

                        UDPTask mSubPackageUDPTask = responseDataQueue.get(mResponseInfo.protocol);

                        if(mSubPackageUDPTask != null){
                            //跟新下响应超时时间，防止超时了。
                            mSubPackageUDPTask.setResponseTime(System.currentTimeMillis());
                            ResponseListener responseListener = mSubPackageUDPTask.getmResponseListener();
                            if(responseListener != null){
                                responseListener.onProgress(mResponseInfo.packetTotal, responseInfos.size(), mResponseInfo.protocol);
                            }
                        }

                        short packetTotal = mResponseInfo.packetTotal;
                        if(packetTotal == responseInfos.size()){
//                            responseSubpackageDataQueue.remove(mResponseInfo.protocol);
                            //解析所有数据包
                            callBackSuccess(mResponseInfo.protocol, responseInfos);
                        }else {
                            responseSubpackageDataQueue.put(mResponseInfo.protocol, responseInfos);
                        }

                    }

                    break;
                default:
                    return;
            }
        } catch (IOException e) {
            //通知数据发送线程重发数据
            e.printStackTrace();
            return;
        }
    }


    /**
     * 数据包长度校验失败回掉
     * @param mFrameNo 响应帧号
     */
    private void callBackDataError(short mFrameNo){
        if(isDebug)System.out.println("requestedDataQueue .size " + requestedDataQueue.size() + "   remove response mFrameNo ： " + mFrameNo);

        UDPTask mDataErrorUDPTask = requestedDataQueue.get(mFrameNo);
        List<DataInfo> dataInfoList = mDataErrorUDPTask.getDataInfo();
        //移除该任务所有未发送的包
        if(dataInfoList != null && dataInfoList.size()>0){
            for(DataInfo mDataInfo : dataInfoList) {
                //移除已发送但未响应的任务
                if(requestedProtocolQueue.contains(mDataInfo.frameNo)) {
                    try {
                        requestedProtocolQueue.remove(mDataInfo.frameNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    requestedDataQueue.remove(mDataInfo.frameNo);
                }

                //移除未发送的任务
                if(unFinishProtocolQueue.contains(mDataInfo.frameNo)) {
                    unFinishProtocolQueue.remove(mDataInfo.frameNo);
                    unFinishDataQueue.remove(mDataInfo.frameNo);
                }
            }
        }

        if(mDataErrorUDPTask==null){
            return;
        }

        //设置已完成接收
        mDataErrorUDPTask.setResponseTime(System.currentTimeMillis());
        mDataErrorUDPTask.setIsResponseFinish(true);

        BaseVo dataVo = mDataErrorUDPTask.getDataVo();

        ResponseListener responseListener = mDataErrorUDPTask.getmResponseListener();

        if(dataVo==null || responseListener==null){
            return;
        }

        dispatchResult(LoadResult.NO_DATA.setDataVo(dataVo), responseListener);
    }

    /**
     * 成功获取数据回掉
     * @param mResponseInfo 响应数据
     */
    private void callBackSuccess(ResponseInfo mResponseInfo){

//        short mFrameNo = mResponseInfo.receiveFrameNo;

        short protocol = mResponseInfo.protocol;

        if(isDebug)System.out.println("responseDataQueue .size " + responseDataQueue.size() + "   response protocol ： " + protocol);
        switch (protocol){
            case Protocol.RESPONSE_PROTOCOL_CARINFO_BEAT_RESULT:
                // A.3.11　车辆基本信息 （协议字：0x000a) 处理
                disposeCarBaseInfoResponse(mResponseInfo);
                break;
            case Protocol.RESPONSE_SIZE_BEAT:
                //A.3.9　列表数量 （协议字：0x0008) 处理
                disposeCountResponse(mResponseInfo);
                break;
            case Protocol.RESPONSE_PROTOCOL_CARHISTORICALROUTE_BEAT_RESULT:
                //车辆历史轨迹 （协议字：0x000e) 处理
                disposeHistoricalRouteResponse(mResponseInfo);
                break;
            case Protocol.RESPONSE_PROTOCOL_VIOLATIONLOG_BEAT_RESULT:
                //车辆违规日志（协议字：0x0014）处理
                disposeViolationLog(mResponseInfo);
                break;
            case Protocol.RESPONSE_PROTOCOL_CAR_ALL_COST_RESULT:
                    //A.3.36 车辆费用应答-总费（协议字：0x0017）
                disposeCarAllCostResponse(mResponseInfo);
                break;
            case Protocol.RESPONSE_PROTOCOL_CARCOST_INFO_BEAT:
                //A.3.37车辆费用应答-项目明细（协议字：0x0018）
                disposeCarCostResponse(mResponseInfo);
                break;
            case Protocol.RESPONSE_PROTOCOL_SELECT_ORDER_BEAT_RESULT:
                //A.3.27　工单列表（协议字:0x001A）
                disposeOrderInfoList(mResponseInfo);
                break;
            case Protocol.RESPONSE_PROTOCOL_ORDER_CTRL_BEAT_RESULT:
                //A.3.29　工单操作结果 (协议字:0x001C)
                disposeOrderCtrlResult(mResponseInfo);
                break;

            default:
                disposeResponse(mResponseInfo,null);
                break;

        }


        if(currentSubPackageProtocol != -1 && currentSubPackageProtocol == protocol){
            List<ResponseInfo> responseInfos = responseSubpackageDataQueue.get(protocol);
            if(responseInfos!= null && responseInfos.size()>0){
                responseInfos.clear();
                responseSubpackageDataQueue.put(protocol, responseInfos);
                if(isDebug)LogUtils.error("callBackSuccess   protocol = "+protocol+"- - - - - - - - - - - - - - responseInfos size = "+ responseSubpackageDataQueue.get(protocol).size());
            }
        }

        currentSubPackageProtocol = -1;

    }

    private void callBackSuccess(short protocol, List<ResponseInfo> mResponseInfos){

        if(isDebug)System.out.println("mResponseInfos .size " + mResponseInfos.size() + "   response protocol ： " + protocol);

       /* switch (protocol) {
            case Protocol.RESPONSE_PROTOCOL_ORDER_CTRL_BEAT_RESULT:*/
//                OrderCtrlResultVo mOrderCtrlResultVo = new OrderCtrlResultVo();

                ResponseInfo responseInfo = new ResponseInfo();
                responseInfo.protocol = protocol;

                UDPTask mSubPackageUDPTask = responseDataQueue.get(protocol);

                if(mSubPackageUDPTask == null) {
                    currentSubPackageProtocol = protocol;
                    callBackSuccess(responseInfo);
                }

                //排序
                ResponseInfoComparator mResponseInfoComparator = new ResponseInfoComparator();
                Collections.sort(mResponseInfos, mResponseInfoComparator);

                byte[] allDatabytes = new byte[1400];
                int contentLength = 0;
                for(ResponseInfo item : mResponseInfos){
                    byte[] dataBytes = item.dataBytes;
                    short dataLength = (short) (item.dataLength-2);
                    if(allDatabytes == null || allDatabytes.length<(contentLength+dataLength)){
                        byte[] buffer = new byte[contentLength + dataLength];

                        if(allDatabytes != null && allDatabytes.length>0) {
                            System.arraycopy(allDatabytes, 0, buffer, 0, contentLength);
                        }

                        System.arraycopy(dataBytes, 0, buffer, contentLength, dataLength);
                        contentLength += dataLength;

                        allDatabytes = buffer;
                    }else{
                        System.arraycopy(dataBytes, 0, allDatabytes, contentLength, dataLength);
                        contentLength += dataLength;
                    }

                    //跟新下响应超时时间，防止超时了。
                    mSubPackageUDPTask.setResponseTime(System.currentTimeMillis());

//                    System.out.println("protocol : "+ item.protocol +"    dataLength = "+item.dataLength+ "     packetSerialNumber : "+ item.packetSerialNumber  +"  contentLength : "+contentLength /*+ "current packageLength : "+dataBytes.length*/);

                }

                responseInfo.dataBytes = allDatabytes;

                //A.3.29　工单操作结果 (协议字:0x001C)
//                disposeOrderCtrlResult(responseInfo);
                callBackSuccess(responseInfo);
/*
                break;
        }*/

    }

    /**
     * 基本响应处理
     * @param mResponseInfo
     * @param dataVo
     */
    private void disposeResponse(ResponseInfo mResponseInfo,BaseVo dataVo){

        UDPTask mSuccessUDPTask;

        mSuccessUDPTask = responseDataQueue.get(mResponseInfo.protocol);

        if(mSuccessUDPTask==null){
            if(isDebug)LogUtils.error("回掉监听 mSuccessUDPTask is null  protocol ： "+mResponseInfo.protocol);
//           后台推送数据处理
            isMessagePush(mResponseInfo);
            return;
        }

        if(dataVo == null){
            dataVo = mSuccessUDPTask.getDataVo();
            //将响应数据转换为dataVo中对应的属性
            mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, dataVo, false);
        }

        boolean isFinishRequest = true;

        if(dataVo != null && dataVo.isHasOrderCtrlResult()){

            isFinishRequest = false;

            //判断是否需要等待 操作结果数据
            if(mResponseInfo.protocol == Protocol.RESPONSE_PROTOCOL_SELECT_ORDER_INFO_BEAT_RESULT){

                boolean disposeOrderCtrlResult = dataVo.isDisposeOrderCtrlResult();

                if(!disposeOrderCtrlResult){
                    //设置接收完内容了。
                    dataVo.setReceiveOtherData(true);
                    //跟新下响应超时时间，防止超时
                    mSuccessUDPTask.setResponseTime(System.currentTimeMillis());
                    return;
                }else{
                    isFinishRequest = true;
                }
            }
        }

        //设置已完成接收
        mSuccessUDPTask.setResponseTime(System.currentTimeMillis());
        mSuccessUDPTask.setIsResponseFinish(true);

        //设置该任务所有数据包发送成功
        List<DataInfo> dataInfoList = mSuccessUDPTask.getDataInfo();

        if(dataInfoList != null && dataInfoList.size()>0) {

            for(DataInfo dataInfo : dataInfoList) {

                if (dataInfo != null && isFinishRequest) {

                    dataInfo.setSendSuccess(true);
                }
            }
        }

        ResponseListener responseListener = mSuccessUDPTask.getmResponseListener();

        if (dataVo != null && responseListener != null && isFinishRequest) {
            dispatchResult(LoadResult.STATUS_SUCCESS.setDataVo(dataVo), responseListener);
            responseDataQueue.remove(mResponseInfo.protocol);
        }

        // 如果是车辆状态信息，对信息进行保持
        if (mResponseInfo.protocol == Protocol.RESPONSE_PROTOCOL_CARSTATUS_BEAT_RESULT){

            if(dataVo != null && dataVo instanceof ResponseCarStatusInfoBean) {
                saveCarStatusInfoToUser((ResponseCarStatusInfoBean) dataVo);
//                System.out.println(((ResponseCarStatusInfoBean) dataVo).toString());
//                TODO save to DB

                if(MainApplication.isUseCarInfoDB) {
                    CarInfoDB carInfoDB = ((ResponseCarStatusInfoBean) dataVo).toCarInfoDB(null);
                    DBManager.insertCarInfo(carInfoDB);
                }
            }

        }else if (mResponseInfo.protocol == Protocol.RESPONSE_PROTOCOL_SELECT_ORDER_INFO_BEAT_RESULT){
            if(dataVo instanceof SelectOrderByNumberVo) {

                SelectOrderByNumberVo mSelectOrderByNumberVo = (SelectOrderByNumberVo) dataVo;
                saveOrderInfo(mSelectOrderByNumberVo);

            }
        }
    }

    /**
     * 车辆基本信息 （协议字：0x000a) 处理
     * @param mResponseInfo
     */
    private void disposeCarBaseInfoResponse(ResponseInfo mResponseInfo){
        //创建车辆列表信息类，并解析数据到此类中
        ResponseCarBaseInfoBean mResponseCarBaseInfoBean = new ResponseCarBaseInfoBean();
        mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, mResponseCarBaseInfoBean, false);

        //获取操作码
        int operationCode = mResponseCarBaseInfoBean.getOperationCode();
        UDPTask mSuccessUDPTask;

        BaseVo dataVo;
        //判断是列表查询数据返回（>0）还是单独车辆查询返回（==0）
        if(operationCode>0){
            mSuccessUDPTask = responseDataQueue.get(Protocol.RESPONSE_PROTOCOL_CARLIST_BEAT_RESULT);

            if(mSuccessUDPTask==null){
                if(isDebug)LogUtils.error("回掉监听 mSuccessUDPTask is null  protocol ： "+Protocol.RESPONSE_PROTOCOL_CARLIST_BEAT_RESULT);
                return;
            }

            //设置该任务的所有数据包发送成功
            List<DataInfo> dataInfoList = mSuccessUDPTask.getDataInfo();
            if(dataInfoList != null && dataInfoList.size()>0){
                for(DataInfo dataInfo : dataInfoList){
                    if(dataInfo!=null){
                        dataInfo.setSendSuccess(true);
                    }
                }
            }

            dataVo = mSuccessUDPTask.getDataVo();

            if(dataVo instanceof ResponseCarListBean){
                ResponseCarListBean mResponseCarListBean = (ResponseCarListBean) dataVo;
                mResponseCarListBean.addResponseCarBaseInfoBean(mResponseCarBaseInfoBean);
                //if(debug)LogUtils.error("add item : "+mResponseCarBaseInfoBean.toString());
                if(isDebug)LogUtils.error(/*"is end "+mResponseCarListBean.isEnd() +*/ "   allsize = " + mResponseCarListBean.getCarlistSize() + "   list size = "+mResponseCarListBean.getResponseCarBaseInfoList().size());

                if(mResponseCarListBean.isEnd()) {
                    //标记完成接收
                    mSuccessUDPTask.setIsResponseFinish(true);
                    ResponseListener responseListener = mSuccessUDPTask.getmResponseListener();
                    dispatchResult(LoadResult.STATUS_SUCCESS.setDataVo(dataVo), responseListener);
                    responseDataQueue.remove(Protocol.RESPONSE_PROTOCOL_CARLIST_BEAT_RESULT);
                }else{
                    //跟新接收到最后一次数据包时间
                    mSuccessUDPTask.setResponseTime(System.currentTimeMillis());
                }
            }

        }else{
            disposeResponse(mResponseInfo, mResponseCarBaseInfoBean);
        }
    }

    /**
     * 车辆历史轨迹 （协议字：0x000e) 处理
     * @param mResponseInfo
     */
    private void disposeHistoricalRouteResponse(ResponseInfo mResponseInfo){

        //创建车辆轨迹信息类
        ResponseCarHistoricalRouteItem mResponseCarHistoricalRouteItem = new ResponseCarHistoricalRouteItem();

        //解析接收的轨迹数据到车辆轨迹信息类中
        mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, mResponseCarHistoricalRouteItem, false);

        //获取查询任务类
        UDPTask mSuccessUDPTask;
        mSuccessUDPTask = responseDataQueue.get(mResponseCarHistoricalRouteItem.getResponseProtocolHead());
        if(mSuccessUDPTask==null){
            if(isDebug)LogUtils.error("回掉监听 mSuccessUDPTask is null  protocol ： "+Protocol.RESPONSE_PROTOCOL_CARHISTORICALROUTE_BEAT_RESULT);
            return;
        }

        //判断是否获取到轨迹查询类
        BaseVo dataVo = mSuccessUDPTask.getDataVo();
        if(dataVo == null || !(dataVo instanceof ResponseCarHistoricalRouteBean)){
            if(isDebug)LogUtils.error("disposeHistoricalRouteResponse dataVo is null  or dataVo is no ResponseCarHistoricalRouteBean class ");
            return;
        }

        //转轨迹查询类
        ResponseCarHistoricalRouteBean mResponseCarHistoricalRouteBean = (ResponseCarHistoricalRouteBean) dataVo;

        //对比操作码
        int operationCode = mResponseCarHistoricalRouteItem.getOperationCode();

        if(operationCode == mResponseCarHistoricalRouteBean.getOperationCode()){

            //添加到对应的轨迹查询类中
            mResponseCarHistoricalRouteBean.addToResultHistoricalInfo(mResponseCarHistoricalRouteItem);

            //回掉，接受完数据
            if(mResponseCarHistoricalRouteBean.isEnd()) {
                //标记完成接收
                mSuccessUDPTask.setIsResponseFinish(true);
                ResponseListener responseListener = mSuccessUDPTask.getmResponseListener();
                dispatchResult(LoadResult.STATUS_SUCCESS.setDataVo(dataVo), responseListener);
                responseDataQueue.remove(mResponseCarHistoricalRouteItem.getResponseProtocolHead());
            }else{
                //跟新接收到最后一次数据包时间
                mSuccessUDPTask.setResponseTime(System.currentTimeMillis());
            }
        }

    }

    /**
     * 车辆违规日志（协议字：0x0014）
     * @param mResponseInfo
     */
    private void disposeViolationLog(ResponseInfo mResponseInfo){
        //创建车辆违规信息类，并解析数据到此类中
        ViolationLogItem mViolationLogItem =  new ViolationLogItem();
        mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, mViolationLogItem, false);

        //获取查询违规任务类
        UDPTask mSuccessUDPTask;
        mSuccessUDPTask = responseDataQueue.get(mViolationLogItem.getResponseProtocolHead());
        if(mSuccessUDPTask==null){
            if(isDebug)LogUtils.error("回掉监听 mSuccessUDPTask is null  protocol ： "+Protocol.RESPONSE_PROTOCOL_VIOLATIONLOG_BEAT_RESULT);
            return;
        }

        //判断是否获取到违规查询类
        BaseVo dataVo = mSuccessUDPTask.getDataVo();
        if(dataVo == null || !(dataVo instanceof ResponseViolationLogBean)){
            if(isDebug)LogUtils.error("ResponseViolationLogBean dataVo is null  or dataVo is no ResponseViolationLogBean class ");
            return;
        }

        //转违规查询类
        ResponseViolationLogBean mResponseViolationLogBean = (ResponseViolationLogBean) dataVo;

        //对比操作码
        int operationCode = mViolationLogItem.getOperationCode();

        if(operationCode == mResponseViolationLogBean.getOperationCode()){
            //添加到对应的轨迹查询类中
            mResponseViolationLogBean.addToViolationLog(mViolationLogItem);

            //回掉，接受完数据
            if(mResponseViolationLogBean.isEnd()) {
                //标记完成接收
                mSuccessUDPTask.setIsResponseFinish(true);
                ResponseListener responseListener = mSuccessUDPTask.getmResponseListener();
                dispatchResult(LoadResult.STATUS_SUCCESS.setDataVo(dataVo), responseListener);
                responseDataQueue.remove(mViolationLogItem.getResponseProtocolHead());
            }else{
                //跟新接收到最后一次数据包时间
                mSuccessUDPTask.setResponseTime(System.currentTimeMillis());
            }
        }

    }

    /**
     * A.3.37车辆费用应答-项目明细（协议字：0x0018） 处理
     * @param mResponseInfo
     */
    private void disposeCarCostResponse(ResponseInfo mResponseInfo){

        //创建费用明细信息类
        CarCostItemInfoVo mCarCostItemInfoVo = new CarCostItemInfoVo();

        //解析接收的轨迹数据到车辆轨迹信息类中
        mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, mCarCostItemInfoVo, false);

        //获取查询任务类
        UDPTask mSuccessUDPTask;
        mSuccessUDPTask = responseDataQueue.get(mCarCostItemInfoVo.getResponseProtocolHead());
        if(mSuccessUDPTask==null){
            if(isDebug)LogUtils.error("回掉监听 mSuccessUDPTask is null  protocol ： "+Protocol.RESPONSE_PROTOCOL_CARCOST_INFO_BEAT);
            return;
        }

        //判断是否获取到轨迹查询类
        BaseVo dataVo = mSuccessUDPTask.getDataVo();
        if(dataVo == null || !(dataVo instanceof CarCostVo)){
            if(isDebug)LogUtils.error("disposeCarCostResponse dataVo is null  or dataVo is no CarCostVo class ");
            return;
        }

        //转车辆费用查询类
        CarCostVo mCarCostVo = (CarCostVo) dataVo;

        //对比车牌号
        String plateNumber = mCarCostVo.getPlateNumber();
        if(!TextUtils.isEmpty(plateNumber) && plateNumber.equals(mCarCostItemInfoVo.getPlateNumber())) {

            //添加到对应的车辆费用查询类中
            mCarCostVo.addCarCostItem(mCarCostItemInfoVo);

            List<CarCostItemInfoVo> carCostItems = mCarCostVo.getCarCostItems();
            //回掉，接受完数据
            if (carCostItems.size() == mCarCostItemInfoVo.getSumTotal()) {
                //标记完成接收
                mSuccessUDPTask.setIsResponseFinish(true);
                ResponseListener responseListener = mSuccessUDPTask.getmResponseListener();
                dispatchResult(LoadResult.STATUS_SUCCESS.setDataVo(dataVo), responseListener);
                responseDataQueue.remove(mCarCostVo.getResponseProtocolHead());
            } else {
                //跟新接收到最后一次数据包时间
                mSuccessUDPTask.setResponseTime(System.currentTimeMillis());
            }
        }

    }

    /**
     * A.3.27　工单列表（协议字:0x001A）
     * @param mResponseInfo
     */
    public void disposeOrderInfoList(ResponseInfo mResponseInfo){

        BaseOrderInfoVo baseOrderInfoVo = new BaseOrderInfoVo();

//        System.out.println(" - - - - - - - - - "+ BaseParsedUDPDataTool.bytes2HexString(mResponseInfo.dataBytes));
        mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, baseOrderInfoVo, false);

        //获取查询任务类
        UDPTask mSuccessUDPTask;
        mSuccessUDPTask = responseDataQueue.get(baseOrderInfoVo.getResponseProtocolHead());
        if(mSuccessUDPTask==null){
            if(isDebug)LogUtils.error("回掉监听 mSuccessUDPTask is null  protocol ： "+Protocol.RESPONSE_PROTOCOL_SELECT_ORDER_BEAT_RESULT);
            return;
        }

        //判断是否获取到工单查询类
        BaseVo dataVo = mSuccessUDPTask.getDataVo();
        if(dataVo == null || !(dataVo instanceof SelectOrderListVo)){
            if(isDebug)LogUtils.error("disposeCarCostResponse dataVo is null  or dataVo is no SelectOrderListVo class ");
            return;
        }

        //转工单查询类
        SelectOrderListVo mSelectOrderListVo = (SelectOrderListVo) dataVo;
        //添加到对应的车辆费用查询类中
        mSelectOrderListVo.addOrderInfoList(baseOrderInfoVo);
//        System.out.println(baseOrderInfoVo.toString());

        //存储到数据库
        baseOrderInfoVo.saveDB();

//        List<CarCostItemInfoVo> carCostItems = mCarCostVo.getCarCostItems();
        List<BaseOrderInfoVo> orderInfoList = mSelectOrderListVo.getOrderInfoList();
        //回掉，接受完数据
        if (orderInfoList.size() == baseOrderInfoVo.getSumTotal()) {
            //标记完成接收
            mSuccessUDPTask.setIsResponseFinish(true);
            ResponseListener responseListener = mSuccessUDPTask.getmResponseListener();
            dispatchResult(LoadResult.STATUS_SUCCESS.setDataVo(dataVo), responseListener);
            responseDataQueue.remove(mSelectOrderListVo.getResponseProtocolHead());
        } else {
            //跟新接收到最后一次数据包时间
            mSuccessUDPTask.setResponseTime(System.currentTimeMillis());
        }

    }

    /**
     * A.3.29　工单操作结果 (协议字:0x001C)
     * @param mResponseInfo
     */
    public void disposeOrderCtrlResult(ResponseInfo mResponseInfo){
        OrderCtrlResultVo mOrderCtrlResultVo = new OrderCtrlResultVo();
        mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, mOrderCtrlResultVo, false);

        Set<Map.Entry<Short, UDPTask>> entries = responseDataQueue.entrySet();
        Iterator<Map.Entry<Short, UDPTask>> iterator = entries.iterator();
        while(iterator.hasNext()){
            Map.Entry<Short, UDPTask> next = iterator.next();
            UDPTask mSuccessUDPTask = next.getValue();
            BaseVo dataVo = mSuccessUDPTask.getDataVo();
            if(dataVo.isHasOrderCtrlResult()){
                //处理工单操作结果
                boolean result = dataVo.disposeOrderCtrlResult(mOrderCtrlResultVo);

//                System.out.println(" disposeOrderCtrlResult result = "+result);
                if(result){


                    byte ctrlResult = mOrderCtrlResultVo.getResult();

                    boolean receiveOtherData = dataVo.isReceiveOtherData();

//                    System.out.println(" isReceiveOtherData receiveOtherData = "+receiveOtherData + "   ctrlResult = "+ctrlResult);

                    //判断是否需要接收其他协议包
                    if((!receiveOtherData) && (ctrlResult == 1)){

                        dataVo.setDisposeOrderCtrlResult(true);
                        //跟新下响应超时时间，防止超时了。
                        mSuccessUDPTask.setResponseTime(System.currentTimeMillis());
                        if(isDebug)System.out.println(" - - - - - - - 等待 OtherData - - - - - - -");
                        return;
                    }

                    //设置已完成接收
                    mSuccessUDPTask.setResponseTime(System.currentTimeMillis());
                    mSuccessUDPTask.setIsResponseFinish(true);

                    //设置（赋值）操作结果信息
                    dataVo.setOrderCtrlResultValue(mOrderCtrlResultVo);

                    //设置该任务的所有数据包发送成功
                    List<DataInfo> dataInfoList = mSuccessUDPTask.getDataInfo();
                    if(dataInfoList != null && dataInfoList.size()>0){
                        for(DataInfo dataInfo : dataInfoList){
                            if(dataInfo!=null){
                                dataInfo.setSendSuccess(true);
                            }
                        }
                    }

                    ResponseListener responseListener = mSuccessUDPTask.getmResponseListener();

                    if (dataVo != null && responseListener != null ) {
//                        System.out.println(" - - - - - - - 回调 STATUS_SUCCESS - - - - - - -");
                        dispatchResult(LoadResult.STATUS_SUCCESS.setDataVo(dataVo), responseListener);
                    }

                    iterator.remove();
                    return;
                }
            }
        }

    }

    /**
     * A.3.9　列表数量 （协议字：0x0008)  处理
     * @param mResponseInfo
     */
    public void disposeCountResponse(ResponseInfo mResponseInfo){
        //创建列表数量类，并解析数据到此类中
        ListCountBean mListCountBean = new ListCountBean();
        mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, mListCountBean, false);

        //获取列表数量类的结果协议字
        short resultProtocol = mListCountBean.getResultProtocol();

//        System.out.println("OperationCode = " + mListCountBean.getOperationCode() + "  OperationInfo = " + mListCountBean.getOperationInfo() + "  Count = " + mListCountBean.getCount() + "   resultProtocol = " + resultProtocol);

        //根据结果协议字获取发送任务类
        UDPTask mSuccessUDPTask;
        mSuccessUDPTask = responseDataQueue.get(resultProtocol);
        if(mSuccessUDPTask == null){
            if(isDebug)LogUtils.error("disposeCountResponse mSuccessUDPTask is null  protocol ： "+resultProtocol);
            return;
        }

        //跟新接收到最后一次数据包时间
        mSuccessUDPTask.setResponseTime(System.currentTimeMillis());

        BaseVo dataVo = mSuccessUDPTask.getDataVo();

        if(dataVo == null){
            if(isDebug)LogUtils.error("disposeCountResponse dataVo is null  protocol ： "+resultProtocol);
            return ;
        }

        //车辆列表的 列表数量 数据
        if(dataVo instanceof ResponseCarListBean){

            //转车辆列表查询类
            ResponseCarListBean mResponseCarListBean = (ResponseCarListBean) dataVo;

            //匹配操作信息
            String operationInfo = mResponseCarListBean.getOperationInfo();
            if(!TextUtils.isEmpty(operationInfo) && operationInfo.equals(mListCountBean.getOperationInfo())){
                //更改数量
                mResponseCarListBean.setCarlistSize(mListCountBean.getCount());
                if(mListCountBean.getCount() == 0){
                    ResponseListener responseListener = mSuccessUDPTask.getmResponseListener();
                    dispatchResult(LoadResult.STATUS_SUCCESS.setDataVo(dataVo), responseListener);
                }
            }


        }else if(dataVo instanceof ResponseCarHistoricalRouteBean){
            //车辆轨迹信息 列表数量 数据

            //转轨迹查询类
            ResponseCarHistoricalRouteBean mResponseCarHistoricalRouteBean = (ResponseCarHistoricalRouteBean) dataVo;
            //匹配操作信息
            String operationInfo = mResponseCarHistoricalRouteBean.getOperationInfo();
            if(!TextUtils.isEmpty(operationInfo) && operationInfo.equals(mListCountBean.getOperationInfo())){
                //更改数量
                mResponseCarHistoricalRouteBean.setCarlistSize(mListCountBean.getCount());

                if(mListCountBean.getCount() == 0){
                    ResponseListener responseListener = mSuccessUDPTask.getmResponseListener();
                    dispatchResult(LoadResult.NO_DATA.setDataVo(dataVo), responseListener);
                }

            }

        }else if(dataVo instanceof ResponseViolationLogBean){
            //转违规查询类
            ResponseViolationLogBean mResponseViolationLogBean = (ResponseViolationLogBean) dataVo;
            //匹配操作信息
            String operationInfo = mResponseViolationLogBean.getOperationInfo();
            if(!TextUtils.isEmpty(operationInfo) && operationInfo.equals(mListCountBean.getOperationInfo())){
                //更改数量
                mResponseViolationLogBean.setCarlistSize(mListCountBean.getCount());

                if(mListCountBean.getCount() == 0){
                    ResponseListener responseListener = mSuccessUDPTask.getmResponseListener();
                    dispatchResult(LoadResult.STATUS_SUCCESS.setDataVo(dataVo), responseListener);
                }

            }
        }


    }

    /**
     * A.3.36车辆费用应答-总费（协议字：0x0017）  处理
     * @param mResponseInfo
     */
    public void disposeCarAllCostResponse(ResponseInfo mResponseInfo) {
        //创建列表数量类，并解析数据到此类中
        CarAllCostVo carAllCostVo = new CarAllCostVo();
        mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, carAllCostVo, false);

        //获取列表数量类的结果协议字
        short resultProtocol = carAllCostVo.getResultProtocol();

//        System.out.println("OperationCode = " + mListCountBean.getOperationCode() + "  OperationInfo = " + mListCountBean.getOperationInfo() + "  Count = " + mListCountBean.getCount() + "   resultProtocol = " + resultProtocol);

        //根据结果协议字获取发送任务类
        UDPTask mSuccessUDPTask;
        mSuccessUDPTask = responseDataQueue.get(resultProtocol);
        if (mSuccessUDPTask == null) {
            if (isDebug)
                LogUtils.error("disposeCarAllCostResponse mSuccessUDPTask is null  protocol ： " + resultProtocol);
            return;
        }

        //跟新接收到最后一次数据包时间
        mSuccessUDPTask.setResponseTime(System.currentTimeMillis());

        BaseVo dataVo = mSuccessUDPTask.getDataVo();

        if (dataVo == null) {
            if (isDebug)
                LogUtils.error("disposeCarAllCostResponse dataVo is null  protocol ： " + resultProtocol);
            return;
        }

        //车辆费用 数据
        if(dataVo instanceof CarCostVo){

            //转车辆列表查询类
            CarCostVo mCarCostVo = (CarCostVo) dataVo;

            //匹配操作信息
            String plateNumber = mCarCostVo.getPlateNumber();
            if(!TextUtils.isEmpty(plateNumber) && plateNumber.equals(carAllCostVo.getResultPlateNumber())){
                //更改数量
                mCarCostVo.setAllArrearage(carAllCostVo.getAllArrearage());
                mCarCostVo.setArrearageMonth(carAllCostVo.getArrearageMonth());
                mCarCostVo.setCarCostCount(carAllCostVo.getCarCostVoCount());

                if(carAllCostVo.getCarCostVoCount() == 0){
                    //标记完成接收
                    mSuccessUDPTask.setIsResponseFinish(true);
                    ResponseListener responseListener = mSuccessUDPTask.getmResponseListener();
                    dispatchResult(LoadResult.STATUS_SUCCESS.setDataVo(dataVo), responseListener);
                    responseDataQueue.remove(resultProtocol);
                }

            }
        }

    }

        /**
         * 后台推送过来的消息
         * @param mResponseInfo 接收的数据
         * @return 是否处理推送消息
         */
    private boolean isMessagePush(ResponseInfo mResponseInfo){

        if(isDebug)LogUtils.error("isMessagePush protocol ： "+mResponseInfo.protocol);
        //有厂商消息推送
        if(mResponseInfo.protocol == Protocol.RESPONSE_PROTOCOL_MESSAGEPUSH_BEAT_RESULT){
            ResponseMessagePushBean mResponseMessagePushBean = new ResponseMessagePushBean();
            //将响应数据转换为dataVo中对应的属性
            mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, mResponseMessagePushBean, false);

//            TODO 通知UI有厂商消息推送
            PushMessage pushMessage = mResponseMessagePushBean.toPushMessage();
            UserInfo.getInstance().receivePushMessage(pushMessage);

            return true;
       /* }else if(mResponseInfo.protocol == Protocol.PROTOCOL_COMPANYALLCARINFO_BEAT_RESULT){
            //            后台推送过来的车辆信息
            CompanyAllCarBaseInfo mBaseCarInfoResult = new CompanyAllCarBaseInfo(null);
            //将响应数据转换为dataVo中对应的属性
            mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, mBaseCarInfoResult, true);

            UserInfo.getInstance().updateCarInfoList(mBaseCarInfoResult.getCarInfoList(),true, mBaseCarInfoResult.isEnd());
            return true;*/

        }else if (mResponseInfo.protocol == Protocol.RESPONSE_PROTOCOL_CARSTATUS_BEAT_RESULT){

            ResponseCarStatusInfoBean mResponseCarStatusInfoBean = new ResponseCarStatusInfoBean(null);

            mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, mResponseCarStatusInfoBean, false);

            saveCarStatusInfoToUser(mResponseCarStatusInfoBean);

//            TODO save to DB
            if(MainApplication.isUseCarInfoDB) {
                CarInfoDB carInfoDB = mResponseCarStatusInfoBean.toCarInfoDB(null);
                DBManager.insertCarInfo(carInfoDB);
            }


            return true;
        }else if (mResponseInfo.protocol == Protocol.RESPONSE_PROTOCOL_SELECT_ORDER_INFO_BEAT_RESULT){
            SelectOrderByNumberVo mSelectOrderByNumberVo = new SelectOrderByNumberVo(null,null);
            mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, mSelectOrderByNumberVo, false);
            saveOrderInfo(mSelectOrderByNumberVo);

            return true;
        }else{
            //LogUtils.error("Could not find the corresponding task , protocol = " + mResponseInfo.protocol);
            if(isDebug)System.out.println("isMessagePush bytes2HexString : " + BaseParsedUDPDataTool.bytes2HexString(mResponseInfo.dataBytes));
        }
        return false;
    }

    private void saveOrderInfo(SelectOrderByNumberVo mSelectOrderByNumberVo){

        String orderNumber = mSelectOrderByNumberVo.getOrderNumber();

        if(TextUtils.isEmpty(orderNumber)){
            return;
        }

        BaseOrderInfoDB baseOrderInfoDB = DBManager.querySelectBaseOrderInfo(UserInfo.getInstance().getUserName(), orderNumber);

        boolean isCreateMaintenanceInfoDB = false;

        boolean isMaintenanceOrder = mSelectOrderByNumberVo.getResultOrderName().equals(MainApplication.getInstance().getString(R.string.order_name_maintenance));

        boolean isCreatBaseOrderInfo = false;

        if(baseOrderInfoDB == null){

            //保存工单
            baseOrderInfoDB = new BaseOrderInfoDB(UserInfo.getInstance().getUserName(), mSelectOrderByNumberVo.getResultOrderName(), mSelectOrderByNumberVo.getOrderNumber(),
                    mSelectOrderByNumberVo.getOrderStatus(), mSelectOrderByNumberVo.getClienteleName(), mSelectOrderByNumberVo.getClientelePhone(), mSelectOrderByNumberVo.getFounder());
            isCreatBaseOrderInfo = true;
        }else{

            baseOrderInfoDB.setOrderStatus(mSelectOrderByNumberVo.getOrderStatus());

            String orderStatusStr = DecodeUDPDataTool.getOrderStatusStr(mSelectOrderByNumberVo);

            if(orderStatusStr.equals(MainApplication.getInstance().getString(R.string.order_maintenance_progress_creat))){

                isCreateMaintenanceInfoDB = true;

            }else{
                baseOrderInfoDB.setDataInfoId(-1);
            }

        }

        if(isCreateMaintenanceInfoDB && isMaintenanceOrder){

            CreateMaintenanceInfoDB createMaintenanceInfoDB = new CreateMaintenanceInfoDB();

            createMaintenanceInfoDB.setOrderNumber(mSelectOrderByNumberVo.getOrderNumber());
            createMaintenanceInfoDB.setProposer(mSelectOrderByNumberVo.getClienteleName());
            createMaintenanceInfoDB.setContactNumber(mSelectOrderByNumberVo.getClientelePhone());

            String maintenance_apply = MainApplication.getInstance().getString(R.string.order_content_maintenance_apply);
            DecodeUDPDataTool.OrderContentListItemData orderContentListItemData = DecodeUDPDataTool.decodeItemByDataName(mSelectOrderByNumberVo.getContentList(), maintenance_apply);

            String repairtime_str = MainApplication.getInstance().getString(R.string.create_maintenanceorder_repairtime);
            String repairtime = orderContentListItemData.getValue(repairtime_str, 0);
            createMaintenanceInfoDB.setRepairTime(repairtime);

            String plate_number_str = MainApplication.getInstance().getString(R.string.plate_number);
            String platenumber = orderContentListItemData.getValue(plate_number_str, 0);
            createMaintenanceInfoDB.setPlateNumber(platenumber);

            String appointmentTime_str = MainApplication.getInstance().getString(R.string.create_maintenanceorder_appointment_time);
            String appointmentTime = orderContentListItemData.getValue(appointmentTime_str, 0);
            createMaintenanceInfoDB.setAppointmentTime(appointmentTime);

            String appointmentLocation_str = MainApplication.getInstance().getString(R.string.create_maintenanceorder_appointment_location);
            String appointmentLocation = orderContentListItemData.getValue(appointmentLocation_str, 0);
            createMaintenanceInfoDB.setAppointmentLocation(appointmentLocation);

            String errordescribe_str = MainApplication.getInstance().getString(R.string.create_maintenanceorder_errordescribe);
            String errordescribe = orderContentListItemData.getValue(errordescribe_str, 0);
            createMaintenanceInfoDB.setProblemDescription(errordescribe);

            DBManager.insertCreateMaintenanceInfo(createMaintenanceInfoDB);

            List<CreateMaintenanceInfoDB> createMaintenanceInfoDBs = DBManager.querySelectCreateMaintenanceInfo(mSelectOrderByNumberVo.getOrderNumber());

            if(createMaintenanceInfoDBs != null && createMaintenanceInfoDBs.size()>0) {
                baseOrderInfoDB.setData(createMaintenanceInfoDBs.get(0));
            }
        }

        if(isCreatBaseOrderInfo) {
            DBManager.insertBaseOrderInfo(baseOrderInfoDB);
        }else{
            DBManager.updataBaseOrderInfo(baseOrderInfoDB);
        }
    }

    /**
     * 保持车辆状态信息到用户中
     * @param mResponseCarStatusInfoBean
     */
    private void saveCarStatusInfoToUser(ResponseCarStatusInfoBean mResponseCarStatusInfoBean){

        String plateNumber = mResponseCarStatusInfoBean.getResultPlateNumber();

        //获取用户信息中的车辆信息列表，判断是否已经存储了该车辆信息
        CarInfoBean mCarInfoBean = UserInfo.getInstance().getNewestCarInfo(plateNumber);

        mCarInfoBean = mResponseCarStatusInfoBean.toCarInfoBean(mCarInfoBean);

        //更新车辆信息，并通知界面数据发生变化
        UserInfo.getInstance().updateCarInfo(mCarInfoBean,true);

        // 如果用户名等于车牌，就是车牌登录，则更新车辆最新信息
        if(UserInfo.getInstance().getUserName().equals(plateNumber)) {
            UserInfo.getInstance().setPersionCarInfo(mCarInfoBean);
        }
    }

    /**
     * 应答数据包创建
     * @param frameNo
     * @return
     */
    private DatagramPacket createAckPacket(short frameNo){

//        System.out.println("createAckPacket ------------------------------------------------------------ frameNo = "+frameNo);
        AckPacket mAckPacket = new AckPacket();
        DataInfo dataInfo  = mBaseParsedUDPDataTool.sendDataParsed(mAckPacket,frameNo);
        DatagramPacket datagramPacket = mBaseParsedUDPDataTool.createDatagramPacket(dataInfo,mAckPacket.requestProtocolHead, null);
        byte[] data = datagramPacket.getData();
        //System.out.println("bytes2HexString --> createAckPacket dataBytes = " + BaseParsedUDPDataTool.bytes2HexString(data));
        //System.out.println("createAckPacket ------------------------------------------------------------");
        return datagramPacket;


    }


}
