package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.util.FileUtil;
import ca.paulshin.yunatube.util.ToastUtil;
import ca.paulshin.yunatube.util.YTPreference;
import ca.paulshin.yunatube.widgets.DepthPageTransformer;

/**
 * Created by paulshin on 15-01-31.
 */
public class AnimatedGifActivity extends BasePhotoActivity implements AnimatedGifItemFragment.OnPhotoClickedListener, ViewPager.OnPageChangeListener {
	public static final String EXTRA_FILENAMES = "filenames";
	public static final String EXTRA_INDEX = "index";
	public static final int NOTIFICATION_ID = 3;

	@Bind(R.id.guide)
	public View mGuideView;

	private String mCurrentFileName;
	private String [] mFileNames;

	protected String getScreenName() {
		return "gif - android";
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

		mFileNames = getIntent().getStringArrayExtra(EXTRA_FILENAMES);
		int index = getIntent().getIntExtra(EXTRA_INDEX, 0);

		mPager.setAdapter(new PhotoPagerAdapter(getSupportFragmentManager(), mFileNames));
		mPager.setPageTransformer(true, new DepthPageTransformer());
		mPager.setTranslationX(-1 * mPager.getWidth());
		mPager.setCurrentItem(index);
		mPager.addOnPageChangeListener(this);
		mPager.setOffscreenPageLimit(2);

		onPageSelected(index);

		setMenubarPadding();

		// Hide the guide
		final String isGuideShown = "is_gif_guide_shown";
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

		showSystemUI();
	}

	@Override
	public void OnPhotoSelected(String url) {
		mMenuView.setTag(url);
	}

	@Override
	public void OnPhotoClicked() {
		showSystemUI();
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		mCurrentFileName = mFileNames[position];

		// Send an event to analytics
		sendEvent("gif - android", "swipe", "gif");
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	public static class PhotoPagerAdapter extends FragmentStatePagerAdapter {
		private List<Fragment> fragments;

		public PhotoPagerAdapter(FragmentManager fm, String[] fileNames) {
			super(fm);

			fragments = new ArrayList<>();
			int size = fileNames.length;

			for (int i = 0; i < size; i++) {
				fragments.add(AnimatedGifItemFragment.newInstance(fileNames[i]));
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
		// Check if this photo is already in the album
		File albumDir = FileUtil.getAlbumDir(this);
		File file = new File(albumDir, mCurrentFileName);
		File tempFile = new File(albumDir, Config.TEMP_PREFIX + mCurrentFileName);
		if (file.exists()) {
			ToastUtil.toast(this, R.string.photo_exists);
			return;
		}
		if (tempFile.exists()) {
			File newFile = new File(tempFile.getParent(), mCurrentFileName);
			if (tempFile.renameTo(newFile)) {
				ToastUtil.toast(AnimatedGifActivity.this, R.string.photo_saved_success);
				showDownloadNotification(newFile);
			}
		}

		sendEvent("gif - android", "click", "save");
	}

	public void onShare(View view) {
		// Check if this photo is already in the album
		File albumDir = FileUtil.getAlbumDir(this);
		File file = new File(albumDir, mCurrentFileName);
		File tempFile = new File(albumDir, Config.TEMP_PREFIX + mCurrentFileName);

		File fileToShare = file.exists() ? file : tempFile;
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/jpeg");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileToShare));
		startActivity(Intent.createChooser(intent, getString(R.string.share_via)));

		sendEvent("gif - android", "click", "share");
	}
}
