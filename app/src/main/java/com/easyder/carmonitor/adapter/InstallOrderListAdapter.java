package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.OrderInfoShowContentInterfaces;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;

import java.util.List;

/**
 * Created by ljn on 2017-11-30.
 */

public class InstallOrderListAdapter extends BaseAdapter {

    private List<InstallOrderBaseInfo> dataList;

    private Context context;

    private OrderInfoShowContentInterfaces listener;


    public InstallOrderListAdapter(Context context) {
        this.context = context;
    }

    public void initData(List<InstallOrderBaseInfo> dataList){
        this.dataList = dataList;

    }

    public void upData(List<InstallOrderBaseInfo> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();

    }

    public void setInstallListener(OrderInfoShowContentInterfaces listener){
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
        InstallViewHolder mHolder;
        if(convertView == null) {
            mHolder = new InstallViewHolder();
            convertView = View.inflate(context, R.layout.install_order_list_item, null);
            mHolder.order_list_item_ordercontacts = (TextView) convertView.findViewById(R.id.order_list_item_ordercontacts);
            mHolder.order_list_item_installstatus = (TextView) convertView.findViewById(R.id.order_list_item_installstatus);
            mHolder.order_list_item_installcount = (TextView) convertView.findViewById(R.id.order_list_item_installcount);
            mHolder.order_list_item_finishtiem = (TextView) convertView.findViewById(R.id.order_list_item_finishtiem);
            mHolder.order_list_item_installlocation = (TextView) convertView.findViewById(R.id.order_list_item_installlocation);

            convertView.setTag(mHolder);
        }else{
            mHolder = (InstallViewHolder) convertView.getTag();
        }


        InstallOrderBaseInfo installOrderBaseInfo = dataList.get(position);

        if(installOrderBaseInfo == null){
            return convertView;
        }

        mHolder.order_list_item_ordercontacts.setText(installOrderBaseInfo.getContacts());

        mHolder.order_list_item_installcount.setText(installOrderBaseInfo.getInstallCount() + context.getString(R.string.unit_tai));
        mHolder.order_list_item_finishtiem.setText(installOrderBaseInfo.getFinishTime());
        mHolder.order_list_item_installlocation.setText(installOrderBaseInfo.getInstallLocation());

//        initStatusCtrlView(installOrderBaseInfo, mHolder);
        initInstallStatus(installOrderBaseInfo, mHolder);

        return convertView;
    }

    private void initInstallStatus(InstallOrderBaseInfo installOrderBaseInfo, InstallViewHolder mHolder){
        int status = installOrderBaseInfo.getOrderStatus();
        String  maintenanceOrderStaus;
        int color;
        switch (status){
            default:
            case  InstallOrderBaseInfo.WAITING_ORDER_RECEIVING_STATUS:
                maintenanceOrderStaus = context.getString(R.string.maintenance_order_status_wait_accept_order);
                color = context.getResources().getColor(R.color.maintenance_evaluate_text_color);
                break;
            case  InstallOrderBaseInfo.ORDER_RECEIVING_STATUS:
                maintenanceOrderStaus = context.getString(R.string.maintenance_order_status_have_accept_order);
                color = context.getResources().getColor(R.color.maintenance_evaluate_text_color);
                break;
            case  InstallOrderBaseInfo.FINISH_STATUS:
                maintenanceOrderStaus = context.getString(R.string.maintenance_order_status_finish_order);
                color = context.getResources().getColor(R.color.order_status_textcolor);
                break;

        }

        mHolder.order_list_item_installstatus.setText(maintenanceOrderStaus);
        mHolder.order_list_item_installstatus.setTextColor(color);
    }

    private void initStatusCtrlView(final InstallOrderBaseInfo installOrderBaseInfo, InstallViewHolder mHolder){

       /* if(installOrderBaseInfo.getOrderStatus() == InstallOrderBaseInfo.FINISH_STATUS ) {
            mHolder.install_order_status_type.setImageResource(R.mipmap.install_order_type_end);
            mHolder.install_order_status_ctrl.setVisibility(View.GONE);
        }else{
            mHolder.install_order_status_type.setImageResource(R.mipmap.install_order_type);
            mHolder.install_order_status_ctrl.setVisibility(View.VISIBLE);
            if(installOrderBaseInfo.getOrderStatus() == InstallOrderBaseInfo.WAITING_ORDER_RECEIVING_STATUS){
                mHolder.install_order_status_ctrl.setText(context.getString(R.string.accept_order));
                mHolder.install_order_status_ctrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener != null){
                            listener.acceptInstallOrder(installOrderBaseInfo);
                        }
                    }
                });
            }else{
                mHolder.install_order_status_ctrl.setText(context.getString(R.string.have_maintenance));
                mHolder.install_order_status_ctrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener != null){
                            listener.upLoadinstallResult(installOrderBaseInfo);
                        }
                    }
                });
            }
        }*/
    }

    class InstallViewHolder{

        public TextView order_list_item_ordercontacts;
        public TextView order_list_item_installcount;
        public TextView order_list_item_installstatus;
        public TextView order_list_item_finishtiem;
        public TextView order_list_item_installlocation;
       /* public TextView install_order_status_ctrl;
        public ImageView install_order_status_type;*/
    }
}
