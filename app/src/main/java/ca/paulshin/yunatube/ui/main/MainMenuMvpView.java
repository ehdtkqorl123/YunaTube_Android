package ca.paulshin.yunatube.ui.main;

import java.util.List;

import ca.paulshin.yunatube.data.model.main.Notice;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.base.BaseMvpView;

public interface MainMenuMvpView extends BaseMvpView {

    void showNotice(Notice notice);

    void updateVideos(List<Video> videos);

    void showError();
}
