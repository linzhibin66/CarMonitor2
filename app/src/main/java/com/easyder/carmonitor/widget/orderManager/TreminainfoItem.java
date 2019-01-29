package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.easyder.carmonitor.R;

/**
 * Created by ljn on 2017-12-07.
 */

public class TreminainfoItem {

    private Context context;

    private View itemView;

    private TextView terminainfo_title;
    private EditText terminainfo_value;
    private View terminainfo_line;

    public TreminainfoItem(Context context) {
        this.context = context;

        itemView = View.inflate(context, R.layout.terminainfo_item, null);

        initView();
    }

    private TreminainfoItem(Context context, String title, String hint) {
        this.context = context;

        itemView = View.inflate(context, R.layout.terminainfo_item, null);

        initView();

        setTitle(title);
        setValueHint(hint);
    }

    public View getItemView(){
        return itemView;
    }

    private void initView(){
        terminainfo_title = (TextView) itemView.findViewById(R.id.terminainfo_title);
        terminainfo_value = (EditText) itemView.findViewById(R.id.terminainfo_value);
        terminainfo_line = itemView.findViewById(R.id.terminainfo_line);
    }

    public void setTitle(String title){
        if(terminainfo_title != null) {
            terminainfo_title.setText(title);
        }
    }

    public void setValue(String value){
        if(terminainfo_value != null) {
            terminainfo_value.setText(value);
        }
    }

    public void setValueHint(String hint){
        if(terminainfo_value != null) {
            terminainfo_value.setHint(hint);
        }
    }

    public void hindLine(){
        if(terminainfo_line != null) {
            terminainfo_line.setVisibility(View.GONE);
        }
    }

    public String getValue(){
        if(terminainfo_value != null) {
            return terminainfo_value.getText().toString().trim();
        }

        return null;
    }
}
