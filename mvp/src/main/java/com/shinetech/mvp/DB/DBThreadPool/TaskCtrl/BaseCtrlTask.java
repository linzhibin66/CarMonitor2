package com.shinetech.mvp.DB.DBThreadPool.TaskCtrl;

import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;

/**
 * Created by ljn on 2016/12/30.
 */
public abstract class BaseCtrlTask {




    /**
     * 运行任务
     * @param task
     */
    public void runTask(TaskBean task){
        boolean toExecute = checkTask(task, true);

        if(toExecute){
            executeTask(task);
        }
    }

    /**
     * 检查任务状态
     * @param task 要校验的任务
     * @param isAddList 是否添加到任务队列中
     * @return ture 可执行的任务，false 根据isAddList是否添加到队列中
     */
    protected abstract boolean checkTask(TaskBean task,boolean isAddList);

    /**
     * 获取下一个执行任务
     */
    protected void getNextTask(){

    }

    /**
     * 添加到线程池中执行
     * add ThreadTool to execute
     * @param task 任务
     */
    protected abstract void executeTask(final TaskBean task);
}
