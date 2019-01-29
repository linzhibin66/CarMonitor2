package com.easyder.carmonitor.dialog.orderDialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.dialog.PopAmimationListener;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.widget.orderManager.MaintenanceOrderValuationWidget;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-11-10.
 */

public class MaintenanceOrderValuationDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private boolean isTouch = false;

    public boolean needRecorve = false;

    private MaintenanceOrderValuationWidget orderValuationWidget;

    private View bundView;

    private BaseOrderInfoDB baseOrderInfoDB;

    private String plateNumber;

    public MaintenanceOrderValuationDialog(Context context, BaseOrderInfoDB baseOrderInfoDB, String plateNumber) {
        super(context, R.layout.maintenance_order_valuation, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        this.baseOrderInfoDB = baseOrderInfoDB;
        this.plateNumber = plateNumber;

        setALLWindow();

        updateFocusable(true);

       /* this.presenter = (AllCarListPresenter) createPresenter();
        presenter.attachView((MvpView) this);*/
        initLayout();

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));

    }

    private void initLayout(){

        orderValuationWidget = new MaintenanceOrderValuationWidget(context, baseOrderInfoDB, plateNumber,  getLayout(), new LayoutBackListener() {

            @Override
            public void onBack() {
                dismiss();
            }
        });


        orderValuationWidget.setOutmostTouchListener(this);

    }
    public void show(View v){
        bundView = v;
        isTouch = false;
        show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
        //显示内容时，执行以下动画
        getLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open));

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            needRecorve = true;
            dismiss();
            isTouch = true;
        }
        return true;
    }


}
