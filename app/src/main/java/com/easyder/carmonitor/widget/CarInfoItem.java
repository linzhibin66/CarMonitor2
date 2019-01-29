package com.easyder.carmonitor.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;

/**
 * Created by ljn on 2017-03-31.
 */
public class CarInfoItem extends RelativeLayout {

    private TextView item_values;

    private OnClickListener listener;

    private View lineView;

    private ImageView arrows;

   /* public CarInfoItem(Context context) {
        super(context);
          initView(context);
    }
*/
    public CarInfoItem(Context context, AttributeSet attrs) {
        super(context, attrs);
//        initParams(context,attrs);
        initView(context, attrs);
    }

    public CarInfoItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        initParams(context,attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs){

        View view = LayoutInflater.from(context).inflate(R.layout.carinfo_item, this, true);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onClick(v);
                }
            }
        });
        ImageView icon = (ImageView) findViewById(R.id.carinfo_icon);
        arrows = (ImageView) findViewById(R.id.arrows);
        lineView = findViewById(R.id.carinfo_item_line);
        TextView item_attribute = (TextView) findViewById(R.id.item_attribute);
        item_values = (TextView) findViewById(R.id.item_values);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.carinfo_item);
        boolean hasArrow = ta.getBoolean(R.styleable.carinfo_item_carinfo_has_arrows, false);
        boolean hasLine = ta.getBoolean(R.styleable.carinfo_item_carinfo_has_line, true);
        int carinfo_attribute = ta.getResourceId(R.styleable.carinfo_item_carinfo_attribute, -1);
        int item_icon = ta.getResourceId(R.styleable.carinfo_item_carinfo_icon, -1);
        ta.recycle();

        if(item_icon != -1){
            icon.setImageResource(item_icon);
        }

        if(carinfo_attribute != -1){
            item_attribute.setText(carinfo_attribute);
        }

        if(hasLine){
            lineView.setVisibility(VISIBLE);
        }else{
            lineView.setVisibility(GONE);
        }

        if(hasArrow){
            arrows.setVisibility(VISIBLE);
            item_values.setVisibility(GONE);
        }else{
            arrows.setVisibility(GONE);
            item_values.setVisibility(VISIBLE);
        }
    }



   /* private void initParams(Context context, AttributeSet attrs){

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.carinfo_item);
        boolean hasArrow = ta.getBoolean(R.styleable.carinfo_item_carinfo_has_arrows, false);
        int carinfo_attribute = ta.getResourceId(R.styleable.carinfo_item_carinfo_attribute, -1);
        int item_icon = ta.getResourceId(R.styleable.carinfo_item_carinfo_icon, -1);
        ta.recycle();

           *//*hasArrow = attrs.getAttributeBooleanValue(R.styleable.carinfo_item_carinfo_has_arrows, false);
            carinfo_attribute = attrs.getAttributeResourceValue(R.styleable.carinfo_item_carinfo_attribute, -1);
            item_icon = attrs.getAttributeResourceValue(R.styleable.carinfo_item_carinfo_icon, -1);*//*
    }*/

  /*  public void setIcon(int resource) {
        icon.setImageResource(resource);
    }

    public void showArrows() {
        arrows.setVisibility(VISIBLE);
    }

    public String geAtttribute() {

        return item_attribute.getText().toString();
    }

    public void setAattribute(String attribute) {

        item_attribute.setText(attribute);

    }

    public String getValues() {
        return item_values.getText().toString();
    }*/

    public void setValues(String values) {
        item_values.setText(values);
    }

    public void setValues(SpannableStringBuilder style) {
        item_values.setText(style);
    }



    public String getValues(){
        return item_values.getText().toString().trim();
    }

    public void showArrow(boolean isShow){
        if(isShow){
            arrows.setVisibility(VISIBLE);
        }else{
            arrows.setVisibility(GONE);
        }
    }

    public void setClickListener(OnClickListener listener){
        this.listener = listener;
    }

    public void setLine(boolean isShow){
        if(isShow){
            lineView.setVisibility(VISIBLE);
        }else{
            lineView.setVisibility(GONE);
        }
    }
}