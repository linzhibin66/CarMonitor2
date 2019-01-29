package com.easyder.carmonitor.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.FeePayBackListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.widget.orderManager.CommitMaintenanceResultDialogWidget;
import com.shinetech.mvp.interfaces.CheckWXPayOrderListener;
import com.shinetech.mvp.interfaces.WxPayViewListener;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;
import com.shinetech.mvp.wxpay.WxPayManager;

import java.util.Map;

/**
 * Created by ljn on 2018-04-03.
 */

public class FeePayWidget implements View.OnClickListener {

    private final int CHECKORDERSTATUS = 2001;

    private Context context;

    private View rootView;

    private FeePayBackListener listener;
    private String plateNumber;

    private TextView feepay_platenumber;

    private EditText feepay_fee;

    private EditText feepay_info;

    private Button commit_feepay;

    private RelativeLayout feepay_layout_outmost;

    private RelativeLayout feepay_hint_layout;

    private boolean iscommit = false;

    private boolean isUserPayIng = false;

    private CommitMaintenanceResultDialogWidget resultDialog;

    private CheckWXPayOrderListener checkWXPayOrderListener;

    private String orderNo;

    private Handler mHandler;

    public FeePayWidget(Context context, String plateNumber, FeePayBackListener listener) {
        this.context = context;
        this.listener = listener;
        this.plateNumber = plateNumber;
        rootView = View.inflate(context, R.layout.feepay_layout, null);
        initView();
        initTitle(rootView);
    }

    public FeePayWidget(Context context, String plateNumber, View rootView, FeePayBackListener listener) {
        this.context = context;
        this.rootView = rootView;
        this.listener = listener;
        this.plateNumber = plateNumber;
        initView();
        initTitle(rootView);
    }

    private void initView(){

        feepay_platenumber = (TextView) rootView.findViewById(R.id.feepay_platenumber);
        feepay_fee = (EditText) rootView.findViewById(R.id.feepay_fee);
        feepay_info = (EditText) rootView.findViewById(R.id.feepay_info);
        commit_feepay = (Button) rootView.findViewById(R.id.commit_feepay);
        commit_feepay.setOnClickListener(this);

        feepay_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.feepay_layout_outmost);
        feepay_hint_layout = (RelativeLayout) rootView.findViewById(R.id.feepay_hint_layout);

        feepay_platenumber.setText(plateNumber);

