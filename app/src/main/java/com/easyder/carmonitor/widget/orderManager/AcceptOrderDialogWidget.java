package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.AcceptOrderDialogListener;
import com.easyder.carmonitor.interfaces.EnterDialogListener;
import com.easyder.carmonitor.presenter.AcceptOrderPresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.AcceptOrderVo;
import com.shinetech.mvp.view.MvpView;

/**
 * Created by ljn on 2017-11-27.
 */

public class AcceptOrderDialogWidget <M extends BaseVo> implements MvpView {

    private Context context;

    private View rootView;

    private RelativeLayout enter_ctrl_layout;
    private TextView enter_title_text;
    private TextView enter_title_hint;
    private Button enter_ctrl_cancel;
    private Button enter_ctrl_enter;
    private ImageView enter_to_loading;

    private AcceptOrderDialogListener listener;

    private AcceptOrderPresenter presenter;

    public AcceptOrderDialogWidget(Context context, AcceptOrderDialogListener listener) {
        this.context = context;

        rootView = View.inflate(context, R.layout.enterdialog_layout, null);
        setClickListener(listener);
        initView();
        creatPresenter();
    }

    public void initView(){
        enter_ctrl_layout = (RelativeLayout) rootView.findViewById(R.id.enter_ctrl_layout);
        enter_title_text = (TextView) rootView.findViewById(R.id.enter_title_text);
        enter_title_hint = (TextView) rootView.findViewById(R.id.enter_title_hint);
        enter_to_loading = (ImageView) rootView.findViewById(R.id.enter_to_loading);
        enter_ctrl_cancel = (Button) rootView.findViewById(R.id.enter_ctrl_cancel);

        enter_ctrl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.OnCancel();
                }
            }
        });
        enter_ctrl_enter = (Button) rootView.findViewById(R.id.enter_ctrl_enter);

        enter_ctrl_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load()
                if(listener != null){
                    BaseVo baseVo = listener.OnLoad();

                    /*onLoading();
                    showContentView(null);
                    onStopLoading();*/
                    presenter.loadData(baseVo);
                    enter_ctrl_layout.setVisibility(View.GONE);

                }
            }
        });
    }

    private void creatPresenter(){
        presenter = new AcceptOrderPresenter();
        presenter.attachView(this);
    }

    public View getView(){
        return rootView;
    }

    public void setTitle(String title){
        enter_title_text.setText(title);
    }

    public void setHint(String hint){
        enter_title_hint.setText(hint);
    }

    public void setClickListener(AcceptOrderDialogListener listener){
        this.listener = listener;
    }

    @Override
    public void onLoading() {
        enter_to_loading.setVisibility(View.VISIBLE);
        Animation loading_anim = AnimationUtils.loadAnimation(context, R.anim.operation_loading_anim);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        loading_anim.setInterpolator(interpolator);
        if (enter_to_loading != null) {
            enter_to_loading.startAnimation(loading_anim);  //开始动画
        }
    }

    @Override
    public void showContentView(BaseVo dataVo) {

        if(listener != null){
            listener.OnSuccess();
        }
    }

    @Override
    public void onStopLoading() {
        /*enter_to_loading.setVisibility(View.GONE);
        enter_to_loading.clearAnimation();
        enter_to_loading.setAnimation(null);*/
        if(listener != null){
            listener.OnCancel();
        }
    }
}
