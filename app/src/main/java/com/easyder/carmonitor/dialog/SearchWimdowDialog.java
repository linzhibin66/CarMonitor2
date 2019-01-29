package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ListView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.HistorySearchAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-03-28.
 */
public class SearchWimdowDialog {

    private Context context;

    private WindowManager.LayoutParams param;

    private View inflate;

    private WindowManager mWindowManager;
    private boolean isShow = false;

    private ListView search_content;

    public SearchWimdowDialog(Context context) {
        this.context = context;
        inflate = View.inflate(context, R.layout.search_layout, null);
        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        param = new WindowManager.LayoutParams();

        param.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;     // 系统提示类型,重要
        param.format=1;
        param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
        param.flags = param.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        param.flags = param.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制

        param.alpha = 1.0f;

        param.gravity= Gravity.CENTER;   //调整悬浮窗口至左上角
        //以屏幕左上角为原点，设置x、y初始值
        param.x=0;
        param.y=0;

        //设置悬浮窗口长宽数据
        param.width=WindowManager.LayoutParams.MATCH_PARENT;
        param.height=WindowManager.LayoutParams.WRAP_CONTENT;


    }

    private void initView(){
        search_content = (ListView) inflate.findViewById(R.id.search_content);
    }

    public void show(){
        //显示myFloatView图像
        mWindowManager.addView(inflate, param);
        isShow = true;

    }

    public boolean isShow() {
        return isShow;

    }

    public void dismiss(){
        if(isShow) {
            mWindowManager.removeView(inflate);
            isShow = false;
        }
    }


}
