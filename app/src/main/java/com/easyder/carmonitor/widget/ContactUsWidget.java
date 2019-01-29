package com.easyder.carmonitor.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-28.
 */
public class ContactUsWidget {

    private Context context;

    private LayoutBackListener listener;

    private View rootView;

    private RelativeLayout about_layout_outmost;

    public ContactUsWidget(Context context, LayoutBackListener listener) {
        this.context = context;
        this.listener = listener;
        rootView = View.inflate(context, R.layout.activity_contact_us, null);
        initTitle(rootView);
        initView();
    }

    public ContactUsWidget(Context context, View rootView, LayoutBackListener listener) {
        this.context = context;
        this.listener = listener;
        this.rootView = rootView;
        initTitle(rootView);
        initView();
    }

    public View getView(){
        return rootView;
    }

    private void initTitle(View rootview){

        ImageButton title_back = (ImageButton) rootview.findViewById(R.id.title_back);
        TextView title_text = (TextView) rootview.findViewById(R.id.title_text);
        ImageButton title_search = (ImageButton) rootview.findViewById(R.id.title_search);

        title_text.setText(context.getString(R.string.app_contact_us));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

        title_search.setVisibility(View.GONE);

        RelativeLayout title_layout = (RelativeLayout) rootview.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }

    public void initView(){
        about_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.about_layout_outmost);
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(about_layout_outmost != null) {
            about_layout_outmost.setOnTouchListener(touchListener);
        }
    }
}
