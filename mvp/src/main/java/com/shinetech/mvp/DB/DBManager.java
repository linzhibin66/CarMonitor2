package com.shinetech.mvp.DB;

import android.text.TextUtils;

import com.shinetech.mvp.DB.DBThreadPool.Task.TaskBean;
import com.shinetech.mvp.DB.DBThreadPool.TaskCtrl.DBCtrlTask;
import com.shinetech.mvp.DB.bean.BaseOrderInfoDB;
import com.shinetech.mvp.DB.bean.CarInfoDB;
import com.shinetech.mvp.DB.bean.CreateMaintenanceInfoDB;
import com.shinetech.mvp.DB.bean.InstallOrderBaseInfo;
import com.shinetech.mvp.DB.bean.InstallTerminalnfo;
import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;
import com.shinetech.mvp.DB.bean.MaintenanceOrderProgressBean;
import com.shinetech.mvp.DB.bean.PushMessage;
import com.shinetech.mvp.DB.bean.SelectCarList;
import com.shinetech.mvp.DB.greendao.BaseOrderInfoDBDao;
import com.shinetech.mvp.DB.greendao.CarInfoDBDao;
import com.shinetech.mvp.DB.greendao.CreateMaintenanceInfoDBDao;
import com.shinetech.mvp.DB.greendao.DaoSession;
import com.shinetech.mvp.DB.greendao.InstallOrderBaseInfoDao;
import com.shinetech.mvp.DB.greendao.InstallTerminalnfoDao;
import com.shinetech.mvp.DB.greendao.MaintenanceOrderInfoBeanDao;
import com.shinetech.mvp.DB.greendao.MaintenanceOrderProgressBeanDao;
import com.shinetech.mvp.DB.greendao.PushMessageDao;
import com.shinetech.mvp.DB.greendao.SelectCarListDao;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.utils.LogUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by ljn on 2017-05-18.
 */
public class DBManager {

//    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - PushMessage  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public static boolean insertPushMessage(PushMessage mPushMessage){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        PushMessageDao pushMessageDao = daoInstant.getPushMessageDao();

        if(pushMessageDao == null){
            return false;
        }

        try {

            pushMessageDao.insertOrReplace(mPushMessage);

        }catch (Exception e){
            LogUtils.error(" insert PushMessage of DB fail");
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 获取还未阅读的消息数量
     * @return
     */
    public static int queryPushMessageNoReadCount(){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        PushMessageDao pushMessageDao = daoInstant.getPushMessageDao();

        if(pushMessageDao == null){
            return 0;
        }
        try {

            List<PushMessage> list = pushMessageDao.queryBuilder().where(PushMessageDao.Properties.IsRead.eq(false)).list();

            return list.size();

        }catch (Exception e){
            LogUtils.error(" insert PushMessage of DB fail");
            e.printStackTrace();
        }

        return 0;

    }

    public static void clearNoReadMessage(){
        final DBCtrlTask mDBCtrlTask = DBCtrlTask.getInstance();
        mDBCtrlTask.runTask(new TaskBean() {
            @Override
            public void run() {

                DaoSession daoInstant = MainApplication.getDaoInstant();

                PushMessageDao pushMessageDao = daoInstant.getPushMessageDao();

                if(pushMessageDao == null){
                    return ;
                }
                try {

                    List<PushMessage> list = pushMessageDao.queryBuilder().where(PushMessageDao.Properties.IsRead.eq(false)).list();

                    for(int i = 0; i<list.size(); i++){
                        final PushMessage pushMessage = list.get(i);
                        TaskBean taskBean = new TaskBean() {
                            @Override
                            public void run() {
                                pushMessage.setIsRead(true);
                                insertPushMessage(pushMessage);
                            }
                        };
                        taskBean.setType("clearNoReadMessage");
                        mDBCtrlTask.runTask(taskBean);
                    }


                }catch (Exception e){
                    LogUtils.error(" insert PushMessage of DB fail");
                    e.printStackTrace();
                }

            }
        });
    }

    public static List<PushMessage> queryPushMessage(){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        PushMessageDao pushMessageDao = daoInstant.getPushMessageDao();

        if(pushMessageDao == null){
            return null;
        }
        try {

            List<PushMessage> list = pushMessageDao.queryBuilder().orderAsc(PushMessageDao.Properties.SendTime).list();

            return list;

        }catch (Exception e){
            LogUtils.error(" query PushMessage of DB fail");
            e.printStackTrace();
        }

        return null;

    }

    public static boolean deletePushMessage(PushMessage mPushMessage){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        PushMessageDao pushMessageDao = daoInstant.getPushMessageDao();

        if(pushMessageDao == null){
            return false;
        }
        try {
            pushMessageDao.deleteByKey(mPushMessage.getId());
            return true;

        }catch (Exception e){
            LogUtils.error(" delete PushMessage of DB fail");
            e.printStackTrace();
        }

        return false;

    }


//    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - SelectCarList  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public static void insertSelectCarList(SelectCarList car){
        DaoSession daoInstant = MainApplication.getDaoInstant();
        SelectCarListDao selectCarListDao = daoInstant.getSelectCarListDao();

        if(selectCarListDao == null){
            return;
        }

        try {
            selectCarListDao.insertOrReplace(car);
            return ;

        }catch (Exception e){
            LogUtils.error(" insert SelectCarList of DB fail");
            e.printStackTrace();
        }
    }

