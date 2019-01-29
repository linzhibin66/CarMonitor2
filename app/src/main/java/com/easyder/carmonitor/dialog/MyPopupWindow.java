package com.easyder.carmonitor.dialog;

import android.view.View;
import android.view.animation.Animation;
import android.widget.PopupWindow;

/**
 * Created by ljn on 2017/2/27.
 */
public class MyPopupWindow extends PopupWindow{

    public MyPopupWindow(View contentView) {
        super(contentView);
    }

    public MyPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public MyPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    private PopAmimationListener mPopAmimationListener;

    private boolean isDismiss = true;

    @Override
    public void dismiss() {

        if(isDismiss){
            return;
        }

        isDismiss = true;
        if(mPopAmimationListener !=null){
            mPopAmimationListener.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mPopAmimationListener.getAnimationlayout().post(new Runnable() {
                        @Override
                        public void run() {
                            MyPopupWindow.super.dismiss();
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mPopAmimationListener.startAnimtaion();
        }else{
            super.dismiss();
        }
    }

    public void noAmimationDismiss(){
        super.dismiss();
    }

    public void setPopAmimationListener(PopAmimationListener mPopAmimationListener){
        this.mPopAmimationListener = mPopAmimationListener;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        isDismiss = false;
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        isDismiss = false;
    }

}
