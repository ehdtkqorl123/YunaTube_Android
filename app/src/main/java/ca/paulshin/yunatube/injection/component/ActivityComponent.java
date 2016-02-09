package ca.paulshin.yunatube.injection.component;

import dagger.Component;
import ca.paulshin.yunatube.injection.PerActivity;
import ca.paulshin.yunatube.injection.module.ActivityModule;
import ca.paulshin.yunatube.ui.main.MainActivity;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

}
