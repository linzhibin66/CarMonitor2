package com.shinetech.mvp.basePresenter;

import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.view.MvpView;

import java.lang.ref.WeakReference;

/**
 * @author 刘琛慧
 * @date 2015/8/10.
 */
public abstract class MvpPresenter<M extends BaseVo, V extends MvpView<M>> extends ResponseListener implements Presenter<V>{
    private WeakReference<V> viewReference; //MvpView的子类的弱引用

    @Override
    public void attachView(V view) {
        viewReference = new WeakReference<>(view);
    }

    /**
     * 检查Activity或者Fragment是否已经绑定到了Presenter层
     *
     * @return 是否已经绑定
     */
    public boolean isViewAttached() {
        return viewReference != null && viewReference.get() != null;
    }


    /**
     * @return 获取实现了MvpView接口的Activity或者Fragment的引用用来实现回调
     */
    public V getView() {
        return viewReference == null ? null : viewReference.get();
    }

    @Override
    public void detachView() {
        if (viewReference != null) {
            viewReference.clear();
            viewReference = null;
        }
    }

}
