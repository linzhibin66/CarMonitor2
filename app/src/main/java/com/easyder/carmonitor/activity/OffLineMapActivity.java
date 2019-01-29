package com.easyder.carmonitor.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.ConfigurationChangedManager;
import com.easyder.carmonitor.adapter.OfflinePagerAdapter;
import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.presenter.MainActivityPresenter;
import com.easyder.carmonitor.widget.OffLineDownLoadManagerWidget;
import com.easyder.carmonitor.widget.OffLineMapListWidget;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.offlineMap.OffLineBaiduMapUtils;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


/**
 * Created by ljn on 2016/11/10.
 */
public class OffLineMapActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

   /* @Bind(R.id.content_view)
    RelativeLayout content_view;*/

    private boolean isDebug = false && LogUtils.isDebug;

    @Bind(R.id.offline_manager)
    ViewPager offline_manager;

    @Bind(R.id.offline_title)
    RadioGroup offline_title_RadioGroup;

    @Bind(R.id.offline_title_map)
    RadioButton offline_title_map;

    @Bind(R.id.offline_title_download_manage)
    RadioButton offline_title_download_mamage;

    @Bind(R.id.title_back)
    ImageButton title_back;

    @Bind(R.id.offline_title_layout)
    RelativeLayout offline_title_layout;

    private boolean isExeRadioStatusChange = true;
    private boolean isExrPagerChange = true;

    private OffLineMapListWidget mOffLineMapListWidget;

    private OffLineDownLoadManagerWidget mOffLineDownLoadManagerWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* OffLineBaiduMapUtils mOffLineBaiduMapUtils = OffLineBaiduMapUtils.getInstance();
        List<MKOLSearchRecord> allOffLineInfo = mOffLineBaiduMapUtils.getOfflineCityList();
        List<MKOLSearchRecord> gd = mOffLineBaiduMapUtils.getSearchCity("广东省");
//        for(MKOLSearchRecord mMKOLSearchRecord:allOffLineInfo){
        for(MKOLSearchRecord mMKOLSearchRecord:gd){

            if(debug)LogUtils.error("cityID : " + mMKOLSearchRecord.cityID + "  cityName : " + mMKOLSearchRecord.cityName +  " download size :" + mMKOLSearchRecord.size);
            ArrayList<MKOLSearchRecord> childCities = mMKOLSearchRecord.childCities;
            if(childCities!=null && childCities.size()>0){
                for(MKOLSearchRecord childCity:childCities)
                    if(debug)LogUtils.error("              childCitie cityID : " + childCity.cityID + "  cityName : " + childCity.cityName +  " download size :" + childCity.size);
            }
        }*/

       /* OffLineMapListWidget mOffLineMapListWidget = new OffLineMapListWidget();

        mOffLineMapListWidget.initListView(this);

        content_view.addView(mOffLineMapListWidget.getView());*/
        OfflinePagerAdapter offlinePagerAdapter = new OfflinePagerAdapter();
        ArrayList<View> views = new ArrayList<>();

        mOffLineDownLoadManagerWidget = new OffLineDownLoadManagerWidget(this);
        views.add(mOffLineDownLoadManagerWidget.getView());

        mOffLineMapListWidget = new OffLineMapListWidget();
        mOffLineMapListWidget.initListView(this);
        views.add(mOffLineMapListWidget.getView());

        offlinePagerAdapter.setData(views);

        offline_manager.setAdapter(offlinePagerAdapter);

        offline_title_RadioGroup.setOnCheckedChangeListener(this);
        offline_manager.setOnPageChangeListener(this);

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        UIUtils.setImmersiveStatusBar(offline_title_layout);

    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getView() {
        return R.layout.activity_offline_map;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OffLineBaiduMapUtils.getInstance().clean();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if(!isExeRadioStatusChange){
            isExeRadioStatusChange = true;
            return;
        }

        isExrPagerChange = false;

        switch (checkedId)
        {
            case R.id.offline_title_map:
                isExrPagerChange = false;
                offline_manager.setCurrentItem(1);
                setSelectTitleText(1);
                if(isDebug)LogUtils.error("onCheckedChanged  1");

                break;
            case R.id.offline_title_download_manage:
                isExrPagerChange = false;
                offline_manager.setCurrentItem(0);
                setSelectTitleText(0);
                if(isDebug)LogUtils.error("onCheckedChanged  0");
                break;
            default:
                isExrPagerChange = false;
                offline_manager.setCurrentItem(0);
                setSelectTitleText(0);
                if(isDebug)LogUtils.error("onCheckedChanged  0");
                break;

        }

    }

    public void setSelectTitleText(int index){
        if(index == 1){
            offline_title_download_mamage.setTextColor(getResources().getColor(R.color.white));
            offline_title_map.setTextColor(getResources().getColor(R.color.select_title_text_color));
        }else {
            offline_title_download_mamage.setTextColor(getResources().getColor(R.color.select_title_text_color));
            offline_title_map.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if(!isExrPagerChange){
            isExrPagerChange = true;
            return;
        }

        if(isDebug)LogUtils.error("onCheckedChanged  onPageScrolled ： "+position);
        switch (position){
            case 1:
                isExeRadioStatusChange = false;
                offline_title_map.setChecked(true);
                setSelectTitleText(1);
                mOffLineMapListWidget.updata();
                break;
            case 0:
                isExeRadioStatusChange = false;
                offline_title_download_mamage.setChecked(true);
                setSelectTitleText(0);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(offline_manager.getCurrentItem() == 1 && mOffLineMapListWidget.isSearchShow()){
                mOffLineMapListWidget.cancelSearch();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //切换为竖屏
        if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
            ConfigurationChangedManager.getInstance().updateOffLineMapDialogView();
        }
        //切换为横屏
        else if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
            ConfigurationChangedManager.getInstance().updateOffLineMapDialogView();
        }
    }
}
