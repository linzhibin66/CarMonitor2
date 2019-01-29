package com.easyder.carmonitor.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.CarInfoPagerAdater;
import com.easyder.carmonitor.interfaces.AllCarSelectChanged;
import com.easyder.carmonitor.interfaces.NavigationBarPagerListener;
import com.easyder.carmonitor.widget.NavigationBar;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.InfoTool.AllCarListClassify;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.CompanyAllCarBaseInfo;
import com.shinetech.mvp.network.UDP.presenter.AllCarListPresenter;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ljn on 2017/2/28.
 */
public class AllCarListActivity extends BaseActivity implements NavigationBarPagerListener, View.OnClickListener, View.OnTouchListener {

    @Bind(R.id.allcarlist_content)
    RelativeLayout allcarlist_content;

    @Bind(R.id.title_layout)
    RelativeLayout title_layout;

    @Bind(R.id.title_back)
    ImageButton title_back;

    @Bind(R.id.title_search)
    ImageButton title_search;

    @Bind(R.id.select_car_enter)
    TextView select_car_enter;

    @Bind(R.id.select_all_cb)
    CheckBox select_all_cb;

    @Bind(R.id.select_all_layout)
    LinearLayout select_all_layout;

    @Bind(R.id.select_car_clean)
    TextView select_car_clean;

    @Bind(R.id.allcarlist_layout_outmost)
    RelativeLayout allcarlist_layout_outmost;

    @Bind(R.id.hint_loading_layout)
    RelativeLayout hint_loading_layout;

    /**
     * 顶部导航控件
     */
    private NavigationBar navigationBar;

    /**
     * 滑动页面适配器
     */
    private CarInfoPagerAdater carInfoPagerAdater;

    /**
     * 标题布局
     */
    private List<View> itemList = new ArrayList<>();

    /**
     * 标题字串
     */
    private String[] stringArray;

    /**
     * 分类后的数据
     */
    private AllCarListClassify myAllCarListClassify;

    /**
     * 定时刷新车辆信息
     */
    private int TIME2REFRESH = 1;

    private long TIME = 30*1000;

    private boolean toExeChangedListener = true;

    private boolean isSaveSelect = false;

    private boolean isTouch = false;

    /**
     * 定时刷新
     */
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            if(msg.what == TIME2REFRESH){
//                System.out.println("Update data handleMessage-------------------------------------------------");
                //更新分类车辆信息
                updateAllCarList();
            }

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTitleItem();

        navigationBar = new NavigationBar(this, itemList, null);

        allcarlist_content.addView(navigationBar.getLayout(), new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        updateAllCarList();

        title_back.setOnClickListener(this);

        title_search.setOnClickListener(this);

        initBottomLayout();

        title_search.setImageResource(R.mipmap.icon_serach_carlist);

        UIUtils.setImmersiveStatusBar(title_layout);

        allcarlist_layout_outmost.setOnTouchListener(this);

    }

