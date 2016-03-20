package ca.paulshin.yunatube.ui.main;

import android.os.Bundle;

import java.util.ArrayList;

import ca.paulshin.yunatube.ui.base.BaseLinkFragment;

/**
 * Created by paulshin on 14-12-13.
 */
public class FanPagesFragment extends BaseLinkFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void loadSites() {
		links = new ArrayList<>();
		links.add(createLinks("fevers"));
		links.add(createLinks("yunacafe"));
		links.add(createLinks("yunagall"));
		links.add(createLinks("fsgall"));
		links.add(createLinks("fsgallbestposts"));
		links.add(createLinks("allthatyuna"));
		links.add(createLinks("yunaforum"));
		links.add(createLinks("russia"));
		links.add(createLinks("china"));
	}

	@Override
	protected String getPrefix() {
		return "links_fan_";
	}
}
