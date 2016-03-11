package ca.paulshin.yunatube.data.local;

import android.database.Cursor;

import ca.paulshin.dao.DBVideo;

public class Db {

    public Db() { }

    public abstract static class FaveVideoTable {
        public static final String TABLE_NAME = "VIDEO";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_STITLE = "STITLE";
        public static final String COLUMN_YTITLE = "YTITLE";
        public static final String COLUMN_YTID = "YTID";

        public static String createQuery() {
            return "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_STITLE + " TEXT NOT NULL, " +
                    COLUMN_YTITLE + " TEXT NOT NULL, " +
                    COLUMN_YTID + " TEXT NOT NULL, " +
                    " ); ";
        }

//        public static ContentValues toContentValues(Profile profile) {
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_EMAIL, profile.email);
//            values.put(COLUMN_FIRST_NAME, profile.name.first);
//            values.put(COLUMN_LAST_NAME, profile.name.last);
//            values.put(COLUMN_HEX_COLOR, profile.hexColor);
//            values.put(COLUMN_DATE_OF_BIRTH, profile.dateOfBirth.getTime());
//            values.put(COLUMN_AVATAR, profile.avatar);
//            if (profile.bio != null) values.put(COLUMN_BIO, profile.bio);
//            return values;
//        }
//
        public static DBVideo parseCursor(Cursor cursor) {
            long id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String sTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STITLE));
            String yTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YTITLE));
            String ytid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YTID));

            DBVideo video = new DBVideo(id, sTitle, yTitle, ytid);
            return video;
        }
    }
}
