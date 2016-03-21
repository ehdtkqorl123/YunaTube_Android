package ca.paulshin.yunatube;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import ca.paulshin.yunatube.injection.component.ApplicationComponent;
import ca.paulshin.yunatube.injection.component.DaggerApplicationComponent;
import ca.paulshin.yunatube.injection.module.ApplicationModule;
import ca.paulshin.yunatube.util.GlideUtil;
import ca.paulshin.yunatube.util.PicassoUtil;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.ViewUtil;
import ca.paulshin.yunatube.util.YTPreference;
import timber.log.Timber;

public class YTApplication extends Application  {

    private ApplicationComponent mApplicationComponent;
    private Tracker mTracker;

    @Override
	public void onCreate() {
        super.onCreate();

		final Context ctx = getApplicationContext();
		YTPreference.init(ctx);
        ResourceUtil.init(ctx);
        PicassoUtil.init(ctx);
        GlideUtil.init(ctx);
        ViewUtil.init(ctx);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
//            Fabric.with(this, new Crashlytics());
        }
    }

    public static YTApplication get(Context context) {
        return (YTApplication) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use:     adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(Config.TRACKER_ID);
        }
        return mTracker;
    }
}