    /**
     * 初始化底部选择界面
     */
    private void initBottomLayout(){

        select_car_enter.setText(getString(R.string.bottom_select_enter, ((AllCarListPresenter)presenter).getSelectCarInfoCount()+""));

        select_car_enter.setOnClickListener(this);

        select_all_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = select_all_cb.isChecked();
                select_all_cb.setChecked(!checked);
            }
        });

        select_all_cb.setChecked(false);

        select_all_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //是否执行全选改变事件
                if (!toExeChangedListener) {
                    toExeChangedListener = true;
                    return;
                }
                if (isChecked) {
                    carInfoPagerAdater.selectAll(navigationBar.getCurrentIndex());
                } else {
                    carInfoPagerAdater.removeAll(navigationBar.getCurrentIndex());
                }

                int selectCarInfoCount = ((AllCarListPresenter) presenter).getSelectCarInfoCount();
                select_car_enter.setText(getString(R.string.bottom_select_enter, selectCarInfoCount + ""));
            }
        });

        select_car_clean.setOnClickListener(this);

    }

    private void initTitleItem(){

        stringArray = getResources().getStringArray(R.array.allcarlist_item);

        for(int i = 0; i<stringArray.length;i++){
            TextView textView = new TextView(this);
            textView.setTextColor(Color.parseColor("#FFFFFFFF"));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.allcarlist_title_textsize));
            textView.setGravity(Gravity.CENTER);
            textView.setText(String.format(stringArray[i],0));
            itemList.add(textView);
        }

    }

    /**
     * 更新标题中的数量
     * @param allCarListClassify
     */
    private void updateTitleView(AllCarListClassify allCarListClassify){

        int count[] = {allCarListClassify.getAllcarList().size(),allCarListClassify.getRunningCarList().size(),allCarListClassify.getStopCarList().size(), allCarListClassify.getAlarmCarList().size(),allCarListClassify.getExceptionList().size()};

        int statusCount = navigationBar.getStatusCount();
        for(int i = 0 ; i<statusCount; i++){
            TextView statusItem = (TextView) navigationBar.getStatusItem(i);
            statusItem.setText(String.format(stringArray[i], count[i]));
        }

    }

    @Override
    protected int getView() {
        return R.layout.activity_allcarlist_menu;
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return new AllCarListPresenter();
    }

    @Override
    public void onLoading() {
        hint_loading_layout.setVisibility(View.VISIBLE);
    }

    /**
     * 获取到分类数据，并更新每个分类中的车辆数据
     * @param dataVo 解析成功后返回VO对象
     */
    @Override
    public void showContentView(BaseVo dataVo) {

        if(dataVo instanceof CompanyAllCarBaseInfo){
            AllCarListClassify allCarListClassify = ((AllCarListPresenter) presenter).getmAllCarListClassify();

            //获取车辆分类信息
            if(myAllCarListClassify == null){
                myAllCarListClassify = new AllCarListClassify(allCarListClassify.getAllcarList());
            }else{
                myAllCarListClassify.setAllcarList(allCarListClassify.getAllcarList());
            }

            myAllCarListClassify.setAlarmCarList(allCarListClassify.getAlarmCarList());
            myAllCarListClassify.setRunningCarList(allCarListClassify.getRunningCarList());
            myAllCarListClassify.setStopCarList(allCarListClassify.getStopCarList());
            myAllCarListClassify.setExceptionList(allCarListClassify.getExceptionList());

            if(carInfoPagerAdater==null){
                carInfoPagerAdater = new CarInfoPagerAdater(this, myAllCarListClassify, navigationBar.getStatusCount(),(AllCarListPresenter)presenter);
                //添加选择监听
                carInfoPagerAdater.setAllCarSelectChanged(new AllCarSelectChanged() {
                    @Override
                    public void onSelectChanged(boolean seleclAll) {
//                        TODO select All or select count view update
//                        点击每个复选框时跟新全选状态和选择个数
                        int selectCarInfoCount = ((AllCarListPresenter)presenter).getSelectCarInfoCount();
                        select_car_enter.setText(getString(R.string.bottom_select_enter, selectCarInfoCount + ""));

                        boolean checked = select_all_cb.isChecked();

                        if(checked != seleclAll) {
                            toExeChangedListener = false;
                            select_all_cb.setChecked(seleclAll);
                        }
                    }
                });
                navigationBar.setPagerAdapter(carInfoPagerAdater);
                navigationBar.setPagerListener(this);

                //跟新底部界面数据
                updateSellectAllView(navigationBar.getCurrentIndex());
                select_car_enter.setText(getString(R.string.bottom_select_enter, ((AllCarListPresenter)presenter).getSelectCarInfoCount() + ""));

            }else{
                carInfoPagerAdater.updatePsoition(navigationBar.getCurrentIndex(),myAllCarListClassify);
            }

            updateTitleView(myAllCarListClassify);

        }

    }

    @Override
    public void onStopLoading() {
        hint_loading_layout.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAllCarList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler = null;
    }

    private void updateAllCarList(){
            //更新分类车辆信息
        removeMessage();
        ((AllCarListPresenter)presenter).getAllCar();
        if(mHandler !=null){
            mHandler.sendEmptyMessageDelayed(TIME2REFRESH,TIME);
        }
    }

    private void removeMessage(){
        if(mHandler !=null) {
            mHandler.removeMessages(TIME2REFRESH);
        }
    }

    @Override
    public void onPageSelected(int position) {
        //更新分类车辆信息
        updateAllCarList();

        updateSellectAllView(position);
    }

    /**
     * 跟新选择全部复选框，不同分类全选状态不一样
     * @param position
     */
    public void updateSellectAllView(int position){
        boolean selectAll = carInfoPagerAdater.isSelectAll(position);
        boolean checked = select_all_cb.isChecked();
        if(checked != selectAll){
            toExeChangedListener = false;
            select_all_cb.setChecked(selectAll);

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.title_back:
                finish();
                break;
            case R.id.title_search:
//                TODO serach
                break;
            case R.id.select_car_enter:

//                UserInfo.getInstance().addCarToShowAll(((AllCarListPresenter)presenter).getSelectCarInfoMapCache());

                finish();
                break;
            case R.id.select_car_clean:
                carInfoPagerAdater.clearSelect(navigationBar.getCurrentIndex());
                UserInfo.getInstance().celanSelectCarAll();

                boolean checked = select_all_cb.isChecked();
                if(checked){
                    toExeChangedListener = false;
                    select_all_cb.setChecked(false);
                }

                int selectCarInfoCount = ((AllCarListPresenter) presenter).getSelectCarInfoCount();
                select_car_enter.setText(getString(R.string.bottom_select_enter, selectCarInfoCount + ""));

                break;
        }
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
