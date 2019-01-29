package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by ljn on 2017/2/27.
 */
public class PopAmimationListener {

    private View animationlayout;
    private int animtaionStyle;
    private Animation animation;
    private Animation.AnimationListener mAnimationListener;

    public PopAmimationListener(Context context, View animationlayout, int animtaionStyle) {
        this.animationlayout = animationlayout;
        this.animtaionStyle = animtaionStyle;
        animation = AnimationUtils.loadAnimation(context, animtaionStyle);
    }

    public void setAnimationListener(Animation.AnimationListener mListener){
        animation.setAnimationListener(mListener);
    }

    public void startAnimtaion(){
        animationlayout.startAnimation(animation);
    }

    public View getAnimationlayout(){
        return animationlayout;
    }
}
