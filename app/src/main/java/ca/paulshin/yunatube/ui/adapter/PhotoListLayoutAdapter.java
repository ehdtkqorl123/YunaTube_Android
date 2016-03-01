package ca.paulshin.yunatube.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.flickr.Photo;
import ca.paulshin.yunatube.util.PicassoUtil;

/**
 * Created by paulshin on 16-02-28.
 */
public class PhotoListLayoutAdapter extends RecyclerView.Adapter<PhotoListLayoutAdapter.SimpleViewHolder> {
	private final Context mContext;
	private final List<Photo> mItems;
	private String[] mUrls;

	public static class SimpleViewHolder extends RecyclerView.ViewHolder {
		public final ImageView thumbnail;

		public SimpleViewHolder(View view) {
			super(view);
			thumbnail = ButterKnife.findById(view, R.id.thumbnail);
		}
	}

	public PhotoListLayoutAdapter(Context context, List<Photo> items, String[] urls) {
		mContext = context;
		mItems = items;
		mUrls = urls;
	}

	@Override
	public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(mContext).inflate(R.layout.i_photo_list, parent, false);
		return new SimpleViewHolder(view);
	}

	@Override
	public void onBindViewHolder(SimpleViewHolder holder, int position) {
		View view = (View) holder.thumbnail.getParent();
		view.setTag(position);

		PicassoUtil.loadImage(mUrls[position], holder.thumbnail);
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}
}