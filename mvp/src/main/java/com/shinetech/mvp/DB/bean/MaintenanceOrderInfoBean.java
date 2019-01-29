package com.shinetech.mvp.DB.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;
import org.greenrobot.greendao.DaoException;
import com.shinetech.mvp.DB.greendao.DaoSession;
import com.shinetech.mvp.DB.greendao.MaintenanceOrderInfoBeanDao;
import com.shinetech.mvp.DB.greendao.MaintenanceOrderProgressBeanDao;

/**
 * Created by ljn on 2017-11-10.
 */

@Entity(indexes = {@Index(value = "status ASC,commitTime DESC", unique = true)})
public class MaintenanceOrderInfoBean {

    public static final int COMMIT_STATUS = 0;

    public static final int PROCESSED_STATUS = 1;

    public static final int EVALUATE_STATUS = 2;

    public static final int FINISH_STATUS = 3;

    @Id
    private Long id;

    /**
     * 登录的用户名
     */
    public String userName;

    /**
     * 进度状态
     */
    private int status = 0;

    /**
     * 车牌和颜色
     */
    private String platenumber;

    /**
     * 报修人（申请人）
     */
    private String proposer;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 预约时间
     */
    private String appointmentTime;

    /**
     * 预约地点
     */
    private String appointmentLocation;

    /**
     * 故障描述
     */
    private String  problemDescription;

    /**
     * 图片描述  ( 图片路径jsonArrayString )
     */
    private String problemBitmap;

    /**
     * 工单号
     */
    @Unique
    private String  orderNumber;

    /**
     * 申请时间
     */
    private String commitTime;

    /**
     * 评价信息
     */
    private String valuation;

    /**
     * 选择的评价语 （jsonArrayString）
     */
    private String selectValuation;

    /**
     * 设备状态
     */
    private int terminalStatus;

    /**
     * 车辆状况
     */
    private String carStatus;

    /**
     * 星级
     */
    private float level;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 故障分析
     */
    private String faultAnalyze;

    /**
     * 维修措施
     */
    private String maintenanceMeasures;

    /**
     * 维修结果
     */
//    private String maintenanceResult;

    /**
     * 完成时间
     */
    private String maintenanceFinishTime;

    /**
     * 维修结果图片描述  ( 图片路径jsonArrayString )
     */
    private String maintenanceResultBitmap;

    /**
     * 终端型号
     */
    private String terminalType;

    /**
     * 安装日期
     */
    private String terminalInstallDate;

    /**
     * 车架号
     */
    private String vin;

