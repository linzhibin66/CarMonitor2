package com.shinetech.mvp.network.UDP.scheme;

import android.text.TextUtils;

import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.DatagramPacketDefine;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.DataInfo;
import com.shinetech.mvp.network.UDP.bean.item.ResponseInfo;
import com.shinetech.mvp.network.UDP.scheme.base.BaseParsedUDPDataTool;
import com.shinetech.mvp.utils.ByteUtil;
import com.shinetech.mvp.utils.LogUtils;

import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * 应答式 UDP 数据处理工具类
 * Created by ljn on 2017-06-05.
 */
public class ResponseUDPDataTool extends BaseParsedUDPDataTool {

    /**
     * 数据包帧号 开始位置
     */
    protected final short RESPONSE_DATA_PACKET_FRAME_NO_POSITION = 1;

    /**
     * 数据包总包数 开始位置
     */
    protected final short RESPONSE_DATA_PACKET_TOTAL_POSITION = 3;

    /**
     * 数据包序号 开始位置
     */
    protected final short RESPONSE_DATA_PACKET_SERIAL_NUMBER_POSITION = 5;

    /**
     * 用户名长度 开始位置
     */
    protected final short RESPONSE_USERNAME_DATA_START_POSITION = 8;
    /**
     * 协议编号 开始位置
     */
    protected final short RESPONSE_PROTOCOL_START_POSITION = 10;

    /**
     * 用户名长度 位置
     */
    protected final short RESPONSE_USERNAME_LENGTH_POSITION = 7;

    private void encodeBytes(byte[] encodeBytes, byte[] targetBytes, int[] startIndex){
        byte byteItem;
        int index = startIndex[0];
        for (int i = 0; i < encodeBytes.length; i++) {
            byteItem = encodeBytes[i];
            if (byteItem == 0x7D || byteItem == 0x7E || byteItem == 0x7F) {
                byteItem = (byte) (byteItem ^ 0x20);
                targetBytes[index++] = 0x7D;
            }

            targetBytes[index++] = byteItem;
        }

        startIndex[0] = index;

    }

