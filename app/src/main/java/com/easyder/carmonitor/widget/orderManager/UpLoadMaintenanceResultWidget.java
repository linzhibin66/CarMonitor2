package com.easyder.carmonitor.widget.orderManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.AddPicturesImgGridAdapter;
import com.easyder.carmonitor.broadcast.SDCardListeren;
import com.easyder.carmonitor.interfaces.EnterDialogListener;
import com.easyder.carmonitor.interfaces.GetPictureListener;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.interfaces.UpLoadMaintenanceListener;
import com.easyder.carmonitor.interfaces.UpLoadResultDialogListener;
import com.easyder.carmonitor.presenter.UpLoadMaintenanceResultPresenter;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.User.UserInfo;
import com.shinetech.mvp.network.UDP.bean.BaseVo;
import com.shinetech.mvp.network.UDP.bean.item.LoadResult;
import com.shinetech.mvp.network.UDP.bean.orderBean.MaintenanceFinishVo;
import com.shinetech.mvp.network.UDP.presenter.UpLoadImgPresenter;
import com.shinetech.mvp.utils.FileUtils;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;
import com.shinetech.mvp.view.MvpView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ljn on 2017-11-28.
 */

public class UpLoadMaintenanceResultWidget <M extends BaseVo> implements MvpView, View.OnClickListener {

    private Context context;

    private View rootView;

    private LayoutBackListener listener;

    private int hintStatus;

    /**
     * 故障分析
     */
    private EditText analyze_value;

    /**
     * 维修措施
     */
    private EditText measures_value;

    /**
     * 维修结果
     */
    private EditText result_value;

    /**
     * 完成时间
     */
    private TextView termain_status_value;

    /**
     * 附件
     */
    private MyGridView enclosure_imglist;

    /**
     * 超出内容的模糊阴影布局
     */
    private RelativeLayout upload_maintenance_result_layout_outmost;

    /**
     * 确认提示，图片获取操作界面
     */
    private RelativeLayout upload_maintenance_result_enter_layout;

    /**
     * 附件图片适配器
     */
    private AddPicturesImgGridAdapter addPicturesImgGridAdapter;

    /**
     * 图片路径集合
     */
    private List<String> imgPath = new ArrayList<>();

    /**
     * 附件本地存储路径
     */
    private String path = "/sdcard/Carmonitor/";

    private String fileName;

    private SDCardListeren mSDCardListeren;

    private UpLoadMaintenanceListener uploadlistener;

    private CommitMaintenanceResultDialogWidget mCommitMaintenanceResultDialogWidget;

    private BaseOrderInfoDB baseOrderInfoDB;

    /**
     * 故障分析
     */
    private String analyzeValue;

    /**
     * 维修措施
     */
    private String measuresValue;

    /**
     * 维修结果
     */
//    private String resultValue;

    /**
     * 完成时间
     */
    private String termainStatus;

    private UpLoadMaintenanceResultPresenter presenter;

    private EnterDialogListener enterDialogListener;
    private List<String> attachmentPathList;

    public UpLoadMaintenanceResultWidget(Context context, BaseOrderInfoDB baseOrderInfoDB, List<String> attachmentPathList, View rootView, LayoutBackListener listener) {
        this.context = context;
        this.rootView = rootView;
        this.listener = listener;
        this.baseOrderInfoDB = baseOrderInfoDB;
        this.attachmentPathList = attachmentPathList;

        initTitle(rootView);

        initLayout();

        creatPresenter();
    }

    private void initTitle(View view){
        ImageButton title_back = (ImageButton) view.findViewById(R.id.title_back);
        TextView title_text = (TextView) view.findViewById(R.id.title_text);
        TextView title_search = (TextView) view.findViewById(R.id.title_search);

        String title = context.getString(R.string.upload_maintenance_result_title);
        title_text.setText(title);

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mSDCardListeren != null){
                    mSDCardListeren.stopWatching();
                }

