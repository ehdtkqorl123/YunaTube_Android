package ca.paulshin.yunatube.ui.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.adapter.MainVideoAdapter;
import ca.paulshin.yunatube.ui.base.BaseActivity;

public class SearchActivity extends BaseActivity implements SearchMvpView {
	public static final String EXTRA_QUERY = "query";

	private String query;

	@Inject
	SearchPresenter mSearchPresenter;

	@Bind(R.id.list)
	public RecyclerView mRecyclerView;
	@Bind(R.id.loading)
	public View mLoadingView;
	@Bind(R.id.none)
	public View mNoneView;

	@Override
	protected String getScreenName() {
		return "search - android: " + query;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_search);
		ButterKnife.bind(this);

		getActivityComponent().inject(this);
		mSearchPresenter.attachView(this);

		query = getIntent().getStringExtra(EXTRA_QUERY);

		setToolbar();
		setTitle(query);

		int padding = getAdjustedPadding();
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setPadding(padding, 0, padding, 0);

		searchVideos();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mSearchPresenter.detachView();
	}

	private void searchVideos() {
		mSearchPresenter.getSearchVideos(query);
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showVideos(List<Video> videos) {
		mLoadingView.setVisibility(View.GONE);

		if (!videos.isEmpty()) {
			MainVideoAdapter adapter = new MainVideoAdapter(mRecyclerView, null);
			adapter.addVideos(videos);
			mRecyclerView.setAdapter(adapter);
		} else {
			mNoneView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void showError() {
		//TODO
	}
}
