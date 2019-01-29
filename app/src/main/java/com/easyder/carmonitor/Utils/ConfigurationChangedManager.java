package com.easyder.carmonitor.Utils;

import android.view.ViewGroup;

import com.easyder.carmonitor.dialog.BasePopupWindowDialog;
import com.easyder.carmonitor.dialog.SearchDilaog;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.utils.SizeUtils;
import com.shinetech.mvp.utils.UIUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ljn on 2017-08-29.
 */

public class ConfigurationChangedManager {

    private static ConfigurationChangedManager mConfigurationChangedManager;

    private List<BasePopupWindowDialog> registConfigList = new ArrayList<>();
    private List<BasePopupWindowDialog> offLineMapConfigList = new ArrayList<>();

    private ConfigurationChangedManager() {
    }

    public static ConfigurationChangedManager getInstance(){
        if(mConfigurationChangedManager == null){
            synchronized(MainApplication.getInstance()) {
                if(mConfigurationChangedManager == null) {
                    mConfigurationChangedManager = new ConfigurationChangedManager();
                }
            }
        }
        return mConfigurationChangedManager;
    }

    public void registConfig(BasePopupWindowDialog dialog){
        synchronized (mConfigurationChangedManager) {
            if (dialog != null && !registConfigList.contains(dialog)) {
                registConfigList.add(dialog);
            }
        }
    }

    public void unRegistConfig(BasePopupWindowDialog dialog){
        synchronized (mConfigurationChangedManager) {
            if (dialog != null && registConfigList.contains(dialog)) {
                registConfigList.remove(dialog);
            }
        }
    }

    public void updateDialogView(){
        synchronized (mConfigurationChangedManager) {
            Iterator<BasePopupWindowDialog> iterator = registConfigList.iterator();
            while (iterator.hasNext()){
                BasePopupWindowDialog next = iterator.next();
                next.setHeight((UIUtils.getScreentHeight() - UIUtils.getNavigationBarHeight()));
                next.update(ViewGroup.LayoutParams.MATCH_PARENT, (UIUtils.getScreentHeight() - UIUtils.getNavigationBarHeight()));

            }

        }
    }

    public void registOffLineMapConfig(BasePopupWindowDialog dialog){
        synchronized (mConfigurationChangedManager) {
            if (dialog != null && !offLineMapConfigList.contains(dialog)) {
                offLineMapConfigList.add(dialog);
            }
        }
    }

    public void unRegistOffLineMapConfig(BasePopupWindowDialog dialog){
        synchronized (mConfigurationChangedManager) {
            if (dialog != null && offLineMapConfigList.contains(dialog)) {
                offLineMapConfigList.remove(dialog);
            }
        }
    }

    public void updateOffLineMapDialogView(){
        synchronized (mConfigurationChangedManager) {
            Iterator<BasePopupWindowDialog> iterator = offLineMapConfigList.iterator();
            while (iterator.hasNext()){
                BasePopupWindowDialog next = iterator.next();
                next.setHeight((UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight()));
                next.update(ViewGroup.LayoutParams.WRAP_CONTENT, (UIUtils.getScreentHeight()-UIUtils.getNavigationBarHeight()));
            }

        }
    }
}
