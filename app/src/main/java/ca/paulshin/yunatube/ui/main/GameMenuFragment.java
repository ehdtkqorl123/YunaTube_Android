package ca.paulshin.yunatube.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseFragment;

/**
 * Created by paulshin on 2016-06-04.
 */

public class GameMenuFragment extends BaseFragment implements View.OnClickListener {
	@Bind(R.id.acrostic_poem)
	public Button mAcrosticPoemView;

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
		return rootView;
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.acrostic_poem) {
			Activity activity = getActivity();
			startActivity(new Intent(activity, AcrosticPoemActivity.class));
			activity.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
		}
	}
}
