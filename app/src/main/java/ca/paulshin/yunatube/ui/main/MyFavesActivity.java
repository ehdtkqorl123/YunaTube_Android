package ca.paulshin.yunatube.ui.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.adapter.MyFaveVideoAdapter;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.events.VideoDeletedEvent;
import ca.paulshin.yunatube.widgets.RecyclerViewEmptySupport;

public class MyFavesActivity extends BaseActivity implements MyFavesMvpView {

	@Inject
	MyFavesPresenter mMyFavesPresenter;
	@Inject
	Bus mBus;

	@Bind(R.id.list)
	public RecyclerViewEmptySupport mRecyclerView;
	@Bind(R.id.none)
	public View mNoneView;

	@Override
	protected String getScreenName() {
		return "faves - android";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_my_faves);
		ButterKnife.bind(this);

		getActivityComponent().inject(this);
		mMyFavesPresenter.attachView(this);
		mBus.register(this);

		setToolbar();

		int padding = getAdjustedPadding();
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setPadding(padding, 0, padding, 0);
		mRecyclerView.setEmptyView(mNoneView);

		loadData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mMyFavesPresenter.detachView();
		mBus.unregister(this);
	}

	private void loadData() {
		mMyFavesPresenter.getMyFaves();
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showVideos(List<Video> videos) {
		MyFaveVideoAdapter adapter = new MyFaveVideoAdapter(videos);
		mRecyclerView.setAdapter(adapter);
	}

	@Override
	public void showError() {
		//TODO
	}

	@Subscribe
	public void onVideoDeleted(VideoDeletedEvent event) {
		loadData();
	}
}