    /**
     * 插入选择的车辆
     * @param carlists
     */
    public static void insertSelectCarList(List<SelectCarList> carlists){
        DaoSession daoInstant = MainApplication.getDaoInstant();
        SelectCarListDao selectCarListDao = daoInstant.getSelectCarListDao();

        if(selectCarListDao == null){
            return;
        }

        try {
            selectCarListDao.insertOrReplaceInTx(carlists);
            return ;

        }catch (Exception e){
            LogUtils.error(" insert all SelectCarList of DB fail");
            e.printStackTrace();
        }
    }

    /**
     * 根据描述查询选择的车辆列表
     * @param description
     * @return
     */
    public static List<SelectCarList> querySelectCarList(String userName, String description){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        SelectCarListDao selectCarListDao = daoInstant.getSelectCarListDao();

        if(selectCarListDao == null){
            return null;
        }
        try {


            List<SelectCarList> list = selectCarListDao.queryBuilder().where( TextUtils.isEmpty(description) ? SelectCarListDao.Properties.Description.isNull() : SelectCarListDao.Properties.Description.eq(description)
                    , SelectCarListDao.Properties.UserName.eq(userName)).list();

            return list;

        }catch (Exception e){
            LogUtils.error(" query SelectCarList of DB fail");
            e.printStackTrace();
        }

        return null;

    }

    public static boolean deleteSelectCarList(SelectCarList car){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        SelectCarListDao selectCarListDao = daoInstant.getSelectCarListDao();

        if(selectCarListDao == null){
            return false;
        }
        try {
            selectCarListDao.deleteByKey(car.getId());
            return true;

        }catch (Exception e){
            LogUtils.error(" delete PushMessage of DB fail");
            e.printStackTrace();
        }

        return false;

    }

    public static boolean deleteSelectCarList(List<SelectCarList> carLists){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        SelectCarListDao selectCarListDao = daoInstant.getSelectCarListDao();

        if(selectCarListDao == null){
            return false;
        }
        try {
            selectCarListDao.deleteInTx(carLists);
            return true;

        }catch (Exception e){
            LogUtils.error(" delete PushMessage of DB fail");
            e.printStackTrace();
        }

        return false;

    }


//    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - CarInfoDB  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public static boolean insertCarInfo(CarInfoDB mCarInfoDB){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        CarInfoDBDao carInfoDBDao = daoInstant.getCarInfoDBDao();

