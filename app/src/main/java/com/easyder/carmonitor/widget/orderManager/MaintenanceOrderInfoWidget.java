package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.MaintenanceChangeStatusListener;
import com.easyder.carmonitor.presenter.OrderInfoPresenter;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.AttachmentItemVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.InstallFinishVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.MaintenanceOrderProgress;
import com.shinetech.mvp.network.UDP.bean.orderBean.SelectOrderByNumberVo;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;
import com.shinetech.mvp.view.MvpView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-11-16.
 */

public class MaintenanceOrderInfoWidget<M extends BaseVo> implements MvpView {

    private Context context;

    private View rootView;

    private LayoutBackListener listener;

    private RelativeLayout orderinfo_layout_outmost;

    private RelativeLayout orderinfo_hint_layout;

    private ImageView orderinfo_status_img;

    private TextView orderinfo_status_title;

    private TextView orderinfo_status_title_hint;

    private TextView maintenance_order_info_status_ctrl_button;

    private ScrollView scrollview_layout;

    private LinearLayout content_layout;

    private BaseOrderInfoDB baseOrderInfoDB;

    private int maintenanceType;

    private MaintenanceChangeStatusListener statusListener;

    private OrderInfoPresenter presenter;

    private SelectOrderByNumberVo orderInfo;

    private int orderStatus = -1;

    private boolean isDebug = false && LogUtils.isDebug;

    private EnterDialogWidget mEnterDialogWidget;

    private List<AttachmentItemVo> attachmentItemList;


    public MaintenanceOrderInfoWidget(Context context, int maintenanceType, BaseOrderInfoDB baseOrderInfoDB, View rootView, LayoutBackListener listener) {
        this.context = context;
        this.rootView = rootView;
        this.listener = listener;
        this.maintenanceType = maintenanceType;
        this.baseOrderInfoDB = baseOrderInfoDB;
        initTitle(rootView);
        creatPresenter();
        initLayout();

    }

    private void creatPresenter(){

        presenter = new OrderInfoPresenter();
        presenter.attachView(this);

    }

    public void setMaintenanceChangeStatusListener(MaintenanceChangeStatusListener statusListener){
        this.statusListener = statusListener;
    }

    private void initTitle(View view){
        ImageButton title_back = (ImageButton) view.findViewById(R.id.title_back);
        TextView title_text = (TextView) view.findViewById(R.id.title_text);
        ImageButton title_search = (ImageButton) view.findViewById(R.id.title_search);
        String title;
        if(maintenanceType == OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE) {
            title = context.getString(R.string.maintenance_order_info);
        }else {
            title = context.getString(R.string.order_info_title);
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

        title_search.setVisibility(View.GONE);

        RelativeLayout title_layout = (RelativeLayout) view.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);
    }

    private void initLayout(){

        if(rootView == null){
            return;
        }

        orderinfo_status_img = (ImageView) rootView.findViewById(R.id.maintenance_title_status_image);
        orderinfo_status_title = (TextView) rootView.findViewById(R.id.maintenance_status_title_tv);
        orderinfo_status_title_hint = (TextView) rootView.findViewById(R.id.maintenance_title_hint_tv);
        maintenance_order_info_status_ctrl_button = (TextView) rootView.findViewById(R.id.maintenance_order_info_status_ctrl_button);
        scrollview_layout = (ScrollView) rootView.findViewById(R.id.scrollview_layout);

//        initStaus();

        orderinfo_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.maintenance_orderinfo_layout_outmost);
        orderinfo_hint_layout = (RelativeLayout) rootView.findViewById(R.id.orderinfo_hint_layout);

