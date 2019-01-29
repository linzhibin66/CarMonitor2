package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.CreatOrderSearchListener;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.utils.UIUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-12-05.
 */

public class CreatOrderSearchPlateNumberWidget {

    private Context context;

    private ListView listView;

    private List<String> plateNumberList = new ArrayList();

    private CreatOrderSearchListener listener;

    private ArrayAdapter<String> stringArrayAdapter;

    public CreatOrderSearchPlateNumberWidget(Context context, CreatOrderSearchListener listener) {
        this.context = context;
        this.listener = listener;
        listView = new ListView(context);
        initView();

    }

    private void initView(){
        listView.setDivider(new ColorDrawable(0xFFE3E3E3));
        listView.setDividerHeight(UIUtils.dip2px(1));
        listView.setBackgroundResource(R.color.white);

        UserInfo instance = UserInfo.getInstance();

        if(!instance.isPerson()){

            List<String> plateNumberList = instance.getPlateNumberList();

            if(plateNumberList == null ){
                if(listener != null) {
                    listener.onBack();
                }
                return;
            }

            this.plateNumberList.addAll(instance.getPlateNumberList());
        }else{
            this.plateNumberList.add(instance.getUserName());
        }

        stringArrayAdapter = new ArrayAdapter(context, R.layout.textview_layout, this.plateNumberList);

        listView.setAdapter(stringArrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectPlateNumber = CreatOrderSearchPlateNumberWidget.this.plateNumberList.get(position);
                if(listener != null){
                    listener.onSelected(selectPlateNumber);
                }
            }
        });

    }

    public void updataSearch(String search){

        UserInfo instance = UserInfo.getInstance();

        List<String> searchresultList = new ArrayList();

        if(!instance.isPerson()){

            List<String> platenumberlist = instance.getPlateNumberList();

            if(platenumberlist == null || platenumberlist.size() == 0){
                return;
            }

            if(TextUtils.isEmpty(search)){
                searchresultList.addAll(platenumberlist);
            }else{

                for (String plateNumber : platenumberlist) {
                    if (plateNumber.contains(search)) {
                        searchresultList.add(plateNumber);
                    }
                }
            }

        }else{
            if(TextUtils.isEmpty(search)) {
                searchresultList.add(instance.getUserName());
            }else{
                String userName = instance.getUserName();
                if(userName.contains(search)){
                    searchresultList.add(userName);
                }
            }
        }

        plateNumberList.clear();
        this.plateNumberList.addAll(searchresultList);

        /*stringArrayAdapter.clear();
        stringArrayAdapter.addAll(plateNumberList);*/
        stringArrayAdapter.notifyDataSetChanged();
    }

    public View getView(){
        return listView;
    }
}
