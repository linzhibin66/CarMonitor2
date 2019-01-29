package com.shinetech.mvp.DB.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.shinetech.mvp.DB.bean.CarInfoDB;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CAR_INFO_DB".
*/
public class CarInfoDBDao extends AbstractDao<CarInfoDB, Void> {

    public static final String TABLENAME = "CAR_INFO_DB";

    /**
     * Properties of entity CarInfoDB.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property UserName = new Property(0, String.class, "userName", false, "USER_NAME");
        public final static Property PlateNumber = new Property(1, String.class, "plateNumber", false, "PLATE_NUMBER");
        public final static Property LocationTime = new Property(2, String.class, "locationTime", false, "LOCATION_TIME");
        public final static Property Lng = new Property(3, int.class, "lng", false, "LNG");
        public final static Property Lat = new Property(4, int.class, "lat", false, "LAT");
        public final static Property Speed = new Property(5, byte.class, "speed", false, "SPEED");
        public final static Property GNSSSpeed = new Property(6, byte.class, "gNSSSpeed", false, "GNSS_SPEED");
        public final static Property Orientation = new Property(7, short.class, "orientation", false, "ORIENTATION");
        public final static Property Altitude = new Property(8, short.class, "altitude", false, "ALTITUDE");
        public final static Property Mileage = new Property(9, int.class, "mileage", false, "MILEAGE");
        public final static Property OilMass = new Property(10, short.class, "oilMass", false, "OILMASS");
        public final static Property Status = new Property(11, int.class, "status", false, "STATUS");
        public final static Property AlarmType = new Property(12, int.class, "alarmType", false, "ALARM_TYPE");
        public final static Property ViolationCount = new Property(13, byte.class, "violationCount", false, "VIOLATION_COUNT");
        public final static Property ViolationList = new Property(14, String.class, "violationList", false, "VIOLATION_LIST");
    }


    public CarInfoDBDao(DaoConfig config) {
        super(config);
    }
    
    public CarInfoDBDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CAR_INFO_DB\" (" + //
                "\"USER_NAME\" TEXT NOT NULL ," + // 0: userName
                "\"PLATE_NUMBER\" TEXT," + // 1: plateNumber
                "\"LOCATION_TIME\" TEXT," + // 2: locationTime
                "\"LNG\" INTEGER NOT NULL ," + // 3: lng
                "\"LAT\" INTEGER NOT NULL ," + // 4: lat
                "\"SPEED\" INTEGER NOT NULL ," + // 5: speed
                "\"GNSS_SPEED\" INTEGER NOT NULL ," + // 6: gNSSSpeed
                "\"ORIENTATION\" INTEGER NOT NULL ," + // 7: orientation
                "\"ALTITUDE\" INTEGER NOT NULL ," + // 8: altitude
                "\"MILEAGE\" INTEGER NOT NULL ," + // 9: mileage
                "\"OILMASS\" INTEGER NOT NULL ," + // 10: oilMass
                "\"STATUS\" INTEGER NOT NULL ," + // 11: status
                "\"ALARM_TYPE\" INTEGER NOT NULL ," + // 12: alarmType
                "\"VIOLATION_COUNT\" INTEGER NOT NULL ," + // 13: violationCount
                "\"VIOLATION_LIST\" TEXT);"); // 14: violationList
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_CAR_INFO_DB_USER_NAME_PLATE_NUMBER_DESC ON \"CAR_INFO_DB\"" +
                " (\"USER_NAME\" ASC,\"PLATE_NUMBER\" DESC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CAR_INFO_DB\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, CarInfoDB entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getUserName());
 
        String plateNumber = entity.getPlateNumber();
        if (plateNumber != null) {
            stmt.bindString(2, plateNumber);
        }
 
        String locationTime = entity.getLocationTime();
        if (locationTime != null) {
            stmt.bindString(3, locationTime);
        }
        stmt.bindLong(4, entity.getLng());
        stmt.bindLong(5, entity.getLat());
        stmt.bindLong(6, entity.getSpeed());
        stmt.bindLong(7, entity.getGNSSSpeed());
        stmt.bindLong(8, entity.getOrientation());
        stmt.bindLong(9, entity.getAltitude());
        stmt.bindLong(10, entity.getMileage());
        stmt.bindLong(11, entity.getOilMass());
        stmt.bindLong(12, entity.getStatus());
        stmt.bindLong(13, entity.getAlarmType());
        stmt.bindLong(14, entity.getViolationCount());
 
        String violationList = entity.getViolationList();
        if (violationList != null) {
            stmt.bindString(15, violationList);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, CarInfoDB entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getUserName());
 
        String plateNumber = entity.getPlateNumber();
        if (plateNumber != null) {
            stmt.bindString(2, plateNumber);
        }
 
        String locationTime = entity.getLocationTime();
        if (locationTime != null) {
            stmt.bindString(3, locationTime);
        }
        stmt.bindLong(4, entity.getLng());
        stmt.bindLong(5, entity.getLat());
        stmt.bindLong(6, entity.getSpeed());
        stmt.bindLong(7, entity.getGNSSSpeed());
        stmt.bindLong(8, entity.getOrientation());
        stmt.bindLong(9, entity.getAltitude());
        stmt.bindLong(10, entity.getMileage());
        stmt.bindLong(11, entity.getOilMass());
        stmt.bindLong(12, entity.getStatus());
        stmt.bindLong(13, entity.getAlarmType());
        stmt.bindLong(14, entity.getViolationCount());
 
        String violationList = entity.getViolationList();
        if (violationList != null) {
            stmt.bindString(15, violationList);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public CarInfoDB readEntity(Cursor cursor, int offset) {
        CarInfoDB entity = new CarInfoDB( //
            cursor.getString(offset + 0), // userName
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // plateNumber
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // locationTime
            cursor.getInt(offset + 3), // lng
            cursor.getInt(offset + 4), // lat
            (byte) cursor.getShort(offset + 5), // speed
            (byte) cursor.getShort(offset + 6), // gNSSSpeed
            cursor.getShort(offset + 7), // orientation
            cursor.getShort(offset + 8), // altitude
            cursor.getInt(offset + 9), // mileage
            cursor.getShort(offset + 10), // oilMass
            cursor.getInt(offset + 11), // status
            cursor.getInt(offset + 12), // alarmType
            (byte) cursor.getShort(offset + 13), // violationCount
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14) // violationList
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, CarInfoDB entity, int offset) {
        entity.setUserName(cursor.getString(offset + 0));
        entity.setPlateNumber(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setLocationTime(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setLng(cursor.getInt(offset + 3));
        entity.setLat(cursor.getInt(offset + 4));
        entity.setSpeed((byte) cursor.getShort(offset + 5));
        entity.setGNSSSpeed((byte) cursor.getShort(offset + 6));
        entity.setOrientation(cursor.getShort(offset + 7));
        entity.setAltitude(cursor.getShort(offset + 8));
        entity.setMileage(cursor.getInt(offset + 9));
        entity.setOilMass(cursor.getShort(offset + 10));
        entity.setStatus(cursor.getInt(offset + 11));
        entity.setAlarmType(cursor.getInt(offset + 12));
        entity.setViolationCount((byte) cursor.getShort(offset + 13));
        entity.setViolationList(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(CarInfoDB entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(CarInfoDB entity) {
        return null;
    }

    @Override
    public boolean hasKey(CarInfoDB entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}