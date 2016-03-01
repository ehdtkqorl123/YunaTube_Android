package ca.paulshin.yunatube.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

/**
 * Created by paulshin on 16-02-12.
 */
public class UIUtil {
	public static boolean isTablet(Context ctx) {
		return ctx.getResources().getConfiguration().smallestScreenWidthDp >= 600;
	}

	public static int getNavBarHeight(Context ctx) {
		Resources resources = ctx.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			return resources.getDimensionPixelSize(resourceId);
		}
		return 0;
	}

	public static int getStatusBarHeight(Context ctx) {
		Resources resources = ctx.getResources();
		int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			return resources.getDimensionPixelSize(resourceId);
		}
		return 0;
	}

	public static int getActionbarHeight(Context ctx) {
		// Calculate ActionBar height
		TypedValue tv = new TypedValue();
		int height = 0;
		if (ctx.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
			height = TypedValue.complexToDimensionPixelSize(tv.data, ctx.getResources().getDisplayMetrics());
		return height;
	}

	public static boolean hasSoftSysBar(Context ctx) {
		boolean hasMenuKey = ViewConfiguration.get(ctx).hasPermanentMenuKey();
		boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
		return !hasBackKey && !hasBackKey;
	}
}
