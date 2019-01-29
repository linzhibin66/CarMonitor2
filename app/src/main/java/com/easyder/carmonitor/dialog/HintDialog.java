package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.LayoutBackListener;

/**
 * Created by ljn on 2017-05-16.
 */
public class HintDialog extends BasePopupWindowDialog implements View.OnTouchListener {

    private Button hint_dialog_enter;

    private LayoutBackListener listener;

    public HintDialog(Context context, LayoutBackListener listener) {
        super(context, R.layout.base_hint_dialog, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.listener = listener;
        initView();
        setALLWindow();

    }

    private void initView(){
        View layout = getLayout();
        layout.setOnTouchListener(this);
        hint_dialog_enter = (Button) layout.findViewById(R.id.hint_dialog_enter);

        hint_dialog_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

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
