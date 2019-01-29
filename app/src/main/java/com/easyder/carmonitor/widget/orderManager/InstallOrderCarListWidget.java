package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.InstallCarListAdapter;
import com.easyder.carmonitor.interfaces.InstallOrderCarInfoChangeListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskCtrl.DBCtrlTask;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;
import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.network.UDP.InfoTool.DecodeUDPDataTool;
import com.shinetech.mvp.network.UDP.bean.orderBean.AttachmentItemVo;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-12-06.
 */

public class InstallOrderCarListWidget {

    private Context context;

    private View rootView;

    private DecodeUDPDataTool.OrderContentListItemData orderContentListItemData;

    private LayoutBackListener listener;

    private ListView install_carlist_listview;

    private RelativeLayout install_carlist_layout_outmost;

    private List<InstallTerminalnfo> installTerminalnfos = new ArrayList<>();

    private InstallCarListAdapter mInstallCarListAdapter;

    private InstallOrderCarInfoChangeListener changeListener;

    private EditText allcar_title_search_et;

    private String orderNumber;

    private int orderStatus;

    private List<AttachmentItemVo> attachmentItemList;

    public InstallOrderCarListWidget(Context context, View rootView, int orderStatus, String orderNumber, DecodeUDPDataTool.OrderContentListItemData orderContentListItemData, List<AttachmentItemVo> attachmentItemList, LayoutBackListener listener) {
        this.context = context;
        this.rootView = rootView;
        this.orderContentListItemData = orderContentListItemData;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.listener = listener;
        this.attachmentItemList = attachmentItemList;
        initTitle(rootView);

        initLayout();
    }

