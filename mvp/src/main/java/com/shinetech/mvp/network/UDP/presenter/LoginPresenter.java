package com.shinetech.mvp.network.UDP.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.R;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.DatagramPacketDefine;
import com.shinetech.mvp.network.UDP.listener.HeartBeatListener;
import com.shinetech.mvp.network.UDP.InfoTool.LoginTool;
import com.shinetech.mvp.network.UDP.UDPHeartBeatConnect;
import com.shinetech.mvp.network.UDP.bean.LoginBean;
import com.shinetech.mvp.network.UDP.bean.UserAuthentication;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.MD5Tool;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.view.MvpView;

/**
 * Created by ljn on 2017/2/14.
 */
public class LoginPresenter extends MvpBasePresenter<BaseVo, MvpView<BaseVo>> {


    private String pws;

    private String userName;

    private final boolean isDebug = true && LogUtils.isDebug;

    private boolean isRememberPWD = false;

    /**
     *
     * @param view 绑定界面，用于界面回掉刷新
     */
   /* public LoginPresenter(MvpView<BaseVo> view) {
        attachView(view);
    }*/

    /**
     * 用户登录
     * @param loginType  DatagramPacketDefine中查看Type
     * @param userName 用户名
     */
    public void login(String userName, String pws, byte loginType, boolean isRememberPWD){
        this.pws = pws;
        this.userName = userName;
        this.isRememberPWD = isRememberPWD;
        UserAuthentication mUserAuthentication = new UserAuthentication(loginType,userName);
        loadData(mUserAuthentication);
    }

    private boolean isEncodingPwd(byte loginType){

        String historyLoginName = UserInfo.getInstance().getHistoryLoginName();
        String historyUserPwd = UserInfo.getInstance().getHistoryUserPwd();
        if((!TextUtils.isEmpty(historyLoginName)) && (!TextUtils.isEmpty(historyUserPwd))) {
            if (loginType == DatagramPacketDefine.USER_TYPE && userName.contains(historyLoginName) && historyUserPwd.equals(pws)) {
                return true;
            }

            if(historyLoginName.equals(userName) && historyUserPwd.equals(pws)){
                return true;
            }
        }
        return false;
    }

    /**
     * 通过申请Token
     * @param mUserAuthentication
     */
    private void login(UserAuthentication mUserAuthentication){
        String loginToken;
        byte loginType = mUserAuthentication.getLoginType();

        if(isEncodingPwd(loginType) && UserInfo.getInstance().getRememberPwdSetting()) {
            loginToken = LoginTool.getEncodingToken(mUserAuthentication.getResultUserName(), mUserAuthentication.getToken(), pws);
        }else {
            loginToken = LoginTool.getToken(loginType, mUserAuthentication.getResultUserName(), mUserAuthentication.getToken(), pws);
        }

        LoginBean loginBean = new LoginBean(mUserAuthentication.getLoginType(), mUserAuthentication.getUserName(), loginToken);

        loadData(loginBean);

    }


    @Override
    public void onSuccess(LoadResult successResult) {

        BaseVo dataVo = successResult.getDataVo();
        if(dataVo instanceof UserAuthentication){
            UserAuthentication mUserAuthentication = (UserAuthentication) dataVo;
            if(mUserAuthentication!=null && mUserAuthentication.getTokenResult() == 0x01){
                login(mUserAuthentication);
            }else{

                if(mUserAuthentication!=null){
                    byte[] error = mUserAuthentication.getToken();
                    if(error!=null && error.length>0){

                        ToastUtil.showShort(BaseVo.parseString(error));

                        if (isViewAttached()) {
                            getView().onStopLoading();
                        }

                        return;
                    }
                }

                ToastUtil.showShort(MainApplication.getInstance().getString(R.string.login_error));
                if (isViewAttached()) {
                    getView().onStopLoading();
                }
            }
        }else if(dataVo instanceof LoginBean){

            LoginBean mLoginBean = (LoginBean) dataVo;
            if(mLoginBean!=null && mLoginBean.getLoginResult() == 0x01){
    //            TODO 保持登录信息
                UDPHeartBeatConnect.getInstance().startHeartBeatAck(new HeartBeatListener() {
                    @Override
                    public void sendToFail() {
                        if (isDebug)
                            LogUtils.error("startHeartBeatAck send to fail -----------------------------------------------------");
                    }

                    @Override
                    public void sendToError() {
                        LogUtils.error("disConnect with the server");
    //                    TODO 与服务器断开连接，重新登录
                        MainApplication.getInstance().sendBroadcast(new Intent(MainApplication.DISCONNECT_SERVICE));

                    }
                });

                //save pwd
                String pwsEncoding;

                if(isEncodingPwd(mLoginBean.getLoginType())){
                    pwsEncoding = pws;
                }else {
                    if (mLoginBean.getLoginType() == DatagramPacketDefine.GIS_TYPE) {
                        pwsEncoding = MD5Tool.encodingSting(pws);
                    } else {
                        pwsEncoding = MD5Tool.encodingSting(pws + pws);
                    }
                }

                UserInfo.getInstance().setPwsEncoding(pwsEncoding);
                UserInfo.getInstance().setUserName(mLoginBean.getResultUserName());
                UserInfo.getInstance().setIsPerson(mLoginBean.getResultLoginType() == DatagramPacketDefine.USER_TYPE);
                UserInfo.getInstance().setLoginType(mLoginBean.getResultLoginType());
                UserInfo.getInstance().setUserPermission(mLoginBean.getUserPermission());

                String resultUserName = mLoginBean.getResultUserName();
                String car_color = "";

                if(mLoginBean.getLoginType() ==  DatagramPacketDefine.USER_TYPE){
                    String[] split = resultUserName.split(" ");
                    if(split.length == 2) {
                        car_color = split[0];
                        resultUserName = split[1];
                    }
                }

                if(isRememberPWD) {
                    UserInfo.getInstance().saveLoginUser(pwsEncoding, car_color, resultUserName, isRememberPWD);
                }else{
                    UserInfo.getInstance().saveLoginUser("", car_color, resultUserName, isRememberPWD);
                }

                super.onSuccess(successResult);

            }else{

                if (isViewAttached()) {
                    getView().onStopLoading();
                }

                if(mLoginBean!=null){
                    String error = mLoginBean.getError();
                    if(!TextUtils.isEmpty(error)){
                        ToastUtil.showShort(error);
                        return;
                    }
                }

                ToastUtil.showShort(MainApplication.getInstance().getString(R.string.login_error));

                if (isDebug)
                    LogUtils.error("login error -----------------  Error : "+mLoginBean.getError());
            }
//            update UI

        }

    }

    @Override
    public void onError(LoadResult errorResult) {
        /*BaseVo dataVo = errorResult.getDataVo();

        if(dataVo==null)
            return;

        UserAuthentication mUserAuthentication = (UserAuthentication) dataVo;
        if(mUserAuthentication!=null && mUserAuthentication.getTokenResult() == 0x00){
            if (debug){
                byte[] token = mUserAuthentication.getToken();
                if(token!=null && token.length>0){
                    LogUtils.error("ResponseUserAuthentication error -----------------  Error : "+mUserAuthentication.parseString(token));
                }else{
                    LogUtils.error("ResponseUserAuthentication error -----------------  Error : null");
                }

            }
        }*/
        super.onError(errorResult);
    }
}
