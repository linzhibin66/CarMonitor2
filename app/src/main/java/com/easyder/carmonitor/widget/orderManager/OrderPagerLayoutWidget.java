package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Scheme.OrderInfoScheme.BaseOrderInfoItem;
import com.easyder.carmonitor.adapter.MaintenanceOrderListAdapter;
import com.easyder.carmonitor.interfaces.CreatOrderSearchListener;
import com.easyder.carmonitor.presenter.MaintenanceOrderActivityPresenter;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.CreateMaintenanceInfoDB;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.easyder.carmonitor.interfaces.OrderInfoShowContentInterfaces;
import com.shinetech.mvp.utils.ToastUtil;

import java.util.List;

/**
 * Created by Lenovo on 2017-11-10.
 */

public class OrderPagerLayoutWidget extends BaseOrderInfoItem {


    private Context context;

    private View rootView;

    private ListView orderitem_listview;

    private RelativeLayout nodata_layout;

    private TextView maintenance_order_add_button;

    private LinearLayout search_order_by_platenumber_layout;

    private ImageButton search_order_button;

    private EditText search_content_edt;

    private TextView cancle_search;

    private List<BaseOrderInfoDB> orderDataList;

    private MaintenanceOrderListAdapter maintenanceOrderListAdapter;

    private OrderInfoShowContentInterfaces orderInterfaces;

    private MaintenanceOrderNoDataWidget mMaintenanceOrderNoDataWidget;

    private boolean showCreatButton;

    private int maintenanceType = OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE;

    private boolean iFistfoused = true;

    private boolean isRefreshList = true;

    private CreatOrderSearchListener mCreatOrderSearchListener;

    private boolean isSelected = false;

    private MaintenanceOrderActivityPresenter presenter;

    public OrderPagerLayoutWidget(Context context, MaintenanceOrderActivityPresenter presenter, int maintenanceType, boolean showCreatButton, List<BaseOrderInfoDB> orderDataList, OrderInfoShowContentInterfaces orderInterfaces) {
        this.context = context;

        this.maintenanceType = maintenanceType;

        this.presenter = presenter;

        rootView = View.inflate(context, R.layout.order_item_layout, null);

        initView();

        update(showCreatButton, orderDataList);

        setOrderListener(orderInterfaces);

    }

    public void setOrderListener(OrderInfoShowContentInterfaces orderInterfaces){
        this.orderInterfaces = orderInterfaces;

        if(maintenanceOrderListAdapter != null) {
            maintenanceOrderListAdapter.setEvlauateListener(this.orderInterfaces);
        }
    }

    @Override
    public View getView() {
        return rootView;
    }

    public void initSearchLayout(){
        isRefreshList = false;
        if(search_content_edt != null) {
            search_content_edt.setText("");
            cancle_search.setVisibility(View.GONE);
            search_order_button.setVisibility(View.VISIBLE);
        }
        hideInput(rootView);
    }

