package com.easyder.carmonitor.interfaces;

/**
 * Created by ljn on 2017-05-11.
 */
public abstract class AllFaqBackListener implements LayoutBackListener{

    /**
     * FAQWidget type
     * @param faq_type
     */
    public abstract void onHeadClickItem(int faq_type, String title);


}
