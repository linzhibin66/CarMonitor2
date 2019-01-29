package com.easyder.carmonitor.Utils;

import android.text.TextUtils;

import com.shinetech.mvp.User.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-11-22.
 */

public class JsonUtil {

    public static String stringListToJsonString(List<String> list){
        JSONArray jsonArray = new JSONArray(list);
        String jsonArrayString = jsonArray.toString();

        return jsonArrayString;
    }

    public static List<String> JSONArrayToList(String jsonArrayString){
        List<String> list = new ArrayList<>();

        if(TextUtils.isEmpty(jsonArrayString)){
            return list;
        }

        try {
            JSONArray jsonArray = new JSONArray(jsonArrayString);

            for(int i = 0; i < jsonArray.length(); i++) {
                String value = jsonArray.getString(i);
                list.add(value);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }


}
