package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;

/**
 * Created by ljn on 2017-11-23.
 */

public class MaintenanceOrderNoDataWidget {

    private Context context;

    private View rootView;

    public MaintenanceOrderNoDataWidget(Context context) {
        this.context = context;

        rootView = View.inflate(context, R.layout.maintenance_order_nodata, null);

    }

    public void showButton(View.OnClickListener listener){
        RelativeLayout nodata_creat_order = (RelativeLayout) rootView.findViewById(R.id.nodata_creat_order);
        nodata_creat_order.setVisibility(View.VISIBLE);
        nodata_creat_order.setOnClickListener(listener);
    }

    public void dismissButton(){
        RelativeLayout nodata_creat_order = (RelativeLayout) rootView.findViewById(R.id.nodata_creat_order);
        nodata_creat_order.setVisibility(View.GONE);
    }

    public void setButtonText(String textContent){
        TextView creat_order_text = (TextView) rootView.findViewById(R.id.creat_order_text);
        creat_order_text.setText(textContent);
    }

    public void setHintText(String hintText){
        TextView order_item_nodata_hint = (TextView) rootView.findViewById(R.id.order_item_nodata_hint);
        order_item_nodata_hint.setText(hintText);

    }

    public View getView(){
        return rootView;
    }
}
