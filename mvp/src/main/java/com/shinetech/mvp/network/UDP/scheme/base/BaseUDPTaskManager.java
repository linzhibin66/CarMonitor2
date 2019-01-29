package com.shinetech.mvp.network.UDP.scheme.base;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.SocketUtils;
import com.shinetech.mvp.network.UDP.UDPHeartBeatConnect;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.DataInfo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.item.ResponseInfo;
import com.shinetech.mvp.network.UDP.bean.item.UDPTask;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.listener.ThreadBlockListener;
import com.shinetech.mvp.network.UDP.scheme.UDPTaskManager;
import com.shinetech.mvp.utils.HttpUtils;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.UIUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by ljn on 2017-06-05.
 */
public abstract class BaseUDPTaskManager {

    /**
     * 需处理的数据包协议字列表（阻塞型，当无任务时为了阻塞线程，提高流畅度减少GC）
     */
    protected final BlockingDeque<Short> unFinishProtocolQueue = new LinkedBlockingDeque<>();

    /**
     * 需处理的数据包
     */
    protected final LinkedHashMap<Short, UDPTask> unFinishDataQueue = new LinkedHashMap<>();

    /**
     * 监测请求超时的任务(request timeout task)协议字列表（阻塞型，当无任务时为了阻塞线程，提高流畅度减少GC）
     */
    protected final BlockingDeque<Short> requestedProtocolQueue = new LinkedBlockingDeque<>();

    /**
     * 分包数据
     * key is responseProtocol
     */
    protected final LinkedHashMap<Short, List<ResponseInfo>> responseSubpackageDataQueue = new LinkedHashMap<>();

    /**
     * 监测请求超时的任务(request timeout task)
     */
    protected final LinkedHashMap<Short, UDPTask> requestedDataQueue = new LinkedHashMap<>();

    private static UDPTaskManager instance;

    /**
     * 限制重新请求次数
     */
    private final int requestAgainCount = 3;

    private long sendPacketTime = 0;


    /**
     * 是否已经退出
     */
    protected boolean isExit;

    protected boolean hasStart;

    protected boolean isDebug = false && LogUtils.isDebug;

    protected BaseParsedUDPDataTool mBaseParsedUDPDataTool;

    public BaseUDPTaskManager(BaseParsedUDPDataTool mBaseParsedUDPDataTool) {
        this.mBaseParsedUDPDataTool = mBaseParsedUDPDataTool;
    }

    public void exit(){
        unFinishDataQueue.clear();
        unFinishProtocolQueue.clear();
        requestedDataQueue.clear();
        requestedProtocolQueue.clear();
    }

    /**
     * 编码并发送数据，处理丢包重发，接受数据验证解码
     *
     * @param dataVo 请求数据需要携带的数据属性
     * @param responseListener 请求数据监听
     */
    public void request( BaseVo dataVo,  ResponseListener responseListener) {

        if (!HttpUtils.isNetWorkConnected()) {
            if(responseListener!=null) {
                responseListener.onError(LoadResult.NO_INTERNET_CONNECT);
            }
            LogUtils.error(" no NetWork Connected ");
            return;
        }

      /*  if(unFinishProtocolQueue.size()>=15 && hasStart){

        }*/

        //如果该任务是可分包任务，清除该任务中对应的存储内容
        if(dataVo.isSubPackageReveive()){
            List<ResponseInfo> responseInfos = responseSubpackageDataQueue.get(dataVo.getResponseProtocolHead());
            if(responseInfos != null && responseInfos.size()>0){
                responseInfos.clear();
                responseSubpackageDataQueue.put(dataVo.getResponseProtocolHead(),responseInfos);
            }
        }

//        根据dataVo中的参数构建发送内容数据
        List<DataInfo> dataInfos = mBaseParsedUDPDataTool.sendDataParsed(dataVo);


//      存储到任务队列中(-1 是将所有dataInfo 添加到Task中)
        saveUnFinishTask(-1, new UDPTask(dataVo, dataInfos, responseListener));

        isExit = false;

        if (!hasStart) {
            hasStart = true;
            startSendData();

            startReceiveData();

//          TODO start Check Timeout
            startCheckTimeout();
        }
    }

