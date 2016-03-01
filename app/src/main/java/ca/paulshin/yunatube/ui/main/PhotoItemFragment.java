package ca.paulshin.yunatube.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;

import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseFragment;
import ca.paulshin.yunatube.util.PicassoUtil;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

/**
 * Created by paulshin on 14-12-26.
 */
public class PhotoItemFragment extends BaseFragment {
	private static final String EXTRA_URL = "url";

	private String mUrl;
	private PhotoViewAttacher mAttacher;

	public static PhotoItemFragment newInstance(String url) {
		PhotoItemFragment photoItemFragment = new PhotoItemFragment();
		Bundle args = new Bundle();
		args.putString(EXTRA_URL, url);
		photoItemFragment.setArguments(args);
		return photoItemFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.f_photo_item, container, false);
		mUrl = getArguments().getString(EXTRA_URL).replace("_m", "_b");

		ImageView imageView = ButterKnife.findById(rootView, R.id.photo);
		PicassoUtil.loadImage(mUrl, imageView, new Callback() {
			@Override
			public void onSuccess() {
				// Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
				mAttacher = new PhotoViewAttacher(imageView);

				if (getActivity() instanceof OnPhotoTapListener) {
					OnPhotoTapListener listener = (OnPhotoTapListener)getActivity();
					mAttacher.setOnPhotoTapListener(listener);
				}
			}

			@Override
			public void onError() {}
		});

		return rootView;
	}
}
