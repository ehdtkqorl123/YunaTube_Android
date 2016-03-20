package ca.paulshin.yunatube.ui.main;

import android.text.TextUtils;

import java.util.List;

import javax.inject.Inject;

import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.data.model.video.Comment;
import ca.paulshin.yunatube.data.model.video.SimpleResult;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.base.BasePresenter;
import ca.paulshin.yunatube.util.ToastUtil;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by paulshin on 16-02-22.
 */
public class VideoPresenter extends BasePresenter<VideoMvpView> {

	private final static String SUCCESS_CODE = "success";
	private final DataManager mDataManager;
	private Subscription mSubscription;

	@Inject
	public VideoPresenter(DataManager dataManager) {
		mDataManager = dataManager;
	}

	@Override
	public void attachView(VideoMvpView mvpView) {
		super.attachView(mvpView);
	}

	@Override
	public void detachView() {
		super.detachView();
		if (mSubscription != null) mSubscription.unsubscribe();
	}

	public void getVideo(String ytid) {
		checkViewAttached();

		mSubscription = mDataManager.getVideo(ytid)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(new Subscriber<Video>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						Timber.e(e, "There was an error loading the messages.");
						getMvpView().showError();
					}

					@Override
					public void onNext(Video video) {
						if (video != null) {
							getMvpView().showVideo(video);
						}
					}
				});
	}

	public void getComments(String ytid, String lastIndex) {
		checkViewAttached();

		mSubscription = mDataManager.getComments(ytid, lastIndex)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(new Subscriber<List<Comment>>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						Timber.e(e, "There was an error loading the messages.");
						getMvpView().showError();
					}

					@Override
					public void onNext(List<Comment> comments) {
						getMvpView().showComments(comments);
					}
				});
	}

	public void report(String ytid) {
		checkViewAttached();

		mSubscription = mDataManager.report(ytid)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(new Subscriber<SimpleResult>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						Timber.e(e, "There was an error loading the messages.");
						getMvpView().showError();
					}

					@Override
					public void onNext(SimpleResult result) {
						if (result != null) {
							getMvpView().report(result);
						}
					}
				});
	}

	public void submitComment(String ytid, String username, String comment, String time, String deviceId) {
		mSubscription = mDataManager.submitComment(ytid, username, comment, time, deviceId)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(new Subscriber<SimpleResult>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						Timber.e(e, "There was an error loading the messages.");
						getMvpView().showError();
					}

					@Override
					public void onNext(SimpleResult result) {
						if (result != null && TextUtils.equals(result.result, SUCCESS_CODE)) {
							String id = "0";
							String time = String.valueOf(System.currentTimeMillis() / 1000);
							String report = "0";
							String isfirst = "0";

							Comment newComment = new Comment(id, report, username, comment, time, deviceId, isfirst);
							getMvpView().updateComment(newComment);
						} else {
							getMvpView().showError();
						}
					}
				});
	}

	public void getFaveStatus(String ytid) {
		mSubscription = mDataManager.getMyFaveKey(ytid)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe((id) -> {
					getMvpView().setFaveStatus(id);
				});

	}

	public void addFave(Video video) {
		mSubscription = mDataManager.insertFave(video)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe((dbVideo) -> {
					getMvpView().addedFave(dbVideo);
				});
	}

	public void deleteFave(int key) {
		mSubscription = mDataManager.deleteFaveByKey(key)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.subscribe((row) -> {
				getMvpView().deletedFave(row);
			});
	}
}
