package com.easyder.carmonitor.presenter;

import android.os.Handler;
import android.text.TextUtils;

import com.easyder.carmonitor.adapter.AlarmLogAdapter;
import com.easyder.carmonitor.interfaces.AlarmLogListListener;
import com.easyder.carmonitor.interfaces.ViolationLogTypeChengedListener;
import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskManager;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.network.UDP.presenter.AlarmLogPresenter;
import com.shinetech.mvp.network.UDP.resopnsebean.ResponseViolationLogBean;
import com.shinetech.mvp.network.UDP.resopnsebean.ViolationLogItem;
import com.shinetech.mvp.sort.ViolationOrdinalComparator;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ljn on 2017-08-02.
 */
public class AlarmLogActivityPresenter extends AlarmLogPresenter{

    /**
     * 警报消息
     */
    private List<ViolationLogItem> mViolationLogItems = new ArrayList<>();

    private List<ViolationLogItem> showViolationLogList = new ArrayList<>();

    /**
     * 警报类型列表
     */
    private List<String> mViolationTypeList = new ArrayList<>();

    /**
     * 选择的类型
     */
    private List<String> mSelectViolationList = new ArrayList<>();

    public void getViolationLogList(String platenumber, String startTime, String endTime, final AlarmLogListListener mAlarmLogListListener){
        getViolationLogResponse(platenumber, startTime, endTime, new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {

                BaseVo dataVo = successResult.getDataVo();
                if(dataVo !=null){
                    ResponseViolationLogBean mResponseViolationLogBean = (ResponseViolationLogBean) dataVo;

                    List<ViolationLogItem> violationLog = mResponseViolationLogBean.getViolationLog();

                    if (violationLog == null || violationLog.size() == 0) {
                        //TODO no data
//                        ToastUtil.showShort("No Alarm Data");
                        if(mAlarmLogListListener != null) {
                            mAlarmLogListListener.onSuccess(violationLog);
                        }
                        return;
                    }

                    //排序
                    ViolationOrdinalComparator mViolationOrdinalComparator = new ViolationOrdinalComparator();
                    Collections.sort(violationLog, mViolationOrdinalComparator);

                    //清除查询结果List
                    mViolationLogItems.clear();

                    //清除要显示的日志List
                    showViolationLogList.clear();

                    //清除日志类型List
                    mViolationTypeList.clear();

                    //清除选择了的类型List
                    mSelectViolationList.clear();

                    addTimeTitle(violationLog);

                    //赋值要显示的日志列表
                    showViolationLogList.addAll(mViolationLogItems);

                    //将所有类型设置为已选择
                    mSelectViolationList.addAll(mViolationTypeList);

                    if(mAlarmLogListListener != null) {
                        mAlarmLogListListener.onSuccess(showViolationLogList);
                    }

                }else{
                    LogUtils.error("getCarHistoricalResponse dataVo is null  ");
                    if(mAlarmLogListListener != null) {
                        mAlarmLogListListener.onError("data is null");
                    }
                }

//                mAlarmLogListListener.onSuccess();

            }

            @Override
            public void onError(LoadResult errorResult) {
                if(mAlarmLogListListener != null) {
                    mAlarmLogListListener.onError(errorResult.getMessage());
                }
            }
        });
    }


    private void addTimeTitle(List<ViolationLogItem> mViolationLogList){
        int index = 0;

        String tiemTitleStr = null;

        do {

            ViolationLogItem violationLogItem = mViolationLogList.get(index);

            //警报类型列表
            String violationName = violationLogItem.getViolationName();
            if(!mViolationTypeList.contains(violationName)){
                mViolationTypeList.add(violationName);
            }

            String violationTime = violationLogItem.getViolationTime();
            String dateTime = getDateTime(violationTime);
            if(TextUtils.isEmpty(tiemTitleStr) || !tiemTitleStr.equals(dateTime)){
                //添加时间标题
                tiemTitleStr = dateTime;
                ViolationLogItem mViolationLogItem = new ViolationLogItem();
                mViolationLogItem.setViolationTime(tiemTitleStr);
                mViolationLogItems.add(mViolationLogItem);

            }

            mViolationLogItems.add(violationLogItem);

            index++;

        }while (mViolationLogList.size()>index);

    }

    private String getDateTime(String violationTime){
        String[] split = violationTime.split(" ");

        String timeTitle =split[0];

        return timeTitle;
    }

    public void updateShowViolationList(final ViolationLogTypeChengedListener chengedListener){

        if(chengedListener != null) {
            chengedListener.onStart();
        }

        TaskManager.runDBTask(new TaskBean() {
            @Override
            public void run() {

                List<ViolationLogItem> tempViolationLogList = new ArrayList<>();
                tempViolationLogList.addAll(mViolationLogItems);

                for(int i = 0; i < tempViolationLogList.size(); i++){

                    ViolationLogItem violationLogItem = tempViolationLogList.get(i);
                    String violationName = violationLogItem.getViolationName();
                    if(violationName!= null){
                        if(!mSelectViolationList.contains(violationName)){
                            tempViolationLogList.remove(i);
                            i--;
                        }

                        if(tempViolationLogList.size()>(i + 1)) {
                            ViolationLogItem previousItem = tempViolationLogList.get(i);

                            ViolationLogItem nextItem = tempViolationLogList.get(i + 1);

                            String previousItemviolationName = previousItem.getViolationName();
                            String nextItemViolationName = nextItem.getViolationName();

                            if (TextUtils.isEmpty(previousItemviolationName) && TextUtils.isEmpty(nextItemViolationName)) {
                                tempViolationLogList.remove(i);
                                i--;
                            }
                        }else{
                            ViolationLogItem previousItem = tempViolationLogList.get(i);
                            String previousItemviolationName = previousItem.getViolationName();
                            if (TextUtils.isEmpty(previousItemviolationName)) {
                                tempViolationLogList.remove(i);
                                i--;
                            }
                        }
                    }
                }

                showViolationLogList.clear();

                showViolationLogList.addAll(tempViolationLogList);

                //回掉，刷新界面
                if(chengedListener != null) {
                    chengedListener.onFinish(showViolationLogList);
                }
            }
        });
    }

    public boolean hasSelectType(){
        if(mSelectViolationList.size()==0){
            return false;
        }
        return true;
    }

    public boolean isSelectAll(){
        if(mSelectViolationList.size()!= 0 && mSelectViolationList.size() == mViolationTypeList.size()){
            return true;
        }
        return false;
    }

    public void selectAll(){
        mSelectViolationList.clear();
        mSelectViolationList.addAll(mViolationTypeList);
    }

    public void cancelAllSelect(){
        mSelectViolationList.clear();
    }

    public int getSelectViolationSize(){
        return mSelectViolationList.size();
    }

    public void addOrRemoveSelectType(String violationType){
        if(mSelectViolationList.contains(violationType)){
            mSelectViolationList.remove(violationType);
        }else{
            mSelectViolationList.add(violationType);
        }
    }

    public List<String> getSelectViolationList(){
        return mSelectViolationList;
    }

    public List<String> getViolationTypeList(){

        return mViolationTypeList;
    }

}
