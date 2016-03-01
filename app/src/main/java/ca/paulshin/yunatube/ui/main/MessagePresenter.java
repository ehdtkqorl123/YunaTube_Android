package ca.paulshin.yunatube.ui.main;

import android.text.TextUtils;

import java.util.List;

import javax.inject.Inject;

import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.data.model.message.Message;
import ca.paulshin.yunatube.data.model.video.SimpleResult;
import ca.paulshin.yunatube.ui.base.BasePresenter;
import ca.paulshin.yunatube.util.CollectionUtil;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by paulshin on 16-02-22.
 */
public class MessagePresenter extends BasePresenter<MessageMvpView> {

	private final static String SUCCESS_CODE = "success";
	private final DataManager mDataManager;
	private Subscription mSubscription;

	@Inject
	public MessagePresenter(DataManager dataManager) {
		mDataManager = dataManager;
	}

	@Override
	public void attachView(MessageMvpView mvpView) {
		super.attachView(mvpView);
	}

	@Override
	public void detachView() {
		super.detachView();
		if (mSubscription != null) mSubscription.unsubscribe();
	}

	public void getMessages(String nextMaxId) {
		checkViewAttached();

		mSubscription = mDataManager.getMessages(nextMaxId)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe(new Subscriber<List<Message>>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						Timber.e(e, "There was an error loading the messages.");
						getMvpView().showError();
					}

					@Override
					public void onNext(List<Message> messages) {
						if (!CollectionUtil.isEmpty(messages)) {
							getMvpView().showMessages(messages);
						}
					}
				});
	}

	public void submitMessage(String username, String message, String deviceId) {
		checkViewAttached();

		mSubscription = mDataManager.submitMessage(username, message, deviceId)
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
						if (TextUtils.equals(result.result, SUCCESS_CODE)) {
							String id = "0";
							String time = String.valueOf(System.currentTimeMillis() / 1000);
							String report = "0";

							Message newMessage = new Message(id, username, message, time, deviceId, report);
							getMvpView().updateMessages(newMessage);
						} else {
							getMvpView().showError();
						}
					}
				});
	}
}
