package ca.paulshin.yunatube.ui.base;

/**
 * Created by paulshin on 15-01-01.
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import ca.paulshin.yunatube.BuildConfig;
import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.util.LanguageUtil;
import ca.paulshin.yunatube.util.YTPreference;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import timber.log.Timber;

public abstract class GCMActivity extends BaseActivity {
	public static final String PROPERTY_REG_ID = "registration_id";

	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private String regId;
	private GoogleCloudMessaging gcm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Timber.tag("GCMActivity");

		if (LanguageUtil.isKorean() || BuildConfig.DEBUG) {
			// Check device for Play Services APK. If check succeeds, proceed with GCM registration.
			if (checkPlayServices()) {
				gcm = GoogleCloudMessaging.getInstance(this);
				regId = getRegistrationId(getApplicationContext());

				registerInBackground();
			}
			else {
				Timber.d("No valid Google Play Services APK found.");
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Timber.d("This device is not supported.");
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		String registrationId = YTPreference.get(PROPERTY_REG_ID, "");
		if (TextUtils.isEmpty(registrationId)) {
			Timber.d("Registration not found.");
			return "";
		}

		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regId is not guaranteed to work with the new
		// app version.
		int registeredVersion = YTPreference.get(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Timber.d("App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg;
				Context ctx = getApplicationContext();
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(ctx);
					}
					regId = gcm.register(Config.GCM_SENDER_ID);
					msg = "Device registered, registration ID=" + regId;

					// You should send the registration ID to your server over HTTP,
					// so it can use GCM/HTTP or CCS to send messages to your app.
					// The request to your server should be authenticated if your app
					// is using accounts.
					sendRegistrationIdToBackend(regId);

					// For this demo: we don't need to send it because the device
					// will send upstream messages to a server that echo back the
					// message using the 'from' address in the message.

					// Persist the regId - no need to register again.
					storeRegistrationId(ctx, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Timber.d(msg);
			}
		}.execute(null, null, null);
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. Not needed for this demo since the
	 * device sends upstream messages to a server that echoes back the message
	 * using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend(String regId) {
		String GCM_SERVER_URL = Config.GCM_SENDER_REGID_URL;
		String setRegIdURL = GCM_SERVER_URL + regId;
		try {
			Timber.d("Registration url: " + setRegIdURL);

			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
					.url(setRegIdURL)
					.build();
			client.newCall(request).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		int appVersion = getAppVersion(context);
		Timber.d("Saving regId on app version " + appVersion);
		YTPreference.put(PROPERTY_REG_ID, regId);
		YTPreference.put(PROPERTY_APP_VERSION, appVersion);
	}
}