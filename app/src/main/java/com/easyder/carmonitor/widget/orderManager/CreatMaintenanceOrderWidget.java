package com.easyder.carmonitor.widget.orderManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.JsonUtil;
import com.easyder.carmonitor.adapter.AddPicturesImgGridAdapter;
import com.easyder.carmonitor.broadcast.SDCardListeren;
import com.easyder.carmonitor.interfaces.CreatMaintenanceOrderListener;
import com.easyder.carmonitor.interfaces.CreatOrderSearchListener;
import com.easyder.carmonitor.interfaces.EnterDialogListener;
import com.easyder.carmonitor.interfaces.GetPictureListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.presenter.CreatMaintenanceOrderPresenter;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.CreateMaintenanceInfoDB;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.CreateOrUpdateOrderVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.MaintenanceApplyVo;
import com.shinetech.mvp.network.UDP.presenter.UpLoadImgPresenter;
import com.shinetech.mvp.utils.FileUtils;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.TimeUtil;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;
import com.shinetech.mvp.view.MvpView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ljn on 2017-11-20.
 */

public class CreatMaintenanceOrderWidget<M extends BaseVo> implements View.OnClickListener, MvpView{

    private Context context;

    private LayoutBackListener listener;

    private View rootView;

    /**
     * 外部阴影界面
     */
    private RelativeLayout orderinfo_layout_outmost;

    /**
     * 阴影布局
     */
    private RelativeLayout maintenance_orderinfo_enter_layout;

    /**
     * 车牌号码
     */
    private EditText platenumber_value;

    /**
     * 车牌颜色
     */
//    private TextView platenumber_color;

    /**
     * 联系人
     */
    private EditText proposer_value;

    /**
     * 联系电话
     */
    private EditText contact_number_value;

    /**
     * 预约时间
     */
    private TextView appointment_time_value;

    /**
     * 创建按钮
     */
    private TextView create_order_createbuttom;

    /**
     * 提交按钮,确认整个工单,将会转交下个处理流程
     */
    private TextView commit_create_maintenance_order;

    /**
     * 工单号
     */
    private TextView create_order_text_ordernumber;

    /**
     * 预约地点
     */
    private EditText appointment_location_value;

    /**
     * 故障描述
     */
    private EditText creat_maintenance_order_errordescribe;

    /**
     * 图片描述
     */
    private MyGridView imglistGridView;

    /**
     * 图片路径list
     */
    private List<String> imgPath = new ArrayList<>();

    private AddPicturesImgGridAdapter addPicturesImgGridAdapter;

    private CreatMaintenanceOrderListener creatOrderListener;

    /**
     * 维修单信息
     */
//    private MaintenanceOrderInfoBean maintenanceOrderInfoBean;

    /**
     * 确认框界面
     */
    private EnterDialogWidget enterDialogWidget;

    /**
     * 搜索车牌
     */
    private CreatOrderSearchPlateNumberWidget creatOrderSearchPlateNumberWidget;

    private String path = "/sdcard/Carmonitor/";

    private SDCardListeren mSDCardListeren;

    private CreatMaintenanceOrderPresenter presenter;

    /**
     * 客户名称
     */
    private String proposer;

    /**
     * 联系电话
     */
    private String contactNumber;

    /**
     * 车牌号码
     */
    private String platenumber;

    /**
     * 预约时间
     */
    private String appointmentTime;

    /**
     * 预约地点
     */
    private String appointmentLocation;

    /**
     * 故障描述
     */
    private String errordescribe;

    private boolean isDebug = false && LogUtils.isDebug;

    private boolean iFistfoused = true;

    private EnterDialogListener enterDialogListener;

    public CreatMaintenanceOrderWidget(Context context, View layout, LayoutBackListener listener) {
        this.context = context;
        this.listener = listener;
        rootView = layout;

        initTitle(rootView);
        initLayout();
        initDate(null);
        creatPresenter();
    }

    public CreatMaintenanceOrderWidget(Context context, String orderNumber, View layout, LayoutBackListener listener) {
        this.context = context;
        this.listener = listener;
        rootView = layout;

        initTitle(rootView);
        initLayout();
        initDate(orderNumber);
        creatPresenter();
    }

