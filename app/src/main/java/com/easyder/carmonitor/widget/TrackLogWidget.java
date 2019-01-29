package com.easyder.carmonitor.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.TrackLogAdapter;
import com.easyder.carmonitor.bean.TrackLogItem;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.TrackLogItemClick;
import com.easyder.carmonitor.presenter.TrackActivityPresenter;
import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskManager;
import com.shinetech.mvp.utils.CoordinateUtil;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ljn on 2017-05-05.
 */
public class TrackLogWidget implements AbsListView.OnScrollListener, OnGetGeoCoderResultListener, AdapterView.OnItemClickListener {

    private List<TrackLogItem> trackLogList = new ArrayList<>();

    /**
     * 跟新ListView 消息
     */
    private final int UPDATA_LISTVIEW = 0;

    private final int UPDATA_ADDRES_LISTVIEW = 1;

    private Context context;

    private View rootView;

    private ListView trackLogListView;

    private RelativeLayout track_log_layout_outmost;

    /**
     * 返回按钮事件
     */
    private LayoutBackListener mLayoutBackListener;

    /**
     * 获取日志数据类
     */
    private TrackActivityPresenter mpresenter;

    /**
     * 日志适配器
     */
    private TrackLogAdapter mTrackLogAdapter;

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

    /**
     * 条目点击事件
     */
    private TrackLogItemClick listener;

    //地址解析类
    private GeoCoder geoCoder;

    private ReverseGeoCodeOption reverseGeoCodeOption;

    /**
     * 第一条日志显示的位置
     */
    private int firstItem;

    /**
     * 页面显示的日志数量
     */
    private int itemCount;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == UPDATA_LISTVIEW) {
                if(mTrackLogAdapter == null) {
                    mTrackLogAdapter = new TrackLogAdapter(trackLogList, context);
                    trackLogListView.setAdapter(mTrackLogAdapter);
                    trackLogListView.setOnScrollListener(TrackLogWidget.this);
                    trackLogListView.setOnItemClickListener(TrackLogWidget.this);

                }else{
                    mTrackLogAdapter.updata(trackLogList);
                }
            }else if(msg.what == UPDATA_ADDRES_LISTVIEW){
                mTrackLogAdapter.notifyDataSetChanged();
            }
        }
    };

    public TrackLogWidget(Context context, TrackActivityPresenter mpresenter, LayoutBackListener mLayoutBackListener) {
        this.context = context;
        this.mLayoutBackListener = mLayoutBackListener;
        this.mpresenter = mpresenter;
        rootView = View.inflate(context, R.layout.activity_track_log, null);
        initView();

    }

    public TrackLogWidget(Context context, View view, TrackActivityPresenter mpresenter, LayoutBackListener mLayoutBackListener) {
        this.context = context;
        this.mLayoutBackListener = mLayoutBackListener;
        this.mpresenter = mpresenter;
        rootView = view;
        initView();

    }

    private void initView(){

        initTitle();

        trackLogListView = (ListView) rootView.findViewById(R.id.track_log_list);
        track_log_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.track_log_layout_outmost);

        initGeoCoder();

        update();
        
    }

    public void initAddres(){
        int firstVisiblePosition = trackLogListView.getFirstVisiblePosition();
        int childCount = trackLogListView.getChildCount();
//                    LogUtils.error("childCount == " + childCount);
        if(childCount != 0){
            firstItem = firstVisiblePosition;
            itemCount = childCount;
            getAddres();
        }
    }

    public void update(){
        TaskManager.runDBTask(new TaskBean() {
            @Override
            public void run() {
                HashMap<Integer, ArrayList<TrackLogItem>> historiLog = mpresenter.getHistoriLog();
                trackLogList.clear();
                setTrackLogList(historiLog);
                mHandler.sendEmptyMessage(UPDATA_LISTVIEW);
            }
        });
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if (track_log_layout_outmost != null) {
            track_log_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    private void initTitle() {

        ImageButton  title_back = (ImageButton) rootView.findViewById(R.id.title_back);
        TextView title_text = (TextView) rootView.findViewById(R.id.title_text);
        ImageButton title_search = (ImageButton) rootView.findViewById(R.id.title_search);

        title_text.setText(context.getString(R.string.track_log_activity_title));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLayoutBackListener != null) {
                    mLayoutBackListener.onBack();
                }
            }
        });

        title_search.setVisibility(View.GONE);

        RelativeLayout title_layout = (RelativeLayout) rootView.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }

    private void setTrackLogList(HashMap<Integer, ArrayList<TrackLogItem>> map){

        for(int i = 0; i<map.size(); i++){
            TrackLogItem trackLogItem = new TrackLogItem(true);
            ArrayList<TrackLogItem> trackLogItems = map.get(i);
            String locationTime = trackLogItems.get(0).getLocationTime();
            trackLogItem.setLocationTime(locationTime);
            trackLogList.add(trackLogItem);
            trackLogList.addAll(trackLogItems);
        }

      /*  Iterator<Map.Entry<Integer, ArrayList<TrackLogItem>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            trackLogList.add(new TrackLogItem(true));
            Map.Entry<Integer, ArrayList<TrackLogItem>> next = iterator.next();
            ArrayList<TrackLogItem> value = next.getValue();
            trackLogList.addAll(value);
        }*/
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

    //滑动监听
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

//        LogUtils.error("scrollState == "+scrollState);
//        LogUtils.error("firstVisibleItem == "+firstItem + "    visibleItemCount == "+itemCount + "    isScroll == "+isScroll);
        if(scrollState == 0){

            isScroll = false;
            getAddres();

        }else{
            isScroll = true;
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

//        LogUtils.error("firstVisibleItem == "+firstVisibleItem + "    visibleItemCount == "+visibleItemCount + "    totalItemCount == "+totalItemCount);
        firstItem = firstVisibleItem;
        itemCount = visibleItemCount;
//        isScroll = true;
    }


    //地址解析结果
    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

        if (!TextUtils.isEmpty(reverseGeoCodeResult.getAddress())){
            String address = reverseGeoCodeResult.getAddress();

            if(!isScroll) {
                View childAt = trackLogListView.getChildAt(currentReverseAddres - firstItem);
                if (childAt != null) {
//                LogUtils.error("childAt == " + childAt);
                    Object tag = childAt.getTag();
                    if (tag != null && tag instanceof TrackLogAdapter.ViewHolder) {
                        TrackLogAdapter.ViewHolder holder = (TrackLogAdapter.ViewHolder) tag;
//                    LogUtils.error("holder == " + holder);
                        holder.track_log_addres.setText(address);
                    }
                }
            }

            TrackLogItem item = (TrackLogItem) mTrackLogAdapter.getItem(currentReverseAddres);
            item.setAdress(address);
//            LogUtils.error("onGetReverseGeoCodeResult address == " + address);

        }else{
            isErrorReverseAddres = true;
        }

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

                    TrackLogItem item = (TrackLogItem) mTrackLogAdapter.getItem(i);

                    if (item.isSplitLine()) {
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
                    while ((System.currentTimeMillis()- sendTime < 1000) && TextUtils.isEmpty(((TrackLogItem) mTrackLogAdapter.getItem(i)).getAdress()) && !isScroll && !isErrorReverseAddres);

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(mTrackLogAdapter.getCount()<=position){
            return;
        }

        TrackLogItem item = (TrackLogItem) mTrackLogAdapter.getItem(position);

        //判断是否是分割线item
        if (item.isSplitLine()) {
            return;
        }

        if(listener != null){
            listener.onItemClick(item);
        }

    }

    public void setClickListener(TrackLogItemClick listener){
        this.listener = listener;
    }
}
