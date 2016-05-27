package ca.paulshin.yunatube.ui.main;

import java.util.List;

import javax.inject.Inject;

import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.ui.base.BasePresenter;
import ca.paulshin.yunatube.util.CollectionUtil;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AnimatedGifListPresenter extends BasePresenter<AnimatedGifListMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public AnimatedGifListPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(AnimatedGifListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void getGifList() {
        checkViewAttached();

        mSubscription = mDataManager.getGifList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading gif list.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(List<String> gifs) {
                        if (!CollectionUtil.isEmpty(gifs)) {
                            getMvpView().showGifs(gifs);
                        }
                    }
                });
    }
}
