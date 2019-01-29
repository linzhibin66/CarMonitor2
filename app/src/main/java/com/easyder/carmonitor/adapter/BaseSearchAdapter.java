package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.InfoTool.AlarmInfoTool;
import com.shinetech.mvp.network.UDP.InfoTool.CarStatusInfoTool;
import com.shinetech.mvp.network.UDP.InfoTool.CarTypeTool;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarBaseInfoBean;

/**
 * Created by ljn on 2017-07-27.
 */
public abstract class BaseSearchAdapter extends BaseAdapter{

    protected Context context;

    public BaseSearchAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;
        if(convertView == null) {

            mViewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.classify_carlist_item, null);
            mViewHolder.check_item = (CheckBox) convertView.findViewById(R.id.check_item);
            mViewHolder.item_car_icon = (ImageView) convertView.findViewById(R.id.item_car_icon);
            mViewHolder.item_car_icon_status = (ImageView) convertView.findViewById(R.id.item_car_icon_status);
            mViewHolder.item_car_platenumber = (TextView) convertView.findViewById(R.id.item_car_platenumber);
            mViewHolder.item_car_acc_status = (TextView) convertView.findViewById(R.id.item_car_acc_status);
            mViewHolder.item_car_alarm_status = (TextView) convertView.findViewById(R.id.item_car_alarm_status);
            mViewHolder.item_car_exception_status = (TextView) convertView.findViewById(R.id.item_car_exception_status);
            mViewHolder.check_layout = (RelativeLayout) convertView.findViewById(R.id.check_layout);

            convertView.setTag(mViewHolder);

        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.check_item.setVisibility(View.GONE);
        mViewHolder.check_layout.setVisibility(View.GONE);
        String plateNumber = getPlateNumber(position);
        if(TextUtils.isEmpty(plateNumber)) {
            mViewHolder.item_car_platenumber.setText("");
        }else{
            mViewHolder.item_car_platenumber.setText(plateNumber);

        }

        // 初始化车辆图标
        int listCarIcon;

        ResponseCarBaseInfoBean carBaseInfo = UserInfo.getInstance().getCarBaseInfo(plateNumber);

        if((carBaseInfo != null) && (!TextUtils.isEmpty(carBaseInfo.getCarType()))) {

            listCarIcon = CarTypeTool.getListCarIcon(carBaseInfo.getCarType());

        }else{
            listCarIcon = R.mipmap.icon_allcar_car;
        }

        mViewHolder.item_car_icon.setImageResource(listCarIcon);

        initData(mViewHolder, getCarinfo(position));

        return convertView;
    }

    public abstract CarInfoBean getCarinfo(int position);

    public abstract String getPlateNumber(int position);

    public void initData(ViewHolder mViewHolder, CarInfoBean carInfoBean){

        if(carInfoBean == null){
            mViewHolder.item_car_alarm_status.setVisibility(View.GONE);
            mViewHolder.item_car_acc_status.setVisibility(View.GONE);
            mViewHolder.item_car_exception_status.setVisibility(View.GONE);
            return;
        }

        initCarStatus(mViewHolder, carInfoBean);

        //报警状态
//        if(carInfoBean.getAlarmType()>0){
        if(AlarmInfoTool.isAlarm(carInfoBean)){
//            mViewHolder.item_car_icon_status.setVisibility(View.VISIBLE);
            mViewHolder.item_car_alarm_status.setVisibility(View.VISIBLE);
        }else {
//            mViewHolder.item_car_icon_status.setVisibility(View.GONE);
            mViewHolder.item_car_alarm_status.setVisibility(View.GONE);
        }

        if(AlarmInfoTool.isExceptionp(carInfoBean.getAlarmType())){
            mViewHolder.item_car_exception_status.setVisibility(View.VISIBLE);
        }else{
            mViewHolder.item_car_exception_status.setVisibility(View.GONE);
        }
    }

    private void initCarStatus(ViewHolder mViewHolder, CarInfoBean carInfoBean){
        /*
        //old
        if(!CarStatusInfoTool.getOnlineStatus(carInfoBean.getStatus())){

            mViewHolder.item_car_platenumber.setTextColor(Color.parseColor("#FFA8A8A8"));
//            mViewHolder.item_car_acc_status.setTextColor(Color.parseColor("#FFA8A8A8"));
            mViewHolder.item_car_acc_status.setText(context.getString(R.string.outline_status));
            mViewHolder.item_car_acc_status.setBackgroundResource(R.drawable.acc_offline_bg);
            mViewHolder.item_car_icon.setImageResource(R.mipmap.icon_list_offline);
        }else{

            mViewHolder.item_car_platenumber.setTextColor(Color.parseColor("#FF3A3A3A"));

            if(CarStatusInfoTool.getAccStatus(carInfoBean.getStatus())){
//                mViewHolder.item_car_acc_status.setTextColor(Color.parseColor("#FF378A37"));
                mViewHolder.item_car_acc_status.setText(context.getString(R.string.acc_run_status));
                mViewHolder.item_car_acc_status.setBackgroundResource(R.drawable.acc_run_bg);
                mViewHolder.item_car_icon.setImageResource(R.mipmap.icon_list_run);
            }else{
//                mViewHolder.item_car_acc_status.setTextColor(Color.parseColor("#FFB52D32"));
                mViewHolder.item_car_acc_status.setText(context.getString(R.string.acc_stop_status));
                mViewHolder.item_car_acc_status.setBackgroundResource(R.drawable.acc_stop_bg);
                mViewHolder.item_car_icon.setImageResource(R.mipmap.icon_list_stop);
            }

        }*/

        // new

        mViewHolder.item_car_acc_status.setVisibility(View.VISIBLE);
        if(CarStatusInfoTool.getAccStatus(carInfoBean.getStatus())){
            mViewHolder.item_car_acc_status.setBackgroundResource(R.mipmap.icon_run);
        }else{
            mViewHolder.item_car_acc_status.setBackgroundResource(R.mipmap.icon_stop);
        }
    }

    public class ViewHolder{
        CheckBox check_item;
        ImageView item_car_icon;
        ImageView item_car_icon_status;
        TextView item_car_platenumber;
        TextView item_car_acc_status;
        TextView item_car_alarm_status;
        TextView item_car_exception_status;
        RelativeLayout check_layout;
    }
}
