package com.shinetech.mvp.DB.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.shinetech.mvp.DB.bean.MaintenanceOrderInfoBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MAINTENANCE_ORDER_INFO_BEAN".
*/
public class MaintenanceOrderInfoBeanDao extends AbstractDao<MaintenanceOrderInfoBean, Long> {

    public static final String TABLENAME = "MAINTENANCE_ORDER_INFO_BEAN";

    /**
     * Properties of entity MaintenanceOrderInfoBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserName = new Property(1, String.class, "userName", false, "USER_NAME");
        public final static Property Status = new Property(2, int.class, "status", false, "STATUS");
        public final static Property Platenumber = new Property(3, String.class, "platenumber", false, "PLATENUMBER");
        public final static Property Proposer = new Property(4, String.class, "proposer", false, "PROPOSER");
        public final static Property Phone = new Property(5, String.class, "phone", false, "PHONE");
        public final static Property AppointmentTime = new Property(6, String.class, "appointmentTime", false, "APPOINTMENT_TIME");
        public final static Property AppointmentLocation = new Property(7, String.class, "appointmentLocation", false, "APPOINTMENT_LOCATION");
        public final static Property ProblemDescription = new Property(8, String.class, "problemDescription", false, "PROBLEM_DESCRIPTION");
        public final static Property ProblemBitmap = new Property(9, String.class, "problemBitmap", false, "PROBLEM_BITMAP");
        public final static Property OrderNumber = new Property(10, String.class, "orderNumber", false, "ORDER_NUMBER");
        public final static Property CommitTime = new Property(11, String.class, "commitTime", false, "COMMIT_TIME");
        public final static Property Valuation = new Property(12, String.class, "valuation", false, "VALUATION");
        public final static Property SelectValuation = new Property(13, String.class, "selectValuation", false, "SELECT_VALUATION");
        public final static Property TerminalStatus = new Property(14, int.class, "terminalStatus", false, "TERMINAL_STATUS");
        public final static Property CarStatus = new Property(15, String.class, "carStatus", false, "CAR_STATUS");
        public final static Property Level = new Property(16, float.class, "level", false, "LEVEL");
        public final static Property EndTime = new Property(17, String.class, "endTime", false, "END_TIME");
        public final static Property FaultAnalyze = new Property(18, String.class, "faultAnalyze", false, "FAULT_ANALYZE");
        public final static Property MaintenanceMeasures = new Property(19, String.class, "maintenanceMeasures", false, "MAINTENANCE_MEASURES");
        public final static Property MaintenanceFinishTime = new Property(20, String.class, "maintenanceFinishTime", false, "MAINTENANCE_FINISH_TIME");
        public final static Property MaintenanceResultBitmap = new Property(21, String.class, "maintenanceResultBitmap", false, "MAINTENANCE_RESULT_BITMAP");
        public final static Property TerminalType = new Property(22, String.class, "terminalType", false, "TERMINAL_TYPE");
        public final static Property TerminalInstallDate = new Property(23, String.class, "terminalInstallDate", false, "TERMINAL_INSTALL_DATE");
        public final static Property Vin = new Property(24, String.class, "vin", false, "VIN");
    }

    private DaoSession daoSession;


    public MaintenanceOrderInfoBeanDao(DaoConfig config) {
        super(config);
    }
    
    public MaintenanceOrderInfoBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MAINTENANCE_ORDER_INFO_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"USER_NAME\" TEXT," + // 1: userName
                "\"STATUS\" INTEGER NOT NULL ," + // 2: status
                "\"PLATENUMBER\" TEXT," + // 3: platenumber
                "\"PROPOSER\" TEXT," + // 4: proposer
                "\"PHONE\" TEXT," + // 5: phone
                "\"APPOINTMENT_TIME\" TEXT," + // 6: appointmentTime
                "\"APPOINTMENT_LOCATION\" TEXT," + // 7: appointmentLocation
                "\"PROBLEM_DESCRIPTION\" TEXT," + // 8: problemDescription
                "\"PROBLEM_BITMAP\" TEXT," + // 9: problemBitmap
                "\"ORDER_NUMBER\" TEXT UNIQUE ," + // 10: orderNumber
                "\"COMMIT_TIME\" TEXT," + // 11: commitTime
                "\"VALUATION\" TEXT," + // 12: valuation
                "\"SELECT_VALUATION\" TEXT," + // 13: selectValuation
                "\"TERMINAL_STATUS\" INTEGER NOT NULL ," + // 14: terminalStatus
                "\"CAR_STATUS\" TEXT," + // 15: carStatus
                "\"LEVEL\" REAL NOT NULL ," + // 16: level
                "\"END_TIME\" TEXT," + // 17: endTime
                "\"FAULT_ANALYZE\" TEXT," + // 18: faultAnalyze
                "\"MAINTENANCE_MEASURES\" TEXT," + // 19: maintenanceMeasures
                "\"MAINTENANCE_FINISH_TIME\" TEXT," + // 20: maintenanceFinishTime
                "\"MAINTENANCE_RESULT_BITMAP\" TEXT," + // 21: maintenanceResultBitmap
                "\"TERMINAL_TYPE\" TEXT," + // 22: terminalType
                "\"TERMINAL_INSTALL_DATE\" TEXT," + // 23: terminalInstallDate
                "\"VIN\" TEXT);"); // 24: vin
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_MAINTENANCE_ORDER_INFO_BEAN_STATUS_COMMIT_TIME_DESC ON \"MAINTENANCE_ORDER_INFO_BEAN\"" +
                " (\"STATUS\" ASC,\"COMMIT_TIME\" DESC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MAINTENANCE_ORDER_INFO_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MaintenanceOrderInfoBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(2, userName);
        }
        stmt.bindLong(3, entity.getStatus());
 
        String platenumber = entity.getPlatenumber();
        if (platenumber != null) {
            stmt.bindString(4, platenumber);
        }
 
        String proposer = entity.getProposer();
        if (proposer != null) {
            stmt.bindString(5, proposer);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(6, phone);
        }
 
        String appointmentTime = entity.getAppointmentTime();
        if (appointmentTime != null) {
            stmt.bindString(7, appointmentTime);
        }
 
        String appointmentLocation = entity.getAppointmentLocation();
        if (appointmentLocation != null) {
            stmt.bindString(8, appointmentLocation);
        }
 
        String problemDescription = entity.getProblemDescription();
        if (problemDescription != null) {
            stmt.bindString(9, problemDescription);
        }
 
        String problemBitmap = entity.getProblemBitmap();
        if (problemBitmap != null) {
            stmt.bindString(10, problemBitmap);
        }
 
        String orderNumber = entity.getOrderNumber();
        if (orderNumber != null) {
            stmt.bindString(11, orderNumber);
        }
 
        String commitTime = entity.getCommitTime();
        if (commitTime != null) {
            stmt.bindString(12, commitTime);
        }
 
        String valuation = entity.getValuation();
        if (valuation != null) {
            stmt.bindString(13, valuation);
        }
 
        String selectValuation = entity.getSelectValuation();
        if (selectValuation != null) {
            stmt.bindString(14, selectValuation);
        }
        stmt.bindLong(15, entity.getTerminalStatus());
 
        String carStatus = entity.getCarStatus();
        if (carStatus != null) {
            stmt.bindString(16, carStatus);
        }
        stmt.bindDouble(17, entity.getLevel());
 
        String endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindString(18, endTime);
        }
 
        String faultAnalyze = entity.getFaultAnalyze();
        if (faultAnalyze != null) {
            stmt.bindString(19, faultAnalyze);
        }
 
        String maintenanceMeasures = entity.getMaintenanceMeasures();
        if (maintenanceMeasures != null) {
            stmt.bindString(20, maintenanceMeasures);
        }
 
        String maintenanceFinishTime = entity.getMaintenanceFinishTime();
        if (maintenanceFinishTime != null) {
            stmt.bindString(21, maintenanceFinishTime);
        }
 
        String maintenanceResultBitmap = entity.getMaintenanceResultBitmap();
        if (maintenanceResultBitmap != null) {
            stmt.bindString(22, maintenanceResultBitmap);
        }
 
        String terminalType = entity.getTerminalType();
        if (terminalType != null) {
            stmt.bindString(23, terminalType);
        }
 
        String terminalInstallDate = entity.getTerminalInstallDate();
        if (terminalInstallDate != null) {
            stmt.bindString(24, terminalInstallDate);
        }
 
        String vin = entity.getVin();
        if (vin != null) {
            stmt.bindString(25, vin);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MaintenanceOrderInfoBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(2, userName);
        }
        stmt.bindLong(3, entity.getStatus());
 
        String platenumber = entity.getPlatenumber();
        if (platenumber != null) {
            stmt.bindString(4, platenumber);
        }
 
        String proposer = entity.getProposer();
        if (proposer != null) {
            stmt.bindString(5, proposer);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(6, phone);
        }
 
        String appointmentTime = entity.getAppointmentTime();
        if (appointmentTime != null) {
            stmt.bindString(7, appointmentTime);
        }
 
        String appointmentLocation = entity.getAppointmentLocation();
        if (appointmentLocation != null) {
            stmt.bindString(8, appointmentLocation);
        }
 
        String problemDescription = entity.getProblemDescription();
        if (problemDescription != null) {
            stmt.bindString(9, problemDescription);
        }
 
        String problemBitmap = entity.getProblemBitmap();
        if (problemBitmap != null) {
            stmt.bindString(10, problemBitmap);
        }
 
        String orderNumber = entity.getOrderNumber();
        if (orderNumber != null) {
            stmt.bindString(11, orderNumber);
        }
 
        String commitTime = entity.getCommitTime();
        if (commitTime != null) {
            stmt.bindString(12, commitTime);
        }
 
        String valuation = entity.getValuation();
        if (valuation != null) {
            stmt.bindString(13, valuation);
        }
 
        String selectValuation = entity.getSelectValuation();
        if (selectValuation != null) {
            stmt.bindString(14, selectValuation);
        }
        stmt.bindLong(15, entity.getTerminalStatus());
 
        String carStatus = entity.getCarStatus();
        if (carStatus != null) {
            stmt.bindString(16, carStatus);
        }
        stmt.bindDouble(17, entity.getLevel());
 
        String endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindString(18, endTime);
        }
 
        String faultAnalyze = entity.getFaultAnalyze();
        if (faultAnalyze != null) {
            stmt.bindString(19, faultAnalyze);
        }
 
        String maintenanceMeasures = entity.getMaintenanceMeasures();
        if (maintenanceMeasures != null) {
            stmt.bindString(20, maintenanceMeasures);
        }
 
        String maintenanceFinishTime = entity.getMaintenanceFinishTime();
        if (maintenanceFinishTime != null) {
            stmt.bindString(21, maintenanceFinishTime);
        }
 
        String maintenanceResultBitmap = entity.getMaintenanceResultBitmap();
        if (maintenanceResultBitmap != null) {
            stmt.bindString(22, maintenanceResultBitmap);
        }
 
        String terminalType = entity.getTerminalType();
        if (terminalType != null) {
            stmt.bindString(23, terminalType);
        }
 
        String terminalInstallDate = entity.getTerminalInstallDate();
        if (terminalInstallDate != null) {
            stmt.bindString(24, terminalInstallDate);
        }
 
        String vin = entity.getVin();
        if (vin != null) {
            stmt.bindString(25, vin);
        }
    }

    @Override
    protected final void attachEntity(MaintenanceOrderInfoBean entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public MaintenanceOrderInfoBean readEntity(Cursor cursor, int offset) {
        MaintenanceOrderInfoBean entity = new MaintenanceOrderInfoBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userName
            cursor.getInt(offset + 2), // status
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // platenumber
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // proposer
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // phone
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // appointmentTime
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // appointmentLocation
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // problemDescription
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // problemBitmap
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // orderNumber
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // commitTime
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // valuation
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // selectValuation
            cursor.getInt(offset + 14), // terminalStatus
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // carStatus
            cursor.getFloat(offset + 16), // level
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // endTime
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // faultAnalyze
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // maintenanceMeasures
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // maintenanceFinishTime
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // maintenanceResultBitmap
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // terminalType
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // terminalInstallDate
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24) // vin
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MaintenanceOrderInfoBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setStatus(cursor.getInt(offset + 2));
        entity.setPlatenumber(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setProposer(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPhone(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setAppointmentTime(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setAppointmentLocation(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setProblemDescription(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setProblemBitmap(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setOrderNumber(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setCommitTime(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setValuation(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setSelectValuation(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setTerminalStatus(cursor.getInt(offset + 14));
        entity.setCarStatus(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setLevel(cursor.getFloat(offset + 16));
        entity.setEndTime(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setFaultAnalyze(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setMaintenanceMeasures(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setMaintenanceFinishTime(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setMaintenanceResultBitmap(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setTerminalType(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setTerminalInstallDate(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setVin(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MaintenanceOrderInfoBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MaintenanceOrderInfoBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MaintenanceOrderInfoBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
