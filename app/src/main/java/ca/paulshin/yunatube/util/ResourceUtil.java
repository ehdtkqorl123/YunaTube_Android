package ca.paulshin.yunatube.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by paulshin on 16-02-13.
 */
public class ResourceUtil {
	public static Context ctx;

	public static void init(Context context) {
		ctx = context;
	}

	public static int getResourceId(String type, String resource) {
		return ctx.getResources().getIdentifier(resource, type, ctx.getPackageName());
	}

	public static String getString(int resId) {
		return ctx.getString(resId);
	}

	public static String getString(int resId, Object... formatArgs) {
		return ctx.getString(resId, formatArgs);
	}

	public static String[] getStringArray(int resourceId) {
		return ctx.getResources().getStringArray(resourceId);
	}

	public static int getColor(int resId) {
		return ctx.getResources().getColor(resId);
	}

	public static int getInteger(int resId) {
		return ctx.getResources().getInteger(resId);
	}

	public static Drawable getDrawable(int resId) {
		return ctx.getResources().getDrawable(resId);
	}

	public static int getDimensionInPx(int resId) {
		return ctx.getResources().getDimensionPixelOffset(resId);
	}
}
