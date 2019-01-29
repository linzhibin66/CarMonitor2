package com.shinetech.mvp.network.UDP.scheme.base;

import android.text.TextUtils;
import com.shinetech.mvp.network.UDP.DatagramPacketDefine;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.DataInfo;
import com.shinetech.mvp.network.UDP.bean.item.ResponseInfo;
import com.shinetech.mvp.utils.ByteUtil;
import com.shinetech.mvp.utils.LogUtils;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lenovo on 2017-06-05.
 */
public abstract class BaseParsedUDPDataTool {

    protected final int DECODE_SUCCESS = 0x1;//编码成功

    protected short send_frame_no = 0x0000; //数据包帧号

    protected short receive_frame_No = 0x0000; //数据包帧号

    //应答帧号
//    protected short ackFrameNo;

    /**
     * word 大小 2字节
     */
    protected final int WORD_SIZE = 2;

    //心跳连接数据包
//    protected final DatagramPacket heartBeatPacket = new DatagramPacket(DatagramPacketDefine.HEART_BEAT_BUFFER, DatagramPacketDefine.HEART_BEAT_BUFFER.length);

    //接收确认包
    protected final DatagramPacket ackPacket = new DatagramPacket(DatagramPacketDefine.ACK_PACKET, DatagramPacketDefine.ACK_PACKET.length);

    //退出登陆请求数据包
//    protected final DatagramPacket loginOutPacket = new DatagramPacket(DatagramPacketDefine.LOGINOUT_PACKET,DatagramPacketDefine.LOGINOUT_PACKET.length);

//    protected ResponseInfo mResponseInfo = new ResponseInfo();

    protected boolean isDebug = false && LogUtils.isDebug;



/**
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 * 发送 数据解析
 * send data parsed
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 */

    /**
     * 根据dataVo中的参数构建发送内容数据
     * @param dataVo 需要处理VO对象
     * @return DataInfo 发送的内容数据
     */
    public List<DataInfo> sendDataParsed(BaseVo dataVo){

        if(dataVo==null){
            LogUtils.error("sendDataParsed BaseVo is null !!!!!");
            return null;
        }

        if(!dataVo.isCanSubPackageSend()) {

            byte[] sendBuffer = translatePropertiesToBytes(dataVo);
            DataInfo dataInfo = new DataInfo(sendBuffer, (short) sendBuffer.length);
//        设置帧号
            short frameNo = send_frame_no++;
            dataInfo.frameNo = frameNo;

            dataInfo.packageTatol = 1;

            dataInfo.packageSerialNumber = 1;
            List<DataInfo> dataInfoList = new ArrayList<>();
            dataInfoList.add(dataInfo);
            return dataInfoList;
        }else{
            byte[] sendBuffer = translatePropertiesToBytes(dataVo);

            int count = sendBuffer.length / 1024 + (sendBuffer.length % 1024>0? 1 : 0);

            List<DataInfo> dataInfoList = new ArrayList<>();

            String str = "";

            int index = 0;

            for(int i = 0; i<count; i++){

                byte[] bytes;
                if(count == 1){
                    bytes = sendBuffer;
                }else{

                    int length = sendBuffer.length;
                    if(length-(i*1024)>=1024) {
                        bytes = new byte[1024];
                    }else{
                        bytes = new byte[length-(i*1024)];
                    }
                    System.arraycopy(sendBuffer, index, bytes, 0, bytes.length);
                    index += bytes.length;
//                    LogUtils.error("= = = = = = = = = = index = "+index);

                }

                DataInfo dataInfo = new DataInfo(bytes, (short) bytes.length);
            //        设置帧号
                short frameNo = send_frame_no++;
                dataInfo.frameNo = frameNo;

                dataInfo.packageTatol = (short) count;

                dataInfo.packageSerialNumber = (short) (i+1);

//                LogUtils.error("sendDataParsed BaseVo to  dataInfoList  send frameNo = "+ frameNo +"    packageTatol = "+dataInfo.packageTatol + "    packageSerialNumber = " + dataInfo.packageSerialNumber + "   bytes.length = "+ bytes.length + "   sendBuffer.length = "+sendBuffer.length);

                str = str + bytes2HexString(dataInfo.dataBytes);
                dataInfoList.add(dataInfo);

            }
//            LogUtils.error("str.length = " + str.length() + "  dataInfoList  byte to hexString = "+ str  );


            return dataInfoList;

        }

    }