        if(carInfoDBDao == null){
            return false;
        }

        try {

            carInfoDBDao.insertOrReplace(mCarInfoDB);

        }catch (Exception e){
            LogUtils.error(" insert CarInfo of DB fail");
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 插入车辆信息
     * @param carInfolists
     */
    public static void insertCarInfoList(List<CarInfoDB> carInfolists){
        DaoSession daoInstant = MainApplication.getDaoInstant();
        CarInfoDBDao carInfoDBDao = daoInstant.getCarInfoDBDao();

        if(carInfoDBDao == null){
            return;
        }

        try {
            carInfoDBDao.insertOrReplaceInTx(carInfolists);
            return ;

        }catch (Exception e){
            LogUtils.error(" insert all CarInfo of DB fail");
            e.printStackTrace();
        }
    }

    /**
     * 根据描述查询选择的车辆列表
     * @param userName
     * @param plateNumber
     * @return
     */
    public static List<CarInfoDB> querySelectCarInfoList(String userName, String plateNumber){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        CarInfoDBDao carInfoDBDao = daoInstant.getCarInfoDBDao();

        if(carInfoDBDao == null){
            return null;
        }
        try {
            List<CarInfoDB> list;

            if(TextUtils.isEmpty(plateNumber)){

               list = carInfoDBDao.queryBuilder().where( CarInfoDBDao.Properties.UserName.eq(userName)).list();

            }else{

                list = carInfoDBDao.queryBuilder().where( CarInfoDBDao.Properties.PlateNumber.eq(plateNumber)
                        , CarInfoDBDao.Properties.UserName.eq(userName)).list();

            }

            return list;

        }catch (Exception e){
            LogUtils.error(" query SelectCarList of DB fail");
            e.printStackTrace();
        }

        return null;

    }
    //    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - BaseOrderInfo  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public static boolean insertBaseOrderInfo(BaseOrderInfoDB mBaseOrderInfoDB){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        BaseOrderInfoDBDao baseOrderInfoDBDao = daoInstant.getBaseOrderInfoDBDao();

        if(baseOrderInfoDBDao == null){
            return false;
        }

        try {

            baseOrderInfoDBDao.insert(mBaseOrderInfoDB);

        }catch (Exception e){
            LogUtils.error(" insert BaseOrderInfoDB of DB fail");
            e.printStackTrace();
        }

        return true;
    }

