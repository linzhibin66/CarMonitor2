package com.easyder.carmonitor.broadcast;

import android.os.FileObserver;

import com.easyder.carmonitor.adapter.AddPicturesImgGridAdapter;
import com.shinetech.mvp.utils.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljn on 2017-11-28.
 */

public class SDCardListeren extends FileObserver {

    private String filePathDir;

    private List<String> imgPath;

    private AddPicturesImgGridAdapter addPicturesImgGridAdapter;

    public SDCardListeren(String path, List<String> listenerFilePathList, AddPicturesImgGridAdapter addPicturesImgGridAdapter) {
        super(path);
        filePathDir = path;
        this.addPicturesImgGridAdapter = addPicturesImgGridAdapter;

        this.imgPath = listenerFilePathList;
    }

    @Override
    public void onEvent(int event, String fileName) {

        final String filePath = filePathDir+fileName;

        switch (event){
            case FileObserver.DELETE:
            case FileObserver.DELETE_SELF:
                //System.out.println("event = "+event +"   onEvent, path: " + filePath);
                UIUtils.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if(imgPath.contains(filePath)){
                            imgPath.remove(filePath);
                            addPicturesImgGridAdapter.initData(imgPath);
                        }
                    }
                });
                break;
            case 64:
                //System.out.println("event = "+event +"   onEvent, path: " + filePath);
                UIUtils.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if(imgPath.contains(filePath)){
                            File file = new File(filePath);
                            if(!file.exists()) {
                                imgPath.remove(filePath);
                                addPicturesImgGridAdapter.initData(imgPath);
                            }
                        }
                    }
                });

                break;
        }

    }
}
