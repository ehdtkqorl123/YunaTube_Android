package ca.paulshin.yunatube.ui.main;

import java.util.List;

import javax.inject.Inject;

import ca.paulshin.dao.DBVideo;
import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.ui.base.BasePresenter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MyFavesPresenter extends BasePresenter<MyFavesMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public MyFavesPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(MyFavesMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void getMyFaves() {
        checkViewAttached();

        mSubscription = mDataManager.getMyFaves()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<DBVideo>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading search data.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(List<DBVideo> videos) {
                        if (videos != null) {
                            getMvpView().showVideos(videos);
                        }
                    }
                });
    }
}
