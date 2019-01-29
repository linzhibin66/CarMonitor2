package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.AllCarCheckBoxChanged;
import com.easyder.carmonitor.interfaces.AllCarClickPlateNumberListener;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.InfoTool.AlarmInfoTool;
import com.shinetech.mvp.network.UDP.InfoTool.CarStatusInfoTool;
import com.shinetech.mvp.network.UDP.InfoTool.CarTypeTool;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.presenter.AllCarListPresenter;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarBaseInfoBean;

import java.util.List;

/**
 * Created by ljn on 2017/3/1.
 */
public class ClassifyCarInfoAdater extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private List<CarInfoBean> carInfoBeanList;

    private Context context;

    private final boolean debug = true;

    private AllCarCheckBoxChanged mAllCarCheckBoxChanged;
    private AllCarListPresenter presenter;

    private AllCarClickPlateNumberListener mClickPlateNumberListener;

    /**
     * 编辑模式标记，是否显示选择框
     */
    private boolean isEditMode = true;

    public ClassifyCarInfoAdater(Context context, List<CarInfoBean> carInfoBeanList, AllCarListPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
        this.carInfoBeanList = carInfoBeanList;
    }

    public void update(List<CarInfoBean> carInfoBeanList){
        this.carInfoBeanList = carInfoBeanList;
        notifyDataSetChanged();
    }

    public List<CarInfoBean> getDataList(){

        return carInfoBeanList;

    }

    /**
     * 是否进入编辑模式
     * @return
     */
    public boolean isEditMode(){
        return isEditMode;
    }

    /**
     * 设置编辑模式
     * @param isEditMode
     */
    public void setEditMode(boolean isEditMode){
        this.isEditMode = isEditMode;
    }

    @Override
    public int getCount() {
        return carInfoBeanList.size();
    }

    @Override
    public Object getItem(int position) {

        if(carInfoBeanList.size()>position){
            return carInfoBeanList.get(position);

        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        System.out.println(" convertView  = "+convertView + "  position = "+position);

        ViewHolder mViewHolder;
        if(convertView != null){
            mViewHolder = (ViewHolder) convertView.getTag();
        }else{
            convertView = View.inflate(context, R.layout.classify_carlist_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.check_item = (CheckBox) convertView.findViewById(R.id.check_item);
            mViewHolder.item_car_icon = (ImageView) convertView.findViewById(R.id.item_car_icon);
            mViewHolder.item_car_icon_status = (ImageView) convertView.findViewById(R.id.item_car_icon_status);
            mViewHolder.classify_carlist_arrows = (ImageView) convertView.findViewById(R.id.classify_carlist_arrows);
            mViewHolder.item_car_platenumber = (TextView) convertView.findViewById(R.id.item_car_platenumber);
            mViewHolder.item_car_acc_status = (TextView) convertView.findViewById(R.id.item_car_acc_status);
            mViewHolder.item_car_alarm_status = (TextView) convertView.findViewById(R.id.item_car_alarm_status);
            mViewHolder.item_car_exception_status = (TextView) convertView.findViewById(R.id.item_car_exception_status);
            mViewHolder.check_layout = (RelativeLayout) convertView.findViewById(R.id.check_layout);
            convertView.setTag(mViewHolder);

        }

        CarInfoBean carInfoBean = carInfoBeanList.get(position);


        mViewHolder.item_car_platenumber.setText(carInfoBean.getPlateNumber());
        mViewHolder.item_car_platenumber.setTag(position);
        mViewHolder.item_car_platenumber.setOnClickListener(this);

        initCarStatus(mViewHolder, carInfoBean);

        // 初始化车辆图标
        int listCarIcon;

        ResponseCarBaseInfoBean carBaseInfo = UserInfo.getInstance().getCarBaseInfo(carInfoBean.getPlateNumber());

        if((carBaseInfo != null) && (!TextUtils.isEmpty(carBaseInfo.getCarType()))) {

            listCarIcon = CarTypeTool.getListCarIcon(carBaseInfo.getCarType());

        }else{
            listCarIcon = R.mipmap.icon_allcar_car;
        }

        mViewHolder.item_car_icon.setImageResource(listCarIcon);

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

        if(isEditMode) {
            mViewHolder.check_item.setVisibility(View.VISIBLE);
            mViewHolder.check_layout.setVisibility(View.VISIBLE);
            mViewHolder.check_item.setOnCheckedChangeListener(null);
            mViewHolder.classify_carlist_arrows.setVisibility(View.GONE);
            mViewHolder.check_item.setTag(carInfoBean);
            if (presenter.getSelectCarInfo(carInfoBean.getPlateNumber()) != null) {
                mViewHolder.check_item.setChecked(true);
            } else {
                mViewHolder.check_item.setChecked(false);
            }

            mViewHolder.check_item.setOnCheckedChangeListener(this);

            mViewHolder.check_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkview = (CheckBox) v.findViewById(R.id.check_item);
                    boolean checked = checkview.isChecked();
                    checkview.setChecked(!checked);
                }
            });
        }else{
            mViewHolder.check_item.setVisibility(View.GONE);
            mViewHolder.check_layout.setVisibility(View.GONE);
            mViewHolder.check_item.setOnCheckedChangeListener(null);
            mViewHolder.classify_carlist_arrows.setVisibility(View.VISIBLE);
        }

        return convertView;
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

        if(CarStatusInfoTool.getAccStatus(carInfoBean.getStatus())){
            mViewHolder.item_car_acc_status.setBackgroundResource(R.mipmap.icon_run);
        }else{
            mViewHolder.item_car_acc_status.setBackgroundResource(R.mipmap.icon_stop);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        CarInfoBean tag = (CarInfoBean) buttonView.getTag();

        boolean isexectResult = false;

        if(isChecked){
            isexectResult = presenter.addCarToShow(tag);
        }else{
            isexectResult = presenter.removeSelectCarInfo(tag);
        }

        if(isexectResult && mAllCarCheckBoxChanged !=null){
            mAllCarCheckBoxChanged.onChanged();
        }

    }

    public void setmAllCarCheckBoxChanged(AllCarCheckBoxChanged mAllCarCheckBoxChanged){
        this.mAllCarCheckBoxChanged = mAllCarCheckBoxChanged;
    }

    public void clickItem(View v){

        if(isEditMode) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof ViewHolder) {
                ViewHolder mViewHolder = (ViewHolder) tag;
                boolean checked = mViewHolder.check_item.isChecked();
                mViewHolder.check_item.setChecked(!checked);
            } else {
                CheckBox check = (CheckBox) v.findViewById(R.id.check_item);
                boolean checked = check.isChecked();
                check.setChecked(!checked);

            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v instanceof TextView){
            TextView platenumberview = (TextView) v;
            Object tag = platenumberview.getTag();
            if(tag != null && tag instanceof Integer){
                int position = (int) tag;
                CarInfoBean carInfoBean = carInfoBeanList.get(position);
                if(mClickPlateNumberListener != null){
                    mClickPlateNumberListener.onClickPlateNumber(carInfoBean);
                }
            }else{
                String platenumber = platenumberview.getText().toString().trim();
                if(!TextUtils.isEmpty(platenumber)){
                    for(CarInfoBean carInfoBean : carInfoBeanList){
                        if(carInfoBean.getPlateNumber().equals(platenumber)){
                            if(mClickPlateNumberListener != null){
                                mClickPlateNumberListener.onClickPlateNumber(carInfoBean);
                            }
                            return;
                        }
                    }
                }

            }
        }
    }

    public void setClickPlateNumber(AllCarClickPlateNumberListener mClickPlateNumberListener){
        this.mClickPlateNumberListener = mClickPlateNumberListener;
    }

    class ViewHolder{
        CheckBox check_item;
        ImageView item_car_icon;
        ImageView item_car_icon_status;
        ImageView classify_carlist_arrows;
        TextView item_car_platenumber;
        TextView item_car_exception_status;
        TextView item_car_alarm_status;
        TextView item_car_acc_status;
        RelativeLayout check_layout;
    }

}
