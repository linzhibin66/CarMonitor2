package com.shinetech.mvp.network.UDP.listener;

import com.shinetech.mvp.network.UDP.bean.item.LoadResult;

/**
 * @author 刘琛慧
 * @date 2015/8/10.
 */
public abstract class ResponseListener {

    /**
     * 请求成功回调方法
     *
     * @param successResult
     */
    public abstract void onSuccess(LoadResult successResult);

    /**
     * 请求失败回调方法
     *
     * @param errorResult
     */
    public abstract void onError(LoadResult errorResult);

    public void onProgress(int total, int currentProgress, short protocol){

    }
}
