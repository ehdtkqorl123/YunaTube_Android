package ca.paulshin.yunatube.ui.base;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YTApplication;
import ca.paulshin.yunatube.injection.component.ActivityComponent;
import ca.paulshin.yunatube.injection.component.DaggerActivityComponent;
import ca.paulshin.yunatube.injection.module.ActivityModule;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.ViewUtil;

public abstract class BaseActivity extends AppCompatActivity {

	protected abstract String getScreenName();

    private ActivityComponent mActivityComponent;

    // SwipeRefreshLayout allows the user to swipe the screen down to trigger a manual refresh
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mProgressBarTopWhenActionBarShown;

    // Durations for certain animations we use:
    private static final int HEADER_HIDE_ANIM_DURATION = 300;

    // variables that control the Action Bar auto hide behavior (aka "quick recall")
    private boolean mActionBarAutoHideEnabled = false;
    private int mActionBarAutoHideSensivity = 0;
    private int mActionBarAutoHideMinY = 0;
    private int mActionBarAutoHideSignal = 0;
    private boolean mActionBarShown = true;

    // When set, these components will be shown/hidden in sync with the action bar
    // to implement the "quick recall" effect (the Action Bar and the header views disappear
    // when you scroll down a list, and reappear quickly when you scroll up).
    private ArrayList<View> mHideableHeaderViews = new ArrayList<>();

    // Primary toolbar and drawer toggle
    private Toolbar mActionBarToolbar;

    // Tracker
    private Tracker mTracker;

	public ActivityComponent getActivityComponent() {
		if (mActivityComponent == null) {
			mActivityComponent = DaggerActivityComponent.builder()
					.activityModule(new ActivityModule(this))
					.applicationComponent(YTApplication.get(this).getComponent())
					.build();
		}
		return mActivityComponent;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = ((YTApplication)getApplication()).getDefaultTracker();
		Window w = getWindow();
		w.setStatusBarColor(ContextCompat.getColor(this, getStatusBarColor()));
    }

	protected void setupToolbar() {
 		Toolbar toolbar = getActionBarToolbar();
		toolbar.setNavigationIcon(R.drawable.ic_up);
		toolbar.setNavigationOnClickListener((__) -> finish());
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		trySetupSwipeRefresh();
		updateSwipeRefreshProgressBarTop();
	}

	@Override
	protected void onStart() {
		super.onStart();
		sendScreen();
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		getActionBarToolbar();
	}

	protected Toolbar getActionBarToolbar() {
		if (mActionBarToolbar == null) {
			mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
			if (mActionBarToolbar != null) {
				setSupportActionBar(mActionBarToolbar);
			}
		}
		return mActionBarToolbar;
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
			padding = (max - medium) / 2;
		} else {
			padding = ResourceUtil.getDimensionInPx(R.dimen.main_layout_padding);
		}

