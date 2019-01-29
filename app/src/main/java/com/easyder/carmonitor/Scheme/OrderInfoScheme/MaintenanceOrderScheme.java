package com.easyder.carmonitor.Scheme.OrderInfoScheme;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.FiltrateSelectAdapter;
import com.easyder.carmonitor.interfaces.OrderManagerFiltrateListener;
import com.easyder.carmonitor.widget.orderManager.OrderInfoWidget;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.easyder.carmonitor.interfaces.LoadOrderDataListener;
import com.easyder.carmonitor.interfaces.OrderInfoShowContentInterfaces;
import com.easyder.carmonitor.presenter.MaintenanceOrderActivityPresenter;
import com.easyder.carmonitor.widget.orderManager.OrderPagerLayoutWidget;
import com.shinetech.mvp.DB.bean.CreateMaintenanceInfoDB;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.OrderCtrlVo;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.view.MvpView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017-11-10.
 */

public class MaintenanceOrderScheme<M extends BaseVo> extends BaseOrderInfoScheme implements MvpView{


    private MaintenanceOrderActivityPresenter presenter;

    private Context context;

    private OrderInfoShowContentInterfaces orderInterfaces;

    private OrderPagerLayoutWidget maintenanceOrderContentLayoutWidget;

    private List<String> selectOrderStatus = new ArrayList();

    private int maintenanceType = OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE;

    private List<BaseOrderInfoDB> baseOrderInfoList;

    private boolean isdebug = false;

    public MaintenanceOrderScheme(Context context, int orderInfoType, LoadOrderDataListener listener) {
        super(listener);
        this.context = context;
        this.maintenanceType = orderInfoType;
        creatPresenter();

    }

    @Override
    public void setOrderInterfaces(OrderInfoShowContentInterfaces orderInterfaces){
        this.orderInterfaces = orderInterfaces;

        if(maintenanceOrderContentLayoutWidget != null){
            maintenanceOrderContentLayoutWidget.setOrderListener(orderInterfaces);
        }

    }

    @Override
    public View CreatFiltrateView(final OrderManagerFiltrateListener listener) {

        View filtrateLayout = View.inflate(context, R.layout.order_manager_filtrate_listview, null);

        ListView filtrate_listview = (ListView) filtrateLayout.findViewById(R.id.order_manager_filtrate_list);

        TextView filtrate_cancle = (TextView) filtrateLayout.findViewById(R.id.order_manager_filtrate_cancle);

        TextView filtrate_enter = (TextView) filtrateLayout.findViewById(R.id.order_manager_filtrate_enter);

        String[] stringArray = context.getResources().getStringArray(R.array.base_order_status_list);

        final FiltrateSelectAdapter mFiltrateSelectAdapter = new FiltrateSelectAdapter(context, stringArray, selectOrderStatus);

        filtrate_listview.setAdapter(mFiltrateSelectAdapter);

        filtrate_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectItem = (String) mFiltrateSelectAdapter.getItem(position);
                if(selectOrderStatus.contains(selectItem)){
                    selectOrderStatus.remove(selectItem);
                }else{
                    selectOrderStatus.add(selectItem);
                }

                mFiltrateSelectAdapter.updata(selectOrderStatus);
            }
        });

        filtrate_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.Oncancle();
                }
            }
        });

        filtrate_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    int[] status;

                    if(selectOrderStatus.size()>0) {
                        status = new int[selectOrderStatus.size()];
                        int count = 0;

                        String[] stringArray = context.getResources().getStringArray(R.array.base_order_status_list);

                        for (int i = 0; i < stringArray.length; i++) {
                            if (selectOrderStatus.contains(stringArray[i])) {
                                status[count] = i;
                                count++;
                            }
                        }
                    }else{
                        status = new int[]{};
                    }

                    if(maintenanceOrderContentLayoutWidget != null) {

                        String userName = UserInfo.getInstance().getUserName();
                        List<BaseOrderInfoDB> baseOrderInfoDBs = DBManager.querySelectBaseOrderInfo(userName,status);

                        boolean hasCreatButton = false;
                        if(maintenanceType == OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE) {
                            if (baseOrderInfoDBs == null || baseOrderInfoDBs.size() == 0) {
                                hasCreatButton = true;
                            }
                        }

                        maintenanceOrderContentLayoutWidget.update(hasCreatButton, baseOrderInfoDBs);

                    }

                    if(listener != null){
                        listener.onEnter(selectOrderStatus);
                    }
                }
        });

        return filtrateLayout;
    }

    @Override
    public void update(int position) {

        String userName = UserInfo.getInstance().getUserName();

        baseOrderInfoList = DBManager.querySelectBaseOrderInfo(userName);
        boolean hasCreatButton = false;
        if(maintenanceType == OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE) {
            if (baseOrderInfoList == null || baseOrderInfoList.size() == 0) {
                hasCreatButton = true;
            }
        }

        if(maintenanceOrderContentLayoutWidget == null) {
            maintenanceOrderContentLayoutWidget = new OrderPagerLayoutWidget(context, presenter, maintenanceType, hasCreatButton, baseOrderInfoList, orderInterfaces);
            mLoadOrderDataListener.onSuccess(maintenanceOrderContentLayoutWidget);
        }else{
            maintenanceOrderContentLayoutWidget.update(hasCreatButton, baseOrderInfoList);
        }
    }

    @Override
    public void initInfo() {
        if(maintenanceOrderContentLayoutWidget != null) {
            maintenanceOrderContentLayoutWidget.initSearchFoused();
        }
    }

    public void initSearchLayout(){
        if(maintenanceOrderContentLayoutWidget != null){
            maintenanceOrderContentLayoutWidget.initSearchLayout();
        }
    }

    @Override
    public void loadData() {

//
//        if(isdebug) {
        if(maintenanceType == OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE) {
            showContentView(null);
        }else{
            presenter.selectMaintenanceOrder(maintenanceType);
        }


    }

    private void creatPresenter(){

        presenter = new MaintenanceOrderActivityPresenter();
        presenter.attachView(this);

    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - MvpView interface  start - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    @Override
    public void onLoading() {

    }

    @Override
    public void showContentView(BaseVo dataVo) {
        if(dataVo instanceof OrderCtrlVo){
            OrderCtrlVo mOrderCtrlVo = (OrderCtrlVo) dataVo;
            if(mOrderCtrlVo.getOrderCtrlResult() == 0){
                ToastUtil.showShort(mOrderCtrlVo.getReason());
            }else{
                //TODO delete
                BaseOrderInfoDB baseOrderInfoDB = DBManager.querySelectBaseOrderInfo(UserInfo.getInstance().getUserName(), mOrderCtrlVo.getOrderNumber());
                if(baseOrderInfoDB != null){
                    baseOrderInfoDB.delete();
                }

                update(0);
            }
        }else {
            update(0);
        }

    }

    @Override
    public void onStopLoading() {

        if(orderInterfaces != null){
            orderInterfaces.dismissHintLayout();
        }
    }

    @Override
    public void deleteOrder(BaseOrderInfoDB baseOrderInfoDB){
        if(presenter != null) {
            presenter.deleteOrder(baseOrderInfoDB);
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - MvpView interface  end - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
