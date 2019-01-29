package com.shinetech.mvp.network.UDP.bean;

import com.shinetech.mvp.DB.bean.PushMessage;

/**
 * Created by ljn on 2017/2/14.
 */
public class MessagePushBean extends BaseVo {

    /**
     * 信息类型
     */
    private String messageType;

    /**
     * 发送时间
     */
    private String sendTime;

    /**
     * 信息内容
     */
    private String messageContent;

    @Override
    public Object[] getProperties() {
        return null;
    }

    @Override
    public void setProperties(Object[] properties) {
        messageType = parseString((byte[]) properties[0]);
        sendTime = parseString((byte[]) properties[1]);
        messageContent = parseString((byte[]) properties[2]);

    }

    @Override
    public short[] getDataTypes() {
        return new short[]{STRING, STRING, STRING};
    }

    public PushMessage toPushMessage(){

        return new PushMessage(messageType,sendTime,messageContent,false);

    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
