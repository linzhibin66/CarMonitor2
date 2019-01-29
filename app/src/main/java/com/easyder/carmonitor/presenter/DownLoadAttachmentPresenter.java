package com.easyder.carmonitor.presenter;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.Utils.JsonUtil;
import com.easyder.carmonitor.bean.DownLoadImgTask;
import com.easyder.carmonitor.interfaces.DownLoadImgListener;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.OrderCtrlVo;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.utils.FileUtils;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.ToastUtil;

import java.io.File;
import java.util.List;

/**
 * Created by Lenovo on 2018-02-06.
 */

public class DownLoadAttachmentPresenter extends MvpBasePresenter {


    private String path = "/sdcard/Carmonitor" ;

    private DownLoadImgListener listener;

    private DownLoadImgTask downLoadImgTask;

    private boolean isDebug = false && LogUtils.isDebug;

    public DownLoadAttachmentPresenter(DownLoadImgTask downLoadImgTask, DownLoadImgListener listener) {
        this.listener = listener;
        this.downLoadImgTask = downLoadImgTask;
    }

    /**
     * 下载附件
     * @param orderName  工单名称
     * @param orderNumber  工单号
     * @param fileType  文件类型
     * @param fileName  文件名 （包含文件后缀）
     */
    public void downloadAttachment(String orderName, String orderNumber, String fileType, String fileName){

        OrderCtrlVo orderCtrlVo = new OrderCtrlVo((byte) 3, orderName, orderNumber, fileType, fileName, new byte[0]);
        orderCtrlVo.setSubPackageReveive(true);
        loadData(orderCtrlVo);

    }

    @Override
    public void onSuccess(LoadResult successResult) {

        BaseVo dataVo = successResult.getDataVo();
        if(dataVo instanceof OrderCtrlVo){
            OrderCtrlVo mOrderCtrlVo = (OrderCtrlVo) dataVo;
            byte[] fileData = mOrderCtrlVo.getFileData();
            if(fileData.length>0) {

                FileUtils.createFileWithByte(fileData, "/sdcard/Carmonitor" + "/" + mOrderCtrlVo.getOrderNumber()+"/"+ mOrderCtrlVo.getFileName());

                //保存到对应的设备信息中
                if(mOrderCtrlVo.getFileType().equals(MainApplication.getInstance().getString(R.string.install_img))) {
                    String fileName = mOrderCtrlVo.getFileName();
                    String[] split = fileName.split("_");
                    if(split.length == 3) {
                        List<InstallTerminalnfo> installTerminalnfos = DBManager.querySelectInstallTerminalnfo(mOrderCtrlVo.getOrderNumber(), split[1]);
                        if(installTerminalnfos.size() == 1){
                            //获取对应的设备信息
                            InstallTerminalnfo installTerminalnfo = installTerminalnfos.get(0);

                            //添加到附件目录中
                            String pathjsonStr = installTerminalnfo.getPathjsonStr();

                            List<String> filePathList = JsonUtil.JSONArrayToList(pathjsonStr);

                            String downloadImgPath = "/sdcard/Carmonitor" + "/" + mOrderCtrlVo.getOrderNumber()+"/"+ mOrderCtrlVo.getFileName();

                            if(!filePathList.contains(downloadImgPath)) {

                                filePathList.add(downloadImgPath);

                                String filePathListStr = JsonUtil.stringListToJsonString(filePathList);

                                installTerminalnfo.setPathjsonStr(filePathListStr);

                                DBManager.updateInstallTerminalnfo(installTerminalnfo);
                            }

                        }
                    }
                }
                ToastUtil.showShort("下载成功");

            }
            if(listener != null) {
                listener.onSuccess(downLoadImgTask);
            }
            return;

        }
        if(isDebug)System.out.println(" unknow request - - - - - - - - - - - - -");
        super.onSuccess(successResult);


    }

    @Override
    public void onError(LoadResult errorResult) {
        BaseVo dataVo = errorResult.getDataVo();
        if(dataVo instanceof OrderCtrlVo) {
            if (listener != null) {
                listener.onError(downLoadImgTask);
            }
            ToastUtil.showShort("下载失败, "+errorResult.getMessage());
            return;
        }

        super.onError(errorResult);
        ToastUtil.showShort(errorResult.getMessage());
    }

    @Override
    public void onProgress(int total, int currentProgress, short protocol) {
        super.onProgress(total, currentProgress, protocol);
        if (listener != null) {
            listener.onProgress(downLoadImgTask, total, currentProgress, protocol);
        }
    }
}