    @Override
    public DatagramPacket createDatagramPacket(DataInfo mDataInfo, short protocol, DatagramPacket mDatagramPacket) {
        byte[] contentBytes = mDataInfo.dataBytes;

        byte[] tempPacketData = new byte[1400];

        short index = 0;

        //添加帧头
        tempPacketData[index++] = DatagramPacketDefine.FRAME_HEAD;

        //添加帧号
        byte byteItem;
        byte[] bytes;
        bytes = ByteUtil.shortToBytes(mDataInfo.frameNo);
        //System.out.println("createDatagramPacket frameNo = " + mDataInfo.frameNo);
        //对帧号进行转义
        for (int i = 0; i < 2; i++) {
            byteItem = bytes[i];
            if (byteItem == 0x7D || byteItem == 0x7E || byteItem == 0x7F) {
                byteItem = (byte) (byteItem ^ 0x20);
                tempPacketData[index++] = 0x7D;
            }

            tempPacketData[index++] = byteItem;
        }

        bytes = ByteUtil.shortToBytes(mDataInfo.packageTatol);
        //System.out.println("createDatagramPacket frameNo = " + mDataInfo.frameNo);
        //对总包数进行转义
        for (int i = 0; i < 2; i++) {
            byteItem = bytes[i];
            if (byteItem == 0x7D || byteItem == 0x7E || byteItem == 0x7F) {
                byteItem = (byte) (byteItem ^ 0x20);
                tempPacketData[index++] = 0x7D;
            }

            tempPacketData[index++] = byteItem;
        }

        bytes = ByteUtil.shortToBytes(mDataInfo.packageSerialNumber);
        //System.out.println("createDatagramPacket frameNo = " + mDataInfo.frameNo);
        //对包序号进行转义
        for (int i = 0; i < 2; i++) {
            byteItem = bytes[i];
            if (byteItem == 0x7D || byteItem == 0x7E || byteItem == 0x7F) {
                byteItem = (byte) (byteItem ^ 0x20);
                tempPacketData[index++] = 0x7D;
            }

            tempPacketData[index++] = byteItem;
        }

//        if(mDataInfo.packageTatol>1){

            String userName = UserInfo.getInstance().getUserName();
            if(TextUtils.isEmpty(userName)){
                bytes = new byte[]{0};
            }else {
                //添加用户名
                bytes = ByteUtil.stringToBytes(UserInfo.getInstance().getUserName());

                byte[] tempbytes = new byte[bytes.length+1];
                tempbytes[0] = (byte) bytes.length;

                System.arraycopy(bytes, 0, tempbytes, 1, bytes.length);

                bytes = tempbytes;
            }

            for (int i = 0; i < bytes.length; i++) {
                byteItem = bytes[i];
                if (byteItem == 0x7D || byteItem == 0x7E || byteItem == 0x7F) {
                    byteItem = (byte) (byteItem ^ 0x20);
                    tempPacketData[index++] = 0x7D;
                }

                tempPacketData[index++] = byteItem;
            }

            //数据包的长度 contentBytes + 长度
            bytes = ByteUtil.shortToBytes((short) (contentBytes.length+2));

            for (int i = 0; i < bytes.length; i++) {
                byteItem = bytes[i];
                if (byteItem == 0x7D || byteItem == 0x7E || byteItem == 0x7F) {
                    byteItem = (byte) (byteItem ^ 0x20);
                    tempPacketData[index++] = 0x7D;
                }

                tempPacketData[index++] = byteItem;
            }

            //填充协议头
            if(isDebug) LogUtils.d("请求协议头：" + Integer.toHexString(protocol));
            byte[] protocolBytes = ByteUtil.shortToBytes(protocol);

            for (int i = 0; i < protocolBytes.length; i++) {
                byteItem = protocolBytes[i];
                if (byteItem == 0x7D || byteItem == 0x7E || byteItem == 0x7F) {
                    byteItem = (byte) (byteItem ^ 0x20);
                    tempPacketData[index++] = 0x7D;
                }

                tempPacketData[index++] = byteItem;
            }
//        }

        //当帧号到达最大数时重置为0
        send_frame_no = send_frame_no == Short.MAX_VALUE ? 0 : send_frame_no;

        //对编码后的数据包内容进行转义，避免和帧头，帧尾的字节混淆
        for (int i = 0; i < contentBytes.length; i++) {
            byteItem = contentBytes[i];
            if (byteItem == 0x7D || byteItem == 0x7E || byteItem == 0x7F) {
                byteItem = (byte) (byteItem ^ 0x20);
                tempPacketData[index++] = 0x7D;
            }

            tempPacketData[index++] = byteItem;
        }

        //添加帧尾
        tempPacketData[index++] = DatagramPacketDefine.FRAME_END;

        byte[] packetData = new byte[index];

        System.arraycopy(tempPacketData, 0, packetData, 0, index);

//        if(debug)System.out.println("DatagramPacket --> tempPacketData = " + ByteUtil.bufferToString(tempPacketData));
//        if(debug)System.out.println("DatagramPacket --> packetData = " + ByteUtil.bufferToString(packetData));
        if(isDebug)System.out.println("bytes2HexString --> createDatagramPacket dataBytes = " + bytes2HexString(packetData));

        if(mDatagramPacket==null){
            mDatagramPacket = new DatagramPacket(packetData,packetData.length);
        }else{
            mDatagramPacket.setData(packetData);
            mDatagramPacket.setLength(packetData.length);
        }

        if(isDebug)LogUtils.info("createDatagramPacket  packetData length : "+ packetData.length);

        return mDatagramPacket;
    }



