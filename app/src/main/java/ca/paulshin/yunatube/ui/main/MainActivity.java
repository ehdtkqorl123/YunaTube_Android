package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewPropertyAnimator;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.GCMActivity;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.YTPreference;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends GCMActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, MainMenuFragment.MainMenuScrollListener {

	@Bind(R.id.main_pager)
	ViewPager mPager;
	@Bind(R.id.main_menu)
	LinearLayout mMenu;
	@Bind(R.id.fab_search)
	View mSearchView;

	public static final String EXTRA_TAB = "extra_tab";

	private static final String HAS_RATED = "has_rated";
	private static final String VISIT_COUNT = "visit_count";
	private static final int NUM_PAGES = 4;
	private static final int TRANSLATE_DURATION_MILLIS = 200;

	private ScreenSlidePagerAdapter mPagerAdapter;
	private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
	private boolean mPrevFabIsShown;
	private int mFabTranslationY;

	public interface OnPageSelectedListener {
		void onPageSelected();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_main);
		ButterKnife.bind(this);

		int fabHeight = ResourceUtil.getDimensionInPx(R.dimen.fab_size_normal);
		mFabTranslationY = fabHeight + ResourceUtil.getDimensionInPx(R.dimen.fab_margin);
		mSearchView.post(() -> mSearchView.setTranslationY(mFabTranslationY));

		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.addOnPageChangeListener(this);
		mPager.setAdapter(mPagerAdapter);
		mPager.setOffscreenPageLimit(3);

		mSearchView.setOnClickListener(v -> {
			Intent intent = new Intent(this, MainSearchActivity.class);
			startActivity(intent);
		});

		onNewIntent(getIntent());

		// Set default push notification
		if (!YTPreference.contains(SettingsActivity.PREF_NOTIFICATION)) {
			YTPreference.put(SettingsActivity.PREF_NOTIFICATION, true);
		}

		// Show "Rate YunaTube" dialog
		// Track visit number
		YTPreference.put(VISIT_COUNT, YTPreference.contains(VISIT_COUNT) ? YTPreference.getInt(VISIT_COUNT) + 1 : 1);
		showRateDialog();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getStringExtra(MainActivity.EXTRA_TAB) != null) {
			mPager.setCurrentItem(3, false);
		}
	}

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			super.onBackPressed();
		} else {
			mPager.setCurrentItem(0, false);
		}
	}

	private int prevPosition;

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		View view1 = ((FrameLayout) mMenu.getChildAt(position)).getChildAt(1);

		if (position != prevPosition) {
			((FrameLayout) mMenu.getChildAt(prevPosition)).getChildAt(1).setAlpha(0);
			prevPosition = position;
		}

		if (positionOffset != 0) {
			view1.setAlpha(1 - positionOffset);

			View view2 = ((FrameLayout) mMenu.getChildAt(position + 1)).getChildAt(1);
			view2.setAlpha(positionOffset);
		}

		if (position == 0) {
			mSearchView.setTranslationY(mFabTranslationY * positionOffset);
			if (positionOffset == 0) {
				mPrevFabIsShown = false;
			}
		} else {
			mSearchView.setTranslationY(mFabTranslationY);
		}
	}

	@Override
	public void onPageSelected(int position) {
		((FrameLayout) mMenu.getChildAt(position)).getChildAt(1).setAlpha(1);

		Fragment fragment = mPagerAdapter.getItem(position);
		if (fragment instanceof OnPageSelectedListener) {
			((OnPageSelectedListener) fragment).onPageSelected();
		}

		if (position != 0) {
			toggleFab(false);
		}

		// Send an event to analytics
		String[] eventNames = {"home", "yuna", "video", "photo"};
		sendEvent("main - android", "swipe", eventNames[position]);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
		Fragment fragments[];

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);

			fragments = new Fragment[]{
					MainMenuFragment.newInstance(),
					YunaMenuFragment.newInstance(),
					VideoMenuFragment.newInstance(),
					PhotoMenuFragment.newInstance()
			};
		}

		@Override
		public Fragment getItem(int position) {
			return fragments[position];
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

	@Override
	public void onClick(View v) {
		String tag = (String) v.getTag();
		int index = Integer.parseInt(tag);
		boolean smoothTransition = Math.abs(mPager.getCurrentItem() - index) == 1;
		mPager.setCurrentItem(index, smoothTransition);
	}

	protected boolean applyTransitionOnFinish() {
		return false;
	}

	/**
	 * Show "Rate YunaTube" dialog after the 3rd use of the app if the user hasn't
	 */
	private void showRateDialog() {
		boolean hasUserRated = YTPreference.get(HAS_RATED, false);
		if (YTPreference.getInt(VISIT_COUNT) > 2 && !hasUserRated) {
			new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
					.setTitleText(getString(R.string.rate_title))
					.setContentText(getString(R.string.rate_content))
					.setConfirmText(getString(R.string.dialog_ok))
					.setConfirmClickListener((dialog, input) -> {
						dialog.dismissWithAnimation();

						Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.MARKET_URL));
						marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(marketIntent);
						YTPreference.put(HAS_RATED, true);
					})
					.setCancelText(getString(R.string.rate_later))
					.setCancelClickListener((dialog, input) -> {
						dialog.dismissWithAnimation();
					})
					.show();
		}
	}

	@Override
	protected String getScreenName() {
		return "main - android";
	}

	@Override
	public void showFab() {
		mSearchView.postDelayed(() -> toggleFab(true), 1000);
	}

	@Override
	public void toggleFab(boolean visible) {
		if (mPrevFabIsShown != visible) {
			mPrevFabIsShown = visible;

			ViewPropertyAnimator.animate(mSearchView).setInterpolator(mInterpolator)
					.setDuration(TRANSLATE_DURATION_MILLIS)
					.translationY(visible ? 0 : mFabTranslationY);
		}
	}
}
