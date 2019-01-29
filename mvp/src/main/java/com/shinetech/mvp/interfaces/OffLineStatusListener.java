package com.shinetech.mvp.interfaces;

/**
 * Created by ljn on 2016/11/15.
 */
public class OffLineStatusListener {

    public void onStart(){}

    /**
     * return 是否删除该监听
     * @return
     */
    public boolean onFinish(){
        return false;
    }

    public void onStop(){}

    public void onReStart(){}

    public boolean onRemove(){
        return false;
    }
}
