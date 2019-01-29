package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.activity.SendMessageActivity;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.OperationListener;
import com.easyder.carmonitor.interfaces.SendMessagebackListener;
import com.easyder.carmonitor.presenter.OperationActivityPresenter;
import com.easyder.carmonitor.widget.AboutUsWidget;
import com.easyder.carmonitor.widget.SendMessageWidget;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-28.
 */
public class SendMessageDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private SendMessageWidget sendMessageWidget;

    private String plateNumber;

    private View bundView;

    public boolean needRecover = true;

    private boolean isTouch = false;

    public  SendMessageDialog(Context context, String plateNumber) {
        super(context, R.layout.sendmessage_layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));

        this.plateNumber = plateNumber;

        sendMessageWidget = new SendMessageWidget(context, getLayout(), new SendMessagebackListener() {
            @Override
            public void onBack() {
                sendMessageWidget.hideInput();
                dismiss();
            }

            @Override
            public void commit(final String message) {
//                presenter.
//                TODO new Persenter
                OperationPwdDialog mSendMessageOperationPwdDialog = new OperationPwdDialog(SendMessageDialog.this.context, OperationActivityPresenter.INSTRUCT_SEND_MESSAGE, SendMessageDialog.this.context.getString(R.string.compact_operation_send_message),
                        SendMessageDialog.this.context.getString(R.string.operation_send_message_success), SendMessageDialog.this.context.getString(R.string.operation_pwd_error), new OperationListener() {
                    @Override
                    public void toDoOperation(OperationPwdDialog mOperationDialog, ResponseListener mResponseListener) {
                        OperationActivityPresenter operationActivityPresenter = new OperationActivityPresenter();
                        operationActivityPresenter.sendMessageResponse(message, SendMessageDialog.this.plateNumber, mResponseListener);

                    }
                });
                mSendMessageOperationPwdDialog.showOperation(bundView);
            }
        });
        sendMessageWidget.setOutmostTouchListener(this);
        setALLWindow();
        setFocusable(true);

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));
    }

    public void show(View v){
        isTouch = false;
        needRecover = true;
        bundView = v;
        setHeight(UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight());
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        getLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open));

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            needRecover = true;
            dismiss();
            isTouch = true;
        }
        return true;
    }
}
