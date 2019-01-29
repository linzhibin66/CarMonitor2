package com.shinetech.mvp.network.UDP.presenter;

import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.basePresenter.MvpBasePresenter;
import com.shinetech.mvp.network.UDP.UDPRequestCtrl;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.OrderCtrlResultVo;
import com.shinetech.mvp.network.UDP.bean.orderBean.OrderCtrlVo;
import com.shinetech.mvp.network.UDP.listener.ResponseListener;
import com.shinetech.mvp.utils.ByteUtil;
import com.shinetech.mvp.view.MvpView;

import java.util.List;

/**
 * Created by Lenovo on 2017-12-21.
 */

public class UpLoadImgPresenter extends MvpBasePresenter<BaseVo, MvpView<BaseVo>> {

    protected List<String> imgList;

    private int imgVoPosition;

    public static byte CREATMAINTENANCEORDERTYPE = 1;

    public static byte MAINTENANCERESULTTYPE = 2;

    public static byte EVALUATETYPE = 3;

    /**
     * 上传单张图片 是否结束
     */
    private boolean isUploadImgEnd = false;

    private boolean isCancleUpload = false;

    private boolean isResult = false;

    public void uploadImg(String orderName, String orderNumber, String fileType, String fileName, byte[] fileData, final UploadImgListener mUploadImgListener){

        OrderCtrlVo orderCtrlVo = new OrderCtrlVo(OrderCtrlResultVo.UPLOAD_ACCESSORY,
                orderName, orderNumber, fileType, fileName, fileData);

        orderCtrlVo.setCanSubPackageSend(true);

        UDPRequestCtrl.getInstance().request(orderCtrlVo, new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {
                if(mUploadImgListener != null) {

                    if (isViewAttached()) {
                        getView().onStopLoading();
                    }

                    BaseVo dataVo = successResult.getDataVo();

                    if(dataVo instanceof OrderCtrlVo){
                        OrderCtrlVo mOrderCtrlVo = (OrderCtrlVo) dataVo;

                        if (mOrderCtrlVo.getOrderCtrlResult() == 1) {
                            mUploadImgListener.OnSuccess();
                        } else {
                            mUploadImgListener.OnError(mOrderCtrlVo.getReason());
                        }
                    }

                }else{
                    UpLoadImgPresenter.this.onSuccess(successResult);
                }

            }

            @Override
            public void onError(LoadResult errorResult) {

                if(mUploadImgListener != null) {

                    if (isViewAttached()) {
                        getView().onStopLoading();
                    }

                    mUploadImgListener.OnError(errorResult.getMessage());

                }else{
                    UpLoadImgPresenter.this.onError(errorResult);
                }
            }
        });

    }

   /* protected void uploadImg(final byte imgType, final String orderNumber){

        TaskBean taskBean = new TaskBean() {
            @Override
            public void run() {

                for(int i = 1; i <= imgList.size(); i++) {
                    String path = imgList.get(i);
                    Bitmap bitmap = BitmapFactory.decodeFile(path);

                    final List<UpLoadIMGVo> upLoadIMGVos = creatPackageList((byte) i, imgType, orderNumber, bitmap);

                    imgVoPosition = 0;
                    isUploadImgEnd = false;
                    isCancleUpload = false;

                    //循环，发送图片内容包
                    while(imgVoPosition < upLoadIMGVos.size()){
                        isResult = false;
                        UpLoadIMGVo upLoadIMGVo = upLoadIMGVos.get(imgVoPosition);
                        UDPRequestCtrl.getInstance().request(upLoadIMGVo, new ResponseListener() {
                            @Override
                            public void onSuccess(LoadResult successResult) {

                                if (imgVoPosition == (upLoadIMGVos.size() - 1)) {
                                    //TODO next img upload
                                    isUploadImgEnd = true;

                                    BaseVo dataVo = successResult.getDataVo();
                                    if(dataVo instanceof UpLoadIMGVo){
                                        UpLoadIMGVo mUpLoadIMGVo = (UpLoadIMGVo) dataVo;
                                        if(mUpLoadIMGVo.getResult() == 0){
                                            isCancleUpload = true;
                                        }
                                    }

                                } else {
                                    // TODO  upload next position package
                                    imgVoPosition++;
                                }

                                isResult = true;

                            }

                            @Override
                            public void onError(LoadResult errorResult) {
                                //TODO cancle upload img
                                isCancleUpload = true;
                                isResult = true;
                            }
                        });

                        while (!isResult) {}

                        //当有数据包发送错误，推出图片上传。
                        if(isCancleUpload){
                            upLoadIMGVo.setResult((byte)0);
                            onError(LoadResult.STATUS_ERROR.setDataVo(upLoadIMGVo).setMessage("上传失败"));
                            return ;
                        }
                    }


                    if((imgList.size()-1) == i){
                        UpLoadIMGVo upLoadIMGVo = new UpLoadIMGVo(orderNumber, imgType, (byte) i, (short) 0, (short) 0, new byte[0]);
                        upLoadIMGVo.setResult((byte)1);
                        onSuccess(LoadResult.STATUS_SUCCESS.setDataVo(upLoadIMGVo));
                    }

                }

            }
        };

        taskBean.setType("uploadImg");

        TaskManager.runDBTask(taskBean);
    }

    private List<UpLoadIMGVo> creatPackageList(byte imgId, byte imgType, String orderNumber, Bitmap bitmap){

        byte[] bytes = BitmapUtils.Bitmap2Bytes(bitmap);

        int imgBytesLength = bytes.length;

        int packageOtherContentLength = getPackageOtherContentLength(orderNumber);

        int imgpackageDataLenth = 1400 - packageOtherContentLength - 100;

        int packageCount = imgBytesLength % imgpackageDataLenth > 0 ? (imgBytesLength / imgpackageDataLenth) + 1 : (imgBytesLength / imgpackageDataLenth);

        List<UpLoadIMGVo> imgPackageList = new ArrayList<>();

        for(int i = 1; i <= packageCount; i++){

            int surplusDataLength = imgBytesLength - (i*imgpackageDataLenth);

            byte[] data;

            if(surplusDataLength > imgpackageDataLenth) {
                data = new byte[imgpackageDataLenth];
            }else{
                data = new byte[surplusDataLength];
            }

            System.arraycopy(bytes, i*imgpackageDataLenth , data, 0, data.length);

            imgPackageList.add(new UpLoadIMGVo(orderNumber,imgType, imgId, (short)packageCount, (short)i,data));
        }
        return imgPackageList;

    }*/



    private int getPackageOtherContentLength(String orderNumber){
        int packageOtherContentLength = 0;
        //帧头，帧号长度
        packageOtherContentLength = packageOtherContentLength + 3;

        //用户名
        byte[] userNamebytes = ByteUtil.stringToBytes(UserInfo.getInstance().getUserName());

        packageOtherContentLength = packageOtherContentLength+ userNamebytes.length;

        //数据包长度 、协议编号 数据长度
        packageOtherContentLength = packageOtherContentLength + 4;

        //计算订单号长度
        byte[] orderNumberbytes = ByteUtil.stringToBytes(orderNumber);

        packageOtherContentLength = packageOtherContentLength+ orderNumberbytes.length;

        //加上包内容中其他数据长度
        packageOtherContentLength = packageOtherContentLength + 6;

        return  packageOtherContentLength;
    }

    public void deleteImg(String orderName, String orderNumber, String fileType, String fileName, final UploadImgListener mUploadImgListener){

        if (isViewAttached()) {
            getView().onLoading();
        }

        OrderCtrlVo orderCtrlVo = new OrderCtrlVo(OrderCtrlResultVo.DELETE_ACCESSORY, orderName, orderNumber, fileType, fileName, new byte[0]);

        UDPRequestCtrl.getInstance().request(orderCtrlVo, new ResponseListener() {
            @Override
            public void onSuccess(LoadResult successResult) {
                if(mUploadImgListener != null) {

                    if (isViewAttached()) {
                        getView().onStopLoading();
                    }

                    BaseVo dataVo = successResult.getDataVo();

                    if(dataVo instanceof OrderCtrlVo){
                        OrderCtrlVo mOrderCtrlVo = (OrderCtrlVo) dataVo;

                        if (mOrderCtrlVo.getOrderCtrlResult() == 1) {
                            mUploadImgListener.OnSuccess();
                        } else {
                            mUploadImgListener.OnError(mOrderCtrlVo.getReason());
                        }
                    }

                }else{
                    UpLoadImgPresenter.this.onSuccess(successResult);
                }

            }

            @Override
            public void onError(LoadResult errorResult) {

                if(mUploadImgListener != null) {

                    if (isViewAttached()) {
                        getView().onStopLoading();
                    }

                    mUploadImgListener.OnError(errorResult.getMessage());

                }else{
                    UpLoadImgPresenter.this.onError(errorResult);
                }
            }
        });

    }

    public interface UploadImgListener{

        void OnSuccess();
        void OnError(String errorInfo);

    }


}
