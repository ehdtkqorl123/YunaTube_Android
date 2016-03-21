package ca.paulshin.yunatube.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.instagram.Feed;
import ca.paulshin.yunatube.data.model.instagram.FeedItem;
import ca.paulshin.yunatube.data.model.main.Notice;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.receiver.ConnectivityChangeReceiver;
import ca.paulshin.yunatube.ui.adapter.MainVideoAdapter;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.ui.base.BaseFragment;
import ca.paulshin.yunatube.util.NetworkUtil;
import ca.paulshin.yunatube.util.PicassoUtil;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.ToastUtil;
import ca.paulshin.yunatube.util.events.ConnectivityChangeEvent;
import ca.paulshin.yunatube.widgets.FloatingActionsMenu;
import ca.paulshin.yunatube.widgets.RecyclerViewScrollDetector;
import ca.paulshin.yunatube.widgets.SquareImageView;
import timber.log.Timber;

public class MainMenuFragment extends BaseFragment implements
		View.OnTouchListener,
		FloatingActionsMenu.ToggleListener,
		View.OnClickListener,
		MainMenuMvpView {

	private static final boolean VIEW_SHARED = true;
	private static final int FAB_TRANSLATE_DURATION_MILLIS = 200;
	private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

	@Inject
	MainMenuPresenter mMainMenuPresenter;
	@Inject
	Bus mBus;

	private View mRootView;
	@Bind(R.id.veil)
	View mVeilView;
	private View mListHeaderView;
	@Bind(R.id.loading)
	View mLoadingView;
	@Bind(R.id.none)
	View mNoneView;
	private EditText mSearchView;
	@Bind(R.id.list)
	RecyclerView mRecyclerView;
	@Bind(R.id.main_fab)
	FloatingActionsMenu mFab;

	private String mLastNewOrder;
	private int mLoadCount;
	private MainVideoAdapter mAdapter;
	private Handler mFabHandler = new Handler();
	private ConnectivityChangeReceiver mConnectivityChangeReceiver;

	public static MainMenuFragment newInstance() {
		MainMenuFragment fragment = new MainMenuFragment();
		return fragment;
	}

	public MainMenuFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((BaseActivity)getActivity()).getActivityComponent().inject(this);
		mMainMenuPresenter.attachView(this);
		Timber.tag("MainMenuFragment");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.f_main, container, false);
		ButterKnife.bind(this, mRootView);

		mFab.setListener(this);
		mFab.setVisibility(View.GONE);

		int padding = getAdjustedPadding();

		mVeilView.setOnTouchListener(this);
		mListHeaderView = inflater.inflate(R.layout.p_main_header, null);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mAdapter = new MainVideoAdapter(mRecyclerView, mListHeaderView);
		mAdapter.setOnLoadMoreListener(() -> mMainMenuPresenter.getNewVideos(mLastNewOrder));
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setPadding(padding, 0, padding, 0);
		mRecyclerView.addOnScrollListener(new RecyclerViewScrollDetectorImpl());

		ButterKnife.findById(mListHeaderView, R.id.fact_more).setOnClickListener(this);
		ButterKnife.findById(mListHeaderView, R.id.insta_more).setOnClickListener(this);

		// Instagram items
		int instaLoadCount = ResourceUtil.getInteger(R.integer.insta_load_count);
		for (int i = 0; i < instaLoadCount; i++) {
			int id = ResourceUtil.getResourceId("id", "insta_frame_" + i);
			ButterKnife.findById(mListHeaderView, id).setOnClickListener(this);
		}

		// Initialize views
		initSearchView();
		initFabs();

		loadData();

		return mRootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mMainMenuPresenter.detachView();
	}

	/**
	 * Load notice, instagram feed and videos by making api calls if network is connected
	 */
	private void loadData() {
		if (NetworkUtil.isNetworkConnected(getActivity())) {
			mNoneView.setVisibility(View.GONE);
			mLoadingView.setVisibility(View.VISIBLE);

			mMainMenuPresenter.getNotice();
			mMainMenuPresenter.getNewInstaFeed();
			mMainMenuPresenter.getNewVideos(mLastNewOrder);
		} else {
			mRecyclerView.setVisibility(View.GONE);
			mLoadingView.setVisibility(View.GONE);
			mNoneView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		mConnectivityChangeReceiver = new ConnectivityChangeReceiver();
		getActivity().registerReceiver(mConnectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		mBus.register(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		getActivity().unregisterReceiver(mConnectivityChangeReceiver);
		mBus.unregister(this);
	}

	private void initSearchView() {
		final Context ctx = getActivity();
		final View clearSearchView = ButterKnife.findById(mListHeaderView, R.id.search_clear);
		mSearchView = ButterKnife.findById(mListHeaderView, R.id.search);

		final PopupMenu popupMenu = new PopupMenu(ctx, mSearchView);
		final int searchId = 101;
		popupMenu.getMenu().add(0, searchId, 1, R.string.action_search_video);
		popupMenu.setOnMenuItemClickListener((item) -> {
			switch (item.getItemId()) {
				case searchId:
					ToastUtil.toast(ctx, item.toString());
					break;
			}

			return true;
		});

		mSearchView.setOnKeyListener((v, keyCode, event) -> {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				EditText searchView = (EditText) v;
				switch (keyCode) {
					case KeyEvent.KEYCODE_SEARCH:
					case KeyEvent.KEYCODE_ENTER:
						if (!TextUtils.isEmpty(searchView.getText().toString().trim())) {
							Intent intent = new Intent(getActivity(), SearchActivity.class);
							intent.putExtra(SearchActivity.EXTRA_QUERY, searchView.getText().toString());
							startActivity(intent);
							getActivity().overridePendingTransition(R.anim.start_enter, R.anim.start_exit);

							sendEvent("main - android", "click", "search");
						}
						return true;
					default:
						break;
				}
				mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
			}
			return false;
		});

		mSearchView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void afterTextChanged(Editable s) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				clearSearchView.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
			}
		});

		clearSearchView.setOnClickListener(this);
	}

	private void initFabs() {
		ButterKnife.findById(mRootView, R.id.fab_settings).setOnClickListener(this);
		ButterKnife.findById(mRootView, R.id.fab_web_search).setOnClickListener(this);
		ButterKnife.findById(mRootView, R.id.fab_links).setOnClickListener(this);
		ButterKnife.findById(mRootView, R.id.fab_jukebox).setOnClickListener(this);
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showNotice(Notice notice) {
		if (TextUtils.isEmpty(notice.notice)) {
			ButterKnife.findById(mListHeaderView, R.id.notice_section).setVisibility(View.GONE);
		} else {
			TextView noticeText = ButterKnife.findById(mListHeaderView, R.id.notice_text);
			noticeText.setText(notice.notice);
		}

		if (TextUtils.isEmpty(notice.fact)) {
			ButterKnife.findById(mListHeaderView, R.id.fact_section).setVisibility(View.GONE);
		} else {
			TextView noticeText = ButterKnife.findById(mListHeaderView, R.id.fact_text);
			noticeText.setText(notice.fact);
		}

		Timber.i("showNotice: " + (notice != null));
		mLoadHandler.sendEmptyMessage(1);
	}

	@Override
	public void showError() {
		//TODO
		ButterKnife.findById(mListHeaderView, R.id.notice_section).setVisibility(View.GONE);
		ButterKnife.findById(mListHeaderView, R.id.fact_section).setVisibility(View.GONE);
	}

	@Override
	public void showNewInstaFeed(Feed feed) {
		List<FeedItem> feedItems = feed.data;

		for (int i = 0; i < feedItems.size(); i++) {
			FeedItem item = feedItems.get(i);
			int thumbnailId = ResourceUtil.getResourceId("id", "insta_thumb_" + i);
			SquareImageView thumbnailView = ButterKnife.findById(mListHeaderView, thumbnailId);

			int instaPlayId = ResourceUtil.getResourceId("id", "insta_video_play_" + i);
			ImageView instaPlayView = ButterKnife.findById(mListHeaderView, instaPlayId);

			String lowUrl = item.images.low_resolution.url;
			String standardUrl = item.images.standard_resolution.url;
			String videoUrl = item.videos != null ? item.videos.standard_resolution.url : null;
			PicassoUtil.loadImage(lowUrl, thumbnailView, R.drawable.placeholder_gray);
			thumbnailView.setTag(R.id.insta_photo_url, standardUrl);

			if (videoUrl != null) {
				thumbnailView.setTag(R.id.insta_video_url, videoUrl);
				thumbnailView.setTag(R.id.insta_video_width, item.videos.standard_resolution.width);
				thumbnailView.setTag(R.id.insta_video_height, item.videos.standard_resolution.height);
				instaPlayView.setVisibility(View.VISIBLE);
			} else {
				thumbnailView.setTag(R.id.insta_video_url, null);
				thumbnailView.setTag(R.id.insta_video_width, null);
				thumbnailView.setTag(R.id.insta_video_height, null);
				instaPlayView.setVisibility(View.GONE);
			}
		}

		Timber.i("showNewInstaFeed: " + feedItems.size());
		mLoadHandler.sendEmptyMessage(1);
	}

	@Override
	public void updateVideos(List<Video> videos) {
		if (!videos.isEmpty()) {
			mLastNewOrder = videos.get(videos.size() - 1).newOrder;

			if (TextUtils.equals(mLastNewOrder, "0")) {
				// After reaching the last one, deactivate loadmore
				mAdapter.setOnLoadMoreListener(null);
			}
			mAdapter.addVideos(videos);
			mAdapter.setLoaded();
			mAdapter.notifyDataSetChanged();
		}

		Timber.i("showVideos: " + videos.size());
		mLoadHandler.sendEmptyMessage(1);
	}

	private Handler mLoadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mLoadCount += msg.what;
			if (mLoadCount >= 3) {
				populateListItems();
			}
		}
	};

	private void populateListItems() {
		mLoadingView.postDelayed(() -> mLoadingView.setVisibility(View.GONE), 1000);
		mRecyclerView.postDelayed(() -> mRecyclerView.setVisibility(View.VISIBLE), 1000);
		mFab.postDelayed(() -> mFab.setVisibility(View.VISIBLE), 1000);
	}

	public void onInstaClicked(View view) {
		Activity activity = getActivity();
		String photoUrl = (String) view.getTag(R.id.insta_photo_url);
		String videoUrl = (String) view.getTag(R.id.insta_video_url);
		Integer videoWidth = (Integer) view.getTag(R.id.insta_video_width);
		Integer videoHeight = (Integer) view.getTag(R.id.insta_video_height);
		Intent intent;

		if (videoUrl == null) {
			intent = new Intent(activity, InstaPhotoActivity.class);
			intent.putExtra(InstaPhotoActivity.EXTRA_INSTA_PHOTO_URL, photoUrl);
		} else {
			intent = new Intent(activity, InstaVideoActivity.class);
			intent.putExtra(InstaVideoActivity.EXTRA_INSTA_PHOTO_URL, photoUrl);
			intent.putExtra(InstaVideoActivity.EXTRA_INSTA_VIDEO_URL, videoUrl);
			intent.putExtra(InstaVideoActivity.EXTRA_INSTA_VIDEO_WIDTH, videoWidth);
			intent.putExtra(InstaVideoActivity.EXTRA_INSTA_VIDEO_HEIGHT, videoHeight);
		}

//		ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, view, "insta");
//		activity.startActivity(intent, options.toBundle());

		startActivity(intent);
		activity.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
	}

	/*****
	 * FAB
	 *****/

	private boolean mFabToggled;
	private boolean mPrevVisibility = true;

	public boolean closeFab() {
		if (mFab != null) {
			boolean wasFabOpen = mFab.isExpanded();
			if (wasFabOpen) {
				mFab.collapse();
			}
			return !wasFabOpen;
		}
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mFab.collapse();
		return true;
	}

	@Override
	public void onToggle(boolean isExpand) {
		MainActivity activity = (MainActivity)getActivity();
		mVeilView.setVisibility(isExpand ? View.VISIBLE : View.GONE);
		if (activity != null) {
			activity.toggleVeil(isExpand);
		}
	}

	public void showFab() {
		toggleFab(true);
	}

	public void hideFab() {
		toggleFab(false);
	}

	private void toggleFab(final boolean visible) {
		if (!mFabToggled && mPrevVisibility != visible) {
			mFabToggled = true;
			mPrevVisibility = visible;

			if (mFab.isExpanded()) {
				mFab.collapse();
			}

			int fabHeight = mFab.getHeight();
			int translationY = visible ? 0 : fabHeight + ResourceUtil.getDimensionInPx(R.dimen.fab_padding_y);

			ViewPropertyAnimator
					.animate(mFab)
					.setInterpolator(mInterpolator)
					.setDuration(FAB_TRANSLATE_DURATION_MILLIS)
					.translationY(translationY)
					.setListener(new Animator.AnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {}

						@Override
						public void onAnimationEnd(Animator animation) {
							mFabToggled = false;
						}

						@Override
						public void onAnimationCancel(Animator animation) {}

						@Override
						public void onAnimationRepeat(Animator animation) {}
					});
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fab_settings:
				startActivity(new Intent(getActivity(), SettingsActivity.class));
				getActivity().overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
				break;
			case R.id.fab_web_search:
				if (mFab.isExpanded())
					mFab.collapse();

				FragmentManager fm = getChildFragmentManager();
				WebSearchDialogFragment f = WebSearchDialogFragment.getInstance();
				f.show(fm, "fragment_web_search");
				return;
			case R.id.fab_jukebox:
				startActivity(new Intent(getActivity(), JukeboxActivity.class));
				getActivity().overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
				break;
			case R.id.fab_links:
				startActivity(new Intent(getActivity(), FamilySitesActivity.class));
				getActivity().overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
				break;
			case R.id.fact_more:
				Uri uri = Uri.parse(Config.YUNAFACT);
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
				getActivity().overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
				sendScreen("fact - android");
				break;
			case R.id.insta_more:
				startActivity(new Intent(getActivity(), InstaFeedActivity.class));
				getActivity().overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
				break;
			case R.id.search_clear:
				mSearchView.setText("");
				v.setVisibility(View.GONE);
				break;
			case R.id.insta_frame_0:
			case R.id.insta_frame_1:
			case R.id.insta_frame_2:
			case R.id.insta_frame_3:
			case R.id.insta_frame_4:
			case R.id.insta_frame_5:
			case R.id.insta_frame_6:
			case R.id.insta_frame_7:
			case R.id.insta_frame_8:
			case R.id.insta_frame_9:
				onInstaClicked(((ViewGroup) v).getChildAt(0));
				break;
		}

		if (mFab.isExpanded()) {
			mFabHandler.postDelayed(() -> mFab.collapse(), 1000);
		}
	}

	public void hideSoftKey() {
		if (isAdded()) {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
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

	@Subscribe
	public void onConnectivityChange(ConnectivityChangeEvent status) {
		if (status.networkEnabled) {
			loadData();
		} else {
			//TODO
			ToastUtil.toast(getActivity(), "No internet");
		}
	}
}
