package com.easyder.carmonitor.widget.orderManager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.InstallChangeStatusListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.UpLoadResultDialogListener;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;
import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.utils.UIUtils;
import com.shinetech.mvp.view.MvpView;

import java.util.List;

/**
 * Created by ljn on 2017-12-05.
 */

public class InstallOrderInfoWidget<M extends BaseVo> implements MvpView {

    private Context context;

    private View rootView;

    private InstallOrderBaseInfo installOrderitem;

    private LayoutBackListener listener;

    private InstallChangeStatusListener statusListener;

    private RelativeLayout orderinfo_layout_outmost;

    private RelativeLayout orderinfo_hint_layout;

    private RelativeLayout content_layout;

    private InstallOrderInfoContentWidget mInstallOrderInfoContentWidget;

    private CommitMaintenanceResultDialogWidget mCommitDialogWidget;

    public InstallOrderInfoWidget(Context context, View rootView, InstallOrderBaseInfo installOrderitem, LayoutBackListener listener) {
        this.context = context;
        this.rootView = rootView;
        this.installOrderitem = installOrderitem;
        this.listener = listener;

        initTitle();
        initLayout();
    }

    public void setInstallChangeStatusListener(InstallChangeStatusListener statusListener){
        this.statusListener = statusListener;

        if(mInstallOrderInfoContentWidget != null){
            mInstallOrderInfoContentWidget.setInstallChangeStatusListener(this.statusListener);
        }
    }

    private void initTitle(){

        ImageButton title_back = (ImageButton) rootView.findViewById(R.id.title_back);
        TextView title_text = (TextView) rootView.findViewById(R.id.title_text);
        ImageButton title_search = (ImageButton) rootView.findViewById(R.id.title_search);

        String title = context.getString(R.string.install_order_info_title);

        title_text.setText(title);

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

        title_search.setVisibility(View.GONE);

        RelativeLayout title_layout = (RelativeLayout) rootView.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }

    private void initLayout(){

        initStatus();
        orderinfo_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.install_orderinfo_layout_outmost);

        orderinfo_hint_layout = (RelativeLayout) rootView.findViewById(R.id.orderinfo_hint_layout);

        content_layout = (RelativeLayout) rootView.findViewById(R.id.install_content_layout);

        mInstallOrderInfoContentWidget = new InstallOrderInfoContentWidget(context,installOrderitem);
        mInstallOrderInfoContentWidget.setInstallChangeStatusListener(statusListener);

        content_layout.addView(mInstallOrderInfoContentWidget.getView());


    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(orderinfo_layout_outmost != null) {
            orderinfo_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    int count = 0;

    private void initStatus(){

        ImageView install_title_status_image = (ImageView) rootView.findViewById(R.id.install_title_status_image);

        TextView install_status_title = (TextView) rootView.findViewById(R.id.install_status_title_tv);
        TextView install_title_hint = (TextView) rootView.findViewById(R.id.install_title_hint_tv);
        TextView install_order_info_status_ctrl_button = (TextView) rootView.findViewById(R.id.install_order_info_status_ctrl_button);

        int orderStatus = installOrderitem.getOrderStatus();
        switch (orderStatus){
            case InstallOrderBaseInfo.WAITING_ORDER_RECEIVING_STATUS:
                install_title_status_image.setImageResource(R.mipmap.icon_wait_accept_order);
                install_status_title.setText(R.string.maintenance_order_status_wait_accept_order);
                install_title_hint.setText(R.string.install_order_info_accept_order_hint);
                install_order_info_status_ctrl_button.setVisibility(View.VISIBLE);
                install_order_info_status_ctrl_button.setText(R.string.accept_order);
                install_order_info_status_ctrl_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(InstallOrderInfoWidget.this.statusListener != null){
                            InstallOrderInfoWidget.this.statusListener.acceptOrder(installOrderitem);
                        }
                    }
                });

                break;
            case InstallOrderBaseInfo.ORDER_RECEIVING_STATUS:

                install_title_status_image.setImageResource(R.mipmap.icon_have_accept_order);
                install_status_title.setText(R.string.maintenance_order_status_have_accept_order);
                install_title_hint.setText(R.string.install_order_info_have_accept_order_hint);
                install_order_info_status_ctrl_button.setVisibility(View.VISIBLE);
                install_order_info_status_ctrl_button.setText(R.string.have_finish_install);

                List<InstallTerminalnfo> terminallist = installOrderitem.getTerminallist("");

