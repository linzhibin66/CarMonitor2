package com.easyder.carmonitor.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.activity.AlarmLogInfoActivity;
import com.easyder.carmonitor.adapter.AlarmLogAdapter;
import com.easyder.carmonitor.adapter.ViolationSelectAdapter;
import com.easyder.carmonitor.interfaces.AlarmLogListListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.ViolationLogTypeChengedListener;
import com.easyder.carmonitor.presenter.AlarmLogActivityPresenter;
import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskManager;
import com.shinetech.mvp.network.UDP.resopnsebean.ViolationLogItem;
import com.shinetech.mvp.utils.CoordinateUtil;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

import java.util.List;

/**
 * Created by ljn on 2017-08-02.
 */
public class AlarmLogWidget implements AlarmLogListListener, AbsListView.OnScrollListener, OnGetGeoCoderResultListener, AdapterView.OnItemClickListener {

    private Context context;

    private LayoutBackListener mLayoutBackListener;

    private View rootView;

    //地址解析类
    private GeoCoder geoCoder;

    private ReverseGeoCodeOption reverseGeoCodeOption;

    private AlarmLogActivityPresenter alarmLogActivityPresenter;

    private String platenumber;

    private String startTime;

    private String endTime;

    private ListView alarm_log_list;

    private ProgressBar alarm_progressbar;

    private RelativeLayout track_log_layout_outmost;

    private RelativeLayout no_content_layout;

    /**
     * 类型筛选界面
     */
    private RelativeLayout alarm_filter_layout;

    private LinearLayout alarm_filter_content_layout;

    private ListView alarm_filter_list;

    private TextView alarm_filter_select_all;

    private TextView alarm_filter_enter_select;

    private AlarmLogAdapter mAlarmLogAdapter;

    private ViolationSelectAdapter mViolationSelectAdapter;

    private TextView title_search;

    /**
     * 第一条日志显示的位置
     */
    private int firstItem;

    /**
     * 页面显示的日志数量
     */
    private int itemCount;

    /**
     * 是否处于滑动状态
     */
    private boolean isScroll = false;

    /**
     * 是否解析地址错误
     */
    private boolean isErrorReverseAddres = false;

    /**
     * 当前解析的地址位置
     */
    private int currentReverseAddres;

    private boolean isDismissingFilterLayout = false;

