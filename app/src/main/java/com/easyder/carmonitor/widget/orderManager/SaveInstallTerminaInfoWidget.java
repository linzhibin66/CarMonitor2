package com.easyder.carmonitor.widget.orderManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.InstallOrderUtil;
import com.easyder.carmonitor.Utils.JsonUtil;
import com.easyder.carmonitor.adapter.AddPicturesImgGridAdapter;
import com.easyder.carmonitor.broadcast.SDCardListeren;
import com.easyder.carmonitor.interfaces.EnterDialogListener;
import com.easyder.carmonitor.interfaces.GetPictureListener;
import com.easyder.carmonitor.interfaces.SaveInstallTermianinfoLayoutBackListener;
import com.easyder.carmonitor.presenter.CreatMaintenanceOrderPresenter;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;
import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.AttachmentItemVo;
import com.shinetech.mvp.network.UDP.presenter.UpLoadImgPresenter;
import com.shinetech.mvp.utils.FileUtils;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;
import com.shinetech.mvp.view.MvpView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-12-07.
 */

public class SaveInstallTerminaInfoWidget <M extends BaseVo> implements MvpView {

    private Context context;

    private View rootView;

    private InstallTerminalnfo mInstallTerminalnfo;

    private SaveInstallTermianinfoLayoutBackListener listener;

    private RelativeLayout order_terminainfo_layout_outmost;

    private RelativeLayout order_terminainfo_enter_layout;

    private LinearLayout terminainfo_iten_info_layout;

    private MyGridView order_terminainfo_imglist;

    private TextView terminainfo_platenumber;

    private AddPicturesImgGridAdapter addPicturesImgGridAdapter;

    /**
     * 图片路径list
     */
    private List<String> imgPath = new ArrayList<>();

    private String path = "/sdcard/Carmonitor/";

    private SDCardListeren mSDCardListeren;

    private int installOrderStatus;

    /**
     * 终端型号
     */
    private TreminainfoItem treminaTypeItem;

    /**
     * 车架号
     */
    private TreminainfoItem treminaVinItem;

    /**
     * 车牌号
     */
    private TreminainfoItem treminaPlateNumberItem;

    /**
     * SIM卡号
     */
    private TreminainfoItem treminaSIMItem;

    /**
     * 定位间隔
     */
    private TreminainfoItem treminaLocationIntervalItem;

    /**
     * 安装人员
     */
    private TreminainfoItem treminanInstallPersonnelItem;

    /**
     * 安装日期
     */
    private TreminainfoItem treminaInstallDateItem;

    /**
     * 设备运行情况
     */
    private TreminainfoItem deviceSituationItem;

    private EnterDialogListener enterDialogListener;

    private UpLoadImgPresenter presenter;

    private String orderName;

    private String deletefilePath;

    private List<AttachmentItemVo> attachmentItemList;

    public SaveInstallTerminaInfoWidget(Context context, View rootView, int installOrderStatus, String orderName, InstallTerminalnfo mInstallTerminalnfo, List<AttachmentItemVo> attachmentItemList, SaveInstallTermianinfoLayoutBackListener listener) {
        this.context = context;
        this.rootView = rootView;
        this.mInstallTerminalnfo = mInstallTerminalnfo;
        this.listener = listener;
        this.orderName = orderName;
        this.installOrderStatus = installOrderStatus;
        this.attachmentItemList = attachmentItemList;

        initTitle();

        initLayout();

        creatPresenter();
    }

    private void creatPresenter(){
        presenter = new UpLoadImgPresenter();
        presenter.attachView(this);
    }

