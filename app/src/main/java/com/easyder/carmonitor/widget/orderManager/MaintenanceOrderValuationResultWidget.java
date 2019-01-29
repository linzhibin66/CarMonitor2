package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.bean.orderBean.SelectOrderByNumberVo;

import java.util.List;

/**
 * Created by ljn on 2017-11-22.
 */

public class MaintenanceOrderValuationResultWidget {

    private Context context;

    private View rootView;

    public MaintenanceOrderValuationResultWidget(Context context, SelectOrderByNumberVo orderinfo) {
        this.context = context;

        rootView = View.inflate(context, R.layout.valuation_result_info_item,null);

        initView(orderinfo);
    }

    private void initView(SelectOrderByNumberVo orderinfo){

        List<String> fieldNameList = null;

        List<String> fieldContentList = null;

        DecodeUDPDataTool.OrderContentListItemData orderContentListItemData = DecodeUDPDataTool.decodeItemByDataName(orderinfo.getContentList(),
                context.getString(R.string.order_content_maintenance_evaluate));

        if(orderContentListItemData != null) {
           fieldNameList = orderContentListItemData.getFieldNameList();
            List<List<String>> fieldListContent = orderContentListItemData.getFieldListContent();

            if (fieldListContent != null && fieldListContent.size() == 1) {
                fieldContentList = fieldListContent.get(0);
            }
        }

        if(fieldNameList == null || fieldNameList.size() == 0 || fieldContentList == null || fieldContentList.size() == 0){
            rootView = null;
            return;
        }

        //评星级
        RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);

        int levelIndex = fieldNameList.indexOf(context.getString(R.string.service_valuation));

        if(levelIndex >= 0) {
            String levelStr = fieldContentList.get(levelIndex);

            int level = 5;
            try {
                level = Integer.parseInt(levelStr);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ratingBar.setRating(level);

        }


        //评价时间
        int valuationTimeIndex = fieldNameList.indexOf(context.getString(R.string.service_valuation_time));
        if(valuationTimeIndex >= 0) {

            String valuationTime = fieldContentList.get(valuationTimeIndex);

            TextView evaluate_time = (TextView) rootView.findViewById(R.id.evaluate_time);
            evaluate_time.setText(valuationTime);

        }

        //设备状态
        int termainStatusIndex = fieldNameList.indexOf(context.getString(R.string.upload_maintenance_result_termain_status));

        if(termainStatusIndex >= 0) {

            String termainStatusStr = fieldContentList.get(termainStatusIndex);

            int termainStatus = 1;
            try {
                termainStatus = Integer.parseInt(termainStatusStr);
            } catch (Exception e) {
                e.printStackTrace();
            }


            TextView termain_status_text = (TextView) rootView.findViewById(R.id.termain_status_text);
            String termain_status_str = context.getString(R.string.valuation_termain_status_reslut, (termainStatus == 1) ? context.getString(R.string.normal_str) : context.getString(R.string.fault_str));
            termain_status_text.setText(termain_status_str);
        }

        //车辆状况
        int carStatusIndex = fieldNameList.indexOf(context.getString(R.string.valuation_carstatus_title));
        if(carStatusIndex >= 0) {

            String carStatusStr = fieldContentList.get(carStatusIndex);

            TextView car_status_text = (TextView) rootView.findViewById(R.id.car_status_text);
            car_status_text.setText(context.getString(R.string.valuation_carstatus_reslut, carStatusStr));
        }

        //选择的评价
        /*String selectValuation = maintenanceOrderItem.getSelectValuation();

            MyGridView select_valuation_gridview = (MyGridView) rootView.findViewById(R.id.select_valuation_gridview);
        if(TextUtils.isEmpty(selectValuation)){
            select_valuation_gridview.setVisibility(View.GONE);
        }else{
            List<String> selectValuationList = JsonUtil.JSONArrayToList(selectValuation);
            if(selectValuationList == null || selectValuationList.size() == 0){
                select_valuation_gridview.setVisibility(View.GONE);
            }else{

                select_valuation_gridview.setVisibility(View.VISIBLE);

                ValuationSelectGridAdapter valuationSelectGridAdapter = new ValuationSelectGridAdapter(context);
                valuationSelectGridAdapter.initData(selectValuationList);
                select_valuation_gridview.setAdapter(valuationSelectGridAdapter);

            }
        }*/

        //意见和建议
        int commentIndex = fieldNameList.indexOf(context.getString(R.string.valuation_comment_reslut));
        if(commentIndex >= 0) {

            String valuation = fieldContentList.get(commentIndex);
            TextView other_suggestion = (TextView) rootView.findViewById(R.id.maintenance_order_valuation_result_other_suggestion);
            if (TextUtils.isEmpty(valuation)) {
                other_suggestion.setVisibility(View.VISIBLE);
                other_suggestion.setText(context.getString(R.string.service_valuation_no_content));
            } else {
                other_suggestion.setVisibility(View.VISIBLE);
                other_suggestion.setText(valuation);
            }
        }




    }

    public View getView(){
        return rootView;
    }
}
