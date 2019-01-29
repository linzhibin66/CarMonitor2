package com.easyder.carmonitor.Utils;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.easyder.carmonitor.R;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.utils.LogUtils;

/**
 * Created by ljn on 2017-07-10.
 */
public class DownloadMapUtils {

    public static String getDownLoadStaus(int status){
        switch(status){
            case MKOLUpdateElement.DOWNLOADING:
                return MainApplication.getInstance().getString(R.string.downloading);
            case MKOLUpdateElement.WAITING:
                return MainApplication.getInstance().getString(R.string.download_wait);
            case MKOLUpdateElement.SUSPENDED:
                return MainApplication.getInstance().getString(R.string.download_stop);
            case MKOLUpdateElement.eOLDSFormatError:
                return MainApplication.getInstance().getString(R.string.dataerror_download_again);
            case MKOLUpdateElement.FINISHED:
            case MKOLUpdateElement.eOLDSInstalling:
                return MainApplication.getInstance().getString(R.string.downloaded);
            default:
//                if(debug) LogUtils.error("getDownLoadStaus status : " + status);
                return MainApplication.getInstance().getString(R.string.undefined);

        }
    }
}
