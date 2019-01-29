package com.easyder.carmonitor.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.ClassifyCarInfoAdater;
import com.easyder.carmonitor.interfaces.AllCarCheckBoxChanged;
import com.easyder.carmonitor.interfaces.AllCarClickListener;
import com.easyder.carmonitor.interfaces.AllCarClickPlateNumberListener;
import com.easyder.carmonitor.interfaces.AllCarSelectChanged;
import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskCtrl.DBCtrlTask;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.InfoTool.AllCarListClassify;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.presenter.AllCarListPresenter;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017/3/1.
 */
public class ClassifyCarInfoWidget implements AllCarCheckBoxChanged {

    public static final int ALL = 0;

    public static final int RUN = 1;

    public static final int STOP = 2;

    public static final int ALARM = 3;

    public static final int EXCEPTIOM = 4;

    private Context context;

    private ListView classify_carlistview;

    private RelativeLayout nocar_layout;

    private ProgressBar loading_pbar;

//    private EditText allcar_search_et;

    private int type = ALL;

    private AllCarListClassify mAllCarListClassify;

    private ClassifyCarInfoAdater classifyCarInfoAdater;

    private AllCarSelectChanged mAllCarSelectChanged;

    private View view;

    private AllCarListPresenter presenter;

    private AllCarClickListener mAllCarClickListener;

    private  String searchStr = "";

    public ClassifyCarInfoWidget(Context context,int ClassifyCarInfoWidgetType, AllCarListClassify mAllCarListClassify, AllCarListPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
        type = ClassifyCarInfoWidgetType;
        this.mAllCarListClassify = mAllCarListClassify;
        initView();
    }

    private void initView(){
        view = View.inflate(context, R.layout.classify_carlist_layout,null);
        classify_carlistview = (ListView) view.findViewById(R.id.classify_carlistview);
        nocar_layout = (RelativeLayout) view.findViewById(R.id.nocar_layout);
        loading_pbar = (ProgressBar) view.findViewById(R.id.loading_pbar);
//        allcar_search_et = (EditText) view.findViewById(R.id.allcar_search_et);

        /*allcar_search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                update(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        List<CarInfoBean> list = getList(type);

        if(list == null || list.size()==0){
            nocar_layout.setVisibility(View.VISIBLE);
        }else{
            nocar_layout.setVisibility(View.GONE);
        }

        classifyCarInfoAdater = new ClassifyCarInfoAdater(context, list, presenter);
        //注册checkbox 状态改变监听
        classifyCarInfoAdater.setmAllCarCheckBoxChanged(this);

//        设置点击车牌定位车辆
        classifyCarInfoAdater.setClickPlateNumber(new AllCarClickPlateNumberListener() {
            @Override
            public void onClickPlateNumber(CarInfoBean mCarInfoBean) {
                if (mAllCarClickListener != null) {
                    mAllCarClickListener.showOnlyCarMode(mCarInfoBean);

                }
            }
        });
        classify_carlistview.setAdapter(classifyCarInfoAdater);
        /*classify_carlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (classifyCarInfoAdater.isEditMode()) {
                    //编辑模式下的点击处理
                    classifyCarInfoAdater.clickItem(view);
                } else {
                    //非编辑模式下点击处理
                    Object item = classifyCarInfoAdater.getItem(position);
                    if (item != null && item instanceof CarInfoBean) {
                        CarInfoBean mCarInfoBean = (CarInfoBean) item;
                        if (mAllCarClickListener != null) {
                            mAllCarClickListener.showOnlyCarMode(mCarInfoBean);
                        }
                    } else {
                        ToastUtil.showShort(context.getString(R.string.data_error_again));
                    }
                }
            }
        });*/

    }

    /**
     * 设置编辑模式
     * @param isEditMode
     */
    public void setEditMode(boolean isEditMode){
        classifyCarInfoAdater.setEditMode(isEditMode);
    }

    /**
     * 设置条目点击事件（非编辑模式下）
     * @param mAllCarClickListener
     */
    public void setItemListener(AllCarClickListener mAllCarClickListener){
        this.mAllCarClickListener = mAllCarClickListener;
    }

    public View getView(){
        return view;
    }

    /**
     * 根系指定分类所有车辆信息
     * @param ClassifyCarInfoWidgetType
     * @param mAllCarListClassify
     */
    public void update(int ClassifyCarInfoWidgetType, AllCarListClassify mAllCarListClassify){
        type = ClassifyCarInfoWidgetType;
        this.mAllCarListClassify = mAllCarListClassify;
        List<CarInfoBean> list = getList(type);

        showNoCarHint(list == null || list.size()==0);

        classifyCarInfoAdater.update(list);

    }

