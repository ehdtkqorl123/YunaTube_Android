package ca.paulshin.yunatube.ui.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.message.Message;
import ca.paulshin.yunatube.util.MiscUtil;

/**
 * Created by paulshin on 16-02-22.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.SimpleViewHolder> {
	private List<Message> mMessages;
	private OnLoadMoreListener mOnLoadMoreListener;
	private boolean mIsLoading;
	// The minimum amount of items to have below your current scroll position before loading more.
	private int mVisibleThreshold = 5;
	private int mLastVisibleItem;
	private int mTotalItemCount;

	public static class SimpleViewHolder extends RecyclerView.ViewHolder {
		public TextView username;
		public TextView time;
		public TextView message;

		public SimpleViewHolder(View view) {
			super(view);
			username = ButterKnife.findById(view, R.id.username);
			time = ButterKnife.findById(view, R.id.time);
			message = ButterKnife.findById(view, R.id.message);
		}
	}

	public MessageAdapter(RecyclerView recyclerView) {
		mMessages = new ArrayList<>();

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

	public void insertNewMessage(Message message) {
		mMessages.add(0, message);
	}

	public void addMessages(boolean isRefreshing, List<Message> items) {
		if (isRefreshing) {
			mMessages.clear();
		}
		mMessages.addAll(items);
	}

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		mOnLoadMoreListener = onLoadMoreListener;
	}

	public void setLoaded() {
		mIsLoading = false;
	}

	@Override
	public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_message, parent, false);
		return new SimpleViewHolder(view);
	}

	@Override
	public void onBindViewHolder(SimpleViewHolder holder, int position) {
		Message message = mMessages.get(position);
		holder.username.setText(message.username);
		holder.message.setText(message.message);
		holder.time.setText(MiscUtil.getCreatedTime(message.time));
	}

	@Override
	public int getItemCount() {
		return mMessages.size();
	}
}