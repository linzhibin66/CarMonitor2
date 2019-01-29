
package com.shinetech.mvp.basePresenter;

import com.shinetech.mvp.network.UDP.UDPRequestCtrl;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.view.MvpView;


/**
 * @author 刘琛慧
 *         date 2015/10/27.
 */

public abstract class MvpBasePresenter<M extends BaseVo, V extends MvpView<M>> extends MvpPresenter<M, V> {
    private boolean isDebug = false && LogUtils.isDebug;

    public void loadData(M dataVo) {
        if (isViewAttached()) {
            getView().onLoading();
        }

        UDPRequestCtrl.getInstance().request(dataVo, this);
    }



/**
     * 请求成功，回调View层方法处理成功的结果
     *
     * @param successResult
     */

    @Override
    public void onSuccess(LoadResult successResult) {
        if (isViewAttached()) {
            getView().onStopLoading();
            getView().showContentView((M) successResult.getDataVo());
        } else {
            if(isDebug)LogUtils.e("MvpView已分离，onSuccess方法无法回调showContentView层的方法");
        }
    }


/**
     * 请求失败，回调View层的方法处理错误信息
     *
     * @param errorResult 请求数据错无的结果包含错误信息或者Exception对象
     */

    @Override
    public void onError(LoadResult errorResult) {
        if (isViewAttached()) {
            getView().onStopLoading();
            switch (errorResult) {
                case STATUS_ERROR:
                    ToastUtil.showShort(errorResult.getMessage());
                    break;
                case ERROR:
                    // TODO: 2015/8/11 请求错误处理代码
                    ToastUtil.showShort("服务器维护中，暂时无法访问数据");
                    break;
                case TIME_OUT:
                    ToastUtil.showShort("网络连接不稳定，请重试");
                    if(isDebug)LogUtils.e("数据接收超时！");
                    break;
                case NO_INTERNET_CONNECT:
                    ToastUtil.showShort("没有可用的网络，请检查您的网络设置");
                    break;
                case NO_DATA:
                    ToastUtil.showShort("没有数据或数据解析错误");
                    break;
                default:
                    ToastUtil.showShort(errorResult.getMessage());
                    break;
            }
        } else {
            if(isDebug)LogUtils.e("MvpView已分离，onError方法无法回调MvpView层的方法");
        }
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public void onProgress(int total, int currentProgress, short protocol) {

    }
}

