package ca.paulshin.yunatube;

public class Config {
	public static final String LOG_TAG = "YunaTube";

	public static final String DATABASE_NAME = "YunaTube";
	public static final int DATABASE_VERSION = 4;

	// Is this an internal dogfood build?
	public static final boolean IS_DOGFOOD_BUILD = false;

	// Warning messages for dogfood build
	public static final String DOGFOOD_BUILD_WARNING_TITLE = "Test build";
	public static final String DOGFOOD_BUILD_WARNING_TEXT = "This is a test build.";

	// App setting
	public static final String APP_NAME = "YunaTube";

	// shorthand for some units of time
	public static final long SECOND_MILLIS = 1000;
	public static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
	public static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
	public static final long DAY_MILLIS = 24 * HOUR_MILLIS;

	// Analytics
	public static final String TRACKER_ID = "UA-39447963-1";

	// GCM config
	public static final String GCM_SERVER_PROD_URL = "";
	public static final String GCM_SERVER_URL = "";

	// the GCM sender ID is the ID of the app in Google Cloud Console
	public static final String GCM_SENDER_ID = "848643795806";
	public static final String GCM_SENDER_REGID_URL = "http://paulshin.ca/yunatube/mobile/php/android.php?action=insert&rid=";

	// API URLs
	// Common
	public static final String BASE_URL_HTTP = "http://paulshin.ca";
	public static final String VIDEO_THUMBNAIL_URL = "http://i.ytimg.com/vi/%s/2.jpg";
	public static final String VIDEO_HQ_THUMBNAIL_URL = "http://i.ytimg.com/vi/%s/hqdefault.jpg";

	// Main
	public static final String YUNAFACT = "https://twitter.com/YunaKim_Facts";

	// Web Search
	public static final String SEARCH_URL_YOUTUBE = "http://m.youtube.com/results?gl=RU&client=mv-google&hl=en&q=%s&submit=Search";
	public static final String SEARCH_URL_GOOGLE = "https://www.google.com/?q=%s#hl=en&newwindow=1&output=search&sclient=psy-ab&q=%s&oq=%s";
	public static final String SEARCH_URL_DAUM = "http://m.search.daum.net/search?w=tot&nil_mtopsearch=btn&q=%s";
	public static final String SEARCH_URL_NAVER = "http://m.search.naver.com/search.naver?query=%s&where=m&sm=mtp_hty";
	public static final String SEARCH_URL_BING = "http://m.bing.com/search?q=%s&FORM=BLXBSS&btsrc=internal";
	public static final String SEARCH_URL_NATE = "http://m.search.nate.com/search/all.html?q=%s";

	public static final String NEWS_URL_GOOGLE = "https://www.google.ca/search?q=%s&tbm=nws";
	public static final String NEWS_URL_DAUM = "http://m.search.daum.net/search?w=news&q=%s&sort=3&cluster=n";
	public static final String NEWS_URL_NAVER = "http://m.news.naver.com/search.nhn?searchType=issue&searchQuery=%s";
	public static final String NEWS_URL_NATE = "http://m.search.nate.com/search/all.html?q=%s&ssn=036";
	public static final String NEWS_URL_BING = "http://www.bing.com/news/search?q=%s&FORM=HDRSC6";

	// GIF
	public static final String GIF_THUMBNAIL_URL = "http://paulshin.ca/yunatube/mobile/php/gifs/get_thumbnail.php?src=../../images/gifs/%s&w=%d&h=%d&hash=%d";
	public static final String GIF_URL = "http://paulshin.ca/yunatube/mobile/images/gifs/%s";

	// Survey
	public static final String SURVEY_URL = "http://paulshin.ca/yunatube/res/survey/index.php?deviceId=";

	// YouTube
	public static final String YOUTUBE_DEVELOPER_KEY = "AIzaSyB4fv0_D1_ZYuQxD6uFK7K3D6oHbVlxIi4";

	// YouTube share URL
	public static final String YOUTUBE_SHARE_URL_PREFIX = "http://youtu.be/%s";

	// Quiz
	public static final String QUIZ_RANKING = "http://paulshin.ca/yunatube/res/quiz/quiz_ranking.php?version=";

	// Game
	public static final String GAME_RANKING = "http://paulshin.ca/yunatube/res/game/game_ranking_2.php?nickname=%s#%s";
	public static final String GAME_SUBMIT = "http://paulshin.ca/yunatube/res/game/game_submit_2.php";

	// Chat
	public static final String CHAT_USERS = "http://paulshin.ca/yunatube/res/chat/chat_users.php";
	public static final String CHAT_DATA = "http://paulshin.ca/yunatube/res/chat/chat_data.php";

	// Photo
	public static final String TEMP_PREFIX = "temp_";

	// Instagram
	public static final String BASE_INSTA_URL_HTTP = "https://api.instagram.com";
	public static final String INSTAGRAM_CLIENT_ID = "ccea607cd56f4a4e8ce4f8b4abd0c7ee";
	public static final String INSTAGRAM_CLIENT_SECRET = "055b6bf56fd849a094ac67cb50781f72";
	public static final String INSTAGRAM_LOVEQYN = "221622328";
	public static final String INSTAGRAM_LOVEQYN_URL = "http://instagram.com/loveqyn";
	// https://api.instagram.com/v1/tags/yunakim/media/recent?client_id=ccea607cd56f4a4e8ce4f8b4abd0c7ee

	// Flickr
	public static final String FLICKR_API_KEY = "044ffe6c8bf9e9c99b512f195d45a628";
	public static final String BASE_FLICKR_URL_HTTPS = "https://api.flickr.com";
	public static final String FLICKR_USER_ID = "52789087@N05";
	public static final String FLICKR_JSON_FORMAT = "json";
	public static final String FLICKR_TYPE_COLLECTION = "flickr.collections.getTree";
	public static final String FLICKR_TYPE_LIST = "flickr.photosets.getPhotos";
	public static final String FLICKR_PER_PAGE = "500";
	public static final String FLICKR_FORMAT = "https://farm%s.staticflickr.com/%s/%s_%s_%s.jpg";

	// Yuna
	public static final String YUNA_ISU_RESULTS_URL = "http://www.isuresults.com/bios/isufs_cr_00007232.htm";
	public static final String Q20A20_URL = "http://paulshin.ca/yunatube/mobile/html/20q20a_%s.html";
	public static final String PROGRAMS_URL = "http://paulshin.ca/yunatube/mobile/html/programs.html";
	public static final String COMPETITIONS_URL = "http://paulshin.ca/yunatube/mobile/html/competitions_%s.html";
	public static final String PRAISES_URL = "http://paulshin.ca/yunatube/mobile/html/praises_%s.html";
	public static final String AWARDS_URL = "http://paulshin.ca/yunatube/mobile/html/awards_%s.html";
	public static final String CHARITIES_URL = "http://paulshin.ca/yunatube/mobile/html/charities_%s.html";

	// Push Notification
	public static final String MARKET_URL = "market://details?id=ca.paulshin.yunatube";

	// Etc.
	public static final String TOSEUNG_URL = "http://paulshin.ca/yunatube/mobile/images/etc/toseung.jpg";
}
