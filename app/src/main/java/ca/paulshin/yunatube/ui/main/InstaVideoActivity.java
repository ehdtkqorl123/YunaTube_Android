package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.PicassoUtil;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.UIUtil;

public class InstaVideoActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl, MediaPlayer.OnCompletionListener {
	public static final String EXTRA_INSTA_PHOTO_URL = "insta_photo_url";
	public static final String EXTRA_INSTA_VIDEO_URL = "insta_video_url";
	public static final String EXTRA_INSTA_VIDEO_WIDTH = "insta_video_width";
	public static final String EXTRA_INSTA_VIDEO_HEIGHT = "insta_video_height";

	@Bind(R.id.insta_photo)
	public ImageView mInstaPhotoView;
	@Bind(R.id.insta_video)
	public SurfaceView mInstaVideoView;

	private MediaPlayer mMediaPlayer;
	private MediaController mMediaController;

	private SurfaceHolder mHolder;
	private String mImageUrl;
	private String mVideoUrl;

	@Override
	protected String getScreenName() {
		return "instagram - android";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_insta_video);
		ButterKnife.bind(this);

		Intent intent = getIntent();
		mImageUrl = intent.getStringExtra(EXTRA_INSTA_PHOTO_URL);
		mVideoUrl = intent.getStringExtra(EXTRA_INSTA_VIDEO_URL);
		int videoWidth = intent.getIntExtra(EXTRA_INSTA_VIDEO_WIDTH, 1);
		int videoHeight = intent.getIntExtra(EXTRA_INSTA_VIDEO_HEIGHT, 1);

		// Change surfaceView size to fit the video
		int screenSize[] = ResourceUtil.getScreenSize();
		RelativeLayout.LayoutParams videoParams = (RelativeLayout.LayoutParams) mInstaVideoView.getLayoutParams();
		if (screenSize[0] < screenSize[1]) {
			videoParams.width = screenSize[0];
			videoParams.height = videoParams.width * videoHeight / videoWidth;
		} else {
			videoParams.height = screenSize[1];
			videoParams.width = videoParams.height * videoWidth / videoHeight;
		}

		PicassoUtil.loadImage(mImageUrl, mInstaPhotoView);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			int statusBarHeight = UIUtil.getStatusBarHeight(this);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mInstaPhotoView.getLayoutParams();
			params.width = screenSize[1] - statusBarHeight;
		}

		mInstaVideoView.setOnTouchListener((__, __2) -> {
			if (mMediaController.isShowing()) {
				mMediaController.hide();
			} else {
				mMediaController.show();
			}

			return true;
		});
		mMediaPlayer = new MediaPlayer();
		mHolder = mInstaVideoView.getHolder();
		mHolder.addCallback(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mMediaPlayer.release();
	}

	@Override
	public void onBackPressed() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			finishAfterTransition();
		else
			super.onBackPressed();
	}

	/*****
	 * Surface Callback Implementation
	 *****/

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDisplay(surfaceHolder);
			mMediaPlayer.setDataSource(mVideoUrl);
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.prepare();
			mMediaPlayer.setOnPreparedListener(this);

			mMediaController = new MediaController(this);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

	}

	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
		mInstaPhotoView.setVisibility(View.GONE);
		mInstaVideoView.setVisibility(View.VISIBLE);

		mMediaController.setAnchorView(mInstaVideoView);
		mMediaController.setMediaPlayer(this);

		mInstaVideoView.post(() -> {
			mMediaController.setEnabled(true);
			mMediaController.show();
		});
		mediaPlayer.start();
	}

	@Override
	public void onCompletion(MediaPlayer mediaPlayer) {
		finish();
	}

	/*****
	 * MediaControl Implementation
	 */

	@Override
	public void start() {
		mMediaPlayer.start();
	}

	@Override
	public void pause() {
		mMediaPlayer.pause();
	}

	@Override
	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	@Override
	public void seekTo(int i) {
		mMediaPlayer.seekTo(i);
	}

	@Override
	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return false;
	}

	@Override
	public boolean canSeekForward() {
		return false;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}
}
