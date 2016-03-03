package ca.paulshin.yunatube.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

import butterknife.ButterKnife;
import ca.paulshin.dao.DBVideo;
import ca.paulshin.dao.VideoDao;
import ca.paulshin.yunatube.BuildConfig;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YTApplication;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.main.VideoActivity;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.ToastUtil;
import ca.paulshin.yunatube.util.YTPreference;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by paulshin on 14-12-20.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {
	private Tracker mTracker;

	public TextView title;
	public TextView section;
	public ImageView thumbnail;
	public View overflow;
	public View bgView;

	public ItemViewHolder(View itemView, final VideoDao dao) {
		super(itemView);

		Context context = itemView.getContext();
		mTracker = ((YTApplication)context.getApplicationContext()).getDefaultTracker();
		title = ButterKnife.findById(itemView, R.id.title);
		section = ButterKnife.findById(itemView, R.id.stitle);
		thumbnail = ButterKnife.findById(itemView, R.id.thumbnail);
		overflow = ButterKnife.findById(itemView, R.id.overflow);
		bgView = ButterKnife.findById(itemView, R.id.bg);

		itemView.setOnClickListener((v) -> {
			Activity activity = (Activity)v.getContext();
			String ytid = (String) thumbnail.getTag();
			Intent intent = new Intent(v.getContext(), VideoActivity.class);
			intent.putExtra(VideoActivity.EXTRA_YTID, ytid);
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
		});

		overflow.setOnClickListener((v) -> {
			final Video video = (Video) v.getTag();

			PopupMenu popup = new PopupMenu(context, v);
			popup.getMenuInflater().inflate(R.menu.menu_new_videos, popup.getMenu());

			// Check if this video is already added
			List queriedVideos = dao.queryBuilder()
					.where(VideoDao.Properties.Ytid.eq(video.ytid))
					.list();
			final long key;
			if (queriedVideos.size() > 0) {
				key = ((DBVideo)queriedVideos.get(0)).getId();
				popup.getMenu().removeItem(R.id.action_add_faves);
			}
			else {
				key = 0;
				popup.getMenu().removeItem(R.id.action_remove_faves);
			}

			popup.setOnMenuItemClickListener((item) -> {
				Intent intent;

				switch (item.getItemId()) {
					case R.id.action_share:
						intent = new Intent(Intent.ACTION_SEND);
						intent.setType("text/plain");
						intent.putExtra(Intent.EXTRA_SUBJECT, video.ytitle);
						intent.putExtra(Intent.EXTRA_TEXT, video.stitle + " - " + video.ytitle + " : http://youtu.be/" + video.ytid);
						context.startActivity(Intent.createChooser(intent, ResourceUtil.getString(R.string.share_via)));

						sendEvent("video - android", "click: " + video.ytid, "share");
						break;

					case R.id.action_add_faves:
						// Create Video
						DBVideo videoDBObj = new DBVideo();
						videoDBObj.setStitle(video.stitle);
						videoDBObj.setYtid(video.ytid);
						videoDBObj.setYtitle(video.ytitle);

						// Set DB
						long id = dao.insert(videoDBObj);
						ToastUtil.toast(context, id > 0 ? R.string.faves_add_success : R.string.faves_add_failure);

						sendEvent("video - android", "add: " + video.ytid, "fave");
						break;

					case R.id.action_remove_faves:
						dao.deleteByKey(key);
						ToastUtil.toast(context, R.string.faves_remove_success);

						sendEvent("video - android", "remove: " + video.ytid, "fave");
						break;

					case R.id.action_download:
						String dlUrl = "http://ssyoutube.com/watch?v=" + video.ytid;
						intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(dlUrl));

						if (BuildConfig.DEBUG || !YTPreference.getBoolean("download_isfirst")) {
							YTPreference.put("download_isfirst", true);

							ImageView guide = new ImageView(context);
							guide.setImageResource(R.drawable.video_download_guide);

							new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_BIG_IMAGE_TYPE)
									.setCustomBigImage(R.drawable.video_download_guide)
									.setConfirmText(ResourceUtil.getString(R.string.download_video))
									.setConfirmClickListener((sDialog, __) -> {
										sDialog.dismissWithAnimation();
										context.startActivity(intent);
									})
									.show();
						}
						else {
							context.startActivity(intent);
						}

						sendEvent("video - android", "click: " + video.ytid, "download");
						break;

					case R.id.action_youtube:
						intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(String.format(Config.YOUTUBE_SHARE_URL_PREFIX, video.ytid)));
						context.startActivity(intent);

						sendEvent("video - android", "click: " + video.ytid, "youtubeapp");
						break;
				}
				return true;
			});

			popup.show();
		});
	}

	protected void sendEvent(String category, String action, String label) {
		mTracker.send(new HitBuilders.EventBuilder()
				.setCategory(category)
				.setAction(action)
				.setLabel(label)
				.build());
	}
}