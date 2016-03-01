package ca.paulshin.yunatube.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import ca.paulshin.yunatube.YTApplication;
import ca.paulshin.yunatube.util.NetworkUtil;
import ca.paulshin.yunatube.util.YTPreference;
import ca.paulshin.yunatube.util.events.ConnectivityChangeEvent;

/**
 * Created by paulshin on 15-01-03.
 */
public class ConnectivityChangeReceiver extends BroadcastReceiver {
	@Inject
	Bus mBus;

	@Override
	public void onReceive(Context context, Intent intent) {
		YTApplication.get(context).getComponent().inject(this);

		boolean status = NetworkUtil.isNetworkConnected(context);
		if (YTPreference.getBoolean("prev_network") != status) {
			// Broadcast only if connectivity change occurs
			mBus.post(new ConnectivityChangeEvent(status));
			YTPreference.put("prev_network", status);
		}
	}

	private boolean getStatus(Context context, Intent intent) {
		if (intent.getExtras() != null) {
			final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

			if (ni != null && ni.isConnectedOrConnecting()) {
				return true;
			} else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
				return false;
			}
		}
		return false;
	}
}