    private void creatPresenter(){
        presenter = new CreatMaintenanceOrderPresenter();
        presenter.attachView(this);
    }

    private void initTitle(View view){
        ImageButton title_back = (ImageButton) view.findViewById(R.id.title_back);
        TextView title_text = (TextView) view.findViewById(R.id.title_text);
        TextView title_search = (TextView) view.findViewById(R.id.title_search);

        String title = context.getString(R.string.creat_maintenance_order);
        title_text.setText(title);

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mSDCardListeren != null){
                    mSDCardListeren.stopWatching();
                }

                if (listener != null) {
                    listener.onBack();

                }
            }
        });

        title_search.setVisibility(View.GONE);

        /*title_search.setText(context.getString(R.string.commit));

        title_search.setOnClickListener(this);*/

        RelativeLayout title_layout = (RelativeLayout) view.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);
    }
    public void initDate(String orderNumber){
        if(TextUtils.isEmpty(orderNumber)){
            platenumber_value.setText("");
            proposer_value.setText("");
            contact_number_value.setText("");
            appointment_time_value.setText("");
            appointment_location_value.setText("");
            creat_maintenance_order_errordescribe.setText("");
            create_order_text_ordernumber.setText("");

            create_order_createbuttom.setOnClickListener(this);
            create_order_createbuttom.setBackgroundResource(R.drawable.create_order_buttom_bg_p);
            create_order_createbuttom.setTextColor(context.getResources().getColor(R.color.creat_maintenance_createbutton_on_color));
            return;
        }else{
            BaseOrderInfoDB baseOrderInfoDB = DBManager.querySelectBaseOrderInfo(UserInfo.getInstance().getUserName(), orderNumber);
            if(baseOrderInfoDB != null){
                CreateMaintenanceInfoDB data = baseOrderInfoDB.getData();
                if(data != null) {
                    platenumber = data.getPlateNumber();
                    platenumber_value.setText(platenumber);

                    proposer = data.getProposer();
                    proposer_value.setText(proposer);

                    contactNumber = data.getContactNumber();
                    contact_number_value.setText(contactNumber);

                    appointmentTime = data.getAppointmentTime();
                    appointment_time_value.setText(appointmentTime);

                    appointmentLocation = data.getAppointmentLocation();
                    appointment_location_value.setText(appointmentLocation);

                    errordescribe = data.getProblemDescription();
                    creat_maintenance_order_errordescribe.setText(errordescribe);

//                    orderNumber = data.getOrderNumber();
                    create_order_text_ordernumber.setText(context.getString(R.string.create_maintenance_order_ordernumber, data.getOrderNumber()));

                    disableEdit();

                    String pathjsonStr = data.getPathjsonStr();
                    List<String> pathList = JsonUtil.JSONArrayToList(pathjsonStr);
                    if(pathList!= null && pathList.size()>0){
                        imgPath.clear();
                        imgPath.addAll(pathList);
                        imgPath.add("photo");
                        if(addPicturesImgGridAdapter != null) {
                            addPicturesImgGridAdapter.initData(imgPath);
                        }
                    }

                }

            }

        }
    }

    private void initLayout(){

        platenumber_value = (EditText) rootView.findViewById(R.id.creat_maintenance_order_platenumber_value);
//        platenumber_color = (TextView) rootView.findViewById(R.id.creat_maintenance_order_platenumber_color_value);
        proposer_value = (EditText) rootView.findViewById(R.id.creat_maintenance_order_proposer_value);
        contact_number_value = (EditText) rootView.findViewById(R.id.creat_maintenance_order_contact_number_value);
        appointment_time_value = (TextView) rootView.findViewById(R.id.creat_maintenance_order_appointment_time_value);
        appointment_location_value = (EditText) rootView.findViewById(R.id.creat_maintenance_order_appointment_location_value);
        creat_maintenance_order_errordescribe = (EditText) rootView.findViewById(R.id.creat_maintenance_order_errordescribe_value);

        create_order_createbuttom = (TextView) rootView.findViewById(R.id.create_order_createbuttom);
//        create_order_createbuttom.setOnClickListener(this);

        create_order_text_ordernumber = (TextView) rootView.findViewById(R.id.create_order_text_ordernumber);
//        create_order_text_ordernumber.setVisibility(View.GONE);

        commit_create_maintenance_order = (TextView) rootView.findViewById(R.id.commit_create_maintenance_order);
        commit_create_maintenance_order.setOnClickListener(this);


        initPlateNumberSelect();
        initAddPicyures();

        //超出内容的模糊阴影布局
        orderinfo_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.creat_maintenance_orderinfo_layout_outmost);

        //整个提示框（加模糊背景）
        maintenance_orderinfo_enter_layout = (RelativeLayout) rootView.findViewById(R.id.maintenance_orderinfo_enter_layout);

         //确认框的初始化
        enterDialogWidget = new EnterDialogWidget(context, new EnterDialogListener() {
            @Override
            public void OnCancel() {
                dismissEnterLayout();
            }

            @Override
            public void OnEnter() {

                // 提交操作
                String orderNumber = getOrderNumber();
                presenter.CommitCreateMaintenanceOrderProcess(orderNumber, platenumber, appointmentTime, appointmentLocation, errordescribe);
                /*if(isDebug){
                    BaseOrderInfoDB baseOrderInfoDB = DBManager.querySelectBaseOrderInfo(UserInfo.getInstance().getUserName(), orderNumber);
                    if(baseOrderInfoDB != null) {
                        CreateMaintenanceInfoDB data = baseOrderInfoDB.getData();
                        if(data != null){
//                            DBManager.deleteCreateMaintenanceInfo(data);
                            baseOrderInfoDB.setDataInfoId(-1);
                            DBManager.updataBaseOrderInfo(baseOrderInfoDB);
                        }
                    }

                    if (!TextUtils.isEmpty(orderNumber)) {
                        creatOrderListener.onSuccessCommitData(orderNumber);
                    } else {
                        ToastUtil.showShort(context.getString(R.string.upload_maintenance_commit_fail));
                    }
                }else {
                    presenter.CommitCreateMaintenanceOrderProcess(orderNumber, platenumber, appointmentTime, appointmentLocation, errordescribe);
                }*/
//                onLoading();
//                creatOrderListener.onSuccessCommitData("GDH123456789");
//                showContentView(null);
            }
        });

       /* platenumber_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(creatOrderListener != null){
                    creatOrderListener.showSelectPlateColorDialog();
                }
            }
        });*/

        appointment_time_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(creatOrderListener != null){
                    creatOrderListener.showSelectAppointmentTimeDialog();
                }
            }
        });
    }

    private void initPlateNumberSelect(){
//        platenumber_value.setFocusable(false);
        platenumber_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus && maintenance_orderinfo_enter_layout != null && maintenance_orderinfo_enter_layout.getVisibility() != View.VISIBLE){
                     if(iFistfoused){
                         iFistfoused = false;
                         return;
                     }

//                     System.out.println("setOnFocusChangeListener - - - - - - - - - ");

                    maintenance_orderinfo_enter_layout.removeAllViews();

                    maintenance_orderinfo_enter_layout.setVisibility(View.VISIBLE);

                    showSearchPlateNumberLayout();

                }
            }
        });