        content_layout = (LinearLayout) rootView.findViewById(R.id.content_layout);

//        addBasePorposerLayout();
        updata();

    }

    public void updata(){
        presenter.requestOrderInfo(baseOrderInfoDB.getOrderNumber(), baseOrderInfoDB.getOrderName());
        /*if(isDaebug){

            SelectOrderByNumberVo mSelectOrderByNumberVo = presenter.getTestData(context, baseOrderInfoDB);

            orderInfo = mSelectOrderByNumberVo;

            initStaus();
            content_layout.removeAllViews();
            addBasePorposerLayout();

        }else {
            presenter.requestOrderInfo(baseOrderInfoDB.getOrderNumber(), baseOrderInfoDB.getOrderName());

        }*/

    }

    private void initStaus(){

        orderStatus = presenter.getOrderStatus(orderInfo);

        if(orderInfo.getResultOrderName().equals(context.getString(R.string.order_name_maintenance)) && maintenanceType == OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE) {

            maintenance_order_info_status_ctrl_button.setVisibility(View.GONE);

            switch (orderStatus) {
                default:
                case MaintenanceOrderInfoBean.COMMIT_STATUS:
                    orderinfo_status_img.setImageResource(R.mipmap.icon_commit_order);
                    orderinfo_status_title.setText(R.string.commit_status_title);
                    orderinfo_status_title_hint.setText(R.string.commit_status_hint);
                    break;
                case MaintenanceOrderInfoBean.PROCESSED_STATUS:
                    orderinfo_status_img.setImageResource(R.mipmap.icon_processed);
                    orderinfo_status_title.setText(R.string.processed_status_title);
                    orderinfo_status_title_hint.setText(R.string.processed_status_hint);
                    break;
                case MaintenanceOrderInfoBean.EVALUATE_STATUS:
                case MaintenanceOrderInfoBean.FINISH_STATUS:
                    if(orderStatus == MaintenanceOrderInfoBean.EVALUATE_STATUS ){
                        maintenance_order_info_status_ctrl_button.setVisibility(View.VISIBLE);
                        maintenance_order_info_status_ctrl_button.setText(R.string.maintenance_order_evaluate);
                        maintenance_order_info_status_ctrl_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(MaintenanceOrderInfoWidget.this.statusListener != null){

                                    DecodeUDPDataTool.OrderContentListItemData orderContentListItemData = DecodeUDPDataTool.decodeItemByDataName(orderInfo.getContentList(),
                                            context.getString(R.string.order_content_maintenance_apply));

                                    if(orderContentListItemData != null) {

                                        String plateNumber = orderContentListItemData.getFistValue(context.getString(R.string.carinfo_platenumber));
                                        MaintenanceOrderInfoWidget.this.statusListener.showMaintenanceOrderValuation(baseOrderInfoDB, plateNumber);

                                    }
                                }
                            }
                        });
                    }else{
                        maintenance_order_info_status_ctrl_button.setVisibility(View.GONE);
                        maintenance_order_info_status_ctrl_button.setOnClickListener(null);
                    }
                    orderinfo_status_img.setImageResource(R.mipmap.icon_finish_order);
                    orderinfo_status_title.setText(R.string.finish_status_title);
                    orderinfo_status_title_hint.setText(R.string.finish_status_hint);
                    break;

            }
        }else{

            String resultOrderName = orderInfo.getResultOrderName();

            switch (orderStatus){
                default:
                case MaintenanceOrderInfoBean.COMMIT_STATUS:
                    orderinfo_status_img.setImageResource(R.mipmap.icon_wait_accept_order);
                    orderinfo_status_title.setText(R.string.maintenance_order_status_wait_accept_order);
                    if(resultOrderName.equals(context.getString(R.string.order_name_maintenance))) {
                        orderinfo_status_title_hint.setText(R.string.maintenance_order_status_accept_order_hint);
                    }else if(resultOrderName.equals(context.getString(R.string.order_name_install))) {
                        orderinfo_status_title_hint.setText(R.string.install_order_info_accept_order_hint);
                    }
                    maintenance_order_info_status_ctrl_button.setVisibility(View.VISIBLE);
                    maintenance_order_info_status_ctrl_button.setText(R.string.accept_order);
                    maintenance_order_info_status_ctrl_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(MaintenanceOrderInfoWidget.this.statusListener != null){
                                MaintenanceOrderInfoWidget.this.statusListener.acceptOrder(baseOrderInfoDB);
                            }
                        }
                    });

                    break;
                case MaintenanceOrderInfoBean.PROCESSED_STATUS:
                    orderinfo_status_img.setImageResource(R.mipmap.icon_have_accept_order);
                    orderinfo_status_title.setText(R.string.maintenance_order_status_have_accept_order);


                    if(resultOrderName.equals(context.getString(R.string.order_name_maintenance))) {

                        orderinfo_status_title_hint.setText(R.string.maintenance_order_status_accept_order_hint);
                        maintenance_order_info_status_ctrl_button.setVisibility(View.VISIBLE);
                        maintenance_order_info_status_ctrl_button.setText(R.string.have_maintenance);
                        maintenance_order_info_status_ctrl_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                List<String> attachmentPath = new ArrayList<>();
                                byte[] attachmentList = orderInfo.getAttachmentList();
                                if(attachmentList != null && attachmentList.length>0) {

                                    List<AttachmentItemVo> accessory = DecodeUDPDataTool.getAccessory(attachmentList);
                                    if(accessory.size()>0){

                                        for(AttachmentItemVo item : accessory){
                                            if(item.getFileType().equals(context.getString(R.string.create_maintenance_result_img))){
                                                attachmentPath.add("/sdcard/Carmonitor"+"/"+ orderInfo.getOrderNumber()+ "/" +item.getFileName());
                                            }
                                        }

                                    }
                                }

                                if (MaintenanceOrderInfoWidget.this.statusListener != null) {
                                    MaintenanceOrderInfoWidget.this.statusListener.upLoadMaintenanceResult(baseOrderInfoDB,attachmentPath);
                                }
                            }
                        });

                    }else if(resultOrderName.equals(context.getString(R.string.order_name_install))){
                        orderinfo_status_title_hint.setText(R.string.install_order_info_have_accept_order_hint);
                        maintenance_order_info_status_ctrl_button.setVisibility(View.VISIBLE);
                        maintenance_order_info_status_ctrl_button.setText(R.string.have_finish_install);

                        DecodeUDPDataTool.OrderContentListItemData orderContentListItemData = DecodeUDPDataTool.decodeItemByDataName(orderInfo.getContentList(),
                                context.getString(R.string.order_content_install_record));

                        final List<InstallTerminalnfo> installTerminalnfos = DBManager.querySelectInstallTerminalnfo(orderInfo.getOrderNumber());
                        boolean saveAll = isSaveAll(installTerminalnfos);
                        boolean isCanCommit;
                        if((installTerminalnfos != null && orderContentListItemData != null && orderContentListItemData.getSize() == installTerminalnfos.size() && saveAll) || orderContentListItemData == null){
                            isCanCommit = true;
                        }else{
                            isCanCommit = false;
                        }

                        if(isCanCommit) {
                            maintenance_order_info_status_ctrl_button.setBackgroundResource(R.color.maintenance_order_info_status_ctrl_button_bg);

                            maintenance_order_info_status_ctrl_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    //TODO  up load data
//                                    ToastUtil.showShort("已安装完毕");
                                    presenter.upLoadinstallResult(context, orderInfo.getOrderNumber(), orderInfo.getResultOrderName(), installTerminalnfos);

                                }
                            });

                        }else {
                            maintenance_order_info_status_ctrl_button.setBackgroundResource(R.color.order_status_disable_bg);
                            maintenance_order_info_status_ctrl_button.setOnClickListener(null);
                        }
                    }
                    break;
                case MaintenanceOrderInfoBean.EVALUATE_STATUS:
                case MaintenanceOrderInfoBean.FINISH_STATUS:
                    orderinfo_status_img.setImageResource(R.mipmap.icon_finish_accept_order);
                    orderinfo_status_title.setText(R.string.maintenance_order_status_finish_order);

                    if(resultOrderName.equals(context.getString(R.string.order_name_maintenance))) {
                        orderinfo_status_title_hint.setText(R.string.maintenance_order_status_finish_accept_order_hint);
                    }else if(resultOrderName.equals(context.getString(R.string.order_name_install))) {
                        orderinfo_status_title_hint.setText(R.string.install_order_info_finish_accept_order_hint);
                    }

                    maintenance_order_info_status_ctrl_button.setVisibility(View.GONE);
                    maintenance_order_info_status_ctrl_button.setOnClickListener(null);
                    break;

            }
        }
    }

    private boolean isSaveAll(List<InstallTerminalnfo> installTerminalnfos){

        if(installTerminalnfos == null || installTerminalnfos.size() == 0){
            return true;
        }

        for(InstallTerminalnfo item : installTerminalnfos){
            boolean integrityInfo = item.isIntegrityInfo();
            if(!integrityInfo){
                return false;
            }
        }

        return true;
    }

    private void addBasePorposerLayout(){

        //添加工单号
        OrderNumberTitleWwidget mOrderNumberTitleWwidget = new OrderNumberTitleWwidget(context,orderInfo.getOrderNumber());
        content_layout.addView(mOrderNumberTitleWwidget.getView(),new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(35)));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = UIUtils.dip2px(10);


        //添加报修人（申请人）基本信息
        MaintenanceOrderProposerInfoWidget maintenanceOrderProposerInfoWidget = new MaintenanceOrderProposerInfoWidget(context, orderInfo);
        content_layout.addView(maintenanceOrderProposerInfoWidget.getView(), layoutParams);

        //添加车辆列表
        if(orderInfo.getResultOrderName().equals(context.getString(R.string.order_name_install))){
            addCarListItem();
        }

        //添加附件信息
        byte[] attachmentList = orderInfo.getAttachmentList();
        if(attachmentList != null && attachmentList.length>0) {

            attachmentItemList = DecodeUDPDataTool.getAccessory(attachmentList);

            if(attachmentItemList.size()>0) {

                if(isDebug) {
                    for (AttachmentItemVo item : attachmentItemList) {
                        System.out.println(item.toString());
                    }
                }

                AttachmentWidget attachmentWidget = new AttachmentWidget(context, orderInfo.getOrderNumber(), orderInfo.getResultOrderName(), attachmentItemList);
                content_layout.addView(attachmentWidget.getView(), layoutParams);
            }

        }

        //添加进度流程
        if(orderInfo.getResultOrderName().equals(context.getString(R.string.order_name_maintenance))) {
            byte[] processList = orderInfo.getProcessList();
            if (processList != null && processList.length > 0) {

                List<MaintenanceOrderProgress> orderProgress = DecodeUDPDataTool.getOrderProgress(processList);

                if(orderProgress.size() > 0){

                    if(isDebug) {
                        for (MaintenanceOrderProgress item : orderProgress) {
                            System.out.println(item.toString());
                        }
                    }

                    OrderProgressWidget mOrderProgressWidget = new OrderProgressWidget(context, orderProgress);
                    content_layout.addView(mOrderProgressWidget.getView(), layoutParams);
                }

            }
        }

        //添加服务评价信息
        if(maintenanceType == OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE && orderStatus == MaintenanceOrderInfoBean.FINISH_STATUS
                && orderInfo.getResultOrderName().equals(context.getString(R.string.order_name_maintenance))) {
            MaintenanceOrderValuationResultWidget mMaintenanceOrderValuationResultWidget = new MaintenanceOrderValuationResultWidget(context, orderInfo);

            View view = mMaintenanceOrderValuationResultWidget.getView();
            if(view != null) {
                content_layout.addView(view, layoutParams);
            }
        }

        //添加维修结果信息
        if(maintenanceType != OrderInfoWidget.MAINTENANCE_ORDER_USER_TYPE && orderStatus == MaintenanceOrderInfoBean.FINISH_STATUS
                && orderInfo.getResultOrderName().equals(context.getString(R.string.order_name_maintenance))) {
            MaintenanceResultInfoWidget mMaintenanceResultInfoWidget = new MaintenanceResultInfoWidget(context, orderInfo);
            View view = mMaintenanceResultInfoWidget.getView();
            if(view != null) {
                content_layout.addView(view, layoutParams);
            }
        }

        scrollview_layout.smoothScrollTo(0,0);
    }

    private void addCarListItem(){

        final DecodeUDPDataTool.OrderContentListItemData orderContentListItemData = DecodeUDPDataTool.decodeItemByDataName(orderInfo.getContentList(),context.getString(R.string.order_content_install_record));

        if(orderContentListItemData== null || (!orderContentListItemData.hasContent())){
            return;
        }
        TextView carListItemView = (TextView) View.inflate(context, R.layout.installorder_carlist_textview, null);

        carListItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.showShort("carListItemView");
                if (MaintenanceOrderInfoWidget.this.statusListener != null) {

                    MaintenanceOrderInfoWidget.this.statusListener.showInstallCarList(orderStatus, orderInfo.getResultOrderName(), orderInfo.getOrderNumber(), orderContentListItemData, attachmentItemList);
                }
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(35));
        layoutParams.topMargin = UIUtils.dip2px(10);
        content_layout.addView(carListItemView, layoutParams);
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

    public void dismissHintLayout(){
        orderinfo_hint_layout.removeAllViews();
        orderinfo_hint_layout.setVisibility(View.GONE);
    }

    public boolean onKeyBack(){
        if(orderinfo_hint_layout != null && orderinfo_hint_layout.getVisibility() == View.VISIBLE){
            dismissHintLayout();
            return true;
        }

        return false;
    }

    @Override
    public void onLoading() {

        if(mEnterDialogWidget == null) {
            mEnterDialogWidget = new EnterDialogWidget(context, null);
        }
        mEnterDialogWidget.showLoading();
        addHint(mEnterDialogWidget.getView());

    }

    @Override
    public void showContentView(BaseVo dataVo) {
        if(dataVo instanceof SelectOrderByNumberVo){
            orderInfo = (SelectOrderByNumberVo) dataVo;
            initStaus();
            content_layout.removeAllViews();
            addBasePorposerLayout();
            BaseOrderInfoDB baseOrderInfoDB = DBManager.querySelectBaseOrderInfo(UserInfo.getInstance().getUserName(), orderInfo.getOrderNumber());
            if(baseOrderInfoDB != null){
                baseOrderInfoDB.setOrderStatus(orderInfo.getOrderStatus());
                DBManager.updataBaseOrderInfo(baseOrderInfoDB);
            }
        }else if(dataVo instanceof InstallFinishVo){
            updata();
        }

    }

    @Override
    public void onStopLoading() {
        dismissHintLayout();

    }
}
