package com.shinetech.mvp.network.UDP.presenter;

import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.UDPRequestCtrl;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseSendMessageTerminal;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseTerminalOperationInstructionBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseViolationLogBean;
import com.shinetech.mvp.view.MvpView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ljn on 2017-04-25.
 */
public class OperationPresenter extends MvpBasePresenter<BaseVo, MvpView<BaseVo>> {

   public void sendInstructResponse(byte instruct, String platenumber, ResponseListener mResponseListener){

      ResponseTerminalOperationInstructionBean mResponseTerminalOperationInstructionBean = new ResponseTerminalOperationInstructionBean(instruct, platenumber);

      UDPRequestCtrl.getInstance().request(mResponseTerminalOperationInstructionBean, mResponseListener);

   }

   public  void sendMessageResponse(String message, String platenumber, ResponseListener mResponseListener){

      ResponseSendMessageTerminal mResponseSendMessageTerminal = new ResponseSendMessageTerminal(message, platenumber);

      UDPRequestCtrl.getInstance().request(mResponseSendMessageTerminal, mResponseListener);

   }




}
