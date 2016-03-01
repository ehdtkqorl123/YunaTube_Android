package ca.paulshin.yunatube.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.PicassoUtil;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.YTPreference;
import ca.paulshin.yunatube.widgets.PeekImageView;

/**
 * Created by paulshin on 15-01-10.
 */
public class StoryActivity extends BaseActivity {
	private final static String IMAGE_URL = "http://paulshin.ca/yunatube/mobile/images/yuna_story/%d.jpg";
	private final static int IMAGE_COUNT = 46;

	@Bind(R.id.guide)
	public View mGuideView;

	@Override
	protected String getScreenName() {
		return "story - android";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_story);
		ButterKnife.bind(this);

		showStoryGuideDialog();

		initImageViews();
	}

	/**
	 * For each images, set viewPortListener to determine when to start loading images
	 * This is necessary to avoid loading all images at once when this activity is open
	 */
	private void initImageViews() {
		PeekImageView view = ButterKnife.findById(this, R.id.story_image_1);
		PicassoUtil.loadImage(String.format(IMAGE_URL, 1), view);

		view = ButterKnife.findById(this, R.id.story_image_2);
		PicassoUtil.loadImage(String.format(IMAGE_URL, 2), view);

		for (int i = 1; i < IMAGE_COUNT - 1; i++) {
			final int j = i;
			view = ButterKnife.findById(this, ResourceUtil.getResourceId("id", "story_image_" + i));
			view.setInViewportListener(new PeekImageView.InViewportListener() {
				@Override
				public void onViewportEnter(PeekImageView view) {
					// Load two images ahead when scrolling
					ImageView nextView = ButterKnife.findById(StoryActivity.this, ResourceUtil.getResourceId("id", "story_image_" + (j + 2)));
					PicassoUtil.loadImage(String.format(IMAGE_URL, j + 2), nextView);
				}

				@Override
				public void onViewportExit(PeekImageView view) {

				}
			});
		}
	}

	private void showStoryGuideDialog() {
		final String isGuideShownKey = "is_story_guide_shown";
		if (!YTPreference.contains(isGuideShownKey) || !YTPreference.get(isGuideShownKey, true)) {
			mGuideView.postDelayed(() -> {
				AnimatorSet set = new AnimatorSet();
				set.playTogether(
						ObjectAnimator.ofFloat(mGuideView, "alpha", 1, 0f),
						ObjectAnimator.ofFloat(mGuideView, "translationY", 0, 300)
				);
				set.setDuration(1000).start();
				YTPreference.put(isGuideShownKey, true);
			}, 1000);
		} else {
			mGuideView.setVisibility(View.GONE);
		}
	}
}
