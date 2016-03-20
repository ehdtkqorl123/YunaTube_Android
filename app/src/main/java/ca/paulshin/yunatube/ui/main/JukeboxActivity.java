package ca.paulshin.yunatube.ui.main;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.base.BaseYouTubeFailureRecoveryActivity;
import ca.paulshin.yunatube.util.ToastUtil;
import timber.log.Timber;

/**
 * Created by paulshin on 14-12-11.
 */
public class JukeboxActivity extends BaseYouTubeFailureRecoveryActivity implements
		YouTubePlayer.OnFullscreenListener, View.OnClickListener, JukeboxMvpView {

	@Inject
	JukeboxPresenter mJukeboxPresenter;

	@Bind(R.id.player)
	public YouTubePlayerView mPlayerView;
	@Bind(R.id.list)
	public RecyclerView mRecyclerView;
	@Bind(R.id.title)
	public TextView mTitleView;
	@Bind(R.id.loading)
	public ProgressBar mLoadingView;

	private MyPlayerStateChangeListener playerStateChangeListener;
	private YouTubePlayer player;
	private int currentIndex;
	private int totalIndex;
	private boolean mIsFullscreen;

	private List<Video> mVideos;
	private VideoAdapter mVideoAdapter;
	private View.OnClickListener mOnClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_jukebox);
		ButterKnife.bind(this);

		getActivityComponent().inject(this);
		mJukeboxPresenter.attachView(this);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mOnClickListener = this;

		loadData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mJukeboxPresenter.detachView();
	}

	@Override
	protected String getScreenName() {
		return "jukebox - android";
	}

	private void loadData() {
		mJukeboxPresenter.getVideos();
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showVideos(List<Video> videos) {
		mVideos = videos;
		totalIndex = mVideos.size();
		mPlayerView.initialize(Config.YOUTUBE_DEVELOPER_KEY, JukeboxActivity.this);
		playerStateChangeListener = new MyPlayerStateChangeListener();
	}

	@Override
	public void showError() {
		//TODO
	}

	/*****
	 * YouTube API
	 *****/
	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
		this.player = player;
		player.setPlayerStateChangeListener(playerStateChangeListener);
		player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
		player.setOnFullscreenListener(this);

		if (!wasRestored) {
			mVideoAdapter = new VideoAdapter();
			mRecyclerView.setAdapter(mVideoAdapter);
			if (!mVideos.isEmpty()) {
				player.loadVideo(mVideos.get(0).ytid);
			} else {
				ToastUtil.toast(this, R.string.server_unavailable);
			}
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
		LinearLayout.LayoutParams playerParams = (LinearLayout.LayoutParams) mPlayerView.getLayoutParams();
		if (mIsFullscreen) {
			// When in fullscreen, the visibility of all other views than the player should be set to
			// GONE and the player should be laid out across the whole screen.
			playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
			playerParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
		} else {
			ViewGroup.LayoutParams otherViewsParams = mRecyclerView.getLayoutParams();
			playerParams.width = otherViewsParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
			playerParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
			playerParams.weight = 0;
			otherViewsParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		}
	}

	@Override
	public void onClick(final View view) {
		int itemPosition = mRecyclerView.getChildPosition(view);

		currentIndex = itemPosition;
		player.loadVideo(mVideos.get(itemPosition).ytid);
		mVideoAdapter.notifyDataSetChanged();
	}

	private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {
		String playerState = "UNINITIALIZED";

		@Override
		public void onLoading() {
			mLoadingView.setVisibility(View.VISIBLE);
			mTitleView.setVisibility(View.GONE);
			playerState = "LOADING";
			Timber.i(playerState);
		}

		@Override
		public void onLoaded(String videoId) {
			mVideoAdapter.notifyDataSetChanged();
			mTitleView.setText(getString(R.string.song_title, currentIndex + 1, totalIndex, mVideos.get(currentIndex).ytitle));
			mLoadingView.setVisibility(View.GONE);
			mTitleView.setVisibility(View.VISIBLE);
			playerState = String.format("LOADED %s", videoId);
			Timber.i(playerState);
		}

		@Override
		public void onVideoStarted() {
			playerState = "VIDEO_STARTED @ " + currentIndex;
			Timber.i(playerState);
		}

		@Override
		public void onVideoEnded() {
			playerState = "VIDEO_ENDED @ " + currentIndex;
			Timber.i(playerState);
			currentIndex = (currentIndex == totalIndex - 1) ? 0 : currentIndex + 1;
			player.loadVideo(mVideos.get(currentIndex).ytid);
		}

		@Override
		public void onAdStarted() {
		}

		@Override
		public void onError(YouTubePlayer.ErrorReason reason) {
			playerState = "ERROR (" + reason + ")";
			if (reason == YouTubePlayer.ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) {
				// When this error occurs the player is released and can no longer be used.
				// player = null;
				// setControlsEnabled(false);
			}
			Timber.i(playerState);
		}
	}

	public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		public class ItemViewHolder extends RecyclerView.ViewHolder {
			public TextView mTitle;
			public ImageView mPlaying;

			public ItemViewHolder(View itemView) {
				super(itemView);
				mTitle = ButterKnife.findById(itemView, R.id.title);
				mPlaying = ButterKnife.findById(itemView, R.id.playing);
			}
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_jukebox, parent, false);
			v.setOnClickListener(mOnClickListener);
			ItemViewHolder ivh = new ItemViewHolder(v);
			return ivh;
		}

		// Replace the contents of a view (invoked by the layout manager)
		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
			ItemViewHolder holder = (ItemViewHolder) viewHolder;
			Video video = mVideos.get(position);

			holder.mTitle.setText(video.ytitle);
			holder.mTitle.setTypeface(null, position == currentIndex ? Typeface.BOLD : Typeface.NORMAL);
			holder.mPlaying.setVisibility(position == currentIndex ? View.VISIBLE : View.GONE);
		}

		@Override
		public int getItemCount() {
			return mVideos.size();
		}
	}
}
