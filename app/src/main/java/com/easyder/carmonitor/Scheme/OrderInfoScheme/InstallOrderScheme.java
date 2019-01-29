package com.easyder.carmonitor.Scheme.OrderInfoScheme;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.FiltrateSelectAdapter;
import com.easyder.carmonitor.interfaces.LoadOrderDataListener;
import com.easyder.carmonitor.interfaces.OrderInfoShowContentInterfaces;
import com.easyder.carmonitor.interfaces.OrderManagerFiltrateListener;
import com.easyder.carmonitor.presenter.InstallOrderActivityPresenter;
import com.easyder.carmonitor.widget.orderManager.InstallOrderPagetLayoutWidget;
import com.easyder.carmonitor.widget.orderManager.OrderInfoWidget;
import com.easyder.carmonitor.widget.orderManager.OrderPagerLayoutWidget;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskManager;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;
import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.view.MvpView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-11-10.
 */

public class InstallOrderScheme<M extends BaseVo> extends BaseOrderInfoScheme implements MvpView{

    private InstallOrderActivityPresenter presenter;

    private OrderInfoShowContentInterfaces orderInterfaces;

    private Context context;

    private InstallOrderPagetLayoutWidget installOrderWidget;
    private OrderPagerLayoutWidget maintenanceOrderContentLayoutWidget;

    private ArrayList<BaseOrderInfoItem> baseOrderInfoItems;

    private List<String> selectOrderStatus = new ArrayList();

    private int currentPosition = 0;

    public InstallOrderScheme(Context context, LoadOrderDataListener listener) {
        super(listener);
        this.context = context;
        creatPresenter();

    }

    private void creatPresenter(){

        presenter = new InstallOrderActivityPresenter();
        presenter.attachView(this);

    }

    @Override
    public void loadData() {
//        presenter.loadData(BaseVo datavo);
        showContentView(null);

    }

    @Override
    public void setOrderInterfaces(OrderInfoShowContentInterfaces orderInterfaces) {
        this.orderInterfaces = orderInterfaces;

        if(installOrderWidget != null){
            installOrderWidget.setOrderListener(orderInterfaces);
        }

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

        String[] stringArray = context.getResources().getStringArray(R.array.orderlist_item_title);

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

                    String[] stringArray = context.getResources().getStringArray(R.array.orderlist_item_title);

                    for (int i = 0; i < stringArray.length; i++) {
                        if (selectOrderStatus.contains(stringArray[i])) {
                            status[count] = i;
                            count++;
                        }
                    }
                }else{
                    status = new int[]{};
                }

                String userName = UserInfo.getInstance().getUserName();

                if(currentPosition == 0){
                    if(installOrderWidget != null) {
                        List<InstallOrderBaseInfo> installOrderBaseInfos = DBManager.querySelectInstallOrderBaseInfo(userName, status);
                        installOrderWidget.update(installOrderBaseInfos);
                    }

                }else {
                    /*if (maintenanceOrderContentLayoutWidget != null) {

                        List<MaintenanceOrderInfoBean> maintenanceOrderInfoList = DBManager.querySelectMaintenanceOrderInfo(userName, status);
                        maintenanceOrderContentLayoutWidget.update(false, maintenanceOrderInfoList);
                    }*/
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
        this.currentPosition = position;
        selectOrderStatus.clear();
        String userName = UserInfo.getInstance().getUserName();
        if (position == 0) {
            List<InstallOrderBaseInfo> installOrderBaseInfos = DBManager.querySelectInstallOrderBaseInfo(userName);
            installOrderWidget.update(installOrderBaseInfos);
        }else{
            //维修单获取
//            List<MaintenanceOrderInfoBean> maintenanceOrderInfoBeenList = DBManager.querySelectMaintenanceOrderInfo(userName);
//            maintenanceOrderContentLayoutWidget.update(false, maintenanceOrderInfoBeenList);
        }
    }

    @Override
    public void initInfo() {

    }

    @Override
    public void deleteOrder(BaseOrderInfoDB baseOrderInfoDB) {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void showContentView(BaseVo dataVo) {

        final String userName = UserInfo.getInstance().getUserName();


        List<InstallOrderBaseInfo> installOrderBaseInfos = DBManager.querySelectInstallOrderBaseInfo(userName);
        if(installOrderBaseInfos == null || installOrderBaseInfos.size() == 0){

            TaskManager.runDBTask(new TaskBean() {
                @Override
                public void run() {
                    List<InstallOrderBaseInfo> installOrderBaseInfolist = new ArrayList();
                    List<InstallTerminalnfo> installTerminalnfolist = new ArrayList();
                    for(int i = 0 ;i < 3; i++){
                        String orderNumber = System.currentTimeMillis()+""+i;
                        installOrderBaseInfolist.add(new InstallOrderBaseInfo(i,orderNumber,userName,"虎门公汽",(short)0,5*i,"李先生","2017-12-1"+i));

                        for(int j = 0 ;j < (5*i)+5; j++){
                            /*InstallTerminalnfo installTerminalnfo = new InstallTerminalnfo(orderNumber, ("DSF558866997733" + i) + j, ("粤S888" + i) + j, (short) 1, ("HG-01-0" + i) + j,
                                    ("GOPRKd5846821" + i) + j, ("88569723188" + i) + j, ("135888888" + i) + j, -1, null, null, null, null);
                            installTerminalnfolist.add(installTerminalnfo);*/
                        }
                    }
                    DBManager.insertInstallOrderBaseInfoList(installOrderBaseInfolist);
                    DBManager.insertInstallTerminalnfoList(installTerminalnfolist);
                }
            });

            installOrderBaseInfos = new ArrayList();
        }

        if(installOrderWidget == null) {
            installOrderWidget = new InstallOrderPagetLayoutWidget(context, installOrderBaseInfos, orderInterfaces);
        }else{
            installOrderWidget.update(installOrderBaseInfos);
        }

        //维修单获取
        /*List<MaintenanceOrderInfoBean> maintenanceOrderInfoBeenList = DBManager.querySelectMaintenanceOrderInfo(userName);

        if(maintenanceOrderContentLayoutWidget == null) {
            maintenanceOrderContentLayoutWidget = new OrderPagerLayoutWidget(context, OrderInfoWidget.MAINTENANCE_ORDER_TYPE, false, maintenanceOrderInfoBeenList, orderInterfaces);
        }else{
            maintenanceOrderContentLayoutWidget.update(false, maintenanceOrderInfoBeenList);
        }*/


        if(baseOrderInfoItems == null || baseOrderInfoItems.size()==0){
            baseOrderInfoItems = new ArrayList();

            baseOrderInfoItems.add(installOrderWidget);
//            baseOrderInfoItems.add(maintenanceOrderContentLayoutWidget);

            mLoadOrderDataListener.onSuccess(baseOrderInfoItems);
        }

    }

    @Override
    public void onStopLoading() {

    }

    public void setCurrentPosition(int position){
        this.currentPosition = position;
    }
}
