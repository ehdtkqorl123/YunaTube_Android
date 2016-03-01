package ca.paulshin.yunatube.injection.component;

import android.app.Application;
import android.content.Context;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.data.SyncService;
import ca.paulshin.yunatube.data.local.DatabaseHelper;
import ca.paulshin.yunatube.data.local.PreferencesHelper;
import ca.paulshin.yunatube.injection.ApplicationContext;
import ca.paulshin.yunatube.injection.module.ApplicationModule;
import ca.paulshin.yunatube.receiver.ConnectivityChangeReceiver;
import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(SyncService syncService);
    void inject(ConnectivityChangeReceiver connectivityChangeReceiver);

    @ApplicationContext Context context();
    Application application();
    PreferencesHelper preferencesHelper();
    DatabaseHelper databaseHelper();
    DataManager dataManager();
    Bus eventBus();

}
