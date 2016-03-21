package ca.paulshin.yunatube.ui.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.ResourceUtil;

/**
 * Created by paulshin on 14-12-13.
 */
public class FamilySitesActivity extends BaseActivity {
	@Bind(R.id.view_pager)
	public ViewPager mViewPager;

	@Override
	protected String getScreenName() {
		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_familysites);
		ButterKnife.bind(this);

		setupToolbar();
		setupViewPager();

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(mViewPager);
	}

	private void setupViewPager() {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new OfficialFragment());
		adapter.addFragment(new FanPagesFragment());
		adapter.addFragment(new FSPagesFragment());
		mViewPager.setAdapter(adapter);
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragments = new ArrayList<>();
		private String[] mFragmentTitles;

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragmentTitles = ResourceUtil.getStringArray(R.array.family_sites);
		}

		public void addFragment(Fragment fragment) {
			mFragments.add(fragment);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitles[position];
		}
	}
}
