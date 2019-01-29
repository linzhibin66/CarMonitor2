package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.OperationListener;
import com.easyder.carmonitor.presenter.OperationActivityPresenter;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.DatagramPacketDefine;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.presenter.OperationPresenter;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseSendMessageTerminal;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseTerminalOperationInstructionBean;
import com.shinetech.mvp.utils.MD5Tool;
import com.shinetech.mvp.utils.ToastUtil;

/**
 * Created by ljn on 2017-05-16.
 */
public class OperationPwdDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    private byte operationType;

    private String title;

    private  Button operation_commit;

    private TextView operation_title;

    private ImageButton cancel_operation;

    private EditText operation_pwd;

//    private EditText operation_new_pwd;

//    private String newTerminalPwd;

    private boolean isTouchDismiss = false;

    private String successHint;
    private String errorHint;
    private OperationListener mOperationListener;

    public boolean needRecover = false;

    public OperationPwdDialog(Context context, byte instruct, String title, String successHint, String errorHint, OperationListener mOperationListener) {
        super(context, R.layout.base_operationpwd_dialog, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.operationType = instruct;
        this.title = title;
        this.successHint = successHint;
        this.errorHint = errorHint;
        this.mOperationListener = mOperationListener;

        RelativeLayout layout = (RelativeLayout) getLayout();
        layout.setOnTouchListener(this);

        setALLWindow();
        setFocusable(true);

    }

    private void initView(){
        RelativeLayout layout = (RelativeLayout) getLayout();

        View inputOperationPwdLayout = getInputOperationPwdLayout(title);
        layout.removeAllViews();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(inputOperationPwdLayout, layoutParams);

    }

    public byte getInstruct(){
        return operationType;
    }

    private View getInputOperationPwdLayout(final String title){
        View input_Layout = View.inflate(context, R.layout.input_operationpwd_dialog, null);

        operation_title = (TextView) input_Layout.findViewById(R.id.operation_title);

        operation_title.setText(title);

        initBaseView(input_Layout);

        return input_Layout;
    }

    /**
     * 初始化密码输入、提交、退出
     * @param view
     */
    private void initBaseView(View view){
        operation_pwd = (EditText) view.findViewById(R.id.operation_pwd);
        operation_commit = (Button) view.findViewById(R.id.operation_commit);
        cancel_operation = (ImageButton) view.findViewById(R.id.cancel_operation);


        operation_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.showShort("title = " + title + "  type " + operationType);

                //TODO check pwd

                String pwsEncoding;

                UserInfo mUserInfo = UserInfo.getInstance();
                byte loginType = mUserInfo.getLoginType();

                //获取输入的密码
                String pws = operation_pwd.getText().toString().trim();

                //验证密码非空
                if (TextUtils.isEmpty(pws)) {
                    ToastUtil.showShort(context.getString(R.string.pwd_null));
                    return;
                }

                // TODO　显示加载界面
                showLoadingView();

                //加密输入的密码
                if (loginType == DatagramPacketDefine.GIS_TYPE) {
                    pwsEncoding = MD5Tool.encodingSting(pws);
                } else {
                    pwsEncoding = MD5Tool.encodingSting(pws + pws);
                }

                //与加密的登录密码匹配
                if (pwsEncoding.equals(mUserInfo.getPwsEncoding())) {

                    //TODO send operation
                    toDoOperation();


                } else {
                    //TODO show pwd error
                    showResultView(false, errorHint);
                    isTouchDismiss = true;
                }

            }
        });

        cancel_operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void toDoOperation(){

        if (mOperationListener == null) {
            showResultView(false, context.getString(R.string.no_operation_error));
            isTouchDismiss = true;
            return;
        }

        mOperationListener.toDoOperation(this, new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {

                BaseVo dataVo = successResult.getDataVo();
                byte result = 0;
                String errorMessage = "";
                if (dataVo != null && dataVo instanceof ResponseTerminalOperationInstructionBean) {
                    ResponseTerminalOperationInstructionBean mResponseTerminalOperationInstructionBean = (ResponseTerminalOperationInstructionBean) dataVo;
                    result = mResponseTerminalOperationInstructionBean.getResult();
                    errorMessage = mResponseTerminalOperationInstructionBean.getErrorMessage();
                } else if (dataVo != null && dataVo instanceof ResponseSendMessageTerminal) {
                    ResponseSendMessageTerminal mResponseSendMessageTerminal = (ResponseSendMessageTerminal) dataVo;
                    result = mResponseSendMessageTerminal.getResult();
                    errorMessage = mResponseSendMessageTerminal.getErrorMessage();
                }

                if (result == 1) {
                    showResultView(true, successHint);
                } else {
                    showResultView(false, TextUtils.isEmpty(errorMessage) ? context.getString(R.string.operation_error) : errorMessage);
                }
                isTouchDismiss = true;

            }

            @Override
            public void onError(LoadResult errorResult) {
                showResultView(false, errorResult.getMessage());
                isTouchDismiss = true;
            }
        });

    }

    private void showLoadingView(){
        RelativeLayout layout = (RelativeLayout) getLayout();
        layout.removeAllViews();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(getLoadingView(), layoutParams);
    }

    private View getLoadingView(){
        View loadingView = View.inflate(context, R.layout.operation_loading_dialog, null);
        ImageView operation_loading = (ImageView) loadingView.findViewById(R.id.operation_loading);
        Animation loading_anim = AnimationUtils.loadAnimation(context, R.anim.operation_loading_anim);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        loading_anim.setInterpolator(interpolator);
        if (operation_loading != null) {
            operation_loading.startAnimation(loading_anim);  //开始动画
        }

        return loadingView;
    }

    private void showResultView(boolean issuccess,String resultHint){
        RelativeLayout layout = (RelativeLayout) getLayout();
        layout.removeAllViews();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(initResultView(issuccess, resultHint), layoutParams);
    }

    /**
     * 初始化结果界面
     * @param isSuccess
     * @return
     */
    private View initResultView(boolean isSuccess, String resultHint){
        View resultView = View.inflate(context, R.layout.operation_result_dialog, null);
        TextView operation_result_tv = (TextView) resultView.findViewById(R.id.operation_result_tv);
        ImageView operation_result_icon = (ImageView) resultView.findViewById(R.id.operation_result_icon);

        if(isSuccess){
            operation_result_icon.setImageResource(R.mipmap.icon_operation_ok);
        }else{
            operation_result_icon.setImageResource(R.mipmap.icon_operation_wrong);
        }

        operation_result_tv.setText(resultHint);


        return resultView;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(isTouchDismiss) {
            dismiss();
        }
        return false;
    }

    private void show(View v){

        show(v, Gravity.CENTER, 0, 0);
        //显示内容时，执行以下动画
//        timepick_content.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_left2right_anim_open));
    }



    /**
     * 由密码验证开始操作
     * @param v
     */
    public void showOperationPwd(View v){

        if(operationType == OperationActivityPresenter.INSTRUCT_FORTIFY_ON  ||  operationType == OperationActivityPresenter.INSTRUCT_FORTIFY_OFF){
            initFortifyView();
        }else {

            initView();
        }

        show(v);
    }
    /**
     * 由密码验证开始操作
     * @param v
     */
  /*  public void showOperationChangePwd(View v){

        initChangePwdView();

        show(v);
    }*/

    /**
     * 由loading view 开始 直接TODO Operation
     * @param v
     */
    public void showOperation(View v){

        // TODO　显示加载界面
        showLoadingView();

        show(v);

        toDoOperation();

    }

    private void initFortifyView(){
        RelativeLayout layout = (RelativeLayout) getLayout();

        View inputOperationPwdLayout = getInputOperationFortifyPwdLayout();
        layout.removeAllViews();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(inputOperationPwdLayout, layoutParams);
    }

    private View getInputOperationFortifyPwdLayout(){
        View input_Layout = View.inflate(context, R.layout.input_operationfortifypwd_dialog, null);

        final TextView fortify_on = (TextView) input_Layout.findViewById(R.id.fortify_on);
        final TextView fortify_off = (TextView) input_Layout.findViewById(R.id.fortify_off);

        operationType = OperationActivityPresenter.INSTRUCT_FORTIFY_ON;

        fortify_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operationType = OperationActivityPresenter.INSTRUCT_FORTIFY_ON;
                fortify_on.setTextColor(Color.parseColor("#FF1B8FFF"));
                fortify_off.setTextColor(Color.parseColor("#FFC9C9C9"));
            }
        });

        fortify_off.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                operationType = OperationActivityPresenter.INSTRUCT_FORTIFY_OFF;
                fortify_on.setTextColor(Color.parseColor("#FFC9C9C9"));
                fortify_off.setTextColor(Color.parseColor("#FF1B8FFF"));
            }
        });

        initBaseView(input_Layout);

        return input_Layout;

    }



   /* public String getNewTerminalPwd(){
        if(TextUtils.isEmpty(newTerminalPwd)){
            return newTerminalPwd;
        }
        return null;
    }*/

     /* private void initChangePwdView(){
        RelativeLayout layout = (RelativeLayout) getLayout();

        View inputOperationPwdLayout = getInputOperationChangePwdLayout(title);
        layout.removeAllViews();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(inputOperationPwdLayout, layoutParams);

    }

   private View getInputOperationChangePwdLayout(final String title){
        View input_Layout = View.inflate(context, R.layout.input_operation_chagepwd_dialog, null);

        operation_title = (TextView) input_Layout.findViewById(R.id.operation_title);
        operation_pwd = (EditText) input_Layout.findViewById(R.id.operation_pwd);
        operation_new_pwd = (EditText) input_Layout.findViewById(R.id.operation_new_pwd);
        operation_commit = (Button) input_Layout.findViewById(R.id.operation_commit);
        cancel_operation = (ImageButton) input_Layout.findViewById(R.id.cancel_operation);

        operation_title.setText(title);

        operation_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showShort("title = " + title + "  type " + operationType);

                //TODO check pwd

                String pwsEncoding;

                UserInfo mUserInfo = UserInfo.getInstance();
                byte loginType = mUserInfo.getLoginType();

                //获取输入的密码
                String pws = operation_pwd.getText().toString().trim();

                //验证密码非空
                if (TextUtils.isEmpty(pws)) {
                    ToastUtil.showShort(context.getString(R.string.ctrl_pwd_null));
                    return;
                }

                // TODO　显示加载界面
                showLoadingView();

                //加密输入的密码
                if (loginType == DatagramPacketDefine.GIS_TYPE) {
                    pwsEncoding = MD5Tool.encodingSting(pws);
                } else {
                    pwsEncoding = MD5Tool.encodingSting(pws + pws);
                }

                //与加密的登录密码匹配
                if (pwsEncoding.equals(mUserInfo.getPwsEncoding())) {


                    //获取输入的密码
                    String new_pws = operation_new_pwd.getText().toString().trim();

                    //验证密码非空
                    if (TextUtils.isEmpty(new_pws)) {
                        ToastUtil.showShort(context.getString(R.string.terminal_pwd_null));
                        return;
                    }

                    newTerminalPwd = new_pws;

                    //TODO send operation
                    toDoOperation();


                } else {
                    //TODO show pwd error
                    showResultView(false, errorHint);
                    isTouchDismiss = true;
                }

            }
        });

        cancel_operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return input_Layout;
    }*/

}