		return padding;
	}

	/************************
	 * Swipe to Refresh
	 ***********************/
	private void trySetupSwipeRefresh() {
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setColorSchemeColors(
					R.color.refresh_progress_1,
					R.color.refresh_progress_2,
					R.color.refresh_progress_3,
					R.color.refresh_progress_4);

			mSwipeRefreshLayout.setOnRefreshListener(() -> requestDataRefresh());
		}
	}

	protected void requestDataRefresh() {
		mSwipeRefreshLayout.setRefreshing(true);
	}

	protected void setProgressBarTopWhenActionBarShown(int progressBarTopWhenActionBarShown) {
		mProgressBarTopWhenActionBarShown = progressBarTopWhenActionBarShown;
		updateSwipeRefreshProgressBarTop();
	}

	private void updateSwipeRefreshProgressBarTop() {
		if (mSwipeRefreshLayout != null) {
			int progressBarStartMargin = getResources().getDimensionPixelSize(
					R.dimen.swipe_refresh_progress_bar_start_margin);
			int progressBarEndMargin = getResources().getDimensionPixelSize(
					R.dimen.swipe_refresh_progress_bar_end_margin);
			int top = mActionBarShown ? mProgressBarTopWhenActionBarShown : 0;
			mSwipeRefreshLayout.setProgressViewOffset(false,
					top + progressBarStartMargin, top + progressBarEndMargin);
		}
	}

	protected void onRefreshingStateChanged(boolean refreshing) {
		if (mSwipeRefreshLayout != null)
			mSwipeRefreshLayout.setRefreshing(refreshing);
	}

	protected void enableDisableSwipeRefresh(boolean enable) {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setEnabled(enable);
		}
	}

	/************************
	 * Header Autohide
	 ************************/
	protected void registerHideableHeaderView(View hideableHeaderView) {
		if (!mHideableHeaderViews.contains(hideableHeaderView)) {
			mHideableHeaderViews.add(hideableHeaderView);
		}
	}

	protected void deregisterHideableHeaderView(View hideableHeaderView) {
		if (mHideableHeaderViews.contains(hideableHeaderView)) {
			mHideableHeaderViews.remove(hideableHeaderView);
		}
	}

	protected void enableActionBarAutoHide(final ListView listView) {
		initActionBarAutoHide();
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			final static int ITEMS_THRESHOLD = 3;
			int lastFvi = 0;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				onMainContentScrolled(firstVisibleItem <= ITEMS_THRESHOLD ? 0 : Integer.MAX_VALUE,
						lastFvi - firstVisibleItem > 0 ? Integer.MIN_VALUE :
								lastFvi == firstVisibleItem ? 0 : Integer.MAX_VALUE
				);
				lastFvi = firstVisibleItem;
			}
		});
	}

	/**
	 * Indicates that the main content has scrolled (for the purposes of showing/hiding
	 * the action bar for the "action bar auto hide" effect). currentY and deltaY may be exact
	 * (if the underlying view supports it) or may be approximate indications:
	 * deltaY may be INT_MAX to mean "scrolled forward indeterminately" and INT_MIN to mean
	 * "scrolled backward indeterminately".  currentY may be 0 to mean "somewhere close to the
	 * start of the list" and INT_MAX to mean "we don't know, but not at the start of the list"
	 */
	private void onMainContentScrolled(int currentY, int deltaY) {
		if (deltaY > mActionBarAutoHideSensivity) {
			deltaY = mActionBarAutoHideSensivity;
		} else if (deltaY < -mActionBarAutoHideSensivity) {
			deltaY = -mActionBarAutoHideSensivity;
		}

		if (Math.signum(deltaY) * Math.signum(mActionBarAutoHideSignal) < 0) {
			// deltaY is a motion opposite to the accumulated signal, so reset signal
			mActionBarAutoHideSignal = deltaY;
		} else {
			// add to accumulated signal
			mActionBarAutoHideSignal += deltaY;
		}

		boolean shouldShow = currentY < mActionBarAutoHideMinY ||
				(mActionBarAutoHideSignal <= -mActionBarAutoHideSensivity);
		autoShowOrHideActionBar(shouldShow);
	}

	protected void autoShowOrHideActionBar(boolean show) {
		if (show == mActionBarShown) {
			return;
		}

		mActionBarShown = show;
		onActionBarAutoShowOrHide(show);
	}

	/**
	 * Initializes the Action Bar auto-hide (aka Quick Recall) effect.
	 */
	private void initActionBarAutoHide() {
		mActionBarAutoHideEnabled = true;
		mActionBarAutoHideMinY = getResources().getDimensionPixelSize(
				R.dimen.action_bar_auto_hide_min_y);
		mActionBarAutoHideSensivity = getResources().getDimensionPixelSize(
				R.dimen.action_bar_auto_hide_sensivity);
	}

	protected void onActionBarAutoShowOrHide(boolean shown) {
		for (View view : mHideableHeaderViews) {
			if (shown) {
				view.animate()
						.translationY(0)
						.alpha(1)
						.setDuration(HEADER_HIDE_ANIM_DURATION)
						.setInterpolator(new DecelerateInterpolator());
			} else {
				view.animate()
						.translationY(-view.getBottom())
						.alpha(0)
						.setDuration(HEADER_HIDE_ANIM_DURATION)
						.setInterpolator(new DecelerateInterpolator());
			}
		}
	}

	protected boolean applyTransitionOnFinish() {
		return true;
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

	@Override
	public void finish() {
		super.finish();
		if (applyTransitionOnFinish()) {
			overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
		}
	}

	protected int getStatusBarColor() {
		return R.color.theme_primary_dark;
	}
}