    private void initView(){
        orderitem_listview = (ListView) rootView.findViewById(R.id.orderitem_listview);
        nodata_layout = (RelativeLayout) rootView.findViewById(R.id.nodata_layout);
        maintenance_order_add_button = (TextView) rootView.findViewById(R.id.maintenance_order_add_button);

        //搜索布局
        search_order_by_platenumber_layout = (LinearLayout) rootView.findViewById(R.id.search_order_by_platenumber_layout);

        if(maintenanceType == OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE) {
            search_order_by_platenumber_layout.setVisibility(View.VISIBLE);
            search_order_button = (ImageButton) rootView.findViewById(R.id.search_order_button);
            search_content_edt = (EditText) rootView.findViewById(R.id.search_content_edt);
            cancle_search = (TextView) rootView.findViewById(R.id.cancle_search);

            cancle_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isRefreshList = false;
                    search_content_edt.setText("");
                    hideInput(rootView);
                    if(orderInterfaces != null){
                        orderInterfaces.dismissHintLayout();
                    }
                    cancle_search.setVisibility(View.GONE);
                    search_order_button.setVisibility(View.VISIBLE);

                }
            });

            mCreatOrderSearchListener = new CreatOrderSearchListener() {
                @Override
                public void onSelected(String plateNumber) {
                    isRefreshList = false;
                    search_content_edt.setText(plateNumber);
                    isSelected = true;
                    if(orderInterfaces != null){
                        orderInterfaces.dismissHintLayout();
                    }
                    cancle_search.setVisibility(View.GONE);
                    search_order_button.setVisibility(View.VISIBLE);
                }

                @Override
                public void onBack() {
                    if(orderInterfaces != null){
                        orderInterfaces.dismissHintLayout();
                    }
                }
            };
            search_content_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        if(iFistfoused){
                            iFistfoused = false;
                            return;
                        }

                        if(orderInterfaces != null){
                            orderInterfaces.showSearchPlateNumberLayout(null, mCreatOrderSearchListener);
                            cancle_search.setVisibility(View.VISIBLE);
                            search_order_button.setVisibility(View.GONE);
                        }

                    }
                }
            });

            search_content_edt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(orderInterfaces != null){
                        orderInterfaces.showSearchPlateNumberLayout(null, mCreatOrderSearchListener);
                        cancle_search.setVisibility(View.VISIBLE);
                        search_order_button.setVisibility(View.GONE);
                    }
                }
            });

            search_content_edt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(orderInterfaces != null && isRefreshList){
                            isSelected = true;
                            orderInterfaces.showSearchPlateNumberLayout(s.toString(), mCreatOrderSearchListener);
                            cancle_search.setVisibility(View.VISIBLE);
                            search_order_button.setVisibility(View.GONE);
                        }else{
                            isRefreshList = true;
                        }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            search_order_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        String search_content = search_content_edt.getText().toString().trim();
                        if(!TextUtils.isEmpty(search_content)){
                            // TODO load Data
//                            ToastUtil.showShort(search_content);
                            String[] split = search_content.split(" ");

                            if(split.length == 2) {
                                search_content = split[1];
                            }

                            presenter.selectMaintenanceOrder(search_content);

                            isRefreshList = false;
                            search_content_edt.setText("");
                        }
                }
            });
        }else{
            search_order_by_platenumber_layout.setVisibility(View.GONE);
        }

    }

    /**
     * 关闭输入法
     *
     * @param view
     */
    public void hideInput(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void update(boolean showCreatButton, List<BaseOrderInfoDB> orderDataList){

        this.orderDataList = orderDataList;
        this.showCreatButton = showCreatButton;

        if(orderDataList == null || orderDataList.size()==0){
            nodata_layout.setVisibility(View.VISIBLE);
            orderitem_listview.setVisibility(View.GONE);
            maintenance_order_add_button.setVisibility(View.GONE);
            initNodata();
            return;
        }else{
            nodata_layout.setVisibility(View.GONE);
            orderitem_listview.setVisibility(View.VISIBLE);
            if(maintenanceType == OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE) {
                maintenance_order_add_button.setVisibility(View.VISIBLE);
                maintenance_order_add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (orderInterfaces != null) {
                            orderInterfaces.ShowCreatMaintenanceOrder(null);
                        }
                    }
                });
            }
            initData();
        }
    }

    private void initNodata(){
        //TODO show Nodata Layout and init Add MaintenanceOrder click event
//        System.out.println("initNodata - - - - ");
        if(mMaintenanceOrderNoDataWidget == null) {
            mMaintenanceOrderNoDataWidget = new MaintenanceOrderNoDataWidget(context);
            mMaintenanceOrderNoDataWidget.setHintText(context.getString(R.string.maintenance_order_item_nodata));
            nodata_layout.addView(mMaintenanceOrderNoDataWidget.getView());
        }

        if(showCreatButton){
            mMaintenanceOrderNoDataWidget.showButton(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(orderInterfaces != null){
                        orderInterfaces.ShowCreatMaintenanceOrder(null);
                    }
                }
            });
        }else{
            mMaintenanceOrderNoDataWidget.dismissButton();
        }

    }

    private void initData(){

        if(maintenanceOrderListAdapter == null) {

            maintenanceOrderListAdapter = new MaintenanceOrderListAdapter(context, maintenanceType);

            maintenanceOrderListAdapter.initData(orderDataList);

            maintenanceOrderListAdapter.setEvlauateListener(orderInterfaces);

            orderitem_listview.setAdapter(maintenanceOrderListAdapter);

            orderitem_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    BaseOrderInfoDB item = (BaseOrderInfoDB) maintenanceOrderListAdapter.getItem(position);
                    // TODO show info Layout
                    if(item.getDataInfoId() <= 0) {
                        if (orderInterfaces != null) {
                            orderInterfaces.showMaintenanceOrderInfo(item);
                        }
                    }else{
                        if (orderInterfaces != null) {
                            orderInterfaces.ShowCreatMaintenanceOrder(item.getData().getOrderNumber());
                        }
                    }
                }
            });

            orderitem_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    BaseOrderInfoDB item = (BaseOrderInfoDB) maintenanceOrderListAdapter.getItem(position);
                    byte orderStatus = item.getOrderStatus();
                    if(orderStatus == 1 || (item.getDataInfoId() > -1)){
                        orderInterfaces.deleteOrder(item);
                        return true;
                    }

                    return false;
                }
            });

        }else{
            maintenanceOrderListAdapter.upData(orderDataList);
        }
    }

    public void initSearchFoused(){
        iFistfoused = true;
    }

}
