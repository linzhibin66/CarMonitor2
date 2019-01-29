package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.BaseSearchAdapter;
import com.easyder.carmonitor.adapter.HistorySearchAdapter;
import com.easyder.carmonitor.adapter.HistorySearchGridViewAdapter;
import com.easyder.carmonitor.adapter.PlateNumberSearchAdapter;
import com.easyder.carmonitor.adapter.SearchAdapter;
import com.easyder.carmonitor.interfaces.SearchDialogClickListeren;
import com.easyder.carmonitor.presenter.MainActivityPresenter;
import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskCtrl.DBCtrlTask;
import com.shinetech.mvp.DB.DBThreadPool.TaskManager;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.CarLatestStatus;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.listener.RequestAllCarListener;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseCarStatusInfoBean;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.SearchHistoryUtil;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by ljn on 2017/2/23.
 */
public class SearchDilaog extends BasePopupWindowDialog implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private RelativeLayout history_layout;

    private ListView search_content;

    /**
     * 搜索内容适配器 (List<CarInfoBean>)
     */
    private SearchAdapter searchAdapter;

    /**
     * 搜索内容适配器 (List<String>)
     */
    private PlateNumberSearchAdapter mPlateNumberSearchAdapter;

    /**
     * listView 历史纪录适配器
     */
    private HistorySearchAdapter mHistorySearchAdapter;

    /**
     * GridView 历史纪录适配器
     */
    private  HistorySearchGridViewAdapter historySearchGridViewAdapter;

    /**
     * 无搜索到车辆
     */
    private TextView no_search_car;

    /**
     * 点击事件监听
     */
    private SearchDialogClickListeren mClickListeren;

    //主界面Persenter
    private MainActivityPresenter persenter;

    /**
     * progressBar 等待
     */
    private  ProgressBar loading_pbar;

    /**
     * 历史纪录
     */
    private List<String> historyList;

    /**
     * 列表形式历史记录，还是表格形式
     */
    private final boolean isListHistory = false;

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

    private int currentParseCarInfo = 0;

    private boolean hasResult = false;

    private GridView history_search;

    private View boundView;

    private String currentSearchString = "";


    public SearchDilaog(Context context, MainActivityPresenter persenter) {
        super(context, R.layout.search_layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, getsearchHeight(context)));

        this.persenter = persenter;

//        setFocusable(true);

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        initView();

