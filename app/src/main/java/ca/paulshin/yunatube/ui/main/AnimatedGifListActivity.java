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
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.adapter.AnimatedGifListLayoutAdapter;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.events.DataLoadedEvent;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by paulshin on 15-01-31.
 */
public class AnimatedGifListActivity extends BaseActivity implements AnimatedGifListMvpView {

	@Inject
	AnimatedGifListPresenter mAnimatedGifListPresenter;
	@Inject
	Bus mBus;

	@Bind(R.id.grid)
	public RecyclerView mRecyclerView;
	@Bind(R.id.loading)
	public View mLoadingView;

	private String[] fileNames;

	@Override
	protected String getScreenName() {
		return "gifs  - android";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_photo_list);
		ButterKnife.bind(this);
		getActivityComponent().inject(this);
		mAnimatedGifListPresenter.attachView(this);

		setupToolbar();
		setTitle(R.string.animated_gifs_title);

		mRecyclerView.setLayoutManager(new GridLayoutManager(this, ResourceUtil.getInteger(R.integer.photos_columns)));

		loadData();
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

		mAnimatedGifListPresenter.detachView();
	}

	private void loadData() {
		mAnimatedGifListPresenter.getGifList();
	}

	public void onPhotoListClicked(final View view) {
		new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText(getString(R.string.data_warning_title))
				.setContentText(getString(R.string.data_warning_content))
				.setConfirmText(getString(R.string.ok))
				.setConfirmClickListener((sDialog, input) -> {
					sDialog.dismissWithAnimation();

					Intent intent = new Intent(AnimatedGifListActivity.this, AnimatedGifActivity.class);
					intent.putExtra(AnimatedGifActivity.EXTRA_FILENAMES, fileNames);
					intent.putExtra(AnimatedGifActivity.EXTRA_INDEX, (Integer) view.getTag());
					startActivity(intent);
					overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
				})
				.show();
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showGifs(List<String> gifs) {
		mLoadingView.setVisibility(View.GONE);
		fileNames = gifs.toArray(new String[0]);
		AnimatedGifListLayoutAdapter adapter = new AnimatedGifListLayoutAdapter(AnimatedGifListActivity.this, gifs);
		mRecyclerView.setAdapter(adapter);
	}

	@Override
	public void showError() {

	}

	@Subscribe
	public void onRefresh(DataLoadedEvent event) {
		onRefreshingStateChanged(event.refreshStarted);
	}
}
