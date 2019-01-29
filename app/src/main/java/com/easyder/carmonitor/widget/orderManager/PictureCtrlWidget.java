package com.easyder.carmonitor.widget.orderManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyder.carmonitor.CarMonitorApplication;
import com.easyder.carmonitor.R;
import com.easyder.carmonitor.activity.MainActivity;
import com.easyder.carmonitor.interfaces.AddPicturesListener;
import com.easyder.carmonitor.interfaces.GetPictureListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskManager;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.network.UDP.presenter.UpLoadImgPresenter;
import com.shinetech.mvp.utils.BitmapUtils;
import com.shinetech.mvp.utils.FileUtils;
import com.shinetech.mvp.utils.TimeUtil;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ljn on 2017-11-28.
 */

public class PictureCtrlWidget implements View.OnClickListener {

    private Context context;

    private View rootView;

    private String path = "/sdcard/Carmonitor";
    private String fileName;

    private TextView getpicture_to_shoot;

    private TextView getpicture_to_photo;

    private TextView cancle_to_getpicture;

    private GetPictureListener listener;

    private UpLoadImgPresenter presenter;

    private String orderName;

    private String orderNumber;

    private String fileType;

    private String fileNameDescribe;

    private List<String> currentFileList = new ArrayList<>();

    public PictureCtrlWidget(Context context, String orderName, String orderNumber, String fileNameDescribe, List<String> currentFileList, String fileType, UpLoadImgPresenter presenter, GetPictureListener listener) {
        this.context = context;
        this.listener = listener;
        this.presenter = presenter;
        this.orderName = orderName;
        this.orderNumber = orderNumber;
        this.fileType =  fileType;
        this.fileNameDescribe = fileNameDescribe;

        if(currentFileList != null && currentFileList.size()>0){
            this.currentFileList.addAll(currentFileList);
        }

        rootView = View.inflate(context, R.layout.getpicture_ctrl_layout, null);

        initView();
    }

    private void initView(){
        getpicture_to_shoot = (TextView) rootView.findViewById(R.id.getpicture_to_shoot);
        getpicture_to_shoot.setOnClickListener(this);
        getpicture_to_photo = (TextView) rootView.findViewById(R.id.getpicture_to_photo);
        getpicture_to_photo.setOnClickListener(this);
        cancle_to_getpicture = (TextView) rootView.findViewById(R.id.cancle_to_getpicture);
        cancle_to_getpicture.setOnClickListener(this);

        initOnResult();
    }

    public View getView(){
        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.getpicture_to_shoot:
                Intent shootintent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                shootintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                        .fromFile(new File(Environment
                                .getExternalStorageDirectory(),
                                "xiaoma.jpg")));
                ((Activity) context).startActivityForResult(shootintent, 2);
                break;
            case R.id.getpicture_to_photo:
                Intent photointent = new Intent(Intent.ACTION_PICK, null);
                photointent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                ((Activity) context).startActivityForResult(photointent, 1);
                break;
            case R.id.cancle_to_getpicture:
                canclePictureMenuView();
                break;
        }
    }

    public void initOnResult(){
        MainActivity.setAddPicturesListener(new AddPicturesListener() {
            @Override
            public boolean disposePictures(int requestCode, int resultCode, Intent data) {

                switch (requestCode) {
                    // 如果是直接从相册获取
                    case 1:
                        if(data!=null){
                            startPhotoZoom(data.getData());
                        }
                        return true;
                    // 如果是调用相机拍照时
                    case 2:
                        File temp = new File(Environment.getExternalStorageDirectory()
                                + "/xiaoma.jpg");
                        startPhotoZoom(Uri.fromFile(temp));
                        return true;
                    // 取得裁剪后的图片
                    case 3:
                        /**
                         * 非空判断大家一定要验证，如果不验证的话，
                         * 在剪裁之后如果发现不满意，要重新裁剪，丢弃
                         * 当前功能时，会报NullException，小马只
                         * 在这个地方加下，大家可以根据不同情况在合适的
                         * 地方做判断处理类似情况
                         *
                         */
                        if (data != null) {
//                            dataIntent = data;
                            setPicToView(data);
                        }
                        return true;


                }
                return false;
            }
        });
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 1024);
        intent.putExtra("outputY", 1024);
