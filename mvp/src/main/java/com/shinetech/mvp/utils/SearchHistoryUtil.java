package com.shinetech.mvp.utils;

import android.text.TextUtils;

import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.bean.item.CarInfoBean;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-03-28.
 */
public class SearchHistoryUtil {

    private static final String SEARCH_HISTORY = "Search_History";

    public static void saveHistory(List<String> list){

        JSONArray jsonArray = new JSONArray(list);
        String jsonArrayString = jsonArray.toString();

        MainApplication.getPreferences().edit().putString(SEARCH_HISTORY,jsonArrayString).commit();

    }

    public static List<String> getHistoryList() {
        String string = MainApplication.getPreferences().getString(SEARCH_HISTORY, "");
        List<String> historyList = new ArrayList<>();

        if(TextUtils.isEmpty(string)){
            return historyList;
        }

        try {
            JSONArray jsonArray = new JSONArray(string);

            for(int i = 0; i < jsonArray.length(); i++) {
                String value = jsonArray.getString(i);
                int selectCarInfoCount = UserInfo.getInstance().getSelectCarCount();
                if(selectCarInfoCount>0) {
                    boolean selectCarInfo = UserInfo.getInstance().isSelectCarInfo(value);
                    if(selectCarInfo) {
                        historyList.add(value);
                    }
                }else{
                    historyList.add(value);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return historyList;

    }

    public static void cleanGistory(){
        MainApplication.getPreferences().edit().putString(SEARCH_HISTORY,"").commit();
    }

}