    public static boolean updataBaseOrderInfo(BaseOrderInfoDB mBaseOrderInfoDB){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        BaseOrderInfoDBDao baseOrderInfoDBDao = daoInstant.getBaseOrderInfoDBDao();

        if(baseOrderInfoDBDao == null){
            return false;
        }

        try {

            baseOrderInfoDBDao.update(mBaseOrderInfoDB);

        }catch (Exception e){
            LogUtils.error(" update BaseOrderInfoDB of DB fail");
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 根据描述查询选择的工单列表
     * @param userName
     * @return
     */
    public static List<BaseOrderInfoDB> querySelectBaseOrderInfo(String userName){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        BaseOrderInfoDBDao baseOrderInfoDBDao = daoInstant.getBaseOrderInfoDBDao();

        if(baseOrderInfoDBDao == null){
            return null;
        }

        try {
            List<BaseOrderInfoDB> list = null;

            list = baseOrderInfoDBDao.queryBuilder().where( BaseOrderInfoDBDao.Properties.UserName.eq(userName)).list();

            return list;

        }catch (Exception e){
            LogUtils.error(" query SelectBaseOrderInfo of DB fail");
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 根据描述查询选择的工单
     * @param userName
     * @return
     */
    public static BaseOrderInfoDB querySelectBaseOrderInfo(String userName, String orderNumber){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        BaseOrderInfoDBDao baseOrderInfoDBDao = daoInstant.getBaseOrderInfoDBDao();

        if(baseOrderInfoDBDao == null){
            return null;
        }

        try {
            List<BaseOrderInfoDB> list = null;

            list = baseOrderInfoDBDao.queryBuilder().where( BaseOrderInfoDBDao.Properties.UserName.eq(userName),BaseOrderInfoDBDao.Properties.OrderNumber.eq(orderNumber)).list();

            if(list != null && list.size()>0){
                return list.get(0);
            }

        }catch (Exception e){
            LogUtils.error(" query SelectBaseOrderInfo of DB fail");
            e.printStackTrace();
        }

        return null;

    }

    public static List<BaseOrderInfoDB> querySelectBaseOrderInfo(String userName,int[] status){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        BaseOrderInfoDBDao baseOrderInfoDBDao = daoInstant.getBaseOrderInfoDBDao();

        if(baseOrderInfoDBDao == null){
            return null;
        }


        try {
            List<BaseOrderInfoDB> list = null;

            QueryBuilder qb = baseOrderInfoDBDao.queryBuilder();
            switch (status.length){
                case 1:
                    if(status[0] == 2){
                        qb.where(BaseOrderInfoDBDao.Properties.UserName.eq(userName), BaseOrderInfoDBDao.Properties.OrderStatus.eq(0), BaseOrderInfoDBDao.Properties.DataInfoId.ge(0));
                    }else {
                        qb.where(BaseOrderInfoDBDao.Properties.UserName.eq(userName), BaseOrderInfoDBDao.Properties.OrderStatus.eq(status[0]), BaseOrderInfoDBDao.Properties.DataInfoId.eq(-1));
                    }
                    break;
                case 2:
                    if((status[0] + status[1]) == 1) {
                        qb.where(BaseOrderInfoDBDao.Properties.UserName.eq(userName), BaseOrderInfoDBDao.Properties.DataInfoId.eq(-1));
                    }else{
                        if((status[0] + status[1]) == 2) {
                            qb.where(BaseOrderInfoDBDao.Properties.UserName.eq(userName),BaseOrderInfoDBDao.Properties.OrderStatus.eq(0));
                        }else{
                            if(status[0] == 2) {
                                qb.where(BaseOrderInfoDBDao.Properties.UserName.eq(userName),
                                        qb.or(qb.and(BaseOrderInfoDBDao.Properties.OrderStatus.eq(0), BaseOrderInfoDBDao.Properties.DataInfoId.ge(0)), BaseOrderInfoDBDao.Properties.OrderStatus.eq(status[1])));
                            }else{
                                qb.where(BaseOrderInfoDBDao.Properties.UserName.eq(userName),
                                        qb.or(qb.and(BaseOrderInfoDBDao.Properties.OrderStatus.eq(0), BaseOrderInfoDBDao.Properties.DataInfoId.ge(0)), BaseOrderInfoDBDao.Properties.OrderStatus.eq(status[0])));
                            }
                        }
                    }
                    break;
                default:
                case 3:
                    qb.where(BaseOrderInfoDBDao.Properties.UserName.eq(userName));
                    break;
            }

            list = qb.list();


            return list;

        }catch (Exception e){
            LogUtils.error(" query SelectCarList of DB fail");
            e.printStackTrace();
        }

        return null;

    }

    public static boolean deleteBaseOrder(BaseOrderInfoDB mBaseOrderInfoDB){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        BaseOrderInfoDBDao baseOrderInfoDBDao = daoInstant.getBaseOrderInfoDBDao();

        if(baseOrderInfoDBDao == null){
            return false;
        }

        try {
            baseOrderInfoDBDao.deleteByKey(mBaseOrderInfoDB.getId());
            return true;
        }catch (Exception e){
            LogUtils.error(" delete BaseOrderInfoDB of DB fail");
            e.printStackTrace();
        }

        return false;

    }


    //    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - MaintenanceOrderInfoBean  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public static boolean insertMaintenanceOrderInfoBean(MaintenanceOrderInfoBean mMaintenanceOrderInfoBean){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        MaintenanceOrderInfoBeanDao maintenanceOrderInfoBeanDao = daoInstant.getMaintenanceOrderInfoBeanDao();

        if(maintenanceOrderInfoBeanDao == null){
            return false;
        }

        try {

            maintenanceOrderInfoBeanDao.insertOrReplace(mMaintenanceOrderInfoBean);

        }catch (Exception e){
            LogUtils.error(" insert CarInfo of DB fail");
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 插入车辆信息
     * @param mMaintenanceOrderInfoList
     */
    public static void insertMaintenanceOrderInfoList(List<MaintenanceOrderInfoBean> mMaintenanceOrderInfoList){
        DaoSession daoInstant = MainApplication.getDaoInstant();
        MaintenanceOrderInfoBeanDao maintenanceOrderInfoBeanDao = daoInstant.getMaintenanceOrderInfoBeanDao();

        if(maintenanceOrderInfoBeanDao == null){
            return;
        }

        try {
            maintenanceOrderInfoBeanDao.insertOrReplaceInTx(mMaintenanceOrderInfoList);
            return ;

        }catch (Exception e){
            LogUtils.error(" insert all CarInfo of DB fail");
            e.printStackTrace();
        }
    }

    /**
     * 根据描述查询选择的车辆列表
     * @param userName
     * @return
     */
    public static List<MaintenanceOrderInfoBean> querySelectMaintenanceOrderInfo(String userName){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        MaintenanceOrderInfoBeanDao maintenanceOrderInfoBeanDao = daoInstant.getMaintenanceOrderInfoBeanDao();

        if(maintenanceOrderInfoBeanDao == null){
            return null;
        }

        try {
            List<MaintenanceOrderInfoBean> list = null;

            list = maintenanceOrderInfoBeanDao.queryBuilder().where( MaintenanceOrderInfoBeanDao.Properties.UserName.eq(userName)).list();

            return list;

        }catch (Exception e){
            LogUtils.error(" query SelectCarList of DB fail");
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 根据描述查询选择的车辆列表
     * @param userName
     * @return
     */
    public static MaintenanceOrderInfoBean querySelectMaintenanceOrderInfo(String userName, String orderNumber){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        MaintenanceOrderInfoBeanDao maintenanceOrderInfoBeanDao = daoInstant.getMaintenanceOrderInfoBeanDao();

        if(maintenanceOrderInfoBeanDao == null){
            return null;
        }

        try {
            List<MaintenanceOrderInfoBean> list = null;

            list = maintenanceOrderInfoBeanDao.queryBuilder().where( MaintenanceOrderInfoBeanDao.Properties.UserName.eq(userName),MaintenanceOrderInfoBeanDao.Properties.OrderNumber.eq(orderNumber)).list();

            if(list != null && list.size()>0){
                return list.get(0);
            }

        }catch (Exception e){
            LogUtils.error(" query SelectCarList of DB fail");
            e.printStackTrace();
        }

        return null;

    }

    public static List<MaintenanceOrderInfoBean> querySelectMaintenanceOrderInfo(String userName,int[] status){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        MaintenanceOrderInfoBeanDao maintenanceOrderInfoBeanDao = daoInstant.getMaintenanceOrderInfoBeanDao();

        if(maintenanceOrderInfoBeanDao == null){
            return null;
        }


        try {
            List<MaintenanceOrderInfoBean> list = null;

            QueryBuilder qb = maintenanceOrderInfoBeanDao.queryBuilder();
            switch (status.length){
                case 1:
                    qb.where(MaintenanceOrderInfoBeanDao.Properties.UserName.eq(userName),MaintenanceOrderInfoBeanDao.Properties.Status.eq(status[0]));
                    break;
                case 2:
                    qb.where(MaintenanceOrderInfoBeanDao.Properties.UserName.eq(userName),
                            qb.or(MaintenanceOrderInfoBeanDao.Properties.Status.eq(status[0]),MaintenanceOrderInfoBeanDao.Properties.Status.eq(status[1])));
                break;
                case 3:
                default:
                    qb.where(MaintenanceOrderInfoBeanDao.Properties.UserName.eq(userName));
                    break;
            }

            list = qb.list();


            return list;

        }catch (Exception e){
            LogUtils.error(" query SelectCarList of DB fail");
            e.printStackTrace();
        }

        return null;

    }

    public static boolean deleteMaintenanceOrder(MaintenanceOrderInfoBean maintenanceOrderItem){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        MaintenanceOrderInfoBeanDao maintenanceOrderInfoBeanDao = daoInstant.getMaintenanceOrderInfoBeanDao();

        if(maintenanceOrderInfoBeanDao == null){
            return false;
        }

        try {
            maintenanceOrderInfoBeanDao.deleteByKey(maintenanceOrderItem.getId());
            return true;
        }catch (Exception e){
            LogUtils.error(" delete maintenanceOrderItem of DB fail");
            e.printStackTrace();
        }

        return false;

    }

    //    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - InstallOrderBaseInfo  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * 插入安装单信息
     * @param mInstallOrderBaseInfo
     * @return
     */
    public static boolean insertInstallOrderBaseInfo(InstallOrderBaseInfo mInstallOrderBaseInfo){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        InstallOrderBaseInfoDao installOrderBaseInfoDao = daoInstant.getInstallOrderBaseInfoDao();

        if(installOrderBaseInfoDao == null){
            return false;
        }

        try {

            installOrderBaseInfoDao.insertOrReplace(mInstallOrderBaseInfo);

        }catch (Exception e){
            LogUtils.error(" insert InstallOrderBaseInfo of DB fail");
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 插入安装单信息
     * @param mInstallOrderBaseInfoList
     */
    public static void insertInstallOrderBaseInfoList(List<InstallOrderBaseInfo>  mInstallOrderBaseInfoList){
        DaoSession daoInstant = MainApplication.getDaoInstant();
        InstallOrderBaseInfoDao installOrderBaseInfoDao = daoInstant.getInstallOrderBaseInfoDao();

        if(installOrderBaseInfoDao == null){
            return ;
        }

        try {
            installOrderBaseInfoDao.insertOrReplaceInTx(mInstallOrderBaseInfoList);
            return ;

        }catch (Exception e){
            LogUtils.error(" insert all InstallOrderBaseInfo of DB fail");
            e.printStackTrace();
        }
    }

    /**
     * 根据描述查询安装单信息
     * @param userName
     * @return
     */
    public static List<InstallOrderBaseInfo> querySelectInstallOrderBaseInfo(String userName){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        InstallOrderBaseInfoDao installOrderBaseInfoDao = daoInstant.getInstallOrderBaseInfoDao();

        if(installOrderBaseInfoDao == null){
            return null;
        }


        try {
            List<InstallOrderBaseInfo> list = null;

            list = installOrderBaseInfoDao.queryBuilder().where(InstallOrderBaseInfoDao.Properties.UserName.eq(userName)).list();

            return list;

        }catch (Exception e){
            LogUtils.error(" query InstallOrderBaseInfo of DB fail");
            e.printStackTrace();
        }

        return null;

    }

    public static List<InstallOrderBaseInfo> querySelectInstallOrderBaseInfo(String userName, int[] status){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        InstallOrderBaseInfoDao installOrderBaseInfoDao = daoInstant.getInstallOrderBaseInfoDao();

        if(installOrderBaseInfoDao == null){
            return null;
        }


        try {
            List<InstallOrderBaseInfo> list = null;

            QueryBuilder qb = installOrderBaseInfoDao.queryBuilder();
            switch (status.length){
                case 1:
                    qb.where(InstallOrderBaseInfoDao.Properties.UserName.eq(userName),InstallOrderBaseInfoDao.Properties.OrderStatus.eq(status[0]));
                    break;
                case 2:
                    qb.where(InstallOrderBaseInfoDao.Properties.UserName.eq(userName),
                            qb.or(InstallOrderBaseInfoDao.Properties.OrderStatus.eq(status[0]),InstallOrderBaseInfoDao.Properties.OrderStatus.eq(status[1])));
                    break;
                case 3:
                default:
                    qb.where(InstallOrderBaseInfoDao.Properties.UserName.eq(userName));
                    break;
            }

            list = qb.list();
           // list = installOrderBaseInfoDao.queryBuilder().where(InstallOrderBaseInfoDao.Properties.UserName.eq(userName)).list();

            return list;

        }catch (Exception e){
            LogUtils.error(" query InstallOrderBaseInfo of DB fail");
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 删除安装单信息
     * @param mInstallOrderBaseInfo
     * @return
     */
    public static boolean deleteInstallOrder(InstallOrderBaseInfo mInstallOrderBaseInfo){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        InstallOrderBaseInfoDao installOrderBaseInfoDao = daoInstant.getInstallOrderBaseInfoDao();

        if(installOrderBaseInfoDao == null){
            return false;
        }

        try {
            installOrderBaseInfoDao.deleteByKey(mInstallOrderBaseInfo.getId());
            return true;
        }catch (Exception e){
            LogUtils.error(" delete InstallOrderBaseInfo of DB fail");
            e.printStackTrace();
        }

        return false;

    }

    //    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - InstallTerminalnfo  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * 插入安装终端信息
     * @param mInstallTerminalnfo
     * @return
     */
    public static boolean updateInstallTerminalnfo(InstallTerminalnfo mInstallTerminalnfo){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        InstallTerminalnfoDao installTerminalnfoDao = daoInstant.getInstallTerminalnfoDao();

        if(installTerminalnfoDao == null){
            return false;
        }

        try {

            installTerminalnfoDao.updateInTx(mInstallTerminalnfo);

        }catch (Exception e){
            LogUtils.error(" insert InstallTerminalnfo of DB fail");
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 插入安装终端信息
     * @param mInstallTerminalnfoList
     */
    public static void insertInstallTerminalnfoList(List<InstallTerminalnfo>  mInstallTerminalnfoList){
        DaoSession daoInstant = MainApplication.getDaoInstant();
        InstallTerminalnfoDao installTerminalnfoDao = daoInstant.getInstallTerminalnfoDao();

        if(installTerminalnfoDao == null){
            return ;
        }

        try {
            installTerminalnfoDao.insertInTx(mInstallTerminalnfoList,true);
            return ;

        }catch (Exception e){
            LogUtils.error(" insert all InstallTerminalnfo of DB fail ");
            e.printStackTrace();
        }
    }

    /**
     * 根据描述查询安装单信息
     * @param orderNumber
     * @return
     */
    public static List<InstallTerminalnfo> querySelectInstallTerminalnfo(String orderNumber){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        InstallTerminalnfoDao installTerminalnfoDao = daoInstant.getInstallTerminalnfoDao();

        if(installTerminalnfoDao == null){
            return null;
        }


        try {
            List<InstallTerminalnfo> list = null;

            list = installTerminalnfoDao.queryBuilder().where(InstallTerminalnfoDao.Properties.OrderNumber.eq(orderNumber)).list();

            return list;

        }catch (Exception e){
            LogUtils.error(" query InstallTerminalnfo of DB fail");
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 根据描述查询安装单信息
     * @param orderNumber
     * @return
     */
    public static List<InstallTerminalnfo> querySelectInstallTerminalnfo(String orderNumber,String likePlateNumber){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        InstallTerminalnfoDao installTerminalnfoDao = daoInstant.getInstallTerminalnfoDao();

        if(installTerminalnfoDao == null){
            return null;
        }


        try {
            List<InstallTerminalnfo> list = null;

            list = installTerminalnfoDao.queryBuilder().where(InstallTerminalnfoDao.Properties.OrderNumber.eq(orderNumber),InstallTerminalnfoDao.Properties.PlateNumber.like("%"+likePlateNumber+"%")).list();

            return list;

        }catch (Exception e){
            LogUtils.error(" query InstallTerminalnfo of DB fail");
            e.printStackTrace();
        }

        return null;

    }



//    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - MaintenanceOrderProgressBean  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * 插入安装单信息
     * @param mMaintenanceOrderProgressBeanList
     */
    public static void insertMaintenanceOrderProgressList(List<MaintenanceOrderProgressBean>  mMaintenanceOrderProgressBeanList){

        DaoSession daoInstant = MainApplication.getDaoInstant();
        MaintenanceOrderProgressBeanDao maintenanceOrderProgressBeanDao = daoInstant.getMaintenanceOrderProgressBeanDao();

        if(maintenanceOrderProgressBeanDao == null){
            return ;
        }

        try {
            maintenanceOrderProgressBeanDao.insertOrReplaceInTx(mMaintenanceOrderProgressBeanList);
            return ;

        }catch (Exception e){
            LogUtils.error(" insert all MaintenanceOrderProgressBean of DB fail");
            e.printStackTrace();
        }
    }

    //    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - InstallOrderBaseInfo  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * 插入新建维修单信息
     * @param mCreateMaintenanceInfoDB
     * @return
     */
    public static long insertCreateMaintenanceInfo(CreateMaintenanceInfoDB mCreateMaintenanceInfoDB){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        CreateMaintenanceInfoDBDao createMaintenanceInfoDBDao = daoInstant.getCreateMaintenanceInfoDBDao();

        long id = 0;

        if(createMaintenanceInfoDBDao == null){
            return id;
        }

        try {

            id = createMaintenanceInfoDBDao.insertOrReplace(mCreateMaintenanceInfoDB);

        }catch (Exception e){
            LogUtils.error(" insert insertCreateMaintenanceInfo of DB fail");
            e.printStackTrace();
        }

        return id;
    }

    /**
     * 根据描述查询安装单信息
     * @param orderNumber
     * @return
     */
    public static List<CreateMaintenanceInfoDB> querySelectCreateMaintenanceInfo(String orderNumber){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        CreateMaintenanceInfoDBDao createMaintenanceInfoDBDao = daoInstant.getCreateMaintenanceInfoDBDao();

        if(createMaintenanceInfoDBDao == null){
            return null;
        }


        try {
            List<CreateMaintenanceInfoDB> list = null;

            list = createMaintenanceInfoDBDao.queryBuilder().where(CreateMaintenanceInfoDBDao.Properties.OrderNumber.eq(orderNumber)).list();

            return list;

        }catch (Exception e){
            LogUtils.error(" query CreateMaintenanceInfoDB of DB fail");
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 删除新建维修单信息
     * @param mCreateMaintenanceInfoDB
     * @return
     */
    public static boolean deleteCreateMaintenanceInfo(CreateMaintenanceInfoDB mCreateMaintenanceInfoDB){

        DaoSession daoInstant = MainApplication.getDaoInstant();

        CreateMaintenanceInfoDBDao createMaintenanceInfoDBDao = daoInstant.getCreateMaintenanceInfoDBDao();

        if(createMaintenanceInfoDBDao == null){
            return false;
        }

        try {
            createMaintenanceInfoDBDao.deleteByKey(mCreateMaintenanceInfoDB.getId());
            return true;
        }catch (Exception e){
            LogUtils.error(" delete deleteCreateMaintenanceInfo of DB fail");
            e.printStackTrace();
        }

        return false;

    }

}