//        if(CarMonitorApplication.isUseSingleDialogMode()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                    .fromFile(new File(Environment
                            .getExternalStorageDirectory(),
                            "xiaoma.jpg")));
       /* }else{
            intent.putExtra("return-data", true);
        }*/
        intent.putExtra("outputFormat", "JPEG");//返回格式
        ((Activity)context).startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
           /* final Bitmap photo ;

            photo = extras.getParcelable("data");*/

           final String filePath = new File(Environment.getExternalStorageDirectory(), "xiaoma.jpg").getAbsolutePath();

            canclePictureMenuView();

            if (listener != null) {
                listener.OnLoading();
            }

            TaskManager.runDBTask(new TaskBean() {
                @Override
                public void run() {
                    //TODO upload IMG
                    //FileUtils.getFileName()
//                    String picturePath = "/sdcard/DCIM/0001.jpg";

                    //Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

                    final String fileName = getFileName(fileNameDescribe);
                    //byte[] bytes = BitmapUtils.Bitmap2Bytes(photo);
                    byte[] bytes = null;
                    /*if(!CarMonitorApplication.isUseSingleDialogMode()) {
                        if(photo != null) {
                            bytes = BitmapUtils.Bitmap2Bytes(photo);
                        }
                    }else{
                        bytes = FileUtils.getBytes(new File(Environment.getExternalStorageDirectory(), "xiaoma.jpg").getAbsolutePath());
                    }*/
                    bytes = FileUtils.getBytes(filePath);

                    if(bytes == null){
                        if (listener != null) {
                            listener.OnCancle();
                        }
                        return;
                    }

                    presenter.uploadImg(orderName, orderNumber, fileType, fileName, bytes, new UpLoadImgPresenter.UploadImgListener() {
                        @Override
                        public void OnSuccess() {

                            saveSd(filePath, fileName);
                            new File(Environment.getExternalStorageDirectory(), "xiaoma.jpg").delete();

                            if(listener != null){
                                listener.OnResult(path +"/"+ orderNumber+ "/" + fileName);
                            }

                        }

                        @Override
                        public void OnError(String errorInfo) {

                            canclePictureMenuView();
                            ToastUtil.showShort(errorInfo);
                        }
                    });

                }
            });


        }
    }

    private String getFileName(String describe){

        String fileNameContent;

        if(TextUtils.isEmpty(describe)){
            fileNameContent = fileType+"_";
        }else{

            //如果是车牌，去掉颜色，文件名不带空格
            String[] split = describe.split(" ");

            if(split.length >=2){
                describe = split[(split.length-1)];
            }

            fileNameContent = fileType+"_"+describe+"_";
        }

        String fileNameStr = fileNameContent+"1"+".jpg";

        File mFile = new File(path +"/"+ orderNumber+ "/" + fileNameStr);
        String absolutePath = mFile.getAbsolutePath();

        boolean iscontainsFileList = currentFileList.contains(absolutePath);

        int count = 2;
        while(mFile.exists() || iscontainsFileList){

            fileNameStr = fileNameContent+ count +".jpg";
            mFile = new File(path +"/"+ orderNumber+ "/" + fileNameStr);
            absolutePath = mFile.getAbsolutePath();
            iscontainsFileList = currentFileList.contains(absolutePath);
            count ++;
        }

        return fileNameStr;
    }

    public void saveSd(Bitmap bitmap, String fileName) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path+"/"+orderNumber);
        file.mkdirs();// 创建文件夹
        //new GetHeadIcon().startRun();
        fileName = path +"/"+ orderNumber+ "/" + fileName;//图片名字
        try {
            b = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void saveSd(String fromFile, String toFile) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        File file = new File(path+"/"+orderNumber);
        file.mkdirs();// 创建文件夹

        fileName = path +"/"+ orderNumber+ "/" + toFile;//图片名字
        InputStream fosfrom = null;
        OutputStream fosto = null;
        try {
            fosfrom = new FileInputStream(fromFile);
             fosto = new FileOutputStream(fileName);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                //关闭流
                if(fosfrom != null) {
                    fosfrom.close();
                }
                if(fosto != null){
                    fosto.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void canclePictureMenuView(){
        if(listener != null){
            listener.OnCancle();
        }
        MainActivity.setAddPicturesListener(null);
    }
}
