package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.CarInfoPagerAdater;
import com.easyder.carmonitor.interfaces.AllCarClickListener;
import com.easyder.carmonitor.interfaces.AllCarSelectChanged;
import com.easyder.carmonitor.interfaces.NavigationBarPagerListener;
import com.easyder.carmonitor.widget.NavigationBar;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.InfoTool.AllCarListClassify;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.CompanyAllCarBaseInfo;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;
import com.shinetech.mvp.network.UDP.presenter.AllCarListPresenter;
import com.shinetech.mvp.utils.UIUtils;
import com.shinetech.mvp.view.MvpView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ljn on 2017/4/07
 */
public class AllCarListDialog<M extends BaseVo> extends BasePopupWindowDialog implements NavigationBarPagerListener, View.OnClickListener , MvpView<M>, View.OnTouchListener {

    private RelativeLayout allcarlist_content;

    private RelativeLayout title_layout;

    /**
     * 返回按钮
     */
    private ImageButton title_back;

    /**
     * 编辑模式按钮
     */
    private ImageButton title_EditMode;

    /**
     * 取消编辑模式
     */
    private TextView title_cancel;

    /**
     * 确认选择
     */
    private TextView select_car_enter;

    /**
     * 全选当前列表
     */
    private CheckBox select_all_cb;

    /**
     * 全选布局
     */
    private LinearLayout select_all_layout;

    /**
     * 清除选择
     */
    private TextView select_car_clean;

    /**
     * 车辆选择提示
     */
    private TextView select_hint_tv;

    /**
     * 搜索框
     */
    private EditText allcar_title_search_et;

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

    private RelativeLayout allcarlist_layout_outmost;

    private RelativeLayout hint_loading_layout;

    /**
     * 底部控制布局
     */
    private RelativeLayout bottom_select;

    /**
     * 定时刷新车辆信息
     */
    private final int TIME2REFRESH = 1;

    private final int UPDATE_TITLE_SELECT = 2;

    /**
     * 更新滑动后，显示的状态内容的跟新
     */
    private final int UPDATE_PAGECHANGE_DATA = 3;

    private long TIME = 30*1000;

    private boolean toExeChangedListener = true;

    private boolean isSaveSelect = false;

    private AllCarListPresenter presenter;

    /**
     * 触摸外围区域是否退出弹框
     */
    private final boolean isdismissPopupWindow = false;

    private PopupWindow.OnDismissListener dismissListener;

    private boolean isTouch = false;

    private View bundView;

    private AllCarClickListener mAllCarClickListener;

//    private HintProgressDialog mHintProgressDialog;

    public boolean isRecorveMarker = true;

    /**
     * 是否设置2种模式切换
     */
    private final boolean hasEditMode = false ;

    private TextWatcher mTextWatcher;

    private String[] allcarlist_item_titleArray;

