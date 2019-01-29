package com.shinetech.mvp.DB.DBThreadPool.TaskCtrl;

import android.text.TextUtils;

import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.Tool.DBCacheDataManager;
import com.shinetech.mvp.DB.DBThreadPool.Tool.ThreadPoolManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;

/**
 * Created by ljn on 2016/12/30.
 */
public class DBCtrlTask extends BaseCtrlTask{

    private static DBCtrlTask mDBCtrlTask;

    DBCacheDataManager mDBCacheDataManager;

    ThreadPoolManager mThreadPoolManager;

    private DBCtrlTask(){
        mDBCacheDataManager = new DBCacheDataManager();
        mThreadPoolManager = new ThreadPoolManager();
    }

    public static DBCtrlTask getInstance(){
        if(mDBCtrlTask==null){
            mDBCtrlTask = new DBCtrlTask();
        }

        return mDBCtrlTask;

    }

    @Override
    protected boolean checkTask(TaskBean task,boolean isAddList){

        if(task == null)
            return false;

        //无类型的任务，表示任何时候都可执行的多线程任务，直接扔到线程中执行队列中。
        if (TextUtils.isEmpty(task.type)){
            mDBCacheDataManager.reMoveTask(task);
//          TODO　execute task
            return true;
        }

        //任务数量未达到最大，且执行的任务中无此type时，执行此任务
        if(mDBCacheDataManager.disAddTaskCache(task, mThreadPoolManager.THREADPOOL_SIZE)){
            mDBCacheDataManager.addTypeList(task.type);
            //任务队列中移除此任务(防止重复)
            mDBCacheDataManager.reMoveTask(task);
//          TODO execute task
            return true;
        }else{
            if(isAddList){
                mDBCacheDataManager.addTask(task);
            }
        }
        return false;
    }

    @Override
    protected void getNextTask(){
        synchronized (this){
            Iterator<TaskBean> iterator = mDBCacheDataManager.getTaskList().iterator();
            while(iterator.hasNext()){
                TaskBean taskBean = iterator.next();
                boolean b = checkTask(taskBean, false);
                if(b){
                    executeTask(taskBean);
                    return;
                }
            }
        }
    }

    @Override
    protected void executeTask(final TaskBean task){

        // 代理 task 在run完成后获取下一个任务执行
        Runnable mTaskBean = (Runnable) Proxy.newProxyInstance(task.getClass().getClassLoader(), new Class[]{Runnable.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Object result = null;
                    if ("run".equals(method.getName())) {
//                        LogUtils.debug("run before  type : " + task.type );
                        result = method.invoke(task, args);
//                        LogUtils.debug("run after type : " + task.type );
                        TaskBean nextTask = mDBCacheDataManager.getTaskOfType(task.type);
                        if(nextTask == null) {
                            mDBCacheDataManager.reMoveTypeList(task.type);
                            mThreadPoolManager.executeNextTask(new Runnable() {
                                @Override
                                public void run() {
                                    getNextTask();
                                }
                            });
                        }else{
                            mDBCacheDataManager.reMoveTask(nextTask);
                            mThreadPoolManager.executeNextTask(nextTask);
                        }
                    } else {
                        result = method.invoke(task, args);
                    }

                    return result;
                }
            });

        mThreadPoolManager.executeTask(mTaskBean);

    }

}
