package com.shinetech.mvp.DB.DBThreadPool;

import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskCtrl.DBCtrlTask;

/**
 * Created by ljn on 2017/1/3.
 */
public class TaskManager {

    /**
     * DB task
     * @param mDBTask
     */
    public static void runDBTask(TaskBean mDBTask){
        DBCtrlTask dbCtrlTask = DBCtrlTask.getInstance();
        dbCtrlTask.runTask(mDBTask);
    }

}
