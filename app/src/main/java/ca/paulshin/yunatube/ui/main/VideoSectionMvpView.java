package ca.paulshin.yunatube.ui.main;

import java.util.List;

import ca.paulshin.yunatube.data.model.video.Section;
import ca.paulshin.yunatube.ui.base.BaseMvpView;

public interface VideoSectionMvpView extends BaseMvpView {

    void showSections(List<Section> sections);

    void showError();
}
