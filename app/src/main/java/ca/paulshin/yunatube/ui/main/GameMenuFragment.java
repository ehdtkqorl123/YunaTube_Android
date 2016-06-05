package ca.paulshin.yunatube.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseFragment;

/**
 * Created by paulshin on 2016-06-04.
 */

public class GameMenuFragment extends BaseFragment {
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

		return rootView;
	}
}
