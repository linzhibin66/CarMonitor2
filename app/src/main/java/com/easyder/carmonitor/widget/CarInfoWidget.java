package com.easyder.carmonitor.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.CarInfoLayoutListener;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.UDPRequestCtrl;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.CarBaseInfoItem;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.CarCostVo;
import com.shinetech.mvp.network.UDP.listener.BaseCarInfoListener;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.presenter.BaseCarInfoPresenter;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarBaseInfoBean;
import com.shinetech.mvp.utils.CoordinateUtil;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ljn on 2017-04-01.
 */
public class CarInfoWidget implements OnGetGeoCoderResultListener {

    /**
     * 返回按键
     */
    private ImageButton title_back;

    /**
     * 标题
     */
    private TextView title_text;

    /**
     * 搜索按钮
     */
    private ImageButton title_search;

    private ScrollView carinfo_scrollview;

    private Context context;

    /**
     * 跟布局
     */
    private View root_layout;

    /**
     * 车辆信息
     */
    private CarInfoBean mCarInfoBean;

    /**
     * 车牌号
     */
    private CarInfoItem platenumber;

    /**
     * 车辆费用
     */
    private CarInfoItem carcost;

    /**
     * 更多状态
     */
    private CarInfoItem carinfo_status;

    /**
     * 报警标志
     */
    private CarInfoItem carinfo_alarms;

    /**
     * 定位时间
     */
    private CarInfoItem carinfo_time;

    /**
     * 时速
     */
    private CarInfoItem carinfo_speed;

    /**
     * 方向
     */
    private CarInfoItem carinfo_orientation;

    /**
     * 海拔
     */
    private CarInfoItem carinfo_elevation;

    /**
     * 里程
     */
    private CarInfoItem carinfo_mileage;

    /**
     * 地址
     */
    private CarInfoItem carinfo_adress;

    /**
     * 安装时间
     */
    private CarInfoItem carinfo_install_time;

    /**
     * 缴费时间
     */
    private CarInfoItem carinfo_pay_time;

    /**
     * 有效期
     */
    private CarInfoItem carinfo_validity_time;

    /**
     * 公司名称
     */
    private CarInfoItem carinfo_company_name;

    /**
     * 分组名称
     */
    private CarInfoItem carinfo_group_name;

    /**
     * 联系人
     */
    private CarInfoItem carinfo_contact;

    /**
     * 联系电话
     */
    private CarInfoItem carinfo_phone;

    private ReverseGeoCodeOption reverseGeoCodeOption;

    //地址解析类
    private GeoCoder geoCoder;

    private CarInfoLayoutListener mCarInfoLayoutListener;

    private ProgressBar progressbar;

    private String adress;

    private LinearLayout carinfo_items;

    private  RelativeLayout carinfo_layout_outmost;

    private  BaseCarInfoPresenter baseCarInfoPersenter;

    public CarInfoWidget(Context context, CarInfoLayoutListener mCarInfoLayoutListener) {
        this.context = context;
        this.mCarInfoLayoutListener = mCarInfoLayoutListener;

        root_layout = View.inflate(context, R.layout.carinfo_activity, null);

        initView();
    }

    public CarInfoWidget(Context context, View view, CarInfoLayoutListener mCarInfoLayoutListener) {
        this.context = context;
        this.mCarInfoLayoutListener = mCarInfoLayoutListener;

        root_layout = view;

        initView();
    }