    public DataInfo sendDataParsed(BaseVo dataVo, short frameNo){

        if(dataVo==null){
            LogUtils.error("sendDataParsed BaseVo is null !!!!!");
            return null;
        }

        byte[] sendBuffer = translatePropertiesToBytes(dataVo);
        DataInfo dataInfo = new DataInfo(sendBuffer, (short) sendBuffer.length);
//        设置帧号
        dataInfo.frameNo = frameNo;
        return dataInfo;

    }

    /**
     * 将VO对象的属性按照接口协议转换为接口需要的对应格式的byte[]
     *
     * @param dataVo 需要处理VO对象
     * @return VO对象属性编码后的byte[] udp包内容
     */
    protected byte[] translatePropertiesToBytes(BaseVo dataVo) {

        byte[] tempBuffer = new byte[1400];

        short index = 0;

        //数据包内容编码和填充
        Object[] properties = dataVo.getProperties();
        byte[] bytes;
        index += 2; //跳过2个字节，留作数据包长度

        //填充协议头
        if(isDebug)LogUtils.d("请求协议头：" + Integer.toHexString(dataVo.requestProtocolHead));
        byte[] protocolBytes = ByteUtil.shortToBytes(dataVo.requestProtocolHead);
        tempBuffer[index++] = protocolBytes[0];
        tempBuffer[index++] = protocolBytes[1];

        Object property;
        //根据属性的类型，编码数据内容
        if(properties!=null){
            for (int i = 0; i < properties.length; i++) {
                property = properties[i];

                if (property instanceof Byte) {
                    tempBuffer[index++] = ((Byte) property);
                    continue;
                } else if (property instanceof Short) {
                    bytes = ByteUtil.shortToBytes(((Short) property));
                } else if (property instanceof Integer) {
                    bytes = ByteUtil.intToBytes(((Integer) property));
                } else if (property instanceof String) {
                    String value = ((String) property);
                    if (TextUtils.isEmpty(value)) {
                        bytes = null; //字符串为空，设置bytes为空，避免复制无效的数据到buffer里面去
                        tempBuffer[index++] = 0;
                    } else {
                        bytes = ByteUtil.stringToBytes(value);
                        tempBuffer[index++] = (byte) bytes.length;
                    }
                }else if(property instanceof byte[]){
                    bytes = (byte[]) property;
                    int length = bytes.length;
                    byte[] lengthbytes = ByteUtil.intToBytes(length);
                    System.arraycopy(lengthbytes, 0, tempBuffer, index, lengthbytes.length);
                    index += lengthbytes.length;

                } else {
                    if (property == null) {
                        throw new NullPointerException(dataVo.getClass().getSimpleName() + "的getProperties()方法返回参数的第" + i + "参数为空");
                    } else {
                        throw new IllegalArgumentException("无法识别的数据类型! " + property.toString());
                    }
                }

                if (bytes != null) {
                    System.arraycopy(bytes, 0, tempBuffer, index, bytes.length);
                    index += bytes.length;
                }
            }
        }

        //数据包的长度已经确认，填充数据包长度
        bytes = ByteUtil.shortToBytes(index);
        tempBuffer[0] = bytes[0];
        tempBuffer[1] = bytes[1];

        if(isDebug)LogUtils.info("translatePropertiesToBytes sendBuffer length : " + tempBuffer.length + "    content length : " + index);

        byte[] sendBuffer = new byte[index];

        System.arraycopy(tempBuffer, 0, sendBuffer, 0, index);

        if(isDebug)System.out.println("translatePropertiesToBytes --> sendBuffer = " + ByteUtil.bufferToString(sendBuffer));

        return sendBuffer;

    }

    /**
     * 将转换的内容数据byte[]构建成UDP发送的DatagramPacket
     * @param mDataInfo  内容数据
     * @param protocol  协议号
     * @param mDatagramPacket
     * @return
     */
    public abstract DatagramPacket createDatagramPacket(DataInfo mDataInfo, short protocol, DatagramPacket mDatagramPacket);

    /**
     * 将转换的内容数据byte[]构建成UDP发送的DatagramPacket
     * @param mDataInfo 内容数据
     * @param mDatagramPacket
     * @return
     */
    public DatagramPacket createDatagramPacket(DataInfo mDataInfo, DatagramPacket mDatagramPacket){
        return createDatagramPacket(mDataInfo, (short)-1, mDatagramPacket);
    }

