package ca.paulshin.yunatube.ui.main;

import android.os.Bundle;

import java.util.ArrayList;

import ca.paulshin.yunatube.ui.base.BaseLinkFragment;

/**
 * Created by paulshin on 14-12-13.
 */
public class FSPagesFragment extends BaseLinkFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void loadSites() {
		links = new ArrayList<>();
		links.add(createLinks("isu"));
		links.add(createLinks("fsu"));
		links.add(createLinks("golden"));
		links.add(createLinks("icenetwork"));
	}

	@Override
	protected String getPrefix() {
		return "links_figureskating_";
	}
}
