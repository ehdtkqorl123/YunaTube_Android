package ca.paulshin.yunatube.ui.main;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.flickr.CollectionItem;
import ca.paulshin.yunatube.ui.adapter.PhotoSectionLayoutAdapter;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.ResourceUtil;

public class PhotoSectionActivity extends BaseActivity {
	public static final String EXTRA_COLLECTION = "collection";

	private CollectionItem collection;

	@Bind(R.id.grid)
	public RecyclerView mRecyclerView;
	@Bind(R.id.loading)
	public View mLoadingView;

	@Override
	protected String getScreenName() {
		return "album_set - android";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_photo_section);
		ButterKnife.bind(this);

		collection = getIntent().getParcelableExtra(EXTRA_COLLECTION);

		setupToolbar();
		setTitle(collection.title);

		mRecyclerView.setLayoutManager(new GridLayoutManager(this, ResourceUtil.getInteger(R.integer.photo_sections_columns)));

		if (collection.set.size() > 0) {
			PhotoSectionLayoutAdapter adapter = new PhotoSectionLayoutAdapter(PhotoSectionActivity.this, collection.set);
			mLoadingView.setVisibility(View.GONE);
			mRecyclerView.setAdapter(adapter);
		}
	}
}
