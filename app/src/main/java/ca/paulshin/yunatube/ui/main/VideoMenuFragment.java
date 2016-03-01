package ca.paulshin.yunatube.ui.main;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.ui.base.BaseFragment;
import ca.paulshin.yunatube.util.LanguageUtil;
import ca.paulshin.yunatube.util.NetworkUtil;
import ca.paulshin.yunatube.util.ResourceUtil;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class VideoMenuFragment extends BaseFragment implements
		View.OnClickListener,
		MainActivity.OnPageSelectedListener,
		VideoMenuMvpView {

	@Bind(R.id.favorite)
	public View mFaveView;

	@Inject
	VideoMenuPresenter mVideoMenuPresenter;

	private boolean mIsFaveViewShown;

	public static VideoMenuFragment newInstance() {
		VideoMenuFragment fragment = new VideoMenuFragment();
		return fragment;
	}

	public VideoMenuFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.f_video, container, false);
		ButterKnife.bind(this, rootView);
		((BaseActivity)getActivity()).getActivityComponent().inject(this);
		mVideoMenuPresenter.attachView(this);

		ButterKnife.findById(rootView, R.id.section_1).setOnClickListener(this);
		ButterKnife.findById(rootView, R.id.section_2).setOnClickListener(this);
		ButterKnife.findById(rootView, R.id.section_3).setOnClickListener(this);
		ButterKnife.findById(rootView, R.id.section_4).setOnClickListener(this);
		ButterKnife.findById(rootView, R.id.section_5).setOnClickListener(this);
		ButterKnife.findById(rootView, R.id.section_6).setOnClickListener(this);
		ButterKnife.findById(rootView, R.id.section_7).setOnClickListener(this);
		ButterKnife.findById(rootView, R.id.section_8).setOnClickListener(this);

		View random = ButterKnife.findById(rootView, R.id.section_9);
		if (random != null) {
			random.setOnClickListener(this);
		}

		mFaveView.setOnClickListener(this);

		rootView.setPadding(getAdjustedPadding(), 0, getAdjustedPadding(), 0);

		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		mVideoMenuPresenter.detachView();
	}

	@Override
	public void onPageSelected() {
		if (!mIsFaveViewShown) {
			mIsFaveViewShown = true;
			showFaveFabView();
		}
	}

	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
			case R.id.favorite:
				showMyFaves();
				break;

			case R.id.section_5:
			case R.id.section_7:
			case R.id.section_8:
				showTVPrograms(v);
				break;

			case R.id.section_9:
				showRandomDialog();
				break;

			default:
				showVideoList(v);
				break;
		}
	}

	public void showFaveFabView() {
		if (mFaveView != null) {
			mFaveView.post(() -> {
				float originalY = ViewHelper.getY(mFaveView);
				ViewHelper.setY(mFaveView, ViewHelper.getY(mFaveView) + mFaveView.getHeight() * 3);
				ObjectAnimator posAnimator = ObjectAnimator.ofFloat(mFaveView, "y", originalY);
				ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mFaveView, "alpha", 0f, 1f);
				posAnimator.setInterpolator(new BounceInterpolator());
				AnimatorSet animatorSet = new AnimatorSet();
				animatorSet.playTogether(posAnimator, alphaAnimator);
				animatorSet.setDuration(1500);
				animatorSet.start();
			});
		}
	}

	private void showMyFaves() {
//		startActivity(new Intent(getActivity(), MyFavesActivity.class));
//		getActivity().overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
	}

	/**
	 * Show TV Programs
	 * Since these may not be available in Korea, show warning message
	 *
	 * @param v
	 */
	private void showTVPrograms(View v) {
		if (LanguageUtil.isKorean()) {
			new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
					.setTitleText(getString(R.string.country_restricted))
					.setContentText(getString(R.string.country_restricted_desc))
					.setConfirmText(getString(R.string.ok))
					.setConfirmClickListener((sDialog, __) -> {
						sDialog.dismissWithAnimation();
						showVideoList(v);
					})
					.show();
		} else {
			showVideoList(v);
		}
	}

	private void showVideoList(View v) {
		String cid = (String) v.getTag();
		Intent intent = new Intent(getActivity(), VideoSectionActivity.class);
		intent.putExtra(VideoSectionActivity.EXTRA_CID, cid);
		intent.putExtra(VideoSectionActivity.EXTRA_CTITLE, ResourceUtil.getResourceId("string", "video_section_" + cid));
		intent.putExtra(VideoSectionActivity.EXTRA_THUMBNAIL_RES, ResourceUtil.getResourceId("drawable", "video_section_" + cid));

		Activity activity = getActivity();
		if (TextUtils.equals("1", cid) || TextUtils.equals("6", cid)) {
			intent = new Intent(getActivity(), VideoListActivity.class);
			intent.putExtra(VideoListActivity.EXTRA_CID, cid);
			intent.putExtra(VideoListActivity.EXTRA_SID, "1");
			intent.putExtra(VideoListActivity.EXTRA_STITLE, getString(ResourceUtil.getResourceId("string", "video_section_" + cid)));
			startActivity(intent);
			activity.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
		} else {
			ViewGroup ll = (ViewGroup) ((ViewGroup) v).getChildAt(0);
			View thumbnail = ll.getChildAt(0);
			ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, thumbnail, "section");
			activity.startActivity(intent, options.toBundle());
		}
	}

	private void showRandomDialog() {
		if (NetworkUtil.isNetworkConnected(getActivity())) {
			mVideoMenuPresenter.getRandomVideo();
		}
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showRandomVideo(Video video) {
		String thumbnailUrl = String.format(Config.VIDEO_HQ_THUMBNAIL_URL, video.ytid);
		new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_REMOTE_IMAGE_TYPE)
				.setCustomRemoteImage(thumbnailUrl)
				.setTitleText(video.ytitle)
				.setContentText(getString(R.string.random_watch_this))
				.setConfirmText(getString(R.string.yes))
				.setConfirmClickListener((sDialog, __) -> {
					sDialog.dismissWithAnimation();

//					Intent intent = new Intent(getActivity(), VideoActivity.class);
//					intent.putExtra(VideoActivity.EXTRA_YTID, video.ytid);
//					startActivity(intent);
//					getActivity().overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
				})
				.setCancelText(getString(R.string.random_something_else))
				.setCancelClickListener((sDialog, __) -> {
					sDialog.dismissWithAnimation();

					showRandomDialog();
				})
				.show();

		sendScreen("video_random - android");
	}

	@Override
	public void showError() {
		//TODO
	}
}
