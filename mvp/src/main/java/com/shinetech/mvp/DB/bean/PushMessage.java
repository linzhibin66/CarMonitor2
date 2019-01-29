package com.shinetech.mvp.DB.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ljn on 2017-05-18.
 */

@Entity(indexes = {@Index(value = "sendTime DESC", unique = true)})
public class PushMessage {

    @Id
    private Long id;

    private String messageType;

    @NotNull
    private String sendTime;

    private String message;

    private boolean isRead;

    @Generated(hash = 2068194525)
    public PushMessage(Long id, String messageType, @NotNull String sendTime,
            String message, boolean isRead) {
        this.id = id;
        this.messageType = messageType;
        this.sendTime = sendTime;
        this.message = message;
        this.isRead = isRead;
    }

    public PushMessage(String messageType, @NotNull String sendTime,
                       String message, boolean isRead) {
        this.messageType = messageType;
        this.sendTime = sendTime;
        this.message = message;
        this.isRead = isRead;
    }

    @Generated(hash = 1468533071)
    public PushMessage() {
    }

    public String getMessageType() {
        return this.messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getSendTime() {
        return this.sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
