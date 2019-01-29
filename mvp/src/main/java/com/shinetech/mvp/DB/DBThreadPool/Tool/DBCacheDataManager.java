package com.shinetech.mvp.DB.DBThreadPool.Tool;

import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2016/12/30.
 */
public class DBCacheDataManager {

    /**
     * 任务顺序
     */
    private List<TaskBean> mTaskList = new ArrayList<>();

    /**
     * 执行中的任务类型
     */
    private List<String> mRunningTaskTypeList = new ArrayList<>();

    /**
     * 添加任务到队列中
     * @param task
     */
    public void addTask(TaskBean task){
        synchronized(this){
            if(!mTaskList.contains(task)){
                mTaskList.add(task);
            }
        }

    }

    /**
     * 根据类型获取未执行的任务
     * @param type
     * @return
     */
    public TaskBean getTaskOfType(String type){
        synchronized(this){
            for(TaskBean task : mTaskList){
                if(task.type == type){
                    return task;
                }
            }
            return null;
        }
    }

    /**
     * 从队列中移除任务
     * @param task
     */
    public void reMoveTask(TaskBean task){
        synchronized (this){
            if(mTaskList.contains(task)){
                mTaskList.remove(task);
            }
        }
    }

    /**
     * 添加类型到运行中的任务队列
     * @param type
     */
    public void addTypeList(String type){
        synchronized (this){
            if(!mRunningTaskTypeList.contains(type)){
                mRunningTaskTypeList.add(type);
            }
        }
    }

    /**
     * 从运行中的任务队列移除类型
     * @param type
     */
    public void reMoveTypeList(String type){
        synchronized (this){
            if(mRunningTaskTypeList.contains(type)){
                mRunningTaskTypeList.remove(type);
            }
        }
    }

    /**
     * 是否添加到RunningTaskTypeList中
     * @param task 任务
     * @param threadPoolSize 线程池大小
     * @return true 不添加，false 添加
     */
    public boolean disAddTaskCache(TaskBean task,int threadPoolSize){
        boolean disAddTaskCache = (!mRunningTaskTypeList.contains(task.type)) && (mRunningTaskTypeList.size() < threadPoolSize);
        return disAddTaskCache;
    }

    public List<TaskBean> getTaskList(){
        return mTaskList;
    }


}
