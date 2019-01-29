package com.easyder.carmonitor.widget;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.DownloadMapUtils;
import com.easyder.carmonitor.adapter.OffLineMapCityAdpter;
import com.easyder.carmonitor.adapter.OffLineMapSearchAdapter;
import com.easyder.carmonitor.adapter.OffLineMapStatusClickListener;
import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskCtrl.DBCtrlTask;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.offlineMap.OffLineBaiduMapUtils;
import com.shinetech.mvp.utils.CityDBManager;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.SizeUtils;
import com.shinetech.mvp.utils.ToastUtil;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ljn on 2016/11/11.
 */
public class OffLineMapListWidget implements ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener, OnGetGeoCoderResultListener {

    private ExpandableListView offline_list;

    private LinearLayout headView;

    private OffLineMapCityAdpter mOffLineMapCityAdpter;

    private TextView current_cityName;

    private TextView current_city_size;

    private ImageView current_download_status;

    private View rootView;

    private EditText search;

    private ListView searchListView;

    private ImageButton offline_cancel_search;

    private GeoCoder geoCoder;

    private ReverseGeoCodeOption reverseGeoCodeOption;

    private OffLineMapStatusClickListener offLineMapStatusClickListener;

    private Context context;

    private final boolean isUserAllCity = true;

    private boolean isDebug = false && LogUtils.isDebug;

    List<MKOLSearchRecord> searchCityList = new ArrayList<>();

    private OffLineMapSearchAdapter mOffLineMapSearchAdapter;

    private final int UPDATE_SEARCH_LIST = 0;

