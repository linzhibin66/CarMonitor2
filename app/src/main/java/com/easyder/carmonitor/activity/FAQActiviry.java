package com.easyder.carmonitor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.easyder.carmonitor.interfaces.AllFaqBackListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.widget.AllFAQWidget;
import com.easyder.carmonitor.widget.FAQWidget;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;

/**
 * Created by ljn on 2017-05-23.
 */
public class FAQActiviry extends BaseActivity implements View.OnTouchListener {

    private FAQWidget faqWidget;

    private boolean isTouch = false;

    private boolean isShowAll = true;

    public final static String FAQ_TYPE = "faq_type_string";
    public final static String FAQ_TITLE = "faq_type_title_string";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isShowAll) {
            faqWidget = new AllFAQWidget(FAQWidget.HOT_FAQ, this, new AllFaqBackListener() {
                @Override
                public void onHeadClickItem(int faq_type, String title) {
                    Intent intent = new Intent(FAQActiviry.this,FAQItemActivity.class);
                    intent.putExtra(FAQ_TYPE,faq_type);
                    intent.putExtra(FAQ_TITLE,title);
                    startActivity(intent);
//                    overridePendingTransition(R.anim.pop_right2left_anim_open, R.anim.pop_right2left_anim_close);
                }

                @Override
                public void onBack() {
                    finish();
                }
            });
        }else {

            faqWidget = new FAQWidget(FAQWidget.HOT_FAQ, this, new LayoutBackListener() {
                @Override
                public void onBack() {
                    finish();

                }
            });
        }

        faqWidget.updata();
        faqWidget.setOutmostTouchListener(this);

        setContentView(faqWidget.getView());


    }

    @Override
    protected int getView() {
        return 0;
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return null;
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void showContentView(BaseVo dataVo) {

    }

    @Override
    public void onStopLoading() {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            finish();
            isTouch = true;
        }
        return true;
    }
}
