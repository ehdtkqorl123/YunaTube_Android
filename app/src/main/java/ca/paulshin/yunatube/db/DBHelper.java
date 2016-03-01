package ca.paulshin.yunatube.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import ca.paulshin.dao.DaoMaster;
import ca.paulshin.yunatube.Config;

/**
 * Created by paulshin on 14-12-15.
 */
public class DBHelper {
	private static Context sCtx;
	private static SQLiteDatabase sDb;

	public static void init(Context ctx) {
		sCtx = ctx;

		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ctx, Config.DATABASE_NAME, null);
		sDb = helper.getWritableDatabase();
	}
	public static DaoMaster getDaoMaster() {
		return new DaoMaster(sDb);
	}
}