    private void initTitle(View view){

        ImageButton title_back = (ImageButton) view.findViewById(R.id.title_back);
        TextView title_text = (TextView) view.findViewById(R.id.title_text);
        title_text.setVisibility(View.GONE);
        ImageButton title_search = (ImageButton) view.findViewById(R.id.title_search);
        title_search.setVisibility(View.GONE);

        allcar_title_search_et = (EditText) view.findViewById(R.id.allcar_title_search_et);
        allcar_title_search_et.setSingleLine();

        allcar_title_search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
//                TODO update car list
                updata(s.toString());
            }
        });

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

        RelativeLayout title_layout = (RelativeLayout) view.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }

    public void cleanSearchContent(){
        if(allcar_title_search_et != null) {
            allcar_title_search_et.setText("");
        }
    }

    public void setInstallOrderCarInfoChangeListener(InstallOrderCarInfoChangeListener changeListener){
        this.changeListener = changeListener;
    }

    private void initLayout(){
        install_carlist_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.install_carlist_layout_outmost);
        install_carlist_listview = (ListView) rootView.findViewById(R.id.install_carlist_listview);

        install_carlist_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(changeListener != null && mInstallCarListAdapter != null){
                    InstallTerminalnfo item = (InstallTerminalnfo) mInstallCarListAdapter.getItem(position);
                    if(item != null) {

                        changeListener.ShowToTerminalnfo(orderStatus, item, attachmentItemList);
                    }
                }

            }
        });

        updata("");
    }

    public void updata(final String search){

        DBCtrlTask.getInstance().runTask(new TaskBean() {
            @Override
            public void run() {

               getTerminallist(search);

                UIUtils.runInMainThread(new Runnable() {
                    @Override
                    public void run() {

                        if(mInstallCarListAdapter == null) {
                            mInstallCarListAdapter = new InstallCarListAdapter(orderStatus, context);
                            mInstallCarListAdapter.updata(installTerminalnfos);
                            install_carlist_listview.setAdapter(mInstallCarListAdapter);
                        }

                        mInstallCarListAdapter.updata(installTerminalnfos);

                    }
                });
            }
        });
    }

    private void getTerminallist(String search){


        List<InstallTerminalnfo> querySelectInstallTerminalnfos;
        if(TextUtils.isEmpty(search)){
            querySelectInstallTerminalnfos = DBManager.querySelectInstallTerminalnfo(orderNumber);

            if(querySelectInstallTerminalnfos == null || querySelectInstallTerminalnfos.size() == 0){
                if(orderContentListItemData != null && orderContentListItemData.hasContent()){

                    List<InstallTerminalnfo> caveInstallTerminalnfos = saveInstallTerminalnfo();

                    this.installTerminalnfos.clear();
                    this.installTerminalnfos.addAll(caveInstallTerminalnfos);

                }else{
                    this.installTerminalnfos.clear();
                }
            }else{
                this.installTerminalnfos.clear();
                this.installTerminalnfos.addAll(querySelectInstallTerminalnfos);
            }

        }else{
            querySelectInstallTerminalnfos = DBManager.querySelectInstallTerminalnfo(orderNumber,search);
            if(querySelectInstallTerminalnfos == null || querySelectInstallTerminalnfos.size() == 0) {
                this.installTerminalnfos.clear();
            }else{
                this.installTerminalnfos.clear();
                this.installTerminalnfos.addAll(querySelectInstallTerminalnfos);
            }
        }


    }

    private List<InstallTerminalnfo> saveInstallTerminalnfo(){
        List<List<String>> fieldListContent = orderContentListItemData.getFieldListContent();

        List<InstallTerminalnfo> terminaInfoList = new ArrayList<>();

        for(int i = 0; i<fieldListContent.size(); i++){

            InstallTerminalnfo installTerminalnfo = new InstallTerminalnfo();

            /**
             * 工单号
             */
            installTerminalnfo.setOrderNumber(orderNumber);

            /**
             * 车牌号
             */
            String plateNumber = orderContentListItemData.getValue(context.getString(R.string.carinfo_platenumber), i);
            if(!TextUtils.isEmpty(plateNumber)){
                installTerminalnfo.setPlateNumber(plateNumber);
            }

            /**
             * 车架号
             */
            String vin = orderContentListItemData.getValue(context.getString(R.string.terminainfo_vin), i);
            if(!TextUtils.isEmpty(vin)){
                installTerminalnfo.setVin(vin);
            }

            /**
             * 终端型号
             */
            String terminaType = orderContentListItemData.getValue(context.getString(R.string.terminainfo_type), i);
            if(!TextUtils.isEmpty(terminaType)){
                installTerminalnfo.setTerminalType(terminaType);
            }

            /**
             * 终端ID
             */
            String terminaID = orderContentListItemData.getValue(context.getString(R.string.terminainfo_id), i);
            if(!TextUtils.isEmpty(terminaID)){
                installTerminalnfo.setTerminalID(terminaID);
            }

            /**
             * SIM卡号
             */
            String simCard = orderContentListItemData.getValue(context.getString(R.string.terminainfo_simcard), i);
            if(!TextUtils.isEmpty(simCard)){
                installTerminalnfo.setSimCard(simCard);
            }

            /**
             * 安装日期
             */
            String installDate = orderContentListItemData.getValue(context.getString(R.string.terminainfo_install_date), i);
            if(!TextUtils.isEmpty(installDate)){
                installTerminalnfo.setInstallTime(installDate);
            }

            /**
             * 设备运行情况
             */
            String deviceStatus = orderContentListItemData.getValue(context.getString(R.string.device_run_status), i);
            if(!TextUtils.isEmpty(deviceStatus)){
                installTerminalnfo.setDeviceStatus(deviceStatus);
            }

            /**
             * 安装人员
             */
            String installPersonnel = orderContentListItemData.getValue(context.getString(R.string.terminainfo_install_personnel), i);
            if(!TextUtils.isEmpty(installPersonnel)){
                installTerminalnfo.setInstallationPersonnel(installPersonnel);
            }

            terminaInfoList.add(installTerminalnfo);

        }

        DBManager.insertInstallTerminalnfoList(terminaInfoList);

        return terminaInfoList;


    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(install_carlist_layout_outmost != null) {
            install_carlist_layout_outmost.setOnTouchListener(touchListener);
        }
    }
}
