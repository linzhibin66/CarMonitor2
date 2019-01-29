package com.shinetech.mvp.view;

import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by Administrator on 2015/8/10.
 */
public interface MvpView<M extends BaseVo> {
    /**
        * 开始加载数据时回调此方法，用以显示加载ProgressDialog或者其他的的操作
        */
        void onLoading();

        /**
         * 默认请求数据解析成功后，将数据填充到View，并显示View
         *
         * @param dataVo 解析成功后返回VO对象
         */
        void showContentView(M dataVo);

        /**
         * 加载数据完成回调方法
         */
        void onStopLoading();
        }
