package ca.paulshin.yunatube.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.flickr.CollectionItem;
import ca.paulshin.yunatube.ui.main.PhotoSectionActivity;
import ca.paulshin.yunatube.util.ResourceUtil;

/**
 * Created by paulshin on 16-02-22.
 */
public class CollectionsLayoutAdapter extends RecyclerView.Adapter<CollectionsLayoutAdapter.SimpleViewHolder> {
	public interface OnGifClickedListener {
		void onGifClicked();
	}

	private final Context mContext;
	private final List<CollectionItem> mItems;
	private OnGifClickedListener mListener;

	public static class SimpleViewHolder extends RecyclerView.ViewHolder {
		public TextView title;

		public SimpleViewHolder(View view) {
			super(view);
			title = ButterKnife.findById(view, R.id.title);
		}
	}

	public CollectionsLayoutAdapter(Context context, OnGifClickedListener listener, List<CollectionItem> items) {
		mContext = context;
		mListener = listener;
		mItems = items;
	}

	@Override
	public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(mContext).inflate(R.layout.r_title, parent, false);
		return new SimpleViewHolder(view);
	}

	@Override
	public void onBindViewHolder(SimpleViewHolder holder, int position) {
		final CollectionItem collection = mItems.get(position);
		holder.title.setText(collection.title);
		if (position == 0) {
			// For animated gif row
			holder.title.setTypeface(null, Typeface.BOLD);
			holder.title.setBackgroundColor(ResourceUtil.getColor(R.color.transparent));
			((View) holder.title.getParent()).setOnClickListener((v) -> {
				if (mListener != null) {
					mListener.onGifClicked();
				}
			});
		} else {
			// For other flickr image row
			holder.title.setTypeface(null, Typeface.NORMAL);
			holder.title.setBackgroundColor(ResourceUtil.getColor(R.color.window_background));
			((View) holder.title.getParent()).setOnClickListener((v) -> {
				Activity activity = (Activity) v.getContext();
				String id = collection.id;

				for (CollectionItem item : mItems) {
					if (TextUtils.equals(id, item.id)) {
						Intent intent = new Intent(activity, PhotoSectionActivity.class);
						intent.putExtra(PhotoSectionActivity.EXTRA_COLLECTION, item);
						activity.startActivity(intent);
						activity.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
						break;
					}
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}
}