    /**
     * 定时刷新
     */
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case TIME2REFRESH:
                    //                System.out.println("Update data handleMessage-------------------------------------------------");
                    //更新分类车辆信息
                    updateAllCarList();
                    break;
                case UPDATE_TITLE_SELECT:
                    updateTitleSelect(navigationBar.getCurrentIndex());
                    break;
                case UPDATE_PAGECHANGE_DATA:
                    int position = (int) msg.obj;
                    carInfoPagerAdater.onPageSelectedUpdate(position);
                    break;
            }

        }
    };


    public AllCarListDialog(Context context) {
//        super(context, R.layout.activity_allcarlist_menu, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        super(context, R.layout.activity_allcarlist_menu, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight())));

        if(!isdismissPopupWindow){
            setPopupWindowTouchModal(false);
        }

        setALLWindow();

        setFocusable(true);

        this.presenter = (AllCarListPresenter) createPresenter();
        presenter.attachView((MvpView) this);
        initLayout();
        //修改dismiss方法，使dismiss之前执行指定的动画PopAmimationListener
        setPopAmimationListener(new PopAmimationListener(context, getLayout(), R.anim.pop_right2left_anim_close));


    }

    public void updateView(){
        setHeight((UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight()));

        presenter.updataSelectCarInfoMapCache();
        int selectCarInfoCount = presenter.getSelectCarInfoCount();
        updateSelectHintTV(selectCarInfoCount + "");
    }

    @Override
    public void setOnDismissListener(PopupWindow.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;

        super.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                onStopLoading();
                removeMessage();
                if (!hasEditMode) {
                    saveSelectCarToDB();
                }
                presenter.setCancelTask(true);
                presenter.detachView();
                AllCarListDialog.this.dismissListener.onDismiss();
            }
        });
    }

    public void show(View v){
        isTouch = false;
        bundView = v;
        show(v,Gravity.LEFT|Gravity.TOP, 0, 0);
//        showAsDropDown(v,  0, 0,Gravity.LEFT);
        presenter.attachView((MvpView) this);
        presenter.setCancelTask(false);
        //显示内容时，执行以下动画
        getLayout().startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_right2left_anim_open));
        if(carInfoPagerAdater != null){
            navigationBar.setCurrentItem(0);
            if(allcar_title_search_et != null && !TextUtils.isEmpty(allcar_title_search_et.getText())) {
                allcar_title_search_et.removeTextChangedListener(mTextWatcher);
                allcar_title_search_et.setText("");
                allcar_title_search_et.addTextChangedListener(mTextWatcher);
            }
        }

        updateAllCarList();

    }

    private void initView(){
        View layout = getLayout();
        allcarlist_content = (RelativeLayout) layout.findViewById(R.id.allcarlist_content);
        title_layout = (RelativeLayout) layout.findViewById(R.id.title_layout);
        title_back = (ImageButton) layout.findViewById(R.id.title_back);
        title_EditMode = (ImageButton) layout.findViewById(R.id.title_search);
        title_cancel = (TextView) layout.findViewById(R.id.title_cancel);
        select_car_enter = (TextView) layout.findViewById(R.id.select_car_enter);
        select_all_cb = (CheckBox) layout.findViewById(R.id.select_all_cb);
        select_all_layout = (LinearLayout) layout.findViewById(R.id.select_all_layout);
        select_hint_tv = (TextView) layout.findViewById(R.id.select_hint_tv);
        select_car_clean = (TextView) layout.findViewById(R.id.select_car_clean);
        allcarlist_layout_outmost = (RelativeLayout) layout.findViewById(R.id.allcarlist_layout_outmost);
        hint_loading_layout = (RelativeLayout) layout.findViewById(R.id.hint_loading_layout);
        bottom_select = (RelativeLayout) layout.findViewById(R.id.bottom_select);
        allcar_title_search_et = (EditText) layout.findViewById(R.id.allcar_title_search_et);

    }

    protected void initLayout() {

        initView();

        initTitleItem();

        //初始化导航栏框架
        navigationBar = new NavigationBar(context, itemList, null);

        allcarlist_content.addView(navigationBar.getLayout(), new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

//        updateAllCarList();

        //返回按钮
        title_back.setOnClickListener(this);

        if(!hasEditMode) {

            title_EditMode.setVisibility(View.GONE);

        }else{
            //编辑模式按钮
            title_EditMode.setOnClickListener(this);

            //取消编辑模式
            title_cancel.setOnClickListener(this);
        }

        initBottomLayout();

        title_EditMode.setImageResource(R.mipmap.icon_carlist_ctrl);

        UIUtils.setImmersiveStatusBar(title_layout);

        allcarlist_layout_outmost.setOnTouchListener(this);

        UpdateSearchHint(0);

    }

    private void updateSelectHintTV(String count){

            String select_hint = context.getString(R.string.bottom_select_hint, count);

            int index = select_hint.indexOf(count);

            SpannableStringBuilder style=new SpannableStringBuilder(select_hint);

            //数量字体修改颜色
            style.setSpan(new ForegroundColorSpan(0xFF2A65FE), index, index + count.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            style.setSpan(new RelativeSizeSpan(1.3f), index, index + count.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            style.setSpan(new StyleSpan(Typeface.BOLD), index, index+count.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            select_hint_tv.setText(style);
    }

    /**
     * 初始化底部选择界面
     */
    private void initBottomLayout(){

        updateSelectHintTV(presenter.getSelectCarInfoCount() + "");

        select_car_enter.setText(context.getString(R.string.bottom_select_enter, presenter.getSelectCarInfoCount() + ""));

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
                if (isChecked && carInfoPagerAdater != null) {
                    carInfoPagerAdater.selectAll(navigationBar.getCurrentIndex());
                } else {
                    carInfoPagerAdater.removeAll(navigationBar.getCurrentIndex());
                }

                int selectCarInfoCount = presenter.getSelectCarInfoCount();
                select_car_enter.setText(context.getString(R.string.bottom_select_enter, selectCarInfoCount + ""));
                updateSelectHintTV(selectCarInfoCount + "");
            }
        });

        select_car_clean.setOnClickListener(this);

    }

    /**
     * 初始化导航栏
     */
    private void initTitleItem(){

        stringArray = context.getResources().getStringArray(R.array.allcarlist_item);

        for(int i = 0; i<stringArray.length;i++){
            TextView textView = new TextView(context);
            textView.setTextColor(Color.parseColor("#FF666666"));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimensionPixelSize(R.dimen.allcarlist_title_textsize));
            textView.setGravity(Gravity.CENTER);
            textView.setText(String.format(stringArray[i],0));
            itemList.add(textView);
        }

    }

    protected MvpBasePresenter createPresenter() {
        return new AllCarListPresenter();
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

    /**
     * 获取到分类数据，并更新每个分类中的车辆数据
     * @param dataVo 解析成功后返回VO对象
     */
    @Override
    public void showContentView(BaseVo dataVo) {

        if(dataVo instanceof CompanyAllCarBaseInfo){
            AllCarListClassify allCarListClassify = presenter.getmAllCarListClassify();

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

            //初始化滑动页面内容
            if(carInfoPagerAdater==null){
                carInfoPagerAdater = new CarInfoPagerAdater(context, myAllCarListClassify, navigationBar.getStatusCount(),presenter);
                //添加选择监听
                carInfoPagerAdater.setAllCarSelectChanged(new AllCarSelectChanged() {
                    @Override
                    public void onSelectChanged(boolean seleclAll) {
//                        TODO select All or select count view update
//                        点击每个复选框时跟新全选状态和选择个数
                        int selectCarInfoCount = presenter.getSelectCarInfoCount();
                        select_car_enter.setText(context.getString(R.string.bottom_select_enter, selectCarInfoCount + ""));
                        updateSelectHintTV(selectCarInfoCount + "");

                        boolean checked = select_all_cb.isChecked();

                        if (checked != seleclAll) {
                            toExeChangedListener = false;
                            select_all_cb.setChecked(seleclAll);
                        }
                    }
                });
                navigationBar.setPagerAdapter(carInfoPagerAdater);
                //滑动监听
                navigationBar.setPagerListener(this);

                 //搜索功能监听
                mTextWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        carInfoPagerAdater.onSearchChanged(navigationBar.getCurrentIndex(),s.toString());

                        /*//跟新底部界面数据
                        updateSellectAllView(navigationBar.getCurrentIndex());*/
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                };
                allcar_title_search_et.addTextChangedListener(mTextWatcher);
                navigationBar.setSearchChangedListener(mTextWatcher);

                //跟新底部界面数据
                updateSellectAllView(navigationBar.getCurrentIndex());

                //跟新标题
                updateTitleSelect(navigationBar.getCurrentIndex());

                select_car_enter.setText(context.getString(R.string.bottom_select_enter, presenter.getSelectCarInfoCount() + ""));
                updateSelectHintTV(presenter.getSelectCarInfoCount() + "");

                //设置默认编辑模式，默认开
                isShowEditMode(true);

                //设置 点击事件
                if(mAllCarClickListener != null){
                    carInfoPagerAdater.setItemListener(new AllCarClickListener() {
                        @Override
                        public void showOnlyCarMode(CarInfoBean mCarInfoBean) {
                            isRecorveMarker = false;
                            presenter.addCarToShow(mCarInfoBean);
                            dismiss();
                            mAllCarClickListener.showOnlyCarMode(mCarInfoBean);
                        }
                    });
                }

            }else{

                //如果搜索中时，定时刷新来了，避免刷新出所有内容
                String searchStr = allcar_title_search_et.getText().toString().trim();
                if(!TextUtils.isEmpty(searchStr)){
                    carInfoPagerAdater.onSearchChanged(navigationBar.getCurrentIndex(),searchStr);

                }else {
                    //跟新当前列表车辆信息
                    carInfoPagerAdater.updatePsoition(navigationBar.getCurrentIndex(), myAllCarListClassify);
                }

            }

            //跟新标题车辆数量
            updateTitleView(myAllCarListClassify);

        }

    }

    /**
     * 设置点击事件（不是编辑模式时）
     * @param mAllCarClickListener
     */
    public void setItemListener(AllCarClickListener mAllCarClickListener){
        this.mAllCarClickListener = mAllCarClickListener;
    }

    protected void onPause() {

        removeMessage();
    }


    protected void onDestroy() {
        mHandler = null;
    }

    private void updateAllCarList(){
        //更新分类车辆信息
        removeMessage();
        presenter.getAllCar();
        if(mHandler !=null){
            mHandler.sendEmptyMessageDelayed(TIME2REFRESH, TIME);
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
        navigationBar.cleanSearchContent();
        cleanTitleSearchContent();
//        updateAllCarList();

        mHandler.removeMessages(UPDATE_PAGECHANGE_DATA);

        //跟新当前显示的状态的数据
        Message obtain = Message.obtain();
        obtain.obj = position;
        obtain.what = UPDATE_PAGECHANGE_DATA;
        mHandler.sendMessageDelayed(obtain, 300);

        updateSellectAllView(position);
        sendToUpdateSelect();
    }

    private void sendToUpdateSelect(){

        if(mHandler !=null){
            mHandler.removeMessages(UPDATE_TITLE_SELECT);
            mHandler.sendEmptyMessage(UPDATE_TITLE_SELECT);
        }
    }

    private void updateTitleSelect(int position){
        int statusCount = navigationBar.getStatusCount();
        for(int i = 0; i<statusCount; i++){
                TextView statusItem = (TextView) navigationBar.getStatusItem(i);
            if(i == position){
                statusItem.setTextColor(Color.parseColor("#FF2C5DFB"));
            }else{
                statusItem.setTextColor(Color.parseColor("#FF666666"));
            }

        }
    }

    private void cleanTitleSearchContent(){
        if(allcar_title_search_et != null && !TextUtils.isEmpty(allcar_title_search_et.getText())) {
            allcar_title_search_et.removeTextChangedListener(mTextWatcher);
            allcar_title_search_et.setText("");
            allcar_title_search_et.addTextChangedListener(mTextWatcher);
        }
        UpdateSearchHint(navigationBar.getCurrentIndex());
    }

    private void UpdateSearchHint(int positon){

        if(allcarlist_item_titleArray == null || allcarlist_item_titleArray.length == 0) {
            allcarlist_item_titleArray = context.getResources().getStringArray(R.array.allcarlist_item_title);
        }

        if(allcar_title_search_et != null) {
            allcar_title_search_et.setHint(context.getString(R.string.title_search_layout_hint, allcarlist_item_titleArray[positon]));
        }

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

    /**
     * 编辑模式设置
     * @param isshow
     */
    public void isShowEditMode(boolean isshow){

        if(!hasEditMode) {
            return;
        }

        if(isshow){
            title_cancel.setVisibility(View.VISIBLE);
            title_EditMode.setVisibility(View.GONE);
            bottom_select.setVisibility(View.VISIBLE);
        }else{
            title_cancel.setVisibility(View.GONE);
            title_EditMode.setVisibility(View.VISIBLE);
            bottom_select.setVisibility(View.GONE);
        }
        //始终现在编辑状态
        carInfoPagerAdater.setEditMode(isshow);
        updateAllCarList();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.title_back:
                dismiss();
                isTouch = true;
                break;
            case R.id.title_cancel:
                isShowEditMode(false);
                break;
            case R.id.title_search:
//                TODO EditMode
                isShowEditMode(true);
                break;
            case R.id.select_car_enter:
//                TODO save to db
                UserInfo.getInstance().addSelectToCacheAndDb(presenter.getSelectCarInfoMapCache());
                dismiss();
                break;
            case R.id.select_car_clean:
                carInfoPagerAdater.clearSelect(navigationBar.getCurrentIndex());
//                UserInfo.getInstance().celanSelectCarAll();

                boolean checked = select_all_cb.isChecked();
                if(checked){
                    toExeChangedListener = false;
                    select_all_cb.setChecked(false);
                }

                int selectCarInfoCount =  presenter.getSelectCarInfoCount();
                select_car_enter.setText(context.getString(R.string.bottom_select_enter, selectCarInfoCount + ""));

                break;
        }
    }

    private void saveSelectCarToDB(){
//      TODO save to db
       Map<String, CarInfoBean> selectCarInfoMapCache = presenter.getSelectCarInfoMapCache();
//        if(selectCarInfoMapCache.size()>0) {
            UserInfo.getInstance().addSelectToCacheAndDb(selectCarInfoMapCache);
//        }

    }

    @Override
    public void onLoading() {

        hint_loading_layout.setVisibility(View.VISIBLE);

        /*if(mHintProgressDialog == null) {
            mHintProgressDialog = new HintProgressDialog(context);
        }
        if(!mHintProgressDialog.isShowing()) {
            mHintProgressDialog.show(bundView);
        }*/

    }

    @Override
    public void onStopLoading() {

        hint_loading_layout.setVisibility(View.GONE);

        /*if(mHintProgressDialog!= null && mHintProgressDialog.isShowing()){
            mHintProgressDialog.dismiss();
        }*/

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isTouch){
            dismiss();
            isTouch = true;
        }
        return true;
    }
}
