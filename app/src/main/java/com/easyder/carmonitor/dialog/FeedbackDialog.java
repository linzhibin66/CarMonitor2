package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.FeedbackListener;
import com.easyder.carmonitor.presenter.MainActivityPresenter;
import com.easyder.carmonitor.widget.FeedbackWidget;
//import com.orhanobut.dialogplus.DialogPlus;
//import com.orhanobut.dialogplus.OnBackPressListener;
//import com.orhanobut.dialogplus.ViewHolder;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.UIUtils;


/**
 * Created by ljn on 2017-04-12.
 */
public class FeedbackDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    private FeedbackWidget feedbackWidget;

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private MainActivityPresenter presenter;

//    private DialogPlus feedbackDialog;

    private ProgressDialog progressDialog;

    private View boundView;

    private boolean isTouch = false;

    private boolean isDebug = false && LogUtils.isDebug;

    public FeedbackDialog(final Context context, MainActivityPresenter presenter) {
        super(context, R.layout.feedback_layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));
//        super(context, R.layout.feedback_layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.presenter = presenter;

        feedbackWidget = new FeedbackWidget(context, getLayout(), new FeedbackListener() {
            @Override
            public void onBack() {
                feedbackWidget.hideInput();
                dismiss();
                isTouch = true;

            }

            @Override
            public void feedbackCommit(String message) {
                if(progressDialog == null){
                    progressDialog = new ProgressDialog(context);
                }

                progressDialog.show(boundView);

//                TODO  commit  opresenter.
                FeedbackDialog.this.presenter.feedbackMessage(message, new ResponseListener() {
                    @Override
                    public void onSuccess(LoadResult successResult) {
                        if(isDebug)System.out.println("onSuccess  feedbackMessage  - - - - - - - - - - - ");
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(LoadResult errorResult) {
                        if(isDebug)System.out.println("onError  feedbackMessage  - - - - - - - - - - - ");
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
        feedbackWidget.setOutmostTouchListener(this);

//        faqWidget.updata();

        setALLWindow();

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        setFocusable(true);

        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));
    }

    public void show(View v){
        isTouch = false;
        boundView = v;
        if(isshowPopupWindow) {
            show(v, Gravity.LEFT | Gravity.TOP, 0, 0);
            //显示内容时，执行以下动画
            getLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open));
        }else{
            dialogPlusShow();
        }

    }

    public void dialogPlusShow(){

       /* ViewHolder viewHolder = new ViewHolder(feedbackWidget.getView());

        feedbackDialog = DialogPlus.newDialog(context)
                .setContentHolder(viewHolder)
                .setGravity(Gravity.LEFT)
                .setInAnimation(R.anim.pop_left2right_anim_open)
                .setOutAnimation(R.anim.pop_left2right_anim_close)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        feedbackDialog.dismiss();
                    }
                })
                .setCancelable(true).create();

        feedbackDialog.show();*/
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
          /*  if(feedbackDialog != null && feedbackDialog.isShowing()){
                feedbackDialog.dismiss();
            }else {*/
                dismiss();
//            }
            isTouch = true;
        }
        return true;
    }
}
