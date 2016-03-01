package ca.paulshin.yunatube.ui.main;

import java.util.List;

import ca.paulshin.yunatube.ui.base.BaseMvpView;

public interface AnimatedGifListMvpView extends BaseMvpView {

    void showGifs(List<String> gifs);

    void showError();
}
