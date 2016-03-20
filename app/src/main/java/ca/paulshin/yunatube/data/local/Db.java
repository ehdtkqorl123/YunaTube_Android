package ca.paulshin.yunatube.data.local;

import android.content.ContentValues;
import android.database.Cursor;

import ca.paulshin.yunatube.data.model.video.Video;

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

        public static ContentValues toContentValues(Video video) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_STITLE, video.stitle);
            values.put(COLUMN_YTITLE, video.ytitle);
            values.put(COLUMN_YTID, video.ytid);
            return values;
        }

        public static Video parseCursor(Cursor cursor) {
            long id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String sTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STITLE));
            String yTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YTITLE));
            String ytid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YTID));

            Video video = new Video(id, sTitle, yTitle, ytid);
            return video;
        }
    }
}
