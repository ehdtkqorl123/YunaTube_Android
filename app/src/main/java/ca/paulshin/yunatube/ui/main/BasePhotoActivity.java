package ca.paulshin.yunatube.ui.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.FileUtil;
import ca.paulshin.yunatube.util.ToastUtil;
import ca.paulshin.yunatube.util.UIUtil;
import ca.paulshin.yunatube.util.ViewUtil;
import ca.paulshin.yunatube.widgets.ViewPagerFixed;
import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by paulshin on 15-01-31.
 */
public abstract class BasePhotoActivity extends BaseActivity implements PhotoViewAttacher.OnPhotoTapListener {
	private static final int TRANSLATE_DURATION_MILLIS = 200;
	private static final int SYSTEM_UI_SHOW_DURATION = 2000;
	private static final int WRITE_ACCESS_REQUEST_CODE_SAVE = 100;
	private static final int WRITE_ACCESS_REQUEST_CODE_SHARING = 200;

	private NotificationManager mNotificationManager;
	private View mDecorView;

	protected LinearLayout mMenuView;
	protected ViewPagerFixed mPager;
	private String mUrl;

	protected abstract int getNotificationId();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mDecorView = getWindow().getDecorView();

		// Hide nav bar on fullscreen
		mDecorView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
						| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
						| View.SYSTEM_UI_FLAG_IMMERSIVE);
	}

	@Override
	public void onPhotoTap(View view, float x, float y) {
		showSystemUI();
	}

	protected void setMenubarPadding() {
		// Get height of nav bar
		int bottomMargin = 0;
		if (UIUtil.hasSoftSysBar(this)) {
			int screenSize[] = ViewUtil.getScreenSize();
			bottomMargin = ViewUtil.pxToDp(Math.min(screenSize[0], screenSize[1])) > 550 ? UIUtil.getNavBarHeight(this) :
					getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 0 : UIUtil.getNavBarHeight(this);
		}

		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mMenuView.getLayoutParams();
		params.setMargins(0, 0, 0, bottomMargin);
		mMenuView.setLayoutParams(params);
	}

	protected void hideSystemUI() {
		mDecorView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
						| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
						| View.SYSTEM_UI_FLAG_IMMERSIVE);

		toggleMenu(false);
	}

	protected void showSystemUI() {
		mDecorView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

		toggleMenu(true);
		mDecorView.postDelayed(() -> hideSystemUI(), SYSTEM_UI_SHOW_DURATION);
	}

	protected void toggleMenu(boolean visible) {
		int alpha = visible ? 1 : 0;

		if (visible) {
			mMenuView.setVisibility(View.VISIBLE);
		}
		ViewPropertyAnimator.animate(mMenuView)
				.setInterpolator(new AccelerateDecelerateInterpolator())
				.setDuration(TRANSLATE_DURATION_MILLIS)
				.setListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animation) {}

					@Override
					public void onAnimationEnd(Animator animation) {
						if (!visible) {
							mMenuView.setVisibility(View.GONE);
						}
					}

					@Override
					public void onAnimationCancel(Animator animation) {}

					@Override
					public void onAnimationRepeat(Animator animation) {}
				})
				.alpha(alpha);
	}

	@TargetApi(Build.VERSION_CODES.M)
	private void checkWriteExternalPermission(String url, int requestCode) {
		String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
		if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
			if (shouldShowRequestPermissionRationale(permission)) {
				ToastUtil.toast(this, R.string.write_access_request_message);
			} else {
				mUrl = url;
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						requestCode);
			}
		} else {
			switch (requestCode) {
				case WRITE_ACCESS_REQUEST_CODE_SAVE:Log.d("log", "!!!!!!!!" + url);
						performWrite(url);
					return;

				case WRITE_ACCESS_REQUEST_CODE_SHARING:
						performShareLocalFile(url);
					return;
			}
		}
	}

	protected void performSave(String url) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			checkWriteExternalPermission(url, WRITE_ACCESS_REQUEST_CODE_SAVE);
		} else {
			performWrite(url);
		}
	}

	private void performWrite(String url) {
		String fileName = new File(url).getName();

		File file = new File(FileUtil.getAlbumDir(BasePhotoActivity.this), fileName);
		File shareFile = new File(FileUtil.getAlbumDir(BasePhotoActivity.this), ca.paulshin.yunatube.Config.TEMP_PREFIX + fileName);
		if (file.exists()) {
			ToastUtil.toast(this, R.string.photo_exists);
			return;
		}
		if (shareFile.exists()) {
			File newFile = new File(shareFile.getParent(), fileName);
			if (shareFile.renameTo(newFile)) {
				ToastUtil.toast(this, R.string.photo_saved_success);
				showDownloadNotification(newFile);
			}
		} else {
			downloadFileToSave(url);
		}
	}

	protected void performShare(String url) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			checkWriteExternalPermission(url, WRITE_ACCESS_REQUEST_CODE_SHARING);
		} else {
			performShareLocalFile(url);
		}
	}

	private void performShareLocalFile(String url) {
		File file = new File(FileUtil.getAlbumDir(this), new File(url).getName());
		if (file.exists()) {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/jpeg");
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

			startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
		} else {
			downloadFileToShare(url);
		}
	}

	private void downloadFileToSave(String url) {
		new AsyncTask<String, Void, File>() {
			@Override
			protected void onPreExecute() {
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(BasePhotoActivity.this)
						.setContentTitle(getString(R.string.download))
						.setContentText(getString(R.string.download_in_progress))
						.setSmallIcon(R.drawable.ic_download)
						.setProgress(0, 0, true);
				mNotificationManager.notify(getNotificationId(), mBuilder.build());
			}

			@Override
			protected File doInBackground(String... params) {
				return FileUtil.downloadFile(BasePhotoActivity.this, params[0], null);
			}

			protected void onPostExecute(File file) {
				if (file != null) {
					ToastUtil.toast(BasePhotoActivity.this, R.string.photo_saved_success);
					showDownloadNotification(file);
				} else {
					new SweetAlertDialog(BasePhotoActivity.this, SweetAlertDialog.WARNING_TYPE)
							.setTitleText(getString(R.string.cannot_save_device))
							.setConfirmClickListener((sDialog, __) -> sDialog.dismissWithAnimation())
							.show();
				}
			}
		}.execute(url);
	}

	private void downloadFileToShare(String url) {
		new AsyncTask<String, Void, File>() {
			private String url;

			@Override
			protected File doInBackground(String... params) {
				url = params[0];
				return FileUtil.downloadFile(BasePhotoActivity.this, params[0], ca.paulshin.yunatube.Config.TEMP_PREFIX);
			}

			protected void onPostExecute(File file) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				if (file != null) {
					intent.setType("image/jpeg");
					intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
					startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
				} else {
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_TEXT, url + " ");
					startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
				}
			}
		}.execute(url);
	}

	protected void showDownloadNotification(File file) {
		// Handle notification
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		Uri path = Uri.fromFile(file);
		intent.setDataAndType(path, "image/*");

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		try {
			bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setContentTitle(getString(R.string.download))
				.setContentText(getString(R.string.download_complete))
				.setSmallIcon(R.drawable.ic_download)
				.setLargeIcon(bitmap)
				.setAutoCancel(true)
				.setContentIntent(pendingIntent);
		mNotificationManager.notify(getNotificationId(), mBuilder.build());

		// Tell the media scanner about the new file so that it is immediately available to the user.
		MediaScannerConnection.scanFile(this,
				new String[]{file.toString()}, null,
				(extPath, uri) -> {
					Log.i("ExternalStorage", "Scanned " + extPath + ":");
					Log.i("ExternalStorage", "-> uri=" + uri);
				});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case WRITE_ACCESS_REQUEST_CODE_SAVE:
				if (grantResults.length == 1 &&
						grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					performWrite(mUrl);
				} else {
					ToastUtil.toast(this, R.string.write_access_request_denied);
				}
				return;

			case WRITE_ACCESS_REQUEST_CODE_SHARING: {
				if (grantResults.length == 1 &&
						grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					performShareLocalFile(mUrl);
				} else {
					ToastUtil.toast(this, R.string.write_access_request_denied);
				}
				return;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		File dir = FileUtil.getAlbumDir(this);
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getName().startsWith(ca.paulshin.yunatube.Config.TEMP_PREFIX))
					file.delete();
			}
		}
	}
}
