package com.shinetech.mvp.DB.DBThreadPool.Task;

/**
 * Created by ljn on 2016/12/22.
 */
public abstract class TaskBean implements Runnable {

    /**
     * 任务类型，
     * 通过类型进行排队执行任务，
     * 同一类型的任务不能同时执行。
     */
    public String type;

    public TaskBean setType(String type) {
        this.type = type;
        return this;
    }
}