    private Handler mHander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == UPDATE_SEARCH_LIST){
                if(mOffLineMapSearchAdapter == null){
                    mOffLineMapSearchAdapter = new OffLineMapSearchAdapter(searchCityList,context);
                    searchListView.setAdapter(mOffLineMapSearchAdapter);
                }else{
                    mOffLineMapSearchAdapter.update(searchCityList);
                }
            }
        }
    };

    /**
     * 所有支持离线地图的城市
     */
    private List<MKOLSearchRecord> offlineCityList = new ArrayList<>();

    public View getView(){
        return rootView;
    }

    public void initListView(Context context){
        this.context = context;
        initData(context);
        initGeoCoder();
        headView = (LinearLayout) View.inflate(context, R.layout.offlinemap_citylist_headview, null);
        current_cityName = (TextView) headView.findViewById(R.id.current_city_name);
        current_city_size = (TextView) headView.findViewById(R.id.current_download_status_text);
        current_download_status = (ImageView) headView.findViewById(R.id.current_download_status);

        //设置状态监听需要
        OffLineMapCityAdpter.GroupViewHolder groupViewHolder = new OffLineMapCityAdpter.GroupViewHolder();
        groupViewHolder.cityName = current_cityName;
        groupViewHolder.group_download_status = current_download_status;
        groupViewHolder.group_download_status_text = current_city_size;
        headView.setTag(groupViewHolder);

        LatLng locationLatLng = UserInfo.getInstance().getLocationLatLng();
        if(locationLatLng!=null) {
            reverseGeoCodeOption.location(locationLatLng);
            geoCoder.reverseGeoCode(reverseGeoCodeOption);
        }

        rootView = View.inflate(context, R.layout.offlinemap_list, null);
        offline_list = (ExpandableListView) rootView.findViewById(R.id.offline_listview);
        offline_list.addHeaderView(headView);
        offline_list.setGroupIndicator(null);
        offline_list.setAdapter(mOffLineMapCityAdpter);
        offline_list.setOnChildClickListener(this);
        offline_list.setOnGroupClickListener(this);

        initSearch(rootView);

    }

    private void initSearch(View rootview){

        search = (EditText) rootview.findViewById(R.id.offlinemap_list_search);
        searchListView = (ListView) rootview.findViewById(R.id.offline_searchlist);
        offline_cancel_search = (ImageButton) rootview.findViewById(R.id.offline_cancel_search);

        offline_cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSearch();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchStr(s.toString().trim());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchStr(final String search){

        if(!TextUtils.isEmpty(search)) {

            searchListView.setVisibility(View.VISIBLE);
            offline_list.setVisibility(View.GONE);
            offline_cancel_search.setVisibility(View.VISIBLE);

            TaskBean taskBean = new TaskBean() {
                @Override
                public void run() {
                    boolean alphabet = isAlphabet(search);

                    String when;
                    if(alphabet){
                        String substring = search.substring(0, 1);
                        when = substring.toUpperCase();
                    }else{
                        when = search.trim();
                    }
//                    System.out.println("when = "+when+"     "+search + "   onTextChanged : " + alphabet);
                    CityDBManager mCityDBManager = CityDBManager.getInstance(context);
                    SQLiteDatabase sqLiteDatabase = mCityDBManager.DBManager(context.getPackageName());
                    List<String> query = mCityDBManager.query(sqLiteDatabase, when, alphabet);

                    searchCityList.clear();

                    //TODO 获取所有支持离线地图的城市
                    OffLineBaiduMapUtils mOfflineUtils = OffLineBaiduMapUtils.getInstance();
                    for(String str : query){
//                        System.out.println("query : "+str);
                        List<MKOLSearchRecord> cityList = mOfflineUtils.getSearchCity(str);
                        if(cityList.size()>0){

                            for(MKOLSearchRecord city : cityList){
                                if(city.cityName.equals(str)){
                                    searchCityList.add(city);
                                }
                            }
                            //searchCityList.addAll(cityList);
                        }

                    }
                    mHander.removeMessages(UPDATE_SEARCH_LIST);
                    mHander.sendEmptyMessage(UPDATE_SEARCH_LIST);
                }
            };

            taskBean.setType("getCityList");

            DBCtrlTask.getInstance().runTask(taskBean);

        }else{
            searchListView.setVisibility(View.GONE);
            offline_list.setVisibility(View.VISIBLE);
            offline_cancel_search.setVisibility(View.GONE);
        }

    }

    /**
     * 是否进入搜索
     * @return
     */
    public boolean isSearchShow(){
        if(searchListView.getVisibility() == View.VISIBLE){
            return true;
        }
        return false;
    }

    /**
     * 取消搜索
     */
    public void cancelSearch(){
        search.setText("");
        hideInput(search);
    }

    /**
     * 关闭输入法
     *
     * @param view
     */
    public void hideInput(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
    /**
     * 开头是否是字母
     * @param str
     * @return
     */
    private boolean isAlphabet(String str){
        String strPattern = "^[A-Za-z]*$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(str);
        return m.matches();
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


    private void initData(Context context){

        //TODO 获取所有支持离线地图的城市
        OffLineBaiduMapUtils mOfflineUtils = OffLineBaiduMapUtils.getInstance();

        offlineCityList.clear();

        if(isUserAllCity) {
            List<MKOLSearchRecord> mOfflineCityList = mOfflineUtils.getOfflineCityList();

            if (mOfflineCityList != null && mOfflineCityList.size() > 0) {
                offlineCityList.addAll(mOfflineCityList);
            }

            Iterator<MKOLSearchRecord> iterator = offlineCityList.iterator();

            while(iterator.hasNext()){
                MKOLSearchRecord next = iterator.next();
                if(next.cityID>20000){
                    iterator.remove();
                    continue;
                }
                ArrayList<MKOLSearchRecord> childCities = next.childCities;
                if(childCities!= null && childCities.size()>0){
                    MKOLSearchRecord allCity = new MKOLSearchRecord();
                    allCity.cityName = context.getString(R.string.allcity);
                    allCity.cityID = next.cityID;
                    allCity.size = next.size;
                    allCity.cityType = 2;
                    childCities.add(0,allCity);
                }
            }

        }else {

            List<MKOLSearchRecord> cityList = mOfflineUtils.getSearchCity(context.getString(R.string.guangdong));

            for (MKOLSearchRecord mMKOLSearchRecord : cityList){
                if(mMKOLSearchRecord.cityID == 8){
                    // 创建所有城市条目
                    MKOLSearchRecord allCity = new MKOLSearchRecord();
                    allCity.cityName = context.getString(R.string.allcity);
                    allCity.cityID = mMKOLSearchRecord.cityID;
                    allCity.size = mMKOLSearchRecord.size;
                    allCity.cityType = 2;

                    offlineCityList.add(allCity);

                    ArrayList<MKOLSearchRecord> childCities = mMKOLSearchRecord.childCities;
                    if(childCities != null && childCities.size()>0){
                        offlineCityList.addAll(childCities);
                    }
                }

            }
        }

        //TODO　init Adapter
        mOffLineMapCityAdpter = new OffLineMapCityAdpter(context);

        mOffLineMapCityAdpter.setData(offlineCityList);

    }

    public void updata(){
        mOffLineMapCityAdpter.notifyDataSetChanged();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        MKOLSearchRecord child = (MKOLSearchRecord) mOffLineMapCityAdpter.getChild(groupPosition, childPosition);
        if(isDebug) LogUtils.debug("onChildClick city : " + child.cityName + "  cityid : " + child.cityID + "  cityType : " + child.cityType);
        if(isDebug) ToastUtil.showShort("onChildClick city : " + child.cityName + "  cityid : " + child.cityID);
        return true;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        MKOLSearchRecord group = (MKOLSearchRecord) mOffLineMapCityAdpter.getGroup(groupPosition);
        if(isDebug) LogUtils.debug("onGroupClick city : " + group.cityName + "  cityid : " + group.cityID + "  cityType : " + group.cityType  +"size = "+ SizeUtils.formatDataSize(group.size));
        if(isDebug) ToastUtil.showShort("onGroupClick city : " + group.cityName + "  cityid : " + group.cityID);
        return false;
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        ReverseGeoCodeResult.AddressComponent addressDetail = reverseGeoCodeResult.getAddressDetail();
        final String city = addressDetail.city;

//        ToastUtil.showShort("onGetReverseGeoCodeResult city : "+city);
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initCurrentLocationView(city);
            }
        });
    }

    private void initCurrentLocationView(String city){

        if(TextUtils.isEmpty(city)) {
            return;
        }
        current_cityName.setText(city);

        //TODO 获取所有支持离线地图的城市
        OffLineBaiduMapUtils mOfflineUtils = OffLineBaiduMapUtils.getInstance();

        List<MKOLSearchRecord> cityList = mOfflineUtils.getSearchCity(city);
        if(cityList!= null && cityList.size() == 1){

            MKOLSearchRecord mkolSearchRecord = cityList.get(0);

            if(mkolSearchRecord.cityName.equals(city)){
                MKOLUpdateElement updateInfo = mOfflineUtils.getUpdateInfo(mkolSearchRecord.cityID);

                if(offLineMapStatusClickListener == null) {
                    offLineMapStatusClickListener = new OffLineMapStatusClickListener(mkolSearchRecord.cityID, null, headView, mkolSearchRecord.cityID);
                }

                //判断是否在下载
                if(updateInfo == null) {
                    current_city_size.setText(SizeUtils.formatDataSize(mkolSearchRecord.size));
                    current_city_size.setTextColor(Color.parseColor("#8C8C8C"));
                    current_download_status.setImageResource(R.mipmap.btn_download);
                    current_download_status.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            offLineMapStatusClickListener.onClick(v);
                            //刷新list，使状态监听同步
                            if(mOffLineMapCityAdpter != null){
                                mOffLineMapCityAdpter.notifyDataSetChanged();
                            }
                        }
                    });
                }else{
                    current_city_size.setText(DownloadMapUtils.getDownLoadStaus(updateInfo.status) + "  " + SizeUtils.formatDataSize(mkolSearchRecord.size));

                    if(MKOLUpdateElement.FINISHED == updateInfo.status  || updateInfo.ratio == 100 ){
                        current_city_size.setTextColor(Color.parseColor("#B3B3B3"));
                        current_download_status.setImageResource(R.mipmap.btn_downloaded);
                    }else{
                        current_city_size.setTextColor(Color.parseColor("#E51C23"));
                        offLineMapStatusClickListener.registStatusListener();
                    }
                    current_download_status.setOnClickListener(null);

                }

            }
        }

    }

}
