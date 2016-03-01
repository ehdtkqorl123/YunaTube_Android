package ca.paulshin.yunatube.ui.main;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import butterknife.ButterKnife;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseFragment;
import ca.paulshin.yunatube.util.FileUtil;
import ca.paulshin.yunatube.util.GlideUtil;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by paulshin on 14-12-26.
 */
public class AnimatedGifItemFragment extends BaseFragment {
	public interface OnPhotoClickedListener {
		void OnPhotoClicked();
		void OnPhotoSelected(String url);
	}

	private static OnPhotoClickedListener sDummyListener = new OnPhotoClickedListener() {
		@Override
		public void OnPhotoClicked() {}
		@Override
		public void OnPhotoSelected(String url) {}
	};

	private static final String EXTRA_FILENAME = "filename";

	private View root;
	private String fileName;
	private String url;
	private OnPhotoClickedListener mListener = sDummyListener;

	public static AnimatedGifItemFragment newInstance(String filename) {
		AnimatedGifItemFragment photoItemFragment = new AnimatedGifItemFragment();
		Bundle args = new Bundle();
		args.putString(EXTRA_FILENAME, filename);
		photoItemFragment.setArguments(args);
		return photoItemFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.f_gif_item, container, false);
		fileName = getArguments().getString(EXTRA_FILENAME);
		url = String.format(Config.GIF_URL, fileName);

		downloadGif();

		return root;
	}

	private void downloadGif() {
		new AsyncTask<String, Void, File>() {
			@Override
			protected File doInBackground(String... params) {
				return FileUtil.downloadFile(getActivity(), url, params[1]);
			}

			protected void onPostExecute(File dest) {
				if (dest != null)
					displayGif(dest);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fileName, Config.TEMP_PREFIX);
	}

	private void displayGif(File file) {
//		ImageView gifImageView = ButterKnife.findById(root, R.id.gif);
//		GlideUtil.loadImage(file, gifImageView);
//		gifImageView.setOnClickListener((v) -> {
//			mListener.OnPhotoSelected(fileName);
//			mListener.OnPhotoClicked();
//		});

		GifImageView gifImageView = ButterKnife.findById(root, R.id.gif);
		try {
			GifDrawable gd = new GifDrawable(getActivity().getContentResolver(), Uri.fromFile(file));
			gifImageView.setImageDrawable(gd);
			gifImageView.setOnClickListener((__) -> {
				mListener.OnPhotoSelected(fileName);
				mListener.OnPhotoClicked();
			});
		} catch (Exception e) {
		}
	}

	private void displayGif(String url) {
		ImageView gifImageView = ButterKnife.findById(root, R.id.gif);
		GlideUtil.loadImage(url, gifImageView);
		gifImageView.setOnClickListener((v) -> {
			mListener.OnPhotoSelected(fileName);
			mListener.OnPhotoClicked();
		});
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (!(context instanceof OnPhotoClickedListener)) {
			throw new ClassCastException("Activity must implement fragment's callbacks.");
		}

		mListener = (OnPhotoClickedListener) context;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = sDummyListener;
	}
}