    @Override
    public void decodeResponseData(ResponseInfo responseInfo, byte[] rawData, int length) {

        short startIndex = 0;
        startIndex++; //去掉帧头占用的一个字节
        --length; //去掉帧尾

        short index = 1;

        short userNameLength = 0;

        //协议内容长度
        short dataLength = 0;

        byte byteItem;

        //是否需要转义
        boolean escape = false;

        byte[] protocolBytes = new byte[WORD_SIZE];

        byte[] packetLengthBytes = new byte[WORD_SIZE];

        byte[] packetFrameNo = new byte[WORD_SIZE];

        byte[] packetTotalBytes = new byte[WORD_SIZE];

        byte[] packetSerialNumberBytes = new byte[WORD_SIZE];

        byte[] userName = null;

        short protocol = 0;

        //帧号
        short frameNo = 0;

        //总包数
        short packetTotal = 0;

        //包序号
        short packetSerialNumber = 0;

        //数据包解码，转义字符，并填充数据（边转义边校验）
        for (int i = startIndex; i < length; i++) {
            byteItem = rawData[i];
            if (byteItem == 0x7D) { //转义标识字节，丢弃并标记下个字节需要转义
                escape = true;
                continue;
            } else if (escape) {
                byteItem = (byte) (byteItem ^ 0x20);
                escape = false;
            }
            if((index>=RESPONSE_DATA_PACKET_FRAME_NO_POSITION) && index < (RESPONSE_DATA_PACKET_FRAME_NO_POSITION+WORD_SIZE)) {
                packetFrameNo[index - RESPONSE_DATA_PACKET_FRAME_NO_POSITION] = byteItem;

                if ((index - RESPONSE_DATA_PACKET_FRAME_NO_POSITION + 1) == packetFrameNo.length) {
                    //获取帧号
                    frameNo = ByteUtil.shortFromBytes(packetFrameNo);
                    responseInfo.receiveFrameNo = frameNo;
                    if(isDebug)LogUtils.error("decodeResponseData  frameNo: " + frameNo);
                }

            }else if((index>=RESPONSE_DATA_PACKET_TOTAL_POSITION) && index < (RESPONSE_DATA_PACKET_TOTAL_POSITION+WORD_SIZE)){

                packetTotalBytes[index - RESPONSE_DATA_PACKET_TOTAL_POSITION] = byteItem;

                if ((index - RESPONSE_DATA_PACKET_TOTAL_POSITION + 1) == packetTotalBytes.length) {
                    //获取总包数
                    packetTotal = ByteUtil.shortFromBytes(packetTotalBytes);
                    responseInfo.packetTotal = packetTotal;
                    if(isDebug)LogUtils.error("decodeResponseData  packetTotal: " + packetTotal);
                }

            }else if((index>=RESPONSE_DATA_PACKET_SERIAL_NUMBER_POSITION) && index < (RESPONSE_DATA_PACKET_SERIAL_NUMBER_POSITION+WORD_SIZE)){

                packetSerialNumberBytes[index - RESPONSE_DATA_PACKET_SERIAL_NUMBER_POSITION] = byteItem;

                if ((index - RESPONSE_DATA_PACKET_SERIAL_NUMBER_POSITION + 1) == packetTotalBytes.length) {
                    //获取包序号
                    packetSerialNumber = ByteUtil.shortFromBytes(packetSerialNumberBytes);
                    responseInfo.packetSerialNumber = packetSerialNumber;
                    if(isDebug)LogUtils.error("decodeResponseData  packetSerialNumber: " + packetSerialNumber);
                }

            }else{
                if(index == RESPONSE_USERNAME_LENGTH_POSITION){
//                    userNameLength = (short)byteItem;
                    userNameLength = ByteUtil.shortFromBytes(new byte[]{0,byteItem});
                    //System.out.println("decodeResponseData --> userNameLength :  "+userNameLength);
                    userName = new byte[userNameLength];

                }else if(index>= RESPONSE_USERNAME_DATA_START_POSITION && index < (RESPONSE_USERNAME_DATA_START_POSITION +userNameLength)){
                    if(userName != null) {
                        userName[index - RESPONSE_USERNAME_DATA_START_POSITION] = byteItem;
                    }

                }else if((index>=(RESPONSE_USERNAME_DATA_START_POSITION +userNameLength)) && index < ((RESPONSE_USERNAME_DATA_START_POSITION +userNameLength)+WORD_SIZE)){
                    //获取协议内容长度
                    packetLengthBytes[index-(RESPONSE_USERNAME_DATA_START_POSITION +userNameLength)] = byteItem;

                }else if(index >= (RESPONSE_PROTOCOL_START_POSITION+userNameLength)  && index < ((RESPONSE_PROTOCOL_START_POSITION+userNameLength)+WORD_SIZE)){

                    //获取协议字，并进行校验
                    protocolBytes[index-(RESPONSE_PROTOCOL_START_POSITION+userNameLength)] = byteItem;

                    if((index-(RESPONSE_PROTOCOL_START_POSITION+userNameLength)+1) == protocolBytes.length){
                        protocol = ByteUtil.shortFromBytes(protocolBytes);
                        if(isDebug)LogUtils.error("decodeResponseData  protocol: " + protocol);
                        //获取协议编号（协议字）
                        if (protocol == 0x0000) {
                            //应答协议字（接收方接收到一个数据包时，给接收方发送的接收确认，无内容）
                            //System.out.println("mResponseInfo.receiveFrameNo : " + responseInfo.receiveFrameNo);
                            responseInfo.resultStatusCode = ResponseInfo.ACK_PACKET_RECEIVED;
                            responseInfo.protocol = protocol;
                            return;
                        }

                        //检测是否是重复包(必需再0x0000协议字判断之后）
                        if (receive_frame_No != 0 && frameNo != 0 && frameNo == receive_frame_No) {
                            if(isDebug)LogUtils.i("decodeResponseData 收到重复数据包，数据包将被丢弃。帧号当前的帧号：" + receive_frame_No + ", 接收到的帧号: " + frameNo);
                            //发送接受确认包
//                        sendAckPacket();
                            responseInfo.protocol = protocol;

//                        TODO sendAckPacket()
                            responseInfo.resultStatusCode = ResponseInfo.REPEAT_PACKET_RECEIVED;
                            return;
                        }

                       // LogUtils.error("====================== decodeResponseData packetFrameNo = " + frameNo + "   protocol = " + protocol);

                        //保持通信包
                        if (protocol == Protocol.RESPONSE_PROTOCOL_HEART_BEAT) {

                            if(isDebug)LogUtils.d("decodeResponseData 心跳连接包已收到，数据包被丢弃!");
                            responseInfo.resultStatusCode = ResponseInfo.HEART_BEAT_PACKET_RECEIVED;
                            responseInfo.dataLength = 4;
                            responseInfo.protocol = protocol;
                            receive_frame_No = frameNo;
//                            sendAckPacket();
//                          TODO sendAckPacket()
                            return;
                        }

                    }
                }else{
                    //提取协议内容（不一开始进行存储数据包内容，为了在进行校验完成后，才对接受包的数据进行修改）
                    rawData[dataLength] = byteItem;
                    dataLength++;
                }
            }

            index++;
        }

        //if(debug)System.out.println("decodeResponseData --> responseInfo dataBytes = " + ByteUtil.bufferToString(responseInfo.dataBytes));
        try{
            //清除后面无用的数据（rawData为去除帧头、帧尾的数据）
            Arrays.fill(rawData,dataLength,rawData.length,(byte)0);
        }catch (Exception e){
            e.printStackTrace();
        }

        //show userName
        if(userName != null) {

//            String usernameHexString = bytes2HexString(userName);
            String usernameString = ByteUtil.stringFromBytes(userName, 0, userName.length);
            //System.out.println("decodeResponseData --> usernameString :  "+usernameString);
        }

//        if(debug)System.out.println("2222decodeResponseData --> responseInfo dataBytes = " + ByteUtil.bufferToString(responseInfo.dataBytes));
//        if(debug)System.out.println("bytes2HexString --> responseInfo dataBytes = " + bytes2HexString(responseInfo.dataBytes));

        //获取数据包中的包内容长度
        short packetLength = ByteUtil.shortFromBytes(packetLengthBytes);

        //数据包合法长度验证，不合法的数据包将中止解码，并被丢弃（2为协议编号长度）
        if ((packetLength-(short)2) != dataLength) {
            if(isDebug)LogUtils.error("decodeResponseData 数据包长度校验失败，数据包被丢弃，startIndex: " + startIndex + "应该长度为：" + packetLength + ", 实际长度为：" + dataLength);
            responseInfo.resultStatusCode = ResponseInfo.INVALID_DATA_LENGTH;
            return;
        }
//        TODO sendAckPacket()
        receive_frame_No = frameNo;

        responseInfo.resultStatusCode = ResponseInfo.RECEIVE_SUCCESS;
        responseInfo.dataLength = packetLength;
        responseInfo.protocol = protocol;

    }

