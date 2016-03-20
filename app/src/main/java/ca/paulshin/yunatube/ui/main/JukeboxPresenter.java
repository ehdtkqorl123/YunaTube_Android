package ca.paulshin.yunatube.ui.main;

import java.util.List;

import javax.inject.Inject;

import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.base.BasePresenter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class JukeboxPresenter extends BasePresenter<JukeboxMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public JukeboxPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(JukeboxMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void getVideos() {
        checkViewAttached();

        mSubscription = mDataManager.getJukeboxVideos()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Video>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading search data.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(List<Video> videos) {
                        if (videos != null) {
                            getMvpView().showVideos(videos);

                            mSubscription.unsubscribe();
                        }
                    }
                });
    }
}
