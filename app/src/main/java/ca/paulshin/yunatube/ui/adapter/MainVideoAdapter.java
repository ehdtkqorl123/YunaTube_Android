package ca.paulshin.yunatube.ui.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.util.PicassoUtil;
import ca.paulshin.yunatube.util.ResourceUtil;

/**
 * Created by paulshin on 14-12-05.
 */
public class MainVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int TYPE_HEADER = 0;
	private static final int TYPE_ITEM = 1;
	private List<Video> mVideos;
	private View mHeader;
	private OnLoadMoreListener mOnLoadMoreListener;
	private boolean mIsLoading;
	// The minimum amount of items to have below your current scroll position before loading more.
	private int mVisibleThreshold = 5;
	private int mLastVisibleItem;
	private int mTotalItemCount;

	public MainVideoAdapter(RecyclerView recyclerView, View header) {
		mHeader = header;
		mVideos = new ArrayList<>();

		// Implement loadmore
		if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
			final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
			recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
						@Override
						public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
							super.onScrolled(recyclerView, dx, dy);

							mTotalItemCount = linearLayoutManager.getItemCount();
							mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
							if (!mIsLoading && mTotalItemCount <= (mLastVisibleItem + mVisibleThreshold)) {
								if (mOnLoadMoreListener != null) {
									mOnLoadMoreListener.onLoadMore();
								}
								mIsLoading = true;
							}
						}
					});
		}
	}

	public void addVideos(List<Video> videos) {
		mVideos.addAll(videos);
	}

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		mOnLoadMoreListener = onLoadMoreListener;
	}

	public void setLoaded() {
		mIsLoading = false;
	}

	public static class HeaderViewHolder extends RecyclerView.ViewHolder {
		public View mHeader;

		public HeaderViewHolder(View header) {
			super(header);
			mHeader = header;
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == TYPE_ITEM) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_video, parent, false);
			ItemViewHolder ivh = new ItemViewHolder(v);
			return ivh;
		} else if (viewType == TYPE_HEADER) {
			HeaderViewHolder hvh = new HeaderViewHolder(mHeader);
			return hvh;
		}

		throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		if (viewHolder instanceof ItemViewHolder) {
			ItemViewHolder holder = (ItemViewHolder) viewHolder;
			Video video = getItem(position);
			String thumbnailUrl = String.format(Config.VIDEO_HQ_THUMBNAIL_URL, video.ytid);
			holder.title.setText(video.ytitle);
			holder.section.setText(video.stitle);
			int thumbnailWidth = ResourceUtil.getInteger(R.integer.thumbnail_resize_width);
			int thumbnailHeight = ResourceUtil.getInteger(R.integer.thumbnail_resize_height);
			PicassoUtil.loadImage(thumbnailUrl, holder.thumbnail, thumbnailWidth, thumbnailHeight, R.drawable.placeholder_gray);
			holder.thumbnail.setTag(video.ytid);
			holder.overflow.setTag(video);
		}
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		if (mHeader != null) {
			return mVideos.size() + 1;
		} else {
			return mVideos.size();
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (mHeader != null) {
			return isPositionHeader(position) ? TYPE_HEADER : TYPE_ITEM;
		} else {
			return TYPE_ITEM;
		}
	}

	private boolean isPositionHeader(int position) {
		if (mHeader != null) {
			return position == 0;
		} else {
			return false;
		}
	}

	private Video getItem(int position) {
		if (mHeader != null) {
			return mVideos.get(position - 1);
		} else {
			return mVideos.get(position);
		}
	}
}