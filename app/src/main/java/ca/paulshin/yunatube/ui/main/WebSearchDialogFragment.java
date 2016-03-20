package ca.paulshin.yunatube.ui.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.YTApplication;
import ca.paulshin.yunatube.util.ToastUtil;

/**
 * Created by paulshin on 15-01-29.
 */
public class WebSearchDialogFragment extends DialogFragment implements View.OnClickListener {

	@Bind(R.id.search_type)
	public RadioGroup searchTypeGroup;
	@Bind(R.id.search_keyword)
	public RadioGroup searchKeywordGroup;

	// Tracker
	private Tracker mTracker;

	private String[] keywordsValues;

	public static WebSearchDialogFragment getInstance() {
		WebSearchDialogFragment f = new WebSearchDialogFragment();
		return f;
	}

	public WebSearchDialogFragment() {
		// Empty constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.f_web_search, container);
		ButterKnife.bind(this, view);
		mTracker = ((YTApplication)getActivity().getApplication()).getDefaultTracker();

		keywordsValues = getActivity().getResources().getStringArray(R.array.search_keyword_value);

		ButterKnife.findById(view, R.id.search_youtube).setOnClickListener(this);
		ButterKnife.findById(view, R.id.search_google).setOnClickListener(this);
		ButterKnife.findById(view, R.id.search_daum).setOnClickListener(this);
		ButterKnife.findById(view, R.id.search_naver).setOnClickListener(this);
		ButterKnife.findById(view, R.id.search_bing).setOnClickListener(this);
		ButterKnife.findById(view, R.id.search_nate).setOnClickListener(this);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		sendScreen("yuna_on_web - android");
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	@Override
	public void onClick(View v) {
		int checkedTypeId = searchTypeGroup.getCheckedRadioButtonId();
		int checkedKeywordId = searchKeywordGroup.getCheckedRadioButtonId();

		String keyword = keywordsValues[checkedKeywordId == R.id.keyword_ko ? 0 : 1];
		String urlString = "";
		String searchUrlFormat;
		int engine;

		switch (v.getId()) {
			case R.id.search_youtube:
				engine = R.string.search_youtube;
				if (checkedTypeId == R.id.search_news) {
					String warning = getString(R.string.search_news_unavailable, "YouTube");
					ToastUtil.toast(getActivity(), warning);
					return;
				} else {
					searchUrlFormat = Config.SEARCH_URL_YOUTUBE;
					urlString = String.format(searchUrlFormat, keyword);
				}
				break;
			case R.id.search_google:
				engine = R.string.search_google;
				if (checkedTypeId == R.id.search_news) {
					searchUrlFormat = Config.NEWS_URL_GOOGLE;
					urlString = String.format(searchUrlFormat, keyword);
				} else {
					searchUrlFormat = Config.SEARCH_URL_GOOGLE;
					urlString = String.format(searchUrlFormat, keyword, keyword, keyword);
				}
				break;
			case R.id.search_daum:
				engine = R.string.search_daum;
				searchUrlFormat = checkedTypeId == R.id.search_news ? Config.NEWS_URL_DAUM : Config.SEARCH_URL_DAUM;
				urlString = String.format(searchUrlFormat, keyword);
				break;
			case R.id.search_naver:
				engine = R.string.search_naver;
				searchUrlFormat = checkedTypeId == R.id.search_news ? Config.NEWS_URL_NAVER : Config.SEARCH_URL_NAVER;
				urlString = String.format(searchUrlFormat, keyword);
				break;
			case R.id.search_bing:
				engine = R.string.search_bing;
				if (checkedTypeId == R.id.search_news) {
					String warning = getString(R.string.search_news_unavailable, "Bing");
					ToastUtil.toast(getActivity(), warning);
					return;
				} else {
					searchUrlFormat = Config.SEARCH_URL_BING;
					urlString = String.format(searchUrlFormat, keyword);
				}
				break;
			case R.id.search_nate:
				engine = R.string.search_nate;
				searchUrlFormat = checkedTypeId == R.id.search_news ? Config.NEWS_URL_NATE : Config.SEARCH_URL_NATE;
				urlString = String.format(searchUrlFormat, keyword);
				break;
			default:
				engine = 0;
		}

		if (engine != 0) {
			String title = getString(engine) + ": " + keyword.replace("%20", " ");
			Intent intent = new Intent(getActivity(), WebViewActivity.class);
			intent.putExtra(WebViewActivity.EXTRA_TITLE, title);
			intent.putExtra(WebViewActivity.EXTRA_URL, urlString);
			intent.putExtra(WebViewActivity.EXTRA_TOOLBAR_BG, R.color.purple);
			startActivity(intent);
		}

		dismiss();
	}

	protected void sendEvent(String category, String action, String label) {
		mTracker.send(new HitBuilders.EventBuilder()
				.setCategory(category)
				.setAction(action)
				.setLabel(label)
				.build());
	}

	protected void sendScreen(String screenName) {
		mTracker.setScreenName(screenName);
		mTracker.send(new HitBuilders.AppViewBuilder().build());
	}
}
