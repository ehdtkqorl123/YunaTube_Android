package ca.paulshin.yunatube.ui.main;

import android.os.Bundle;

import java.util.ArrayList;

import ca.paulshin.yunatube.ui.base.BaseLinkFragment;

/**
 * Created by paulshin on 14-12-13.
 */
public class OfficialFragment extends BaseLinkFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void loadSites() {
		links = new ArrayList<>();
		links.add(createLinks("yunakim"));
		links.add(createLinks("yunaaaa"));
		links.add(createLinks("facebook"));
		links.add(createLinks("youtube"));
		links.add(createLinks("allthatskate"));
		links.add(createLinks("allthatskate_twitter"));
		links.add(createLinks("allthatskate_youtube"));
		links.add(createLinks("allthatsports"));
	}

	@Override
	protected String getPrefix() {
		return "links_official_";
	}
}
