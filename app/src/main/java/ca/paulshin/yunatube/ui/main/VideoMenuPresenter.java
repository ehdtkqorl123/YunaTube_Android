package ca.paulshin.yunatube.ui.main;

import javax.inject.Inject;

import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.base.BasePresenter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class VideoMenuPresenter extends BasePresenter<VideoMenuMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public VideoMenuPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(VideoMenuMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void getRandomVideo() {
        checkViewAttached();

        mSubscription = mDataManager.getRandomVideo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map((videos) -> videos.get(0))
                .subscribe(new Subscriber<Video>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading search data.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(Video video) {
                        getMvpView().showRandomVideo(video);
                    }
                });
    }
}
