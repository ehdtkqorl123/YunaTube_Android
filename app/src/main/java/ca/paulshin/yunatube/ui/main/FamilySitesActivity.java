package ca.paulshin.yunatube.ui.main;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.widgets.SlidingTabLayout;

/**
 * Created by paulshin on 14-12-13.
 */
public class FamilySitesActivity extends BaseActivity {

	@Bind(R.id.view_pager)
	public ViewPager mViewPager;
	@Bind(R.id.sliding_tabs)
	public SlidingTabLayout mSlidingTabLayout;

	ViewPagerAdapter mViewPagerAdapter;

	@Override
	protected String getScreenName() {
		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_familysites);
		ButterKnife.bind(this);

		setToolbar();

		mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mViewPagerAdapter);

		mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);

		Resources res = getResources();
		mSlidingTabLayout.setSelectedIndicatorColors(ResourceUtil.getColor(R.color.tab_selected_strip));
		mSlidingTabLayout.setDistributeEvenly(true);
		mSlidingTabLayout.setViewPager(mViewPager);
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {
		private String[] sites;

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);

			Resources res = getResources();
			sites = res.getStringArray(R.array.family_sites);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return new OfficialFragment();
				case 1:
					return new FanPagesFragment();
				case 2:
					return new FSPagesFragment();
				default:
					return null;
			}
		}

		@Override
		public int getCount() {
			return sites.length;

		}

		@Override
		public CharSequence getPageTitle(int position) {
			return sites[position];
		}
	}
}