    @ToMany(joinProperties = {
            @JoinProperty(name = "orderNumber", referencedName = "orderNumber")})
    private List<MaintenanceOrderProgressBean> maintenanceOrderProgresBeen;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2027109156)
    private transient MaintenanceOrderInfoBeanDao myDao;

    public MaintenanceOrderInfoBean() {
    }

    public MaintenanceOrderInfoBean(String userName, String platenumber,
                                    String proposer, String phone, String appointmentTime,
                                    String appointmentLocation, String problemDescription,
                                    String problemBitmap, String commitTime) {
        this.userName = userName;
        this.platenumber = platenumber;
        this.proposer = proposer;
        this.phone = phone;
        this.appointmentTime = appointmentTime;
        this.appointmentLocation = appointmentLocation;
        this.problemDescription = problemDescription;
        this.problemBitmap = problemBitmap;
        this.commitTime = commitTime;
    }

    public MaintenanceOrderInfoBean(String userName, int status,
                                    String platenumber, String proposer, String phone,
                                    String appointmentTime, String appointmentLocation,
                                    String problemDescription, String problemBitmap, String orderNumber,
                                    String commitTime, String valuation, String selectValuation,
                                    float level, String endTime) {
        this.userName = userName;
        this.status = status;
        this.platenumber = platenumber;
        this.proposer = proposer;
        this.phone = phone;
        this.appointmentTime = appointmentTime;
        this.appointmentLocation = appointmentLocation;
        this.problemDescription = problemDescription;
        this.problemBitmap = problemBitmap;
        this.orderNumber = orderNumber;
        this.commitTime = commitTime;
        this.valuation = valuation;
        this.selectValuation = selectValuation;
        this.level = level;
        this.endTime = endTime;
    }

    @Generated(hash = 95226427)
    public MaintenanceOrderInfoBean(Long id, String userName, int status, String platenumber, String proposer, String phone, String appointmentTime, String appointmentLocation,
            String problemDescription, String problemBitmap, String orderNumber, String commitTime, String valuation, String selectValuation, int terminalStatus, String carStatus, float level,
            String endTime, String faultAnalyze, String maintenanceMeasures, String maintenanceFinishTime, String maintenanceResultBitmap, String terminalType, String terminalInstallDate,
            String vin) {
        this.id = id;
        this.userName = userName;
        this.status = status;
        this.platenumber = platenumber;
        this.proposer = proposer;
        this.phone = phone;
        this.appointmentTime = appointmentTime;
        this.appointmentLocation = appointmentLocation;
        this.problemDescription = problemDescription;
        this.problemBitmap = problemBitmap;
        this.orderNumber = orderNumber;
        this.commitTime = commitTime;
        this.valuation = valuation;
        this.selectValuation = selectValuation;
        this.terminalStatus = terminalStatus;
        this.carStatus = carStatus;
        this.level = level;
        this.endTime = endTime;
        this.faultAnalyze = faultAnalyze;
        this.maintenanceMeasures = maintenanceMeasures;
        this.maintenanceFinishTime = maintenanceFinishTime;
        this.maintenanceResultBitmap = maintenanceResultBitmap;
        this.terminalType = terminalType;
        this.terminalInstallDate = terminalInstallDate;
        this.vin = vin;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPlatenumber() {
        return platenumber;
    }

    public void setPlatenumber(String platenumber) {
        this.platenumber = platenumber;
    }

    public String getProposer() {
        return proposer;
    }

    public void setProposer(String proposer) {
        this.proposer = proposer;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentLocation() {
        return appointmentLocation;
    }

    public void setAppointmentLocation(String appointmentLocation) {
        this.appointmentLocation = appointmentLocation;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getProblemBitmap() {
        return problemBitmap;
    }

    public void setProblemBitmap(String problemBitmap) {
        this.problemBitmap = problemBitmap;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(String commitTime) {
        this.commitTime = commitTime;
    }

    public String getValuation() {
        return valuation;
    }

    public void setValuation(String valuation) {
        this.valuation = valuation;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSelectValuation() {
        return selectValuation;
    }

    public void setSelectValuation(String selectValuation) {
        this.selectValuation = selectValuation;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFaultAnalyze() {
        return faultAnalyze;
    }

    public void setFaultAnalyze(String faultAnalyze) {
        this.faultAnalyze = faultAnalyze;
    }

    public String getMaintenanceMeasures() {
        return maintenanceMeasures;
    }

    public void setMaintenanceMeasures(String maintenanceMeasures) {
        this.maintenanceMeasures = maintenanceMeasures;
    }

   /* public String getMaintenanceResult() {
        return maintenanceResult;
    }

    public void setMaintenanceResult(String maintenanceResult) {
        this.maintenanceResult = maintenanceResult;
    }*/

    public String getMaintenanceFinishTime() {
        return maintenanceFinishTime;
    }

    public void setMaintenanceFinishTime(String maintenanceFinishTime) {
        this.maintenanceFinishTime = maintenanceFinishTime;
    }

    public MaintenanceOrderInfoBean copy(){
        MaintenanceOrderInfoBean maintenanceOrderInfoBean = new MaintenanceOrderInfoBean(userName, status, platenumber, proposer, phone, appointmentTime, appointmentLocation, problemDescription,
                problemBitmap, orderNumber, commitTime, valuation, selectValuation, level, endTime);
        maintenanceOrderInfoBean.setFaultAnalyze(faultAnalyze);
        maintenanceOrderInfoBean.setMaintenanceMeasures(maintenanceMeasures);
//        maintenanceOrderInfoBean.setMaintenanceResult(maintenanceResult);
        maintenanceOrderInfoBean.setMaintenanceFinishTime(maintenanceFinishTime);
        return maintenanceOrderInfoBean;
    }

    public String getMaintenanceResultBitmap() {
        return this.maintenanceResultBitmap;
    }

    public void setMaintenanceResultBitmap(String maintenanceResultBitmap) {
        this.maintenanceResultBitmap = maintenanceResultBitmap;
    }

    public String getTerminalType() {
        return this.terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getTerminalInstallDate() {
        return this.terminalInstallDate;
    }

    public void setTerminalInstallDate(String terminalInstallDate) {
        this.terminalInstallDate = terminalInstallDate;
    }

    public String getVin() {
        return this.vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1314867209)
    public List<MaintenanceOrderProgressBean> getMaintenanceOrderProgresBeen() {
        if (maintenanceOrderProgresBeen == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MaintenanceOrderProgressBeanDao targetDao = daoSession.getMaintenanceOrderProgressBeanDao();
            List<MaintenanceOrderProgressBean> maintenanceOrderProgresBeenNew = targetDao._queryMaintenanceOrderInfoBean_MaintenanceOrderProgresBeen(orderNumber);
            synchronized (this) {
                if (maintenanceOrderProgresBeen == null) {
                    maintenanceOrderProgresBeen = maintenanceOrderProgresBeenNew;
                }
            }
        }
        return maintenanceOrderProgresBeen;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1837409779)
    public synchronized void resetMaintenanceOrderProgresBeen() {
        maintenanceOrderProgresBeen = null;
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
    @Generated(hash = 1250890842)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMaintenanceOrderInfoBeanDao() : null;
    }

    public int getTerminalStatus() {
        return this.terminalStatus;
    }

    public void setTerminalStatus(int terminalStatus) {
        this.terminalStatus = terminalStatus;
    }

    public String getCarStatus() {
        return this.carStatus;
    }

    public void setCarStatus(String carStatus) {
        this.carStatus = carStatus;
    }

}