        feepay_fee.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL| InputType.TYPE_CLASS_NUMBER);
        feepay_fee.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        if(source.equals(".") && dest.toString().length() == 0){
                            return "0.";
                        }
                        if(dest.toString().contains(".")){
                            int index = dest.toString().indexOf(".");
                            int lengtb = dest.toString().substring(index).length();
                            if(lengtb == 3){
                                return "";
                            }
                        }
                        return null;
                    }
                }
        });
    }

    private void initTitle(View rootview){

        ImageButton title_back = (ImageButton) rootview.findViewById(R.id.title_back);
        TextView title_text = (TextView) rootview.findViewById(R.id.title_text);
        ImageButton title_search = (ImageButton) rootview.findViewById(R.id.title_search);

        title_text.setText(context.getString(R.string.fee_pay));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

        title_search.setVisibility(View.GONE);

        RelativeLayout title_layout = (RelativeLayout) rootview.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(feepay_layout_outmost != null){
            feepay_layout_outmost.setOnTouchListener(touchListener);
        }
    }


    @Override
    public void onClick(View v) {
        commit_feepay.setClickable(false);
        commit_feepay.setFocusable(false);

        String feeStr = feepay_fee.getText().toString();
        if(TextUtils.isEmpty(feeStr)) {
            ToastUtil.showShort(context.getString(R.string.no_input_feepay));
            commit_feepay.setClickable(true);
            commit_feepay.setFocusable(true);
            return;
        }

        String feepayInfoStr = feepay_info.getText().toString();
        if(TextUtils.isEmpty(feepayInfoStr)){
            ToastUtil.showShort(context.getString(R.string.no_input_feepay_info));
            commit_feepay.setClickable(true);
            commit_feepay.setFocusable(true);
            return;
        }


        float fee = Float.parseFloat(feeStr);
//      System.out.println("fee float : " + fee);
        int feeint = (int)(fee*100);
//      System.out.println("fee int : " + feeint);

        WxPayManager wxPayManager = WxPayManager.getInstance();
        wxPayManager.setPayListener(new WxPayViewListener() {
            @Override
            public void onError(String message) {
                ToastUtil.showShort(message);
                commit_feepay.setClickable(true);
                commit_feepay.setFocusable(true);
            }

            @Override
            public void onStart(String orderNo) {
//                startCheckOrderStatus(orderNo);
                FeePayWidget.this.orderNo = orderNo;
                UIUtils.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading();
                    }
                });

            }
        });
        wxPayManager.createOrder(feepayInfoStr, feeint+"", plateNumber);


    }

    public String getorderNo(){
        return orderNo;
    }

    public void startCheckOrderStatus(String orderno){

        if(TextUtils.isEmpty(orderno)){
            orderno = orderNo;
        }

        if(mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if(msg.what == CHECKORDERSTATUS){
                        String msgOrderNo = (String) msg.obj;
                        startCheckOrderStatus(msgOrderNo);
                    }

                }
            };

            checkWXPayOrderListener = new CheckWXPayOrderListener() {
                @Override
                public void onResult(final String resultOrderNo, final Map<String, String> result) {
                    UIUtils.runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            showResult(result);
                            mHandler.removeMessages(CHECKORDERSTATUS);
                            if(isUserPayIng){
                                Message msg = Message.obtain();
                                msg.what = CHECKORDERSTATUS;
                                msg.obj = resultOrderNo;
                                mHandler.sendEmptyMessageDelayed(CHECKORDERSTATUS, 3000);
                            }
                        }
                    });

                }
            };
        }


        WxPayManager wxPayManager = WxPayManager.getInstance();
        wxPayManager.checkOrder(orderno, checkWXPayOrderListener);

    }

    private void showLoading(){

        if(resultDialog == null) {
            resultDialog = new CommitMaintenanceResultDialogWidget(context);
        }

        RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        resultDialog.showLoadingView();
        feepay_hint_layout.removeAllViews();
        feepay_hint_layout.addView(resultDialog.getView(),params);
        feepay_hint_layout.setVisibility(View.VISIBLE);

    }

    public void showResult(Map<String, String> resultData){

        if(resultData == null || resultData.size() == 0){
            feepay_hint_layout.removeAllViews();
            feepay_hint_layout.setVisibility(View.GONE);
            ToastUtil.showShort("单号异常，请重新下单！");
            return;
        }

        String resultTitle;
        String resultHint;

        isUserPayIng = false;

        if("FAIL".equals(resultData.get("return_code"))){
            resultTitle = context.getString(R.string.fee_pay_fail);
            resultHint = resultData.get("return_msg");
            showResultDialog(resultTitle, resultHint, R.mipmap.icon_commit_fail);
            return;
        }

        if("FAIL".equals(resultData.get("result_code"))){
            resultTitle = context.getString(R.string.fee_pay_fail);
            resultHint = resultData.get("err_code");
            showResultDialog(resultTitle, resultHint, R.mipmap.icon_commit_fail);
            return;
        }

        String trade_state = resultData.get("trade_state");
        if("SUCCESS".equals(trade_state)){
            resultTitle = context.getString(R.string.fee_pay_success);
            resultHint = resultData.get("trade_state_desc");
            showResultDialog(resultTitle, resultHint, R.mipmap.icon_commit_success);
            return;
        }else if("USERPAYING".equals(trade_state)){
            isUserPayIng = true;
            //TODO nothing
//            System.out.println("showResult : 用户支付中 . . . . . . . . . ");
            return;

        }else{
            //fail
            resultTitle = context.getString(R.string.fee_pay_fail);
            resultHint = resultData.get("trade_state_desc");
            showResultDialog(resultTitle, resultHint, R.mipmap.icon_commit_fail);
            return ;
        }

    }

    private void showResultDialog(String title, String hint, int iconResId){

        if(resultDialog == null) {
            resultDialog = new CommitMaintenanceResultDialogWidget(context);
        }

        resultDialog.showResultView(new LayoutBackListener() {
            @Override
            public void onBack() {
                if (listener != null) {
                    listener.onBack();
                }
            }
        }, title,hint,R.mipmap.icon_commit_success);
        RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        feepay_hint_layout.removeAllViews();
        feepay_hint_layout.addView(resultDialog.getView(),params);
        feepay_hint_layout.setVisibility(View.VISIBLE);
    }

    public void cancleDialog(){
        feepay_hint_layout.removeAllViews();
        feepay_hint_layout.setVisibility(View.GONE);
    }


}
