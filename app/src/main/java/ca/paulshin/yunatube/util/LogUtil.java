package ca.paulshin.yunatube.util;

import ca.paulshin.yunatube.BuildConfig;
import okhttp3.logging.HttpLoggingInterceptor;

public class LogUtil {
	public static HttpLoggingInterceptor.Level getNetworkLogLevel() {
		HttpLoggingInterceptor.Level result;

		if (BuildConfig.DEBUG) {
			result = HttpLoggingInterceptor.Level.BODY;
		} else {
			result = HttpLoggingInterceptor.Level.NONE;
		}

		return result;
	}
}
