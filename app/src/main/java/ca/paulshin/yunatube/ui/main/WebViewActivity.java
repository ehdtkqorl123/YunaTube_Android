package ca.paulshin.yunatube.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.ResourceUtil;

/**
 * Created by paulshin on 15-01-03.
 */
public class WebViewActivity extends BaseActivity {
	public static final String EXTRA_URL = "extra_url";
	public static final String EXTRA_TITLE = "extra_title";
	public static final String EXTRA_TOOLBAR_BG = "extra_toolbar_bg";
	public static final String EXTRA_FROM_NOTIFICATION = "from_notification";

	private boolean isFromNotification;
	private String title;

	@Bind(R.id.view)
	public WebView mWebView;
	@Bind(R.id.loading)
	public View mLoadingView;

	@Override
	protected String getScreenName() {
		return "webview - android (" + title + ")";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_webview);
		ButterKnife.bind(this);

		String url = getIntent().getStringExtra(EXTRA_URL);
		title = getIntent().getStringExtra(EXTRA_TITLE);
		int toolbarBg = getIntent().getIntExtra(EXTRA_TOOLBAR_BG, 0);
		isFromNotification = getIntent().getBooleanExtra(EXTRA_FROM_NOTIFICATION, false);

		Toolbar toolbar = getActionBarToolbar();
		if (toolbarBg != 0) {
			toolbar.setBackgroundColor(ResourceUtil.getColor(toolbarBg));
		}
		toolbar.setNavigationIcon(R.drawable.ic_up);
		toolbar.setNavigationOnClickListener((__) -> finish());
		setTitle(title);

		initWebView();

		mWebView.loadUrl(url);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(false);

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url != null && url.contains(".pdf")) {
					view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
					return true;
				} else {
					return false;
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mLoadingView.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (isFromNotification) {
			finish();
			startActivity(new Intent(this, MainActivity.class));
			overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					if (mWebView.canGoBack()) {
						mWebView.goBack();
					} else {
						finish();
					}
					return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}
}