//        setAnimationStyle(R.style.popwindow_anim_upanddown);

    }

    private static int getsearchHeight(Context context){
        int searchHeight = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();

//        System.out.printf("wimdow height = "+ height);

        int statusBarHeight = UIUtils.getStatusBarHeight();
        int staty_h = statusBarHeight + 20;

        int title_h = UIUtils.dip2px(29);

        searchHeight = height - staty_h - title_h;

//        System.out.printf("wimdow searchHeight = "+ searchHeight);
        return searchHeight;

    }

    private static int startX(Context context){
        int startX = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        /*if(width >height) {
            startX = (width - height) / 2;
        }else{
            startX = UIUtils.dip2px(10);
        }*/
        if(width >height) {
            startX = (width - height) / 2;
        }else{
            startX = UIUtils.dip2px(10);
        }

        return startX;

    }

    private static int startY(Context context){
        int startY = 0;

        int statusBarHeight = UIUtils.getStatusBarHeight();

        int staty_h = statusBarHeight + 20;

        int title_h = UIUtils.dip2px(29);

        startY = staty_h + title_h + 20;

        return startY;
    }

    public void updateSearchHeight(){
        mPopupWindow.setHeight(getsearchHeight(context));
        mPopupWindow.update(0, 0, ViewGroup.LayoutParams.WRAP_CONTENT, getsearchHeight(context));

    }

    @Override
    public void showAsDropDown(View view) {
        boundView = view;
        mPopupWindow.setHeight(getsearchHeight(context));
        super.showAsDropDown(view);
    }

    private void initView(){

        View layout = getLayout();

        history_layout = (RelativeLayout) layout.findViewById(R.id.history_layout);
        history_layout.setVisibility(View.VISIBLE);

        no_search_car = (TextView) layout.findViewById(R.id.no_search_car);

        search_content = (ListView) layout.findViewById(R.id.search_content);

        List<String> showHistoryList = getShowHistoryList();

        if(isListHistory){

            mHistorySearchAdapter = new HistorySearchAdapter(context,showHistoryList);

            search_content.setAdapter(mHistorySearchAdapter);
        }else{
//            TODO GridView init history

            historySearchGridViewAdapter = new HistorySearchGridViewAdapter(context, showHistoryList);

            history_search = (GridView) layout.findViewById(R.id.history_search);
            history_search.setVisibility(View.VISIBLE);
            history_search.setAdapter(historySearchGridViewAdapter);
            history_search.setOnItemClickListener(this);

        }

        search_content.setOnItemClickListener(this);


        TextView clean_history = (TextView) layout.findViewById(R.id.clean_history);
        clean_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchHistoryUtil.cleanGistory();
                historyList.clear();
                updateHistory();
            }
        });

        loading_pbar = (ProgressBar) layout.findViewById(R.id.loading_pbar);

    }

    /**
     * 更新搜索内容
     * @param searchString
     */
    public void updateContent(final String searchString){

        currentSearchString = searchString;

        loading_pbar.setVisibility(View.VISIBLE);
        search_content.setVisibility(View.GONE);

        persenter.getAllCar(new RequestAllCarListener() {
            @Override
            public void OnSuccessCarInfo(List<CarInfoBean> allCarList) {
                loading_pbar.setVisibility(View.GONE);
                search_content.setVisibility(View.VISIBLE);
                updateContent(allCarList, searchString);
            }

            @Override
            public void OnSuccessPlateNumberList(List<String> allCarPlateNumberList) {
                updateContentPlateNumberList(allCarPlateNumberList, searchString);
            }

            @Override
            public void OnError(String message) {
                ToastUtil.showShort(message);
                loading_pbar.setVisibility(View.GONE);
                search_content.setVisibility(View.VISIBLE);
            }
        });

    }

    private void updateContentPlateNumberList(final List<String> carPlateNumberList, final String searchString){


        new Thread(new Runnable() {
            @Override
            public void run() {

                //遍历所有车牌，移除未选择的车辆。
                Iterator<String> iterator = carPlateNumberList.iterator();
                //TODO remove no select car
                List<String> selectCarList = UserInfo.getInstance().getSelectCarList();

                while (iterator.hasNext()){

                    if(TextUtils.isEmpty(currentSearchString) || !currentSearchString.equals(searchString)){
//                       System.out.println("currentSearchString = "+currentSearchString+"   searchString = "+searchString);
                        return;
                    }

                    String plateNumber = iterator.next();

                    if(!plateNumber.contains(searchString)){
                        iterator.remove();
                    }

                    /*
                    //过滤未选择的车辆
                    if((selectCarList.size()>0 && selectCarList.contains(plateNumber)) || (selectCarList.size() == 0)){
                        if(!plateNumber.contains(searchString)){
                            iterator.remove();
                        }
                    }else{
                        iterator.remove();
                    }*/
                }

//                System.out.println("updateContentPlateNumberList end ===============================================================");

                UIUtils.runInMainThread(new Runnable() {
                    @Override
                    public void run() {

//                        ToastUtil.showShort("taskBean  updateContentPlateNumberList : "+searchString);

                        loading_pbar.setVisibility(View.GONE);
                        search_content.setVisibility(View.VISIBLE);

                        //是否显示无搜索内容
                        if(carPlateNumberList.size() == 0){
                            no_search_car.setVisibility(View.VISIBLE);
                        }else{
                            no_search_car.setVisibility(View.GONE);
                        }

                        if(mPlateNumberSearchAdapter == null){
                            mPlateNumberSearchAdapter = new PlateNumberSearchAdapter(carPlateNumberList, context);
                        }else{
                            mPlateNumberSearchAdapter.setData(carPlateNumberList);
                        }

                        search_content.setAdapter(mPlateNumberSearchAdapter);
                        //TODO 注册滑动监听，获取车辆状态信息
                        search_content.setOnScrollListener(SearchDilaog.this);

                        initCarInfo();

                        history_layout.setVisibility(View.GONE);

                    }
                });
            }
        }).start();

        /*//单线程任务管理标识，（同一标识的任务，按队列顺序执行）
        taskBean.setType("updateContentPlateNumberList");

        DBCtrlTask.getInstance().runTask(taskBean);*/


    }

    private void updateContent(List<CarInfoBean> mCarInfoList, String searchString){

       //过滤未选择的车辆
       Iterator<CarInfoBean> iterator = mCarInfoList.iterator();
        while (iterator.hasNext()){
            CarInfoBean carInfoBean = iterator.next();

            if(!carInfoBean.getPlateNumber().contains(searchString)){
                iterator.remove();
            }
//            TODO remove no select car
            /*List<String> selectCarList = UserInfo.getInstance().getSelectCarList();
            if((selectCarList.size()>0 && selectCarList.contains(carInfoBean.getPlateNumber())) || (selectCarList.size() == 0)){
                if(!carInfoBean.getPlateNumber().contains(searchString)){
                    iterator.remove();
                }
            }else{
                iterator.remove();
            }*/
        }

        if(mCarInfoList.size() == 0){
            no_search_car.setVisibility(View.VISIBLE);
        }else{
            no_search_car.setVisibility(View.GONE);
        }

        if(searchAdapter == null) {
            searchAdapter = new SearchAdapter(mCarInfoList, context);
        }else{
            searchAdapter.setData(mCarInfoList);
        }

        search_content.setAdapter(searchAdapter);
        search_content.setOnScrollListener(null);
        history_layout.setVisibility(View.GONE);
    }

    /**
     * 获取历史记录，根据选择了的车辆
     * @return
     */
    private List<String> getShowHistoryList(){
        if(historyList == null) {
            historyList = SearchHistoryUtil.getHistoryList();
        }

        List<String> showhistoryList = new ArrayList<>(historyList);
        Iterator<String> iterator = showhistoryList.iterator();

        while(iterator.hasNext()){
            String plateNumber = iterator.next();
            //匹配选择的车辆
//            boolean isSelectCarInfo = UserInfo.getInstance().isSelectCarInfo(plateNumber);
            boolean contains = UserInfo.getInstance().getPlateNumberList().contains(plateNumber);
            if(!contains){
                iterator.remove();
            }
        }

        return showhistoryList;
    }


    /**
     * 更新历史纪录
     */
    public void updateHistory(){
        history_layout.setVisibility(View.VISIBLE);

        List<String> showhistoryList = getShowHistoryList();

        if(isListHistory) {
            mHistorySearchAdapter.update(showhistoryList);
            search_content.setAdapter(mHistorySearchAdapter);
        }else{
//            TODO GridView updata
            historySearchGridViewAdapter.update(showhistoryList);
            history_search.setAdapter(historySearchGridViewAdapter);

        }

    }

    public void show(View v) {
        super.show(v, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0/*UIUtils.dip2px(7)*/);
//        super.show(v, Gravity.CENTER_HORIZONTAL, 0, 0/*UIUtils.dip2px(7)*/);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object item;
        if(history_layout.getVisibility() == View.VISIBLE){
            item = history_search.getAdapter().getItem(position);
        }else{
            item = search_content.getAdapter().getItem(position);
        }
        String plateNumber = "";
        if(mClickListeren !=null) {

            mClickListeren.OnClickBefore();

            if (item instanceof String) {
                plateNumber = (String) item;

            } else if (item instanceof CarInfoBean) {

                CarInfoBean mCarInfoBean = (CarInfoBean) item;
                plateNumber = mCarInfoBean.getPlateNumber();
            }

            if(TextUtils.isEmpty(plateNumber)){
                ToastUtil.showShort(context.getString(R.string.unidentifiable_car));
                mClickListeren.OnClickAfter();
                return;
            }

//           保存历史纪录
            if(!historyList.contains(plateNumber)){
                historyList.add(plateNumber);
                SearchHistoryUtil.saveHistory(historyList);
            }

            CarInfoBean newestCarInfo = UserInfo.getInstance().getNewestCarInfo(plateNumber);
            if (newestCarInfo == null) {

                ResponseListener responseListener = new ResponseListener() {
                    @Override
                    public void onSuccess(LoadResult successResult) {
                        BaseVo dataVo = successResult.getDataVo();
                        if (dataVo instanceof CarLatestStatus) {

                            CarLatestStatus mCarLatestStatus = (CarLatestStatus) dataVo;
                            CarInfoBean carInfoBean = mCarLatestStatus.toCarInfoBean();

                            mClickListeren.OnClick(carInfoBean);

                            //TODO 清除地图中其他Marker图标
                            persenter.cleanUnLockMarkerOnCearch();
                        } else if(dataVo instanceof ResponseCarStatusInfoBean) {

                            ResponseCarStatusInfoBean mResponseCarStatusInfoBean = (ResponseCarStatusInfoBean) dataVo;
                            CarInfoBean carInfoBean = mResponseCarStatusInfoBean.toCarInfoBean(null);


                            updataSelectCar(carInfoBean);

                            mClickListeren.OnClick(carInfoBean);

                            //TODO 清除地图中其他Marker图标
                            persenter.cleanUnLockMarkerOnCearch();

                        }else{
                            ToastUtil.showShort(context.getString(R.string.no_get_carinfo));
                            mClickListeren.OnClickAfter();
                        }
                    }

                    @Override
                    public void onError(LoadResult errorResult) {
                        ToastUtil.showShort(context.getString(R.string.no_get_carinfo));
                        mClickListeren.OnClickAfter();
                    }
                };

                if(MainApplication.isUserResponseUDP){
                    persenter.responseUDPGetCarInfo(plateNumber, responseListener);
                }else{
                    persenter.getCarInfo(plateNumber, responseListener);
                }


            } else {
                updataSelectCar(newestCarInfo);
                mClickListeren.OnClick(newestCarInfo);

                //TODO 清除地图中其他Marker图标
                //persenter.cleanUnLockMarkerOnCearch();
            }
        }
    }

    private void updataSelectCar(CarInfoBean carInfoBean){
        List<String> selectCarList = UserInfo.getInstance().getSelectCarList();
        if(!selectCarList.contains(carInfoBean.getPlateNumber())){
            UserInfo.getInstance().addSelectToCacheAndDb(carInfoBean);
        }
    }

    public void setClickListeren(SearchDialogClickListeren mClickListeren) {
        this.mClickListeren = mClickListeren;
    }


    //新接口需要监听listView的滑动
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == 0){

            isScroll = false;

            TaskBean taskBean = new TaskBean() {
                @Override
                public void run() {
                    getCarInfo();
                }
            };

            taskBean.setType("getCarInfo");

            TaskManager.runDBTask(taskBean);

        }else{
            isScroll = true;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        firstItem = firstVisibleItem;
        itemCount = visibleItemCount;
    }

    public void initCarInfo(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                String oldSearchStr = currentSearchString;

                int firstVisiblePosition = search_content.getFirstVisiblePosition();
                int childCount = search_content.getChildCount();
                int oldchildCount = -1;
//                LogUtils.error("childCount == " + childCount);

                while (childCount != oldchildCount) {

                    if (isScroll) {
                        return;
                    }

                    firstVisiblePosition = search_content.getFirstVisiblePosition();
                    childCount = search_content.getChildCount();
                    oldchildCount = childCount;

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    childCount = search_content.getChildCount();
                }

                if(!TextUtils.isEmpty(oldSearchStr) && oldSearchStr.equals(currentSearchString)) {

                    firstItem = firstVisiblePosition;
                    itemCount = childCount;

                    getCarInfo();
                }

            }
        }).start();


     /*   taskBean.setType("initCarInfo");

        TaskManager.runDBTask(taskBean);*/

    }

    private void getCarInfo(){

        String oldSearcgString = currentSearchString;

        for (int i = firstItem; i < (firstItem + itemCount); i++) {

            hasResult = false;

            if (isScroll) {
                return;
            }

            if(TextUtils.isEmpty(currentSearchString) || !oldSearcgString.equals(currentSearchString)){
                return;
            }

            String plateNumber = (String) mPlateNumberSearchAdapter.getItem(i);

            CarInfoBean carinfo = mPlateNumberSearchAdapter.getCarinfo(i);

            if(carinfo != null){
                continue;
            }

            currentParseCarInfo = i;

            persenter.responseUDPGetCarInfo(plateNumber, new ResponseListener() {
                @Override
                public void onSuccess(LoadResult successResult) {
                    View childAt = search_content.getChildAt(currentParseCarInfo - firstItem);

                    if(childAt == null){
                        return;
                    }

                    Object tag = childAt.getTag();
                    if(tag != null && tag instanceof BaseSearchAdapter.ViewHolder) {
                        BaseSearchAdapter.ViewHolder viewHolder = (BaseSearchAdapter.ViewHolder) tag;
                        BaseVo dataVo = successResult.getDataVo();
                        if(dataVo instanceof ResponseCarStatusInfoBean) {

                            ResponseCarStatusInfoBean mResponseCarStatusInfoBean = (ResponseCarStatusInfoBean) dataVo;
                            mPlateNumberSearchAdapter.initData(viewHolder,mResponseCarStatusInfoBean.toCarInfoBean(null));
                        }
                    }
                    hasResult = true;
                }

                @Override
                public void onError(LoadResult errorResult) {
                    hasResult = true;
                }
            });

           // long sendTime = System.currentTimeMillis();
            /*(System.currentTimeMillis()- sendTime < 1000)  &&*/

            //等待 当请求完成 、 请求超时 、 列表滑动操作 或 解析失败 等情况退出等待。
            while (!isScroll && !hasResult && !TextUtils.isEmpty(currentSearchString) && oldSearcgString.equals(currentSearchString));

        }
    }


}
