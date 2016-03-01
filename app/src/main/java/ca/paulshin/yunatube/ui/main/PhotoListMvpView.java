package ca.paulshin.yunatube.ui.main;

import ca.paulshin.yunatube.data.model.flickr.PhotosetObject;
import ca.paulshin.yunatube.ui.base.BaseMvpView;

public interface PhotoListMvpView extends BaseMvpView {

    void showPhotos(PhotosetObject photosetObject);

    void showError();
}
