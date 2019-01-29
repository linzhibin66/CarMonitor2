package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.bean.orderBean.SelectOrderByNumberVo;

import java.util.List;

/**
 * Created by ljn on 2017-11-29.
 */

public class MaintenanceResultInfoWidget {

    private Context context;

    private View rootView;

    public MaintenanceResultInfoWidget(Context context,  SelectOrderByNumberVo orderinfo) {
        this.context = context;
        rootView = View.inflate(context, R.layout.maintenance_result_info_item, null);

        initLayout(orderinfo);
    }

    private void initLayout(SelectOrderByNumberVo orderinfo){

        List<String> fieldNameList = null;

        List<String> fieldContentList = null;

        DecodeUDPDataTool.OrderContentListItemData orderContentListItemData = DecodeUDPDataTool.decodeItemByDataName(orderinfo.getContentList(),
                context.getString(R.string.order_content_maintenance_result));

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

//      故障分析
        int analyzeIndex = fieldNameList.indexOf(context.getString(R.string.upload_maintenance_result_analyze));
        if(analyzeIndex >= 0) {

            String analyzeValue = fieldContentList.get(analyzeIndex);

            TextView analyze_value = (TextView) rootView.findViewById(R.id.maintenance_order_result_analyze_value);

            analyze_value.setText(analyzeValue);
        }

//       维修措施
        int measuresIndex = fieldNameList.indexOf(context.getString(R.string.upload_maintenance_result_measures));
        if(measuresIndex >= 0) {

            String measuresValue = fieldContentList.get(measuresIndex);

            TextView measures_value = (TextView) rootView.findViewById(R.id.maintenance_result_measures_value);

            measures_value.setText(measuresValue);
        }



//      维修日期
        int timeIndex = fieldNameList.indexOf(context.getString(R.string.upload_maintenance_time));
        if(timeIndex >= 0) {

            String timeValue = fieldContentList.get(timeIndex);

            TextView tiem_value = (TextView) rootView.findViewById(R.id.maintenance_time_value);

            tiem_value.setText(timeValue);
        }

        //设备状态
        int termainStatusIndex = fieldNameList.indexOf(context.getString(R.string.upload_maintenance_result_termain_status));
        if(termainStatusIndex >= 0) {

            String termainStatusValueStr = fieldContentList.get(termainStatusIndex);

            int termainStatusValue = 1;
            try {
                termainStatusValue = Integer.parseInt(termainStatusValueStr);
            } catch (Exception e) {
                e.printStackTrace();
            }

            TextView termain_status_value = (TextView) rootView.findViewById(R.id.maintenance_result_termain_status_value);
            termain_status_value.setText(termainStatusValue == 1 ? context.getString(R.string.normal_str) : context.getString(R.string.fault_str));

        }
    }

    public View getView(){
        return rootView;
    }


}
