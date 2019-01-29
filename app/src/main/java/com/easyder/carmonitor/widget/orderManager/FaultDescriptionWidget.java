package com.easyder.carmonitor.widget.orderManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.JsonUtil;
import com.easyder.carmonitor.adapter.FaultDescriptionImgGridAdapter;
import com.easyder.carmonitor.widget.orderManager.MyGridView;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.shinetech.mvp.network.UDP.bean.orderBean.SelectOrderByNumberVo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-11-17.
 */

public class FaultDescriptionWidget {

    private Context context;

    private View rootView;

    private FaultDescriptionImgGridAdapter mFaultDescriptionImgGridAdapter;

    public FaultDescriptionWidget(Context context, SelectOrderByNumberVo item) {
        this.context = context;

        rootView = View.inflate(context, R.layout.maintenance_fault_description_layout,null);

        initLayout(item);
    }

    private void initLayout(SelectOrderByNumberVo item){

        TextView maintenance_fault_description_value = (TextView) rootView.findViewById(R.id.maintenance_fault_description_value);
//        maintenance_fault_description_value.setText(item.getProblemDescription());
        MyGridView img_gridview = (MyGridView) rootView.findViewById(R.id.img_gridview);
//       String problemBitmap = item.getProblemBitmap();

//        List<String> bitmapPathList = JsonUtil.JSONArrayToList(problemBitmap);
        List<String> bitmapPathList = new ArrayList<>();

        if(bitmapPathList != null && bitmapPathList.size()>0){

            img_gridview.setVisibility(View.VISIBLE);

            mFaultDescriptionImgGridAdapter = new FaultDescriptionImgGridAdapter(context);

            mFaultDescriptionImgGridAdapter.initData(bitmapPathList);

            img_gridview.setAdapter(mFaultDescriptionImgGridAdapter);

            img_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);    //打开图片得启动ACTION_VIEW意图

                    Uri uri = Uri.fromFile(new File((String)mFaultDescriptionImgGridAdapter.getItem(position)));
                    intent.setDataAndType(uri, "image/*");    //设置intent数据和图片格式
                    ((Activity)context).startActivity(intent);
                }
            });
        }else{
            img_gridview.setVisibility(View.GONE);
        }

    }

    public View getView(){
        return rootView;
    }
}
