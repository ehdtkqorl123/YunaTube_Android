package ca.paulshin.yunatube.ui.main;

import javax.inject.Inject;

import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.data.model.flickr.CollectionsObject;
import ca.paulshin.yunatube.data.model.flickr.Stream;
import ca.paulshin.yunatube.ui.base.BasePresenter;
import ca.paulshin.yunatube.util.CollectionUtil;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PhotoMenuPresenter extends BasePresenter<PhotoMenuMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public PhotoMenuPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(PhotoMenuMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void getStream() {
        checkViewAttached();

        mSubscription = mDataManager.getStream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Stream>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading search data.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(Stream stream) {
                        if (stream != null
                            && !CollectionUtil.isEmpty(stream.items)) {
                            getMvpView().showStream(stream.items);
                        } else {
                            getMvpView().showError();
                        }
                    }
                });
    }

    public void getCollections() {
        checkViewAttached();

        mSubscription = mDataManager.getCollections()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CollectionsObject>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading search data.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(CollectionsObject collectionsObject) {
                        if (collectionsObject != null
                                && collectionsObject.collections != null
                                && !CollectionUtil.isEmpty(collectionsObject.collections.collection)) {
                            getMvpView().showCollections(collectionsObject.collections.collection);
                        } else {
                            getMvpView().showError();
                        }
                    }
                });
    }
}
