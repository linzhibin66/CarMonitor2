package com.easyder.carmonitor.widget.carCost;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.dialog.orderDialog.CarCostDialog;
import com.easyder.carmonitor.interfaces.CarCostBackListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.presenter.CarCostPresenter;
import com.easyder.carmonitor.widget.orderManager.EnterDialogWidget;
import com.shinetech.mvp.DB.bean.CarCostInfo;
import com.shinetech.mvp.DB.bean.CostDetails;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.CarCostItemInfoVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.CarCostVo;
import com.shinetech.mvp.utils.TimeUtil;
import com.shinetech.mvp.utils.UIUtils;
import com.shinetech.mvp.view.MvpView;

import java.util.Date;
import java.util.List;

/**
 * Created by ljn on 2017-12-12.
 */

public class CarCostWidget <M extends BaseVo> implements MvpView {


    private Context context;

    private String plateNumber;

    private View rootView;

    private CarCostBackListener listener;

    private RelativeLayout carcost_layout_outmost;

    private RelativeLayout carcost_enter_layout;

    private LinearLayout carcost_info_content_layout;

    private TextView carcost_platenumber;

    private TextView carcost_update;

    private TextView all_arrears;

    private TextView arrears_month;

    private CarCostPresenter presenter;

    private EnterDialogWidget enterDialogWidget;

    private CarCostDialog dialog;

    public CarCostWidget(CarCostDialog dialog, Context context, String plateNumber, View rootView, CarCostBackListener listener) {
        this.context = context;
        this.plateNumber = plateNumber;
        this.rootView = rootView;
        this.listener = listener;
        this.dialog = dialog;

        initTitle(this.rootView);

        creatPresenter();

        initLayout();
    }

    private void initTitle(View view){
        ImageButton title_back = (ImageButton) view.findViewById(R.id.title_back);
        TextView title_text = (TextView) view.findViewById(R.id.title_text);
        TextView title_search = (TextView) view.findViewById(R.id.title_search);

        String title = context.getString(R.string.car_cost);

        title_text.setText(title);

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack(dialog);
                }
            }
        });

        title_search.setText(context.getString(R.string.fee_pay));
        title_search.setVisibility(View.VISIBLE);
        title_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShowFeePay(plateNumber);
            }
        });

        RelativeLayout title_layout = (RelativeLayout) view.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);
    }

    private void initLayout(){


        carcost_platenumber = (TextView) rootView.findViewById(R.id.carcost_platenumber);

        carcost_update = (TextView) rootView.findViewById(R.id.carcost_update);

        all_arrears = (TextView) rootView.findViewById(R.id.all_arrears);

        arrears_month = (TextView) rootView.findViewById(R.id.arrears_month);


        carcost_info_content_layout = (LinearLayout) rootView.findViewById(R.id.carcost_info_content_layout);
        carcost_enter_layout = (RelativeLayout) rootView.findViewById(R.id.carcost_enter_layout);

        if(TextUtils.isEmpty(plateNumber)){
            if (listener != null) {
                listener.onBack(dialog);
            }
            return;
        }

       /* CarCostInfo carCostInfo = new CarCostInfo(plateNumber, (short) 1, "SDL145235956756", 1523465, 5, 5);

        for(int i = 0; i<5;i++){

            carCostInfo.addCostDetails(new CostDetails("SDL145235956756","GPS费用" ,8500, Math.abs(8500-(i*8500)), 0, 500,8500-(i*8500)>0? 0 : Math.abs(8500-(i*8500)), 8500-(i*8500)>0?0:i+1,8500-(i*8500)>0?8500:0, 500,8500-(i*8500)>0?8500:0,
                    TimeUtil.getTiemString(new Date(System.currentTimeMillis()-300*24*60*60*1000)), TimeUtil.getTiemString(new Date(System.currentTimeMillis()))));

        }

        if(carCostInfo != null) {
            updata(carCostInfo);
        }else{
            if (listener != null) {
                listener.onBack();
            }
            return;
        }*/

        carcost_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.carcost_layout_outmost);
        enterDialogWidget = new EnterDialogWidget(context,null);

        carcost_platenumber.setText(context.getString(R.string.carcost_platenumber, plateNumber));

        carcost_update.setText(context.getString(R.string.carcost_update_time, ""));

       presenter.requestCarCost(plateNumber);

    }

    private void updata(CarCostVo mCarCostVo){

        String plateNumberColor = mCarCostVo.getPlateNumber();

        String[] split = plateNumberColor.split(" ");

        String plateNumber;
        if(split.length == 2){
            plateNumber = split[1];
        }else{
            plateNumber = plateNumberColor;
        }

        carcost_platenumber.setText(context.getString(R.string.carcost_platenumber, plateNumber));

        carcost_update.setText(context.getString(R.string.carcost_update_time, TimeUtil.getTiemString(new Date(System.currentTimeMillis()))));

        all_arrears.setText(""+mCarCostVo.getAllArrearage());

        arrears_month.setText(""+ mCarCostVo.getArrearageMonth());

        addPayItems(mCarCostVo);

    }

    private void addPayItems(CarCostVo mCarCostVo){

        List<CarCostItemInfoVo> carCostItems = mCarCostVo.getCarCostItems();

        if(carCostItems == null || carCostItems.size() == 0){
            return ;
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,UIUtils.dip2px(10),0,0);

        for(CarCostItemInfoVo carCostItemInfo : carCostItems){
            CarCostItemLayoutWidget carCostItemLayoutWidget = new CarCostItemLayoutWidget(context, carCostItemInfo);
            carcost_info_content_layout.addView(carCostItemLayoutWidget.getItemLayout(),params);
        }

    }

    private void creatPresenter(){

        presenter = new CarCostPresenter();
        presenter.attachView(this);

    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(carcost_layout_outmost != null) {
            carcost_layout_outmost.setOnTouchListener(touchListener);
        }
    }


    @Override
    public void onLoading() {
        showLoading();
    }

    private void showLoading(){
        if(carcost_enter_layout != null) {
            carcost_enter_layout.removeAllViews();

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) carcost_enter_layout.getLayoutParams();
            layoutParams.setMargins(0,0,0,0);
            carcost_enter_layout.setVisibility(View.VISIBLE);

            enterDialogWidget.showLoading();

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            carcost_enter_layout.addView(enterDialogWidget.getView(), params);
        }

    }

    @Override
    public void showContentView(BaseVo dataVo) {
        if(dataVo instanceof CarCostVo){
            CarCostVo mCarCostVo = (CarCostVo) dataVo;
            updata(mCarCostVo);

        }else{
            if (listener != null) {
                listener.onBack(dialog);
            }
            return;
        }
    }

    @Override
    public void onStopLoading() {
        dismissEnterLayout();
    }

    private void dismissEnterLayout(){
        if(carcost_enter_layout != null) {
            carcost_enter_layout.setVisibility(View.GONE);
            carcost_enter_layout.removeAllViews();
        }

    }
}
