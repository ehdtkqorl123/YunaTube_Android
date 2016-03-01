package ca.paulshin.yunatube.ui.main;

import javax.inject.Inject;

import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.data.model.flickr.PhotosetObject;
import ca.paulshin.yunatube.ui.base.BasePresenter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PhotoListPresenter extends BasePresenter<PhotoListMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public PhotoListPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(PhotoListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void getPhotoList(String setId) {
        checkViewAttached();

        mSubscription = mDataManager.getPhotoList(setId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<PhotosetObject>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the notice.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(PhotosetObject photosetObject) {
                        if (photosetObject != null) {
                            getMvpView().showPhotos(photosetObject);
                        }
                    }
                });
    }
}
