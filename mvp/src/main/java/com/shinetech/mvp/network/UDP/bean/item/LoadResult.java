package com.shinetech.mvp.network.UDP.bean.item;


import com.shinetech.mvp.network.UDP.bean.BaseVo;

public enum LoadResult {
    ERROR("请求数据失败!"),
    TIME_OUT("连接超时"),
    NO_DATA("没有数据或数据解析错误!"),
    STATUS_SUCCESS("服务器返回成功信息"),
    STATUS_ERROR("服务器返回错误信息"),
    NO_INTERNET_CONNECT("没有检测到可用的网络连接");

    private String message;
    private String url;
    private BaseVo dataVo; //数据请求成功后的生成的Vo对象
    private Throwable errorObject; //数据请求过程中抛出的错误
    private int statusCode; //服务器返回的状态码
    private boolean isCacheData = false;


    LoadResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public LoadResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public BaseVo getDataVo() {
        return dataVo;
    }

    public LoadResult setDataVo(BaseVo dataVo) {
        this.dataVo = dataVo;
        return this;
    }

    public Throwable getErrorObject() {
        return errorObject;
    }

    public void setErrorObject(Throwable errorObject) {
        this.errorObject = errorObject;
    }

    public String getUrl() {
        return url;
    }

    public LoadResult setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public LoadResult setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public boolean isCacheData() {
        return isCacheData;
    }

    public void setIsCacheData(boolean isCacheData) {
        this.isCacheData = isCacheData;
    }
}