    //BaseVo 转成 byte[] 数组，用于封装成发送数据包
    @Override
    protected byte[] translatePropertiesToBytes(BaseVo dataVo) {

        return propertiesToBytes(dataVo);

        /*//分包数据处理
        if(dataVo.isCanSubPackageSend()){


        }*/

        /*

        byte[] tempBuffer = new byte[1400];

        short index = 0;
        byte[] bytes;
        String userName = UserInfo.getInstance().getUserName();
        if(TextUtils.isEmpty(userName)){
            bytes = new byte[]{0};
        }else {
            //添加用户名
            bytes = ByteUtil.stringToBytes(UserInfo.getInstance().getUserName());
        }
        tempBuffer[index++] = (byte) bytes.length;
        //if(debug)System.out.println("getUserName length : " + bytes.length);
        System.arraycopy(bytes, 0, tempBuffer, index, bytes.length);
        //if(debug)System.out.println("getUserName HexString : " + bytes2HexString(bytes));
        index += bytes.length;

        //记录数据包长度的起始位置
        short packageLengthLocation = index;


        //数据包内容编码和填充
        Object[] properties = dataVo.getProperties();

        index += 2; //跳过2个字节，留作数据包长度

        //填充协议头
        if(debug) LogUtils.d("请求协议头：" + Integer.toHexString(dataVo.requestProtocolHead));
        byte[] protocolBytes = ByteUtil.shortToBytes(dataVo.requestProtocolHead);
        //if(debug)System.out.println("protocolBytes HexString : " + bytes2HexString(protocolBytes));
        tempBuffer[index++] = protocolBytes[0];
        tempBuffer[index++] = protocolBytes[1];

        Object property;
        //根据属性的类型，编码数据内容
        if(properties!=null){
            for (int i = 0; i < properties.length; i++) {
                property = properties[i];

                if (property instanceof Byte) {
                    tempBuffer[index++] = ((Byte) property);
                    //if(debug)System.out.println(" property Byte HexString : " + byte2HexString(((Byte) property)));
                    continue;
                } else if (property instanceof Short) {
                    bytes = ByteUtil.shortToBytes(((Short) property));
                    //if(debug)System.out.println(" Short Byte HexString : " + bytes2HexString(bytes));
                } else if (property instanceof Integer) {
                    bytes = ByteUtil.intToBytes(((Integer) property));
                   // if(debug)System.out.println(" Integer Byte HexString : " + bytes2HexString(bytes));
                } else if (property instanceof String) {
                    String value = ((String) property);
                    if (TextUtils.isEmpty(value)) {
                        bytes = null; //字符串为空，设置bytes为空，避免复制无效的数据到buffer里面去
                        tempBuffer[index++] = 0;
                    } else {
                        bytes = ByteUtil.stringToBytes(value);
                        tempBuffer[index++] = (byte) bytes.length;
                        //if(debug)System.out.println(" String length : " + bytes.length);
                        //if(debug)System.out.println(" String Byte HexString : " + bytes2HexString(bytes));
                    }

                }else if(property instanceof byte[]){
                    bytes = (byte[]) property;
                    int length = bytes.length;

                    int residual_length = tempBuffer.length - index;
                    if(residual_length< length){
                        byte[] temp = new byte[length + tempBuffer.length];

                        System.arraycopy(tempBuffer, 0, temp, 0, tempBuffer.length);

                        tempBuffer = temp;

                    }

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

        //数据包的长度已经确认，填充数据包长度(去除数据包长度2字节)
        bytes = ByteUtil.shortToBytes((short) (index-((short)2)-packageLengthLocation));
        tempBuffer[packageLengthLocation++] = bytes[0];
        tempBuffer[packageLengthLocation] = bytes[1];


        if(debug)LogUtils.info("translatePropertiesToBytes sendBuffer length : " + tempBuffer.length + "    content length : " + index);

        byte[] sendBuffer = new byte[index];

        System.arraycopy(tempBuffer, 0, sendBuffer, 0, index);

        //if(debug)System.out.println("bytes2HexString --> translatePropertiesToBytes dataBytes = " + bytes2HexString(sendBuffer));

//        if(debug)System.out.println("translatePropertiesToBytes --> sendBuffer = " + ByteUtil.bufferToString(sendBuffer));

        return sendBuffer;

        */
    }

