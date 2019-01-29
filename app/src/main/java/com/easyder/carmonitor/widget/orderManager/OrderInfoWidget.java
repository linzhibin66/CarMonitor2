package com.easyder.carmonitor.widget.orderManager;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Scheme.OrderInfoScheme.BaseOrderInfoItem;
import com.easyder.carmonitor.Scheme.OrderInfoScheme.BaseOrderInfoScheme;
import com.easyder.carmonitor.Scheme.OrderInfoScheme.InstallOrderScheme;
import com.easyder.carmonitor.Scheme.OrderInfoScheme.MaintenanceOrderScheme;
import com.easyder.carmonitor.adapter.OrderInfoPagerAdapter;
import com.easyder.carmonitor.interfaces.CreatOrderSearchListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.LoadOrderDataListener;
import com.easyder.carmonitor.interfaces.NavigationBarPagerListener;
import com.easyder.carmonitor.interfaces.OrderInfoShowContentInterfaces;
import com.easyder.carmonitor.interfaces.OrderManagerFiltrateListener;
import com.easyder.carmonitor.widget.NavigationBar;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-11-10.
 */

public class OrderInfoWidget implements LoadOrderDataListener, NavigationBarPagerListener {

    public static final int MAINTENANCE_ORDER_USER_TYPE = 0;

    public static final int INSTALL_ORDER_TYPE = 1;

    private Context context;

    private View rootView;

    private LayoutBackListener listener;

    private RelativeLayout orderinfo_layout_outmost;

    private RelativeLayout orderinfo_hint_layout;

    private RelativeLayout orderinfo_content;

    private RelativeLayout hint_loading_layout;

    /**
     * 标题字串
     */
    private String[] stringArray;

    private NavigationBar navigationBar;

    private int OrderInfoType;

    private BaseOrderInfoScheme mBaseOrderInfoScheme;

    private OrderInfoPagerAdapter mOrderInfoPagerAdapter;

    private OrderInfoShowContentInterfaces orderInterfaces;

    private Handler mHandler;

    public OrderInfoWidget(int OrderInfoType, Context context, View rootView, LayoutBackListener listener) {
        this.context = context;
        this.rootView = rootView;
        this.listener = listener;
        this.OrderInfoType = OrderInfoType;
        initOrderInfoScheme();
        initTitle(rootView);
        initLayout();
    }

    /**
     * 初始化导航栏对应的内容管理对象
     */
    private void initOrderInfoScheme(){

        mBaseOrderInfoScheme = new MaintenanceOrderScheme(context, OrderInfoType, this);

        /*switch (OrderInfoType){
            case MAINTENANCE_ORDER_USER_TYPE:
            case MAINTENANCE_ORDER_TYPE:
                mBaseOrderInfoScheme = new MaintenanceOrderScheme(context, OrderInfoType, this);
                break;
            case  INSTALL_ORDER_TYPE:
                mBaseOrderInfoScheme = new InstallOrderScheme(context, this);
                mHandler = new Handler(){

                    @Override
                    public void handleMessage(Message msg) {
                        mBaseOrderInfoScheme.update((int)msg.obj);
                    }
                };
                break;
        }*/
    }

    private void initTitle(View view){
        ImageButton title_back = (ImageButton) view.findViewById(R.id.title_back);
        TextView title_text = (TextView) view.findViewById(R.id.title_text);
        TextView title_search = (TextView) view.findViewById(R.id.title_search);
        title_search.setCompoundDrawablesWithIntrinsicBounds(null,null,context.getResources().getDrawable(R.mipmap.icon_funnel),null);
        title_search.setCompoundDrawablePadding(UIUtils.dip2px(5));
        title_search.setText(R.string.order_manager_filtrate);
        title_search.setVisibility(View.VISIBLE);

        title_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View filtrateView = mBaseOrderInfoScheme.CreatFiltrateView(new OrderManagerFiltrateListener() {
                    @Override
                    public void Oncancle() {
                        dismissHintLayout();

                    }

                    @Override
                    public void onEnter(List<String> selectList) {
                        dismissHintLayout();
                    }
                });

                if(filtrateView != null) {
                    orderinfo_hint_layout.removeAllViews();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
                    orderinfo_hint_layout.addView(filtrateView,params);
                    int statusBarHeight = UIUtils.getStatusBarHeight();
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) orderinfo_hint_layout.getLayoutParams();
                    layoutParams.setMargins(0, statusBarHeight + UIUtils.dip2px(57), 0, 0);
                    orderinfo_hint_layout.setVisibility(View.VISIBLE);

                    if(mBaseOrderInfoScheme instanceof MaintenanceOrderScheme) {
                        ((MaintenanceOrderScheme)mBaseOrderInfoScheme).initSearchLayout();
                    }
                }
            }
        });

        String title;
        if(OrderInfoType == MAINTENANCE_ORDER_USER_TYPE){
            title = context.getString(R.string.maintenance_order_manager);
            /*title_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(orderInterfaces != null){
                        orderInterfaces.ShowCreatMaintenanceOrder();
                    }
                }
            });*/
        }else{
            title = context.getString(R.string.install_maintenance_order_manager);
        }
        title_text.setText(title);

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

//        title_search.setVisibility(View.GONE);

        RelativeLayout title_layout = (RelativeLayout) view.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);
    }

    private void initLayout(){

        if(rootView == null){
            return;
        }

        orderinfo_content = (RelativeLayout) rootView.findViewById(R.id.orderinfo_content);
        hint_loading_layout = (RelativeLayout) rootView.findViewById(R.id.hint_loading_layout);
        orderinfo_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.orderinfo_layout_outmost);
        orderinfo_hint_layout = (RelativeLayout) rootView.findViewById(R.id.orderinfo_hint_layout);

