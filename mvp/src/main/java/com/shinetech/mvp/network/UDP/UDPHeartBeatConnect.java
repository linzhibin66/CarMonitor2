package com.shinetech.mvp.network.UDP;

import android.os.Handler;
import android.os.Message;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.network.UDP.bean.LoginOutBean;
import com.shinetech.mvp.network.UDP.bean.item.HeartBeatBean;
import com.shinetech.mvp.network.UDP.listener.HeartBeatListener;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.utils.LogUtils;

/**
 * Created by ljn on 2017/1/25.
 */
public class UDPHeartBeatConnect {

    public final Handler heartBeatHandler = new HeartBeatHandler();

    /**
     * 心跳包发送消息代码
     */
    private final int HEART_BEAT_PACKET_SEND = 0XFF;

    /**
     * 心跳包发送间隔时间30秒
     */
    private final int HEART_BEAT_SEND_PERIOD = 30000;

    public static long lastReceivePackageTime;

    /**
     * 消息发送开关
     */
    private boolean isEnableSendMassage = false;

    /**
     * 心跳包发送失败次数
     */
    private int heartBeatSendFailCount = 0;

    private static UDPHeartBeatConnect mUDPHeartBeatConnect;

    private final boolean isDebug = false && LogUtils.isDebug;


    private UDPHeartBeatConnect() {
    }

    public static UDPHeartBeatConnect getInstance(){

        if(mUDPHeartBeatConnect==null){
            synchronized (MainApplication.getInstance()){
                if(mUDPHeartBeatConnect==null){
                    mUDPHeartBeatConnect = new UDPHeartBeatConnect();
                }
            }
        }

        return mUDPHeartBeatConnect;
    }

    /**
     * 登录成功后开始保持心跳连接
     */
    public void startHeartBeatAck(HeartBeatListener mHeartBeatListener) {

        heartBeatHandler.removeMessages(HEART_BEAT_PACKET_SEND);

        isEnableSendMassage = true;
        Message mMassage = Message.obtain();
        mMassage.obj = mHeartBeatListener;
        mMassage.what = HEART_BEAT_PACKET_SEND;
        heartBeatHandler.sendMessage(mMassage);
        lastReceivePackageTime = System.currentTimeMillis();
    }

    public void removeHeartBeat(){
        heartBeatHandler.removeMessages(HEART_BEAT_PACKET_SEND);
        isEnableSendMassage = false;
    }

    public void onExit(){
        exit(null);
    }

    public void exit(final HeartBeatListener mListener){

        removeHeartBeat();

        //退出用户登录
        UDPRequestCtrl.getInstance().request(new LoginOutBean(), new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {
                disConnectServer(mListener);
            }

            @Override
            public void onError(LoadResult errorResult) {
                disConnectServer(mListener);
            }
        });

    }

    /**
     * 断开服务器连接
     * @param mListener
     */
    private void disConnectServer(HeartBeatListener mListener){

        // 停掉socket接收、发送、检测超时线程、清除缓存等。
        UDPRequestCtrl.getInstance().exit();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 断开socket连接
                SocketUtils.getInstance().closeSocket();
            }
        }).start();

        //回到登录界面
        if(mListener!=null){
            mListener.sendToError();
        }
    }

    /**
     * 发送心跳连接包
     * 调用后send_frame_no需要自加1
     */
    private void sendHeartBeatPacket(final HeartBeatListener mHeartBeatListener) {

        UDPRequestCtrl.getInstance().request(new HeartBeatBean(), new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {

                if(heartBeatSendFailCount != 0){
                    heartBeatSendFailCount = 0;
                }

            }

            @Override
            public void onError(LoadResult errorResult) {

                if(heartBeatSendFailCount >= 2 && mHeartBeatListener != null){
                    exit(mHeartBeatListener);
                    return;
                }

                if(mHeartBeatListener!=null){
                    heartBeatSendFailCount++;
                    mHeartBeatListener.sendToFail();
                }
            }
        });

      /*  DatagramPacket heartBeatPacket = ParsedUDPDataTool.createHeartBeatPacket();

        try {

            if(debug)LogUtils.d("心跳连接数据包,发送....");
            if(debug)LogUtils.d("发送包数据:." + ByteUtil.bufferToString(heartBeatPacket.getData()));

            SocketUtils.getInstance().sendPacket(heartBeatPacket);

            if(heartBeatSendFailCount != 0){
                heartBeatSendFailCount = 0;
            }

        } catch (IOException e) {
            if(debug)LogUtils.e("心跳连接数据包发送失败！");

            if(heartBeatSendFailCount >= 5 && mHeartBeatListener != null){
                mHeartBeatListener.sendToError();
                heartBeatHandler.removeMessages(HEART_BEAT_PACKET_SEND);
                isEnableSendMassage = false;
                return;
            }

            if(mHeartBeatListener!=null){
                heartBeatSendFailCount++;
                mHeartBeatListener.sendToFail();
            }
            e.printStackTrace();
        }*/
    }

    class HeartBeatHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HEART_BEAT_PACKET_SEND) {
//              lastReceivePackageTime;
                long currentTimeMillis = System.currentTimeMillis();
                long distanceTime = currentTimeMillis - lastReceivePackageTime;
                Object mHeartBeatListener = msg.obj;


//                LogUtils.e("time : "+distanceTime);
                if(distanceTime >= HEART_BEAT_SEND_PERIOD) {
//                    LogUtils.e("心跳连接发送消息");
                    sendHeartBeatPacket(mHeartBeatListener != null ? (HeartBeatListener) mHeartBeatListener : null);
                }
                heartBeatHandler.removeMessages(HEART_BEAT_PACKET_SEND);

                if(isEnableSendMassage){
                    Message obtain = Message.obtain();
                    obtain.obj = (mHeartBeatListener != null ?  mHeartBeatListener : null);
                    obtain.what = HEART_BEAT_PACKET_SEND;
                    heartBeatHandler.sendMessageDelayed(obtain, (distanceTime >= HEART_BEAT_SEND_PERIOD)?HEART_BEAT_SEND_PERIOD : (HEART_BEAT_SEND_PERIOD - distanceTime));
                    //              heartBeatHandler.sendEmptyMessageDelayed(HEART_BEAT_PACKET_SEND, HEART_BEAT_SEND_PERIOD);
                    if(isDebug) LogUtils.d("心跳连接延时消息已发送");

                }
            }
        }
    }


}
