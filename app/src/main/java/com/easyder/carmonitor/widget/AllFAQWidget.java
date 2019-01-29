package com.easyder.carmonitor.widget;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.FAQAdapter;
import com.easyder.carmonitor.adapter.FaqGridViewAdapter;
import com.easyder.carmonitor.interfaces.AllFaqBackListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-12.
 */
public class AllFAQWidget extends FAQWidget{

    private FaqGridViewAdapter headAdapter;

    public AllFAQWidget(int type, Context context, View rootView, AllFaqBackListener listener) {
        super(type, context, rootView, listener);
    }

    public AllFAQWidget(int type, Context context, AllFaqBackListener listener) {
        super(type, context, listener);
    }

    @Override
    public void updata() {
        super.updata();
        initHeadView();
    }

    private void initHeadView(){
        View faq_headview = View.inflate(context, R.layout.faq_headview, null);

        LineGridView faq_type_layout = (LineGridView) faq_headview.findViewById(R.id.faq_type_layout);
        headAdapter = new FaqGridViewAdapter(context);
        faq_type_layout.setAdapter(headAdapter);

        faq_list.addHeaderView(faq_headview);

        faq_type_layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listener!= null){
                    ToastUtil.showShort("faq type = " + position);
                    ((AllFaqBackListener) listener).onHeadClickItem(position, headAdapter!= null ? (String)headAdapter.getItem(position): context.getString(R.string.faq_title));
                }
            }
        });
    }
}