    /**
     * 根系搜索内容列表
     * @param search
     */
    public void updateSearch(final String search){
        searchStr = search;

        if(TextUtils.isEmpty(search)){

            List<CarInfoBean> list = getList(type);
            loading_pbar.setVisibility(View.GONE);
            showNoCarHint(list == null || list.size()==0);

            classifyCarInfoAdater.update(list);

            onChanged();
        }else {

            loading_pbar.setVisibility(View.VISIBLE);

            TaskBean taskBean = new TaskBean() {
                @Override
                public void run() {

                    final List<CarInfoBean> searchList = new ArrayList<>();

                    List<CarInfoBean> list = getList(ClassifyCarInfoWidget.this.type);

                    for (CarInfoBean mCarInfoBean : list) {
//                        System.out.println("searchStr = "+searchStr+"   search = "+search);

                        if(TextUtils.isEmpty(searchStr) || !searchStr.equals(search)){
                            return;
                        }

                        if (mCarInfoBean.getPlateNumber().toLowerCase().contains(search.toLowerCase())) {
                            searchList.add(mCarInfoBean);
                        }
                    }

//                    System.out.println("search end ===============================================================");

                    UIUtils.runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            loading_pbar.setVisibility(View.GONE);
                            showNoCarHint(searchList == null || searchList.size() == 0);

                            classifyCarInfoAdater.update(searchList);
                            onChanged();
                        }
                    });


                }
            };

            taskBean.setType("updateSearch");

            DBCtrlTask.getInstance().runTask(taskBean);

        }
    }

    public void initData(){
        int count = classifyCarInfoAdater.getCount();
        List<CarInfoBean> list = getList(type);

        if(list.size() != count){
            showNoCarHint(false);
            classifyCarInfoAdater.update(list);
        }else{
            classifyCarInfoAdater.notifyDataSetChanged();
        }
    }

    private void showNoCarHint(boolean isshow){

        if(isshow){
            nocar_layout.setVisibility(View.VISIBLE);
        }else{
            nocar_layout.setVisibility(View.GONE);
        }
    }

    private List<CarInfoBean> getList(int type){
        switch (type){
            case ALL:
                return mAllCarListClassify.getAllcarList();
            case RUN:
                return mAllCarListClassify.getRunningCarList();
            case STOP:
                return mAllCarListClassify.getStopCarList();
            case EXCEPTIOM:
                return mAllCarListClassify.getExceptionList();
            case ALARM:
                return mAllCarListClassify.getAlarmCarList();
        }
        return new ArrayList<>();

    }

    /**
     * 设置车辆选择改变监听
     * @param mAllCarSelectChanged
     */
    public void setAllCarSelectChanged(AllCarSelectChanged mAllCarSelectChanged){
        this.mAllCarSelectChanged = mAllCarSelectChanged;
    }

    @Override
    public void onChanged() {
        if(mAllCarSelectChanged == null){
            return;
        }

        boolean isSeleAll = isSelectAll();

        mAllCarSelectChanged.onSelectChanged(isSeleAll);

    }

    /**
     * 判断是否已经全选该分类车辆
     * @return
     */
    public boolean isSelectAll(){

        boolean isSeleAll = false;

//        List<CarInfoBean> list = getList(type);
        //获取listData，进行全部选择
        List<CarInfoBean> dataList = classifyCarInfoAdater.getDataList();

        if(dataList.size() == 0){
            return isSeleAll;
        }

        for(CarInfoBean mCarInfoBean : dataList){
            CarInfoBean selectCarInfo = presenter.getSelectCarInfo(mCarInfoBean.getPlateNumber());
            if(selectCarInfo == null){
                isSeleAll = false;
                mAllCarSelectChanged.onSelectChanged(isSeleAll);
                return isSeleAll;
            }
        }

        isSeleAll = true;

        return isSeleAll;

    }

    /**
     * 全选该分类车辆
     */
    public void seleclAll(){

        //获取listData，进行全部选择
        List<CarInfoBean> dataList = classifyCarInfoAdater.getDataList();
//        List<CarInfoBean> list = getList(type);
        for(CarInfoBean mCarInfoBean : dataList){
            presenter.addCarToShow(mCarInfoBean);
        }
        classifyCarInfoAdater.notifyDataSetChanged();
    }

    /**
     * 取消该分类车辆的所有选择
     */
    public void removeAll(){
        List<CarInfoBean> list = getList(type);

        if(type == ALL){
            presenter.celanSelectCarAll();
            classifyCarInfoAdater.notifyDataSetChanged();
            return;
        }

        for(CarInfoBean mCarInfoBean : list){
            presenter.removeSelectCarInfo(mCarInfoBean);
        }

        classifyCarInfoAdater.notifyDataSetChanged();

    }

    public void clearSelect(){
        presenter.celanSelectCarAll();
        classifyCarInfoAdater.notifyDataSetChanged();
    }
}
