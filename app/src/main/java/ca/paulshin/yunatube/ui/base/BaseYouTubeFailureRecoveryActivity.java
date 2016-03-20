package ca.paulshin.yunatube.ui.base;

import android.os.Bundle;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import ca.paulshin.yunatube.YTApplication;
import ca.paulshin.yunatube.injection.component.ActivityComponent;
import ca.paulshin.yunatube.injection.component.DaggerActivityComponent;
import ca.paulshin.yunatube.injection.module.ActivityModule;
import ca.paulshin.yunatube.ui.main.YouTubeFailureRecoveryActivity;

/**
 * Created by paulshin on 16-03-19.
 */
public abstract class BaseYouTubeFailureRecoveryActivity extends YouTubeFailureRecoveryActivity {
	protected abstract String getScreenName();

	private ActivityComponent mActivityComponent;

	// Tracker
	private Tracker mTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTracker = ((YTApplication)getApplication()).getDefaultTracker();
	}

	@Override
	protected void onStart() {
		super.onStart();

		sendScreen();
	}

	public ActivityComponent getActivityComponent() {
		if (mActivityComponent == null) {
			mActivityComponent = DaggerActivityComponent.builder()
					.activityModule(new ActivityModule(this))
					.applicationComponent(YTApplication.get(this).getComponent())
					.build();
		}
		return mActivityComponent;
	}

	protected void sendScreen() {
		mTracker.setScreenName(getScreenName());
		mTracker.send(new HitBuilders.ScreenViewBuilder()
				.build());
	}

	protected void sendEvent(String category, String action, String label) {
		mTracker.send(new HitBuilders.EventBuilder()
				.setCategory(category)
				.setAction(action)
				.setLabel(label)
				.build());
	}
}
