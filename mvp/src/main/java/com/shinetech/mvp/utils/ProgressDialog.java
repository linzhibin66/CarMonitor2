/*
package com.shinetech.mvp.utils;

import android.content.Context;
import android.view.Gravity;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.EmptyHolder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.shinetech.mvp.R;

*/
/**
 * Created by ljn on 2017/2/20.
 *//*

public class ProgressDialog {

    DialogPlus progressDialog;

    public ProgressDialog(Context context) {
        EmptyHolder mViewHolder = new EmptyHolder(R.layout.progressdialog);

        progressDialog = DialogPlus.newDialog(context)
                .setContentHolder(mViewHolder)
                .setGravity(Gravity.CENTER)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        progressDialog.dismiss();
                    }
                })
                .setCancelable(true).create();


    }

    public void show(){
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    public void dismiss(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }

    }
}
*/
