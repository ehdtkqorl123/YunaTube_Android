package ca.paulshin.yunatube.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;

import butterknife.ButterKnife;
import ca.paulshin.yunatube.BuildConfig;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YTApplication;
import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.main.VideoActivity;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.ToastUtil;
import ca.paulshin.yunatube.util.YTPreference;
import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by paulshin on 14-12-20.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder {
	@Inject
	DataManager mDataManager;

	public interface OnRemoveListener {
		void onRemove();
	}

	private static final String YOUTUBE_DOWNLOAD_URL = "http://ssyoutube.com/watch?v=";

	private Tracker mTracker;
	private Subscription mSubscription;
	private Context mCtx;

	public TextView title;
	public TextView section;
	public ImageView thumbnail;
	public View overflow;
	public View bgView;

	public OnRemoveListener onRemoveListener;

	public ItemViewHolder(View itemView) {
		super(itemView);

		mCtx = itemView.getContext();
		mTracker = ((YTApplication)mCtx.getApplicationContext()).getDefaultTracker();
		YTApplication.get(mCtx).getComponent().inject(this);

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
			Video video = (Video) v.getTag();

			PopupMenu popup = new PopupMenu(mCtx, v);
			popup.getMenuInflater().inflate(R.menu.menu_new_videos, popup.getMenu());

			// Check if this video is already added
			mSubscription = mDataManager.getMyFaveKey(video.ytid)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribeOn(Schedulers.io())
					.subscribe((id) -> {
						boolean isFaved = id != -1;
						popup.getMenu().removeItem(isFaved ? R.id.action_add_faves : R.id.action_remove_faves);
						popup.setOnMenuItemClickListener((item) -> onMenuItemClick(item, video, id));
						popup.show();
					});
		});
	}

	public void setOnRemoveListener(OnRemoveListener listener) {
		this.onRemoveListener = listener;
	}

	private boolean onMenuItemClick(MenuItem item, Video video, Integer key) {
		switch (item.getItemId()) {
			case R.id.action_share:
				share(video);
				break;

			case R.id.action_add_faves:
				addFave(video);
				break;

			case R.id.action_remove_faves:
				removeFave(video, key);
				break;

			case R.id.action_download:
				download(video);
				break;

			case R.id.action_youtube:
				watchOnYouTube(video);
				break;
		}
		return true;
	}

	private void share(Video video) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, video.ytitle);
		intent.putExtra(Intent.EXTRA_TEXT, video.stitle + " - " + video.ytitle + " : http://youtu.be/" + video.ytid);
		mCtx.startActivity(Intent.createChooser(intent, ResourceUtil.getString(R.string.share_via)));

		sendEvent("video - android", "click: " + video.ytid, "share");
	}

	private void addFave(Video video) {
		mSubscription = mDataManager.insertFave(video)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe((dbVideo) -> {
					if (dbVideo != null) {
						ToastUtil.toast(mCtx, R.string.faves_add_success);

						sendEvent("video - android", "add: " + dbVideo.ytid, "fave");
					} else {
						ToastUtil.toast(mCtx, R.string.faves_add_failure);
					}
				});
	}

	private void removeFave(Video video, int key) {
		mSubscription = mDataManager.deleteFaveByKey(key)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.subscribe((row) -> {
					if (row > 0) {
						ToastUtil.toast(mCtx, R.string.faves_remove_success);
						if (onRemoveListener != null) {
							onRemoveListener.onRemove();
						}

						sendEvent("video - android", "remove: " + video.ytid, "fave");
					} else {
						ToastUtil.toast(mCtx, R.string.faves_remove_failure);
					}
				});
	}

	private void download(Video video) {
		String dlUrl = YOUTUBE_DOWNLOAD_URL + video.ytid;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(dlUrl));

		if (BuildConfig.DEBUG || !YTPreference.getBoolean("download_isfirst")) {
			YTPreference.put("download_isfirst", true);

			ImageView guide = new ImageView(mCtx);
			guide.setImageResource(R.drawable.video_download_guide);

			new SweetAlertDialog(mCtx, SweetAlertDialog.CUSTOM_BIG_IMAGE_TYPE)
					.setCustomBigImage(R.drawable.video_download_guide)
					.setConfirmText(ResourceUtil.getString(R.string.download_video))
					.setConfirmClickListener((sDialog, __) -> {
						sDialog.dismissWithAnimation();
						mCtx.startActivity(intent);
					})
					.show();
		}
		else {
			mCtx.startActivity(intent);
		}

		sendEvent("video - android", "click: " + video.ytid, "download");
	}

	private void watchOnYouTube(Video video) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(String.format(Config.YOUTUBE_SHARE_URL_PREFIX, video.ytid)));
		mCtx.startActivity(intent);

		sendEvent("video - android", "click: " + video.ytid, "youtubeapp");
	}

	protected void sendEvent(String category, String action, String label) {
		mTracker.send(new HitBuilders.EventBuilder()
				.setCategory(category)
				.setAction(action)
				.setLabel(label)
				.build());
	}
}