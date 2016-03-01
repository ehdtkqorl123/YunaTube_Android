package ca.paulshin.yunatube.ui.main;

import javax.inject.Inject;

import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.data.model.instagram.Feed;
import ca.paulshin.yunatube.ui.base.BasePresenter;
import ca.paulshin.yunatube.util.CollectionUtil;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class InstaFeedPresenter extends BasePresenter<InstaFeedMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    private static final String INSTA_LOAD_COUNT = "18";

    @Inject
    public InstaFeedPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(InstaFeedMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void getInstaFeed(String nextMaxId) {
        checkViewAttached();

        mSubscription = mDataManager.getInstaFeed(nextMaxId, INSTA_LOAD_COUNT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Feed>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the notice.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(Feed feed) {
                        if (feed != null && !CollectionUtil.isEmpty(feed.data)) {
                            getMvpView().showInstaFeed(feed);
                        }
                    }
                });
    }
}
