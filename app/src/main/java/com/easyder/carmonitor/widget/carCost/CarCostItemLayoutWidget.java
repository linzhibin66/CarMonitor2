package com.easyder.carmonitor.widget.carCost;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.shinetech.mvp.DB.bean.CostDetails;
import com.shinetech.mvp.network.UDP.bean.orderBean.CarCostItemInfoVo;

/**
 * Created by ljn on 2017-12-12.
 */

public class CarCostItemLayoutWidget {

    private Context context;

    private View rootItem;

    private TextView item_layout_title;

    private LinearLayout carcost_items_content;

    private CarCostItemInfoVo carCostItemInfo;

    public CarCostItemLayoutWidget(Context context,CarCostItemInfoVo carCostItemInfo) {
        this.context = context;
        this.carCostItemInfo = carCostItemInfo;

        rootItem = View.inflate(context, R.layout.carcost_item_layout, null);

        initView();

    }

    private void initView(){

        item_layout_title = (TextView) rootItem.findViewById(R.id.item_layout_title);
        carcost_items_content = (LinearLayout) rootItem.findViewById(R.id.carcost_items_content);

        item_layout_title.setText(carCostItemInfo.getPayItems());

        addItems();

    }

    public View getItemLayout(){
        return rootItem;
    }

    private void addItems(){

        //添加月租
        CarCostItemWidget carCostItemMonthlyRent = new CarCostItemWidget(context);
        carCostItemMonthlyRent.setTitle(context.getString(R.string.monthly_rent));
        carCostItemMonthlyRent.setValue("￥"+(carCostItemInfo.getMonthlyRent()));
        carcost_items_content.addView(carCostItemMonthlyRent.getView());

        //添加预存金额
        CarCostItemWidget carCostItemDepositedamount = new CarCostItemWidget(context);
        carCostItemDepositedamount.setTitle(context.getString(R.string.deposited_amount));
        carCostItemDepositedamount.setValue("￥"+(carCostItemInfo.getDepositedamount()));
        carcost_items_content.addView(carCostItemDepositedamount.getView());

        //添加赠送金额
        CarCostItemWidget carCostItemGiftamount = new CarCostItemWidget(context);
        carCostItemGiftamount.setTitle(context.getString(R.string.giftamount));
        carCostItemGiftamount.setValue("￥"+(carCostItemInfo.getGiftamount()));
        carcost_items_content.addView(carCostItemGiftamount.getView());

        //添加累计历史欠费
        CarCostItemWidget historyArrearsItem = new CarCostItemWidget(context);
        historyArrearsItem.setTitle(context.getString(R.string.history_arrears));
        historyArrearsItem.setValue("￥"+(carCostItemInfo.getHistoryArrears()));
        carcost_items_content.addView(historyArrearsItem.getView());

        //添加本月减免
        CarCostItemWidget currentMonthDerateItem = new CarCostItemWidget(context);
        currentMonthDerateItem.setTitle(context.getString(R.string.current_month_derate));
        currentMonthDerateItem.setValue("￥"+(carCostItemInfo.getCurrentMonthDerate()));
        carcost_items_content.addView(currentMonthDerateItem.getView());

        //添加本月抵扣预存额
        CarCostItemWidget deductionDepositedAmountItem = new CarCostItemWidget(context);
        deductionDepositedAmountItem.setTitle(context.getString(R.string.current_month_gift));
        deductionDepositedAmountItem.setValue("￥"+(carCostItemInfo.getCurrentMonthGift()));
        carcost_items_content.addView(deductionDepositedAmountItem.getView());

        //添加本月抵扣赠送额
        CarCostItemWidget deductionGiftAmountItem = new CarCostItemWidget(context);
        deductionGiftAmountItem.setTitle(context.getString(R.string.current_charge_monthlyrent));
        deductionGiftAmountItem.setValue("￥"+(carCostItemInfo.getCurrentChargeMonthlyRent()));
        carcost_items_content.addView(deductionGiftAmountItem.getView());

      /*  //添加预存金额
        CarCostItemWidget chargeHistoryArrearsItem = new CarCostItemWidget(context);
        chargeHistoryArrearsItem.setTitle(context.getString(R.string.charge_history_arrears));
        chargeHistoryArrearsItem.setValue("￥"+(carCostItemInfo.getChargeHistoryArrears()/100));
        carcost_items_content.addView(chargeHistoryArrearsItem.getView());*/

        //添加预存金额
        CarCostItemWidget startDateItem = new CarCostItemWidget(context);
        startDateItem.setTitle(context.getString(R.string.start_date));
        startDateItem.setValue(carCostItemInfo.getStartDate());
        startDateItem.setValueColor(R.color.carcost_item_title_textcolor);
        carcost_items_content.addView(startDateItem.getView());

        //添加预存金额
        CarCostItemWidget endDateItem = new CarCostItemWidget(context);
        endDateItem.setTitle(context.getString(R.string.end_date));
        endDateItem.setValue(carCostItemInfo.getEndDate());
        endDateItem.setValueColor(R.color.carcost_item_title_textcolor);
        carcost_items_content.addView(endDateItem.getView());



    }
}
