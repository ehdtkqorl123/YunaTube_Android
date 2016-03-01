package ca.paulshin.yunatube.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ca.paulshin.dao.DaoMaster;
import ca.paulshin.dao.DaoSession;
import ca.paulshin.dao.VideoDao;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.db.DBHelper;
import ca.paulshin.yunatube.util.PicassoUtil;

/**
 * Created by paulshin on 14-12-05.
 */
public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private List<Video> mVideos;

	private VideoDao videoDao;

	public VideoAdapter(List<Video> videos) {
		mVideos = videos;

		DaoMaster daoMaster = DBHelper.getDaoMaster();
		DaoSession daoSession = daoMaster.newSession();
		videoDao = daoSession.getVideoDao();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_video, parent, false);
		ItemViewHolder ivh = new ItemViewHolder(v, videoDao);
		return ivh;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		ItemViewHolder holder = (ItemViewHolder) viewHolder;
		Video video = mVideos.get(position);
		String thumbnailUrl = String.format(Config.VIDEO_HQ_THUMBNAIL_URL, video.ytid);
		holder.mTitle.setText(video.ytitle);
		holder.mSection.setText(video.stitle);
		PicassoUtil.loadImage(thumbnailUrl, holder.mThumbnail, 240, 180, R.drawable.placeholder_gray);
		holder.mThumbnail.setTag(video.ytid);
		holder.mOverflow.setTag(video);
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mVideos.size();
	}
}