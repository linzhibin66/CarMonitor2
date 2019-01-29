package com.shinetech.mvp.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-07-07.
 */
public class CityDBManager {

    private String DB_NAME = "citys.db";
    private Context mContext;
    private static CityDBManager mCityDBManager;

    private CityDBManager(Context mContext) {
        this.mContext = mContext;
    }

    public static CityDBManager getInstance(Context mContext){

        if(mCityDBManager == null){
            synchronized (mContext){
                if(mCityDBManager == null){
                    mCityDBManager = new CityDBManager(mContext);
                }
            }
        }

        return mCityDBManager;
    }
    //把assets目录下的db文件复制到dbpath下
    public SQLiteDatabase DBManager(String packName) {
        String dbPath = "/data/data/" + packName
                + "/databases/" + DB_NAME;
        if (!new File(dbPath).exists()) {
            try {
                FileOutputStream out = new FileOutputStream(dbPath);
                InputStream in = mContext.getAssets().open("citys.db");
                byte[] buffer = new byte[1024];
                int readBytes = 0;
                while ((readBytes = in.read(buffer)) != -1)
                    out.write(buffer, 0, readBytes);
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return SQLiteDatabase.openOrCreateDatabase(dbPath, null);
    }
    //查询
    public List<String> query(SQLiteDatabase sqliteDB,String where , boolean isPinYin) {

            List<String> resultList = new ArrayList<>();
        try {
            String table = "city";
            String[] columns = new String[]{"cityName"};
            String selection;
            if(isPinYin){
                selection = "keys like ?";
            }else{
                selection = "cityName like ?";
            }

            String[] selectionArgs = new String[]{"%"+where+"%"};

            Cursor cursor = sqliteDB.query(table, columns, selection, selectionArgs, null, null, null);

            for(cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext()){
                String cityName = cursor.getString(cursor.getColumnIndex("cityName"));
                resultList.add(cityName);
            }
                cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
