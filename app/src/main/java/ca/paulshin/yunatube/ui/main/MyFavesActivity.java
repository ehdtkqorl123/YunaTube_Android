package ca.paulshin.yunatube.ui.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.dao.DBVideo;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.adapter.MyFaveVideoAdapter;
import ca.paulshin.yunatube.ui.base.BaseActivity;

public class MyFavesActivity extends BaseActivity implements MyFavesMvpView {

	@Inject
	MyFavesPresenter mMyFavesPresenter;

	@Bind(R.id.list)
	public RecyclerView mRecyclerView;
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

		final Toolbar toolbar = getActionBarToolbar();
		toolbar.setNavigationIcon(R.drawable.ic_up);
		toolbar.setNavigationOnClickListener((__) -> finish());

		int padding = getAdjustedPadding();
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setPadding(padding, 0, padding, 0);

		loadData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mMyFavesPresenter.detachView();
	}

	private void loadData() {
		mMyFavesPresenter.getMyFaves();
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showVideos(List<DBVideo> videos) {
		if (!videos.isEmpty()) {
			mNoneView.setVisibility(View.GONE);
			MyFaveVideoAdapter adapter = new MyFaveVideoAdapter(videos);
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
