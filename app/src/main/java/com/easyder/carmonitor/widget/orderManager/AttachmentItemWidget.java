package com.easyder.carmonitor.widget.orderManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.DownLoadImgListener;
import com.shinetech.mvp.network.UDP.bean.orderBean.AttachmentItemVo;
import com.shinetech.mvp.utils.FileUtils;

import java.io.File;

/**
 * Created by ljn on 2018-01-16.
 */

public class AttachmentItemWidget {

    private Context context;

    private View itemView;

    private ImageView fileIcon;

    private TextView fileNameView;

    private TextView uploadTimeView;

    private TextView downloadOrLook;

    private ProgressBar downloadProgress;

    private String orderNumber;

    private String orderName;

    private AttachmentItemVo mAttachmentItem;

    private File file;

    private DownLoadImgListener listener;

    public AttachmentItemWidget(Context context, String orderNumber, String orderName, AttachmentItemVo mAttachmentItem, DownLoadImgListener listener) {
        this.context = context;
        this.orderNumber = orderNumber;
        this.orderName = orderName;
        this.mAttachmentItem = mAttachmentItem;
        this.listener = listener;

        itemView = View.inflate(context, R.layout.attachment_widget_item_layout, null);

        initView();
    }

    private void initView(){

        fileIcon = (ImageView) itemView.findViewById(R.id.attachment_file_icon);
        fileNameView = (TextView) itemView.findViewById(R.id.attachment_filename);
        uploadTimeView = (TextView) itemView.findViewById(R.id.attachment_upload_time);
        downloadOrLook = (TextView) itemView.findViewById(R.id.attachment_download_or_look);

        downloadProgress = (ProgressBar) itemView.findViewById(R.id.download_progress);

        initFileIcon(mAttachmentItem.getFileName());
        fileNameView.setText(mAttachmentItem.getFileName());
        uploadTimeView.setText(mAttachmentItem.getUploadTime());

        downloadProgress.setVisibility(View.GONE);

        String fileName = mAttachmentItem.getFileName();
        file = new File("/sdcard/Carmonitor" + "/" + orderNumber + "/" + fileName);
        if(file.exists()){
            initDownLoadStatus(context.getString(R.string.look_info));
        }else{
            initDownLoadLayout();
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(file.exists()){

                    Intent intent = FileUtils.openFile(file.getAbsolutePath());
                    ((Activity)context).startActivity(intent);

                    /*Intent intent = new Intent(Intent.ACTION_VIEW);    //打开图片得启动ACTION_VIEW意图
                    Uri uri = Uri.fromFile(file);
                    intent.setDataAndType(uri, "image*//*");    //设置intent数据和图片格式
                    ((Activity)context).startActivity(intent);*/
                }
            }
        });
    }

    private void initDownLoadLayout(){
        downloadOrLook.setText(context.getString(R.string.download));
        downloadOrLook.setFocusable(true);
        downloadOrLook.setClickable(true);
        downloadOrLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.addTask(orderName, orderNumber, mAttachmentItem.getFileType(), mAttachmentItem.getFileName(), AttachmentItemWidget.this);

            }
        });
    }

    private void initDownLoadStatus(String status){
        downloadOrLook.setText(status);
        downloadOrLook.setOnClickListener(null);
        downloadOrLook.setFocusable(false);
        downloadOrLook.setClickable(false);
    }

    public void updateProgress(int total, int currentProgress){
        downloadProgress.setVisibility(View.VISIBLE);
        uploadTimeView.setVisibility(View.GONE);
        downloadProgress.setMax(total);
        downloadProgress.setProgress(currentProgress);

    }

    public void waitingDownLoad(){
        initDownLoadStatus(context.getString(R.string.download_wait));

    }

    public void downLoading(){
        initDownLoadStatus(context.getString(R.string.downloading));
        downloadProgress.setVisibility(View.VISIBLE);
        uploadTimeView.setVisibility(View.GONE);
        downloadProgress.setMax(100);
        downloadProgress.setProgress(0);


    }

    public void downLoadError(){
        initDownLoadLayout();
        downloadProgress.setVisibility(View.GONE);
        uploadTimeView.setVisibility(View.VISIBLE);
    }

    public void downLoadFinish(){
        initDownLoadStatus(context.getString(R.string.look_info));
        downloadProgress.setVisibility(View.GONE);
        uploadTimeView.setVisibility(View.VISIBLE);
    }



    private void initFileIcon(String fileName){

        String fileSuffix = FileUtils.getFileSuffix(fileName).toLowerCase();

        if(TextUtils.isEmpty(fileSuffix)){
            fileIcon.setImageResource(R.mipmap.default_file_img);
            return;
        }

        switch (fileSuffix){
            case "jpg":
                fileIcon.setImageResource(R.mipmap.file_type_jpg);
                break;
            case "doc":
            case "docx":
                fileIcon.setImageResource(R.mipmap.file_type_word);
                break;
            case "pdf":
                fileIcon.setImageResource(R.mipmap.file_type_pdf);
                break;
            case "xls":
            case "xlsx":
                fileIcon.setImageResource(R.mipmap.file_type_excel);
                break;
        }
    }

    public View getItemView(){
        return itemView;
    }
}