    private void initView(){
        initTitle(root_layout);
        initItems(root_layout);

        progressbar = (ProgressBar) root_layout.findViewById(R.id.progressbar);
        carinfo_items = (LinearLayout) root_layout.findViewById(R.id.carinfo_items);
        carinfo_layout_outmost = (RelativeLayout) root_layout.findViewById(R.id.carinfo_layout_outmost);
        carinfo_scrollview = (ScrollView) root_layout.findViewById(R.id.carinfo_scrollview);

        initGeoCoder();
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(carinfo_layout_outmost != null) {
            carinfo_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    private void initTitle(View rootView) {

        title_back = (ImageButton) rootView.findViewById(R.id.title_back);
        title_text = (TextView) rootView.findViewById(R.id.title_text);
        title_search = (ImageButton) rootView.findViewById(R.id.title_search);

        title_text.setText(context.getString(R.string.carinfo_title));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCarInfoLayoutListener != null) {
                    mCarInfoLayoutListener.onBack();
                }
            }
        });

        title_search.setVisibility(View.VISIBLE);
        title_search.setImageResource(R.mipmap.status_refresh);
        title_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Animation animation = AnimationUtils.loadAnimation(context, R.anim.refresh_anim);
                title_search.startAnimation(animation);
                CarInfoBean newestCarInfo = UserInfo.getInstance().getNewestCarInfo(mCarInfoBean.getPlateNumber());
                if(newestCarInfo != null) {
                    upData(newestCarInfo, adress);
                }
            }
        });

        RelativeLayout title_layout = (RelativeLayout) rootView.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }

    private void initItems(View rootView) {
        platenumber = (CarInfoItem) rootView.findViewById(R.id.carinfo_platenumber);

        carcost = (CarInfoItem) rootView.findViewById(R.id.carcost);

        carinfo_status = (CarInfoItem) rootView.findViewById(R.id.carinfo_status);
        carinfo_alarms = (CarInfoItem) rootView.findViewById(R.id.carinfo_alarms);

        carinfo_time = (CarInfoItem) rootView.findViewById(R.id.carinfo_time);
        carinfo_speed = (CarInfoItem) rootView.findViewById(R.id.carinfo_speed);
        carinfo_orientation = (CarInfoItem) rootView.findViewById(R.id.carinfo_orientation);
        carinfo_elevation = (CarInfoItem) rootView.findViewById(R.id.carinfo_elevation);
        carinfo_mileage = (CarInfoItem) rootView.findViewById(R.id.carinfo_mileage);
        carinfo_adress = (CarInfoItem) rootView.findViewById(R.id.carinfo_adress);

        carinfo_install_time = (CarInfoItem) rootView.findViewById(R.id.carinfo_install_time);
        carinfo_pay_time = (CarInfoItem) rootView.findViewById(R.id.carinfo_pay_time);
        carinfo_validity_time = (CarInfoItem) rootView.findViewById(R.id.carinfo_validity_time);
        carinfo_company_name = (CarInfoItem) rootView.findViewById(R.id.carinfo_company_name);
        carinfo_group_name = (CarInfoItem) rootView.findViewById(R.id.carinfo_group_name);
        carinfo_contact = (CarInfoItem) rootView.findViewById(R.id.carinfo_contact);
        carinfo_phone = (CarInfoItem) rootView.findViewById(R.id.carinfo_phone);
    }

    public View getView() {
        return root_layout;
    }

    public void setCarInfoLayoutListener(CarInfoLayoutListener listener) {
        mCarInfoLayoutListener = listener;
    }

    public void upData(CarInfoBean mCarInfoBean, String adress) {

        if(mCarInfoBean == null){
            return;
        }
        this.mCarInfoBean = mCarInfoBean;
        this.adress = adress;

        //      根据坐标解析地址
        double lng = mCarInfoBean.getLng() / 1E6;
        double lat = mCarInfoBean.getLat() / 1E6;

        if(lng != 0 || lat != 0){
            LatLng latLng = CoordinateUtil.gps84_to_bd09(lat, lng);

            reverseGeoCodeOption.location(latLng);
            geoCoder.reverseGeoCode(reverseGeoCodeOption);
        }

        if(MainApplication.isUserResponseUDP){
            initResponseCarBaseInfo(mCarInfoBean.getPlateNumber());
        }else {
            initBaseCarInfo(mCarInfoBean.getPlateNumber());
        }

    }

    /**
     * 初始化地址解析类
     */
    private void initGeoCoder(){
        //地址反向编码
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);
        reverseGeoCodeOption = new ReverseGeoCodeOption();
    }

    private void initResponseCarBaseInfo(String plateNumber){

        ResponseCarBaseInfoBean carBaseInfoBean = UserInfo.getInstance().getCarBaseInfo(plateNumber);
        if(carBaseInfoBean != null){
            List<CarBaseInfoItem> responseCarBaseInfoList = createResponseCarBaseInfoList(carBaseInfoBean);
            initCarInfo(responseCarBaseInfoList);
//            progressbar.setVisibility(View.VISIBLE);
            getCarCost(plateNumber);
            return;
        }

        //如果没找到，自己请求一次
        progressbar.setVisibility(View.VISIBLE);

        ResponseCarBaseInfoBean responseCarBaseInfoBean = new ResponseCarBaseInfoBean(plateNumber);

        UDPRequestCtrl.getInstance().request(responseCarBaseInfoBean, new ResponseListener() {

            @Override
            public void onSuccess(LoadResult successResult) {
                BaseVo dataVo = successResult.getDataVo();
                if (dataVo != null && dataVo instanceof ResponseCarBaseInfoBean) {
                    ResponseCarBaseInfoBean mResponseCarBaseInfoBean = (ResponseCarBaseInfoBean) dataVo;

                    List<CarBaseInfoItem> responseCarBaseInfoList = createResponseCarBaseInfoList(mResponseCarBaseInfoBean);
                    initCarInfo(responseCarBaseInfoList);

                    getCarCost(mCarInfoBean.getPlateNumber());

                } else   if (dataVo != null && dataVo instanceof CarCostVo) {
//                    LogUtils.error("getCarBaseListResponse dataVo is null  ");
                    ToastUtil.showShort("获取数据失败");
                }

                progressbar.setVisibility(View.GONE);

            }

            @Override
            public void onError(LoadResult errorResult) {
//                LogUtils.error("getCarBaseListResponse onError : " + errorResult.getMessage());
                ToastUtil.showShort(errorResult.getMessage());
                progressbar.setVisibility(View.GONE);
            }
        });



    }

    private void getCarCost(String plateNumber){

        CarCostVo carCostVo = new CarCostVo(plateNumber);

        UDPRequestCtrl.getInstance().request(carCostVo, new ResponseListener() {

            @Override
            public void onSuccess(LoadResult successResult) {
                BaseVo dataVo = successResult.getDataVo();
                if(dataVo instanceof CarCostVo){
                    CarCostVo mCarCostVo = (CarCostVo) dataVo;
                    int allArrearage = mCarCostVo.getAllArrearage();
                    if(allArrearage>0){
                        setCarCost(""+(allArrearage));
                    }
                }

                progressbar.setVisibility(View.GONE);

            }

            @Override
            public void onError(LoadResult errorResult) {
                progressbar.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 创建车辆基础信息列表（适用于响应式UDP接口）
     * @param mResponseCarBaseInfoBean
     * @return
     */
    private  List<CarBaseInfoItem> createResponseCarBaseInfoList(ResponseCarBaseInfoBean mResponseCarBaseInfoBean){
        List<CarBaseInfoItem> resultCarInfoItem = mResponseCarBaseInfoBean.getResultCarInfoItem();
        //添加所属公司到list
        String subordinateCompanies = mResponseCarBaseInfoBean.getSubordinateCompanies();
        if(!TextUtils.isEmpty(subordinateCompanies)){
            CarBaseInfoItem carBaseInfoItemCompany = new CarBaseInfoItem(context.getString(R.string.carinfo_company_name), subordinateCompanies);
            resultCarInfoItem.add(carBaseInfoItemCompany);
        }

        //添加所属分组到list
        String subordinateGroup = mResponseCarBaseInfoBean.getSubordinateGroup();
        if(!TextUtils.isEmpty(subordinateGroup)){
            CarBaseInfoItem carBaseInfoItemGroup = new CarBaseInfoItem(context.getString(R.string.carinfo_group_name), subordinateGroup);
            resultCarInfoItem.add(carBaseInfoItemGroup);
        }

        return resultCarInfoItem;
    }

    private void initBaseCarInfo(String plateNumber) {

        if(baseCarInfoPersenter == null) {
            baseCarInfoPersenter = new BaseCarInfoPresenter();
        }

        progressbar.setVisibility(View.VISIBLE);
//        carinfo_items.setVisibility(View.GONE);

        baseCarInfoPersenter.getBaseCarinfo(plateNumber, new BaseCarInfoListener() {
            @Override
            public void OnSuccess(List<CarBaseInfoItem> list) {
                initCarInfo(list);
                progressbar.setVisibility(View.GONE);
//                carinfo_items.setVisibility(View.VISIBLE);
            }

            @Override
            public void OnError(String message) {
                ToastUtil.showShort(message);
                progressbar.setVisibility(View.GONE);
//                carinfo_items.setVisibility(View.VISIBLE);
            }
        });
    }

    public void cleanlayout(){
        carinfo_scrollview.fullScroll(ScrollView.FOCUS_UP);
        platenumber.setValues("");
        carcost.setValues("");
        carinfo_status.setClickListener(null);
//        carinfo_alarms.setClickListener(null);
        carinfo_alarms.setValues("");
        carinfo_time.setValues("");
        carinfo_speed.setValues("");
        carinfo_orientation.setValues("");
        carinfo_elevation.setValues("");
        carinfo_mileage.setValues("");
        carinfo_adress.setValues("");
        carinfo_install_time.setValues("");
        carinfo_pay_time.setValues("");
        carinfo_validity_time.setValues("");
        carinfo_company_name.setValues("");
        carinfo_group_name.setValues("");
        carinfo_contact.setValues("");
        carinfo_phone.setValues("");
    }


    private void setCarCost(String money){
        String moneyStr = "￥"+money+"元";

        String text = "总欠费"+moneyStr;
        int index = text.indexOf(moneyStr);
        SpannableStringBuilder style=new SpannableStringBuilder(text);
//        style.setSpan(new BackgroundColorSpan(Color.RED),2,5, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);     //设置指定位置textview的背景颜色
        style.setSpan(new ForegroundColorSpan(Color.RED),index,moneyStr.length()+index,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);     //设置指定位置文字的颜色
        carcost.setValues(style);
    }

    private void initCarInfo(List<CarBaseInfoItem> list) {

        platenumber.setValues(mCarInfoBean.getPlateNumber());

        carinfo_status.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.showShort("carinfo_status");
                if (mCarInfoLayoutListener != null) {
                    mCarInfoLayoutListener.OnClickMoreStatus(mCarInfoBean);
                }

            }
        });

        carcost.showArrow(true);
        carcost.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCarInfoLayoutListener != null) {
                    mCarInfoLayoutListener.OnClickCarcost(mCarInfoBean);
                }

            }
        });

       /* carinfo_alarms.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.showShort("carinfo_alarms");
                if (mCarInfoLayoutListener != null) {
                    mCarInfoLayoutListener.OnClickAlarm(mCarInfoBean);
                }
            }
        });*/

        if(mCarInfoBean.getViolationCount()>0){
            List<String> violationList = mCarInfoBean.getViolationList();
            String str = "";
            for(int i = 0; i<violationList.size(); i++){
                if((violationList.size() - 1) == i){
                    str += violationList.get(i);
                }else{
                    str += violationList.get(i)+",";
                }
            }
            carinfo_alarms.setValues(str);
            carinfo_alarms.setVisibility(View.VISIBLE);
            carinfo_status.setLine(true);
        }else{
            carinfo_alarms.setVisibility(View.GONE);
            carinfo_status.setLine(false);
        }

        if(mCarInfoBean.getLat() == 0 && mCarInfoBean.getLng() == 0){
            carinfo_time.setValues("");
        }else{
            carinfo_time.setValues(mCarInfoBean.getLocationTime());
        }

        carinfo_speed.setValues(mCarInfoBean.getSpeed() + "km/h");

        carinfo_orientation.setValues(getOrientationImage(mCarInfoBean.getOrientation()));

        carinfo_elevation.setValues(mCarInfoBean.getAltitude() + "米");
        carinfo_mileage.setValues((mCarInfoBean.getMileage()/10f)+"KM");

        carinfo_adress.setValues(adress);

        initBaseCarInfo(list);

        /*if (list.size() > 0) {
            carinfo_install_time.setValues(list.get(0).getInfoContent());
        }

        if (list.size() > 1) {
            carinfo_pay_time.setValues(list.get(1).getInfoContent());
        }

        if (list.size() > 2) {
            carinfo_validity_time.setValues(list.get(2).getInfoContent());
        }

        if (list.size() > 3) {
            carinfo_company_name.setValues(list.get(3).getInfoContent());
        }

        if (list.size() > 4) {
            carinfo_contact.setValues(list.get(4).getInfoContent());
        }

        if (list.size() > 5) {
            carinfo_phone.setValues(list.get(5).getInfoContent());
        }
*/

    }

    private void initBaseCarInfo(List<CarBaseInfoItem> list){

        for(CarBaseInfoItem item : list){
            String infoName = item.getInfoName();
            CarInfoItem carInfoItem = getCarInfoItem(infoName);
            if(carInfoItem != null){
                carInfoItem.setValues(item.getInfoContent());
                carInfoItem.setVisibility(View.VISIBLE);
            }
        }

    }

    private CarInfoItem getCarInfoItem(String title){

        if(title.equals(context.getString(R.string.carinfo_install_time))){
//            carinfo_install_time.setVisibility(View.VISIBLE);
            return carinfo_install_time;
        }/*else{
            carinfo_install_time.setVisibility(View.GONE);
        }*/

        if(title.equals(context.getString(R.string.carinfo_pay_time))){
            return carinfo_pay_time;
        }

        if(title.equals(context.getString(R.string.carinfo_validity_time))){
            return carinfo_validity_time;
        }

        if(title.equals(context.getString(R.string.carinfo_company_name))){
            return carinfo_company_name;
        }

        if(title.equals(context.getString(R.string.carinfo_group_name))){
            return carinfo_group_name;
        }

        if(title.equals(context.getString(R.string.carinfo_contact))){
            return carinfo_contact;
        }

      if(title.equals(context.getString(R.string.carinfo_phone))){
                return carinfo_phone;
            }
        return null;



    }

    public String getOrientationImage(short orientation){

        orientation = (short) (orientation%360);

        if((orientation>(360-22.5)) && orientation <= 22.5){
            return context.getString(R.string.north);
        }else if(orientation>22.5 && (orientation <= (45+22.5))){
            return context.getString(R.string.northeast);
        }else if(orientation>(45+22.5) && (orientation <= (90+22.5))){
            return context.getString(R.string.east);
        }else if(orientation>(90+22.5) && (orientation <= (135+22.5))){
            return context.getString(R.string.southeast);
        }else if(orientation>(135+22.5) && (orientation <= (180+22.5))){
            return context.getString(R.string.south);
        }else if(orientation>(180+22.5) && (orientation <= (225+22.5))){
            return context.getString(R.string.southwest);
        }else if(orientation>(225+22.5) && (orientation <= (270+22.5))){
            return context.getString(R.string.west);
        }else if(orientation>(270+22.5) && (orientation <= (315+22.5))){
            return context.getString(R.string.northwest);
        }

        return context.getString(R.string.north);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

        String address = context.getString(R.string.marker_address_error);
        if (!TextUtils.isEmpty(reverseGeoCodeResult.getAddress())){
            address = reverseGeoCodeResult.getAddress();
        }
        adress = address;

//        if(TextUtils.isEmpty(carinfo_adress.getValues())){
            carinfo_adress.setValues(adress);
//        }
    }
}
