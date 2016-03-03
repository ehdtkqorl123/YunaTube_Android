package ca.paulshin.yunatube.ui.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.video.Comment;
import ca.paulshin.yunatube.util.MiscUtil;
import ca.paulshin.yunatube.util.ResourceUtil;

/**
 * Created by paulshin on 16-02-22.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int TYPE_HEADER = 0;
	private static final int TYPE_ITEM = 1;
	private List<Comment> mComments;
	private View mHeader;
	private OnLoadMoreListener mOnLoadMoreListener;
	private boolean mIsLoading;
	// The minimum amount of items to have below your current scroll position before loading more.
	private int mVisibleThreshold = 5;
	private int mLastVisibleItem;
	private int mTotalItemCount;

	public CommentAdapter(RecyclerView recyclerView, View header) {
		mHeader = header;
		mComments = new ArrayList<>();

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

	public void insertNewComment(Comment comment) {
		mComments.add(0, comment);
	}

	public void addComments(boolean isRefreshing, List<Comment> comments) {
		if (isRefreshing) {
			mComments.clear();
		}
		mComments.addAll(comments);
	}

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		mOnLoadMoreListener = onLoadMoreListener;
	}

	public void setLoaded() {
		mIsLoading = false;
	}

	public static class ItemViewHolder extends RecyclerView.ViewHolder {
		public ImageView avatar;
		public TextView username;
		public TextView time;
		public TextView comment;

		public ItemViewHolder(View itemView) {
			super(itemView);
			avatar = ButterKnife.findById(itemView, R.id.avatar);
			username = ButterKnife.findById(itemView, R.id.username);
			time = ButterKnife.findById(itemView, R.id.created_on);
			comment = ButterKnife.findById(itemView, R.id.comment);
		}
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
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_comment, parent, false);
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
			Comment comment = getItem(position);
			int rand = (Math.abs(comment.username.hashCode()) % 5) + 1;
			holder.avatar.setImageResource(ResourceUtil.getResourceId("drawable", "ic_sn_" + rand));
			holder.username.setText(comment.username);
			holder.time.setText(MiscUtil.getCreatedTime(comment.time));
			holder.comment.setText(comment.message);
		}
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		if (mHeader != null) {
			return mComments.size() + 1;
		} else {
			return mComments.size();
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

	private Comment getItem(int position) {
		if (mHeader != null) {
			return mComments.get(position - 1);
		} else {
			return mComments.get(position);
		}
	}
}