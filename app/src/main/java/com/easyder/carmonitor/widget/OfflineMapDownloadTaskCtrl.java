package com.easyder.carmonitor.widget;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.OfflineTaskCtrlInterfaces;

/**
 * Created by ljn on 2017-07-10.
 */
public class OfflineMapDownloadTaskCtrl implements View.OnClickListener {

    private int downloadStatus;

    private View rootView;

    private TextView city_name_title;

    private TextView offline_task_ctrl;

    private TextView offline_task_delete;

    private TextView offline_task_cancel;
    private View offline_task_ctrl_line;
    private LinearLayout offline_task_content;

    private OfflineTaskCtrlInterfaces interfaces;

    public OfflineMapDownloadTaskCtrl(int downloadStatus, View rootView, OfflineTaskCtrlInterfaces interfaces) {
        this.downloadStatus = downloadStatus;
        this.interfaces = interfaces;
        this.rootView = rootView;
        initView();
    }

    private void initView(){

        offline_task_content = (LinearLayout) rootView.findViewById(R.id.offline_task_content);

        city_name_title = (TextView) rootView.findViewById(R.id.offline_task_city_name_title);

        offline_task_ctrl = (TextView) rootView.findViewById(R.id.offline_task_ctrl);
        offline_task_ctrl.setOnClickListener(this);

        offline_task_ctrl_line =  rootView.findViewById(R.id.offline_task_ctrl_line);

        offline_task_delete = (TextView) rootView.findViewById(R.id.offline_task_delete);
        offline_task_delete.setOnClickListener(this);

        offline_task_cancel = (TextView) rootView.findViewById(R.id.offline_task_cancel);
        offline_task_cancel .setOnClickListener(this);
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        rootView.setOnTouchListener(touchListener);
    }

    public View getcontentLayout(){
        return offline_task_content;
    }

    public void setTitle(String title){
        if(city_name_title != null){
            city_name_title.setText(title);
        }
    }

    public void setCtrl(String ctrl){
        if(offline_task_ctrl != null){
            offline_task_ctrl.setVisibility(View.VISIBLE);
            offline_task_ctrl_line.setVisibility(View.VISIBLE);
            offline_task_ctrl.setText(ctrl);
        }
    }
    public void setDelete(String delete){
        if(offline_task_delete != null){
            offline_task_delete.setText(delete);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.offline_task_ctrl:
                if(interfaces!= null){
                    interfaces.onCtrl();
                }
                break;
            case R.id.offline_task_delete:
                if(interfaces!= null){
                    interfaces.onDelete();
                }
                break;
            case R.id.offline_task_cancel:
                if(interfaces!= null){
                    interfaces.onCancel();
                }
                break;
            default:
                break;
        }

    }
}
