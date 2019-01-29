package com.shinetech.mvp.network.UDP.scheme;

import com.shinetech.mvp.network.UDP.DatagramPacketDefine;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.item.DataInfo;
import com.shinetech.mvp.network.UDP.bean.item.ResponseInfo;
import com.shinetech.mvp.network.UDP.scheme.base.BaseParsedUDPDataTool;
import com.shinetech.mvp.utils.ByteUtil;
import com.shinetech.mvp.utils.LogUtils;
import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * 无应答 UDP 数据处理类
 * Created by ljn on 2017/1/24.
 */
public class ParsedUDPDataTool extends BaseParsedUDPDataTool {

    /**
     * 数据包长度 开始位置
     */
    protected final int DATA_PACKET_LEMGTH_START_POSITION = 1;
    /**
     * 协议编号 开始位置
     */
    protected final int PROTOCOL_START_POSITION = 3;

    @Override
    public DatagramPacket createDatagramPacket(DataInfo mDataInfo, short protocol, DatagramPacket mDatagramPacket){

        byte[] contentBytes = mDataInfo.dataBytes;

        byte[] tempPacketData = new byte[1400];

        short index = 0;
        index++;

        //添加帧号
        short frameNo = send_frame_no++;
        mDataInfo.frameNo = frameNo;

        //当帧号到达最大数时重置为0
        send_frame_no = send_frame_no == Short.MAX_VALUE ? 0 : send_frame_no;

        byte byteItem;
        //对编码后的数据包内容进行转义，避免和帧头，帧尾的字节混淆
        for (int i = 0; i < contentBytes.length; i++) {
            byteItem = contentBytes[i];
            if (byteItem == 0x7D || byteItem == 0x7E || byteItem == 0x7F) {
                byteItem = (byte) (byteItem ^ 0x20);
                tempPacketData[index++] = 0x7D;
            }

            tempPacketData[index++] = byteItem;
        }

        tempPacketData[0] = DatagramPacketDefine.FRAME_HEAD; //添加帧头
        tempPacketData[index++] = DatagramPacketDefine.FRAME_END; //添加帧尾

        byte[] packetData = new byte[index];

        System.arraycopy(tempPacketData, 0, packetData, 0, index);

        if(isDebug)System.out.println("DatagramPacket --> tempPacketData = " + ByteUtil.bufferToString(tempPacketData));
        if(isDebug)System.out.println("DatagramPacket --> packetData = " + ByteUtil.bufferToString(packetData));

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
    public void decodeResponseData(ResponseInfo responseInfo, byte[] rawData, int length){

        short startIndex = 0;
        startIndex++; //去掉帧头占用的一个字节
        --length; //去掉帧尾

        short index = 1;

        //协议内容长度
        short dataLength = 0;

        byte byteItem;

        //是否需要转义
        boolean escape = false;

        byte[] protocolBytes = new byte[WORD_SIZE];

        byte[] packetLengthBytes = new byte[WORD_SIZE];

        short protocol = 0;

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

            if((index>=DATA_PACKET_LEMGTH_START_POSITION) && index < (DATA_PACKET_LEMGTH_START_POSITION+WORD_SIZE)){
                //获取协议内容长度
                packetLengthBytes[index-DATA_PACKET_LEMGTH_START_POSITION] = byteItem;

            }else if(index >= PROTOCOL_START_POSITION  && index < (PROTOCOL_START_POSITION+WORD_SIZE)){

                //获取协议字，并进行校验
                protocolBytes[index-PROTOCOL_START_POSITION] = byteItem;

                if((index-PROTOCOL_START_POSITION+1) == protocolBytes.length){
                    protocol = ByteUtil.shortFromBytes(protocolBytes);
                    if(isDebug)LogUtils.error("decodeResponseData protocol bytes ：" + Arrays.toString(protocolBytes) + ", protocol: " + protocol);
                    //获取协议编号（协议字）
                  /*  if (protocol == 0x0000) {
                        //应答协议字（接收方接收到一个数据包时，给接收方发送的接收确认，无内容）
                        responseInfo.resultStatusCode = ResponseInfo.ACK_PACKET_RECEIVED;
                        return;
                    } else */
                    if (protocol == Protocol.PROTOCOL_HEART_BEAT) {

                        if(isDebug)LogUtils.d("decodeResponseData 心跳连接包已收到，数据包被丢弃!");
                        responseInfo.resultStatusCode = ResponseInfo.HEART_BEAT_PACKET_RECEIVED;
                        responseInfo.dataLength = 4;
                        responseInfo.protocol = protocol;
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

            index++;
        }

        if(isDebug)System.out.println("decodeResponseData --> responseInfo dataBytes = " + ByteUtil.bufferToString(responseInfo.dataBytes));
        try{
            //清除后面无用的数据（rawData为去除帧头、帧尾的数据）
            Arrays.fill(rawData,dataLength,rawData.length,(byte)0);
        }catch (Exception e){
            e.printStackTrace();
        }


        if(isDebug)System.out.println("2222decodeResponseData --> responseInfo dataBytes = " + ByteUtil.bufferToString(responseInfo.dataBytes));
        if(isDebug)System.out.println("bytes2HexString --> responseInfo dataBytes = " + bytes2HexString(responseInfo.dataBytes));

        short packetLength = ByteUtil.shortFromBytes(packetLengthBytes);

        //数据包合法长度验证，不合法的数据包将中止解码，并被丢弃
        if ((packetLength-(short)4) != dataLength) {
            if(isDebug)LogUtils.error("decodeResponseData 数据包长度校验失败，数据包被丢弃，startIndex: " + startIndex + "应该长度为：" + packetLength + ", 实际长度为：" + dataLength);
            responseInfo.resultStatusCode = ResponseInfo.INVALID_DATA_LENGTH;
            return;
        }

//        TODO sendAckPacket()



        responseInfo.resultStatusCode = ResponseInfo.RECEIVE_SUCCESS;
        responseInfo.dataLength = packetLength;
        responseInfo.protocol = protocol;

    }
}
