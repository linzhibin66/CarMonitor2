package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.shinetech.mvp.network.UDP.bean.orderBean.SelectOrderByNumberVo;

/**
 * Created by ljn on 2017-11-17.
 */

public class MaintenanceOrderInfoFootWidget {

    private Context context;

    private View rootView;

    public MaintenanceOrderInfoFootWidget(Context context, SelectOrderByNumberVo orderinfo) {
        this.context = context;

        rootView = View.inflate(context, R.layout.base_proposer_info_foot,null);

        initView(orderinfo);
    }

    private void initView(SelectOrderByNumberVo orderinfo){

        TextView maintenance_order_number_value = (TextView) rootView.findViewById(R.id.maintenance_order_number_value);
        TextView maintenance_commit_time_value = (TextView) rootView.findViewById(R.id.maintenance_commit_time_value);

        maintenance_order_number_value.setText(orderinfo.getOrderNumber());
//        maintenance_commit_time_value.setText(orderinfo.get());

        TextView maintenance_end_time_value = (TextView) rootView.findViewById(R.id.maintenance_end_time_value);
        TextView maintenance_end_time = (TextView) rootView.findViewById(R.id.maintenance_end_time);
       /* if(maintenanceOrderItem.getLevel() != -1) {
            maintenance_end_time_value.setVisibility(View.VISIBLE);
            maintenance_end_time.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(maintenanceOrderItem.getEndTime())) {
                maintenance_end_time_value.setText(maintenanceOrderItem.getEndTime());
            }
        }*/
    }

    public View getView(){
        return rootView;
    }
}
