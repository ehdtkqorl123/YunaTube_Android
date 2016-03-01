package ca.paulshin.yunatube.ui.main;

import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.base.BaseMvpView;

public interface VideoMenuMvpView extends BaseMvpView {

    void showRandomVideo(Video video);

    void showError();
}
