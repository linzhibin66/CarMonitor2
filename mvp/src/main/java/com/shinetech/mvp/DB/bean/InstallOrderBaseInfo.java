package com.shinetech.mvp.DB.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.shinetech.mvp.DB.greendao.DaoSession;
import com.shinetech.mvp.DB.greendao.InstallTerminalnfoDao;
import com.shinetech.mvp.DB.greendao.InstallOrderBaseInfoDao;
import com.shinetech.mvp.MainApplication;
import com.shinetech.mvp.R;

/**
 * Created by Lenovo on 2017-11-30.
 */

@Entity(indexes = {@Index(value = "id DESC", unique = true)})
public class InstallOrderBaseInfo {

    public static final int WAITING_ORDER_RECEIVING_STATUS = 0;

    public static final int ORDER_RECEIVING_STATUS = 1;

    public static final int FINISH_STATUS = 2;

    @Id(autoincrement = true)
    private Long id;

    /**
     * 订单状态
     */
    private int orderStatus;

    /**
     * 工单号
     */
    @Unique
    private String orderNumber;

    /**
     * 登录的用户名
     */
    public String userName;

    /**
     * 安装地址
     */
    private String installLocation;

    /**
     * 安装形式
     */
    public short installType;

    /**
     * 安装台数
     */
    private int installCount;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 要求完成时间
     */
    private String finishTime;

    @ToMany(joinProperties = {
            @JoinProperty(name = "orderNumber", referencedName = "orderNumber")
    })
    private List<InstallTerminalnfo> terminallist;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1121229267)
    private transient InstallOrderBaseInfoDao myDao;

    @Generated(hash = 1789267184)
    public InstallOrderBaseInfo(Long id, int orderStatus, String orderNumber, String userName,
            String installLocation, short installType, int installCount, String contacts,
            String finishTime) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.orderNumber = orderNumber;
        this.userName = userName;
        this.installLocation = installLocation;
        this.installType = installType;
        this.installCount = installCount;
        this.contacts = contacts;
        this.finishTime = finishTime;
    }

    public InstallOrderBaseInfo(int orderStatus, String orderNumber, String userName,
                                String installLocation, short installType, int installCount,
                                String contacts, String finishTime) {
        this.orderStatus = orderStatus;
        this.orderNumber = orderNumber;
        this.userName = userName;
        this.installLocation = installLocation;
        this.installType = installType;
        this.installCount = installCount;
        this.contacts = contacts;
        this.finishTime = finishTime;
    }

    @Generated(hash = 342491780)
    public InstallOrderBaseInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return this.orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getInstallLocation() {
        return this.installLocation;
    }

    public void setInstallLocation(String installLocation) {
        this.installLocation = installLocation;
    }

    public short getInstallType() {
        return this.installType;
    }

    public void setInstallType(short installType) {
        this.installType = installType;
    }

    public int getInstallCount() {
        return this.installCount;
    }

    public void setInstallCount(int installCount) {
        this.installCount = installCount;
    }

    public String getFinishTime() {
        return this.finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 6245789)
    public List<InstallTerminalnfo> getTerminallist() {
        if (terminallist == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            InstallTerminalnfoDao targetDao = daoSession.getInstallTerminalnfoDao();
            List<InstallTerminalnfo> terminallistNew = targetDao
                    ._queryInstallOrderBaseInfo_Terminallist(orderNumber);
            synchronized (this) {
                if (terminallist == null) {
                    terminallist = terminallistNew;
                }
            }
        }
        return terminallist;
    }

    /**
     * 模糊查询车牌
     * @param plateNumberlike
     * @return
     */
    public List<InstallTerminalnfo> getTerminallist(String plateNumberlike) {

            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            InstallTerminalnfoDao targetDao = daoSession.getInstallTerminalnfoDao();

        Query<InstallTerminalnfo> build;

        synchronized (this) {
            QueryBuilder<InstallTerminalnfo> queryBuilder = targetDao.queryBuilder();
            queryBuilder.where(InstallTerminalnfoDao.Properties.OrderNumber.eq(null), InstallTerminalnfoDao.Properties.PlateNumber.like("%" + plateNumberlike + "%"));
            queryBuilder.orderAsc(InstallTerminalnfoDao.Properties.PlateNumber);
             build = queryBuilder.build();
        }

        Query<InstallTerminalnfo> installTerminalnfoQuery = build.forCurrentThread();
        installTerminalnfoQuery.setParameter(0,orderNumber);
        List<InstallTerminalnfo> terminallistNew = installTerminalnfoQuery.list();

        return terminallistNew;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 123101177)
    public synchronized void resetTerminallist() {
        terminallist = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 41909302)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getInstallOrderBaseInfoDao() : null;
    }

    public String getInstallTypeString(){
        switch (installType){
            case 0:
                return MainApplication.getInstance().getString(R.string.new_install);
            case 1:
                return MainApplication.getInstance().getString(R.string.change_install);
            case 2:
                return MainApplication.getInstance().getString(R.string.dismantle_install);
        }
        return null;
    }

    public int getOrderStatus() {
        return this.orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getContacts() {
        return this.contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }



}
