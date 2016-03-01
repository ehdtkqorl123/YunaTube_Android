package ca.paulshin.yunatube.ui.main;

import ca.paulshin.yunatube.data.model.instagram.Feed;
import ca.paulshin.yunatube.ui.base.BaseMvpView;

public interface InstaFeedMvpView extends BaseMvpView {

    void showInstaFeed(Feed feed);

    void showError();
}
