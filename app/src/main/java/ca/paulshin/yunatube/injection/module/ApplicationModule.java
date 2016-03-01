package ca.paulshin.yunatube.injection.module;

import android.app.Application;
import android.content.Context;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import ca.paulshin.yunatube.data.remote.FlickrService;
import ca.paulshin.yunatube.data.remote.InstaService;
import ca.paulshin.yunatube.data.remote.YunaTubeService;
import ca.paulshin.yunatube.injection.ApplicationContext;
import dagger.Module;
import dagger.Provides;

/**
 * Provide application-level dependencies.
 */
@Module
public class ApplicationModule {
    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    Bus provideEventBus() {
        return new Bus();
    }

    @Provides
    @Singleton
    YunaTubeService provideYunaTubeService() {
        return YunaTubeService.Creator.newYunaTubeService();
    }

    @Provides
    @Singleton
    InstaService provideInstaService() {
        return InstaService.Creator.newInstaService();
    }

    @Provides
    @Singleton
    FlickrService provideFlickrService() {
        return FlickrService.Creator.newFlickrService();
    }
}
