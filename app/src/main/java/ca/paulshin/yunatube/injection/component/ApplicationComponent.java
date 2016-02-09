package ca.paulshin.yunatube.injection.component;

import android.app.Application;
import android.content.Context;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Component;
import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.data.SyncService;
import ca.paulshin.yunatube.data.local.DatabaseHelper;
import ca.paulshin.yunatube.data.local.PreferencesHelper;
import ca.paulshin.yunatube.data.remote.RibotsService;
import ca.paulshin.yunatube.injection.ApplicationContext;
import ca.paulshin.yunatube.injection.module.ApplicationModule;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(SyncService syncService);

    @ApplicationContext Context context();
    Application application();
    RibotsService ribotsService();
    PreferencesHelper preferencesHelper();
    DatabaseHelper databaseHelper();
    DataManager dataManager();
    Bus eventBus();

}
