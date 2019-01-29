package com.easyder.carmonitor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.dialog.OperationPwdDialog;
import com.easyder.carmonitor.interfaces.OperationListener;
import com.easyder.carmonitor.interfaces.SendMessagebackListener;
import com.easyder.carmonitor.presenter.OperationActivityPresenter;
import com.easyder.carmonitor.widget.SendMessageWidget;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;

/**
 * Created by ljn on 2017-05-23.
 */
public class SendMessageActivity extends BaseActivity implements View.OnTouchListener {

    private SendMessageWidget sendMessageWidget;

    public static final String MESSAGE_PLATENUMBER = "message_plateNumber";

    private boolean isTouch = false;

    private String plateNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        plateNumber = intent.getStringExtra(MESSAGE_PLATENUMBER);

        sendMessageWidget = new SendMessageWidget(this, new SendMessagebackListener() {
            @Override
            public void onBack() {
                sendMessageWidget.hideInput();
                finish();
            }

            @Override
            public void commit(final String message) {
//                presenter.
//                TODO new Persenter
                OperationPwdDialog mSendMessageOperationPwdDialog = new OperationPwdDialog(SendMessageActivity.this, OperationActivityPresenter.INSTRUCT_SEND_MESSAGE, getString(R.string.compact_operation_lock_cardoor),
                        getString(R.string.operation_send_success), getString(R.string.operation_pwd_error), new OperationListener() {
                    @Override
                    public void toDoOperation(OperationPwdDialog mOperationDialog, ResponseListener mResponseListener) {

                        ((OperationActivityPresenter)presenter).sendMessageResponse(message, plateNumber, mResponseListener);

                    }
                });
                mSendMessageOperationPwdDialog.showOperation(sendMessageWidget.getView());
            }
        });
        sendMessageWidget.setOutmostTouchListener(this);

        setContentView(sendMessageWidget.getView());
    }

    @Override
    protected int getView() {
        return 0;
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return new OperationActivityPresenter();
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
