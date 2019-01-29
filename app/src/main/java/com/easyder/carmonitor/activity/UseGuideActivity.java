package com.easyder.carmonitor.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.widget.AboutUsWidget;
import com.easyder.carmonitor.widget.UseGuideWidget;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2017-05-23.
 */
public class UseGuideActivity extends BaseActivity implements View.OnTouchListener {

    private UseGuideWidget useGuideWidget;

    private boolean isTouch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        useGuideWidget = new UseGuideWidget(this, new LayoutBackListener() {
            @Override
            public void onBack() {
               finish();

            }
        });

        useGuideWidget.setOutmostTouchListener(this);
        setContentView(useGuideWidget.getView());

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
        if (!isTouch) {
            finish();
            isTouch = true;
        }
        return true;
    }
}
