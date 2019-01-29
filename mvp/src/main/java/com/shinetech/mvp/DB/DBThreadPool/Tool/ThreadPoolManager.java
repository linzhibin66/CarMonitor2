package com.shinetech.mvp.DB.DBThreadPool.Tool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ljn on 2016/12/30.
 */
public class ThreadPoolManager {

    /**
     * 固定数量的线程池（执行任务线程）
     */
    private ExecutorService executorService;

    /**
     * 单线程池（用于获取下一个任务处理）
     */
    private ExecutorService singleThreadExecutor;

    /**
     * 线程池大小
     */
    public final int THREADPOOL_SIZE = 5;

    public ThreadPoolManager() {
        initThreadPool();
    }

    /**
     * 初始化线程池
     */
    private void initThreadPool(){
        executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
        singleThreadExecutor = Executors.newSingleThreadExecutor();

    }

    /**
     * 执行任务
     * @param task 要执行的任务
     */
    public void executeTask(Runnable task){
        if(executorService!=null){
            executorService.execute(task);
        }
    }

    /**
     * 执行下一个任务
     */
    public void executeNextTask(Runnable task){
        singleThreadExecutor.execute(task);
    }

}
