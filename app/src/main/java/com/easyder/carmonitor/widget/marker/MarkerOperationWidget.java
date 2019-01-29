package com.easyder.carmonitor.widget.marker;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.activity.SendMessageActivity;
import com.easyder.carmonitor.dialog.OperationPwdDialog;
import com.easyder.carmonitor.interfaces.CompactMarkerListener;
import com.easyder.carmonitor.interfaces.OperationListener;
import com.easyder.carmonitor.presenter.OperationActivityPresenter;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.utils.ToastUtil;

/**
 * Created by ljn on 2017-04-17.
 */
public class MarkerOperationWidget implements View.OnClickListener {

    private Context context;

    private CarInfoBean mCarInfoBean;

    private View rootView;

    private MapView bundView;

    private OperationActivityPresenter mOperationPresenter;

    private CompactMarkerListener mCompactMarkerListener;

    public MarkerOperationWidget(Context context, CarInfoBean mCarInfoBean, MapView bundView) {
        this.context = context;
        this.mCarInfoBean = mCarInfoBean;
        this.bundView = bundView;

        initView();
    }

    public void updateInfo(CarInfoBean mCarInfoBean){
        this.mCarInfoBean = mCarInfoBean;
    }

    public  View getView(){
        return rootView;
    }

    private void initView(){

        rootView = View.inflate(context, R.layout.marker_widget_operation_layout, null);

        TextView send_message = (TextView) rootView.findViewById(R.id.marker_operation_send_message);
        send_message.setOnClickListener(this);

        TextView reset_pws = (TextView) rootView.findViewById(R.id.compact_operation_reset_psw);
        reset_pws.setOnClickListener(this);

        TextView lock_cardoor = (TextView) rootView.findViewById(R.id.lock_cardoor);
        lock_cardoor.setOnClickListener(this);

        TextView open_cardoor = (TextView) rootView.findViewById(R.id.open_cardoor);
        open_cardoor.setOnClickListener(this);

        TextView horn_on = (TextView) rootView.findViewById(R.id.horn_on);
        horn_on.setOnClickListener(this);

        TextView horn_off = (TextView) rootView.findViewById(R.id.horn_off);

        horn_off.setOnClickListener(this);


        TextView compact_operation_fortify = (TextView) rootView.findViewById(R.id.compact_operation_fortify);
        compact_operation_fortify.setOnClickListener(this);

       /* CheckBox check_fortify = (CheckBox) rootView.findViewById(R.id.check_fortify);
        check_fortify.setChecked(false);
        check_fortify.setOnCheckedChangeListener(this);*/

    }


    @Override
    public void onClick(View v) {

        String messageString = "";
        String successString = "";
        String errorString = "";
        byte clickType = -1;

        switch (v.getId()){
            case R.id.marker_operation_send_message:
                messageString = context.getString(R.string.compact_operation_send_message);
                successString = context.getString(R.string.operation_send_success);
                errorString = context.getString(R.string.operation_pwd_error);
                clickType = OperationActivityPresenter.INSTRUCT_SEND_MESSAGE;

                break;
            case R.id.compact_operation_reset_psw:
//                ToastUtil.showShort("reset psw");
                //TODO RESET PWD
                messageString = context.getString(R.string.compact_operation_reset_psw);
                successString = context.getString(R.string.operation_send_success);
                errorString = context.getString(R.string.operation_pwd_error);
                clickType = OperationActivityPresenter.INSTRUCT_RESET_PWD;

                break;
            case R.id.lock_cardoor:
//                ToastUtil.showShort("lock cardoor");
                messageString = context.getString(R.string.compact_operation_lock_cardoor);
                successString = context.getString(R.string.operation_send_success);
                errorString = context.getString(R.string.operation_pwd_error);
                clickType = OperationActivityPresenter.INSTRUCT_LOCK_CARDOOR;

                break;
            case R.id.open_cardoor:
//                ToastUtil.showShort("open cardoor");
                messageString = context.getString(R.string.compact_operation_open_cardoor);
                successString = context.getString(R.string.operation_send_success);
                errorString = context.getString(R.string.operation_pwd_error);
                clickType = OperationActivityPresenter.INSTRUCT_OPEN_CARDOOR;

                break;
            case R.id.horn_on:
//                ToastUtil.showShort("horn on");
                messageString = context.getString(R.string.compact_operation_horn_on);
                successString = context.getString(R.string.operation_send_success);
                errorString = context.getString(R.string.operation_pwd_error);
                clickType = OperationActivityPresenter.INSTRUCT_HORN_ON;

                break;
            case R.id.horn_off:
//                ToastUtil.showShort("horn off");
                messageString = context.getString(R.string.compact_operation_horn_off);
                successString = context.getString(R.string.operation_send_success);
                errorString = context.getString(R.string.operation_pwd_error);
                clickType = OperationActivityPresenter.INSTRUCT_HORN_OFF;

                break;
            case R.id.compact_operation_fortify:
                messageString = context.getString(R.string.compact_operation_fortify);
                successString = context.getString(R.string.operation_send_success);
                errorString = context.getString(R.string.operation_pwd_error);
                clickType = OperationActivityPresenter.INSTRUCT_FORTIFY_ON;
                break;
        }

        if(mCompactMarkerListener != null){
            mCompactMarkerListener.onOperationClick(messageString, successString, errorString, mCarInfoBean.getPlateNumber(), clickType);
        }
    }
/*
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ToastUtil.showShort("fortify check = "+isChecked);
        byte clickType = -1;
        if(isChecked){
            clickType = OperationActivityPresenter.INSTRUCT_FORTIFY_ON;
        }else{
            clickType = OperationActivityPresenter.INSTRUCT_FORTIFY_OFF;
        }

        if(mCompactMarkerListener != null){
            mCompactMarkerListener.onOperationClick(context.getString(R.string.compact_operation_fortify),
                    context.getString(R.string.operation_send_success), context.getString(R.string.operation_pwd_error),
                    mCarInfoBean.getPlateNumber(), clickType);
        }
    }*/

    public void setOperationClick(CompactMarkerListener mCompactMarkerListener){
        this.mCompactMarkerListener = mCompactMarkerListener;
    }

}
