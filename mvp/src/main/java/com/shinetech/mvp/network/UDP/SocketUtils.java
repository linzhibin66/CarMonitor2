package com.shinetech.mvp.network.UDP;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.network.UDP.bean.item.ResponseInfo;
import com.shinetech.mvp.utils.LogUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by ljn on 2017/1/24.
 */
public class SocketUtils {

    private static final boolean isDebug = false && LogUtils.isDebug;

    private static SocketUtils instance;

    public static DatagramSocket datagramSocket;

    //正式
//    private static final int SERVER_PORT = 10034;//测试用11034，发布用10034;11035
//    private static final String SERVER_ADDRESS = "211.139.198.78";//"211.139.198.78/211.139.199.150";

    // 测试
//     private static final int SERVER_PORT = 10040;//测试用
//     private static final int SERVER_PORT = 10038;//分包测试用
     private static final int SERVER_PORT = 10043;//分包正式
//     private static final int SERVER_PORT = 10041;//正式
    private static final String SERVER_ADDRESS = "59.37.17.67";
//    private static final String SERVER_ADDRESS = "192.168.0.41";

    private SocketUtils() {
        if (datagramSocket == null) {
            try {
                datagramSocket = new DatagramSocket();
            } catch (SocketException e) {
                /*if(debug)*/LogUtils.e("初始化UDP Socket失败！");
                e.printStackTrace();
            }
        }

    }

    public static SocketUtils getInstance() {
        if(instance==null){
            synchronized (MainApplication.getInstance()){
                if(instance==null){
                    instance = new SocketUtils();
                }
            }
        }
        return instance;
    }

    public void closeSocket(){
        if(datagramSocket!=null){

            if(!datagramSocket.isClosed()){
                datagramSocket.close();
            }

            datagramSocket.disconnect();
        }
    }

    private void checkSocket() {
        if (datagramSocket == null) {
            throw new IllegalStateException("网络连接验证失败，UDP Socket为空，请检查网络配置！");
        }

        if(datagramSocket.isClosed()){
            try {
                datagramSocket = new DatagramSocket();

            } catch (SocketException e) {
                /*if(debug)*/LogUtils.e("初始化UDP Socket失败！");
            }
        }

        if (!datagramSocket.isConnected()) {
            synchronized (datagramSocket) {
//                 LogUtils.error("datagramSocket connect - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
                try {
                        datagramSocket.connect(Inet4Address.getByName(SERVER_ADDRESS), SERVER_PORT);
                        datagramSocket.setReceiveBufferSize(Integer.MAX_VALUE);
                        datagramSocket.setSendBufferSize(Integer.MAX_VALUE);
                        if(isDebug)LogUtils.error("datagramSocket connect - - - - -   ReceiveBufferSize "+ datagramSocket.getReceiveBufferSize() +"   sendBufferSize = "+ datagramSocket.getSendBufferSize());
                } catch (Exception e) {
                    e.printStackTrace();

                    if(isDebug)LogUtils.error("datagramSocket connect - - - - - catch ");
                    throw new IllegalStateException("服务器连接失败，无法识别的服务器地址");
//                throw new IllegalStateException("服务器连接失败，无法识别的服务器地址");
                }
            }
        }
    }

    public void connect(){
        checkSocket();
    }

    /**
     * 发送接收确认包
     */
    public void sendPacket(DatagramPacket datagramPacket) throws IOException {
//        LogUtils.error("sendPacket checkSocket  - - - - - - ");
        checkSocket();
//        LogUtils.error("datagramSocket send - - - - -  start ");
        datagramSocket.send(datagramPacket);
        //int sendBufferSize = datagramSocket.getReceiveBufferSize();

//        LogUtils.error("datagramSocket send - - - - - end  getReceiveBufferSize = "+sendBufferSize);

    }


    /**
     * 通过udp socket接受服务器返回的数据,此方法会一直阻塞线程，
     * 直到成功接受服务器返回的数据，或者等待接受数据超时，所以
     * 一定要在子线程中处理当前的操作。
     *
     * @return 服务器返回的原始数据包转义重排后的长度, 或者错误错误码
     */
    public DatagramPacket receiveData(ResponseInfo responseInfo) throws IOException {
//        checkSocket();
        if ((!datagramSocket.isConnected()) || datagramSocket.isClosed()) {
//            LogUtils.error("receiveData dis connected  - - - - - - ");
            return null;
        }

        DatagramPacket datagramPacket;

        if(responseInfo != null){
            datagramPacket = new DatagramPacket(responseInfo.dataBytes, responseInfo.dataBytes.length);
        }else{
            byte[] buffer = new byte[1400];
            datagramPacket = new DatagramPacket(buffer, buffer.length);
        }
        datagramSocket.receive(datagramPacket);
//        int localPort = datagramSocket.getLocalPort();
//        System.out.println("= = = = = = = = = = = = = localPort = "+localPort);
        return datagramPacket;
    }

    public void clearSocket(){
        closeSocket();
        datagramSocket = null;
        instance = null;
//        LogUtils.error("clearSocket  - - - - - - ");
    }

}
