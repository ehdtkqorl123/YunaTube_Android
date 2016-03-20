package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.adapter.VideoAdapter;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.events.DataLoadedEvent;

public class VideoListActivity extends BaseActivity implements VideoListMvpView {
	public static final String EXTRA_CID = "cid";
	public static final String EXTRA_SID = "sid";
	public static final String EXTRA_STITLE = "stitle";

	@Inject
	VideoListPresenter mVideoListPresenter;
	@Inject
	Bus mBus;

	@Bind(R.id.list)
	public RecyclerView mRecyclerView;
	@Bind(R.id.loading)
	public View mLoadingView;
	@Bind(R.id.none)
	public View mNoneView;

	private String cid;
	private String sid;
	private String stitle;

	@Override
	protected String getScreenName() {
		return "video_list - android";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_videos);
		ButterKnife.bind(this);
		getActivityComponent().inject(this);
		mVideoListPresenter.attachView(this);

		Intent intent = getIntent();
		cid = intent.getStringExtra(EXTRA_CID);
		sid = intent.getStringExtra(EXTRA_SID);
		stitle = intent.getStringExtra(EXTRA_STITLE);

		setToolbar();
		setTitle(stitle);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		int padding = getAdjustedPadding();
		mRecyclerView.setPadding(padding, 0, padding, 0);

		loadData();
	}

	private void loadData() {
		mVideoListPresenter.getVideos(cid, sid);
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

		mVideoListPresenter.detachView();
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showVideos(List<Video> videos) {
		mLoadingView.setVisibility(View.GONE);
		int size = videos.size();
		if (size > 0) {
			for (Video video : videos)
				video.stitle = stitle;

			VideoAdapter adapter = new VideoAdapter(videos);
			mRecyclerView.setAdapter(adapter);
		} else {
			mNoneView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void showError() {

	}

	@Subscribe
	public void onRefresh(DataLoadedEvent event) {
		onRefreshingStateChanged(event.refreshStarted);
	}
}
