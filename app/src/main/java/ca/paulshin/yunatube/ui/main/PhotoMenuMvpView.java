package ca.paulshin.yunatube.ui.main;

import java.util.List;

import ca.paulshin.yunatube.data.model.flickr.CollectionItem;
import ca.paulshin.yunatube.data.model.flickr.Item;
import ca.paulshin.yunatube.ui.base.BaseMvpView;

public interface PhotoMenuMvpView extends BaseMvpView {

    void showStream(List<Item> items);

    void showCollections(List<CollectionItem> collectionItems);

    void showError();
}