    /**
     * 将转换的内容数据byte[]构建成UDP发送的DatagramPacket
     * @param mDataInfo 内容数据
     * @return
     */
    public DatagramPacket createDatagramPacket(DataInfo mDataInfo){
        return createDatagramPacket(mDataInfo,null);
    }


    /**
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     * 接收 数据解析
     * response data parsed
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     */

    /**
     * （优化）将原始的数据包去掉帧头和帧尾,去掉转义标识字节，恢复转义字节，并重排转义后字节的位置,更新ResponseInfo的信息
     * @author ljn
     * @param rawData 原始的数据包
     * @param length  数据包的有效长度
     * @return 转义后有效的字节长度
     */
    public abstract void decodeResponseData(ResponseInfo responseInfo, byte[] rawData, int length);

    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[ i ] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static String byte2HexString(byte b){
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex.toUpperCase();
    }

    /**
     * 将返回的udp数据解码为对应的属性
     * @param receiveBuffer 需解码的数据
     * @param dataVo 属性对象
     * @param iscompanyAllCar 是否是企业车辆定位状态数据返回
     * @return 是否解码成功
     */
    public short parseDataToProperties(byte[] receiveBuffer, BaseVo dataVo, boolean iscompanyAllCar) {
        short[] dataTypes = dataVo.getDataTypes();
        short index = 0;

        int[] startIndex = new int[1];
        startIndex[0] = index;
        Object[] properties = decodeProperties(receiveBuffer, dataVo, dataTypes, startIndex, iscompanyAllCar);
        dataVo.setProperties(properties);

        return DECODE_SUCCESS;
    }

    /**
     * 解析返回的udp数据，但不赋值到对应的对象属性中，只返回dataTypes的对应值。（注：不能获取list数据，如果要获取List数据请使用parseDataToProperties（）方法）
     * @param receiveBuffer 需解码的数据
     * @param dataTypes 属性类型数组
     * @param iscompanyAllCar 特殊处理0x0210数据包
     * @return
     */
    public Object[] decodeProperties(byte[] receiveBuffer, short[] dataTypes, boolean iscompanyAllCar){
        int[] startIndex = new int[1];
        startIndex[0] = 0;
        return decodeProperties(receiveBuffer, null, dataTypes, startIndex, iscompanyAllCar);
    }

