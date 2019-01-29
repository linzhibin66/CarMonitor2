package com.easyder.carmonitor.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.activity.MainActivity;
import com.easyder.carmonitor.dialog.AlarmLogDialog;
import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.dialog.orderDialog.CreatMaintenanceOrderDialog;
import com.easyder.carmonitor.dialog.FAQDialog;
import com.easyder.carmonitor.dialog.orderDialog.InstallOrderInfoDialog;
import com.easyder.carmonitor.dialog.orderDialog.MaintenanceOrderInfoDialog;
import com.easyder.carmonitor.dialog.orderDialog.OrderManagerDialog;
import com.easyder.carmonitor.dialog.orderDialog.UpLoadMaintenanceResultDialog;
import com.easyder.carmonitor.dialog.UserMenuDialog;
import com.easyder.carmonitor.dialog.markerShowScheme.BaseMarkerScheme;
import com.easyder.carmonitor.dialog.markerShowScheme.CompactMarkerDialog;
import com.easyder.carmonitor.dialog.markerShowScheme.CompactMarkerScheme;
import com.easyder.carmonitor.dialog.SearchDilaog;
import com.easyder.carmonitor.dialog.markerShowScheme.OriginalMarkerScheme;
import com.easyder.carmonitor.interfaces.SearchDialogClickListeren;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.interfaces.BaseUpdateBaiduMarker;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.CarLatestStatus;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.presenter.MainPresenter;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarStatusInfoBean;
import com.shinetech.mvp.utils.BaiduMapUtils;
import com.shinetech.mvp.utils.CoordinateUtil;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ljn on 2017-03-29.
 */
public abstract class MainActivityPresenter extends MainPresenter {

    protected Context context;

    private BaseMarkerScheme showMarkerScheme;


    private static List<BasePopupWindowDialog> showDialogList = new ArrayList<>();

    /**
     * 当前地图显示范围
     */
    protected int[] mapViewScope;

    protected boolean isDebug = false && LogUtils.isDebug;

    private SearchDilaog searchDilaog;

    // 用户图标点击弹框
    private UserMenuDialog mUserMenuDialog;

    private boolean isUseOriginalScheme = false;

    private boolean isNeedRecoverMarkerDialog = false;

    public MainActivityPresenter(Context context, BaseUpdateBaiduMarker markerScheme) {
        super(markerScheme);
        this.context = context;
        if(isUseOriginalScheme) {
            //旧界面
            showMarkerScheme = new OriginalMarkerScheme(context, this);
        }else {
            //新界面
            showMarkerScheme = new CompactMarkerScheme(context, this);
        }
    }

    /**
     * 显示Marker的详细信息
     *
     * @param bmapView
     * @param plateNumber 车牌号
     * @return
     */
    public boolean showMarkerDialog(MapView bmapView, String plateNumber) {

        CarInfoBean carInfoBean = UserInfo.getInstance().getNewestCarInfo(plateNumber);

        if (carInfoBean == null) {
            ToastUtil.showShort(context.getString(R.string.marker_info_error));
            return false;
        }

//        int alarmType = carInfoBean.getAlarmType();
//        System.out.println("getAlarmType : "+alarmType +" isExceptionp : "+ AlarmInfoTool.isExceptionp(alarmType));

        return showMarkerDialog(bmapView, carInfoBean);

    }

