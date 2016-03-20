package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.flickr.Photo;
import ca.paulshin.yunatube.data.model.flickr.PhotosetObject;
import ca.paulshin.yunatube.ui.adapter.PhotoListLayoutAdapter;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.events.DataLoadedEvent;

public class PhotoListActivity extends BaseActivity implements PhotoListMvpView {

	@Inject
	PhotoListPresenter mPhotoListPresenter;
	@Inject
	Bus mBus;

	public static final String EXTRA_SET_ID = "set_id";
	public static final String EXTRA_SET_TITLE = "set_title";

	private static final String PHOTO_SIZE = "m";

	private String setId;
	private String setTitle;

	private String [] urls;

	@Bind(R.id.grid)
	public RecyclerView mRecyclerView;
	@Bind(R.id.loading)
	public View mLoadingView;

	@Override
	protected String getScreenName() {
		return "album_photo  - android";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_photo_list);
		ButterKnife.bind(this);
		getActivityComponent().inject(this);
		mPhotoListPresenter.attachView(this);

		setId = getIntent().getStringExtra(EXTRA_SET_ID);
		setTitle = getIntent().getStringExtra(EXTRA_SET_TITLE);

		setToolbar();
		setTitle(setTitle);

		mRecyclerView.setLayoutManager(new GridLayoutManager(this, ResourceUtil.getInteger(R.integer.photos_columns)));

		loadData();
	}

	private void loadData() {
		mPhotoListPresenter.getPhotoList(setId);
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

		mPhotoListPresenter.detachView();
	}

	public void onPhotoListClicked(View view) {
		Intent intent = new Intent(this, PhotoActivity.class);
		intent.putExtra(PhotoActivity.EXTRA_URLS, urls);
		intent.putExtra(PhotoActivity.EXTRA_INDEX, (Integer)view.getTag());
		startActivity(intent);
		overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showPhotos(PhotosetObject photosetObject) {
		List<Photo> items = photosetObject.photoset.photo;

		// Create urls array
		urls = new String[items.size()];
		for (int i = 0; i < items.size(); i++) {
			Photo photo = items.get(i);
			String url = String.format(Config.FLICKR_FORMAT, photo.farm, photo.server, photo.id, photo.secret, PHOTO_SIZE);
			urls[i] = url;
		}

		if (items != null) {
			mLoadingView.setVisibility(View.GONE);
			PhotoListLayoutAdapter adapter = new PhotoListLayoutAdapter(PhotoListActivity.this, items, urls);
			mRecyclerView.setAdapter(adapter);

			mBus.post(new DataLoadedEvent(false));
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
