package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
import ca.paulshin.yunatube.ui.adapter.InstaFeedLayoutAdapter;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.events.DataLoadedEvent;

public class InstaFeedActivity extends BaseActivity implements InstaFeedMvpView {

	@Inject
	InstaFeedPresenter mInstaFeedPresenter;
	@Inject
	Bus mBus;

	private InstaFeedLayoutAdapter mAdapter;

	private boolean mIsRefreshing;
	private String mNextMaxId;

	@Bind(R.id.grid)
	public RecyclerView mRecyclerView;
	@Bind(R.id.loading)
	public View mLoadingView;

	@Override
	protected String getScreenName() {
		return "instagram_feed - android";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_insta_feed);
		ButterKnife.bind(this);

		getActivityComponent().inject(this);
		mInstaFeedPresenter.attachView(this);

		setToolbar();

		mRecyclerView.setLayoutManager(new GridLayoutManager(this, ResourceUtil.getInteger(R.integer.photos_columns)));
		mAdapter = new InstaFeedLayoutAdapter(mRecyclerView);
		mAdapter.setOnLoadMoreListener(() -> mInstaFeedPresenter.getInstaFeed(mNextMaxId));
		mRecyclerView.setAdapter(mAdapter);

		loadInstaFeed();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mBus.register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mBus.unregister(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mInstaFeedPresenter.detachView();
	}

	private void loadInstaFeed() {
		mInstaFeedPresenter.getInstaFeed(mNextMaxId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.menu_insta_feed, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_instagram:
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(Config.INSTAGRAM_LOVEQYN_URL));
				startActivity(intent);
				break;

			case R.id.action_refresh:
				requestDataRefresh();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void requestDataRefresh() {
		super.requestDataRefresh();
		InstaFeedLayoutAdapter adapter = (InstaFeedLayoutAdapter) mRecyclerView.getAdapter();
		if (adapter != null) {
			mIsRefreshing = true;
			mNextMaxId = null;
			loadInstaFeed();
		}
	}

	public void onInstaClicked(View view) {
		String photoUrl = (String) view.getTag(R.id.insta_photo_url);
		String videoUrl = (String) view.getTag(R.id.insta_video_url);
		Integer videoWidth = (Integer) view.getTag(R.id.insta_video_width);
		Integer videoHeight = (Integer) view.getTag(R.id.insta_video_height);
		Intent intent;

		if (videoUrl == null) {
			intent = new Intent(this, InstaPhotoActivity.class);
			intent.putExtra(InstaPhotoActivity.EXTRA_INSTA_PHOTO_URL, photoUrl);
		} else {
			intent = new Intent(this, InstaVideoActivity.class);
			intent.putExtra(InstaVideoActivity.EXTRA_INSTA_PHOTO_URL, photoUrl);
			intent.putExtra(InstaVideoActivity.EXTRA_INSTA_VIDEO_URL, videoUrl);
			intent.putExtra(InstaVideoActivity.EXTRA_INSTA_VIDEO_WIDTH, videoWidth);
			intent.putExtra(InstaVideoActivity.EXTRA_INSTA_VIDEO_HEIGHT, videoHeight);
		}

//		ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view, "insta");
//		startActivity(intent, options.toBundle());

		startActivity(intent);
		overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showInstaFeed(Feed feed) {
		List<FeedItem> feedItems = feed.data;
		mNextMaxId = feed.pagination.next_max_id;

		if (!feedItems.isEmpty()) {
			mLoadingView.setVisibility(View.GONE);

			mNextMaxId = feed.pagination.next_max_id;
			mAdapter.addItems(mIsRefreshing, feedItems);
			mAdapter.setLoaded();
			mAdapter.notifyDataSetChanged();

			mBus.post(new DataLoadedEvent(false));
			mIsRefreshing = false;
		}
	}

	@Override
	public void showError() {
		//TODO
	}

	@Subscribe
	public void onRefresh(DataLoadedEvent event) {
		onRefreshingStateChanged(event.refreshStarted);
	}
}
