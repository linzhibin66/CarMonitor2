package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.UpLoadResultDialogListener;

/**
 * Created by ljn on 2017-11-28.
 */

public class CommitMaintenanceResultDialogWidget {

    private Context context;

    private View rootView;

    private ImageView commit_loading;

    private LinearLayout upload_fial_ctrl_layout;

    private RelativeLayout back_ctrl_layout;
    private ImageView upload_maintenance_result_icon;
    private TextView success_title_hint;
    private TextView success_title_text;
    private Button back_ctrl_bottom;

    private Button upload_back;
    private Button upload_recommit;

    private UpLoadResultDialogListener listener;

    private LayoutBackListener backListener;

    private boolean isShowSuccess = false;

    private boolean isShow = false;


    public CommitMaintenanceResultDialogWidget(Context context) {
        this.context = context;

        rootView = View.inflate(context, R.layout.commit_maintenance_result_hint,null);
        
        initView();
    }
    
    private void initView(){

        back_ctrl_layout = (RelativeLayout) rootView.findViewById(R.id.back_ctrl_layout);
        upload_maintenance_result_icon = (ImageView) rootView.findViewById(R.id.upload_maintenance_result_icon);
        back_ctrl_bottom = (Button) rootView.findViewById(R.id.back_ctrl_bottom);
        success_title_hint = (TextView) rootView.findViewById(R.id.success_title_hint);
        success_title_text = (TextView) rootView.findViewById(R.id.success_title_text);

        upload_fial_ctrl_layout = (LinearLayout) rootView.findViewById(R.id.upload_fial_ctrl_layout);
        upload_back = (Button) rootView.findViewById(R.id.upload_back);
        upload_recommit = (Button) rootView.findViewById(R.id.upload_recommit);


        commit_loading = (ImageView) rootView.findViewById(R.id.commit_loading);
    }

    public View getView(){
        return rootView;
    }

    public void showLoadingView(){
        isShow = true;
        back_ctrl_layout.setVisibility(View.GONE);
        commit_loading.setVisibility(View.VISIBLE);

        Animation loading_anim = AnimationUtils.loadAnimation(context, R.anim.operation_loading_anim);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        loading_anim.setInterpolator(interpolator);
        if (commit_loading != null) {
            commit_loading.startAnimation(loading_anim);  //开始动画
        }
    }

    public void showSuccessView(LayoutBackListener backListener){
        /*isShow = true;
        isShowSuccess = true;
        this.backListener = backListener;
        back_ctrl_layout.setVisibility(View.VISIBLE);
        commit_loading.setVisibility(View.GONE);
        commit_loading.setAnimation(null);

        upload_maintenance_result_icon.setImageResource(R.mipmap.icon_commit_success);

        upload_fial_ctrl_layout.setVisibility(View.GONE);
        back_ctrl_bottom.setVisibility(View.VISIBLE);

        success_title_text.setText(R.string.upload_maintenance_commit_success);
        success_title_hint.setText(R.string.upload_maintenance_commit_success_hint);

        back_ctrl_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShow = false;
                if(CommitMaintenanceResultDialogWidget.this.backListener != null) {
                    CommitMaintenanceResultDialogWidget.this.backListener.onBack();
                }
            }
        });
*/
        showResultView(backListener, context.getString(R.string.upload_maintenance_commit_success), context.getString(R.string.upload_maintenance_commit_success_hint), R.mipmap.icon_commit_success);

    }

    public void showResultView(LayoutBackListener backListener, String title, String hint, int resId){
        isShow = true;
        isShowSuccess = true;
        this.backListener = backListener;
        back_ctrl_layout.setVisibility(View.VISIBLE);
        commit_loading.setVisibility(View.GONE);
        commit_loading.setAnimation(null);

        upload_maintenance_result_icon.setImageResource(resId);

        upload_fial_ctrl_layout.setVisibility(View.GONE);
        back_ctrl_bottom.setVisibility(View.VISIBLE);

        success_title_text.setText(title);
        success_title_hint.setText(hint);

        back_ctrl_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShow = false;
                if(CommitMaintenanceResultDialogWidget.this.backListener != null) {
                    CommitMaintenanceResultDialogWidget.this.backListener.onBack();
                }
            }
        });

    }

    public void showFailView(String hint,UpLoadResultDialogListener listener){
        isShow = true;
        this.listener = listener;
        back_ctrl_layout.setVisibility(View.VISIBLE);
        commit_loading.setVisibility(View.GONE);
        commit_loading.setAnimation(null);

        upload_fial_ctrl_layout.setVisibility(View.VISIBLE);
        back_ctrl_bottom.setVisibility(View.GONE);

        upload_maintenance_result_icon.setImageResource(R.mipmap.icon_commit_fail);
        success_title_text.setText(R.string.upload_maintenance_commit_fail);
        success_title_hint.setText(hint);

        upload_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShow = false;
                if(CommitMaintenanceResultDialogWidget.this.listener != null) {
                    CommitMaintenanceResultDialogWidget.this.listener.onBack();
                }
            }
        });

        upload_recommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CommitMaintenanceResultDialogWidget.this.listener != null) {
                    CommitMaintenanceResultDialogWidget.this.listener.onReCommit();
                }
            }
        });
    }



    public boolean isShowSuccess(){
        return (isShow && isShowSuccess);
    }
}
