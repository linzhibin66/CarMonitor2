package com.easyder.carmonitor.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.dialog.ProgressDialog;
import com.easyder.carmonitor.dialog.SettingSelectDialog;
import com.easyder.carmonitor.interfaces.SettingSelectListener;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.DatagramPacketDefine;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.LoginBean;
import com.shinetech.mvp.network.UDP.presenter.LoginPresenter;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

import butterknife.Bind;

public class LoginActivity extends BaseActivity{

    @Bind(R.id.username)
    EditText et_userName;

    @Bind(R.id.pwd)
    EditText et_pwd;

    @Bind(R.id.bt_login)
    Button bt_login;

     @Bind(R.id.login_icon_bg)
     ImageView login_icon_bg;

    @Bind(R.id.platenumber_color)
    Spinner platenumber_color;

    @Bind(R.id.platenumber_color_layout)
    RelativeLayout platenumber_color_layout;

    @Bind(R.id.login_type_platenumber_layout)
    RelativeLayout login_type_platenumber_layout;

    @Bind(R.id.login_type_platenumber_tv)
    TextView login_type_platenumber_tv;

    @Bind(R.id.login_type_group_layout)
    RelativeLayout login_type_group_layout;

    @Bind(R.id.login_type_group_tv)
    TextView login_type_group_tv;

    @Bind(R.id.login_type_company_layout)
    RelativeLayout login_type_company_layout;

    @Bind(R.id.login_type_company_tv)
    TextView login_type_company_tv;

    @Bind(R.id.login_type_gis_layout)
    RelativeLayout login_type_gis_layout;

    @Bind(R.id.platenumber_color_tv)
    TextView platenumber_color_tv;

    @Bind(R.id.login_type_gis_tv)
    TextView login_type_gis_tv;

    @Bind(R.id.remember_pwd_check)
    CheckBox remember_pwd_check;

    @Bind(R.id.accountexplain_tv)
    TextView accountexplain_tv;

    /**
     * DatagramPacketDefine中查看Type
     */
    private byte loginType;

    /**
     * 加载提示
     */
    private ProgressDialog progressDialog;

    /**
     * 车牌颜色选择适配器
     */
    private ArrayAdapter<String> platenumber_color_adapter;

    /**
     * 当前选择的车牌颜色
     */
    private String currentPlateNumberColor;

    private SettingSelectDialog mSettingSelectDialog;

    private final boolean isDebug = false && LogUtils.isDebug;;

    private boolean iskillApp = true;

    private boolean isShowDialogSelect = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(MainApplication.getInstance().isInit()) {
            //在使用SDK各组件之前初始化context信息，传入ApplicationContext
            //注意该方法要再setContentView方法之前实现
            SDKInitializer.initialize(MainApplication.getInstance());
            MainApplication.getInstance().finishInit();
        }

        UserInfo.getInstance().cleanUserInfo();
        initView();
    }

    @Override
    protected int getView() {
        return R.layout.activity_login;
    }

    private void initView(){
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginCommit();
            }
        });
        initLoginMode();
//        checkLoginMode(R.id.login_type_group_layout);
        initHistoryLogin();
        initLogo(getResources().getConfiguration().orientation);
