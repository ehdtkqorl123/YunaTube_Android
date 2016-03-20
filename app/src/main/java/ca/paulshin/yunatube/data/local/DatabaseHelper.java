package ca.paulshin.yunatube.data.local;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.paulshin.yunatube.data.model.video.Video;
import rx.Observable;
import rx.Subscriber;

@Singleton
public class DatabaseHelper {
    private static final String SELECT_ALL_QUERY = "SELECT * FROM " + Db.FaveVideoTable.TABLE_NAME;
    private static final String FAVE_ID_QUERY = "SELECT " + Db.FaveVideoTable.COLUMN_ID + " FROM "
            + Db.FaveVideoTable.TABLE_NAME
            + " WHERE "
            + Db.FaveVideoTable.COLUMN_YTID
            + " = ?";

    private final BriteDatabase mDb;

    @Inject
    public DatabaseHelper(DbOpenHelper dbOpenHelper) {
        mDb = SqlBrite.create().wrapDatabaseHelper(dbOpenHelper);
    }

    public BriteDatabase getBriteDb() {
        return mDb;
    }

    /**
     * Remove all the data from all the tables in the database.
     */
    public Observable<Void> clearTables() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                BriteDatabase.Transaction transaction = mDb.newTransaction();
                try {
                    Cursor cursor = mDb.query("SELECT name FROM sqlite_master WHERE type='table'");
                    while (cursor.moveToNext()) {
                        mDb.delete(cursor.getString(cursor.getColumnIndex("name")), null);
                    }
                    cursor.close();
                    transaction.markSuccessful();
                    subscriber.onCompleted();
                } finally {
                    transaction.end();
                }
            }
        });
    }

    public Observable<List<Video>> getMyFaves() {
        return mDb.createQuery(Db.FaveVideoTable.TABLE_NAME, SELECT_ALL_QUERY)
                .mapToList(Db.FaveVideoTable::parseCursor);
    }

    public Observable<Integer> getMyFaveKey(String ytid) {
        return mDb.createQuery(Db.FaveVideoTable.TABLE_NAME, FAVE_ID_QUERY, ytid)
                .first()
                .map((query) -> {
                    Cursor cursor = query.run();
                    try {
                        if (!cursor.moveToNext()) {
                            return -1;
                        }
                        return cursor.getInt(0);
                    } finally {
                        cursor.close();
                    }
                });
    }

    public Observable<Video> insert(Video video) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            BriteDatabase.Transaction transaction = mDb.newTransaction();
            try {
                long result = mDb.insert(Db.FaveVideoTable.TABLE_NAME,
                        Db.FaveVideoTable.toContentValues(video),
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (result >= 0) subscriber.onNext(video);
                transaction.markSuccessful();
                subscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<Integer> delete(Integer key) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;

            int result = mDb.delete(Db.FaveVideoTable.TABLE_NAME, Db.FaveVideoTable.COLUMN_ID + "=" + key);
            subscriber.onNext(result);
        });
    }
}
