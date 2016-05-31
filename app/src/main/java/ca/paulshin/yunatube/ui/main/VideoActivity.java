package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
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
import ca.paulshin.yunatube.util.ToastUtil;
import ca.paulshin.yunatube.util.YTPreference;
import ca.paulshin.yunatube.util.events.VideoDeletedEvent;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class VideoActivity extends BaseYouTubeFailureRecoveryActivity implements
		YouTubePlayer.OnFullscreenListener,
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
	@Bind(R.id.fab_action)
	View mActionView;
	@Bind(R.id.comment_box)
	FrameLayout mCommentBox;
	@Bind(R.id.menu_box)
	FrameLayout mMenuBox;
	@Bind(R.id.content)
	EditText mCommentView;
	@Bind(R.id.action_favorite)
	TextView mFavoriteTextView;
	@Bind(R.id.icon_favorite)
	ImageView mFavoriteImageView;
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

	private BottomSheetBehavior mMenuBottomSheetBehavior;
	private BottomSheetBehavior mCommentBottomSheetBehavior;

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

		mMenuBottomSheetBehavior = BottomSheetBehavior.from(mMenuBox);
		mCommentBottomSheetBehavior = BottomSheetBehavior.from(mCommentBox);

		mVideoPresenter.getFaveStatus(mYtid);

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
		if (mCommentBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
			closeCommentWindow(null);
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
		} else {
			ViewGroup.LayoutParams otherViewsParams = mRecyclerView.getLayoutParams();
			playerParams.width = otherViewsParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
			playerParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

			mRecyclerView.setVisibility(View.VISIBLE);
		}
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
		closeCommentWindow(null);
		mAdapter.insertNewComment(comment);
		mAdapter.notifyDataSetChanged();

		sendEvent("video - android", "send: " + mVideo.ytid, "comment");
	}

	@Override
	public void setFaveStatus(int id) {
		mVideoKey = id;
		boolean isFaved = id != -1;
		mFavoriteTextView.setText(isFaved ? R.string.youtube_remove_from_my_faves : R.string.youtube_add_to_my_faves);
		mFavoriteImageView.setImageResource(isFaved ? R.drawable.ic_unfavorite_gray : R.drawable.ic_favorite_gray);
	}

	@Override
	public void addedFave(Video dbVideo) {
		if (dbVideo != null) {
			ToastUtil.toast(this, R.string.faves_add_success);
			mVideoPresenter.getFaveStatus(mYtid);
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

			mFavoriteTextView.setText(R.string.youtube_add_to_my_faves);
			mFavoriteImageView.setBackgroundResource(R.drawable.ic_favorite_gray);

			sendEvent("video - android", "remove: " + mYtid, "fave");
		} else {
			ToastUtil.toast(this, R.string.faves_remove_failure);
		}
	}

	@Override
	public void showError() {
		//TODO
	}

	public void openActionMenu(View view) {
		mCommentBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		mMenuBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
	}

	public void comment(View view) {
		closeActionWindow();

		if (!TextUtils.isEmpty(mUsername))
			mCommentBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
	}

	public void favorite(View view) {
		closeActionWindow();

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
	}

	public void watchOnYouTube(View view) {
		closeActionWindow();

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(String.format(Config.YOUTUBE_SHARE_URL_PREFIX, mYtid)));
		startActivity(intent);

		sendEvent("video - android", "click: " + mYtid, "youtubeapp");
	}

	public void report(View view) {
		closeActionWindow();

		new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText(getString(R.string.report_video))
				.setContentText(getString(R.string.is_video_blocked))
				.setConfirmText(getString(R.string.yes))
				.setConfirmClickListener((sDialog, __) -> {
					sDialog.dismissWithAnimation();
					int cid = Integer.parseInt(mVideo.cid);
					if (cid != 5 && cid != 7 & cid != 8) {
						mVideoPresenter.report(mYtid);
					}
				})
				.setCancelText(getString(R.string.no))
				.setCancelClickListener((sDialog, __) -> sDialog.dismissWithAnimation())
				.show();
	}

	public void share(View view) {
		closeActionWindow();

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, mVideo.ytitle);
		intent.putExtra(Intent.EXTRA_TEXT, mVideo.stitle + " - " + mVideo.ytitle + " : http://youtu.be/" + mVideo.ytid);
		startActivity(Intent.createChooser(intent, getString(R.string.share_via)));

		sendEvent("video - android", "click: " + mVideo.ytid, "share");
	}

	public void downloadVideo(View view) {
		closeActionWindow();

		String dlUrl = "http://ssyoutube.com/watch?v=" + mYtid;
		Intent intent = new Intent(Intent.ACTION_VIEW);
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
	}

	public void submit(View view) {
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

	private void closeActionWindow() {
		mMenuBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
	}

	public void closeCommentWindow(View view) {
		mCommentBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
	}
}
