package com.easyder.carmonitor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.baidu.mobstat.StatService;
import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.shinetech.mvp.view.MvpActivity;

import butterknife.ButterKnife;

/**
 * Created by ljn on 2017/3/1.
 */
public abstract class BaseActivity extends MvpActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getView() != 0) {
            setContentView(getView());
            ButterKnife.bind(this);
        }
        CarMonitorApplication.getInstance().addActivityList(this);
    }

    /**
     * layout
     * @return
     */
    protected abstract int getView();

    @Override
    protected void onDestroy() {
        CarMonitorApplication.getInstance().reMoveActivityList(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.notodo_anim_open, R.anim.pop_right2left_anim_close);
    }
}
