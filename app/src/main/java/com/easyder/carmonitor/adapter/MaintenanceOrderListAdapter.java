package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.widget.orderManager.OrderInfoWidget;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.easyder.carmonitor.interfaces.OrderInfoShowContentInterfaces;

import java.util.List;

/**
 * Created by ljn on 2017-11-10.
 */

public class MaintenanceOrderListAdapter extends BaseAdapter {

    private List<BaseOrderInfoDB> dataList;

    private Context context;

    private OrderInfoShowContentInterfaces listener;

    private int maintenanceType = OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE;

    public MaintenanceOrderListAdapter(Context context, int maintenanceType) {
        this.context = context;
        this.maintenanceType = maintenanceType;
    }

    public void initData(List<BaseOrderInfoDB> dataList){
        this.dataList = dataList;

    }

    public void upData(List<BaseOrderInfoDB> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();

    }

    public void setEvlauateListener(OrderInfoShowContentInterfaces listener){
        this.listener = listener;
    }

    @Override
    public int getCount() {

        if(dataList == null){
            return 0;
        }

        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        if(dataList == null || ((dataList.size()-1)<position)){
            return null;
        }
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MaintenanceViewHolder mHolder;
        if(convertView == null) {
            mHolder = new MaintenanceViewHolder();
            convertView = View.inflate(context, R.layout.maintenance_order_list_item, null);
            mHolder.order_list_item_status = (TextView) convertView.findViewById(R.id.order_list_item_status);
            mHolder.plateNumber = (TextView) convertView.findViewById(R.id.order_list_item_platenumber);
            mHolder.appointmentTime = (TextView) convertView.findViewById(R.id.order_list_item_time);
            mHolder.appointmentLocation = (TextView) convertView.findViewById(R.id.order_list_item_addres);
            mHolder.order_type = (TextView) convertView.findViewById(R.id.order_type);
//            mHolder.maintenance_evaluate_endtiem = (TextView) convertView.findViewById(R.id.maintenance_evaluate_endtiem);
            mHolder.maintenance_evaluate_ctrl = (TextView) convertView.findViewById(R.id.maintenance_evaluate_ctrl);
//            mHolder.maintenance_evaluate_layout = (RelativeLayout) convertView.findViewById(R.id.maintenance_evaluate_layout);

           /* mHolder.maintenance_order_status_ctrl = (TextView) convertView.findViewById(R.id.maintenance_order_status_ctrl);
            mHolder.maintenance_order_status_type = (ImageView) convertView.findViewById(R.id.maintenance_order_status_type);
            mHolder.maintenance_order_status_ctrl_layout = (RelativeLayout) convertView.findViewById(R.id.maintenance_order_status_ctrl_layout);*/

            convertView.setTag(mHolder);
        }else{
            mHolder = (MaintenanceViewHolder) convertView.getTag();
        }


        final BaseOrderInfoDB baseOrderInfoDB = dataList.get(position);

        if(baseOrderInfoDB == null){
            return convertView;
        }

        mHolder.plateNumber.setText(baseOrderInfoDB.getOrderNumber());
        mHolder.appointmentTime.setText(baseOrderInfoDB.getClienteleName());
        mHolder.appointmentLocation.setText(baseOrderInfoDB.getClientelePhone());

        initOrderStatus(baseOrderInfoDB, mHolder);

//        initEvaluateView(baseOrderInfoDB, mHolder);

//        initStatusCtrlView(maintenanceOrderInfoBean, mHolder);

        return convertView;
    }

    private void initOrderStatus(BaseOrderInfoDB baseOrderInfoDB, MaintenanceViewHolder mHolder){

        int status = baseOrderInfoDB.getOrderStatus();
        String  maintenanceOrderStaus;
        int color;
        switch (status){
            default:
            case  0:
                if(baseOrderInfoDB.getDataInfoId() >=0){
                    maintenanceOrderStaus = context.getString(R.string.base_order_status_nocommit);

                    color = context.getResources().getColor(R.color.maintenance_evaluate_text_color);
                }else{
                    maintenanceOrderStaus = context.getString(R.string.base_order_status_process);
                    color = context.getResources().getColor(R.color.maintenance_evaluate_text_color);
                }
                break;
            case  1:
                maintenanceOrderStaus = context.getString(R.string.base_order_status_end);
                color = context.getResources().getColor(R.color.order_status_textcolor);
                break;
        }

        mHolder.order_list_item_status.setText(maintenanceOrderStaus);
        mHolder.order_list_item_status.setTextColor(color);

        if(baseOrderInfoDB.getOrderName().equals(context.getString(R.string.order_name_maintenance))){
            mHolder.order_type.setText(context.getString(R.string.order_type_maintenance));
            mHolder.order_type.setBackgroundResource(R.drawable.order_list_item_orderstatus_maintenance_bg);
        }else if(baseOrderInfoDB.getOrderName().equals(context.getString(R.string.order_name_install))){
            mHolder.order_type.setText(context.getString(R.string.order_type_install));
            mHolder.order_type.setBackgroundResource(R.drawable.order_list_item_orderstatus_install_bg);
        }else{
            mHolder.order_type.setText(context.getString(R.string.unknown));
            mHolder.order_type.setBackgroundResource(R.drawable.order_list_item_orderstatus_maintenance_bg);
        }

    }


