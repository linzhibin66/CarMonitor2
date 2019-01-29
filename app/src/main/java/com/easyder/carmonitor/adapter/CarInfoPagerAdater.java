package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;

import com.easyder.carmonitor.interfaces.AllCarClickListener;
import com.easyder.carmonitor.interfaces.AllCarSelectChanged;
import com.easyder.carmonitor.widget.ClassifyCarInfoWidget;
import com.shinetech.mvp.network.UDP.InfoTool.AllCarListClassify;
import com.shinetech.mvp.network.UDP.presenter.AllCarListPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/3/1.
 */
public class CarInfoPagerAdater extends PagerAdapter{

    private int count;
    List<ClassifyCarInfoWidget> widgetList = new ArrayList<>();

    private Context context;

    private AllCarSelectChanged mAllCarSelectChanged;

    private AllCarListPresenter presenter;

    public CarInfoPagerAdater(Context context,AllCarListClassify mAllCarListClassify,int count, AllCarListPresenter presenter) {
        this.count = count;
        this.context = context;
        this.presenter = presenter;
       initData(mAllCarListClassify);
    }

    /**
     * 初始化车辆分类的内容
     * @param mAllCarListClassify
     */
    private void initData(AllCarListClassify mAllCarListClassify){
        for(int i = 0; i<count; i++){
            ClassifyCarInfoWidget classifyCarInfoWidget = new ClassifyCarInfoWidget(context, i, mAllCarListClassify,presenter);
            widgetList.add(classifyCarInfoWidget);
        }
    }

    /**
     * 更新指定位置的车辆列表信息
     * @param position
     * @param mAllCarListClassify
     */
    public void updatePsoition(int position, AllCarListClassify mAllCarListClassify){
        widgetList.get(position).update(position,mAllCarListClassify);
    }

    public void onSearchChanged(int position, String changed){
        widgetList.get(position).updateSearch(changed);
    }

    public void onPageSelectedUpdate(int postion){
        widgetList.get(postion).initData();
    }

    /**
     * 设置编辑模式，选择车辆
     * @param isEditMode
     */
    public void setEditMode(boolean isEditMode){
        if(widgetList != null && widgetList.size()>0) {
            for (ClassifyCarInfoWidget classifyCarInfoWidget : widgetList) {
                classifyCarInfoWidget.setEditMode(isEditMode);
            }
        }
    }

    public void setItemListener(AllCarClickListener mAllCarClickListener){
        if(widgetList != null && widgetList.size()>0) {
            for (ClassifyCarInfoWidget classifyCarInfoWidget : widgetList) {
                classifyCarInfoWidget.setItemListener(mAllCarClickListener);
            }
        }
    }

    /**
     * 设置选择车辆监听
     * @param mAllCarSelectChanged
     */
    public void setAllCarSelectChanged(AllCarSelectChanged mAllCarSelectChanged){
        this.mAllCarSelectChanged = mAllCarSelectChanged;
        for(ClassifyCarInfoWidget classifyCarInfoWidget : widgetList){
            classifyCarInfoWidget.setAllCarSelectChanged(mAllCarSelectChanged);
        }
    }

    public void selectAll(int persion){
        if(widgetList.size()>persion) {
            widgetList.get(persion).seleclAll();
        }
    }

    public void removeAll(int persion){
        if(widgetList.size()>persion) {
            widgetList.get(persion).removeAll();
        }
    }

    public void clearSelect(int persion){
        widgetList.get(persion).clearSelect();
    }

    public boolean isSelectAll(int persion){
        return widgetList.get(persion).isSelectAll();
    }



    // 获取要滑动的控件的数量，在这里我们以滑动的广告栏为例，那么这里就应该是展示的广告图片的ImageView数量
    @Override
    public int getCount() {
        return count;
    }

    // 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(widgetList.get(position).getView());
    }

    // 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = widgetList.get(position).getView();
        container.addView(view);
        return view;
    }
}
