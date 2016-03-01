package ca.paulshin.yunatube.ui.main;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseFragment;
import ca.paulshin.yunatube.util.UIUtil;
import ca.paulshin.yunatube.util.YTPreference;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class YunaMenuFragment extends BaseFragment implements View.OnClickListener {
	@Bind(R.id.story)
	View mProfileView;
	@Bind(R.id.message)
	View mMessageView;
	@Bind(R.id.q20a20)
	View mQ20A20View;
	@Bind(R.id.wiki)
	View mWikiView;
	@Bind(R.id.programs)
	View mProgramsView;
	@Bind(R.id.competitions)
	View mCompetitionsView;
	@Bind(R.id.awards)
	View mAwardsView;
	@Bind(R.id.praises)
	View mPraisesView;
	@Bind(R.id.yuna_kiss)
	View mYunaKissView;
	@Bind(R.id.heart)
	ImageView mHeartView;

	private static final int MAX_KISS_COUNT = 20;

	public static YunaMenuFragment newInstance() {
		YunaMenuFragment fragment = new YunaMenuFragment();
		return fragment;
	}

	public YunaMenuFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.f_yuna, container, false);
		ButterKnife.bind(this, root);

		int padding = getAdjustedPadding();
		LinearLayout content = ButterKnife.findById(root, R.id.yuna_content);
		content.setPadding(padding, 0, padding, 0);

		mProfileView.setOnClickListener(this);
		mMessageView.setOnClickListener(this);
		mQ20A20View.setOnClickListener(this);
		mWikiView.setOnClickListener(this);
		mProgramsView.setOnClickListener(this);
		mAwardsView.setOnClickListener(this);
		mCompetitionsView.setOnClickListener(this);
		mPraisesView.setOnClickListener(this);
		mYunaKissView.setOnClickListener(this);

		setKissView();

		return root;
	}

	/**
	 * Set kiss view programmatically
	 */
	private void setKissView() {
		ViewTreeObserver kissViewVTO = mYunaKissView.getViewTreeObserver();
		kissViewVTO.addOnGlobalLayoutListener(() -> {
			int[] locations = new int[2];
			Context ctx = getActivity();
			mYunaKissView.getLocationOnScreen(locations);

			int kissViewWidth = mYunaKissView.getMeasuredWidth();
			int kissViewHeight = mYunaKissView.getMeasuredHeight();

			int marginLeft = locations[0] - 15 + kissViewWidth / 2;
			int marginTop = locations[1] - 15 - UIUtil.getStatusBarHeight(ctx) - UIUtil.getActionbarHeight(ctx) + kissViewHeight / 2;

			// Programmatically position mHeartView
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(marginLeft, marginTop, 0, 0);
			layoutParams.width = 30;
			layoutParams.height = 30;
			mHeartView.setLayoutParams(layoutParams);
		});
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getActivity(), WebViewActivity.class);
		int title = 0;
		int toolbarBg = 0;
		String url = null;

		switch (v.getId()) {
			case R.id.story:
				String hasWarningShownKey = "has_warning_shown_key";
				if (!YTPreference.contains(hasWarningShownKey) || !YTPreference.get(hasWarningShownKey, true)) {
					new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
							.setContentText(getString(R.string.story_alert))
							.setConfirmText(getString(R.string.dialog_ok))
							.setConfirmClickListener((sDialog, __) -> {
								sDialog.dismissWithAnimation();
								startActivity(new Intent(getActivity(), StoryActivity.class));
								getActivity().overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
								YTPreference.put(hasWarningShownKey, true);
							})
							.show();
				} else {
					startActivity(new Intent(getActivity(), StoryActivity.class));
					getActivity().overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
				}
				return;

			case R.id.message:
				startActivity(new Intent(getActivity(), MessageActivity.class));
				getActivity().overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
				return;

			case R.id.wiki:
				title = R.string.wiki;
				url = getString(R.string.wiki_url);
				toolbarBg = R.color.wiki_bg;
				break;

			case R.id.q20a20:
				title = R.string.q20a20;
				url = Config.Q20A20_URL;
				toolbarBg = R.color.q20a20_bg;
				break;

			case R.id.programs:
				title = R.string.programs;
				url = Config.PROGRAMS_URL;
				toolbarBg = R.color.programs_bg;
				break;

			case R.id.competitions:
				title = R.string.competitions;
				url = Config.COMPETITIONS_URL;
				toolbarBg = R.color.competitions_bg;
				break;

			case R.id.awards:
				title = R.string.awards;
				url = Config.AWARDS_URL;
				toolbarBg = R.color.awards_bg;
				break;

			case R.id.praises:
				title = R.string.praises;
				url = Config.PRAISES_URL;
				toolbarBg = R.color.praises_bg;
				break;

			case R.id.yuna_kiss:
				handleKissTouch(v);
		}

		// Open webview with given values
		if (!TextUtils.isEmpty(url)) {
			intent.putExtra(WebViewActivity.EXTRA_TITLE, getString(title));
			intent.putExtra(WebViewActivity.EXTRA_URL, String.format(url, getString(R.string.lang)));
			intent.putExtra(WebViewActivity.EXTRA_TOOLBAR_BG, toolbarBg);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
		}
	}

	/**
	 * Handle kiss touch count show dialog once it hits the limit
	 * @param v Kiss View
	 */
	private void handleKissTouch(View v) {
		mHeartView.setImageResource(R.drawable.heart);

		// Animate heart image
		v.post(() -> {
			AnimatorSet set = new AnimatorSet();
			set.playTogether(
					ObjectAnimator.ofFloat(mHeartView, "translationY", 0, -600),
					ObjectAnimator.ofFloat(mHeartView, "scaleX", 1, 100f),
					ObjectAnimator.ofFloat(mHeartView, "scaleY", 1, 100f),
					ObjectAnimator.ofFloat(mHeartView, "alpha", 1, 0f)
			);
			set.setDuration(2 * 1000).start();
		});

		sendEvent("yuna - android", "click", "kiss");

		String kissCountKey = "kiss_count";
		int kissCount = YTPreference.get(kissCountKey, 0);
		if (kissCount == MAX_KISS_COUNT) {
			new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_REMOTE_IMAGE_TYPE)
					.setCustomRemoteImage(Config.TOSEUNG_URL)
					.setTitleText(getString(R.string.secret))
					.setContentText(getString(R.string.merong))
					.setConfirmText(getString(R.string.close))
					.setConfirmClickListener((sDialog, __) -> {
						YTPreference.put(kissCountKey, 0);
						sDialog.dismissWithAnimation();
					})
					.show();
		} else {
			YTPreference.put(kissCountKey, kissCount + 1);
		}

		return;
	}
}
