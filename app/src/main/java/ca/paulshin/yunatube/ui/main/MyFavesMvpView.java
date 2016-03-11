package ca.paulshin.yunatube.ui.main;

import java.util.List;

import ca.paulshin.dao.DBVideo;
import ca.paulshin.yunatube.ui.base.BaseMvpView;

public interface MyFavesMvpView extends BaseMvpView {

    void showVideos(List<DBVideo> videos);

    void showError();
}
