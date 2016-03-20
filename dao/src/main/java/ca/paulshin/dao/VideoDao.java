package ca.paulshin.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table VIDEO.
*/
public class VideoDao extends AbstractDao<DBVideo, Long> {

    public static final String TABLENAME = "VIDEO";

    /**
     * Properties of entity Video.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Stitle = new Property(1, String.class, "stitle", false, "STITLE");
        public final static Property Ytitle = new Property(2, String.class, "ytitle", false, "YTITLE");
        public final static Property Ytid = new Property(3, String.class, "ytid", false, "YTID");
    };


    public VideoDao(DaoConfig config) {
        super(config);
    }
    
    public VideoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'VIDEO' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'STITLE' TEXT NOT NULL ," + // 1: stitle
                "'YTITLE' TEXT NOT NULL ," + // 2: ytitle
                "'YTID' TEXT NOT NULL );"); // 3: ytid
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'VIDEO'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DBVideo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getStitle());
        stmt.bindString(3, entity.getYtitle());
        stmt.bindString(4, entity.getYtid());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public DBVideo readEntity(Cursor cursor, int offset) {
        DBVideo entity = new DBVideo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // stitle
            cursor.getString(offset + 2), // ytitle
            cursor.getString(offset + 3) // ytid
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DBVideo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setStitle(cursor.getString(offset + 1));
        entity.setYtitle(cursor.getString(offset + 2));
        entity.setYtid(cursor.getString(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DBVideo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(DBVideo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}