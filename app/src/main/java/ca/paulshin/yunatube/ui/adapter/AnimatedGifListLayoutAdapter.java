package ca.paulshin.yunatube.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import butterknife.ButterKnife;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.util.MiscUtil;
import ca.paulshin.yunatube.util.PicassoUtil;

/**
 * Created by paulshin on 16-02-28.
 */
public class AnimatedGifListLayoutAdapter extends RecyclerView.Adapter<AnimatedGifListLayoutAdapter.SimpleViewHolder> {
	private static final int CELL_WIDTH = 400;

	private final Context mContext;
	private final List<String> mItems;

	public static class SimpleViewHolder extends RecyclerView.ViewHolder {
		public final ImageView thumbnail;

		public SimpleViewHolder(View view) {
			super(view);
			thumbnail = ButterKnife.findById(view, R.id.thumbnail);
		}
	}

	public AnimatedGifListLayoutAdapter(Context context, List<String> items) {
		mContext = context;
		mItems = items;
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

		String url = String.format(Config.GIF_THUMBNAIL_URL, mItems.get(position), CELL_WIDTH, CELL_WIDTH, MiscUtil.getRandomInt());
		PicassoUtil.loadImage(url, holder.thumbnail);
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}
}