    /**
     * 显示Marker的详细信息
     *
     * @param bmapView
     * @param carInfoBean 车辆信息
     * @return
     */
    public boolean showMarkerDialog(final MapView bmapView, CarInfoBean carInfoBean) {
        if(showMarkerScheme != null) {
            isNeedRecoverMarkerDialog = false;
            return showMarkerScheme.showMarkerDialog(bmapView, carInfoBean, new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    resetCurrentClickMarker();
                }
            });
        }
        return false;

    }

    public static void addDialogOfManager(BasePopupWindowDialog mdialog){
        synchronized (showDialogList) {
            showDialogList.add(mdialog);
        }
    }

    public static void removeDailogOfManager(BasePopupWindowDialog mdialog){
        synchronized (showDialogList) {
            showDialogList.remove(mdialog);
        }
    }

    public void updateDialogViewOfInputMethod(int usableHeightNow, View mChildOfContent, FrameLayout.LayoutParams frameLayoutParams){
        synchronized (showDialogList) {
            if (showDialogList.size() > 0) {
                BasePopupWindowDialog basePopupWindowDialog = showDialogList.get((showDialogList.size() - 1));

                if(basePopupWindowDialog == null) {
                    return;
                }

                if(basePopupWindowDialog instanceof CompactMarkerDialog){
                    return;
                }

                if(basePopupWindowDialog instanceof SearchDilaog){
                    int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
                    int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                    if (heightDifference > (usableHeightSansKeyboard/4)) {
                        // keyboard probably just became visible
                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                    } else {
                        // keyboard probably just became hidden
                        frameLayoutParams.height = usableHeightSansKeyboard;
                    }
                    mChildOfContent.requestLayout();
                    return;
                }

               /* if(basePopupWindowDialog instanceof SearchDilaog){
                    int statusBarHeight = UIUtils.getStatusBarHeight();
                    int staty_h = statusBarHeight + 20;

                    int title_h = UIUtils.dip2px(29);
                    int height =usableHeightNow - staty_h - title_h;

                    basePopupWindowDialog.setHeight(height);
                    basePopupWindowDialog.update(ViewGroup.LayoutParams.MATCH_PARENT, height);

                    return;
                }*/

                if(usableHeightNow == UIUtils.getScreentHeight()) {
                    basePopupWindowDialog.setHeight((usableHeightNow - UIUtils.getNavigationBarHeight()));
                    basePopupWindowDialog.update(ViewGroup.LayoutParams.MATCH_PARENT, (usableHeightNow - UIUtils.getNavigationBarHeight()));
                }else{
                    basePopupWindowDialog.setHeight(usableHeightNow);
                    basePopupWindowDialog.update(ViewGroup.LayoutParams.MATCH_PARENT, usableHeightNow);
                }
            }

        }
    }

    public boolean onDismissDialog(){
        synchronized (showDialogList) {
            if (showDialogList.size() > 0) {
                BasePopupWindowDialog basePopupWindowDialog = showDialogList.get((showDialogList.size() - 1));

                if(basePopupWindowDialog instanceof AlarmLogDialog){
                    AlarmLogDialog mAlarmLogDialog = (AlarmLogDialog) basePopupWindowDialog;
                    boolean b = mAlarmLogDialog.onKeyBack();
                    if(b){
                       return true;
                    }
                }

                if(basePopupWindowDialog instanceof FAQDialog){
                    FAQDialog mFAQDialog = (FAQDialog) basePopupWindowDialog;
                    boolean b = mFAQDialog.onKeyBack();
                    if(b){
                        return true;
                    }
                }

                if(basePopupWindowDialog instanceof CreatMaintenanceOrderDialog){
                    if(isDebug)System.out.println("onDismissDialog -----------CreatMaintenanceOrderDialog----------");
                    CreatMaintenanceOrderDialog mCreatMaintenanceOrderDialog = (CreatMaintenanceOrderDialog) basePopupWindowDialog;
                    boolean b = mCreatMaintenanceOrderDialog.onKeyBack();
                    if(b){
                        return true;
                    }
                }


                if(basePopupWindowDialog instanceof UpLoadMaintenanceResultDialog){
                    if(isDebug)System.out.println("onDismissDialog -----------UpLoadMaintenanceResultDialog----------");
                    UpLoadMaintenanceResultDialog mUpLoadMaintenanceResultDialog = (UpLoadMaintenanceResultDialog) basePopupWindowDialog;
                    boolean b = mUpLoadMaintenanceResultDialog.onKeyBack();
                    if(b){
                        return true;
                    }
                }

                if(basePopupWindowDialog instanceof MaintenanceOrderInfoDialog){
                    if(isDebug)System.out.println("onDismissDialog -----------MaintenanceOrderInfoDialog----------");
                    MaintenanceOrderInfoDialog mMaintenanceOrderInfoDialog = (MaintenanceOrderInfoDialog) basePopupWindowDialog;
                    boolean b = mMaintenanceOrderInfoDialog.onKeyBack();
                    if(b){
                        return true;
                    }
                }

                if(basePopupWindowDialog instanceof InstallOrderInfoDialog){
                    if(isDebug)System.out.println("onDismissDialog -----------mInstallOrderInfoDialog----------");
                    InstallOrderInfoDialog mInstallOrderInfoDialog = (InstallOrderInfoDialog) basePopupWindowDialog;
                    boolean b = mInstallOrderInfoDialog.onKeyBack();
                    if(b){
                        return true;
                    }
                }

                if(basePopupWindowDialog instanceof OrderManagerDialog){
                    if(isDebug)System.out.println("onDismissDialog -----------OrderManagerDialog----------");
                    OrderManagerDialog mOrderManagerDialog = (OrderManagerDialog) basePopupWindowDialog;
                    boolean b = mOrderManagerDialog.onKeyBack();
                    if(b){
                        return true;
                    }
                }

                if(basePopupWindowDialog instanceof CompactMarkerDialog){
                    initRecoverMarkerDialog();
                }

                if (basePopupWindowDialog.isShowing()) {
                    basePopupWindowDialog.dismiss();
                    return true;
                }
            }
        }
        return false;
    }

    public void cleanOpenDoalogs(){
        synchronized (showDialogList) {

            List<BasePopupWindowDialog> basePopupWindowDialogs = new ArrayList<>(showDialogList);

            Iterator<BasePopupWindowDialog> iterator = basePopupWindowDialogs.iterator();
            while (iterator.hasNext()){
                BasePopupWindowDialog basePopupWindowDialog = iterator.next();
                if(basePopupWindowDialog.isShowing()){
                    basePopupWindowDialog.noAmimationDismiss();
                }
            }

        }

        if(searchDilaog!= null && searchDilaog.isShowing()){
            searchDilaog.noAmimationDismiss();
        }

        showDialogList.clear();
    }

    public boolean isMarkerCarInfoChange(CarInfoBean mCarInfoBean){
        if(showMarkerScheme != null) {
            return showMarkerScheme.isMarkerCarInfoChange(mCarInfoBean);
        }
        return false;
    }

    public boolean updateMarkerDialogInfo(CarInfoBean mCarInfoBean) {
        if(showMarkerScheme != null) {
           return showMarkerScheme.updateMarkerDialogInfo(mCarInfoBean);
        }
        return false;
    }

    /**
     * 退出Marker弹框
     */
    public void dismissMarkerDialog() {
        if(showMarkerScheme != null) {
            showMarkerScheme.dismissMarkerDialog();
        }
    }

    /**
     * Marker弹框是否显示
     *
     * @return
     */
    public boolean isShowMarkerDialog() {
        if(showMarkerScheme != null) {
            return showMarkerScheme.isShowMarkerDialog();
        }
        return false;
    }

    public void recoverMarkerDialog(){

        if(isNeedRecoverMarkerDialog && CarMonitorApplication.isUseSingleDialogMode()){
            showMarkerScheme.revoverShow();
            isNeedRecoverMarkerDialog = false;
            //设置成当前点击的marker
            String plateNumber = showMarkerScheme.getPlateNumber();
            if(!TextUtils.isEmpty(plateNumber)) {
                setCurrentClickMarker(plateNumber);
            }
        }
    }

    public void markerDialogToBackground(){
        if(isShowMarkerDialog() && CarMonitorApplication.isUseSingleDialogMode()) {
            isNeedRecoverMarkerDialog = true;
            dismissMarkerDialog();
        }
    }

    public void initRecoverMarkerDialog(){
        isNeedRecoverMarkerDialog = false;
    }

    /**
     * 获取地图中所有能显示的车辆,如果MainApplication.isUserResponseUDP == true 时会return 掉（新街口无次数据）
     */
    public void getCarOfView(Handler mHandler, BaiduMap mBaiduMap, MapView bmapView, boolean isShowLoading) {

        if (mHandler != null) {
            mHandler.removeMessages(MainActivity.REFER_MARKER);
            mHandler.sendEmptyMessageDelayed(MainActivity.REFER_MARKER, CarMonitorApplication.REFER_MARKER_TIME);
        }

        if(MainApplication.isUserResponseUDP){
            //TODO Update Marker
            updateMarkers(UserInfo.getInstance().getNewestCarInfoList());
            return;
        }

        if (bmapView == null) {
            return;
        }

        if (mBaiduMap == null) {
            mBaiduMap = bmapView.getMap();
        }

       /* if (isDebug) {

            mapViewScope = BaiduMapUtils.getMapViewScope((Activity) context, mBaiduMap);

        } else {*/

            mapViewScope = BaiduMapUtils.getMapViewScope(mBaiduMap);

//        }

        if ((mapViewScope != null) && (mapViewScope.length == 4)) {

            updateCarOfView(isShowLoading);

        }
    }

    /**
     * 获取地图中所有能显示的车辆
     */
    public abstract void updateCarOfView(boolean isShowLoading);

    /**
     * 个人用户刷新方式
     */
    protected void getPersionCarOfView(final boolean isShowLoading) {

        if(isShowLoading){
            if (isViewAttached()) {
                getView().onLoading();
            }
        }

        getCarInfo(UserInfo.getInstance().getUserName(), new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {
                if (isShowLoading) {
                    if (isViewAttached()) {
                        getView().onStopLoading();
                    }
                }

                BaseVo dataVo = successResult.getDataVo();
                if (dataVo instanceof CarLatestStatus) {
                    CarLatestStatus mCarLatestStatus = (CarLatestStatus) dataVo;
                    CarInfoBean carInfoBean = mCarLatestStatus.toCarInfoBean();
                    if ((mapViewScope != null) && (mapViewScope.length == 4)) {
                        updateMarker(carInfoBean, mapViewScope);
                    }
                }
            }

            @Override
            public void onError(LoadResult errorResult) {

                if (isShowLoading) {
                    if (isViewAttached()) {
                        getView().onStopLoading();
                    }
                }
            }
        });
    }

    /**
     * 更新单个Marker图标
     *
     * @param mCarInfoBean
     */
    public void updateMarker(CarInfoBean mCarInfoBean) {
        updateMarker(mCarInfoBean, mapViewScope);
        //更新marker弹框界面
        updateMarkerDialogInfo(mCarInfoBean);
    }

    @Override
    public void updateMarkers(Collection<CarInfoBean> carInfoList) {
        super.updateMarkers(carInfoList);

        //更新marker弹框界面
        for (CarInfoBean mCarInfoBean : carInfoList) {
            boolean update = updateMarkerDialogInfo(mCarInfoBean);
            if (update) {
                return;
            }
        }
    }

    public void cleanUnLockMarkerOnCearch() {
        cleanUNLockCar();
    }

    /**
     * 获取LatLng对象，经纬度解析
     *
     * @param mCarLatestStatus
     * @return
     */
    public LatLng getLatLng(CarLatestStatus mCarLatestStatus) {

        double lng = mCarLatestStatus.getResultlng() / 1E6;
        double lat = mCarLatestStatus.getResultlat() / 1E6;

        LatLng latLng = CoordinateUtil.gps84_to_bd09(lat, lng);

        return latLng;
    }

    /**
     * 获取LatLng对象，经纬度解析
     *
     * @param mResponseCarStatusInfoBean
     * @return
     */
    public LatLng getLatLng(ResponseCarStatusInfoBean mResponseCarStatusInfoBean) {

        double lng = mResponseCarStatusInfoBean.getResultlng() / 1E6;
        double lat = mResponseCarStatusInfoBean.getResultlat() / 1E6;

        LatLng latLng = CoordinateUtil.gps84_to_bd09(lat, lng);

        return latLng;
    }

    /**
     * 显示搜索界面
     *
     * @param titleView
     * @param mBaiduMap
     * @param bmapView
     */
    public void showSearchDialog(final View titleView, final BaiduMap mBaiduMap, final MapView bmapView, PopupWindow.OnDismissListener dismissListener) {

//        search_et.setOnClickListener(null);

        if (searchDilaog == null) {
            searchDilaog = new SearchDilaog(context, this);
            searchDilaog.setClickListeren(new SearchDialogClickListeren() {

                @Override
                public void OnClick(CarInfoBean mCarInfoBean) {

                    if (searchDilaog != null) {
                        searchDilaog.dismiss();
                    }
//                    removeDailogOfManager(searchDilaog);

                    if(mCarInfoBean.getLng() == 0 && mCarInfoBean.getLat() == 0){
                        ToastUtil.showShort(context.getString(R.string.no_location_info));

                    }else{
                        lockOnlyCar(mBaiduMap,bmapView, mCarInfoBean);
                    }

                    EditText search_et = (EditText) titleView.findViewById(R.id.search_et);
                    hideInput(search_et);
                }

            });

        }else{
            updateSearchDialogHeight();
        }

        if (dismissListener != null) {
            searchDilaog.setOnDismissListener(dismissListener);
        }

        if(!searchDilaog.isShowing()) {
            searchDilaog.updateHistory();
        }
//        addDialogOfManager(searchDilaog);
      searchDilaog.show(titleView);
//        searchDilaog.showAsDropDown(titleView);
        markerDialogToBackground();

    }

    public void updateSearchDialogHeight(){

        if(searchDilaog!= null && searchDilaog.isShowing()){
            searchDilaog.updateSearchHeight();

        }

    }

    public void lockOnlyCar(BaiduMap mBaiduMap, MapView bmapView, CarInfoBean mCarInfoBean){
        //设置定位一台车辆
//        UserInfo.getInstance().setCurrentLocationCar(mCarInfoBean.getPlateNumber());
//        UserInfo.getInstance().setIsLocationCar(true);

        double lng = mCarInfoBean.getLng() / 1E6;
        double lat = mCarInfoBean.getLat() / 1E6;

        LatLng latLng = CoordinateUtil.gps84_to_bd09(lat, lng);

        //跟新地图位置，定位到当前车辆位置（会调用onMapStatusChangeFinish）
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));

        showMarkerDialog(bmapView, mCarInfoBean);

        //设置成当前点击的marker
        setCurrentClickMarker(mCarInfoBean.getPlateNumber());
    }

    /**
     * 弹出输入法
     *
     * @param view
     */
    public void showInput(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 接受软键盘输入的编辑文本或其它视图
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 关闭输入法
     *
     * @param view
     */
    public void hideInput(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    /**
     * 输入法弹框是否为空
     *
     * @return
     */
    public boolean searchDilaogIsEmpty() {
        return searchDilaog == null;
    }

    /**
     * 搜索框是否显示
     *
     * @return
     */
    public boolean searchDilaogIsShow() {
        return (searchDilaog != null && searchDilaog.isShowing());
    }

    /**
     * 更新搜索结果
     *
     * @param str 根据输入内容
     */
    public void searchDilaogupdateContent(String str) {
        if (TextUtils.isEmpty(str)) {
            searchDilaog.updateHistory();
//            ToastUtil.showShort("updateHistory");
        } else {
            searchDilaog.updateContent(str);
//            ToastUtil.showShort("updateContent" + str);
        }
    }

    /**
     * 关闭搜索弹框
     */
    public boolean searchDilaogDismiss() {
        if (searchDilaog != null && searchDilaog.isShowing()) {
            searchDilaog.dismiss();
            return true;
        }
        return false;
    }

    /*public void onPause(){
        if(showMarkerScheme != null){
            showMarkerScheme.onPause();
        }
    }

    public void onResume(){
        if(showMarkerScheme != null){
            showMarkerScheme.onResume();
        }
    }*/

    public void showUserMenuDialog(View view){
        if (mUserMenuDialog == null) {
            mUserMenuDialog = new UserMenuDialog(context, this);
            mUserMenuDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ConfigurationChangedManager.getInstance().unRegistConfig(mUserMenuDialog);
                }
            });
        }else{
            mUserMenuDialog.updateView();
            mUserMenuDialog.updateMessage();
        }
        mUserMenuDialog.show(view);
        ConfigurationChangedManager.getInstance().registConfig(mUserMenuDialog);
        searchDilaogDismiss();
        markerDialogToBackground();

    }

    public boolean dismissUserMenu(){
        if(mUserMenuDialog != null && mUserMenuDialog.isShowing()){
            mUserMenuDialog.dismiss();
            return true;
        }
        return false;
    }

}