    private void initTitle(){

        ImageButton title_back = (ImageButton) rootView.findViewById(R.id.title_back);
        TextView title_text = (TextView) rootView.findViewById(R.id.title_text);
        TextView title_search = (TextView) rootView.findViewById(R.id.title_search);
        if(installOrderStatus != InstallOrderBaseInfo.ORDER_RECEIVING_STATUS) {
            title_search.setVisibility(View.GONE);
        }else{
            if((!mInstallTerminalnfo.isIntegrityInfo())) {
                title_search.setText(context.getString(R.string.save_string));
                title_search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commitData();
                    }
                });
            }else{
                title_search.setVisibility(View.GONE);
            }
        }

        title_text.setText(context.getString(R.string.carinfo_title));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

        RelativeLayout title_layout = (RelativeLayout) rootView.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }

    private void initLayout() {
        order_terminainfo_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.order_terminainfo_layout_outmost);
        order_terminainfo_enter_layout = (RelativeLayout) rootView.findViewById(R.id.order_terminainfo_enter_layout);

        terminainfo_iten_info_layout = (LinearLayout) rootView.findViewById(R.id.terminainfo_iten_info_layout);
        order_terminainfo_imglist = (MyGridView) rootView.findViewById(R.id.order_terminainfo_imglist);
        terminainfo_platenumber = (TextView) rootView.findViewById(R.id.terminainfo_platenumber);

        terminainfo_platenumber.setText(mInstallTerminalnfo.getPlateNumber());

        addItems();

        if(installOrderStatus >= InstallOrderBaseInfo.ORDER_RECEIVING_STATUS ) {
            order_terminainfo_imglist.setVisibility(View.VISIBLE);
            initPicture();
        }else{
            order_terminainfo_imglist.setVisibility(View.GONE);
        }

    }

    private void commitData(){

        //检查数据是否齐全

        EnterDialogWidget mEnterDialogWidget = new EnterDialogWidget(context, new EnterDialogListener() {
            @Override
            public void OnCancel() {
                if (listener != null) {
                    listener.updateDialogFocusable(true);
                }
                dismissEnterLayout();
            }

            @Override
            public void OnEnter() {

                //判断是否存储车牌号
                if (treminaPlateNumberItem != null && TextUtils.isEmpty(mInstallTerminalnfo.getPlateNumber())) {
                    String plateNumber = treminaPlateNumberItem.getValue();
                    if (!TextUtils.isEmpty(plateNumber)) {
                        mInstallTerminalnfo.setPlateNumber(plateNumber);
                    }else{
                        ToastUtil.showShort(context.getString(R.string.carinfo_platenumber) + context.getString(R.string.content_no_null));
                        return;
                    }
                }


                //判断是否存储车架号
                if (treminaVinItem != null && TextUtils.isEmpty(mInstallTerminalnfo.getVin())) {
                    String vin = treminaVinItem.getValue();
                    if (!TextUtils.isEmpty(vin)) {
                        mInstallTerminalnfo.setVin(vin);
                    }else{
                        ToastUtil.showShort(context.getString(R.string.terminainfo_vin) + context.getString(R.string.content_no_null));
                        return;
                    }
                }


                if(treminaTypeItem != null && TextUtils.isEmpty(mInstallTerminalnfo.getTerminalType())){
                    String value = treminaTypeItem.getValue();
                    if(!TextUtils.isEmpty(value)){
                        mInstallTerminalnfo.setTerminalType(value);
                    }else{
                        ToastUtil.showShort(context.getString(R.string.terminainfo_type) + context.getString(R.string.content_no_null));
                        return;
                    }
                }

                if(treminaSIMItem != null && TextUtils.isEmpty(mInstallTerminalnfo.getSimCard())){
                    String value = treminaSIMItem.getValue();
                    if(!TextUtils.isEmpty(value)){
                        mInstallTerminalnfo.setSimCard(value);
                    }else{
                        ToastUtil.showShort(context.getString(R.string.terminainfo_simcard) + context.getString(R.string.content_no_null));
                        return;
                    }
                }

               /* if(treminaLocationIntervalItem != null && (mInstallTerminalnfo.getLocationInterval() == -1)){
                    String value = treminaLocationIntervalItem.getValue();
                    if(!TextUtils.isEmpty(value)){
                        try {
                            mInstallTerminalnfo.setLocationInterval(Integer.parseInt(value));
                        }catch (Exception e){
                            ToastUtil.showShort(context.getString(R.string.terminainfo_location_interval) + context.getString(R.string.content_error));
                            return;
                        }
                    }else{
                        ToastUtil.showShort(context.getString(R.string.terminainfo_location_interval) + context.getString(R.string.content_no_null));
                        return;
                    }
                }*/

                if(treminanInstallPersonnelItem != null && TextUtils.isEmpty(mInstallTerminalnfo.getInstallationPersonnel())){
                    String value = treminanInstallPersonnelItem.getValue();
                    if(!TextUtils.isEmpty(value)){
                        mInstallTerminalnfo.setInstallationPersonnel(value);
                    }else{
                        ToastUtil.showShort(context.getString(R.string.terminainfo_install_personnel) + context.getString(R.string.content_no_null));
                        return;
                    }
                }

                if(treminaInstallDateItem != null && TextUtils.isEmpty(mInstallTerminalnfo.getInstallTime())){
                    String value = treminaInstallDateItem.getValue();
                    if(!TextUtils.isEmpty(value)){
                        mInstallTerminalnfo.setInstallTime(value);
                    }else{
                        ToastUtil.showShort(context.getString(R.string.terminainfo_install_date) + context.getString(R.string.content_no_null));
                        return;
                    }
                }

                if(deviceSituationItem != null && TextUtils.isEmpty(mInstallTerminalnfo.getDeviceStatus())){
                    String value = deviceSituationItem.getValue();
                    if(!TextUtils.isEmpty(value)){
                        mInstallTerminalnfo.setDeviceStatus(value);
                    }else{
                        ToastUtil.showShort(context.getString(R.string.device_run_status) + context.getString(R.string.content_no_null));
                        return;
                    }
                }

                DBManager.updateInstallTerminalnfo(mInstallTerminalnfo);

//                TODO  save info to DB
                if (listener != null) {
                    listener.onBack();
                }


            }
        });

        mEnterDialogWidget.setTitle(context.getString(R.string.enter_info_no_error));
        mEnterDialogWidget.setHint(context.getString(R.string.save_enter_commit_hint));


        RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        order_terminainfo_enter_layout.addView(mEnterDialogWidget.getView(), params);

        order_terminainfo_enter_layout.setVisibility(View.VISIBLE);

        if (listener != null) {
            listener.updateDialogFocusable(false);
        }

    }

    private void addItems(){


        //车牌
        String plateNumber = mInstallTerminalnfo.getPlateNumber();

        if(TextUtils.isEmpty(plateNumber)){
            if(installOrderStatus == InstallOrderBaseInfo.ORDER_RECEIVING_STATUS) {
                treminaPlateNumberItem = new TreminainfoItem(context);
                treminaPlateNumberItem.setTitle(context.getString(R.string.carinfo_platenumber));
                treminaPlateNumberItem.setValueHint("颜色+车牌号码(如：“蓝 粤S*****”)");
                terminainfo_iten_info_layout.addView(treminaPlateNumberItem.getItemView());
            }
        }

        //车架号
        String terminalvin = mInstallTerminalnfo.getVin();
        if(!TextUtils.isEmpty(terminalvin)){
            TreminainfoSelectItem treminaVinItem = new TreminainfoSelectItem(context);
            treminaVinItem.setTitle(context.getString(R.string.terminainfo_vin));
            treminaVinItem.setNoSelectText(terminalvin);
            terminainfo_iten_info_layout.addView(treminaVinItem.getItemView());
        }else {
            if(installOrderStatus == InstallOrderBaseInfo.ORDER_RECEIVING_STATUS) {
                treminaVinItem = new TreminainfoItem(context);
                treminaVinItem.setTitle(context.getString(R.string.terminainfo_vin));
                terminainfo_iten_info_layout.addView(treminaVinItem.getItemView());
            }
        }


        //旧车牌
       /* String terminalOldPlateNumber = mInstallTerminalnfo.getOldPlateNumber();
        if(!TextUtils.isEmpty(terminalOldPlateNumber)){
            TreminainfoSelectItem treminaOldPlateNumberItem = new TreminainfoSelectItem(context);
            treminaOldPlateNumberItem.setTitle(context.getString(R.string.terminainfo_oldplatenumber));
            treminaOldPlateNumberItem.setNoSelectText(terminalOldPlateNumber);
            terminainfo_iten_info_layout.addView(treminaOldPlateNumberItem.getItemView());
        }*/


        //终端序列号
       /* String terminalSerialNumber = mInstallTerminalnfo.getSerialNumber();
        if(!TextUtils.isEmpty(terminalSerialNumber)){
            TreminainfoSelectItem treminaSerialNumberItem = new TreminainfoSelectItem(context);
            treminaSerialNumberItem.setTitle(context.getString(R.string.terminainfo_serialnumber));
            treminaSerialNumberItem.setNoSelectText(terminalSerialNumber);
            terminainfo_iten_info_layout.addView(treminaSerialNumberItem.getItemView());
        }*/

        //终端类型
        String terminalType = mInstallTerminalnfo.getTerminalType();
        if(!TextUtils.isEmpty(terminalType)){
            TreminainfoSelectItem treminaTypeItem = new TreminainfoSelectItem(context);
            treminaTypeItem.setTitle(context.getString(R.string.terminainfo_type));
            treminaTypeItem.setNoSelectText(terminalType);
            terminainfo_iten_info_layout.addView(treminaTypeItem.getItemView());
        }else {
            if(installOrderStatus == InstallOrderBaseInfo.ORDER_RECEIVING_STATUS) {
                treminaTypeItem = new TreminainfoItem(context);
                treminaTypeItem.setTitle(context.getString(R.string.terminainfo_type));
                terminainfo_iten_info_layout.addView(treminaTypeItem.getItemView());
            }
        }

        //SIM卡号
        String simCard = mInstallTerminalnfo.getSimCard();
        if(!TextUtils.isEmpty(simCard)){
            TreminainfoSelectItem treminaSIMItem = new TreminainfoSelectItem(context);
            treminaSIMItem.setTitle(context.getString(R.string.terminainfo_simcard));
            treminaSIMItem.setNoSelectText(simCard);
            terminainfo_iten_info_layout.addView(treminaSIMItem.getItemView());
        }else {
            if(installOrderStatus == InstallOrderBaseInfo.ORDER_RECEIVING_STATUS) {
                treminaSIMItem = new TreminainfoItem(context);
                treminaSIMItem.setTitle(context.getString(R.string.terminainfo_simcard));
                terminainfo_iten_info_layout.addView(treminaSIMItem.getItemView());
            }
        }

        //定位间隔
        /*int locationInterval = mInstallTerminalnfo.getLocationInterval();
        if(locationInterval != -1) {
            TreminainfoSelectItem treminaLocationIntervalItem = new TreminainfoSelectItem(context);
            treminaLocationIntervalItem.setTitle(context.getString(R.string.terminainfo_location_interval));
            treminaLocationIntervalItem.setNoSelectText(""+locationInterval);
            terminainfo_iten_info_layout.addView(treminaLocationIntervalItem.getItemView());
        }else{
            if(installOrderStatus == InstallOrderBaseInfo.ORDER_RECEIVING_STATUS) {
                treminaLocationIntervalItem = new TreminainfoItem(context);
                treminaLocationIntervalItem.setTitle(context.getString(R.string.terminainfo_location_interval));
                terminainfo_iten_info_layout.addView(treminaLocationIntervalItem.getItemView());
            }
        }*/

        //安装日期
        String installTime = mInstallTerminalnfo.getInstallTime();
        if(!TextUtils.isEmpty(installTime)){
            if(installOrderStatus != InstallOrderBaseInfo.WAITING_ORDER_RECEIVING_STATUS){
                TreminainfoSelectItem treminaInstallDateItem = new TreminainfoSelectItem(context);
                treminaInstallDateItem.setTitle(context.getString(R.string.terminainfo_install_date));
                treminaInstallDateItem.setNoSelectText(installTime);
                terminainfo_iten_info_layout.addView(treminaInstallDateItem.getItemView());
            }
        }else{
            if(installOrderStatus == InstallOrderBaseInfo.ORDER_RECEIVING_STATUS){
                treminaInstallDateItem = new TreminainfoItem(context);
                treminaInstallDateItem.setTitle(context.getString(R.string.terminainfo_install_date));
                terminainfo_iten_info_layout.addView(treminaInstallDateItem.getItemView());
            }
        }

        //设备运行情况
        String deviceStatus = mInstallTerminalnfo.getDeviceStatus();
        if(!TextUtils.isEmpty(deviceStatus)){
            if(installOrderStatus != InstallOrderBaseInfo.WAITING_ORDER_RECEIVING_STATUS){
                TreminainfoSelectItem treminaInstallSituationItem = new TreminainfoSelectItem(context);
                treminaInstallSituationItem.setTitle(context.getString(R.string.device_run_status));
                treminaInstallSituationItem.setNoSelectText(deviceStatus);
                terminainfo_iten_info_layout.addView(treminaInstallSituationItem.getItemView());
            }
        }else{
            if(installOrderStatus == InstallOrderBaseInfo.ORDER_RECEIVING_STATUS){
                deviceSituationItem = new TreminainfoItem(context);
                deviceSituationItem.setTitle(context.getString(R.string.device_run_status));
//              treminaInstallSituationItem.hindLine();
                terminainfo_iten_info_layout.addView(deviceSituationItem.getItemView());
            }
        }

        //安装人员
        String installationPersonnel = mInstallTerminalnfo.getInstallationPersonnel();
        if(!TextUtils.isEmpty(installationPersonnel)){
            if(installOrderStatus != InstallOrderBaseInfo.WAITING_ORDER_RECEIVING_STATUS){
                TreminainfoSelectItem treminanInstallPersonnelItem = new TreminainfoSelectItem(context);
                treminanInstallPersonnelItem.setTitle(context.getString(R.string.terminainfo_install_personnel));
                treminanInstallPersonnelItem.setNoSelectText(installationPersonnel);
                terminainfo_iten_info_layout.addView(treminanInstallPersonnelItem.getItemView());
            }
        }else{
            if(installOrderStatus == InstallOrderBaseInfo.ORDER_RECEIVING_STATUS){
                treminanInstallPersonnelItem = new TreminainfoItem(context);
                treminanInstallPersonnelItem.setTitle(context.getString(R.string.terminainfo_install_personnel));
                terminainfo_iten_info_layout.addView(treminanInstallPersonnelItem.getItemView());
            }
        }

    }

    //将附件图片添加回安装信息中
    private List<String> initAttachment(){

        String pathjsonStr = mInstallTerminalnfo.getPathjsonStr();
        List<String> filePathList = null;
        if(!TextUtils.isEmpty(pathjsonStr)) {
            filePathList = JsonUtil.JSONArrayToList(pathjsonStr);
        }

        boolean canSave = false;

        String plateNumber = mInstallTerminalnfo.getPlateNumber();
        String[] split = plateNumber.split(" ");
        if(split.length == 2){
            plateNumber = split[1];
        }

        for(AttachmentItemVo item : attachmentItemList){

            if(item.getFileType().equals(context.getString(R.string.install_img)) && item.getFileName().contains(plateNumber)){

                String filePath = path+mInstallTerminalnfo.getOrderNumber()+ "/" + item.getFileName();//图片名字

                if(filePathList != null) {
                    if (!filePathList.contains(filePath)) {

                        filePathList.add(filePath);
                        canSave = true;
                        //TODO save
                    }
                }else{
                    filePathList = new ArrayList<>();
                    filePathList.add(filePath);
                    canSave = true;
                }
            }

        }

        if(canSave) {
            String pathStr = JsonUtil.stringListToJsonString(filePathList);
            mInstallTerminalnfo.setPathjsonStr(pathStr);
            DBManager.updateInstallTerminalnfo(mInstallTerminalnfo);
        }

        return filePathList;
    }

    private void initPicture(){

//        attachmentItemList
        List<String> filePathList = initAttachment();

        imgPath.clear();
        if(filePathList != null && filePathList.size()>0) {

            for (String filepath : filePathList) {
                File file = new File(filepath);
                if (file.exists()) {
                    imgPath.add(filepath);
                }
            }
        }

        if(installOrderStatus >= InstallOrderBaseInfo.ORDER_RECEIVING_STATUS && (!mInstallTerminalnfo.isIntegrityInfo())) {
            imgPath.add("photo");
        }

        addPicturesImgGridAdapter = new AddPicturesImgGridAdapter(context);

        addPicturesImgGridAdapter.initData(imgPath);

        order_terminainfo_imglist.setAdapter(addPicturesImgGridAdapter);

        order_terminainfo_imglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(installOrderStatus >= InstallOrderBaseInfo.ORDER_RECEIVING_STATUS && (!mInstallTerminalnfo.isIntegrityInfo())) {

                    if((addPicturesImgGridAdapter.getCount()-1) == position){
                        //TODO show select
                        showPictureMenuView();
                        return;
                    }
                }

                //TODO show picture
                Intent intent = new Intent(Intent.ACTION_VIEW);    //打开图片得启动ACTION_VIEW意图
                Uri uri = Uri.fromFile(new File(imgPath.get(position)));
                intent.setDataAndType(uri, "image/*");    //设置intent数据和图片格式
                ((Activity)context).startActivity(intent);

            }
        });

        if(installOrderStatus >= InstallOrderBaseInfo.ORDER_RECEIVING_STATUS && (!mInstallTerminalnfo.isIntegrityInfo())) {

            order_terminainfo_imglist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    if ((addPicturesImgGridAdapter.getCount() - 1) == position) {
                        //TODO show select
                        showPictureMenuView();
                    } else {

                        //TODO show picture
                        deletefilePath = (String) addPicturesImgGridAdapter.getItem(position);

                        deleteImg(deletefilePath);

                    }
                    return true;
                }
            });
        }

        mSDCardListeren = new SDCardListeren(path, imgPath, addPicturesImgGridAdapter);
        mSDCardListeren.startWatching();

    }

    /**
     * 删除图片
     * @param filePath
     */
    private void deleteImg(final String filePath){

        if(order_terminainfo_enter_layout != null) {


            if(enterDialogListener == null) {
                enterDialogListener = new EnterDialogListener() {
                    @Override
                    public void OnCancel() {
                        dismissEnterLayout();
                    }

                    @Override
                    public void OnEnter() {

                        String fileName = FileUtils.getFileName(deletefilePath);

                        presenter.deleteImg(orderName, mInstallTerminalnfo.getOrderNumber(), context.getString(R.string.install_img), fileName, new UpLoadImgPresenter.UploadImgListener() {
                            @Override
                            public void OnSuccess() {
                                File file = new File(deletefilePath);
                                if (file.exists()) {
                                    file.delete();
                                }

                                imgPath.remove(deletefilePath);
                                addPicturesImgGridAdapter.initData(imgPath);
                                saveImgPaths();

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

            order_terminainfo_enter_layout.removeAllViews();

            RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            order_terminainfo_enter_layout.addView(enterDialogWidget.getView(),params);
            order_terminainfo_enter_layout.setVisibility(View.VISIBLE);
        }
    }

    private void saveImgPaths(){
        List<String> pathList = new ArrayList<>();
        pathList.addAll(imgPath);
        pathList.remove("photo");
        String pathStr = JsonUtil.stringListToJsonString(pathList);
        mInstallTerminalnfo.setPathjsonStr(pathStr);
        DBManager.updateInstallTerminalnfo(mInstallTerminalnfo);
    }

    private void showPictureMenuView(){
        if(order_terminainfo_enter_layout != null) {

            order_terminainfo_enter_layout.removeAllViews();

            String fileNameDescribe = InstallOrderUtil.getTitle(mInstallTerminalnfo);

            PictureCtrlWidget pictureCtrlWidget = new PictureCtrlWidget(context, orderName, mInstallTerminalnfo.getOrderNumber(), fileNameDescribe, imgPath, context.getString(R.string.install_img), presenter,  new GetPictureListener() {
                @Override
                public void OnResult(String filePath) {
                    dismissEnterLayout();
                    imgPath.add(0, filePath);
                    addPicturesImgGridAdapter.initData(imgPath);

                    saveImgPaths();
                }

                @Override
                public void OnCancle() {
                    canclePictureMenuView();
                }

                @Override
                public void OnLoading() {
                    onLoading();
                }
            });

            RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

            order_terminainfo_enter_layout.addView(pictureCtrlWidget.getView(), params);

            order_terminainfo_enter_layout.setVisibility(View.VISIBLE);
        }

        if (listener != null) {
            listener.updateDialogFocusable(false);
        }

    }

    private void canclePictureMenuView(){
        dismissEnterLayout();
    }

    private void dismissEnterLayout(){
        if (listener != null) {
            listener.updateDialogFocusable(true);
        }
        if(order_terminainfo_enter_layout != null) {
            order_terminainfo_enter_layout.setVisibility(View.GONE);
            order_terminainfo_enter_layout.removeAllViews();
        }

    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(order_terminainfo_layout_outmost != null) {
            order_terminainfo_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    @Override
    public void onLoading() {

        EnterDialogWidget enterDialogWidget = new EnterDialogWidget(context, null);
        enterDialogWidget.showLoading();

        order_terminainfo_enter_layout.removeAllViews();

        RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        order_terminainfo_enter_layout.addView(enterDialogWidget.getView(), params);
        order_terminainfo_enter_layout.setVisibility(View.VISIBLE);

    }

    @Override
    public void showContentView(BaseVo dataVo) {
    }

    @Override
    public void onStopLoading() {

        dismissEnterLayout();

    }
}
