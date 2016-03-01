package ca.paulshin.yunatube.ui.main;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.util.PicassoUtil;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.UIUtil;
import uk.co.senab.photoview.PhotoViewAttacher;

public class InstaPhotoActivity extends BasePhotoActivity {
	public static final String EXTRA_INSTA_PHOTO_URL = "insta_photo_url";
	public static final int NOTIFICATION_ID = 2;

	@Bind(R.id.insta_photo)
	public ImageView mInstaPhotoView;
	private PhotoViewAttacher mAttacher;

	private String mImageUrl;

	@Override
	protected String getScreenName() {
		return "instagram - android";
	}

	@Override
	protected int getNotificationId() {
		return NOTIFICATION_ID;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_insta_photo);
		ButterKnife.bind(this);

		mMenuView = ButterKnife.findById(this, R.id.menu);
		mImageUrl = getIntent().getStringExtra(EXTRA_INSTA_PHOTO_URL);
		PicassoUtil.loadImage(mImageUrl, mInstaPhotoView, new Callback() {
			@Override
			public void onSuccess() {
				// Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
				mAttacher = new PhotoViewAttacher(mInstaPhotoView);
				mAttacher.setOnPhotoTapListener(InstaPhotoActivity.this);
			}

			@Override
			public void onError() {}
		});

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mInstaPhotoView.getLayoutParams();

			int statusBarHeight = UIUtil.getStatusBarHeight(this);
			params.width = ResourceUtil.getScreenSize()[1] - statusBarHeight;
		}
	}

	public void onSave(View view) {
		performSave(mImageUrl);

		sendEvent("instagram - android", "click", "save");
	}

	public void onShare(View view) {
		performShare(mImageUrl);

		sendEvent("instagram - android", "click", "share");
	}

	@Override
	public void onBackPressed() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			finishAfterTransition();
		else
			super.onBackPressed();
	}
}