                boolean isCanCommit = true;
                for(InstallTerminalnfo item : terminallist){
                    if(!item.isIntegrityInfo()){
                        isCanCommit = false;
                        break;
                    }
                }

                if(isCanCommit) {

                    install_order_info_status_ctrl_button.setBackgroundResource(R.color.maintenance_order_info_status_ctrl_button_bg);

                    install_order_info_status_ctrl_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        /*if(InstallOrderInfoWidget.this.statusListener != null){
                            InstallOrderInfoWidget.this.statusListener.upLoadInstallResult(installOrderitem);
                        }*/
                            //TODO  up load data

                            if (mCommitDialogWidget == null) {
                                mCommitDialogWidget = new CommitMaintenanceResultDialogWidget(context);
                            }

                            onLoading();

                            if (count / 2 == 0) {
                                showCommitFail("服务器无响应");
                            } else {
                                showCommitSuccess();
                            }
                            count++;


                        }
                    });
                }else{
                    install_order_info_status_ctrl_button.setBackgroundResource(R.color.order_status_disable_bg);
                    install_order_info_status_ctrl_button.setOnClickListener(null);
                }
                break;
            case InstallOrderBaseInfo.FINISH_STATUS:
                install_title_status_image.setImageResource(R.mipmap.icon_finish_accept_order);
                install_status_title.setText(R.string.maintenance_order_status_finish_order);
                install_title_hint.setText(R.string.install_order_info_finish_accept_order_hint);
                install_order_info_status_ctrl_button.setVisibility(View.GONE);
                install_order_info_status_ctrl_button.setOnClickListener(null);
                break;

        }

    }

    public void updata(InstallOrderBaseInfo installOrderitem){
        this.installOrderitem = installOrderitem;
        initStatus();
    }

    public void addHint(View view){
        orderinfo_hint_layout.removeAllViews();
        orderinfo_hint_layout.addView(view);
        orderinfo_hint_layout.setVisibility(View.VISIBLE);
    }

    public void dismissHintLayout(){
        orderinfo_hint_layout.removeAllViews();
        orderinfo_hint_layout.setVisibility(View.GONE);
    }

    public boolean onKeyBack(){
        if(orderinfo_hint_layout != null && orderinfo_hint_layout.getVisibility() == View.VISIBLE){
            dismissHintLayout();
            return true;
        }

        return false;
    }

    @Override
    public void onLoading() {

        mCommitDialogWidget.showLoadingView();
        RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        orderinfo_hint_layout.removeAllViews();
        orderinfo_hint_layout.addView(mCommitDialogWidget.getView(),params);
        orderinfo_hint_layout.setVisibility(View.VISIBLE);
        if(statusListener != null){
            statusListener.upLoadDialogFocusable(false);
        }

    }

    @Override
    public void showContentView(BaseVo dataVo) {

    }

    @Override
    public void onStopLoading() {
        dismissHintLayout();
    }

    private void showCommitFail(String errorMessage){

        if(mCommitDialogWidget == null){
            return;
        }

        mCommitDialogWidget.showFailView(errorMessage, new UpLoadResultDialogListener() {
            @Override
            public void onBack() {
                orderinfo_hint_layout.removeAllViews();
                orderinfo_hint_layout.setVisibility(View.GONE);
                if(statusListener != null){
                    statusListener.upLoadDialogFocusable(true);
                }
            }

            @Override
            public void onReCommit() {
                orderinfo_hint_layout.removeAllViews();
                orderinfo_hint_layout.setVisibility(View.GONE);
                //commit();
                onLoading();
            }
        });

        RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        orderinfo_hint_layout.removeAllViews();
        orderinfo_hint_layout.addView(mCommitDialogWidget.getView(),params);
        orderinfo_hint_layout.setVisibility(View.VISIBLE);
        if(statusListener != null){
            statusListener.upLoadDialogFocusable(false);
        }
    }

    public void showCommitSuccess(){

        if(mCommitDialogWidget == null){
            return;
        }

        mCommitDialogWidget.showSuccessView(new LayoutBackListener() {
            @Override
            public void onBack() {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });
        RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        orderinfo_hint_layout.removeAllViews();
        orderinfo_hint_layout.addView(mCommitDialogWidget.getView(),params);
        orderinfo_hint_layout.setVisibility(View.VISIBLE);
        if(statusListener != null){
            statusListener.upLoadDialogFocusable(true);
        }
    }
}
