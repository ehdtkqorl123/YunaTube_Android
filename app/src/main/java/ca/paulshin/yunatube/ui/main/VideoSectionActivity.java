package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.video.Section;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.events.DataLoadedEvent;

/**
 * Created by paulshin on 14-12-18.
 */
public class VideoSectionActivity extends BaseActivity implements View.OnClickListener, VideoSectionMvpView {

	@Inject
	VideoSectionPresenter mVideoSectionPresenter;
	@Inject
	Bus mBus;

	@Bind(R.id.header)
	public ImageView mHeader;
	@Bind(R.id.content)
	public LinearLayout mContents;

	public static final String EXTRA_CID = "cid";
	public static final String EXTRA_CTITLE = "ctitle";
	public static final String EXTRA_THUMBNAIL_RES = "thumbnail_res";

	private String cid;
	private int ctitleRes;
	private int thumbnailRes;

	@Override
	protected String getScreenName() {
		return "video_section - android";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_video_section);
		ButterKnife.bind(this);
		getActivityComponent().inject(this);
		mVideoSectionPresenter.attachView(this);

		Intent intent = getIntent();
		cid = intent.getStringExtra(EXTRA_CID);
		ctitleRes = intent.getIntExtra(EXTRA_CTITLE, 0);
		thumbnailRes = intent.getIntExtra(EXTRA_THUMBNAIL_RES, 0);

		mHeader.setImageResource(thumbnailRes);

		setToolbar();

		CollapsingToolbarLayout collapsingToolbar =
				(CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		collapsingToolbar.setTitle(getString(ctitleRes));

		loadData();
	}

	private void loadData() {
		mVideoSectionPresenter.getSections(cid);
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

		mVideoSectionPresenter.detachView();
	}

	@Override
	public void onClick(View v) {
		Section section = (Section)v.getTag();

		Intent intent = new Intent(this, VideoListActivity.class);
		intent.putExtra(VideoListActivity.EXTRA_CID, cid);
		intent.putExtra(VideoListActivity.EXTRA_SID, section.sid);
		intent.putExtra(VideoListActivity.EXTRA_STITLE, section.stitle);
		startActivity(intent);
		overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showSections(List<Section> sections) {
		for (Section section : sections) {
			View view = LayoutInflater.from(VideoSectionActivity.this).inflate(R.layout.r_title, null);
			view.setTag(section);
			view.setOnClickListener(VideoSectionActivity.this);
			TextView title = ButterKnife.findById(view, R.id.title);
			title.setText(section.stitle);
			mContents.addView(view);
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