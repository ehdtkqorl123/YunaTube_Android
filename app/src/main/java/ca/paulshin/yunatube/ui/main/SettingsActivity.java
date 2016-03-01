package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.BuildConfig;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.LanguageUtil;
import ca.paulshin.yunatube.util.ToastUtil;
import ca.paulshin.yunatube.util.YTPreference;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by paulshin on 14-12-13.
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
	public static final String PREF_NOTIFICATION = "notification";
	public static final String PREF_USERNAME = "nickname";

	@Bind(R.id.push)
	public CheckBox mPushView;
	@Bind(R.id.username)
	public TextView mUsernameView;
	@Bind(R.id.version)
	public TextView mVersionView;

	@Override
	protected String getScreenName() {
		return "settings - android";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_settings);
		ButterKnife.bind(this);

		final Toolbar toolbar = getActionBarToolbar();
		toolbar.setNavigationIcon(R.drawable.ic_up);
		toolbar.setNavigationOnClickListener((__) -> {
			finish();
		});

		// Push Settings
		if (!LanguageUtil.isKorean() && !BuildConfig.DEBUG) {
			findViewById(R.id.setting_title).setVisibility(View.GONE);
			findViewById(R.id.set_push).setVisibility(View.GONE);
		}

		boolean notification = YTPreference.getBoolean(PREF_NOTIFICATION);
		mPushView.setChecked(notification);
		mPushView.setOnCheckedChangeListener(this);

		// Username
		String username = YTPreference.getString(PREF_USERNAME);
		mUsernameView.setText(username);

		// Version
		String version = getString(R.string.version_description, BuildConfig.VERSION_NAME, String.valueOf(BuildConfig.VERSION_CODE));
		mVersionView.setText(version);

		findViewById(R.id.set_username).setOnClickListener(this);
		findViewById(R.id.set_push).setOnClickListener(this);
		findViewById(R.id.feedback).setOnClickListener(this);
		findViewById(R.id.rate).setOnClickListener(this);
	}

	private void showUsernameDialog() {
		new SweetAlertDialog(this, SweetAlertDialog.INPUT_TYPE)
				.setTitleText(getString(R.string.username_set))
				.setContentText(getString(R.string.username_desc))
				.setConfirmText(getString(R.string.ok))
				.setConfirmClickListener((dialog, input) -> {
					if (!TextUtils.isEmpty(input)) {
						dialog.dismissWithAnimation();

						YTPreference.put(PREF_USERNAME, input);
						ToastUtil.toast(SettingsActivity.this, R.string.username_successful);
						mUsernameView.setText(input);
					}
				})
				.setCancelText(getString(R.string.cancel))
				.setCancelClickListener((dialog, input) -> {
					dialog.dismissWithAnimation();
				})
				.show();
	}

	private void setPush(boolean notification) {
		YTPreference.put(PREF_NOTIFICATION, notification);
		ToastUtil.toast(this, notification ? R.string.notification_set : R.string.notification_canceled);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.set_username:
				showUsernameDialog();
				break;

			case R.id.set_push:
				mPushView.setChecked(!mPushView.isChecked());
				break;

			case R.id.feedback:
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("message/rfc822");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[] { getString(R.string.feedback_email) });
				intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
				intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_chooser));
				startActivity(Intent.createChooser(intent, getString(R.string.feedback_chooser)));
				break;

			case R.id.rate:
				String appPackageName = getPackageName();
				Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.MARKET_URL));
				marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(marketIntent);
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		setPush(isChecked);
	}
}
