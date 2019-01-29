package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.bean.orderBean.SelectOrderByNumberVo;

import java.util.List;

/**
 * Created by ljn on 2017-11-17.
 */

public class MaintenanceOrderProposerInfoWidget {

    private Context context;

    private View rootView;

    private final String installOrderType;
    private final String maintenanceOrderType;

    public MaintenanceOrderProposerInfoWidget(Context context, SelectOrderByNumberVo orderinfo) {
        this.context = context;

        rootView = View.inflate(context, R.layout.base_order_info_layout,null);
        installOrderType = context.getString(R.string.install_order_type);
        maintenanceOrderType = context.getString(R.string.maintenance_order_type);
        initLayout(orderinfo);
    }

    private void initLayout(SelectOrderByNumberVo orderinfo){

        String itemName;
        if(orderinfo.getResultOrderName().equals(context.getString(R.string.order_name_install))) {
            itemName = context.getString(R.string.order_content_install_apply);
        }else{
            itemName = context.getString(R.string.order_content_maintenance_apply);
        }

        DecodeUDPDataTool.OrderContentListItemData orderContentListItemData = DecodeUDPDataTool.decodeItemByDataName(orderinfo.getContentList(),itemName);

        if(orderContentListItemData != null){
            List<String> fieldNameList = orderContentListItemData.getFieldNameList();
            List<List<String>> fieldListContent = orderContentListItemData.getFieldListContent();

            if(fieldListContent != null && fieldListContent.size() == 1) {
                List<String> fieldContentList = fieldListContent.get(0);
                if (fieldContentList.size() == fieldNameList.size()) {
                    for (int i = 0; i < fieldNameList.size(); i++) {
                        String fieldName = fieldNameList.get(i);
                        String fieldContent = fieldContentList.get(i);

                        String parseFieldContent = parseFieldContent(fieldName, fieldContent);

                        View view = initItemView(fieldName, parseFieldContent);
                        ((LinearLayout) rootView).addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                }
            }
        }
    }

    private String parseFieldContent(String fieldName, String fieldContent){

       if(installOrderType.equals(fieldName)){

           int type = 0;

           try {
               type = Integer.parseInt(fieldContent);
           } catch (NumberFormatException e) {
               e.printStackTrace();
           }


           switch (type){
               case 0:
                   return context.getString(R.string.new_install);
               case 1:
                   return context.getString(R.string.change_install);
               case 2:
                   return context.getString(R.string.dismantle_install);
           }

       }else if(maintenanceOrderType.equals(fieldName)){
           int type = 0;

           try {
               type = Integer.parseInt(fieldContent);
           } catch (NumberFormatException e) {
               e.printStackTrace();
           }

           switch (type){
               case 0:
                   return context.getString(R.string.telephone_service);
               case 1:
                   return context.getString(R.string.fault_screening);
               case 2:
                   return context.getString(R.string.self_help_service);
           }
       }

       return fieldContent;

    }

    private View initItemView(String fieldName, String value){
        View itemView = View.inflate(context, R.layout.orderinfo_item_layout, null);
        TextView filed_name = (TextView) itemView.findViewById(R.id.filed_name);
        TextView filed_value = (TextView) itemView.findViewById(R.id.filed_value);
        filed_name.setText(fieldName);
        filed_value.setText(value);
        return itemView;
    }

    public View getView(){
        return rootView;
    }
}