//        accountexplain_tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        accountexplain_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AccountExplainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initHistoryLogin(){

        String historyLoginName = UserInfo.getInstance().getHistoryLoginName();
        if(TextUtils.isEmpty(historyLoginName)){
            checkLoginMode(R.id.login_type_platenumber_layout);
            loginType = DatagramPacketDefine.USER_TYPE;
        }else{

            //历史登录
            if(et_userName !=  null) {
                et_userName.setText(historyLoginName);
            }

            //设置登录类型
            byte historyLoginType = UserInfo.getInstance().getHistoryLoginType();
            loginType = historyLoginType;
            int loginTypeIDOfByte = getLoginTypeIDOfByte(historyLoginType);
            checkLoginMode(loginTypeIDOfByte);

            if(historyLoginType == DatagramPacketDefine.USER_TYPE){
                String historyCarColor = UserInfo.getInstance().getHistoryCarColor();
                if(!TextUtils.isEmpty(historyCarColor) && (platenumber_color_tv != null)){
                    if(isShowDialogSelect){
                       platenumber_color_tv.setText(historyCarColor);
                    }/*else {
                        currentPlateNumberColor = platenumber_color.getSelectedItem().toString();
                    }*/
                }
            }
            boolean rememberPwdSetting = UserInfo.getInstance().getRememberPwdSetting();

            if(rememberPwdSetting) {
                //历史登录密码
                String historyUserPwd = UserInfo.getInstance().getHistoryUserPwd();
                if (!TextUtils.isEmpty(historyUserPwd) && (et_pwd != null)) {
                    et_pwd.setText(historyUserPwd);
                }
            }else{
                et_pwd.setText("");
            }

            if(remember_pwd_check != null) {
                remember_pwd_check.setChecked(rememberPwdSetting);
            }
        }



    }

    private int getLoginTypeIDOfByte(byte loginType){

        switch (loginType){
            case DatagramPacketDefine.USER_TYPE:
                return R.id.login_type_platenumber_layout;
            case DatagramPacketDefine.GROUP_TYPE:
                return R.id.login_type_group_layout;
            case DatagramPacketDefine.COMPANY_TYPE:
                return R.id.login_type_company_layout;
            case DatagramPacketDefine.GIS_TYPE:
                return R.id.login_type_gis_layout;
            default:
                return R.id.login_type_platenumber_layout;
        }
    }

    /**
     * 检测登录模式，并设置界面
     */
    private void checkLoginMode(int id){

        switch (id){
            case R.id.login_type_platenumber_layout:
                login_type_platenumber_layout.setBackgroundResource(R.mipmap.login_type_click_bg);
                login_type_group_layout.setBackgroundResource(R.drawable.login_type_bg_n);
                login_type_gis_layout.setBackgroundResource(R.drawable.login_type_bg_n);
                login_type_company_layout.setBackgroundResource(R.drawable.login_type_bg_n);

                login_type_platenumber_tv.setTextColor(Color.parseColor("#2F53FE"));
                login_type_group_tv.setTextColor(Color.parseColor("#929292"));
                login_type_gis_tv.setTextColor(Color.parseColor("#929292"));
                login_type_company_tv.setTextColor(Color.parseColor("#929292"));


                platenumber_color_layout.setVisibility(View.VISIBLE);

                et_userName.setHint(getString(R.string.login_user_name_personage));

                if(isShowDialogSelect){
                    initPlateNumberColorDialog();
                }else {
                    initPlateNumberColor();
                }
                break;
            case R.id.login_type_group_layout:

                platenumber_color_layout.setVisibility(View.GONE);

                login_type_platenumber_layout.setBackgroundResource(R.drawable.login_type_bg_n);
                login_type_group_layout.setBackgroundResource(R.mipmap.login_type_click_bg);
                login_type_gis_layout.setBackgroundResource(R.drawable.login_type_bg_n);
                login_type_company_layout.setBackgroundResource(R.drawable.login_type_bg_n);

                login_type_platenumber_tv.setTextColor(Color.parseColor("#929292"));
                login_type_group_tv.setTextColor(Color.parseColor("#2F53FE"));
                login_type_gis_tv.setTextColor(Color.parseColor("#929292"));
                login_type_company_tv.setTextColor(Color.parseColor("#929292"));
                et_userName.setHint(getString(R.string.login_user_name_group));

                break;

            case R.id.login_type_company_layout:

                platenumber_color_layout.setVisibility(View.GONE);

                login_type_platenumber_layout.setBackgroundResource(R.drawable.login_type_bg_n);
                login_type_group_layout.setBackgroundResource(R.drawable.login_type_bg_n);
                login_type_company_layout.setBackgroundResource(R.mipmap.login_type_click_bg);
                login_type_gis_layout.setBackgroundResource(R.drawable.login_type_bg_n);

                login_type_platenumber_tv.setTextColor(Color.parseColor("#929292"));
                login_type_group_tv.setTextColor(Color.parseColor("#929292"));
                login_type_company_tv.setTextColor(Color.parseColor("#2F53FE"));
                login_type_gis_tv.setTextColor(Color.parseColor("#929292"));
                et_userName.setHint(getString(R.string.login_user_name_company));
                break;

            case R.id.login_type_gis_layout:

                platenumber_color_layout.setVisibility(View.GONE);

                login_type_platenumber_layout.setBackgroundResource(R.drawable.login_type_bg_n);
                login_type_group_layout.setBackgroundResource(R.drawable.login_type_bg_n);
                login_type_company_layout.setBackgroundResource(R.drawable.login_type_bg_n);
                login_type_gis_layout.setBackgroundResource(R.mipmap.login_type_click_bg);

                login_type_platenumber_tv.setTextColor(Color.parseColor("#929292"));
                login_type_group_tv.setTextColor(Color.parseColor("#929292"));
                login_type_company_tv.setTextColor(Color.parseColor("#929292"));
                login_type_gis_tv.setTextColor(Color.parseColor("#2F53FE"));
                et_userName.setHint(getString(R.string.login_user_name_gis));

                break;

        }

    }

    /**
     * 初始化车牌颜色控件
     */
    private void initPlateNumberColor(){

        if(platenumber_color_adapter != null){
            return;
        }

        // 建立数据源
        String[] mItems = getResources().getStringArray(R.array.platenumber_color);
        // 建立Adapter并且绑定数据源
        platenumber_color_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
        platenumber_color_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        platenumber_color .setAdapter(platenumber_color_adapter);
        platenumber_color.setDropDownVerticalOffset(UIUtils.dip2px(45));
        platenumber_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                currentPlateNumberColor = platenumber_color.getSelectedItem().toString();
                ToastUtil.showShort(currentPlateNumberColor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    /**
     * 初始化车牌颜色控件
     */
    public void initPlateNumberColorDialog(){

        if(mSettingSelectDialog != null) {
            return;
        }

        String[] stringArray = getResources().getStringArray(R.array.platenumber_color);

        platenumber_color_tv.setText(stringArray[0]);

        mSettingSelectDialog = new SettingSelectDialog(this, stringArray, "", 0);

        mSettingSelectDialog.setTitle(getString(R.string.login_type_platenumber_color));

        mSettingSelectDialog.setOnSelectListener(new SettingSelectListener() {
            @Override
            public void onSelect(String select) {

                platenumber_color_tv.setText(select);

            }
        });

        platenumber_color_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentColor = platenumber_color_tv.getText().toString();

                String[] stringArray = getResources().getStringArray(R.array.platenumber_color);

                int index = 0;
                for(int i = 0; i<stringArray.length; i++){
                    if(stringArray[i].startsWith(currentColor + "")){
                        index = i;
                        break;
                    }
                }

                mSettingSelectDialog.setindex(index);

                mSettingSelectDialog.show(platenumber_color_tv);
            }
        });
    }

    /**
     * 初始化登录模式
     */
    private void initLoginMode(){

        login_type_platenumber_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginType = DatagramPacketDefine.USER_TYPE;
                checkLoginMode(v.getId());
            }
        });

        login_type_group_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginType = DatagramPacketDefine.GROUP_TYPE;
                checkLoginMode(v.getId());
            }
        });

        login_type_company_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginType = DatagramPacketDefine.COMPANY_TYPE;
                checkLoginMode(v.getId());
            }
        });

        login_type_gis_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginType = DatagramPacketDefine.GIS_TYPE;
                checkLoginMode(v.getId());
            }
        });
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return new LoginPresenter();
    }


    @Override
    public void showContentView(BaseVo dataVo) {
        if(dataVo instanceof LoginBean){
            LoginBean mLoginBean = (LoginBean) dataVo;
            if(mLoginBean.getLoginResult() == 0x01){
                if(isDebug)System.out.println("login success "+UserInfo.getInstance().getUserName());
                iskillApp = false;
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.pop_right2left_anim_open, R.anim.pop_right2left_anim_close);
            }
        }

    }

    @Override
    public void onLoading() {
        /*if(progressDialog==null){
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.show();*/
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
        }

        progressDialog.show(bt_login);


    }
    @Override
    public void onStopLoading() {
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }

    }

    /**
     * 登录操作
     */
    private void loginCommit(){

        if(et_userName==null) {
//          TODO 无法获取用户名，退出界面再重新进入
            ToastUtil.showShort(getString(R.string.username_error));
            return;
        }

        String usermame = et_userName.getText().toString().trim();

        if(TextUtils.isEmpty(usermame)) {
//          TODO 用户名不能为空
            ToastUtil.showShort(getString(R.string.username_null));
            return;
        }

        if(loginType == DatagramPacketDefine.USER_TYPE){
            if(isShowDialogSelect){
                currentPlateNumberColor = platenumber_color_tv.getText().toString();
            }else {
                currentPlateNumberColor = platenumber_color.getSelectedItem().toString();
            }
            usermame = currentPlateNumberColor+ " "+ usermame;
        }

        if(et_pwd==null){
//          TODO 无法获取密码，退出界面再重新进入
            ToastUtil.showShort(getString(R.string.pwd_error));
            return;
        }

        String pwd = et_pwd.getText().toString().trim();

        if(TextUtils.isEmpty(pwd)){
//            TODO 密码不要能为空
            ToastUtil.showShort(getString(R.string.pwd_null));
            return ;
        }

        UserInfo.getInstance().setUserName(usermame);

        if(remember_pwd_check == null){
            ToastUtil.showShort(getString(R.string.remenber_error));
            return;
        }

        boolean checked = remember_pwd_check.isChecked();

        //得到结果后会回掉 showContentView()
        ((LoginPresenter)presenter).login(usermame, pwd, loginType, checked);

        return;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(progressDialog!= null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                return true;
            }else{
                super.onKeyDown(keyCode, event);
                iskillApp = true;
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(iskillApp) {
            CarMonitorApplication.getInstance().exitApp();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

       initLogo(newConfig.orientation);

    }

    private void initLogo(int orientation){
        //切换为竖屏
        if (orientation == this.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
            login_icon_bg.setBackgroundResource(R.mipmap.login_bg);
            login_icon_bg.setImageBitmap(null);
        }
//切换为横屏
        else if (orientation == this.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
            login_icon_bg.setBackgroundResource(R.mipmap.login_logo_bg);
            login_icon_bg.setImageResource(R.mipmap.logo_login);
        }
    }
}
