package com.shinetech.mvp.network.UDP.resopnsebean;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.R;
import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.CarBaseInfoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/9/4.
 * 意见反馈
 */
public class ResponseFeedBackBean extends BaseVo{

    /**
     * 来源 固定为：车掌控
     */
    private String origin;

    /**
     * 意见
     */
    private String opinion;

    /**
     * 反馈人
     */
    private String feedbackPeople;


    public ResponseFeedBackBean(String opinion, String feedbackPeople) {
        this.opinion = opinion;
        this.feedbackPeople = feedbackPeople;
        origin = MainApplication.getInstance().getString(R.string.feedback_origin);
        requestProtocolHead = Protocol.RESPONSE_PROTOCOL_FEEDBACK_BEAT;
        responseProtocolHead = Protocol.RESPONSE_PROTOCOL_FEEDBACK_BEAT;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{origin, opinion, feedbackPeople};
    }

    @Override
    public void setProperties(Object[] properties) {}

    @Override
    public short[] getDataTypes() {
        return null;
    }
}
