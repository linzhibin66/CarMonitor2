package com.shinetech.mvp.DB.bean;

import com.shinetech.mvp.network.UDP.bean.orderBean.CreateOrderDataVo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;

import static android.R.attr.data;
import org.greenrobot.greendao.DaoException;
import com.shinetech.mvp.DB.greendao.DaoSession;
import com.shinetech.mvp.DB.greendao.CreateMaintenanceInfoDBDao;
import com.shinetech.mvp.DB.greendao.BaseOrderInfoDBDao;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by ljn on 2018-01-03.
 */
@Entity(indexes = {@Index(value = "orderNumber DESC")})
public class BaseOrderInfoDB {

    @Id
    @Unique
    private Long id;

    /**
     * 登录的用户名
     */
    public String userName;

    /**
     * 工单名称
     */
    private String orderName;

    /**
     * 工单号
     */
    private String orderNumber;

    /**
     * 状态
     */
    private byte orderStatus;

    /**
     * 客户名称
     */
    private String clienteleName;

    /**
     * 客户电话
     */
    private String clientelePhone;

    /**
     * 创建人
     */
    private String founder;

    private long dataInfoId;

//    @ToOne(joinProperty = "orderNumber")
    @ToOne(joinProperty = "dataInfoId")
    private CreateMaintenanceInfoDB data;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1707216334)
    private transient BaseOrderInfoDBDao myDao;

    @Generated(hash = 998359)
    private transient Long data__resolvedKey;



    @Generated(hash = 294166674)
    public BaseOrderInfoDB(Long id, String userName, String orderName, String orderNumber, byte orderStatus, String clienteleName,
            String clientelePhone, String founder, long dataInfoId) {
        this.id = id;
        this.userName = userName;
        this.orderName = orderName;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.clienteleName = clienteleName;
        this.clientelePhone = clientelePhone;
        this.founder = founder;
        this.dataInfoId = dataInfoId;
    }

    public BaseOrderInfoDB(String userName, String orderName,
                           String orderNumber, byte orderStatus, String clienteleName,
                           String clientelePhone, String founder) {
        this.userName = userName;
        this.orderName = orderName;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.clienteleName = clienteleName;
        this.clientelePhone = clientelePhone;
        this.founder = founder;
    }

    @Generated(hash = 146133820)
    public BaseOrderInfoDB() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderName() {
        return this.orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderNumber() {
        return this.orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public byte getOrderStatus() {
        return this.orderStatus;
    }

    public void setOrderStatus(byte orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getClienteleName() {
        return this.clienteleName;
    }

    public void setClienteleName(String clienteleName) {
        this.clienteleName = clienteleName;
    }

    public String getClientelePhone() {
        return this.clientelePhone;
    }

    public void setClientelePhone(String clientelePhone) {
        this.clientelePhone = clientelePhone;
    }

    public String getFounder() {
        return this.founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseOrderInfoDB that = (BaseOrderInfoDB) o;

        if (getOrderStatus() != that.getOrderStatus()) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getUserName() != null ? !getUserName().equals(that.getUserName()) : that.getUserName() != null)
            return false;
        if (getOrderName() != null ? !getOrderName().equals(that.getOrderName()) : that.getOrderName() != null)
            return false;
        if (getOrderNumber() != null ? !getOrderNumber().equals(that.getOrderNumber()) : that.getOrderNumber() != null)
            return false;
        if (getClienteleName() != null ? !getClienteleName().equals(that.getClienteleName()) : that.getClienteleName() != null)
            return false;
        if (getClientelePhone() != null ? !getClientelePhone().equals(that.getClientelePhone()) : that.getClientelePhone() != null)
            return false;
        return getFounder() != null ? getFounder().equals(that.getFounder()) : that.getFounder() == null;

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getUserName() != null ? getUserName().hashCode() : 0);
        result = 31 * result + (getOrderName() != null ? getOrderName().hashCode() : 0);
        result = 31 * result + (getOrderNumber() != null ? getOrderNumber().hashCode() : 0);
        result = 31 * result + (int) getOrderStatus();
        result = 31 * result + (getClienteleName() != null ? getClienteleName().hashCode() : 0);
        result = 31 * result + (getClientelePhone() != null ? getClientelePhone().hashCode() : 0);
        result = 31 * result + (getFounder() != null ? getFounder().hashCode() : 0);
        return result;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 832308945)
    public CreateMaintenanceInfoDB getData() {
        long __key = this.dataInfoId;
        if (data__resolvedKey == null || !data__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CreateMaintenanceInfoDBDao targetDao = daoSession.getCreateMaintenanceInfoDBDao();
            CreateMaintenanceInfoDB dataNew = targetDao.load(__key);
            synchronized (this) {
                data = dataNew;
                data__resolvedKey = __key;
            }
        }
        return data;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 341820889)
    public void setData(@NotNull CreateMaintenanceInfoDB data) {
        if (data == null) {
            throw new DaoException("To-one property 'dataInfoId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.data = data;
            dataInfoId = data.getId();
            data__resolvedKey = dataInfoId;
        }
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
    @Generated(hash = 970988943)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBaseOrderInfoDBDao() : null;
    }

    public long getDataInfoId() {
        return this.dataInfoId;
    }

    public void setDataInfoId(long dataInfoId) {
        this.dataInfoId = dataInfoId;
    }
}
