package ca.paulshin.yunatube.ui.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.instagram.FeedItem;
import ca.paulshin.yunatube.util.PicassoUtil;
import ca.paulshin.yunatube.widgets.SquareImageView;

/**
 * Created by paulshin on 16-02-26.
 */
public class InstaFeedLayoutAdapter extends RecyclerView.Adapter<InstaFeedLayoutAdapter.SimpleViewHolder> {
	private List<FeedItem> mItems;
	private OnLoadMoreListener mOnLoadMoreListener;
	private boolean mIsLoading;
	// The minimum amount of items to have below your current scroll position before loading more.
	private int mVisibleThreshold = 18;
	private int mLastVisibleItem;
	private int mTotalItemCount;

	public InstaFeedLayoutAdapter(RecyclerView recyclerView) {
		mItems = new ArrayList<>();

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

	public void addItems(boolean isRefreshing, List<FeedItem> items) {
		if (isRefreshing) {
			mItems.clear();
		}
		mItems.addAll(items);
	}

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		mOnLoadMoreListener = onLoadMoreListener;
	}

	public void setLoaded() {
		mIsLoading = false;
	}

	public static class SimpleViewHolder extends RecyclerView.ViewHolder {
		public final SquareImageView thumbnail;
		public final ImageView instaPlayView;

		public SimpleViewHolder(View view) {
			super(view);
			thumbnail = ButterKnife.findById(view, R.id.thumbnail);
			instaPlayView = ButterKnife.findById(view, R.id.insta_video_play);
		}
	}

	@Override
	public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.i_insta, parent, false);
		return new SimpleViewHolder(view);
	}

	@Override
	public void onBindViewHolder(SimpleViewHolder holder, int position) {
		FeedItem item = mItems.get(position);
		String standardURl = item.images.standard_resolution.url;
		String url = standardURl;
		String videoUrl = item.videos != null ? item.videos.standard_resolution.url : null;
		PicassoUtil.loadImage(url, holder.thumbnail, R.drawable.placeholder_gray);
		((View)holder.thumbnail.getParent()).setTag(R.id.insta_photo_url, standardURl);

		View view = ((View)holder.thumbnail.getParent());
		if (videoUrl != null) {
			view.setTag(R.id.insta_video_url, videoUrl);
			view.setTag(R.id.insta_video_width, item.videos.standard_resolution.width);
			view.setTag(R.id.insta_video_height, item.videos.standard_resolution.height);
			holder.instaPlayView.setVisibility(View.VISIBLE);
		} else {
			view.setTag(R.id.insta_video_url, null);
			view.setTag(R.id.insta_video_width, null);
			view.setTag(R.id.insta_video_height, null);
			holder.instaPlayView.setVisibility(View.GONE);
		}
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}
}