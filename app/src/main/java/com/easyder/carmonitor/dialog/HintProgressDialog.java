package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyder.carmonitor.R;

/**
 * Created by ljn on 2017-03-27.
 */
public class HintProgressDialog extends BasePopupWindowDialog implements View.OnTouchListener {


    public HintProgressDialog(Context context) {
        super(context, R.layout.my_hint_progressdialog, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initView();
        setALLWindow();

    }

    private void initView(){
        View layout = getLayout();
        layout.setOnTouchListener(this);
        TextView hint_tv = (TextView) layout.findViewById(R.id.hint_tv);
        hint_tv.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public void show(View v){

        show(v, Gravity.CENTER, 0, 0);
        //显示内容时，执行以下动画
//        timepick_content.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_left2right_anim_open));
    }

}
