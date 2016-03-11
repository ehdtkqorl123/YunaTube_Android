package ca.paulshin.yunatube.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.flickr.Set;
import ca.paulshin.yunatube.ui.main.PhotoListActivity;
import ca.paulshin.yunatube.ui.main.PhotoSectionThumbnail;
import ca.paulshin.yunatube.util.PicassoUtil;

/**
 * Created by paulshin on 16-02-28.
 */
public class PhotoSectionLayoutAdapter extends RecyclerView.Adapter<PhotoSectionLayoutAdapter.SimpleViewHolder> {
	private final Context mContext;
	private final List<Set> mItems;

	public static class SimpleViewHolder extends RecyclerView.ViewHolder {
		public final ImageView thumbnail;
		public final TextView title;

		public SimpleViewHolder(View view) {
			super(view);
			thumbnail = ButterKnife.findById(view, R.id.thumbnail);
			title = ButterKnife.findById(view, R.id.title);
		}
	}

	public PhotoSectionLayoutAdapter(Context context, List<Set> items) {
		mContext = context;
		mItems = items;
	}

	@Override
	public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(mContext).inflate(R.layout.i_photo_section, parent, false);
		return new SimpleViewHolder(view);
	}

	@Override
	public void onBindViewHolder(SimpleViewHolder holder, int position) {
		final Set item = mItems.get(position);
		holder.title.setText(item.title);
		final PhotoSectionThumbnail thumbnail = PhotoSectionThumbnail.getInstance();
		PicassoUtil.loadImage(thumbnail.setThumbnailMap.get(item.id), holder.thumbnail, R.drawable.placeholder_gray);

		((View)holder.thumbnail.getParent().getParent()).setOnClickListener((v) -> {
			Activity activity = (Activity) v.getContext();
			String url = (String) v.getTag();
			Intent intent = new Intent(activity, PhotoListActivity.class);
			intent.putExtra(PhotoListActivity.EXTRA_SET_ID, item.id);
			intent.putExtra(PhotoListActivity.EXTRA_SET_TITLE, item.title);
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
		});
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}
}