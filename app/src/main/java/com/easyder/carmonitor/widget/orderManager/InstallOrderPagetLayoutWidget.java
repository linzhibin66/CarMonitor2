package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Scheme.OrderInfoScheme.BaseOrderInfoItem;
import com.easyder.carmonitor.adapter.InstallOrderListAdapter;
import com.easyder.carmonitor.interfaces.OrderInfoShowContentInterfaces;
import com.easyder.carmonitor.widget.orderManager.MaintenanceOrderNoDataWidget;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;

import java.util.List;

/**
 * Created by ljn on 2017-11-30.
 */

public class InstallOrderPagetLayoutWidget extends BaseOrderInfoItem{

    private Context context;

    private View rootView;

    private ListView orderitem_listview;

    private RelativeLayout nodata_layout;

    private List<InstallOrderBaseInfo> mInstallInfoList;

    private OrderInfoShowContentInterfaces orderInterfaces;

    private MaintenanceOrderNoDataWidget mMaintenanceOrderNoDataWidget;

    private InstallOrderListAdapter mInstallOrderListAdapter;

    public InstallOrderPagetLayoutWidget(Context context, List<InstallOrderBaseInfo> mInstallInfoList, OrderInfoShowContentInterfaces orderInterfaces) {
        this.context = context;
        this.mInstallInfoList = mInstallInfoList;
        this.orderInterfaces = orderInterfaces;
        rootView = View.inflate(context, R.layout.order_item_layout, null);

        initView();

        update(mInstallInfoList);

        setOrderListener(orderInterfaces);

    }

    @Override
    public View getView() {
        return rootView;
    }

    private void initView(){
        orderitem_listview = (ListView) rootView.findViewById(R.id.orderitem_listview);
        nodata_layout = (RelativeLayout) rootView.findViewById(R.id.nodata_layout);

    }

    public void update( List<InstallOrderBaseInfo> orderDataList){

        this.mInstallInfoList = orderDataList;

        if(orderDataList == null || orderDataList.size()==0){
            nodata_layout.setVisibility(View.VISIBLE);
            orderitem_listview.setVisibility(View.GONE);
            initNodata();
            return;
        }else{
            nodata_layout.setVisibility(View.GONE);
            orderitem_listview.setVisibility(View.VISIBLE);
            initData();
        }
    }

    private void initNodata(){
        //TODO show Nodata Layout and init Add MaintenanceOrder click event
        if(mMaintenanceOrderNoDataWidget == null) {
            mMaintenanceOrderNoDataWidget = new MaintenanceOrderNoDataWidget(context);
            mMaintenanceOrderNoDataWidget.setHintText(context.getString(R.string.install_order_item_nodata));
            nodata_layout.addView(mMaintenanceOrderNoDataWidget.getView());
        }

        mMaintenanceOrderNoDataWidget.dismissButton();

    }

    private void initData(){
        if(mInstallOrderListAdapter == null) {
            mInstallOrderListAdapter = new InstallOrderListAdapter(context);

            mInstallOrderListAdapter.initData(mInstallInfoList);

            mInstallOrderListAdapter.setInstallListener(orderInterfaces);

            orderitem_listview.setAdapter(mInstallOrderListAdapter);

            orderitem_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    InstallOrderBaseInfo item = (InstallOrderBaseInfo) mInstallOrderListAdapter.getItem(position);

                    if(orderInterfaces != null){
                        orderInterfaces.showInstallOrderInfo(item);
                    }

                }
            });
        }else{
            mInstallOrderListAdapter.upData(mInstallInfoList);
        }
    }


    public void setOrderListener(OrderInfoShowContentInterfaces orderInterfaces) {
        this.orderInterfaces = orderInterfaces;

        if(mInstallOrderListAdapter != null){
            mInstallOrderListAdapter.setInstallListener(orderInterfaces);
        }
    }

}
