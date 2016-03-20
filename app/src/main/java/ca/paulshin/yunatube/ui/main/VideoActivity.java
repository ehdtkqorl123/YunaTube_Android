package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.BuildConfig;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.video.Comment;
import ca.paulshin.yunatube.data.model.video.SimpleResult;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.adapter.CommentAdapter;
import ca.paulshin.yunatube.ui.base.BaseYouTubeFailureRecoveryActivity;
import ca.paulshin.yunatube.util.NetworkUtil;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.ToastUtil;
import ca.paulshin.yunatube.util.YTPreference;
import ca.paulshin.yunatube.util.events.VideoDeletedEvent;
import ca.paulshin.yunatube.widgets.FloatingActionButton;
import ca.paulshin.yunatube.widgets.FloatingActionsMenu;
import ca.paulshin.yunatube.widgets.RecyclerViewScrollDetector;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class VideoActivity extends BaseYouTubeFailureRecoveryActivity implements
		YouTubePlayer.OnFullscreenListener,
		View.OnTouchListener,
		FloatingActionsMenu.ToggleListener,
		View.OnClickListener,
		VideoMvpView {

	@Inject
	VideoPresenter mVideoPresenter;
	@Inject
	Bus mBus;

	public static final String EXTRA_YTID = "ytid";
	public static final String EXTRA_FROM_NOTIF = "from_notif";
	private static final int TRANSLATE_DURATION_MILLIS = 200;

	@Bind(R.id.player)
	public YouTubePlayerView mPlayerView;
	@Bind(R.id.list)
	public RecyclerView mRecyclerView;
	@Bind(R.id.main_fab)
	public FloatingActionsMenu mFab;
	@Bind(R.id.veil)
	public View mVeilView;
	@Bind(R.id.comment_window)
	RelativeLayout mCommentWindow;
	@Bind(R.id.content)
	EditText mCommentView;
	@Bind(R.id.favorite)
	TextView mFavoriteView;
	@Bind(R.id.fab_favorite)
	FloatingActionButton mFavorite;
	@Bind(R.id.loading)
	View mLoadingView;
	@Bind(R.id.none)
	View mNoneView;

	private View mListHeaderView;
	private boolean mFromNotification;
	private String mYtid;
	private Video mVideo;
	private boolean mIsFullscreen;
	private String mUsername;
	private int mVideoKey;
	private boolean mIsRefreshing;
	private String mLastIndex;
	private CommentAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_video);
		ButterKnife.bind(this);
		getActivityComponent().inject(this);
		mVideoPresenter.attachView(this);

		Intent intent = getIntent();
		mYtid = intent.getStringExtra(EXTRA_YTID);
		mFromNotification = intent.getBooleanExtra(EXTRA_FROM_NOTIF, false);

		mListHeaderView = LayoutInflater.from(this).inflate(R.layout.p_comments_header, null);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mAdapter = new CommentAdapter(mRecyclerView, mListHeaderView);
		mAdapter.setOnLoadMoreListener(() -> mVideoPresenter.getComments(mYtid, mLastIndex));
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.addOnScrollListener(new RecyclerViewScrollDetectorImpl());

		mFab.setListener(this);
		mVeilView.setOnTouchListener(this);

		mVideoPresenter.getFaveStatus(mYtid);

		ButterKnife.findById(this, R.id.close).setOnClickListener(this);
		ButterKnife.findById(this, R.id.submit).setOnClickListener(this);

		setFABs();

		loadData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mVideoPresenter.detachView();
	}

	@Override
	protected void onStart() {
		super.onStart();

		sendScreen();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mUsername = YTPreference.getString(SettingsActivity.PREF_USERNAME);
		((TextView) ButterKnife.findById(this, R.id.my_username)).setText(mUsername);
	}

	@Override
	public void onBackPressed() {
		if (mCommentWindow.getVisibility() == View.VISIBLE) {
			toggleCommentWindow(false);
		} else if (mFab.isExpanded()) {
			mFab.collapse();
		} else {
			if (mFromNotification) {
				finish();
				startActivity(new Intent(this, MainActivity.class));
				overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
			} else
				super.onBackPressed();
		}
	}

	@Override
	protected String getScreenName() {
		return "video - android: " + mYtid;
	}

	private void loadData() {
		if (NetworkUtil.isNetworkConnected(this)) {
			mVideoPresenter.getVideo(mYtid);
			mVideoPresenter.getComments(mYtid, mLastIndex);
		} else {
			//TODO
		}
	}

	private void setFABs() {
		ButterKnife.findById(this, R.id.fab_comment).setOnClickListener(this);
		ButterKnife.findById(this, R.id.fab_favorite).setOnClickListener(this);
		ButterKnife.findById(this, R.id.fab_youtube).setOnClickListener(this);
		ButterKnife.findById(this, R.id.fab_report).setOnClickListener(this);
		ButterKnife.findById(this, R.id.fab_share).setOnClickListener(this);
		ButterKnife.findById(this, R.id.fab_download).setOnClickListener(this);
	}

	/**
	 * YouTube API
	 */
	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
		player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
		player.setOnFullscreenListener(this);
		if (!wasRestored) {
			player.loadVideo(mYtid);
		}

		int controlFlags = player.getFullscreenControlFlags();
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
		player.setFullscreenControlFlags(controlFlags);
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return mPlayerView;
	}

	@Override
	public void onFullscreen(boolean isFullscreen) {
		mIsFullscreen = isFullscreen;
		doLayout();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		mIsFullscreen = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
		doLayout();
	}

	private void doLayout() {
		RelativeLayout.LayoutParams playerParams = (RelativeLayout.LayoutParams) mPlayerView.getLayoutParams();
		if (mIsFullscreen) {
			// When in fullscreen, the visibility of all other views than the player should be set to
			// GONE and the player should be laid out across the whole screen.
			playerParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
			playerParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

			mRecyclerView.setVisibility(View.GONE);
			mFab.setVisibility(View.GONE);
		} else {
			ViewGroup.LayoutParams otherViewsParams = mRecyclerView.getLayoutParams();
			playerParams.width = otherViewsParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
			playerParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

			mRecyclerView.setVisibility(View.VISIBLE);
			mFab.setVisibility(View.VISIBLE);
		}

		if (ResourceUtil.getInteger(R.integer.hide_video_fab) == 1)
			mFab.setVisibility(View.GONE);
		else
			mFab.setVisibility(mIsFullscreen ? View.GONE : View.VISIBLE);
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showVideo(Video video) {
		mVideo = video;
		mPlayerView.initialize(Config.YOUTUBE_DEVELOPER_KEY, VideoActivity.this);

		TextView section = ButterKnife.findById(mListHeaderView, R.id.section);
		section.setText(video.stitle);

		TextView title = ButterKnife.findById(mListHeaderView, R.id.title);
		title.setText(video.ytitle);
	}

	@Override
	public void showComments(List<Comment> comments) {
		mLoadingView.setVisibility(View.GONE);

		if (comments.isEmpty()) {
			mNoneView.setVisibility(View.VISIBLE);
		} else {
			Comment lastComment = comments.get(comments.size() - 1);
			mLastIndex = lastComment.id;

			if (TextUtils.equals(lastComment.isfirst, "1")) {
				// After reaching the last one, deactivate loadmore
				mAdapter.setOnLoadMoreListener(null);
			}
			mAdapter.addComments(mIsRefreshing, comments);
			mAdapter.setLoaded();
			mAdapter.notifyDataSetChanged();

			mIsRefreshing = false;
		}
	}

	@Override
	public void report(SimpleResult result) {
		if (TextUtils.equals("success", result.result)) {
			ToastUtil.toast(VideoActivity.this, R.string.report_successful);
		}

		sendEvent("video - android", "click: " + mVideo.ytid, "report");
	}

	@Override
	public void updateComment(Comment comment) {
		toggleCommentWindow(false);
		mAdapter.insertNewComment(comment);
		mAdapter.notifyDataSetChanged();

		sendEvent("video - android", "send: " + mVideo.ytid, "comment");
	}

	@Override
	public void setFaveStatus(int id) {
		mVideoKey = id;
		boolean isFaved = id != -1;
		mFavoriteView.setText(isFaved ? R.string.youtube_remove_from_my_faves : R.string.youtube_add_to_my_faves);
		mFavorite.setIcon(isFaved ? R.drawable.ic_unfavorite : R.drawable.ic_favorite);
	}

	@Override
	public void addedFave(Video dbVideo) {
		if (dbVideo != null) {
			ToastUtil.toast(this, R.string.faves_add_success);
			sendEvent("video - android", "add: " + mYtid, "fave");
		} else {
			ToastUtil.toast(this, R.string.faves_add_failure);
		}
	}

	@Override
	public void deletedFave(Integer row) {
		if (row > 0) {
			ToastUtil.toast(this, R.string.faves_remove_success);
			mVideoKey = -1;
			mBus.post(new VideoDeletedEvent());
			sendEvent("video - android", "remove: " + mYtid, "fave");
		} else {
			ToastUtil.toast(this, R.string.faves_remove_failure);
		}
	}

	@Override
	public void showError() {
		//TODO
	}

	/*****
	 * FAB
	 *****/

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mFab.collapse();
		return true;
	}

	@Override
	public void onToggle(boolean isExpand) {
		mVeilView.setVisibility(isExpand ? View.VISIBLE : View.GONE);
	}

	public void showFab() {
		toggle(true);
	}

	public void hideFab() {
		toggle(false);
	}

	private boolean mFabToggled;
	private boolean mPrevVisibility = true;

	private void toggle(final boolean visible) {

		if (!mFabToggled && mPrevVisibility != visible) {
			mFabToggled = true;
			mPrevVisibility = visible;
			if (mFab.isExpanded())
				mFab.collapse();

			int fabHeight = mFab.getHeight();
			int translationY = visible ? 0 : fabHeight + ResourceUtil.getPx(16);
			ViewPropertyAnimator.animate(mFab).setInterpolator(new AccelerateDecelerateInterpolator())
					.setDuration(TRANSLATE_DURATION_MILLIS)
					.translationY(translationY)
					.setListener(new Animator.AnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {

						}

						@Override
						public void onAnimationEnd(Animator animation) {
							mFabToggled = false;
						}

						@Override
						public void onAnimationCancel(Animator animation) {

						}

						@Override
						public void onAnimationRepeat(Animator animation) {

						}
					});
		}
	}

	private void toggleCommentWindow(boolean show) {
		Animation animShow = AnimationUtils.loadAnimation(this, R.anim.popup_show);
		Animation animHide = AnimationUtils.loadAnimation(this, R.anim.popup_hide);

		if (show) {
			mCommentWindow.setVisibility(View.VISIBLE);
			mCommentView.requestFocus();
			mFab.setVisibility(View.GONE);
		} else {
			hideKeyboard();
		}
		mCommentWindow.startAnimation(show ? animShow : animHide);
		if (!show) {
			mCommentWindow.setVisibility(View.GONE);
			mFab.setVisibility(View.VISIBLE);
		}
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mCommentView.getWindowToken(), 0);
		mCommentView.setText("");
	}

	@Override
	public void onClick(View v) {
		final Intent intent;
		mFab.collapse();

		switch (v.getId()) {
			case R.id.fab_comment:
				if (!TextUtils.isEmpty(mUsername))
					toggleCommentWindow(true);
				else {
					new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
							.setTitleText(null)
							.setContentText(getString(R.string.set_username))
							.setConfirmText(getString(R.string.yes))
							.setConfirmClickListener((sDialog, __) -> {
								sDialog.dismissWithAnimation();
								startActivity(new Intent(VideoActivity.this, SettingsActivity.class));
								overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
							})
							.setCancelText(getString(R.string.no))
							.setCancelClickListener((sDialog, __) -> {
								sDialog.dismissWithAnimation();
							})
							.show();
				}

				break;

			case R.id.fab_favorite:
				if (mVideoKey == -1) {
					// Create Video
					Video video = new Video();
					video.stitle = mVideo.stitle;
					video.ytid = mVideo.ytid;
					video.ytitle = mVideo.ytitle;
					mVideoPresenter.addFave(video);
				} else {
					mVideoPresenter.deleteFave(mVideoKey);
				}
				mFavoriteView.setText(mVideoKey == 0 ? R.string.youtube_add_to_my_faves : R.string.youtube_remove_from_my_faves);
				mFavorite.setIcon(mVideoKey == 0 ? R.drawable.ic_favorite : R.drawable.ic_unfavorite);
				break;

			case R.id.fab_youtube:
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(String.format(Config.YOUTUBE_SHARE_URL_PREFIX, mYtid)));
				startActivity(intent);

				sendEvent("video - android", "click: " + mYtid, "youtubeapp");
				break;

			case R.id.fab_report:
				new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
						.setTitleText(getString(R.string.report_video))
						.setContentText(getString(R.string.is_video_blocked))
						.setConfirmText(getString(R.string.yes))
						.setConfirmClickListener((sDialog, __) -> {
							sDialog.dismissWithAnimation();
							report();
						})
						.setCancelText(getString(R.string.no))
						.setCancelClickListener((sDialog, __) -> sDialog.dismissWithAnimation())
						.show();
				break;

			case R.id.fab_share:
				intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, mVideo.ytitle);
				intent.putExtra(Intent.EXTRA_TEXT, mVideo.stitle + " - " + mVideo.ytitle + " : http://youtu.be/" + mVideo.ytid);
				startActivity(Intent.createChooser(intent, getString(R.string.share_via)));

				sendEvent("video - android", "click: " + mVideo.ytid, "share");
				break;

			case R.id.fab_download:
				String dlUrl = "http://ssyoutube.com/watch?v=" + mYtid;
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(dlUrl));

				if (BuildConfig.DEBUG || !YTPreference.getBoolean("download_isfirst")) {
					YTPreference.put("download_isfirst", true);

					ImageView guide = new ImageView(this);
					guide.setImageResource(R.drawable.video_download_guide);

					new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_BIG_IMAGE_TYPE)
							.setCustomBigImage(R.drawable.video_download_guide)
							.setConfirmText(getString(R.string.download_video))
							.setConfirmClickListener((sDialog, __) -> {
								sDialog.dismissWithAnimation();
								startActivity(intent);
							})
							.show();
				} else {
					startActivity(intent);
				}

				sendEvent("video - android", "click: " + mYtid, "download");
				break;

			case R.id.close:
				toggleCommentWindow(false);
				break;

			case R.id.submit:
				submitComment();
				break;
		}
	}

	private void report() {
		int cid = Integer.parseInt(mVideo.cid);
		if (cid != 5 && cid != 7 & cid != 8) {
			mVideoPresenter.report(mYtid);
		}
	}

	private void submitComment() {
		String comment = mCommentView.getText().toString().trim();

		if (!TextUtils.isEmpty(comment)) {
			String time = String.valueOf(System.currentTimeMillis() / 1000);
			String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

			mVideoPresenter.submitComment(mYtid, mUsername, comment, time, deviceId);
		} else {
			new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
					.setTitleText(getString(R.string.enter_content))
					.setConfirmClickListener((sDialog, __) -> sDialog.dismissWithAnimation())
					.show();
		}
	}

	private class RecyclerViewScrollDetectorImpl extends RecyclerViewScrollDetector {
		@Override
		public void onScrollDown() {
			showFab();
		}

		@Override
		public void onScrollUp() {
			hideFab();
		}
	}
}