    /**
     * 解析udp数据到属性数组中
     * @param receiveBuffer 需解析的数据
     * @param dataVo  要设置属性的对象
     * @param dataTypes 属性类型数组
     * @param startIndex 从那个位置开始读取数据
     * @param iscompanyAllCar 特殊处理0x0210数据包
     * @return 解析成功后的属性数组
     */
    private Object[] decodeProperties(byte[] receiveBuffer, BaseVo dataVo, short[] dataTypes, int[] startIndex,boolean iscompanyAllCar) {
        byte[] bytes;
        short dataType;
        Object property = null;
        Object[] properties = new Object[dataTypes.length];
        int index = startIndex[0];

        boolean debugDate = false;

        //根据属性的类型解码数据内容
        for (int i = 0; i < dataTypes.length; i++) {
            dataType = dataTypes[i];

            if (dataType == BaseVo.BYTE) {
                if(receiveBuffer.length-index>0) {
                    property = receiveBuffer[index++];
                }else {
                    property = (byte)0;
                }
                if(isDebug && debugDate)System.out.println("hex BYTE : "+byte2HexString((Byte) property));
            } else if (dataType == BaseVo.SHORT) {
                bytes = new byte[2];
                if(receiveBuffer.length-index>0) {
                    System.arraycopy(receiveBuffer, index, bytes, 0, 2);
                }
                if(isDebug && debugDate)System.out.println("hex SHORT : " + bytes2HexString(bytes));
                property = ByteUtil.shortFromBytes(bytes);
                index += bytes.length;
            } else if (dataType == BaseVo.INT) {
                bytes = new byte[4];

                if(receiveBuffer.length-index>0) {

                    if (receiveBuffer.length - index < 4) {

                        System.arraycopy(receiveBuffer, index, bytes, 0, receiveBuffer.length - index);

                    } else {
                        System.arraycopy(receiveBuffer, index, bytes, 0, 4);
                    }
                }
//                System.arraycopy(receiveBuffer, index, bytes, 0, 4);
                if(isDebug && debugDate)System.out.println("hex INT : " + bytes2HexString(bytes));
                property = ByteUtil.intFromBytes(bytes);
                index += bytes.length;
            } else if (dataType == BaseVo.STRING) {

                if(receiveBuffer.length-index>0) {

                    int len = receiveBuffer[index++] & 0xFF;
                    bytes = new byte[len];
                    System.arraycopy(receiveBuffer, index, bytes, 0, len);
                    if (isDebug && debugDate)
                        System.out.println("STRING : int len = " + len + " hex string :" + bytes2HexString(bytes));

                    property = bytes;
                    index += len;

                }else{
                    property = new byte[1];
                }
            } else if (dataType == BaseVo.DATA) {
                if(receiveBuffer.length-index>0) {
                    int len = 0;
                    bytes = new byte[4];
                    if (receiveBuffer.length - index < 4) {
                        index += 4;
                    }else{
                        System.arraycopy(receiveBuffer, index, bytes, 0, 4);
                        len = ByteUtil.intFromBytes(bytes);
                        index += bytes.length;
                    }


                    if(isDebug && debugDate)System.out.println("hex DATA : len = " + len );
                    bytes = new byte[len];
                    System.arraycopy(receiveBuffer, index, bytes, 0, len);
                    property = bytes;
                    index += len;

                    if(isDebug && debugDate)System.out.println("hex DATA :  " + bytes2HexString(bytes) );

                }else{
                    property = new byte[0];
                }

            } else if (dataType == BaseVo.LIST) {

                if(dataVo==null)
                    continue;

                short[] elementDataTypes = dataVo.getElementDataTypes(i);
                byte listLength;
                //特殊处理0x0210协议，list中无声明长度，固定20长度
                if(iscompanyAllCar){
                    listLength = 20;
                }else{
                    listLength = (byte) properties[i - 1]; //这里是前面解析出来的数组的长度
                }
                if(isDebug && debugDate)System.out.println("LIST : hex len = " + byte2HexString(listLength));
                startIndex[0] = index;
                for (int j = 0; j < listLength; j++) {

                    dataVo.addListElement(decodeProperties(receiveBuffer, dataVo, elementDataTypes, startIndex, false), i);
                }
            }

            properties[i] = property;
        }

        startIndex[0] = index;

        return properties;
    }


    /**
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     * 心跳 数据创建
     * Create HeartBeat data
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     */

    /**
     * 创建心跳发送数据包
     * @return DatagramPacket
     */
 /*   public DatagramPacket createHeartBeatPacket(){

        //填充帧头
        byte[] bytes = ByteUtil.shortToBytes(send_frame_no++);
        System.arraycopy(bytes, 0, heartBeatPacket.getData(), 1, bytes.length);

//        bytes = ByteUtil.stringToBytes(BaseVo.phoneNumber);

        //填充用户手机号
        heartBeatPacket.getData()[3] = (byte) bytes.length;
        System.arraycopy(bytes, 0, heartBeatPacket.getData(), 4, bytes.length);

        return heartBeatPacket;
    }*/

    /**
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     * 应答 数据创建
     * Create Ack data
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     */

    /**
     * 创建应答数据包
     * @param ackFrameNo
     * @return DatagramPacket
     */
    public DatagramPacket createAckPacket(short ackFrameNo){

        //填充帧头
        byte[] bytes = ByteUtil.shortToBytes(ackFrameNo);
        System.arraycopy(bytes, 0, ackPacket.getData(), 1, bytes.length);

        return ackPacket;

    }

    /**
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     * 退出登陆 数据创建
     * Create LoginOut data
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     */

    /**
     * 创建退出登陆请求数据包
     * @return DatagramPacket
     */
    /*public DatagramPacket createLoginOutPacket(){

        //填充帧头
        byte[] bytes = ByteUtil.shortToBytes(send_frame_no++);
        System.arraycopy(bytes, 0, loginOutPacket.getData(), 1, bytes.length);

//        bytes = ByteUtil.stringToBytes(BaseVo.phoneNumber);

        //填充用户手机号
        loginOutPacket.getData()[3] = (byte) bytes.length;
        System.arraycopy(bytes, 0, loginOutPacket.getData(), 4, bytes.length);

        return loginOutPacket;
    }*/
}
