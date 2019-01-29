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
import com.easyder.carmonitor.interfaces.EnterDialogListener;

/**
 * Created by ljn on 2017-11-24.
 */

public class EnterDialogWidget {

    private Context context;

    private View rootView;

    private TextView enter_title_text;
    private TextView enter_title_hint;
    private Button enter_ctrl_cancel;
    private Button enter_ctrl_enter;

    private ImageView enter_to_loading;

    private RelativeLayout enter_ctrl_layout;

    private EnterDialogListener listener;

    public EnterDialogWidget(Context context, EnterDialogListener listener) {
        this.context = context;

        rootView = View.inflate(context, R.layout.enterdialog_layout, null);
        setClickListener(listener);
        initView();
    }

    public void initView(){
        enter_ctrl_layout = (RelativeLayout) rootView.findViewById(R.id.enter_ctrl_layout);
        enter_title_text = (TextView) rootView.findViewById(R.id.enter_title_text);
        enter_title_hint = (TextView) rootView.findViewById(R.id.enter_title_hint);
        enter_ctrl_cancel = (Button) rootView.findViewById(R.id.enter_ctrl_cancel);
        enter_to_loading = (ImageView) rootView.findViewById(R.id.enter_to_loading);

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
                if(listener != null){
                    listener.OnEnter();
                }
            }
        });
    }

    public void showLoading(){
        enter_to_loading.setVisibility(View.VISIBLE);
        enter_ctrl_layout.setVisibility(View.GONE);

        Animation loading_anim = AnimationUtils.loadAnimation(context, R.anim.operation_loading_anim);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        loading_anim.setInterpolator(interpolator);
        if (enter_to_loading != null) {
            enter_to_loading.startAnimation(loading_anim);  //开始动画
        }

    }

    public void showEnterDialog(){
        enter_to_loading.setVisibility(View.GONE);
        enter_to_loading.setAnimation(null);
        enter_ctrl_layout.setVisibility(View.VISIBLE);
    }

    public View getView(){
        return rootView;
    }

    public void setTitle(String title){
        enter_title_text.setText(title);
    }

    public void setHintVisibility(int visibility){
        enter_title_hint.setVisibility(visibility);
    }

    public void setHint(String hint){
        enter_title_hint.setText(hint);
    }

    public void setClickListener(EnterDialogListener listener){
        this.listener = listener;
    }
}
