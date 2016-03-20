package ca.paulshin.yunatube.ui.main;

import java.util.List;

import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.base.BaseMvpView;

public interface JukeboxMvpView extends BaseMvpView {

    void showVideos(List<Video> videos);

    void showError();
}
