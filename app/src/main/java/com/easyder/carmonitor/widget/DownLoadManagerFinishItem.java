package com.easyder.carmonitor.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.dialog.OfflineTaskCtrlDialog;
import com.easyder.carmonitor.interfaces.DownLoadManagerItemClickListener;
import com.easyder.carmonitor.interfaces.UpdataOfflineMapOnDelete;
import com.shinetech.mvp.utils.SizeUtils;
import com.shinetech.mvp.utils.ToastUtil;

/**
 * Created by ljn on 2017-04-18.
 */
public class DownLoadManagerFinishItem implements View.OnClickListener {

    private Context context;

    private MKOLUpdateElement updateInfo;

    private View rootView;

    private DownLoadManagerItemClickListener clickListener;

    public DownLoadManagerFinishItem(Context context, MKOLUpdateElement updateInfo) {
        this.context = context;
        this.updateInfo = updateInfo;
        initView();
    }

    private void initView(){
        rootView = View.inflate(context, R.layout.download_manager_finish_item, null);

        rootView.setOnClickListener(this);

        TextView cityName = (TextView) rootView.findViewById(R.id.finish_item_cityname);
        TextView citysize = (TextView) rootView.findViewById(R.id.finish_item_citysize);
        cityName.setText(updateInfo.cityName);
        citysize.setText(SizeUtils.formatDataSize(updateInfo.serversize));
    }

    public View getRootView(){
        return rootView;
    }

    @Override
    public void onClick(View v) {
//        ToastUtil.showShort("cityId = "+ updateInfo.cityID + "   cityName = "+updateInfo.cityName+ "   status = "+updateInfo.status);

        clickListener.onItemClick(v, updateInfo);

    }

    public void setOnItemClick(DownLoadManagerItemClickListener clickListener){
        this.clickListener = clickListener;
    }

}
