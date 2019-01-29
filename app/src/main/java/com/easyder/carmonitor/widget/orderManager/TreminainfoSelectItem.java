package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.easyder.carmonitor.R;

/**
 * Created by ljn on 2017-12-07.
 */

public class TreminainfoSelectItem {

    private Context context;

    private View itemView;

    private TextView terminainfo_select_title;
    private TextView terminainfo_select_value;
    private View terminainfo_select_line;

    public TreminainfoSelectItem(Context context) {
        this.context = context;

        itemView = View.inflate(context, R.layout.terminainfo_item_select, null);

        initView();
    }

    private TreminainfoSelectItem(Context context, String title) {
        this.context = context;

        itemView = View.inflate(context, R.layout.terminainfo_item_select, null);

        initView();
        setTitle(title);
    }

    private void initView(){
        terminainfo_select_title = (TextView) itemView.findViewById(R.id.terminainfo_select_title);
        terminainfo_select_value = (TextView) itemView.findViewById(R.id.terminainfo_select_value);
        terminainfo_select_line = itemView.findViewById(R.id.terminainfo_select_line);
    }

    public View getItemView(){
        return itemView;
    }

    public void setTitle(String title){
        if(terminainfo_select_title != null) {
            terminainfo_select_title.setText(title);
        }
    }

    public void setValue(String value){
        if(terminainfo_select_value != null) {
            terminainfo_select_value.setText(value);
            terminainfo_select_value.setTextColor(context.getResources().getColor(R.color.creat_maintenance_title_text_color));
        }
    }

    public void setNoSelectText(String value){
        if(terminainfo_select_value != null){
            terminainfo_select_value.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            terminainfo_select_value.setText(value);
            terminainfo_select_value.setTextColor(context.getResources().getColor(R.color.creat_maintenance_title_text_color));
        }
    }

    public void hindLine(){
        if(terminainfo_select_line != null) {
            terminainfo_select_line.setVisibility(View.GONE);
        }
    }
}
