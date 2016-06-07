package ca.paulshin.yunatube.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseFragment;

/**
 * Created by paulshin on 2016-06-04.
 */

public class GameMenuFragment extends BaseFragment implements View.OnClickListener {
	@Bind(R.id.acrostic_poem)
	public View mAcrosticPoemView;
	@Bind(R.id.yuna_sticker)
	public View mStickerView;

	private static final String YUNA_STICKER_PACKAGE_NAME = "ca.paulshin.yunasticker";

	public static GameMenuFragment newInstance() {
		GameMenuFragment fragment = new GameMenuFragment();
		return fragment;
	}

	public GameMenuFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.f_game, container, false);
		ButterKnife.bind(this, rootView);

		mAcrosticPoemView.setOnClickListener(this);
		mStickerView.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.acrostic_poem) {
			Activity activity = getActivity();
			startActivity(new Intent(activity, AcrosticPoemActivity.class));
			activity.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
		} else if (id == R.id.yuna_sticker) {
			startNewActivity();
		}
	}

	public void startNewActivity() {
		Context context = getActivity();
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(YUNA_STICKER_PACKAGE_NAME);
		if (intent != null) {
			// We found the activity now start the activity
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} else {
			// Bring user to the market or let them choose an app?
			intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("market://details?id=" + YUNA_STICKER_PACKAGE_NAME));
			context.startActivity(intent);
		}
	}
}
