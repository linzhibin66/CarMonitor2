package com.easyder.carmonitor.widget.carCost;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.easyder.carmonitor.R;

/**
 * Created by ljn on 2017-12-12.
 */

public class CarCostItemWidget {

    private Context context;

    private View item;

    private TextView title;

    private TextView value;

    private View line;

    public CarCostItemWidget(Context context) {
        this.context = context;

        item = View.inflate(context, R.layout.carcost_item, null);

        initView();
    }

    private void initView(){
        title = (TextView) item.findViewById(R.id.carcost_baseitem_title);
        value = (TextView) item.findViewById(R.id.carcost_baseitem_value);
        line = item.findViewById(R.id.carcost_baseitem_bottom_line);
    }

    public void setTitle(String strTitle){
        if(title != null) {
            title.setText(strTitle);
        }
    }
    public void setValue(String strValue){
        if(value != null) {
            value.setText(strValue);
        }
    }

    public void setValueColor(int resourcesColor){
        if(value != null){
            value.setTextColor(context.getResources().getColor(resourcesColor));
        }
    }

    public void setLine(boolean isShow){

        if(isShow){
            line.setVisibility(View.VISIBLE);
        }else{
            line.setVisibility(View.GONE);
        }

    }

    public View getView(){
        return item;
    }
}
