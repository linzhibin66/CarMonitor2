package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.JsonUtil;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.presenter.UploadMaintenanceEvaluationPresenter;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.presenter.UpLoadImgPresenter;
import com.shinetech.mvp.utils.FileUtils;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;
import com.shinetech.mvp.view.MvpView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-11-21.
 */

public class MaintenanceOrderValuationWidget<M extends BaseVo> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener ,MvpView {

    private LayoutBackListener listener;

    private Context context;

    private View rootView;

    private RelativeLayout valuation_layout_outmost;

    private RelativeLayout maintenance_orderinfo_valuation_result_layout;

    private RatingBar ratingBar;

    private CheckBox good_service_attitude_check;

    private CheckBox arrive_on_time_check;

    private CheckBox thole_check;

    private CheckBox quick_pick_check;

    private CheckBox zeal_check;

    private CheckBox fast_speed_check;

    private TextView normal_check;

    private TextView fault_check;

    private EditText car_status_text;

    private EditText other_comment_suggestion_text;

    private boolean isTermainNormal = true;

    private List<String> selectValuationList = new ArrayList();

    private UploadMaintenanceEvaluationPresenter presenter;

    /**
     * 加载中和提交成功提示框
     */
    private MaintenanceOrderValuationLoadingWidget mMaintenanceOrderValuationLoadingWidget;

    private BaseOrderInfoDB baseOrderInfoDB;

    private String plateNumber;

    public MaintenanceOrderValuationWidget(Context context, BaseOrderInfoDB baseOrderInfoDB, String plateNumber, View rootView, LayoutBackListener backListener) {
        this.context = context;
        this.rootView = rootView;
        this.listener = backListener;
        this.baseOrderInfoDB = baseOrderInfoDB;
        this.plateNumber = plateNumber;

        initTitle(this.rootView);

        initLayout();

        creatPresenter();

    }