    public byte[] propertiesToBytes(BaseVo dataVo) {

        //数据包内容编码和填充
        Object[] properties = dataVo.getProperties();

        byte[] tempBuffer = new byte[1400];

        int index = 0;

        byte[] bytes;

        Object property;
        //根据属性的类型，编码数据内容
        if(properties!=null){
            for (int i = 0; i < properties.length; i++) {
                property = properties[i];

                if (property instanceof Byte) {
                    tempBuffer[index++] = ((Byte) property);
                    //if(debug)System.out.println(" property Byte HexString : " + byte2HexString(((Byte) property)));
                    continue;
                } else if (property instanceof Short) {
                    bytes = ByteUtil.shortToBytes(((Short) property));
                    //if(debug)System.out.println(" Short Byte HexString : " + bytes2HexString(bytes));
                } else if (property instanceof Integer) {
                    bytes = ByteUtil.intToBytes(((Integer) property));
                    // if(debug)System.out.println(" Integer Byte HexString : " + bytes2HexString(bytes));
                } else if (property instanceof String) {
                    String value = ((String) property);
                    if (TextUtils.isEmpty(value)) {
                        bytes = null; //字符串为空，设置bytes为空，避免复制无效的数据到buffer里面去
                        tempBuffer[index++] = 0;
                    } else {
                        bytes = ByteUtil.stringToBytes(value);
                        tempBuffer[index++] = (byte) bytes.length;
                        //if(debug)System.out.println(" String length : " + bytes.length);
                        //if(debug)System.out.println(" String Byte HexString : " + bytes2HexString(bytes));
                    }

                }else if(property instanceof byte[]){
                    bytes = (byte[]) property;
                    int length = bytes.length;

                    int residual_length = tempBuffer.length - index;
                    if(residual_length< length){
                        byte[] temp = new byte[length + tempBuffer.length];

                        System.arraycopy(tempBuffer, 0, temp, 0, tempBuffer.length);

                        tempBuffer = temp;

                    }

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

        byte[] sendBuffer = new byte[index];

        System.arraycopy(tempBuffer, 0, sendBuffer, 0, index);


//        System.out.println(" - - - - - - - - - - - propertiesToBytes HexString : " + bytes2HexString(tempBuffer));
        return sendBuffer;

    }

}
