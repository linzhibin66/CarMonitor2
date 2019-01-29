package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-11-21.
 */

public class CreatMaintenanceOrderSuccessWidget {

    private LayoutBackListener backlListener;

    private Context context;

    private View rootView;

    private RelativeLayout orderinfo_layout_outmost;

    private RelativeLayout show_order_list_layout;

    private TextView success_hint_ordernumber;

    public CreatMaintenanceOrderSuccessWidget(Context context, View rootView, LayoutBackListener backlListener) {
        this.backlListener = backlListener;
        this.context = context;
        this.rootView = rootView;

        initTitle(this.rootView);

        initLayout();
    }

    private void initTitle(View view){
        ImageButton title_back = (ImageButton) view.findViewById(R.id.title_back);
        TextView title_text = (TextView) view.findViewById(R.id.title_text);
        ImageButton title_search = (ImageButton) view.findViewById(R.id.title_search);

        String title = context.getString(R.string.creat_maintenance_commit_success);
        title_text.setText(title);

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backlListener != null) {
                    backlListener.onBack();
                }
            }
        });

        title_search.setVisibility(View.GONE);

        RelativeLayout title_layout = (RelativeLayout) view.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);
    }

    private void initLayout(){


        success_hint_ordernumber = (TextView) rootView.findViewById(R.id.success_hint_ordernumber);

        show_order_list_layout = (RelativeLayout) rootView.findViewById(R.id.show_order_list_layout);

        //超出内容的模糊阴影布局
        orderinfo_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.orderinfo_layout_outmost);

    }

    public void setOnShowOrderListClickListener(View.OnClickListener listener){
        show_order_list_layout.setOnClickListener(listener);
    }

    public void setOrderNumber(String orderNumber){

//        success_hint_ordernumber.setText(context.getString(R.string.creat_maintenance_commit_success_hint, orderNumber));

//        String trim = success_hint_ordernumber.getText().toString().trim();

        String orderNumberHint = context.getString(R.string.creat_maintenance_commit_success_hint, orderNumber);

        int index = orderNumberHint.indexOf(orderNumber);

        SpannableStringBuilder style=new SpannableStringBuilder(orderNumberHint);

        //数量字体修改颜色
        style.setSpan(new ForegroundColorSpan(0xFFFC3D4F), index, index+orderNumber.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//        style.setSpan(new RelativeSizeSpan(1.3f), index, index+count.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new StyleSpan(Typeface.BOLD), index, index+orderNumber.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        success_hint_ordernumber.setText(style);

    }


    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(orderinfo_layout_outmost != null) {
            orderinfo_layout_outmost.setOnTouchListener(touchListener);
        }
    }


}
