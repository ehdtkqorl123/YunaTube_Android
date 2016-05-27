package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.util.YTPreference;
import ca.paulshin.yunatube.widgets.DepthPageTransformer;

/**
 * Created by paulshin on 14-12-26.
 */
public class PhotoActivity extends BasePhotoActivity implements ViewPager.OnPageChangeListener {
	public static final String EXTRA_URLS = "urls";
	public static final String EXTRA_INDEX = "index";
	public static final String EXTRA_FROM_NOTIF = "from_notif";
	public static final int NOTIFICATION_ID = 2;

	private boolean mIsFromNotification;
	private PhotoPagerAdapter mPagerAdapter;
	private String mCurrentImageUrl;
	private String [] mUrls;

	@Bind(R.id.guide)
	public View mGuideView;

	@Override
	protected String getScreenName() {
		return "photo - android";
	}

	@Override
	protected int getNotificationId() {
		return NOTIFICATION_ID;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_photo);
		ButterKnife.bind(this);

		mMenuView = ButterKnife.findById(this, R.id.menu);
		mPager = ButterKnife.findById(this, R.id.pager);

		mUrls = getIntent().getStringArrayExtra(EXTRA_URLS);
		int index = getIntent().getIntExtra(EXTRA_INDEX, 0);
		mIsFromNotification = getIntent().getBooleanExtra(EXTRA_FROM_NOTIF, false);

		mPagerAdapter = new PhotoPagerAdapter(getSupportFragmentManager(), mUrls);
		mPager.setAdapter(mPagerAdapter);
		mPager.setPageTransformer(true, new DepthPageTransformer());
		mPager.setTranslationX(-1 * mPager.getWidth());
		mPager.setCurrentItem(index);
		mPager.addOnPageChangeListener(this);

		onPageSelected(index);

		setMenubarPadding();

		showSwipeGuide();

		showSystemUI();
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		mCurrentImageUrl = mUrls[position];

		// Send an event to analytics
		sendEvent("photo - android", "swipe", "photo");
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	public static class PhotoPagerAdapter extends FragmentStatePagerAdapter {
		private List<Fragment> fragments;

		public PhotoPagerAdapter(FragmentManager fm, String[] urls) {
			super(fm);

			fragments = new ArrayList<>();
			int size = urls.length;

			for (int i = 0; i < size; i++) {
				fragments.add(PhotoItemFragment.newInstance(urls[i]));
			}
		}

		@Override
		public Fragment getItem(int position) {
			return this.fragments.get(position);
		}

		@Override
		public int getCount() {
			return this.fragments.size();
		}
	}

	public void onSave(View view) {
		if (!TextUtils.isEmpty(mCurrentImageUrl)) {
			performSave(mCurrentImageUrl);

			sendEvent("photo - android", "click", "save");
		}
	}

	public void onShare(View view) {
		if (!TextUtils.isEmpty(mCurrentImageUrl)) {
			performShare(mCurrentImageUrl);

			sendEvent("photo - android", "click", "share");
		}
	}

	@Override
	public void onBackPressed() {
		if (mIsFromNotification) {
			finish();
			startActivity(new Intent(this, MainActivity.class));
			overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
		}
		else
			super.onBackPressed();
	}

	/**
	 * Show swipe guide
	 */
	private void showSwipeGuide() {
		// Hide the guide
		final String isGuideShown = "is_photo_guide_shown";
		if (!YTPreference.contains(isGuideShown) || !YTPreference.get(isGuideShown, true)) {
			mGuideView.postDelayed(() -> {
				AnimatorSet set = new AnimatorSet();
				set.playTogether(
						ObjectAnimator.ofFloat(mGuideView, "alpha", 1, 0f),
						ObjectAnimator.ofFloat(mGuideView, "translationY", 0, 300)
				);
				set.setDuration(1000).start();
				YTPreference.put(isGuideShown, true);
			}, 1000);
		} else {
			mGuideView.setVisibility(View.GONE);
		}
	}
}