    private final int UPDATA_ADDRES_LISTVIEW = 1001;
    private final int UPDATA_LOG_LISTVIEW = 1002;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == UPDATA_ADDRES_LISTVIEW){
                mAlarmLogAdapter.notifyDataSetChanged();
            }
        }
    };

    public AlarmLogWidget(String platenumber, String startTime, String endTime, LayoutBackListener mLayoutBackListener, Context context) {
        this.mLayoutBackListener = mLayoutBackListener;
        this.context = context;
        this.platenumber = platenumber;
        this.startTime = startTime;
        this.endTime = endTime;
        rootView = View.inflate(context, R.layout.activity_alarm_log, null);
        initView();
    }

    public AlarmLogWidget(String platenumber, String startTime, String endTime, Context context, View rootView, LayoutBackListener mLayoutBackListener) {
        this.context = context;
        this.mLayoutBackListener = mLayoutBackListener;
        this.rootView = rootView;
        this.platenumber = platenumber;
        this.startTime = startTime;
        this.endTime = endTime;
        initView();
    }

    private void initView(){

        initTitle();

        initGeoCoder();

        alarm_progressbar = (ProgressBar) rootView.findViewById(R.id.alarm_progressbar);

        alarm_log_list = (ListView) rootView.findViewById(R.id.alarm_log_list);

        track_log_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.track_log_layout_outmost);

        alarm_filter_layout = (RelativeLayout) rootView.findViewById(R.id.alarm_filter_layout);

        alarm_filter_content_layout = (LinearLayout) rootView.findViewById(R.id.alarm_filter_content_layout);

        no_content_layout = (RelativeLayout) rootView.findViewById(R.id.no_content_layout);

        alarm_filter_list = (ListView) rootView.findViewById(R.id.alarm_filter_list);

        alarm_filter_select_all = (TextView) rootView.findViewById(R.id.alarm_filter_select_all);

        alarm_filter_enter_select = (TextView) rootView.findViewById(R.id.alarm_filter_enter_select);

        //显示加载进度
        alarm_progressbar.setVisibility(View.VISIBLE);
        //获取警报日志
        alarmLogActivityPresenter = new AlarmLogActivityPresenter();

        alarmLogActivityPresenter.getViolationLogList(platenumber, startTime, endTime, this);

    }

    public boolean onKeyBack(){
        if(alarm_filter_layout.getVisibility() == View.VISIBLE){
//            ToastUtil.showShort("alarm_filter_layout dismiss ");
            dismissFilterLayout(null);
            return true;
        }
        return false;
    }

    /**
     * 初始化标题
     */
    private void initTitle() {

        ImageButton title_back = (ImageButton) rootView.findViewById(R.id.title_back);
        TextView title_text = (TextView) rootView.findViewById(R.id.title_text);
        title_search = (TextView) rootView.findViewById(R.id.title_search);

        title_text.setText(context.getString(R.string.warning));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLayoutBackListener != null) {
                    mLayoutBackListener.onBack();
                }
            }
        });

        title_search.setVisibility(View.GONE);

        title_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectView();
            }
        });

        RelativeLayout title_layout = (RelativeLayout) rootView.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }

    /**
     * 初始化地址解析类
     */
    private void initGeoCoder(){
        //地址反向编码
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);
        reverseGeoCodeOption = new ReverseGeoCodeOption();
    }


    @Override
    public void onSuccess(List<ViolationLogItem> mViolationLogList) {

        if(mViolationLogList == null || mViolationLogList.size() == 0){
            no_content_layout.setVisibility(View.VISIBLE);
            title_search.setVisibility(View.GONE);
            return;
        }else{
            no_content_layout.setVisibility(View.GONE);
            title_search.setVisibility(View.VISIBLE);
        }

        alarm_progressbar.setVisibility(View.GONE);

        mAlarmLogAdapter = new AlarmLogAdapter(mViolationLogList,context);

        alarm_log_list.setAdapter(mAlarmLogAdapter);

        alarm_log_list.setOnScrollListener(AlarmLogWidget.this);

        alarm_log_list.setOnItemClickListener(this);

        initAdress();

    }

    private void showSelectView(){

        //初始化类型选择适配器
        if(mViolationSelectAdapter == null) {
            mViolationSelectAdapter = new ViolationSelectAdapter(context, alarmLogActivityPresenter.getViolationTypeList(), alarmLogActivityPresenter.getSelectViolationList());
        }else{
            mViolationSelectAdapter.updata(alarmLogActivityPresenter.getViolationTypeList(), alarmLogActivityPresenter.getSelectViolationList());
        }

        alarm_filter_list.setAdapter(mViolationSelectAdapter);

        //设置点击事件
        alarm_filter_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //获取所有类型
                List<String> mViolationTypeList = alarmLogActivityPresenter.getViolationTypeList();

                if(mViolationTypeList.size()<=position) {
                    return;
                }

                //获取点击的类型
                String clickItemType = mViolationTypeList.get(position);

                //做添加或移除操作
                alarmLogActivityPresenter.addOrRemoveSelectType(clickItemType);

                //跟新选择数量
                alarm_filter_enter_select.setText(context.getString(R.string.bottom_select_finish,""+alarmLogActivityPresenter.getSelectViolationSize()));

                //初始化全选按钮
                if(alarmLogActivityPresenter.isSelectAll()){
                    alarm_filter_select_all.setText(context.getString(R.string.bottom_select_cancelall));
                }else {
                    alarm_filter_select_all.setText(context.getString(R.string.bottom_select_all));
                }

                //跟新列表显示
                mViolationSelectAdapter.updata(mViolationTypeList, alarmLogActivityPresenter.getSelectViolationList());

            }
        });

        //初始化全选按钮
        if(alarmLogActivityPresenter.isSelectAll()){
            alarm_filter_select_all.setText(context.getString(R.string.bottom_select_cancelall));
        }else {
            alarm_filter_select_all.setText(context.getString(R.string.bottom_select_all));
        }

        //添加点击事件
        alarm_filter_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alarmLogActivityPresenter.isSelectAll()){
                    alarmLogActivityPresenter.cancelAllSelect();
                    alarm_filter_select_all.setText(context.getString(R.string.bottom_select_all));
                }else{
                    alarmLogActivityPresenter.selectAll();
                    alarm_filter_select_all.setText(context.getString(R.string.bottom_select_cancelall));
                }

                mViolationSelectAdapter.updata(alarmLogActivityPresenter.getViolationTypeList(), alarmLogActivityPresenter.getSelectViolationList());
            }
        });

        //初始化确认选择
        alarm_filter_enter_select.setText(context.getString(R.string.bottom_select_finish,""+alarmLogActivityPresenter.getSelectViolationSize()));
        alarm_filter_enter_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!alarmLogActivityPresenter.hasSelectType()){
                    ToastUtil.showShort(context.getString(R.string.bottom_select_null));
                    return;
                }

                if(isDismissingFilterLayout){
                    return;
                }

                dismissFilterLayout(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        alarm_filter_layout.post(new Runnable() {
                            @Override
                            public void run() {
                                alarm_filter_layout.setVisibility(View.GONE);
                                isDismissingFilterLayout = false;

                                //TODO update Violation log list
                                alarmLogActivityPresenter.updateShowViolationList(new ViolationLogTypeChengedListener() {
                                    @Override
                                    public void onStart() {

                                    }

                                    @Override
                                    public void onFinish(final List<ViolationLogItem> showViolationLogList) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAlarmLogAdapter.updata(showViolationLogList);
                                                initAdress();
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        });

        alarm_filter_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                alarm_filter_layout.setVisibility(View.GONE);
                dismissFilterLayout(null);
            }
        });

        if(alarm_filter_layout.getVisibility() != View.VISIBLE) {
            showFilterLayout();
        }
    }

    private void showFilterLayout(){
        alarm_filter_layout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.up2down_show_anim);
        alarm_filter_content_layout.startAnimation(animation);
    }

    private void dismissFilterLayout(Animation.AnimationListener mAnimationListener){
        if(!isDismissingFilterLayout){
            isDismissingFilterLayout = true;
        }else{
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.down2up_show_anim);
        if(mAnimationListener == null){
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    alarm_filter_layout.post(new Runnable() {
                        @Override
                        public void run() {
                            alarm_filter_layout.setVisibility(View.GONE);
                            isDismissingFilterLayout = false;
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }else {
            animation.setAnimationListener(mAnimationListener);
        }

        alarm_filter_content_layout.startAnimation(animation);

    }

    @Override
    public void onError(String message) {
        alarm_progressbar.setVisibility(View.GONE);
        no_content_layout.setVisibility(View.VISIBLE);
        title_search.setVisibility(View.GONE);
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if (track_log_layout_outmost != null) {
            track_log_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == 0){
            isScroll = false;
            getAddres();

        }else{
            isScroll = true;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        firstItem = firstVisibleItem;
        itemCount = visibleItemCount;
    }

    private void getAddres(){
        TaskBean taskBean = new TaskBean() {
            @Override
            public void run() {

                currentReverseAddres = 0;

                for (int i = firstItem; i < (firstItem + itemCount); i++) {

                    if (isScroll) {
                        return;
                    }

//                    TrackLogItem item = (TrackLogItem) mTrackLogAdapter.getItem(i);

                    ViolationLogItem item = (ViolationLogItem) mAlarmLogAdapter.getItem(i);

                    if (item == null || TextUtils.isEmpty(item.getPlateNumber())) {
                        continue;
                    }

                    if (!TextUtils.isEmpty(item.getAdress())) {
                        continue;
                    }

//                        LogUtils.error("TaskBean  i == "+i );

                    //      根据坐标解析地址
                    double lng = item.getLng() / 1E6;
                    double lat = item.getLat() / 1E6;

                    LatLng latLng = CoordinateUtil.gps84_to_bd09(lat, lng);
                    currentReverseAddres = i;
                    //初始化解析错误标记
                    isErrorReverseAddres = false;

                    //解析地址
                    reverseGeoCodeOption.location(latLng);
                    geoCoder.reverseGeoCode(reverseGeoCodeOption);

                    long sendTime = System.currentTimeMillis();

                    //等待 当解析完成 、 解析超时 、 列表滑动操作 或 解析失败 等情况退出等待。
                    while ((System.currentTimeMillis()- sendTime < 1000) && TextUtils.isEmpty(((ViolationLogItem) mAlarmLogAdapter.getItem(i)).getAdress()) && !isScroll && !isErrorReverseAddres);

                        /*LogUtils.error("time == " + (System.currentTimeMillis()- sendTime)
                                + "  is empty == "+TextUtils.isEmpty(((TrackLogItem) mTrackLogAdapter.getItem(i)).getAdress())
                                +"  isScroll == "+isScroll + "  isErrorReverseAddres == "+isErrorReverseAddres);*/
                }

                //跟新listView数据（没进行地址解析，就不用重新刷新列表数据）
                if(currentReverseAddres != 0){
                    mHandler.sendEmptyMessage(UPDATA_ADDRES_LISTVIEW);
                }

            }
        };

//            taskBean.setType("initAddres");

        TaskManager.runDBTask(taskBean);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

        if (!TextUtils.isEmpty(reverseGeoCodeResult.getAddress())){
            String address = reverseGeoCodeResult.getAddress();

            if(!isScroll) {
                View childAt = alarm_log_list.getChildAt(currentReverseAddres - firstItem);
                if (childAt != null) {
//                LogUtils.error("childAt == " + childAt);
                    Object tag = childAt.getTag();
                    if (tag != null && tag instanceof AlarmLogAdapter.ViewHolder) {
                        AlarmLogAdapter.ViewHolder holder = (AlarmLogAdapter.ViewHolder) tag;
//                    LogUtils.error("holder == " + holder);
                        holder.alarm_log_addres.setText(address);
                    }
                }
            }

            ViolationLogItem item = (ViolationLogItem) mAlarmLogAdapter.getItem(currentReverseAddres);
            item.setAdress(address);
//            LogUtils.error("onGetReverseGeoCodeResult address == " + address);

        }else{
            isErrorReverseAddres = true;
        }

    }

    public void initAdress(){

        TaskBean taskBean = new TaskBean() {
            @Override
            public void run() {

                int firstVisiblePosition = alarm_log_list.getFirstVisiblePosition();
                int childCount = alarm_log_list.getChildCount();
                int oldchildCount = -1;
//                LogUtils.error("childCount == " + childCount);

                while(childCount != oldchildCount){

                    if (isScroll) {
                        return;
                    }

                    firstVisiblePosition = alarm_log_list.getFirstVisiblePosition();
                    childCount = alarm_log_list.getChildCount();
                    oldchildCount = childCount;

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    childCount = alarm_log_list.getChildCount();
                }

                firstItem = firstVisiblePosition;
                itemCount = childCount;
                getAddres();

            }

        };

        TaskManager.runDBTask(taskBean);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ViolationLogItem item = (ViolationLogItem) mAlarmLogAdapter.getItem(position);

        if(TextUtils.isEmpty(item.getPlateNumber())){
            return;
        }

        Intent intent = new Intent(context, AlarmLogInfoActivity.class);
        intent.putExtra(AlarmLogInfoActivity.ALARM_LOG_INFO,item);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.pop_right2left_anim_open, R.anim.pop_right2left_anim_close);
//        ToastUtil.showShort("name = "+item.getViolationName()+"   time :"+item.getViolationTime());
    }
}