    private void initTitle(View view){
        ImageButton title_back = (ImageButton) view.findViewById(R.id.title_back);
        TextView title_text = (TextView) view.findViewById(R.id.title_text);
        TextView title_search = (TextView) view.findViewById(R.id.title_search);

        String title = context.getString(R.string.creat_maintenance_order);
        title_text.setText(title);

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

        title_search.setVisibility(View.VISIBLE);

        title_search.setText(context.getString(R.string.commit));

        title_search.setOnClickListener(this);

        RelativeLayout title_layout = (RelativeLayout) view.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);
    }

    private void initLayout(){

        ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
        good_service_attitude_check = (CheckBox) rootView.findViewById(R.id.good_service_attitude_check);
        good_service_attitude_check.setOnCheckedChangeListener(this);
        arrive_on_time_check = (CheckBox) rootView.findViewById(R.id.arrive_on_time_check);
        arrive_on_time_check.setOnCheckedChangeListener(this);
        thole_check = (CheckBox) rootView.findViewById(R.id.thole_check);
        thole_check.setOnCheckedChangeListener(this);
        quick_pick_check = (CheckBox) rootView.findViewById(R.id.quick_pick_check);
        quick_pick_check.setOnCheckedChangeListener(this);
        zeal_check = (CheckBox) rootView.findViewById(R.id.zeal_check);
        zeal_check.setOnCheckedChangeListener(this);
        fast_speed_check = (CheckBox) rootView.findViewById(R.id.fast_speed_check);
        fast_speed_check.setOnCheckedChangeListener(this);
        other_comment_suggestion_text = (EditText) rootView.findViewById(R.id.other_comment_suggestion_text);

        normal_check = (TextView) rootView.findViewById(R.id.normal_check);
        normal_check.setBackgroundResource(R.drawable.valuation_item_bg_p);
        normal_check.setOnClickListener(this);
        fault_check = (TextView) rootView.findViewById(R.id.fault_check);
        fault_check.setBackgroundResource(R.drawable.valuation_item_bg_n);
        fault_check.setOnClickListener(this);
        car_status_text = (EditText) rootView.findViewById(R.id.car_status_text);

        //result layout 结果提示界面
        maintenance_orderinfo_valuation_result_layout = (RelativeLayout) rootView.findViewById(R.id.maintenance_orderinfo_valuation_result_layout);

        //超出内容的模糊阴影布局
        valuation_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.maintenance_orderinfo_valuation_layout_outmost);
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(valuation_layout_outmost != null) {
            valuation_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    private void creatPresenter(){
        presenter = new UploadMaintenanceEvaluationPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id){
            case R.id.normal_check:
                if(!isTermainNormal) {
                    isTermainNormal = true;
                }
                normal_check.setBackgroundResource(R.drawable.valuation_item_bg_p);
                fault_check.setBackgroundResource(R.drawable.valuation_item_bg_n);
                break;
            case R.id.fault_check:
                if(isTermainNormal) {
                    isTermainNormal = false;
                }
                normal_check.setBackgroundResource(R.drawable.valuation_item_bg_n);
                fault_check.setBackgroundResource(R.drawable.valuation_item_bg_p);

               /* String picturePath = "/sdcard/DCIM/0001.jpg";
                byte[] bytes = FileUtils.getBytes(picturePath);

                presenter.uploadImg(baseOrderInfoDB.getOrderName(), baseOrderInfoDB.getOrderNumber(), "评价图片", "12535.jpg", bytes, new UpLoadImgPresenter.UploadImgListener() {
                    @Override
                    public void OnSuccess() {

                        System.out.println("OnSuccess = = = = = = = = = = ");

                    }

                    @Override
                    public void OnError(String errorInfo) {

                        ToastUtil.showShort(errorInfo);
                    }
                });*/


                break;
            case R.id.title_search:

                //TODO commit to server

                String otherSuggestion = other_comment_suggestion_text.getText().toString().trim();

                String carStatus = car_status_text.getText().toString().trim();

                int rating = (int) ratingBar.getRating();

                String valuationStr = otherSuggestion;
                if(selectValuationList != null && selectValuationList.size()>0) {
                    for (String str : selectValuationList) {
                        valuationStr = valuationStr + "; "+str;
                    }
                }

                presenter.uploadEvaluation(baseOrderInfoDB,plateNumber, isTermainNormal?1:0, carStatus, valuationStr, rating );

                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int id = buttonView.getId();

        String valuationString = "";

        switch (id){
            case R.id.good_service_attitude_check:
                valuationString = good_service_attitude_check.getText().toString().trim();
                break;
            case R.id.arrive_on_time_check:
                valuationString = arrive_on_time_check.getText().toString().trim();
                break;
            case R.id.thole_check:
                valuationString = thole_check.getText().toString().trim();
                break;
            case R.id.quick_pick_check:
                valuationString = quick_pick_check.getText().toString().trim();
                break;
            case R.id.zeal_check:
                valuationString = zeal_check.getText().toString().trim();
                break;
            case R.id.fast_speed_check:
                valuationString = fast_speed_check.getText().toString().trim();
                break;
        }

        if(TextUtils.isEmpty(valuationString)){
            return;
        }

        if(isChecked){
            if(!selectValuationList.contains(valuationString)) {
                selectValuationList.add(valuationString);
            }
        }else{
            if(selectValuationList.contains(valuationString)) {
                selectValuationList.remove(valuationString);
            }
        }

    }

    private void saveValuationInfoToDB(){

        /*if(selectValuationList .size()>0) {
            String jsonArrayString = JsonUtil.stringListToJsonString(selectValuationList);
            maintenanceOrderItem.setSelectValuation(jsonArrayString);
        }

        maintenanceOrderItem.setLevel(ratingBar.getRating());

        String otherSuggestion = other_comment_suggestion_text.getText().toString().trim();
        if(!TextUtils.isEmpty(otherSuggestion)) {
            maintenanceOrderItem.setValuation(otherSuggestion);
        }
        maintenanceOrderItem.setStatus(MaintenanceOrderInfoBean.FINISH_STATUS);

        String carStatus = car_status_text.getText().toString().trim();
        if(!TextUtils.isEmpty(carStatus)) {
            maintenanceOrderItem.setCarStatus(carStatus);
        }

        maintenanceOrderItem.setTerminalStatus(isTermainNormal?1 : 0);

        DBManager.insertMaintenanceOrderInfoBean(maintenanceOrderItem);*/

    }

    @Override
    public void onLoading() {
        showLoadingCommit();

    }

    @Override
    public void showContentView(BaseVo dataVo) {
        //saveValuationInfoToDB();
        showValuationSuccess();
    }

    @Override
    public void onStopLoading() {
        maintenance_orderinfo_valuation_result_layout.setVisibility(View.GONE);
        maintenance_orderinfo_valuation_result_layout.removeAllViews();
    }

    /**
     * 显示提交成功提示
     */
    private void showValuationSuccess(){

        maintenance_orderinfo_valuation_result_layout.setVisibility(View.VISIBLE);
        maintenance_orderinfo_valuation_result_layout.removeAllViews();

        mMaintenanceOrderValuationLoadingWidget.showValuationSuccess(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onBack();
                }
            }
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        maintenance_orderinfo_valuation_result_layout.addView(mMaintenanceOrderValuationLoadingWidget.getView(),params);
    }

    /**
     * 显示加载中
     */
    private void showLoadingCommit(){
        maintenance_orderinfo_valuation_result_layout.setVisibility(View.VISIBLE);
        maintenance_orderinfo_valuation_result_layout.removeAllViews();
        if(mMaintenanceOrderValuationLoadingWidget == null) {
            mMaintenanceOrderValuationLoadingWidget = new MaintenanceOrderValuationLoadingWidget(context);
        }

        mMaintenanceOrderValuationLoadingWidget.showLoading();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        maintenance_orderinfo_valuation_result_layout.addView(mMaintenanceOrderValuationLoadingWidget.getView(),params);
    }
}
