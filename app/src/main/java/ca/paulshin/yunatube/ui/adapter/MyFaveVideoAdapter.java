package ca.paulshin.yunatube.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.util.PicassoUtil;

/**
 * Created by paulshin on 14-12-05.
 */
public class MyFaveVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private List<Video> mVideos;

	public MyFaveVideoAdapter(List<Video> videos) {
		mVideos = videos;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_video, parent, false);
		ItemViewHolder ivh = new ItemViewHolder(v);
		return ivh;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		ItemViewHolder holder = (ItemViewHolder) viewHolder;
		holder.setOnRemoveListener(() -> {
			notifyItemRemoved(position);
			mVideos.remove(position);
			holder.overflow.postDelayed(() -> notifyDataSetChanged(), 300);
		});
		Video video = getItem(position);
		String thumbnailUrl = String.format(Config.VIDEO_HQ_THUMBNAIL_URL, video.ytid);
		holder.title.setText(video.ytitle);
		holder.section.setText(video.stitle);
		PicassoUtil.loadImage(thumbnailUrl, holder.thumbnail, 240, 180, R.drawable.placeholder_gray);
		holder.thumbnail.setTag(video.ytid);
		holder.overflow.setTag(video);
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mVideos.size();
	}

	private Video getItem(int position) {
		return mVideos.get(position);
	}
}