                if (listener != null) {
                    listener.onBack();

                }
            }
        });

        title_search.setVisibility(View.VISIBLE);

        title_search.setText(context.getString(R.string.commit));

        title_search.setOnClickListener(this);

        RelativeLayout title_layout = (RelativeLayout) view.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);
    }

    private void initLayout(){

        analyze_value = (EditText) rootView.findViewById(R.id.upload_maintenance_result_analyze_value);
        measures_value = (EditText) rootView.findViewById(R.id.upload_maintenance_result_measures_value);
        result_value = (EditText) rootView.findViewById(R.id.upload_maintenance_result_value);

        //完成时间upload_maintenance_result_termain_status_title
        termain_status_value = (TextView) rootView.findViewById(R.id.upload_maintenance_result_termain_status_value);

        termain_status_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentColor = termain_status_value.getText().toString();

                String[] stringArray = context.getResources().getStringArray(R.array.platenumber_color);

                int index = 0;
                for(int i = 0; i<stringArray.length; i++){
                    if(stringArray[i].startsWith(currentColor + "")){
                        index = i;
                        break;
                    }
                }

                if(uploadlistener != null){
                    uploadlistener.showSelectTermainStatus(index);
                }

            }
        });

        //附件中的图片布局
        enclosure_imglist = (MyGridView) rootView.findViewById(R.id.upload_maintenance_result_enclosure_imglist);

        //确认提示，图片获取操作界面
        upload_maintenance_result_enter_layout = (RelativeLayout) rootView.findViewById(R.id.upload_maintenance_result_enter_layout);

        //超出内容的模糊阴影布局
        upload_maintenance_result_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.upload_maintenance_result_layout_outmost);

        initAddPicyures();
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(upload_maintenance_result_layout_outmost != null) {
            upload_maintenance_result_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    public void setUpLoadListener(UpLoadMaintenanceListener uploadlistener){
        this.uploadlistener = uploadlistener;
    }

    public void UpDataTermainStatus(String status){
        if(termain_status_value != null) {
            termain_status_value.setText(status);
        }
    }

    private void creatPresenter(){
        presenter = new UpLoadMaintenanceResultPresenter();
        presenter.attachView(this);
    }


    @Override
    public void onClick(View v) {
        commit();

    }

    private void commit(){

        analyzeValue = analyze_value.getText().toString().trim();
        if(TextUtils.isEmpty(analyzeValue)){
            ToastUtil.showShort(context.getString(R.string.upload_maintenance_result_analyze)+context.getString(R.string.content_no_null));
            return ;
        }


        measuresValue = measures_value.getText().toString().trim();
        if(TextUtils.isEmpty(measuresValue)){
            ToastUtil.showShort(context.getString(R.string.upload_maintenance_result_measures)+context.getString(R.string.content_no_null));
            return ;
        }

        /*resultValue = result_value.getText().toString().trim();
        if(TextUtils.isEmpty(resultValue)){
            ToastUtil.showShort(context.getString(R.string.upload_maintenance_result)+context.getString(R.string.content_no_null));
            return ;
        }*/

        termainStatus = termain_status_value.getText().toString().trim();
        if(TextUtils.isEmpty(termainStatus) || termainStatus.equals(context.getString(R.string.maintenance_order_hint_platenumber_color))){
            ToastUtil.showShort(context.getString(R.string.upload_maintenance_result_termain_status)+context.getString(R.string.content_no_null));
            return ;
        }

        if(mCommitMaintenanceResultDialogWidget == null) {
            mCommitMaintenanceResultDialogWidget = new CommitMaintenanceResultDialogWidget(context);
        }

        ArrayList<String> imgPathList = new ArrayList<>();
        if(imgPath.size()>1) {
            imgPathList.addAll(imgPath);
            imgPathList.remove("photo");
        }
        int status = termainStatus.equals(context.getString(R.string.normal_str))?1:0;

        presenter.uploadMaintenanceResult(baseOrderInfoDB.getOrderName(), baseOrderInfoDB.getOrderNumber(), analyzeValue, measuresValue, status, UserInfo.getInstance().getUserName());

       /* onLoading();

        if(count/2 == 0){
            showCommitFail("服务器无响应");
        }else{
            showCommitSuccess();
        }
        count++;*/

    }

//    private int count = 0;

    /**
     * 初始化添加图片界面
     */
    private void initAddPicyures(){

        if(attachmentPathList.size()>0){
            imgPath.addAll(attachmentPathList);
        }

        imgPath.add("photo");

        addPicturesImgGridAdapter = new AddPicturesImgGridAdapter(context);

        addPicturesImgGridAdapter.initData(imgPath);

        enclosure_imglist.setAdapter(addPicturesImgGridAdapter);

        enclosure_imglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if((addPicturesImgGridAdapter.getCount()-1) == position){
                    //TODO show select
                    showPictureMenuView();
                }else{
                    //TODO show picture
                    File file = new File(imgPath.get(position));
                    if(file.exists()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);    //打开图片得启动ACTION_VIEW意图
                        Uri uri = Uri.fromFile(file);
                        intent.setDataAndType(uri, "image/*");    //设置intent数据和图片格式
                        ((Activity) context).startActivity(intent);
                    }else{
                        ToastUtil.showShort(context.getString(R.string.undownload_image));
                    }

                }
            }
        });

        enclosure_imglist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if((addPicturesImgGridAdapter.getCount()-1) == position){
                    //TODO show select
                    showPictureMenuView();
                }else{
                    //TODO show picture
                    String filePath = (String) addPicturesImgGridAdapter.getItem(position);

                    deleteImg(filePath);

                   /* File file = new File(filePath);
                    if(file.exists()){
                        file.delete();
                    }*/


                }
                return true;
            }
        });

