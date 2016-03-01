package ca.paulshin.yunatube.data;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.local.DatabaseHelper;
import ca.paulshin.yunatube.data.local.PreferencesHelper;
import ca.paulshin.yunatube.data.model.flickr.CollectionsObject;
import ca.paulshin.yunatube.data.model.flickr.PhotosetObject;
import ca.paulshin.yunatube.data.model.flickr.Stream;
import ca.paulshin.yunatube.data.model.instagram.Feed;
import ca.paulshin.yunatube.data.model.main.Notice;
import ca.paulshin.yunatube.data.model.message.Message;
import ca.paulshin.yunatube.data.model.video.Section;
import ca.paulshin.yunatube.data.model.video.SimpleResult;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.data.remote.FlickrService;
import ca.paulshin.yunatube.data.remote.InstaService;
import ca.paulshin.yunatube.data.remote.YunaTubeService;
import ca.paulshin.yunatube.util.EventPosterHelper;
import ca.paulshin.yunatube.util.LanguageUtil;
import ca.paulshin.yunatube.util.ResourceUtil;
import rx.Observable;
import rx.functions.Action0;

@Singleton
public class DataManager {

    private final YunaTubeService mYunaTubeService;
    private final InstaService mInstaService;
	private final FlickrService mFlickrService;
    private final DatabaseHelper mDatabaseHelper;
    private final PreferencesHelper mPreferencesHelper;
    private final EventPosterHelper mEventPoster;

    @Inject
    public DataManager(YunaTubeService yunaTubeService, InstaService instaService,
					   FlickrService flickrService,
                       PreferencesHelper preferencesHelper, DatabaseHelper databaseHelper,
                       EventPosterHelper eventPosterHelper) {
        mYunaTubeService = yunaTubeService;
        mInstaService = instaService;
		mFlickrService = flickrService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
        mEventPoster = eventPosterHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

//    public Observable<Ribot> syncRibots() {
//        return mRibotsService.getRibots()
//                .concatMap(new Func1<List<Ribot>, Observable<Ribot>>() {
//                    @Override
//                    public Observable<Ribot> call(List<Ribot> ribots) {
//                        return mDatabaseHelper.setRibots(ribots);
//                    }
//                });
//    }
//
//    public Observable<List<Ribot>> getRibots() {
////        return mDatabaseHelper.getRibots().distinct();
//        return mRibotsService.getRibots();
//    }

    public Observable<Notice> getNotice(String lang, int random) {
        return mYunaTubeService.getNotice(lang, random);
    }

	public Observable<Feed> getInstaFeed(String nextMaxId, String instaLoadCount) {
		Map<String, String> options = new HashMap<>();
		options.put("count", String.valueOf(instaLoadCount));
		options.put("client_id", Config.INSTAGRAM_CLIENT_ID);
		if (!TextUtils.isEmpty(nextMaxId)) {
			options.put("max_id", nextMaxId);
		}

		return mInstaService.getFeedItemByUser(Config.INSTAGRAM_LOVEQYN, options);
	}

    public Observable<List<Video>> getNewVideos(String lastNewOrder) {
        Map<String, String> options = new HashMap<>();
		int videoLoadCount = ResourceUtil.getInteger(R.integer.video_load_count);
        options.put("lo", LanguageUtil.getLangCode());
        options.put("count", String.valueOf(videoLoadCount));
        if (!TextUtils.isEmpty(lastNewOrder)) {
            options.put("lastNewOrder", lastNewOrder);
        }

        return mYunaTubeService.getNewVideos(options);
    }

	public Observable<List<Video>> getSearchVideos(String query) {
		Map<String, String> options = new HashMap<>();
		options.put("q", query);
		options.put("lo", LanguageUtil.getLangCode());

		return mYunaTubeService.getSearchVideos(options);
	}

	public Observable<List<Message>> getMessages(String nextMaxId) {
		Map<String, String> options = new HashMap<>();
		int messageLoadCount = ResourceUtil.getInteger(R.integer.message_load_count);
		options.put("numOfMessages", String.valueOf(messageLoadCount));
		if (!TextUtils.isEmpty(nextMaxId)) {
			options.put("lastIndex", nextMaxId);
		}

		return mYunaTubeService.getMessages(options);
	}

	public Observable<SimpleResult> submitMessage(String username, String message, String deviceId) {
		String time = String.valueOf(System.currentTimeMillis() / 1000);

		Map<String, String> options = new HashMap<>();
		options.put("username", username);
		options.put("message", message);
		options.put("time", time);
		options.put("device", deviceId);
		options.put("report", "0");

		return mYunaTubeService.submitMessage(options);
	}

	public Observable<List<Video>> getRandomVideo() {
		String locationCode = LanguageUtil.getLangCode();

		return mYunaTubeService.getRandomVideo(locationCode);
	}

	public Observable<Stream> getStream() {
		Map<String, String> options = new HashMap<>();
		options.put("id", Config.FLICKR_USER_ID);
		options.put("format", Config.FLICKR_JSON_FORMAT);
		options.put("nojsoncallback", "1");

		return mFlickrService.getStream(options);
	}

	public Observable<CollectionsObject> getCollections() {
		Map<String, String> options = new HashMap<>();
		options.put("method", Config.FLICKR_TYPE_COLLECTION);
		options.put("api_key", Config.FLICKR_API_KEY);
		options.put("user_id", Config.FLICKR_USER_ID);
		options.put("format", Config.FLICKR_JSON_FORMAT);
		options.put("nojsoncallback", "1");

		return mFlickrService.getCollections(options);
	}

	public Observable<List<String>> getGifList() {
		return mYunaTubeService.getGifList();
	}

	public Observable<PhotosetObject> getPhotoList(String setId) {
		Map<String, String> options = new HashMap<>();
		options.put("method", Config.FLICKR_TYPE_LIST);
		options.put("api_key", Config.FLICKR_API_KEY);
		options.put("user_id", Config.FLICKR_USER_ID);
		options.put("format", Config.FLICKR_JSON_FORMAT);
		options.put("photoset_id", setId);
		options.put("per_page", Config.FLICKR_PER_PAGE);
		options.put("nojsoncallback", "1");

		return mFlickrService.getPhotoList(options);
	}

	public Observable<List<Section>> getSections(String cid) {
		String order = (TextUtils.equals(cid, "1") || TextUtils.equals(cid, "2") || TextUtils.equals(cid, "3")) ? "desc" : "asc";

		Map<String, String> options = new HashMap<>();
		options.put("cid", cid);
		options.put("order", "asc");
		options.put("lo", LanguageUtil.getLangCode());

		return mYunaTubeService.getSections(options);
	}

	public Observable<List<Video>> getVideos(String cid, String sid) {
		String order = (TextUtils.equals(cid, "2") || TextUtils.equals(cid, "4") || TextUtils.equals(cid, "6")) ? "desc" : "asc";

		Map<String, String> options = new HashMap<>();
		options.put("cid", cid);
		options.put("sid", sid);
		options.put("order", order);
		options.put("lo", LanguageUtil.getLangCode());

		return mYunaTubeService.getVideos(options);
	}

    /// Helper method to post events from doOnCompleted.
    private Action0 postEventAction(final Object event) {
        return new Action0() {
            @Override
            public void call() {
                mEventPoster.postEventSafely(event);
            }
        };
    }
}
