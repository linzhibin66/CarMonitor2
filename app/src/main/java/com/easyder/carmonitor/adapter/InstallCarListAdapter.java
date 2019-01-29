package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.InstallOrderUtil;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;
import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-12-06.
 */

public class InstallCarListAdapter extends BaseAdapter {

    private List<InstallTerminalnfo> mData = new ArrayList<>();

    private Context context;

    private int orderstatus;

    public InstallCarListAdapter(int orderstatus, Context context) {
        this.context = context;
        this.orderstatus = orderstatus;
    }

    public void updata(List<InstallTerminalnfo> data){
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(mData != null) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(mData != null && mData.size()>position){
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        InstallViewHolder mInstallViewHolder;
        if(convertView != null){
            mInstallViewHolder = (InstallViewHolder) convertView.getTag();
        }else{
            mInstallViewHolder = new InstallViewHolder();

            convertView = View.inflate(context, R.layout.install_order_carlist_item, null);

            mInstallViewHolder.plateNumber = (TextView) convertView.findViewById(R.id.carlist_platenumber);
            mInstallViewHolder.carInfoStatus = (TextView) convertView.findViewById(R.id.carlist_status);

            convertView.setTag(mInstallViewHolder);
        }


        InstallTerminalnfo installTerminalnfo = mData.get(position);

        mInstallViewHolder.plateNumber.setText(InstallOrderUtil.getTitle(installTerminalnfo));

        if(orderstatus == InstallOrderBaseInfo.ORDER_RECEIVING_STATUS) {
            mInstallViewHolder.carInfoStatus.setVisibility(View.VISIBLE);
            if (installTerminalnfo.isIntegrityInfo()) {
                mInstallViewHolder.carInfoStatus.setText(context.getString(R.string.install_order_car_list_item_completed));
                mInstallViewHolder.carInfoStatus.setTextColor(context.getResources().getColor(R.color.install_carlist_status_finish_text_color));
            } else {
                mInstallViewHolder.carInfoStatus.setTextColor(context.getResources().getColor(R.color.install_carlist_status_text_color));
                mInstallViewHolder.carInfoStatus.setText(context.getString(R.string.install_order_car_list_item_no_completed));
            }
        }else{
            mInstallViewHolder.carInfoStatus.setVisibility(View.GONE);
        }

        return convertView;
    }

    class InstallViewHolder{
        TextView plateNumber;
        TextView carInfoStatus;
    }
}
