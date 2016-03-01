package ca.paulshin.yunatube.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import ca.paulshin.dao.VideoDao;
import ca.paulshin.yunatube.R;

/**
 * Created by paulshin on 14-12-20.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {
	public TextView mTitle;
	public TextView mSection;
	public ImageView mThumbnail;
	public View mOverflow;
	public View mBgView;

	public ItemViewHolder(View itemView, final VideoDao dao) {
		super(itemView);
		mTitle = ButterKnife.findById(itemView, R.id.title);
		mSection = ButterKnife.findById(itemView, R.id.stitle);
		mThumbnail = ButterKnife.findById(itemView, R.id.thumbnail);
		mOverflow = ButterKnife.findById(itemView, R.id.overflow);
		mBgView = ButterKnife.findById(itemView, R.id.bg);

//		itemView.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Activity activity = (Activity)v.getContext();
//				String ytid = (String) mThumbnail.getTag();
//				Intent intent = new Intent(v.getContext(), VideoActivity.class);
//				intent.putExtra(VideoActivity.EXTRA_YTID, ytid);
//				activity.startActivity(intent);
//				activity.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
//			}
//		});
//		mOverflow.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(final View v) {
//				final Context context = v.getContext();
//				final Video video = (Video) v.getTag();
//
//				PopupMenu popup = new PopupMenu(context, v);
//				popup.getMenuInflater().inflate(R.menu.menu_new_videos, popup.getMenu());
//
//				// Check if this video is already added
//				List queriedVideos = dao.queryBuilder()
//						.where(VideoDao.Properties.Ytid.eq(video.ytid))
//						.list();
//				final long key;
//				if (queriedVideos.size() > 0) {
//					key = ((DBVideo)queriedVideos.get(0)).getId();
//					popup.getMenu().removeItem(R.id.action_add_faves);
//				}
//				else {
//					key = 0;
//					popup.getMenu().removeItem(R.id.action_remove_faves);
//				}
//
//				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//					public boolean onMenuItemClick(MenuItem item) {
//						Intent intent;
//						Tracker tracker = ((YTApplication)context.getApplicationContext()).getTracker();
//						switch (item.getItemId()) {
//							case R.id.action_share:
//								intent = new Intent(Intent.ACTION_SEND);
//								intent.setType("text/plain");
//								intent.putExtra(Intent.EXTRA_SUBJECT, video.ytitle);
//								intent.putExtra(Intent.EXTRA_TEXT, video.stitle + " - " + video.ytitle + " : http://youtu.be/" + video.ytid);
//								context.startActivity(Intent.createChooser(intent, YTUtils.getString(R.string.share_via)));
//
//								tracker.send(new HitBuilders.EventBuilder()
//										.setCategory("video - android")
//										.setAction("click: " + video.ytid)
//										.setLabel("share")
//										.build());
//								break;
//
//							case R.id.action_add_faves:
//								// Create Video
//								DBVideo videoDBObj = new DBVideo();
//								videoDBObj.setStitle(video.stitle);
//								videoDBObj.setYtid(video.ytid);
//								videoDBObj.setYtitle(video.ytitle);
//
//								// Set DB
//								long id = dao.insert(videoDBObj);
//								YTUtils.toast(context, id > 0 ? R.string.faves_add_success : R.string.faves_add_failure);
//
//								tracker.send(new HitBuilders.EventBuilder()
//										.setCategory("video - android")
//										.setAction("add: " + video.ytid)
//										.setLabel("fave")
//										.build());
//								break;
//
//							case R.id.action_remove_faves:
//								dao.deleteByKey(key);
//								YTUtils.toast(context, R.string.faves_remove_success);
//
//								tracker.send(new HitBuilders.EventBuilder()
//										.setCategory("video - android")
//										.setAction("remove: " + video.ytid)
//										.setLabel("fave")
//										.build());
//								break;
//
//							case R.id.action_download:
//								if (Config.debuggable || !YTPreference.getBoolean("download_isfirst")) {
//									YTPreference.put("download_isfirst", true);
//
//									ImageView guide = new ImageView(context);
//									guide.setImageResource(R.drawable.video_download_guide);
//
//									new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_BIG_IMAGE_TYPE)
//											.setCustomBigImage(R.drawable.video_download_guide)
//											.setConfirmText(YTUtils.getString(R.string.download_video))
//											.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//												@Override
//												public void onClick(SweetAlertDialog sDialog, String input) {
//													sDialog.dismissWithAnimation();
//
//													String dlUrl = "http://ssyoutube.com/watch?v=" + video.ytid;
//													Intent intent = new Intent(Intent.ACTION_VIEW);
//													intent.setData(Uri.parse(dlUrl));
//													context.startActivity(intent);
//												}
//											})
//											.show();
//								}
//								else {
//									String dlUrl = "http://ssyoutube.com/watch?v=" + video.ytid;
//									intent = new Intent(Intent.ACTION_VIEW);
//									intent.setData(Uri.parse(dlUrl));
//									context.startActivity(intent);
//								}
//
//								tracker.send(new HitBuilders.EventBuilder()
//										.setCategory("video - android")
//										.setAction("click: " + video.ytid)
//										.setLabel("download")
//										.build());
//								break;
//
//							case R.id.action_youtube:
//								intent = new Intent(Intent.ACTION_VIEW);
//								intent.setData(Uri.parse(String.format(Config.YOUTUBE_SHARE_URL_PREFIX, video.ytid)));
//								context.startActivity(intent);
//
//								tracker.send(new HitBuilders.EventBuilder()
//										.setCategory("video - android")
//										.setAction("click: " + video.ytid)
//										.setLabel("youtubeapp")
//										.build());
//								break;
//						}
//						return true;
//					}
//				});
//
//				popup.show();
//			}
//		});
	}
}