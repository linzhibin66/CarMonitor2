package com.easyder.carmonitor.widget;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.InfoTool.CarStatusInfoTool;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-10.
 */
public class CarStatusWidget {

    /**
     * 返回按键
     */
    private ImageButton title_back;

    /**
     * 标题
     */
    private TextView title_text;

    /**
     * 搜索按钮
     */
    private ImageButton title_search;

    /**
     * Acc 状态
     */
    private CarInfoItem acc_status;

    /**
     * 定位状态
     */
    private CarInfoItem location_status;

    /**
     * 纬度状态
     */
    private CarInfoItem lat_status;

    /**
     * 经度状态
     */
    private CarInfoItem lng_status;

    /**
     * 运营状态
     */
    private CarInfoItem operation_status;

    /**
     * 保密插件状态
     */
    private CarInfoItem secrecy_status;

    /**
     * 刹车状态
     */
    private CarInfoItem brake_status;

    /**
     * 左转向灯状态
     */
    private CarInfoItem signalleft_status;

    /**
     * 右转向灯状态
     */
    private CarInfoItem signalright_status;

    /**
     * 远光灯状态
     */
    private CarInfoItem highbeam_status;

    /**
     * 油路状态
     */
    private CarInfoItem oiline_status;

    /**
     * 电路状态
     */
    private CarInfoItem circuitry_status;

    private RelativeLayout carstatus_layout_outmost;

    private Context context;

    private View root_layout;

    private String plateNumber;

    private LayoutBackListener backListener;

    public CarStatusWidget(Context context,LayoutBackListener listener) {
        this.context = context;
        this.backListener = listener;
        root_layout = View.inflate(context, R.layout.carstatus_activity, null);

        initTitle(root_layout);
        
        initItems(root_layout);
    }

    public CarStatusWidget(Context context,View layout, LayoutBackListener listener) {
        this.context = context;
        this.backListener = listener;
        root_layout = layout;

        initTitle(root_layout);

        initItems(root_layout);
    }

    public void setPlateNumber(String plateNumber){
        this.plateNumber = plateNumber;
    }

    private void initTitle(View rootView) {

        title_back = (ImageButton) rootView.findViewById(R.id.title_back);
        title_text = (TextView) rootView.findViewById(R.id.title_text);
        title_search = (ImageButton) rootView.findViewById(R.id.title_search);

        title_text.setText(context.getString(R.string.carinfo_status));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backListener != null) {
                    backListener.onBack();
                }
            }
        });

        title_search.setVisibility(View.VISIBLE);
        title_search.setImageResource(R.mipmap.status_refresh);

        title_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Animation animation = AnimationUtils.loadAnimation(context, R.anim.refresh_anim);
                title_search.startAnimation(animation);
                CarInfoBean newestCarInfo = UserInfo.getInstance().getNewestCarInfo(plateNumber);
                if(newestCarInfo != null) {
                    updateAllStatus(newestCarInfo.getStatus());
                }
            }
        });

        RelativeLayout title_layout = (RelativeLayout) rootView.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }
    
    private void initItems(View rootView){

        acc_status = (CarInfoItem) rootView.findViewById(R.id.acc_status);
        location_status = (CarInfoItem) rootView.findViewById(R.id.location_status);
        lat_status = (CarInfoItem) rootView.findViewById(R.id.lat_status);
        lng_status = (CarInfoItem) rootView.findViewById(R.id.lng_status);
        operation_status = (CarInfoItem) rootView.findViewById(R.id.operation_status);
        secrecy_status = (CarInfoItem) rootView.findViewById(R.id.secrecy_status);
        brake_status = (CarInfoItem) rootView.findViewById(R.id.brake_status);
        signalleft_status = (CarInfoItem) rootView.findViewById(R.id.signalleft_status);
        signalright_status = (CarInfoItem) rootView.findViewById(R.id.signalright_status);
        highbeam_status = (CarInfoItem) rootView.findViewById(R.id.highbeam_status);
        oiline_status = (CarInfoItem) rootView.findViewById(R.id.oiline_status);
        circuitry_status = (CarInfoItem) rootView.findViewById(R.id.circuitry_status);
        carstatus_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.carstatus_layout_outmost);

    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(carstatus_layout_outmost!= null) {
            carstatus_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    public View getView() {
        return root_layout;
    }

    public void setbackLayoutListener(LayoutBackListener listener) {
        backListener = listener;
    }

    public void updateAllStatus(int status){

        boolean accStatus = CarStatusInfoTool.getAccStatus(status);
        if(accStatus){
            acc_status.setValues(context.getString(R.string.status_open));
        }else{
            acc_status.setValues(context.getString(R.string.status_close));
        }

        boolean locationStatus = CarStatusInfoTool.getLocationStatus(status);
        if(locationStatus){
            location_status.setValues(context.getString(R.string.status_location));
        }else{
            location_status.setValues(context.getString(R.string.status_unlocation));
        }

        boolean latStatus = CarStatusInfoTool.getLatStatus(status);
        if(latStatus) {
            lat_status.setValues(context.getString(R.string.status_lat_south));
        }else{
            lat_status.setValues(context.getString(R.string.status_lat_north));
        }

        boolean lngStatus = CarStatusInfoTool.getLngStatus(status);
        if (lngStatus){
            lng_status.setValues(context.getString(R.string.status_lng_west));
        }else{
            lng_status.setValues(context.getString(R.string.status_lng_east));
        }

        boolean operationStatus = CarStatusInfoTool.getOperationStatus(status);
        if(operationStatus){
            operation_status.setValues(context.getString(R.string.status_operation_null));
        }else{
            operation_status.setValues(context.getString(R.string.status_operation_all));
        }

        boolean secrecyStatus = CarStatusInfoTool.getSecrecyStatus(status);
        if(secrecyStatus){
            secrecy_status.setValues(context.getString(R.string.status_secrecy_open));
        }else {
            secrecy_status.setValues(context.getString(R.string.status_secrecy_close));
        }

        boolean brakeStatus = CarStatusInfoTool.getBrakeStatus(status);
        if(brakeStatus){
            brake_status.setValues(context.getString(R.string.status_brake_open));
        }else{
            brake_status.setValues(context.getString(R.string.status_brake_close));
        }

        boolean signalLeftStatus = CarStatusInfoTool.getSignalLeftStatus(status);
        if(signalLeftStatus){
            signalleft_status.setValues(context.getString(R.string.status_on));
        }else{
            signalleft_status.setValues(context.getString(R.string.status_off));
        }

        boolean signalRightStatus = CarStatusInfoTool.getSignalRightStatus(status);
        if(signalRightStatus){
            signalright_status.setValues(context.getString(R.string.status_on));
        }else{
            signalright_status.setValues(context.getString(R.string.status_off));
        }

        boolean highBeamStatus = CarStatusInfoTool.getHighBeamStatus(status);
        if(highBeamStatus){
            highbeam_status.setValues(context.getString(R.string.status_on));
        }else{
            highbeam_status.setValues(context.getString(R.string.status_off));
        }

        boolean oilineStatus = CarStatusInfoTool.getOilineStatus(status);
        if(oilineStatus){
            oiline_status.setValues(context.getString(R.string.status_on));
        }else{
            oiline_status.setValues(context.getString(R.string.status_off));
        }

        boolean circuitryStatus = CarStatusInfoTool.getCircuitryStatus(status);
        if(circuitryStatus){
            circuitry_status.setValues(context.getString(R.string.status_on));
        }else{
            circuitry_status.setValues(context.getString(R.string.status_off));
        }

    }
}