    /**
     * 开始发送数据线程
     */
    private void startSendData() {

        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if(isDebug)LogUtils.error("发送线程开始");
                while (!isExit) {
                    sendData();
                }
            }
        });
    }

    /**
     * 开始数据接受线程
     */
    private void startReceiveData() {

       AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if(isDebug)LogUtils.error("接受线程开始");
                ResponseInfo mResponseInfo = new ResponseInfo();
                while (!isExit) {
                    Arrays.fill(mResponseInfo.dataBytes, (byte) 0);
                    UDPHeartBeatConnect.lastReceivePackageTime = System.currentTimeMillis();
                    receiveData(mResponseInfo);
                    //System.out.println("UDPHeartBeatConnect.lastReceivePackageTime  = "+UDPHeartBeatConnect.lastReceivePackageTime );
                }
            }
        });
    }

    /**
     * 监测任务是否超时
     */
    private void startCheckTimeout(){

        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (isDebug) LogUtils.error("检查超时线程开始");
                while (!isExit) {
                    checkTaskTimeOut(null);
                }

                if (isDebug) LogUtils.i("数据包超时处理线程退出");
            }
        });

    }

    private void sendData(){

        //获取任务队列中的第一个数据包

        Short aShort = null;
        try {
            aShort = unFinishProtocolQueue.takeFirst();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final UDPTask mUDPTask;

        synchronized (unFinishDataQueue) {
            mUDPTask = unFinishDataQueue.remove(aShort);
        }

        if(mUDPTask == null){
            if(isDebug)System.out.println("mUDPTask is null  aShort : "+aShort);
            return;
        }


        List<DataInfo> dataInfoList = mUDPTask.getDataInfo();

//        LogUtils.error("aShort = "+aShort + "  protocol = "+mUDPTask.getDataVo().requestProtocolHead);


        for(DataInfo dataInfo : dataInfoList) {

//            LogUtils.error("dataInfoList size  = "+dataInfoList.size());

            boolean iscurrentDataInfo = true;

            //匹配对应的 DataInfo 并进行发送
            if(mUDPTask.isUseFrameNoKey){
                iscurrentDataInfo = (dataInfo.frameNo == aShort);
            }

//            LogUtils.error("iscurrentDataInfo  = "+iscurrentDataInfo +"   dataInfo.isSendSuccess() = "+dataInfo.isSendSuccess() + "   dataInfo.isSend = "+dataInfo.isSend );

            if ((dataInfo != null) && (!dataInfo.isSendSuccess()) && (!dataInfo.isSend) && iscurrentDataInfo) {
//          TODO 创建数据包
                DatagramPacket datagramPacket = mBaseParsedUDPDataTool.createDatagramPacket(dataInfo, mUDPTask.getDataVo().requestProtocolHead, null);

                if(isDebug)System.out.println("send : " + mBaseParsedUDPDataTool.bytes2HexString(datagramPacket.getData()));

                try {
                    synchronized (MainApplication.getInstance()) {
                        SocketUtils.getInstance().sendPacket(datagramPacket);
                        try {
                            Thread.currentThread().sleep(5);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    try {
                        Thread.currentThread().sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    LogUtils.error("sendData requestAgain");
//              重新放到任务队列中请求
                    requestAgain(mUDPTask, dataInfoList.indexOf(dataInfo));
                    return;

                }

//          TODO save sendData List order to Check timeOut
                dataInfo.lastSendTimeMillis = SystemClock.elapsedRealtime();
                dataInfo.isSend = true;
                saveRequestTask(dataInfoList.indexOf(dataInfo), mUDPTask);

            }
        }
    }

    /**
     * 重新放到任务队列中请求。（可重新请求3次）
     * @param mUDPTask 数据
     */
    private void requestAgain(UDPTask mUDPTask, int index){

        List<DataInfo> dataInfoLists = mUDPTask.getDataInfo();
        DataInfo mdataInfo = null;

        if(dataInfoLists.size()>index){
            mdataInfo = dataInfoLists.get(index);
        }

        if(mdataInfo==null){
            return;
        }

        mdataInfo.retryTimes++;
//        判断重新请求是否超过3次
        if (mdataInfo.retryTimes < 3) {

            saveUnFinishTask(index, mUDPTask);

            //可能存在断网情况,断开socket连接,重新进行连接.(断网后，网络再次连接上是,通信异常)
           /* try {
                SocketUtils.getInstance().closeSocket();
            }catch(Exception e){
                e.printStackTrace();
            }*/
        }
    }

    /**
     * 检测任务是否超时
     * @param protocolOrframeNo 任务协议字或者是帧号（根据saveRequestTask中操作）
     */
    private void checkTaskTimeOut(Short protocolOrframeNo){
        if(protocolOrframeNo==null){
            try {
                protocolOrframeNo = requestedProtocolQueue.takeFirst();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        UDPTask mUDPTask = requestedDataQueue.get(protocolOrframeNo);

        if(mUDPTask == null){
            if(isDebug)System.out.println("requestedDataQueue  mUDPTask is null protocolOrframeNo = "+protocolOrframeNo);
            return;
        }

        if(isDebug)System.out.println("timeout requestedDataQueue .size "+ requestedDataQueue.size() + "   check protocolOrframeNo ： "+protocolOrframeNo);

        List<DataInfo> dataInfoList = mUDPTask.getDataInfo();

        for(DataInfo dataInfo : dataInfoList){

            boolean iscurrentDataInfo = true;

            //匹配对应的 DataInfo 并进行发送
            if(mUDPTask.isUseFrameNoKey){
                iscurrentDataInfo = (dataInfo.frameNo == protocolOrframeNo);
            }


//        if(debug)System.out.println("dataInfo "+ dataInfo!=null + "   isSendSuccess ： "+!dataInfo.isSendSuccess() + "  iscurrentDataInfo = "+iscurrentDataInfo);

        if (dataInfo != null && !dataInfo.isSendSuccess() && iscurrentDataInfo) {
            long elapsedTime = SystemClock.elapsedRealtime() - dataInfo.lastSendTimeMillis;

            //无响应超过3秒（超时）
            if (elapsedTime / 1000 >= 3) {

                //访问3次都超时
                if (dataInfo.retryTimes >= 3) {
                   /* if(debug)*/
//                    LogUtils.error("请求超时");
                    sendTimeOutListener(protocolOrframeNo);
                    return ;

                } else {
                    // 没有接受到确认数据包，从已发送队列移除，并放入未发送队列末尾重发数据包
                    /*if(debug)*/
//                    LogUtils.error("请求超时 dataInfo.retryTimes = " + dataInfo.retryTimes + "    重新发送");
                    dataInfo.retryTimes++;

                    try{
                        requestedProtocolQueue.remove(protocolOrframeNo);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    requestedDataQueue.remove(protocolOrframeNo);

                    dataInfo.isSend = false;
//                    LogUtils.error("checkTaskTimeOut aShort = " + mUDPTask.getDataInfo().frameNo + "  protocol = " + mUDPTask.getDataVo().requestProtocolHead);
                    saveUnFinishTask(dataInfoList.indexOf(dataInfo), mUDPTask);

                    return;

                }

            } else {
                long sleepTime = 3 * 1000 - elapsedTime;

                try {
                    if (isDebug) System.out.println("sleep " + sleepTime);
                    Thread.currentThread().sleep(sleepTime);
                } catch (InterruptedException e) {
                    if (isDebug)
                        LogUtils.e("数据超时处理线程尝试睡眠时异常终止! 尝试睡眠时间为：" + sleepTime);
                }

                //重新检测该任务超时状态
                checkTaskTimeOut(protocolOrframeNo);
                return;

            }

        }

        }
    }

    /**
     * 回掉超时监听
     * @param protocolOrframeNo 任务协议字或者是帧号（根据saveRequestTask中操作）
     */
    private void sendTimeOutListener(short protocolOrframeNo){

        //已超时，丢弃任务

        UDPTask udpTask = requestedDataQueue.get(protocolOrframeNo);
        if(udpTask.getDataInfo().size() == 1) {
            try {
                requestedProtocolQueue.remove(protocolOrframeNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            requestedDataQueue.remove(protocolOrframeNo);
        }else{
            //是否使用FrameNo作为Key
            if(!udpTask.isUseFrameNoKey){
                try {
                    requestedProtocolQueue.remove(protocolOrframeNo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                requestedDataQueue.remove(protocolOrframeNo);
            }else{
                //将该任务所有未完成的数据包移除
                List<DataInfo> dataInfoList = udpTask.getDataInfo();
                if(dataInfoList != null && dataInfoList.size()>0) {
                    for (DataInfo mDataInfo : dataInfoList) {
                        //移除以发送未响应的任务
                        try {
                            requestedProtocolQueue.remove(mDataInfo.frameNo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        requestedDataQueue.remove(mDataInfo.frameNo);

                        //移除未发送的任务
                        if(unFinishProtocolQueue.contains(mDataInfo.frameNo)) {
                            unFinishProtocolQueue.remove(mDataInfo.frameNo);
                            unFinishDataQueue.remove(mDataInfo.frameNo);
                        }
                    }
                }
            }
        }

        BaseVo dataVo = udpTask.getDataVo();

        ResponseListener responseListener = udpTask.getmResponseListener();

        if(dataVo!=null && responseListener!=null){
            dispatchResult(LoadResult.TIME_OUT.setDataVo(dataVo), responseListener);
        }
    }

    /**
     * 分发请求结果
     */
    protected void dispatchResult(final LoadResult result, final ResponseListener responseListener) {

        if(responseListener==null){
            return;
        }

        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                switch (result) {
                    case STATUS_SUCCESS:
                        responseListener.onSuccess(result);
                        break;
                    default:
                        responseListener.onError(result);
                }
            }
        });
    }

    /**
     * 根据ResponseProtocol或者frameNo记录发送的任务（超时和响应后，可通过对于的方式重新获取回发送的任务，并赋值后台返回的数据）
     * @param mUDPTask
     */
    protected abstract void saveRequestTask(int index, UDPTask mUDPTask);

    /**
     * 接受数据并将数据的结果分发到presenter层
     */
    protected abstract void receiveData(ResponseInfo mResponseInfo);

    protected abstract void saveUnFinishTask(int index , UDPTask mUDPTask);
}
