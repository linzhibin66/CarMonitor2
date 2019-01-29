package com.easyder.carmonitor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.easyder.carmonitor.interfaces.FeedbackListener;
import com.easyder.carmonitor.widget.FeedbackWidget;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2017-05-23.
 */
public class FeedbackActivity extends BaseActivity implements View.OnTouchListener {

    private FeedbackWidget feedbackWidget;

    private boolean isTouch = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        feedbackWidget = new FeedbackWidget(this, new FeedbackListener() {
            @Override
            public void onBack() {
                feedbackWidget.hideInput();
                finish();
            }

            @Override
            public void feedbackCommit(String message) {
//                presenter.
//                TODO new Persenter
            }
        });
        feedbackWidget.setOutmostTouchListener(this);

        setContentView(feedbackWidget.getView());
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