//        initgetPicturesMenu();

        mSDCardListeren = new SDCardListeren(path, imgPath, addPicturesImgGridAdapter);
        mSDCardListeren.startWatching();
    }

    /**
     * 删除图片
     * @param filePath
     */
    private void deleteImg(final String filePath){

        if(upload_maintenance_result_enter_layout != null) {


            if(enterDialogListener == null) {
                enterDialogListener = new EnterDialogListener() {
                    @Override
                    public void OnCancel() {
                        dismissEnterLayout();
                    }

                    @Override
                    public void OnEnter() {

                        String fileName = FileUtils.getFileName(filePath);

                        presenter.deleteImg(baseOrderInfoDB.getOrderName(), baseOrderInfoDB.getOrderNumber(), context.getString(R.string.install_img), fileName, new UpLoadImgPresenter.UploadImgListener() {
                            @Override
                            public void OnSuccess() {
                                File file = new File(filePath);
                                if (file.exists()) {
                                    file.delete();
                                }

                                imgPath.remove(filePath);
                                addPicturesImgGridAdapter.initData(imgPath);
                            }

                            @Override
                            public void OnError(String errorInfo) {
                                ToastUtil.showShort(errorInfo);
                            }
                        });
                    }
                };
            }

            EnterDialogWidget enterDialogWidget = new EnterDialogWidget(context, enterDialogListener);

            enterDialogWidget.setTitle(context.getString(R.string.enter_to_delete));
            enterDialogWidget.setHintVisibility(View.GONE);

            enterDialogWidget.showEnterDialog();

            upload_maintenance_result_enter_layout.removeAllViews();

            RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            upload_maintenance_result_enter_layout.addView(enterDialogWidget.getView(),params);
        }
    }

    private void dismissEnterLayout(){
        upload_maintenance_result_enter_layout.removeAllViews();
        upload_maintenance_result_enter_layout.setVisibility(View.GONE);
        if(uploadlistener != null){
            uploadlistener.updateDialogFocusable(true);
        }
    }

    private void showPictureMenuView(){

        PictureCtrlWidget mPictureCtrlWidget = new PictureCtrlWidget(context, baseOrderInfoDB.getOrderName(), baseOrderInfoDB.getOrderNumber(), null, imgPath, context.getString(R.string.create_maintenance_result_img), presenter, new GetPictureListener() {
            @Override
            public void OnResult(String filePath) {

                onStopLoading();

                imgPath.add(0,filePath);
                addPicturesImgGridAdapter.initData(imgPath);
                if(uploadlistener != null){
                    uploadlistener.updateDialogFocusable(true);
                }
            }

            @Override
            public void OnCancle() {
                upload_maintenance_result_enter_layout.removeAllViews();
                upload_maintenance_result_enter_layout.setVisibility(View.GONE);
                if(uploadlistener != null){
                    uploadlistener.updateDialogFocusable(true);
                }
            }

            @Override
            public void OnLoading() {
                onLoading();
            }
        });

        RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        upload_maintenance_result_enter_layout.removeAllViews();

        upload_maintenance_result_enter_layout.addView(mPictureCtrlWidget.getView(),params);

        upload_maintenance_result_enter_layout.setVisibility(View.VISIBLE);

        if(uploadlistener != null){
            uploadlistener.updateDialogFocusable(false);
        }
    }


    @Override
    public void onLoading() {

        if(mCommitMaintenanceResultDialogWidget == null) {
            mCommitMaintenanceResultDialogWidget = new CommitMaintenanceResultDialogWidget(context);
        }

        mCommitMaintenanceResultDialogWidget.showLoadingView();
        RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        upload_maintenance_result_enter_layout.removeAllViews();
        upload_maintenance_result_enter_layout.addView(mCommitMaintenanceResultDialogWidget.getView(),params);
        upload_maintenance_result_enter_layout.setVisibility(View.VISIBLE);
        if(uploadlistener != null){
            uploadlistener.updateDialogFocusable(false);
        }
    }

    @Override
    public void showContentView(BaseVo dataVo) {

        if(dataVo instanceof MaintenanceFinishVo){
            MaintenanceFinishVo mMaintenanceFinishVo = (MaintenanceFinishVo) dataVo;

            if(mMaintenanceFinishVo.getResult() == 1) {
                showCommitSuccess();
            }else{
                showCommitFail(mMaintenanceFinishVo.getResultReason());
            }
            return;

        }

    }

    @Override
    public void onStopLoading() {
        upload_maintenance_result_enter_layout.removeAllViews();
        upload_maintenance_result_enter_layout.setVisibility(View.GONE);
    }

    private void showCommitFail(String errorMessage){

        if(mCommitMaintenanceResultDialogWidget == null){
            return;
        }

        mCommitMaintenanceResultDialogWidget.showFailView(errorMessage, new UpLoadResultDialogListener() {
            @Override
            public void onBack() {
                upload_maintenance_result_enter_layout.removeAllViews();
                upload_maintenance_result_enter_layout.setVisibility(View.GONE);
                if(uploadlistener != null){
                    uploadlistener.updateDialogFocusable(true);
                }
            }

            @Override
            public void onReCommit() {
                upload_maintenance_result_enter_layout.removeAllViews();
                upload_maintenance_result_enter_layout.setVisibility(View.GONE);
                commit();
//                onLoading();s
            }
        });

        RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        upload_maintenance_result_enter_layout.removeAllViews();
        upload_maintenance_result_enter_layout.addView(mCommitMaintenanceResultDialogWidget.getView(),params);
        upload_maintenance_result_enter_layout.setVisibility(View.VISIBLE);
        if(uploadlistener != null){
            uploadlistener.updateDialogFocusable(false);
        }
    }

    public void showCommitSuccess(){

        if(mCommitMaintenanceResultDialogWidget == null){
            return;
        }

        mCommitMaintenanceResultDialogWidget.showSuccessView(new LayoutBackListener() {
            @Override
            public void onBack() {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });
        RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        upload_maintenance_result_enter_layout.removeAllViews();
        upload_maintenance_result_enter_layout.addView(mCommitMaintenanceResultDialogWidget.getView(),params);
        upload_maintenance_result_enter_layout.setVisibility(View.VISIBLE);
        if(uploadlistener != null){
            uploadlistener.updateDialogFocusable(false);
        }
    }

    public boolean OnKeyBack(){

        if(mCommitMaintenanceResultDialogWidget != null && mCommitMaintenanceResultDialogWidget.isShowSuccess()){
            return false;
        }else{
            if (upload_maintenance_result_enter_layout.getVisibility() == View.VISIBLE) {
                upload_maintenance_result_enter_layout.removeAllViews();
                upload_maintenance_result_enter_layout.setVisibility(View.GONE);
                if (uploadlistener != null) {
                    uploadlistener.updateDialogFocusable(true);
                }
                return true;
            }

            return false;

        }
    }


}
