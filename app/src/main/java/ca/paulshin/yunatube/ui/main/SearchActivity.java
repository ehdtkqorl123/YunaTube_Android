package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
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
	public static final String EXTRA_QUERY = "mQuery";

	private String mQuery;

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
		return "search - android: " + mQuery;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_search);
		ButterKnife.bind(this);

		getActivityComponent().inject(this);
		mSearchPresenter.attachView(this);

		mQuery = getIntent().getStringExtra(EXTRA_QUERY);

		setupToolbar();
		setTitle(mQuery);

		int padding = getAdjustedPadding();
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setPadding(padding, 0, padding, 0);

		searchVideos();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.menu_search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_search:
				Intent intent = new Intent(this, MainSearchActivity.class);
				startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mSearchPresenter.detachView();
	}

	private void searchVideos() {
		mSearchPresenter.getSearchVideos(mQuery);
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
