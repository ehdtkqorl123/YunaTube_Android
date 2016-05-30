package ca.paulshin.yunatube.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.game.DroidRunJumpView;
import ca.paulshin.yunatube.ui.base.BaseFragment;
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
	@Bind(R.id.family_sites)
	View mFamilySitesView;
	@Bind(R.id.search)
	View mSearchView;
//	@Bind(R.id.droidrunjump)
//	DroidRunJumpView drjView;

	public static final String PREFS_NAME = "DRJPrefsFile";

	private DroidRunJumpView.DroidRunJumpThread drjThread;

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
		mFamilySitesView.setOnClickListener(this);
		mSearchView.setOnClickListener(this);

		return root;
	}

	/*
	@Override
	public void onPause() {
		super.onPause();

		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		drjThread = drjView.getThread();

		// if player wants to quit then reset the game
		if (getActivity().isFinishing()) {
			drjThread.resetGame();
		}
		else {
			drjThread.pause();
		}

		drjThread.saveGame(editor);
	}

	@Override
	public void onResume() {
		super.onResume();
		// restore game
		drjThread = drjView.getThread();
		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		drjThread.restoreGame(settings);
	}

	*/

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

			case R.id.search:
				FragmentManager fm = getChildFragmentManager();
				WebSearchDialogFragment f = WebSearchDialogFragment.getInstance();
				f.show(fm, "fragment_web_search");
				return;

			case R.id.family_sites:
				startActivity(new Intent(getActivity(), FamilySitesActivity.class));
				getActivity().overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
				return;
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
}
