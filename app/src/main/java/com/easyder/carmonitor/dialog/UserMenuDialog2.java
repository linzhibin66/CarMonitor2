package com.easyder.carmonitor.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.UserMenuItemAdapter;
//import com.orhanobut.dialogplus.DialogPlus;
//import com.orhanobut.dialogplus.OnBackPressListener;
//import com.orhanobut.dialogplus.ViewHolder;
import com.shinetech.mvp.User.UserInfo;

/**
 * Created by ljn on 2017/2/24.
 */
public class UserMenuDialog2 {

//    private RoundImageView settings_usericon;
    private TextView settings_username;
    private TextView settings_userinfo;
    private ListView settings_list;
//    private DialogPlus progressDialog;
//    private ViewHolder mViewHolder;
    private Context context;

    public UserMenuDialog2(Context context) {
        this.context = context;
        creatDialog(context);
        initView();
        upData();
    }

    private void initView(){

        /*View layout = mViewHolder.getInflatedView();

        settings_username = (TextView) layout.findViewById(R.id.settings_username);
        settings_userinfo = (TextView) layout.findViewById(R.id.settings_userinfo);
        settings_list = (ListView) layout.findViewById(R.id.settings_list);*/
    }

    public void upData(){

        String userName = UserInfo.getInstance().getUserName();
        settings_username.setText(TextUtils.isEmpty(userName) ? "" : userName);

        //设置车辆数量
        String count = UserInfo.getInstance().getPlateNumberList().size()+"";
        String userinfo = context.getString(R.string.settings_userinfo, count);

        int index = userinfo.indexOf(count);

        SpannableStringBuilder style=new SpannableStringBuilder(userinfo);

        //数量字体修改颜色
        style.setSpan(new ForegroundColorSpan(0xFF00A2F6), index, index+count.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        settings_userinfo.setText(style);

        //items
        String[] stringArray = context.getResources().getStringArray(R.array.settings_items);

        int[] icon = new int[]{R.mipmap.icon_guide, R.mipmap.icon_help, R.mipmap.icon_advice, R.mipmap.icon_message, R.mipmap.icon_setting};

        UserMenuItemAdapter settingsItemAdapter = new UserMenuItemAdapter(context, stringArray,icon);

        settings_list.setAdapter(settingsItemAdapter);

    }

    public void creatDialog(Context context) {

//        mViewHolder = new ViewHolder(R.layout.main_user_menu);

        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();

        /*progressDialog = DialogPlus.newDialog(context)
                .setContentHolder(mViewHolder)
                .setGravity(Gravity.LEFT)
                .setInAnimation(R.anim.pop_left2right_anim_open)
                .setOutAnimation(R.anim.pop_left2right_anim_close)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        progressDialog.dismiss();
                    }
                })
                .setCancelable(true).create();*/

    }

    public void show(){
        /*if(!progressDialog.isShowing()){
            progressDialog.show();
        }*/
    }

    public void dismiss(){
        /*if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }*/

    }

}
