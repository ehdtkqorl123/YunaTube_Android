package ca.paulshin.yunatube.ui.main;

import java.util.List;

import javax.inject.Inject;

import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.data.model.main.Notice;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.base.BasePresenter;
import ca.paulshin.yunatube.util.CollectionUtil;
import ca.paulshin.yunatube.util.MiscUtil;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainMenuPresenter extends BasePresenter<MainMenuMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public MainMenuPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(MainMenuMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void getNotice() {
        checkViewAttached();

        String lang = MiscUtil.getLang();
        int random = MiscUtil.getRandomInt();

        mSubscription = mDataManager.getNotice(lang, random)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Notice>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the notice.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(Notice notice) {
                        if (notice != null) {
                            getMvpView().showNotice(notice);
                        }
                    }
                });
    }


    public void getNewVideos(String lastNewOrder) {
        checkViewAttached();

        mSubscription = mDataManager.getNewVideos(lastNewOrder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Video>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the videos.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(List<Video> videos) {
                        if (!CollectionUtil.isEmpty(videos)) {
                            getMvpView().updateVideos(videos);
                        }
                    }
                });
    }
}