//        initContentLayout();

        //加载数据
        mBaseOrderInfoScheme.loadData();

    }

    public View getView(){
        return rootView;
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(orderinfo_layout_outmost != null) {
            orderinfo_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    public void addHint(View view){
        orderinfo_hint_layout.removeAllViews();
        orderinfo_hint_layout.addView(view);
        orderinfo_hint_layout.setVisibility(View.VISIBLE);

    }

    public void addHint(View view, RelativeLayout.LayoutParams layoutParams){
        orderinfo_hint_layout.removeAllViews();
        orderinfo_hint_layout.addView(view, layoutParams);
        orderinfo_hint_layout.setVisibility(View.VISIBLE);

    }

    public RelativeLayout.LayoutParams getHintLayoutParams(){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) orderinfo_hint_layout.getLayoutParams();

        return layoutParams;
    }

    public void dismissHintLayout(){
        orderinfo_hint_layout.removeAllViews();
        orderinfo_hint_layout.setVisibility(View.GONE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) orderinfo_hint_layout.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, 0);
    }

    private void initContentLayout(){
        if(OrderInfoType != INSTALL_ORDER_TYPE){
            return;
        }

        List<View> itemList = initTitleItem(context.getResources().getStringArray(R.array.order_type_item_title));

        //初始化导航栏框架
        navigationBar = new NavigationBar(context, itemList, null);
        navigationBar.setStatusLineWidth(UIUtils.dip2px(45));

        navigationBar.setPagerListener(this);

        orderinfo_content.addView(navigationBar.getLayout(), new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    /**
     * 初始化导航栏
     */
    private List<View> initTitleItem(String[] stringArray){

        List<View> itemList = new ArrayList<>();

        if(stringArray == null || stringArray.length==0){
            return itemList;
        }

        for(int i = 0; i<stringArray.length;i++){
            TextView textView = new TextView(context);
            textView.setTextColor(Color.parseColor("#FF666666"));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimensionPixelSize(R.dimen.allcarlist_title_textsize));
            textView.setGravity(Gravity.CENTER);
            textView.setText(stringArray[i]);
            itemList.add(textView);
        }

        return itemList;

    }

    @Override
    public void onPageSelected(int position) {
        updateTitleSelect(position);
    }

    private void updateTitleSelect(final int position){
        int statusCount = navigationBar.getStatusCount();
        for(int i = 0; i<statusCount; i++){
            TextView statusItem = (TextView) navigationBar.getStatusItem(i);
            if(i == position){
                statusItem.setTextColor(Color.parseColor("#FF2C5DFB"));
            }else{
                statusItem.setTextColor(Color.parseColor("#FF666666"));
            }
        }
//

        if(mHandler!= null){
            mHandler.removeMessages(1);
            Message message = Message.obtain();
            message.what = 1;
            message.obj = position;
            mHandler.sendMessageDelayed(message,500);
        }
//        ((InstallOrderScheme)mBaseOrderInfoScheme).setCurrentPosition(position);
    }


    /**
     * 用户或维修人员调用（只有维修单）
     * @param pagerItem
     */
    @Override
    public void onSuccess(BaseOrderInfoItem pagerItem) {

        orderinfo_content.addView(pagerItem.getView(), new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

    }


    /**
     * 组长调用（包含安装单核维修单）
     * @param pagerItem
     */
    @Override
    public void onSuccess(List<BaseOrderInfoItem> pagerItem) {

        if(OrderInfoType != INSTALL_ORDER_TYPE){
            return;
        }

        if(pagerItem.size() == 0){
            //TODO show no data
            return;
        }

        if(mOrderInfoPagerAdapter == null){
            mOrderInfoPagerAdapter = new OrderInfoPagerAdapter();
        }

        mOrderInfoPagerAdapter.initData(pagerItem);
        navigationBar.setPagerAdapter(mOrderInfoPagerAdapter);

        updateTitleSelect(navigationBar.getCurrentIndex());

    }

    @Override
    public void onError(String errorInfo) {

        //TODO show no load data
    }

    public void setOrderInterfaces(OrderInfoShowContentInterfaces orderInterfaces ){
        this.orderInterfaces = orderInterfaces;
        if(mBaseOrderInfoScheme != null) {
            mBaseOrderInfoScheme.setOrderInterfaces(orderInterfaces);
        }
    }

    public void updata(){
        //加载数据
        mBaseOrderInfoScheme.loadData();

    }

    public boolean onKeyBack(){
        if(orderinfo_hint_layout != null && orderinfo_hint_layout.getVisibility() == View.VISIBLE){
            dismissHintLayout();
            return true;
        }

        return false;
    }

    public void initInfo(){
        if(mBaseOrderInfoScheme != null) {
            mBaseOrderInfoScheme.initInfo();
        }
    }

    public void deleteOrder(BaseOrderInfoDB baseOrderInfoDB){
        if(mBaseOrderInfoScheme != null){

            EnterDialogWidget mEnterDialogWidget = new EnterDialogWidget(context,null);
            mEnterDialogWidget.showLoading();

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);

            addHint(mEnterDialogWidget.getView(),params);

            mBaseOrderInfoScheme.deleteOrder(baseOrderInfoDB);
        }
    }


}
