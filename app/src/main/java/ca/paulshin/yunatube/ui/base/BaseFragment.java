package ca.paulshin.yunatube.ui.base;

import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YTApplication;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.ViewUtil;

/**
 * Created by paulshin on 14-11-25.
 */
public class BaseFragment extends android.support.v4.app.Fragment {

	// Tracker
	private Tracker mTracker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTracker = ((YTApplication)getActivity().getApplication()).getDefaultTracker();
	}

	protected void sendEvent(String category, String action, String label) {
		mTracker.send(new HitBuilders.EventBuilder()
				.setCategory(category)
				.setAction(action)
				.setLabel(label)
				.build());
	}

	protected void sendScreen(String screenName) {
		mTracker.setScreenName(screenName);
		mTracker.send(new HitBuilders.AppViewBuilder().build());
	}

	/**
	 * Adjust the width of the view based on the orientation
	 * @return padding in Px
	 */
	protected int getAdjustedPadding() {
		int padding;
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			int screenSize [] = ViewUtil.getScreenSize();
			int max = Math.max(screenSize[0], screenSize[1]);
			int medium = (screenSize[0] + screenSize[1]) / 2;
			padding = (max - medium) / 3;
		} else {
			padding = ResourceUtil.getDimensionInPx(R.dimen.main_layout_padding);
		}

		return padding;
	}
}