//        platenumber_value.setFocusable(true);

        platenumber_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(maintenance_orderinfo_enter_layout != null) {

                   /* if(maintenance_orderinfo_enter_layout.getVisibility() == View.VISIBLE){
                        maintenance_orderinfo_enter_layout.removeAllViews();

                        maintenance_orderinfo_enter_layout.setVisibility(View.GONE);
                    }else {*/

                        maintenance_orderinfo_enter_layout.removeAllViews();

                        maintenance_orderinfo_enter_layout.setVisibility(View.VISIBLE);
//                    System.out.println("setOnClickListener - - - - - - - - - ");
                        showSearchPlateNumberLayout();

//                    }
                }

            }
        });

        platenumber_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(maintenance_orderinfo_enter_layout.getVisibility() == View.VISIBLE){
                    if(creatOrderSearchPlateNumberWidget != null) {
                        creatOrderSearchPlateNumberWidget.updataSearch(s.toString());
                    }
                }else{
//                    System.out.println("onTextChanged - - - - - - - - - ");
                    showSearchPlateNumberLayout();
                    creatOrderSearchPlateNumberWidget.updataSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showSearchPlateNumberLayout(){
        if(creatOrderSearchPlateNumberWidget == null) {
            creatOrderSearchPlateNumberWidget = new CreatOrderSearchPlateNumberWidget(context, new CreatOrderSearchListener() {
                @Override
                public void onSelected(String plateNumber) {
                    maintenance_orderinfo_enter_layout.removeAllViews();
                    maintenance_orderinfo_enter_layout.setVisibility(View.GONE);
                    platenumber_value.setText(plateNumber);
                }

                @Override
                public void onBack() {
                    if(listener != null) {
                        listener.onBack();
                    }
                }
            });
        }else{
            creatOrderSearchPlateNumberWidget.updataSearch(null);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        maintenance_orderinfo_enter_layout.removeAllViews();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) maintenance_orderinfo_enter_layout.getLayoutParams();
        layoutParams.setMargins(0,UIUtils.getStatusBarHeight()+UIUtils.dip2px(57)+UIUtils.dip2px(50),0,0);

        maintenance_orderinfo_enter_layout.addView(creatOrderSearchPlateNumberWidget.getView(), params);
    }

    /**
     * 初始化添加图片界面
     */
    private void initAddPicyures(){
        imglistGridView = (MyGridView) rootView.findViewById(R.id.creat_maintenance_order_imglist);

        imgPath.add("photo");

        addPicturesImgGridAdapter = new AddPicturesImgGridAdapter(context);

        addPicturesImgGridAdapter.initData(imgPath);

        imglistGridView.setAdapter(addPicturesImgGridAdapter);

        imglistGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if((addPicturesImgGridAdapter.getCount()-1) == position){
                    //TODO show select
                    showPictureMenuView();
                }else{
                    //TODO show picture

                    Intent intent = new Intent(Intent.ACTION_VIEW);    //打开图片得启动ACTION_VIEW意图
                   Uri uri = Uri.fromFile(new File(imgPath.get(position)));
                    intent.setDataAndType(uri, "image/*");    //设置intent数据和图片格式
                    ((Activity)context).startActivity(intent);

                }
            }
        });

        imglistGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if((addPicturesImgGridAdapter.getCount()-1) == position){
                    //TODO show select
                    showPictureMenuView();
                }else{
                    //TODO show picture
                    String filePath = (String) addPicturesImgGridAdapter.getItem(position);

                    deleteImg(filePath);

                   /* File file = new File(filePath);
                    if(file.exists()){
                        file.delete();
                    }
*/

                }
                return true;
            }
        });

        mSDCardListeren = new SDCardListeren(path, imgPath, addPicturesImgGridAdapter);
        mSDCardListeren.startWatching();
    }

    /**
     * 删除图片
     * @param filePath
     */
    private void deleteImg(final String filePath){

        if(create_order_text_ordernumber == null || TextUtils.isEmpty(create_order_text_ordernumber.getText().toString().trim())){
            ToastUtil.showShort(context.getString(R.string.create_maintenance_order_add_picture_hint));
            return ;
        }

        if(maintenance_orderinfo_enter_layout != null) {


            if(enterDialogListener == null) {
                enterDialogListener = new EnterDialogListener() {
                    @Override
                    public void OnCancel() {
                        dismissEnterLayout();
                    }

                    @Override
                    public void OnEnter() {

                        String fileName = FileUtils.getFileName(filePath);

                        //工单名
                        String orderName = CarMonitorApplication.getInstance().getString(R.string.order_name_maintenance);

                        presenter.deleteImg(orderName, getOrderNumber(), context.getString(R.string.create_maintenance_img), fileName, new UpLoadImgPresenter.UploadImgListener() {
                            @Override
                            public void OnSuccess() {
                                File file = new File(filePath);
                                if (file.exists()) {
                                    file.delete();
                                }

                                imgPath.remove(filePath);
                                addPicturesImgGridAdapter.initData(imgPath);

                                updataImgPathToDB();
                            }

                            @Override
                            public void OnError(String errorInfo) {
                                ToastUtil.showShort(errorInfo);
                            }
                        });
                    }
                };
            }

            EnterDialogWidget enterDialogWidget = new EnterDialogWidget(context, enterDialogListener);

            enterDialogWidget.setTitle(context.getString(R.string.enter_to_delete));
            enterDialogWidget.setHintVisibility(View.GONE);

            enterDialogWidget.showEnterDialog();

            maintenance_orderinfo_enter_layout.removeAllViews();

            RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            maintenance_orderinfo_enter_layout.addView(enterDialogWidget.getView(),params);
            maintenance_orderinfo_enter_layout.setVisibility(View.VISIBLE);
        }
    }

   /* public void updatePlateColor(String color){
        if(platenumber_color != null){
            platenumber_color.setText(color);
            platenumber_color.setTextColor(context.getResources().getColorStateList(R.color.creat_maintenance_title_text_color));
        }
    }*/

    /*public String getPlateColor(){

        if(platenumber_color != null) {

            String platenumberColor = platenumber_color.getText().toString().trim();

            if (TextUtils.isEmpty(platenumberColor) || platenumberColor.equals(context.getString(R.string.maintenance_order_hint_platenumber_color))) {
                return "";
            }

            return platenumberColor;
        }

        return "";
    }*/

    public void updateAppointmentTime(String time){

        if(appointment_time_value != null){
            appointment_time_value.setText(time);
            appointment_time_value.setTextColor(context.getResources().getColorStateList(R.color.creat_maintenance_title_text_color));
        }

    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(orderinfo_layout_outmost != null) {
            orderinfo_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    public void setCreatOrderListener(CreatMaintenanceOrderListener creatOrderListener){
        this.creatOrderListener = creatOrderListener;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){
            case R.id.create_order_createbuttom:
                commitData();
                break;
            case R.id.commit_create_maintenance_order:
                String orderNumber = create_order_text_ordernumber.getText().toString().trim();
                if(isDebug)System.out.println("commit_create_maintenance_order  : "+ orderNumber);
                if(TextUtils.isEmpty(orderNumber)){
                    ToastUtil.showShort(context.getString(R.string.create_maintenance_order_hint));
                    return;
                }
                commitOrder();
                break;
        }

    }

    private void commitOrder(){
        //      TODO　load data  commit maintenanceOrderInfoBean
        showEnterView();
    }

    private void commitData(){
        platenumber = platenumber_value.getText().toString().trim();

        if(TextUtils.isEmpty(platenumber)){
            ToastUtil.showShort(context.getString(R.string.creat_maintenance_order_error_platenumber_null));
            return;
        }

        proposer = proposer_value.getText().toString().trim();

        if(TextUtils.isEmpty(proposer)){
            ToastUtil.showShort(context.getString(R.string.creat_maintenance_order_error_proposer_null));
            return;
        }

        contactNumber = contact_number_value.getText().toString().trim();

        if(TextUtils.isEmpty(contactNumber)){
            ToastUtil.showShort(context.getString(R.string.creat_maintenance_order_error_contact_number_null));
            return;
        }

        appointmentTime = appointment_time_value.getText().toString().trim();

        appointmentLocation = appointment_location_value.getText().toString().trim();
        errordescribe = creat_maintenance_order_errordescribe.getText().toString().trim();


        //TODO commit data to server;
        ArrayList<String> imgPathList = new ArrayList<>();
        if(imgPath.size()>1) {
            imgPathList.addAll(imgPath);
            imgPathList.remove("photo");
        }

        presenter.CreatMaintenanceOrder(platenumber, proposer, contactNumber, appointmentTime, appointmentLocation, errordescribe);
        /*if(isDebug){

            String orderNumber = ""+System.currentTimeMillis();

            CreateMaintenanceInfoDB createMaintenanceInfoDB = new CreateMaintenanceInfoDB();

            createMaintenanceInfoDB.setOrderNumber(orderNumber);
            createMaintenanceInfoDB.setProposer(proposer);
            createMaintenanceInfoDB.setContactNumber(contactNumber);
            createMaintenanceInfoDB.setPlateNumber(platenumber);
            createMaintenanceInfoDB.setRepairTime(TimeUtil.getTiemString(new Date(System.currentTimeMillis())));
            createMaintenanceInfoDB.setAppointmentTime(appointmentTime);
            createMaintenanceInfoDB.setAppointmentLocation(appointmentLocation);
            createMaintenanceInfoDB.setProblemDescription(errordescribe);

            DBManager.insertCreateMaintenanceInfo(createMaintenanceInfoDB);

            //保持工单
            BaseOrderInfoDB baseOrderInfoDB = new BaseOrderInfoDB(UserInfo.getInstance().getUserName(), context.getString(R.string.order_name_maintenance), orderNumber,
                    (byte)0, proposer, contactNumber, proposer);

            List<CreateMaintenanceInfoDB> createMaintenanceInfoDBs = DBManager.querySelectCreateMaintenanceInfo(orderNumber);

            if(createMaintenanceInfoDBs != null && createMaintenanceInfoDBs.size()>0) {
                baseOrderInfoDB.setData(createMaintenanceInfoDBs.get(0));
            }

            DBManager.insertBaseOrderInfo(baseOrderInfoDB);

            create_order_text_ordernumber.setText(context.getString(R.string.create_maintenance_order_ordernumber, orderNumber));

        }else {
            presenter.CreatMaintenanceOrder(platenumber, proposer, contactNumber, appointmentTime, appointmentLocation, errordescribe);
        }*/

    }

    private void showEnterView(){
        if(maintenance_orderinfo_enter_layout != null) {

            maintenance_orderinfo_enter_layout.removeAllViews();

            maintenance_orderinfo_enter_layout.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) maintenance_orderinfo_enter_layout.getLayoutParams();
            layoutParams.setMargins(0,0,0,0);

            enterDialogWidget.showEnterDialog();

            enterDialogWidget.setTitle(context.getString(R.string.creat_maintenance_enter_commit));

            enterDialogWidget.setHint(context.getString(R.string.creat_maintenance_enter_commit_hint));

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            maintenance_orderinfo_enter_layout.addView(enterDialogWidget.getView(), params);
        }

        if(creatOrderListener != null){
            creatOrderListener.updateDialogFocusable(false);
        }
    }

    private void showLoading(){
        if(maintenance_orderinfo_enter_layout != null) {
            maintenance_orderinfo_enter_layout.removeAllViews();

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) maintenance_orderinfo_enter_layout.getLayoutParams();
            layoutParams.setMargins(0,0,0,0);
            maintenance_orderinfo_enter_layout.setVisibility(View.VISIBLE);

            enterDialogWidget.showLoading();

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            maintenance_orderinfo_enter_layout.addView(enterDialogWidget.getView(), params);
        }

        if(creatOrderListener != null){
            creatOrderListener.updateDialogFocusable(false);
        }
    }

    private String getOrderNumber(){
        String orderNumber;
        String orderNumberStr = create_order_text_ordernumber.getText().toString().trim();

        if(TextUtils.isEmpty(orderNumberStr)){
            return null;
        }

        String[] split = orderNumberStr.split("：");
        if(split.length>1){
            orderNumber = split[1];
        }else{
            orderNumber = split[0];
        }

        return orderNumber;
    }

    private void canclePictureMenuView(){
        dismissEnterLayout();
    }

    private void showPictureMenuView(){

        if(create_order_text_ordernumber == null || TextUtils.isEmpty(create_order_text_ordernumber.getText().toString().trim())){
            ToastUtil.showShort(context.getString(R.string.create_maintenance_order_add_picture_hint));
            return ;
        }

        if(maintenance_orderinfo_enter_layout != null) {

            maintenance_orderinfo_enter_layout.removeAllViews();

            //工单名
            String orderName = CarMonitorApplication.getInstance().getString(R.string.order_name_maintenance);

            PictureCtrlWidget pictureCtrlWidget = new PictureCtrlWidget(context, orderName, getOrderNumber(), null, imgPath, context.getString(R.string.create_maintenance_img), presenter, new GetPictureListener() {
                @Override
                public void OnResult(String filePath) {
                    dismissEnterLayout();
                    imgPath.add(0, filePath);
                    addPicturesImgGridAdapter.initData(imgPath);

                    updataImgPathToDB();
                }

                @Override
                public void OnCancle() {
                    canclePictureMenuView();
                }

                @Override
                public void OnLoading() {
                    showLoading();
                }
            });

            RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

            maintenance_orderinfo_enter_layout.addView(pictureCtrlWidget.getView(), params);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) maintenance_orderinfo_enter_layout.getLayoutParams();
            layoutParams.setMargins(0,0,0,0);

            maintenance_orderinfo_enter_layout.setVisibility(View.VISIBLE);
        }

        if(creatOrderListener != null){
            creatOrderListener.updateDialogFocusable(false);
        }

    }

    private void updataImgPathToDB(){
        //保持图片路径信息到数据库
        BaseOrderInfoDB baseOrderInfoDB = DBManager.querySelectBaseOrderInfo(UserInfo.getInstance().getUserName(), getOrderNumber());
        if(baseOrderInfoDB != null) {
            CreateMaintenanceInfoDB data = baseOrderInfoDB.getData();
            List<String> pathList = new ArrayList<>();
            pathList.addAll(imgPath);
            pathList.remove("photo");
            String pathStr = JsonUtil.stringListToJsonString(pathList);
            data.setPathjsonStr(pathStr);

            DBManager.insertCreateMaintenanceInfo(data);
        }
    }

    @Override
    public void onLoading() {
        showLoading();
    }

    @Override
    public void showContentView(BaseVo dataVo) {

        if(mSDCardListeren != null){
            mSDCardListeren.stopWatching();
        }

        String orderNumber = null;

        if(dataVo instanceof CreateOrUpdateOrderVo){
            CreateOrUpdateOrderVo mCreateOrUpdateOrderVo = (CreateOrUpdateOrderVo) dataVo;
            orderNumber = mCreateOrUpdateOrderVo.getResultOrderNumber();
            create_order_text_ordernumber.setText(context.getString(R.string.create_maintenance_order_ordernumber, orderNumber));

            disableEdit();

        }else if(dataVo instanceof MaintenanceApplyVo) {
            MaintenanceApplyVo mMaintenanceApplyVo = (MaintenanceApplyVo) dataVo;
            orderNumber = mMaintenanceApplyVo.getOrderNumber();
            if (!TextUtils.isEmpty(orderNumber)) {
                creatOrderListener.onSuccessCommitData(orderNumber);
            } else {
                ToastUtil.showShort(context.getString(R.string.upload_maintenance_commit_fail));
            }
        }
    }

    @Override
    public void onStopLoading() {
        dismissEnterLayout();

    }

    public boolean onkeyBack(){
        if(maintenance_orderinfo_enter_layout != null && (maintenance_orderinfo_enter_layout.getVisibility() == View.VISIBLE)){
//            dismissEnterLayout();
            canclePictureMenuView();
            return true;
        }

        if(mSDCardListeren != null){
            mSDCardListeren.stopWatching();
        }

        deleteFielpicture();

        return false;
    }

    public void deleteFielpicture(){
        if(imgPath.size()>1){
            for(String filePath : imgPath){
                File file = new File(filePath);
                boolean exists = file.exists();
                if(exists){
                    file.delete();
                }
            }
        }
    }

    private void dismissEnterLayout(){
        if(creatOrderListener != null){
            creatOrderListener.updateDialogFocusable(true);
        }
        if(maintenance_orderinfo_enter_layout != null) {
            maintenance_orderinfo_enter_layout.setVisibility(View.GONE);
            maintenance_orderinfo_enter_layout.removeAllViews();
        }

    }

    private void disableEdit(){

        create_order_createbuttom.setOnClickListener(null);
        create_order_createbuttom.setBackgroundResource(R.drawable.create_order_buttom_bg_n);
        create_order_createbuttom.setTextColor(context.getResources().getColor(R.color.creat_maintenance_createbutton_off_color));

        platenumber_value.setFocusable(false);
        platenumber_value.setClickable(false);
        platenumber_value.setOnClickListener(null);

        proposer_value.setClickable(false);
        proposer_value.setFocusable(false);

        contact_number_value.setClickable(false);
        contact_number_value.setFocusable(false);

        appointment_time_value.setClickable(false);
        appointment_time_value.setFocusable(false);

        appointment_location_value.setClickable(false);
        appointment_location_value.setFocusable(false);

        creat_maintenance_order_errordescribe.setClickable(false);
        creat_maintenance_order_errordescribe.setFocusable(false);

    }
}