    private void initEvaluateView(final BaseOrderInfoDB baseOrderInfoDB, MaintenanceViewHolder mHolder){

        if(maintenanceType == OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE){
            mHolder.maintenance_evaluate_ctrl.setVisibility(View.VISIBLE);
//            mHolder.maintenance_evaluate_endtiem.setText(context.getString(R.string.maintenance_order_evaluate_endtiem) + maintenanceOrderInfoBean.getEndTime());
            if(baseOrderInfoDB.getOrderStatus() == 1) {

                mHolder.maintenance_evaluate_ctrl.setText(context.getString(R.string.maintenance_order_delete_order));
                mHolder.maintenance_evaluate_ctrl.setTextColor(context.getResources().getColor(R.color.maintenance_look_evaluate_text_color));
                mHolder.maintenance_evaluate_ctrl.setBackgroundResource(R.drawable.maintenance_look_evaluate_bg);
                mHolder.maintenance_evaluate_ctrl.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.icon_delete_order), null, null, null);
                mHolder.maintenance_evaluate_ctrl.setClickable(true);
                mHolder.maintenance_evaluate_ctrl.setFocusable(true);
                mHolder.maintenance_evaluate_ctrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.deleteOrder(baseOrderInfoDB);
                        }
                    }
                });
            }else{
                mHolder.maintenance_evaluate_ctrl.setVisibility(View.GONE);
            }
        }else{
            mHolder.maintenance_evaluate_ctrl.setVisibility(View.GONE);
        }
    }

    private void initStatusCtrlView(final MaintenanceOrderInfoBean maintenanceOrderInfoBean, MaintenanceViewHolder mHolder){
/*
        if(maintenanceType == OrderInfoWidget.MAINTENANCE_ORDER_TYPE){
            mHolder.maintenance_order_status_ctrl_layout.setVisibility(View.VISIBLE);
            if(maintenanceOrderInfoBean.getStatus() == MaintenanceOrderInfoBean.FINISH_STATUS ) {
                mHolder.maintenance_order_status_type.setImageResource(R.mipmap.maintenance_order_type_end);
                mHolder.maintenance_order_status_ctrl.setVisibility(View.GONE);
            }else{
                mHolder.maintenance_order_status_type.setImageResource(R.mipmap.maintenance_order_type);
                mHolder.maintenance_order_status_ctrl.setVisibility(View.VISIBLE);
                if(maintenanceOrderInfoBean.getStatus() == MaintenanceOrderInfoBean.COMMIT_STATUS){
                    mHolder.maintenance_order_status_ctrl.setText(context.getString(R.string.accept_order));
                    mHolder.maintenance_order_status_ctrl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(listener != null){
                                listener.acceptOrder(maintenanceOrderInfoBean);
                            }
                        }
                    });
                }else{
                    mHolder.maintenance_order_status_ctrl.setText(context.getString(R.string.have_maintenance));
                    mHolder.maintenance_order_status_ctrl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(listener != null){
                                listener.upLoadMaintenanceResult(maintenanceOrderInfoBean);
                            }
                        }
                    });
                }
            }

        }else{
            mHolder.maintenance_order_status_ctrl_layout.setVisibility(View.GONE);
        }
        */
    }



    class MaintenanceViewHolder{
        public TextView order_list_item_status;
        public TextView plateNumber;
        public TextView appointmentTime;
        public TextView appointmentLocation;
        public TextView order_type;
//        public RelativeLayout maintenance_evaluate_layout;
//        public TextView maintenance_evaluate_endtiem;
        public TextView maintenance_evaluate_ctrl;

        /*public RelativeLayout maintenance_order_status_ctrl_layout;
        public ImageView maintenance_order_status_type;
        public TextView maintenance_order_status_ctrl;*/
    }
}
