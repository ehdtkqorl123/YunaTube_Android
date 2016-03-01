package ca.paulshin.yunatube.util;

import java.util.Locale;

/**
 * Created by paulshin on 16-02-12.
 */
public class LanguageUtil {
	public static boolean isKorean() {
		boolean isKorean = Locale.getDefault().getLanguage().startsWith("ko");
		return isKorean;
	}

	public static String getLangCode() {
		return isKorean() ? "2" : "1";
	}
}
