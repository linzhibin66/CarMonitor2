package com.easyder.carmonitor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.widget.PushMessageWidget;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2017-05-23.
 */
public class PushMessageActivity extends BaseActivity implements View.OnTouchListener {

    private PushMessageWidget mPushMessageWidget;

    private boolean isTouch = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPushMessageWidget = new PushMessageWidget(this, new LayoutBackListener() {
            @Override
            public void onBack() {
                finish();
            }
        });
        mPushMessageWidget.setOutmostTouchListener(this);

        setContentView(mPushMessageWidget.getView());
    }

    @Override
    protected int getView() {
        return 0;
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return null;
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void showContentView(BaseVo dataVo) {

    }

    @Override
    public void onStopLoading() {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            finish();
            isTouch = true;
        }
        return true;
    }
}
