package com.easyder.carmonitor.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.NavigationBarPagerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/3/1.
 */
public class NavigationBar implements ViewPager.OnPageChangeListener {

    private Context context;

    /**
     * 界面布局
     */
    private View layout;

    /**
     * 状态条
     */
    private View status_bar;

    /**
     * 屏幕宽度
     */
    private int screenWidth;

    /**
     *
     */
    private ViewPager viewPager;

    private EditText allcar_search_et;

    /**
     * ViewPager的当前选中页
     */
    private int currentIndex;

    /**
     * 标题个数
     */
    private int statusCount = 0;

    /**
     * 标题栏容器
     */
    private LinearLayout status_layout;

    /**
     * 标题界面
     */
    private List<View> itemlist = new ArrayList<>();

    /**
     * 适配器
     */
    private PagerAdapter mPagerAdapter;

    private final boolean isShowSearch = false;

    private int lineWidth = -1;

    /**
     * 分类切换监听
     */
    private NavigationBarPagerListener mPagerListener;

    private TextWatcher mTextWatcher;

    private View.OnClickListener itemsListener;

    public NavigationBar(Context context, List<View> viewList,PagerAdapter adapter) {
        this.context = context;
        this.mPagerAdapter = adapter;

        layout = View.inflate(context, R.layout.navigationbar_layout, null);

        initView();

        addItems(viewList);

        initLine();
    }

    public int getStatusCount(){
        return statusCount;
    }

    /**
     * 添加导航布局
     * @param view
     */
    private void addItem(View view){
        itemlist.add(view);
        statusCount++;

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if(layoutParams == null || !(layoutParams instanceof LinearLayout.LayoutParams)){
            layoutParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        }else{
            ((LinearLayout.LayoutParams)layoutParams).width = 0;
        }

        ((LinearLayout.LayoutParams)layoutParams).weight = 1;

        setViewCliclListener(view);

        status_layout.addView(view, layoutParams);
    }

    /**
     * 批量添加导航布局
     * @param viewList
     */
    private void addItems(List<View> viewList){
        itemlist.clear();

        if(viewList==null)
            return;

        for(View item : viewList){
            addItem(item);
        }
    }

    /**
     * 设置每个导航的点击事件
     * @param view
     */
    private void setViewCliclListener(View view){

        if(itemsListener == null){

            itemsListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = itemlist.indexOf(v);
                    if(viewPager.getCurrentItem() == index){
                        return;
                    }else{
                        viewPager.setCurrentItem(index, true);
                    }

                }
            };
        }

        view.setOnClickListener(itemsListener);

    }

    /**
     * 获取指定位置的导航布局
     * @param index
     * @return
     */
    public View getStatusItem(int index){

        if(itemlist.size()>index){
            return itemlist.get(index);
        }

        return null;

    }

    public View getLayout(){
        return layout;
    }

    /**
     * 初始化基础布局
     */
    private void initView(){
        status_bar = layout.findViewById(R.id.status_bar_view);

        status_layout = (LinearLayout) layout.findViewById(R.id.status_layout);

        viewPager = (ViewPager) layout.findViewById(R.id.allcarlist_viewpager);

        if(isShowSearch) {

            allcar_search_et = (EditText) layout.findViewById(R.id.allcar_search_et);
        }else{
            RelativeLayout allcar_search_layout = (RelativeLayout) layout.findViewById(R.id.allcar_search_layout);
            allcar_search_layout.setVisibility(View.GONE);
        }

        if(mPagerAdapter==null){
            return;
        }
        viewPager.setAdapter(mPagerAdapter);

        viewPager.addOnPageChangeListener(this);

    }

    public void setSearchChangedListener(TextWatcher mTextWatcher){
        if(isShowSearch) {
            this.mTextWatcher = mTextWatcher;
            allcar_search_et.addTextChangedListener(mTextWatcher);
        }
    }

    public void cleanSearchContent(){
        if(isShowSearch) {
//        TODO
            allcar_search_et.removeTextChangedListener(mTextWatcher);
            allcar_search_et.setText("");
            allcar_search_et.addTextChangedListener(mTextWatcher);
//        TODO
        }
    }

    /**
     * 设置每个导航内容的适配器
     * @param pagerAdapter
     */
    public void setPagerAdapter(PagerAdapter pagerAdapter){
        this.mPagerAdapter = pagerAdapter;
        viewPager.setAdapter(mPagerAdapter);

        viewPager.addOnPageChangeListener(this);

    }

    /**
     * 将指定位置设置为当前显示。
     * @param index 所指定的位置
     */
    public void setCurrentItem(int index){
        if(index<statusCount){
            viewPager.setCurrentItem(index);
        }
    }

    /**
     * 获取当前显示的位置
     */
    public int getCurrentIndex(){
        return viewPager.getCurrentItem();
    }

    /**
     * 初始化底部线条
     */
    private void initLine(){
        DisplayMetrics dpMetrics = new DisplayMetrics();
        ((Activity)context).getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);

        int widthPixels = dpMetrics.widthPixels;
        int heightPixels = dpMetrics.heightPixels;

        if(widthPixels>heightPixels){
            screenWidth = heightPixels;
        }else{
            screenWidth = widthPixels;
        }
//        screenWidth = dpMetrics.widthPixels;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) status_bar
                .getLayoutParams();
        if(lineWidth != -1){
            lp.width = lineWidth;
            int maxwidth = screenWidth / statusCount;

            if(maxwidth>lineWidth){
                lp.leftMargin =  (maxwidth-lineWidth)/2;
            }
        }else{
            lp.width = screenWidth / statusCount;
        }

        status_bar.setLayoutParams(lp);
    }

    public void setStatusLineWidth(int lineWidth){
        this.lineWidth = lineWidth;
        initLine();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) status_bar
                .getLayoutParams();

        /**
         * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
         * 设置mTabLineIv的左边距 滑动场景：
         * 记3个页面,
         * 从左到右分别为0,1,2
         * 0->1; 1->2; 2->1; 1->0
         */
        //当线的宽比最大设置的宽度小时，需要做居中处理
        int displacement = 0;
        if(lineWidth != -1) {
            int maxwidth = screenWidth / statusCount;

            if (maxwidth > lineWidth) {
                displacement = (maxwidth - lineWidth) / 2;
            }
        }

        if(currentIndex>position){
            lp.leftMargin = (int) (-(1 - positionOffset)
                    * (screenWidth * 1.0 / statusCount) + currentIndex
                    * (screenWidth / statusCount))+displacement;
        }else{
            lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / statusCount) + currentIndex
                    * (screenWidth / statusCount))+displacement;
        }
        status_bar.setLayoutParams(lp);

    }

    @Override
    public void onPageSelected(int position) {
        currentIndex = position;
        if(mPagerListener != null){
            mPagerListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setPagerListener(NavigationBarPagerListener listener){
        mPagerListener = listener;
    }
}
