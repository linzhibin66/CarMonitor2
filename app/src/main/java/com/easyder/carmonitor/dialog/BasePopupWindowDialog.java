package com.easyder.carmonitor.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.baidu.mobstat.StatService;
import com.shinetech.mvp.utils.UIUtils;

import java.lang.reflect.Method;

/**
 * Created by ljn on 2017/2/23.
 */
public abstract class BasePopupWindowDialog implements PopupWindow.OnDismissListener {

    protected final boolean isshowPopupWindow = true;

    protected MyPopupWindow mPopupWindow;

    protected View dialogView;

    protected Context context;

    private PopupWindow.OnDismissListener dismissListener;

    public BasePopupWindowDialog(Context context,int resource,ViewGroup.LayoutParams layoutParams) {

        this.context = context;
        dialogView = View.inflate(context, resource, null);
        initPopuWondow(dialogView, layoutParams);
    }

    public BasePopupWindowDialog(Context context,View layout,ViewGroup.LayoutParams layoutParams) {

        this.context = context;
        dialogView = layout;
        initPopuWondow(dialogView, layoutParams);
    }

    protected void initPopuWondow(View view,ViewGroup.LayoutParams layoutParams){

        if(layoutParams==null){
            mPopupWindow = new MyPopupWindow(view,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }else{
            mPopupWindow = new MyPopupWindow(view,layoutParams.width,layoutParams.height);
        }

        //防止虚拟软键盘被弹出菜单遮住
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

//        设置背景颜色
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#00FFFFFF"));
        setBackgrund(colorDrawable);

//        设置可以获取焦点
        mPopupWindow.setFocusable(false);
        mPopupWindow.setTouchable(true);

       /* mPopupWindow.setWindowLayoutType(
                WindowManager.LayoutParams.TYPE_APPLICATION);*/

        if(Build.VERSION.SDK_INT >= 22) {
            mPopupWindow.setAttachedInDecor(true);
        }

//        mPopupWindow.setWindowLayoutType();

//      设置可以触摸弹出框以外的区域
        mPopupWindow.setOutsideTouchable(true);

        mPopupWindow.setOnDismissListener(this);

    }

    public void setFocusable(boolean b){
        if(mPopupWindow!=null){
            mPopupWindow.setFocusable(b);
        }
    }

    public void updateFocusable(boolean b){
        if(mPopupWindow!=null){
            mPopupWindow.setFocusable(b);
            mPopupWindow.update();
        }
    }

    public void setPopAmimationListener(PopAmimationListener mPopAmimationListener){
        mPopupWindow.setPopAmimationListener(mPopAmimationListener);
    }

    public void setBackgrund(Drawable mDrawable){
        if(mPopupWindow!=null){
//          设置背景颜色
            mPopupWindow.setBackgroundDrawable(mDrawable);

        }
    }

    public void showAsDropDown(View view){
        if(!isShowing()){
            statisticsDialogStart();
            mPopupWindow.showAsDropDown(view);

        }
    }

    public void showAsDropDown(View view, int xoff, int yoff, int gravity){
        if(!isShowing()){
            statisticsDialogStart();
            mPopupWindow.showAsDropDown(view, xoff, yoff, gravity);

        }
    }

    public void show(View v, int gravity, int x, int y){

        if(!isShowing()){
            statisticsDialogStart();
            mPopupWindow.showAtLocation(v, gravity, x, y);
//            mPopupWindow.setFocusable(true);
        }
    }

    public void update(){
        if(mPopupWindow!= null){
            mPopupWindow.update();
        }
    }

    public void update(int width, int height){
        if(mPopupWindow!= null){
            mPopupWindow.update(width,height);
        }
    }

    public void setHeight(int height){
        if(mPopupWindow!= null){
            mPopupWindow.setHeight(height);
        }
    }

    public void setWidth(int width){
        if(mPopupWindow!= null){
            mPopupWindow.setWidth(width);
        }
    }

    public boolean isShowing(){
        return (mPopupWindow!=null && mPopupWindow.isShowing());
    }

    public void dismiss(){
        if(isShowing()){
            mPopupWindow.dismiss();
        }
    }

    public void noAmimationDismiss(){
        if(isShowing()){
            mPopupWindow.noAmimationDismiss();
        }
    }

    protected View getLayout(){
        return dialogView;
    }

    /**
     * 设置触摸外部区域，是否退出popupWindow
     * @param touchModal
     */
    public void setPopupWindowTouchModal(boolean touchModal){
        mPopupWindow.setOutsideTouchable(false);
        setPopupWindowTouchModal(mPopupWindow,touchModal);
    }

    /**
     * Set whether this window is touch modal or if outside touches will be sent
     * to
     * other windows behind it.
     *
     */
    private void setPopupWindowTouchModal(PopupWindow popupWindow,
                                                boolean touchModal) {
        if (null == popupWindow) {
            return;
        }
        Method method;
        try {

            method = PopupWindow.class.getDeclaredMethod("setTouchModal",
                    boolean.class);
            method.setAccessible(true);
            method.invoke(popupWindow, touchModal);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setAnimationStyle(int animStyle){
        mPopupWindow.setAnimationStyle(animStyle);
    }

    public void uodatePopupWindow(){
        mPopupWindow.update();
    }

    /**
     * 设置全屏显示
     */
    public void setALLWindow(){
//        mPopupWindow.setLayoutInScreenEnabled(true);
        mPopupWindow.setClippingEnabled(false);
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener dismissListener){
        this.dismissListener = dismissListener;
    }

    public static  int getDisplayWidth(Context context){

        Display defaultDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        if(width>height){
            return height;
        }else{
            return width;
        }

    }

    @Override
    public void onDismiss() {

        statisticsDialogEnd();
        if(dismissListener != null){
            dismissListener.onDismiss();
        }
    }

    /**
     * 百度统计 dialog页面
     */
    private void statisticsDialogStart(){
        StatService.onPageStart(context,this.getClass().getSimpleName());
    }
    private void statisticsDialogEnd(){
        StatService.onPageEnd(context,this.getClass().getSimpleName());
    }


}
