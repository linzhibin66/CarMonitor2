package com.shinetech.mvp.network.UDP.scheme;

import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.SocketUtils;
import com.shinetech.mvp.network.UDP.bean.CompanyAllCarBaseInfo;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.scheme.base.BaseUDPTaskManager;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.CarHistoricalRouteBean;
import com.shinetech.mvp.network.UDP.bean.MessagePushBean;
import com.shinetech.mvp.network.UDP.bean.item.BaseCarInfoResult;
import com.shinetech.mvp.network.UDP.bean.item.UDPTask;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.DataInfo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.item.ResponseInfo;
import com.shinetech.mvp.utils.LogUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.List;


/**
 * 无应答的 UDP 任务管理类
 * Created by ljn on 2017/1/25.
 */
public class UDPTaskManager extends BaseUDPTaskManager {

    public UDPTaskManager() {
        super(new ParsedUDPDataTool());
    }

    @Override
    protected void saveUnFinishTask(int index, UDPTask mUDPTask) {
        try {
            //必须在unFinishProtocolQueue之前添加，否则会出现找不到的bug
            unFinishDataQueue.put(mUDPTask.getDataVo().getResponseProtocolHead(), mUDPTask);
            unFinishProtocolQueue.putLast(mUDPTask.getDataVo().getResponseProtocolHead());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void saveRequestTask(int index, UDPTask mUDPTask) {
        try {
            requestedProtocolQueue.putLast(mUDPTask.getDataVo().getResponseProtocolHead());
            requestedDataQueue.put(mUDPTask.getDataVo().getResponseProtocolHead(), mUDPTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(isDebug)System.out.println("requestedDataQueue .size "+ requestedDataQueue.size() + "   responseProtocol ： "+mUDPTask.getDataVo().getResponseProtocolHead());
    }

    /**
     * 接受数据并将数据的结果分发到presenter层
     */
    @Override
    protected void receiveData(ResponseInfo mResponseInfo) {
        try {

            if(isDebug)LogUtils.error("receiveData  = =- - - - - - - - - - - - - - - - - - - - - - - - -= =  mResponseInfo.protocol = "+mResponseInfo.protocol);
//            获取接收服务器返回的数据包
            DatagramPacket datagramPacket = SocketUtils.getInstance().receiveData(mResponseInfo);

            if(datagramPacket == null){
                return;
            }
//            对返回的数据包数据的解析，存储到ResponseInfo中
            mBaseParsedUDPDataTool.decodeResponseData(mResponseInfo,datagramPacket.getData(),datagramPacket.getLength());

            switch(mResponseInfo.resultStatusCode){
                case ResponseInfo.RECEIVE_SUCCESS:
                    if(isDebug)LogUtils.error("接收数据成功");
//                    DatagramPacket successAckPacket = ParsedUDPDataTool.createAckPacket(mResponseInfo.protocol);
//                    SocketUtils.getInstance().sendPacket(successAckPacket);

                    //从响应任务缓存中获取对应的任务
                    callBackSuccess(mResponseInfo);

                    break;

                case ResponseInfo.HEART_BEAT_PACKET_RECEIVED:
                    if(isDebug && (mResponseInfo.resultStatusCode == ResponseInfo.HEART_BEAT_PACKET_RECEIVED))LogUtils.error("心跳链接");
                    //从响应任务缓存中获取对应的任务
                    callBackSuccess(mResponseInfo);
                   /* DatagramPacket ackPacket = ParsedUDPDataTool.createAckPacket(mResponseInfo.receiveFrameNo);
                    SocketUtils.getInstance().sendPacket(ackPacket);*/
//                    TODO timer 60s (倒计时60s，60s后还无下个心跳包接收视为断开连接)
                    return;
                case ResponseInfo.INVALID_DATA_LENGTH:
                    if(isDebug)LogUtils.error("数据包长度校验失败，数据包被丢弃");
                   /* DatagramPacket dataErrorPacket = ParsedUDPDataTool.createAckPacket(mResponseInfo.receiveFrameNo);
                    SocketUtils.getInstance().sendPacket(dataErrorPacket);*/
                    //从响应任务缓存中获取对应的任务
                    callBackDataError(mResponseInfo.protocol);

                    return;
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
     * @param protocol 响应协议字
     */
    private void callBackDataError(short protocol){
        if(isDebug)System.out.println("requestedDataQueue .size " + requestedDataQueue.size() + "   remove responseProtocol ： " + protocol);
        try {
            requestedProtocolQueue.remove(protocol);

        }catch (Exception e){
            e.printStackTrace();
        }
        UDPTask mDataErrorUDPTask = requestedDataQueue.remove(protocol);
        if(mDataErrorUDPTask==null){
            return;
        }

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

        short protocol = mResponseInfo.protocol;

        if(isDebug)System.out.println("requestedDataQueue .size " + requestedDataQueue.size() + "   responseProtocol ： " + protocol);


        UDPTask mSuccessUDPTask = requestedDataQueue.get(protocol);
        if(mSuccessUDPTask==null){

//            后台推送数据处理
           isMessagePush(mResponseInfo);

            return;
        }


        List<DataInfo> dataInfoList = mSuccessUDPTask.getDataInfo();

        if(dataInfoList != null && dataInfoList.size()>0) {

            for(DataInfo dataInfo : dataInfoList) {
                if (dataInfo != null) {
                    dataInfo.setSendSuccess(true);
                }
            }
        }

        BaseVo dataVo = mSuccessUDPTask.getDataVo();

        ResponseListener responseListener = mSuccessUDPTask.getmResponseListener();

        if (dataVo != null && responseListener != null) {
            if(isDebug)LogUtils.error("回掉监听 protocol ： "+protocol);
            //将响应数据转换为dataVo中对应的属性
            mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, dataVo, protocol == Protocol.PROTOCOL_COMPANYALLCARINFO_BEAT_RESULT);

            //对历史轨迹接收包进行处理，监测是否已经接收完毕（涉及分数据包发送）
            if(mResponseInfo.protocol == Protocol.PROTOCOL_CARHISTORICALROUTE_BEAT_RESULT){
                CarHistoricalRouteBean mCarHistoricalRouteBean = (CarHistoricalRouteBean) dataVo;
                boolean isend = mCarHistoricalRouteBean.isend();
                if(isend){
                    try{
                        requestedProtocolQueue.remove(protocol);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    requestedDataQueue.remove(protocol);
                }

            }else if(mResponseInfo.protocol == Protocol.PROTOCOL_COMPANYALLCARINFO_BEAT_RESULT) {
                BaseCarInfoResult mBaseCarInfoResult = (BaseCarInfoResult) dataVo;

                boolean end = mBaseCarInfoResult.isEnd();

                //更新车辆信息
                UserInfo.getInstance().updateCarInfoList(mBaseCarInfoResult.getCarInfoList(), false, end);

                if(end){
                    try{
                        requestedProtocolQueue.remove(protocol);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    requestedDataQueue.remove(protocol);
                }
            }else{
                try{
                    requestedProtocolQueue.remove(protocol);
                }catch (Exception e){
                    e.printStackTrace();
                }
                requestedDataQueue.remove(protocol);
            }


            dispatchResult(LoadResult.STATUS_SUCCESS.setDataVo(dataVo), responseListener);
        }
    }

    /**
     * 后台推送过来的消息
     * @param mResponseInfo 接收的数据
     * @return 是否处理推送消息
     */
    private boolean isMessagePush(ResponseInfo mResponseInfo){
        //有厂商消息推送
        if(mResponseInfo.protocol == Protocol.PROTOCOL_MESSAGEPUSH_BEAT_RESULT){
            MessagePushBean messagePushBean = new MessagePushBean();
            //将响应数据转换为dataVo中对应的属性
            mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, messagePushBean,false);
//            TODO 通知UI有厂商消息推送
            return true;
        }else if(mResponseInfo.protocol == Protocol.PROTOCOL_COMPANYALLCARINFO_BEAT_RESULT){
        //            后台推送过来的车辆信息
            CompanyAllCarBaseInfo mBaseCarInfoResult = new CompanyAllCarBaseInfo(null);
            //将响应数据转换为dataVo中对应的属性
            mBaseParsedUDPDataTool.parseDataToProperties(mResponseInfo.dataBytes, mBaseCarInfoResult, true);

            UserInfo.getInstance().updateCarInfoList(mBaseCarInfoResult.getCarInfoList(),true, mBaseCarInfoResult.isEnd());
            return true;

        }else if (mResponseInfo.protocol == Protocol.PROTOCOL_CARSTATUS_BEAT_RESULT){

            Object[] objects = mBaseParsedUDPDataTool.decodeProperties(mResponseInfo.dataBytes, CarInfoBean.getDataTypes(), false);

            String plateNumber = BaseVo.parseString((byte[]) objects[0]);

            //获取用户信息中的车辆信息列表，判断是否已经存储了该车辆信息
            CarInfoBean mCarInfoBean = UserInfo.getInstance().getNewestCarInfo(plateNumber);

            if(mCarInfoBean == null){
                mCarInfoBean = new CarInfoBean();
            }

            mCarInfoBean.setProperties(objects);

            //更新车辆信息，并通知界面数据发生变化
            UserInfo.getInstance().updateCarInfo(mCarInfoBean,true);

            // 如果用户名等于车牌，就是车牌登录，则更新车辆最新信息
            if(UserInfo.getInstance().getUserName().equals(plateNumber)) {
                UserInfo.getInstance().setPersionCarInfo(mCarInfoBean);
            }

            return true;
        }
        return false;
    }

    /**
     * 清楚缓存数据
     * @param protocol 数据帧号
     */
    /*private void cleanCache(short protocol){

        DataInfo removeDataInfo = responseProtocol.remove(protocol);
        if(removeDataInfo!=null){
            BaseVo removeBaseVo = requestParam.remove(removeDataInfo);
            if(removeBaseVo!=null){
                baseVoListeners.remove(removeBaseVo);
            }
        }
    }*/


}
