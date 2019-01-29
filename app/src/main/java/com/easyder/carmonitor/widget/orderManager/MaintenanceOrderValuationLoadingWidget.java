package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easyder.carmonitor.R;

/**
 * Created by ljn on 2017-11-29.
 */

public class MaintenanceOrderValuationLoadingWidget {

    private Context context;

    private View rootView;

    private RelativeLayout back_ctrl_layout;

    private ImageView commit_loading;

    private Button back_ctrl_bottom;

    public MaintenanceOrderValuationLoadingWidget(Context context) {
        this.context = context;
        rootView = View.inflate(context, R.layout.valuation_loading_layout, null);

        initLayout();
    }

    private void initLayout(){

        //成功并返回的提示布局
        back_ctrl_layout = (RelativeLayout) rootView.findViewById(R.id.back_ctrl_layout);

        //加载中的等待图片
        commit_loading = (ImageView) rootView.findViewById(R.id.commit_loading);

        back_ctrl_bottom = (Button) rootView.findViewById(R.id.back_ctrl_bottom);

    }

    public View getView(){
        return rootView;
    }

    public void showLoading(){
        back_ctrl_layout.setVisibility(View.GONE);
        commit_loading.setVisibility(View.VISIBLE);

        Animation loading_anim = AnimationUtils.loadAnimation(context, R.anim.operation_loading_anim);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        loading_anim.setInterpolator(interpolator);
        if (commit_loading != null) {
            commit_loading.startAnimation(loading_anim);  //开始动画
        }
    }

    public void showValuationSuccess(View.OnClickListener backClickListener){
        back_ctrl_layout.setVisibility(View.VISIBLE);
        commit_loading.setVisibility(View.GONE);
        commit_loading.setAnimation(null);
        back_ctrl_bottom.setOnClickListener(backClickListener);
    }
}
