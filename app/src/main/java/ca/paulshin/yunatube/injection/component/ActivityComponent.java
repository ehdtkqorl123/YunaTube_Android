package ca.paulshin.yunatube.injection.component;

import ca.paulshin.yunatube.injection.PerActivity;
import ca.paulshin.yunatube.injection.module.ActivityModule;
import ca.paulshin.yunatube.ui.main.AnimatedGifListActivity;
import ca.paulshin.yunatube.ui.main.InstaFeedActivity;
import ca.paulshin.yunatube.ui.main.MainMenuFragment;
import ca.paulshin.yunatube.ui.main.MessageActivity;
import ca.paulshin.yunatube.ui.main.PhotoListActivity;
import ca.paulshin.yunatube.ui.main.PhotoMenuFragment;
import ca.paulshin.yunatube.ui.main.SearchActivity;
import ca.paulshin.yunatube.ui.main.VideoListActivity;
import ca.paulshin.yunatube.ui.main.VideoMenuFragment;
import ca.paulshin.yunatube.ui.main.VideoSectionActivity;
import dagger.Component;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainMenuFragment mainFragment);
    void inject(VideoMenuFragment videoMenuFragment);
    void inject(PhotoMenuFragment photoMenuFragment);
    void inject(InstaFeedActivity instaFeedActivity);
    void inject(AnimatedGifListActivity animatedGifListActivity);
    void inject(PhotoListActivity photoListActivity);
    void inject(VideoSectionActivity videoSectionActivity);
    void inject(VideoListActivity videoListActivity);
    void inject(SearchActivity searchActivity);
    void inject(MessageActivity messageActivity);
}
