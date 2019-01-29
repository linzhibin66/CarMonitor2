package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.shinetech.mvp.network.UDP.bean.orderBean.AttachmentItemVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.MaintenanceOrderProgress;
import com.shinetech.mvp.utils.UIUtils;

import java.util.List;

/**
 * Created by ljn on 2018-01-16.
 */

public class OrderProgressWidget {

    private Context context;

    private View rootView;

    private List<MaintenanceOrderProgress> orderProgress;

    private UnderLineLinearLayout content_layout;

    private boolean hasContent = false;

    public OrderProgressWidget(Context context, List<MaintenanceOrderProgress> orderProgress) {
        this.context = context;

        rootView = View.inflate(context, R.layout.order_progress_widget_layout, null);
        this.orderProgress = orderProgress;

        initData();

    }

    private void initData(){

        content_layout = (UnderLineLinearLayout) rootView.findViewById(R.id.order_progress_content_layout);

//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(41));
        for(MaintenanceOrderProgress item :orderProgress){


            if(item.getDisposeResult() == 0){
                addItem(item.getProgressName(),"");
                return;
            }

            addItem(item.getProgressName(),item.getDisposeTime());

        }



    }

    private void addItem(String progressName, String progressTime) {
        View v = LayoutInflater.from(context).inflate(R.layout.order_progress_item_vertical, content_layout, false);
        ((TextView) v.findViewById(R.id.progress_name)).setText(progressName);
        ((TextView) v.findViewById(R.id.progress_time)).setText(progressTime);
        content_layout.addView(v);

//        int childCount = content_layout.getChildCount();

//        System.out.println("addItem  childCount = "+childCount);

    }



    public View getView(){
        return rootView;
    }


}
