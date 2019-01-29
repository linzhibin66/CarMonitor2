package com.shinetech.mvp.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.shinetech.mvp.DB.greendao.DaoMaster;

/**
 * Created by ljn on 2017-05-18.
 */
public class DBUpGradeHelper extends DaoMaster.OpenHelper{

    public DBUpGradeHelper(Context context, String name) {
        super(context, name);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
//        TODO upGrade DB
        /*if (oldVersion == newVersion) {
            System.out.printf("数据库是最新版本" + oldVersion + "，不需要升级");
            return;
        }

        System.out.printf("数据库从版本" + oldVersion + "升级到版本" + newVersion);
        switch (oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE 'Note' ADD 'cose' TEXT;");
            case 2:
            default:
                break;
        }*/
    }
}
