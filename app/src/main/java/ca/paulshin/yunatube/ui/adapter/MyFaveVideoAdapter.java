package ca.paulshin.yunatube.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import ca.paulshin.dao.DBVideo;
import ca.paulshin.dao.DaoMaster;
import ca.paulshin.dao.DaoSession;
import ca.paulshin.dao.VideoDao;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.db.DBHelper;
import ca.paulshin.yunatube.ui.main.VideoActivity;
import ca.paulshin.yunatube.util.PicassoUtil;

/**
 * Created by paulshin on 14-12-05.
 */
public class MyFaveVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private List<DBVideo> mVideos;
	private VideoDao videoDao;

	public MyFaveVideoAdapter(List<DBVideo> videos) {
		mVideos = videos;

		DaoMaster daoMaster = DBHelper.getDaoMaster();
		DaoSession daoSession = daoMaster.newSession();
		videoDao = daoSession.getVideoDao();
	}

	public static class ItemViewHolder extends RecyclerView.ViewHolder {
		public TextView mTitle;
		public TextView mSection;
		public ImageView mThumbnail;
		public View mOverflow;
		public View mBgView;

		public ItemViewHolder(View itemView) {
			super(itemView);
			mTitle = ButterKnife.findById(itemView, R.id.title);
			mSection = ButterKnife.findById(itemView, R.id.stitle);
			mThumbnail = ButterKnife.findById(itemView, R.id.thumbnail);
			mOverflow = ButterKnife.findById(itemView, R.id.overflow);
			mBgView = ButterKnife.findById(itemView, R.id.bg);

			itemView.setOnClickListener((v) -> {
				Activity activity = (Activity)v.getContext();
				String ytid = (String) mThumbnail.getTag();
				Intent intent = new Intent(v.getContext(), VideoActivity.class);
				intent.putExtra(VideoActivity.EXTRA_YTID, ytid);
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
			});
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_video, parent, false);
		ItemViewHolder ivh = new ItemViewHolder(v);
		return ivh;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
		ItemViewHolder holder = (ItemViewHolder) viewHolder;
		DBVideo video = getItem(position);
		String thumbnailUrl = String.format(Config.VIDEO_HQ_THUMBNAIL_URL, video.getYtid());
		holder.mTitle.setText(video.getYtitle());
		holder.mSection.setText(video.getStitle());
		PicassoUtil.loadImage(thumbnailUrl, holder.mThumbnail, 240, 180, R.drawable.placeholder_gray);
		holder.mThumbnail.setTag(video.getYtid());
		holder.mOverflow.setTag(video);
		holder.mOverflow.setOnClickListener((v) -> {
//			final Context context = v.getContext();
//			final DBVideo video = (DBVideo) v.getTag();
//
//			PopupMenu popup = new PopupMenu(context, v);
//			popup.getMenuInflater().inflate(R.menu.menu_new_videos, popup.getMenu());
//
//			// Check if this video is already added
//			List queriedVideos = videoDao.queryBuilder()
//					.where(VideoDao.Properties.Ytid.eq(video.getYtid()))
//					.list();
//			final long key;
//			if (queriedVideos.size() > 0) {
//				key = ((DBVideo)queriedVideos.get(0)).getId();
//				popup.getMenu().removeItem(R.id.action_add_faves);
//			}
//			else {
//				key = 0;
//				popup.getMenu().removeItem(R.id.action_remove_faves);
//			}
//
//			popup.setOnMenuItemClickListener((item) -> {
//				Intent intent;
//				Tracker tracker = ((YTApplication)context.getApplicationContext()).getTracker();
//				switch (item.getItemId()) {
//					case R.id.action_share:
//						intent = new Intent(Intent.ACTION_SEND);
//						intent.setType("text/plain");
//						intent.putExtra(Intent.EXTRA_SUBJECT, video.getYtitle());
//						intent.putExtra(Intent.EXTRA_TEXT, video.getStitle() + " - " + video.getYtitle() + " : http://youtu.be/" + video.getYtid());
//						context.startActivity(Intent.createChooser(intent, YTUtils.getString(R.string.share_via)));
//
//						tracker.send(new HitBuilders.EventBuilder()
//								.setCategory("faves - android")
//								.setAction("click: " + video.getYtid())
//								.setLabel("share")
//								.build());
//						break;
//
//					case R.id.action_remove_faves:
//						videoDao.deleteByKey(key);
//						ToastUtil.toast(context, R.string.faves_remove_success);
//						mVideos.remove(position);
//						MyFaveVideoAdapter.this.notifyItemRemoved(position);
//						v.postDelayed(() -> MyFaveVideoAdapter.this.notifyDataSetChanged(), 200);
//
//						tracker.send(new HitBuilders.EventBuilder()
//								.setCategory("faves - android")
//								.setAction("remove: " + video.getYtid())
//								.setLabel("fave")
//								.build());
//						break;
//
//					case R.id.action_download:
//						if (BuildConfig.DEBUG || !YTPreference.getBoolean("download_isfirst")) {
//							YTPreference.put("download_isfirst", true);
//
//							ImageView guide = new ImageView(context);
//							guide.setImageResource(R.drawable.video_download_guide);
//
//							new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_BIG_IMAGE_TYPE)
//									.setCustomBigImage(R.drawable.video_download_guide)
//									.setConfirmText(YTUtils.getString(R.string.download_video))
//									.setConfirmClickListener((sDialog, __) -> {
//										sDialog.dismissWithAnimation();
//
//										String dlUrl = "http://ssyoutube.com/watch?v=" + video.getYtid();
//										Intent intent = new Intent(Intent.ACTION_VIEW);
//										intent.setData(Uri.parse(dlUrl));
//										context.startActivity(intent);
//									})
//									.show();
//						}
//						else {
//							String dlUrl = "http://ssyoutube.com/watch?v=" + video.getYtid();
//							intent = new Intent(Intent.ACTION_VIEW);
//							intent.setData(Uri.parse(dlUrl));
//							context.startActivity(intent);
//						}
//
//						tracker.send(new HitBuilders.EventBuilder()
//								.setCategory("faves - android")
//								.setAction("click: " + video.getYtid())
//								.setLabel("download")
//								.build());
//						break;
//
//					case R.id.action_youtube:
//						intent = new Intent(Intent.ACTION_VIEW);
//						intent.setData(Uri.parse(String.format(Config.YOUTUBE_SHARE_URL_PREFIX, video.getYtid())));
//						context.startActivity(intent);
//
//						tracker.send(new HitBuilders.EventBuilder()
//								.setCategory("faves - android: " + video.getYtid())
//								.setAction("click")
//								.setLabel("youtubeapp")
//								.build());
//						break;
//				}
//				return true;
//			});
//
//			popup.show();
		});
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mVideos.size();
	}

	private DBVideo getItem(int position) {
		return mVideos.get(position);
	}
}