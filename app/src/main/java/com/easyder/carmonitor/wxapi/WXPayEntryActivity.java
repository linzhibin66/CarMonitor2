package com.easyder.carmonitor.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.activity.BaseActivity;
import com.easyder.carmonitor.interfaces.FeePayBackListener;
import com.easyder.carmonitor.widget.FeePayWidget;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.wxpay.wxapi.Constants;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;



public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler, View.OnTouchListener {
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;

    private FeePayWidget mFeePayWidget;

    private boolean isTouch = false;

    private boolean isdebug = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = View.inflate(this, R.layout.feepay_layout, null);
        setContentView(rootView);
        Intent intent = getIntent();
        String plateNumber = intent.getStringExtra("plateNumber");

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);

		mFeePayWidget = new FeePayWidget(this, plateNumber, rootView, new FeePayBackListener() {
			@Override
			public void onBack() {
				finish();
			}
		});

		mFeePayWidget.setOutmostTouchListener(this);
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getView() {
        return 0;
    }

    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
//		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if(resp instanceof PayResp){
                PayResp mPayResp = (PayResp) resp;
//                    mFeePayWidget.startCheckOrderStatus("");
                mFeePayWidget.startCheckOrderStatus("");
               /* if(mPayResp.errCode == 0){
                    mFeePayWidget.startCheckOrderStatus("");
                }else{
                    mFeePayWidget.cancleDialog();
                    ToastUtil.showShort("errCode = "+mPayResp.errCode);
                }*/
                //mFeePayWidget.startCheckOrderStatus("");true
                if(isdebug)System.out.println("WXPayEntryActivity - - - - - prepayId = " + mPayResp.prepayId);
            }



		}
	}

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            finish();
            isTouch = true;
        }
        return false;
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
}