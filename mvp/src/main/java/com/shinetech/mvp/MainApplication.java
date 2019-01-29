package com.shinetech.mvp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskCtrl.DBCtrlTask;
import com.shinetech.mvp.DB.DBUpGradeHelper;
import com.shinetech.mvp.DB.greendao.DaoMaster;
import com.shinetech.mvp.DB.greendao.DaoSession;
import com.shinetech.mvp.network.UDP.SocketUtils;
import com.shinetech.mvp.utils.HttpUtils;
import com.shinetech.mvp.utils.LogUtils;
import com.shinetech.mvp.utils.ShareUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


import java.util.ArrayList;
import java.util.List;

/**
 * @author ljn
 */
public class MainApplication extends Application {
    public static final String SHARE_PREFERENCE_NAME = "SHARE_PREFERENCE_NAME";

    public static final String DISCONNECT_SERVICE = "disConnect_server";

    public static final String OFFLINEMAPS = "OffLineMap_Key";

    /**
     * 是否使用有应答的UDP接口
     */
    public static final boolean isUserResponseUDP = true;

    /**
     * 是否使用数据库存储车辆信息
     */
    public static final boolean isUseCarInfoDB = false;

    /**
     * 是否使用聚合方式显示车辆，缩小地图到一定大小时，以数量的形式显示车辆
     */
    public static final boolean isUseMarkerCluster = false;

    //主线程handler
    private static Handler mMainThreadHandler = new Handler();
    //主线程
    private static Thread mMainThread = Thread.currentThread();
    //主线程Id
    private static int mMainThreadId = android.os.Process.myTid();
    //context
    private static MainApplication instance;
    //支付相关密匙信息
//    public static PaymentKey paymentKey = new PaymentKey();
    //内存检测watcher
//    private RefWatcher refWatcher;
    //是否登录成功
    public static boolean isLogin = false;
    private static SharedPreferences preferences;

    private static IWXAPI wxapi;

    /**
     * 数据库操作类
     */
    private static DaoSession daoSession;

    private List<Activity> allActivityList = new ArrayList<>();

    private boolean isInit = true;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //        refWatcher = LeakCanary.install(this);
        preferences = getSharedPreferences(SHARE_PREFERENCE_NAME, MODE_PRIVATE);

        init();

    }

    private void init(){
        DBCtrlTask.getInstance().runTask(new TaskBean() {
            @Override
            public void run() {

                //配置数据库
                setupDatabase();

                if (!HttpUtils.isNetWorkConnected()) {
                    LogUtils.error(" no NetWork Connected ");
                    return;
                }

                try {
                    SocketUtils.getInstance().connect();
                }catch (Exception e){
                    e.printStackTrace();
                }


                initImageLoad();
            }
        });
    }

    private void initImageLoad(){


        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(instance)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
//                .discCacheExtraOptions(480, 800, Bitmap.CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)/设置缓存的详细信息，最好不要设置这个
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100) //缓存的文件数量
               // .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(instance, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
//                .writeDebugLogs() // Remove for release app
                .build();//开始构建

        ImageLoader.getInstance().init(config);//全局初始化此配置

    }

    public boolean isInit(){
        return isInit;
    }

    public void finishInit(){
        isInit = false;
    }

    public static Thread getMainThread() {
        return mMainThread;
    }

    public static int getMainThreadId() {
        return mMainThreadId;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    public static MainApplication getInstance() {
        return instance;
    }

    /*  public static RefWatcher getRefWatcher() {
          MainApplication application = (MainApplication) MainApplication.getInstance();
          return application.refWatcher;
      }
  */
    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public void exitApp(){
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {// android2.1
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            am.restartPackage(getPackageName());
//            System.exit(0);
        }
    }

    public void addActivityList(Activity mActivity){
        if(!allActivityList.contains(mActivity)) {
            allActivityList.add(mActivity);
        }
    }

    public void reMoveActivityList(Activity mActivity){
        allActivityList.remove(mActivity);
    }

    public List<Activity> getActivityList(){
        return allActivityList;
    }

    /**
     * 配置数据库
     */
    private void setupDatabase() {
        //创建数据库carMonitor_db.db"
        DBUpGradeHelper dbUpGradeHelper = new DBUpGradeHelper(this, "carMonitor_db");
        //获取可写数据库
        SQLiteDatabase db = dbUpGradeHelper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        daoSession = daoMaster.newSession();
    }

    /**
     * 获取数据库操作类
     * @return
     */
    public static DaoSession getDaoInstant() {
        return daoSession;
    }

    public void initWXApi(Context context){
        //微信分享
        wxapi = WXAPIFactory.createWXAPI(context, ShareUtils.WX_APP_ID, true);
        wxapi.registerApp(ShareUtils.WX_APP_ID);
    }

    public IWXAPI getWXApi(){
        return wxapi;
    }

}
