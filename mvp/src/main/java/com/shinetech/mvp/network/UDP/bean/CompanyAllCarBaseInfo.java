package com.shinetech.mvp.network.UDP.bean;

import com.shinetech.mvp.network.UDP.InfoTool.Protocol;
import com.shinetech.mvp.network.UDP.bean.item.BaseCarInfoResult;

/**
 * Created by ljn on 2017/2/14.
 */
public class CompanyAllCarBaseInfo extends BaseCarInfoResult {

    /**
     * 企业用户名
     */
    private String companyName;

    public CompanyAllCarBaseInfo(String companyName) {
        this.companyName = companyName;
        requestProtocolHead = Protocol.PROTOCOL_COMPANYALLCARINFO_BEAT;
        responseProtocolHead = Protocol.PROTOCOL_COMPANYALLCARINFO_BEAT_RESULT;
    }

    @Override
    public Object[] getProperties() {
        return new Object[]{companyName};
    }

}
