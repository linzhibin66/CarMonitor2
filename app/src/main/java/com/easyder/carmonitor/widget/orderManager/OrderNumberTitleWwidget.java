package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.easyder.carmonitor.R;

/**
 * Created by ljn on 2018-01-12.
 */

public class OrderNumberTitleWwidget {

    private Context context;

    private View rootView;

    public OrderNumberTitleWwidget(Context context,String orderNumber) {
        this.context = context;
        rootView = View.inflate(context, R.layout.ordernumber_title_textview, null);
        ((TextView)rootView).setText(context.getString(R.string.create_maintenance_order_ordernumber,orderNumber));
    }

    public View getView(){
        return rootView;
    }


}
