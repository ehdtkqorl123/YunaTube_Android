package ca.paulshin.yunatube.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.flickr.Item;
import ca.paulshin.yunatube.ui.main.PhotoActivity;
import ca.paulshin.yunatube.util.PicassoUtil;
import ca.paulshin.yunatube.util.ViewUtil;

/**
 * Created by paulshin on 16-02-22.
 */
public class StreamLayoutAdapter extends RecyclerView.Adapter<StreamLayoutAdapter.SimpleViewHolder> {
	private static final String DIMENSION_REGEX = "width=\"(\\d+)\" height=\"(\\d+)\"";

	private final Context mContext;
	private final List<Item> mItems;
	private String [] mUrls;
	private int mImageWidth;

	public static class SimpleViewHolder extends RecyclerView.ViewHolder {
		public FrameLayout layout;
		public ImageView image;

		public SimpleViewHolder(View view) {
			super(view);
			layout = (FrameLayout) view;
			image = ButterKnife.findById(view, R.id.image);
		}
	}

	public StreamLayoutAdapter(Context context, List<Item> items) {
		mContext = context;
		mItems = items;
		mImageWidth = ViewUtil.getScreenSize()[0] / 3;

		mUrls = new String[mItems.size()];
		for (int i = 0; i < mItems.size(); i++) {
			mUrls[i] = mItems.get(i).media.get("m").replace("_m", "_b");
		}
	}

	@Override
	public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(mContext).inflate(R.layout.r_stream_item, parent, false);
		return new SimpleViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final SimpleViewHolder holder, int position) {
		Item item = mItems.get(position);

		// Get width and height
		Pattern pattern = Pattern.compile(DIMENSION_REGEX);
		Matcher matcher = pattern.matcher(item.description);
		String loadedWidth = "1", loadedHeight = "1";
		if (matcher.find()) {
			loadedWidth = matcher.group(1);
			loadedHeight = matcher.group(2);
		}

		int loadedWidthInt = Integer.parseInt(loadedWidth);
		int loadedHeightInt = Integer.parseInt(loadedHeight);
		int mImageHeight = loadedHeightInt * mImageWidth / loadedWidthInt;

		// Resize ImageView
		RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.layout.getLayoutParams();
		params.height = mImageHeight;
		params.width = mImageWidth;

		String url = item.media.get("m").replace("_m", "");
		PicassoUtil.loadImage(url, holder.image, R.drawable.placeholder_gray);

		holder.layout.setOnClickListener((v) -> {
			Activity activity = (Activity)v.getContext();
			Intent intent = new Intent(activity, PhotoActivity.class);
			intent.putExtra(PhotoActivity.EXTRA_URLS, mUrls);
			intent.putExtra(PhotoActivity.EXTRA_INDEX, position);
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
		});
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}
}