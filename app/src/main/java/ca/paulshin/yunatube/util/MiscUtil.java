package ca.paulshin.yunatube.util;

import android.text.TextUtils;

import ca.paulshin.yunatube.R;

/**
 * Created by paulshin on 16-02-21.
 */
public class MiscUtil {
	public static String getLang() {
		return ResourceUtil.getString(R.string.lang);
	}

	public static int getRandomInt() {
		return (int)(Math.random() * 1000000);
	}

	/**
	 * Return created time in "** ago" format with given timestamp
	 * @param timestamp
	 * @return
	 */
	public static String getCreatedTime(String timestamp) {
		Long currentTime = System.currentTimeMillis() / 1000;
		Long prevTime = Long.parseLong(((TextUtils.isEmpty(timestamp)) ? String.valueOf(currentTime) : timestamp));
		StringBuilder sb = new StringBuilder();

		Long interval = currentTime - prevTime;
		if (interval < 60) {
			sb.append(interval).append(ResourceUtil.getString(R.string.created_second));
		} else if (interval < 3600) {
			sb.append((int) (interval / 60)).append(ResourceUtil.getString(R.string.created_minute));
		} else if (interval < 86400) {
			sb.append((int) (interval / 3600)).append(ResourceUtil.getString(R.string.created_hour));
		} else if (interval < 2592000) {
			sb.append((int) (interval / 86400)).append(ResourceUtil.getString(R.string.created_day));
		} else if (interval < 80352000) {
			sb.append((int) (interval / 2592000)).append(ResourceUtil.getString(R.string.created_month));
		} else {
			sb.append((int) (interval / 964224000)).append(ResourceUtil.getString(R.string.created_year));
		}

		return sb.toString();
	}
}
