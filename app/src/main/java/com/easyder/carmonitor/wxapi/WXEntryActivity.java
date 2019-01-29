package com.easyder.carmonitor.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.easyder.carmonitor.R;
import com.shinetech.mvp.utils.ShareUtils;
import com.shinetech.mvp.utils.ToastUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**
 * Created by ljn on 2017-05-09.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, ShareUtils.WX_APP_ID, false);

        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            api.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

        switch (baseReq.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                ToastUtil.showShort("goToGetMsg");
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                ToastUtil.showShort("goToShowMsg");
                break;
            default:
                break;
        }

        finish();

    }

    @Override
    public void onResp(BaseResp baseResp) {

        String result;

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = getString(R.string.share_success);
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                result = getString(R.string.share_sent_failed);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = getString(R.string.share_cancel);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = getString(R.string.share_auth_denied);
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = getString(R.string.share_unsupport);
                break;
            default:
                System.out.println("baseResp.errCode : "+baseResp.errCode);
                result = getString(R.string.share_unknown);
                break;
        }

        ToastUtil.showShort(result);
        finish();

    }
}
