package ca.paulshin.yunatube.ui.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.ui.base.BaseActivity;

/**
 * Created by paulshin on 2016-05-26.
 */

public class MainSearchActivity extends BaseActivity {

	@Bind(R.id.search_view)
	public SearchView mSearchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_main_search);

		ButterKnife.bind(this);

		setupSearchView();

		Toolbar toolbar = getActionBarToolbar();
		Drawable up = DrawableCompat.wrap(ContextCompat.getDrawable(this, R.drawable.ic_up));
		DrawableCompat.setTint(up, getResources().getColor(R.color.app_body_text_2));
		toolbar.setNavigationIcon(up);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			doEnterAnim();
		}

		overridePendingTransition(0, 0);
	}

	protected void setupSearchView() {
		SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
		mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		mSearchView.setIconified(false);
		// Set the query hint.
		mSearchView.setQueryHint(getString(R.string.search_hint));
		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				// Hide keyboard
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);

				searchFor(s);
				finish();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String s) {
				return true;
			}
		});

		mSearchView.setOnCloseListener(() -> {
			dismiss(null);
			return false;
		});

		// Show keyboard
		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
				toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				dismiss(null);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected String getScreenName() {
		return "main - search - android";
	}

	@Override
	public void onBackPressed() {
		dismiss(null);
	}

	public void dismiss(View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			doExitAnim();
		} else {
			ActivityCompat.finishAfterTransition(this);
		}
	}

	/**
	 * On Lollipop+ perform a circular reveal animation (an expanding circular mask) when showing
	 * the search panel.
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	protected void doEnterAnim() {
		// Fade in a background scrim as this is a floating window. We could have used a
		// translucent window background but this approach allows us to turn off window animation &
		// overlap the fade with the reveal animation – making it feel snappier.
		View scrim = findViewById(R.id.scrim);
		scrim.animate()
				.alpha(1f)
				.setDuration(500L)
				.setInterpolator(
						AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in))
				.start();

		// Next perform the circular reveal on the search panel
		final View searchPanel = findViewById(R.id.search_panel);
		if (searchPanel != null) {
			// We use a view tree observer to set this up once the view is measured & laid out
			searchPanel.getViewTreeObserver().addOnPreDrawListener(
					new ViewTreeObserver.OnPreDrawListener() {
						@Override
						public boolean onPreDraw() {
							searchPanel.getViewTreeObserver().removeOnPreDrawListener(this);
							// As the height will change once the initial suggestions are delivered by the
							// loader, we can't use the search panels height to calculate the final radius
							// so we fall back to it's parent to be safe
							int revealRadius = ((ViewGroup) searchPanel.getParent()).getHeight();
							// Center the animation on the top right of the panel i.e. near to the
							// search button which launched this screen.
							Animator show = ViewAnimationUtils.createCircularReveal(searchPanel,
									searchPanel.getRight(), searchPanel.getTop(), 0f, revealRadius);
							show.setDuration(250L);
							show.setInterpolator(AnimationUtils.loadInterpolator(MainSearchActivity.this,
									android.R.interpolator.fast_out_slow_in));
							show.start();
							return false;
						}
					});
		}
	}

	/**
	 * On Lollipop+ perform a circular animation (a contracting circular mask) when hiding the
	 * search panel.
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	protected void doExitAnim() {
		final View searchPanel = findViewById(R.id.search_panel);
		// Center the animation on the top right of the panel i.e. near to the search button which
		// launched this screen. The starting radius therefore is the diagonal distance from the top
		// right to the bottom left
		int revealRadius = (int) Math.sqrt(Math.pow(searchPanel.getWidth(), 2)
				+ Math.pow(searchPanel.getHeight(), 2));
		// Animating the radius to 0 produces the contracting effect
		Animator shrink = ViewAnimationUtils.createCircularReveal(searchPanel,
				searchPanel.getRight(), searchPanel.getTop(), revealRadius, 0f);
		shrink.setDuration(200L);
		shrink.setInterpolator(AnimationUtils.loadInterpolator(MainSearchActivity.this,
				android.R.interpolator.fast_out_slow_in));
		shrink.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				searchPanel.setVisibility(View.INVISIBLE);
				ActivityCompat.finishAfterTransition(MainSearchActivity.this);
			}
		});
		shrink.start();

		// We also animate out the translucent background at the same time.
		findViewById(R.id.scrim).animate()
				.alpha(0f)
				.setDuration(200L)
				.setInterpolator(
						AnimationUtils.loadInterpolator(MainSearchActivity.this,
								android.R.interpolator.fast_out_slow_in))
				.start();
	}

	private void searchFor(String query) {
		Intent intent = new Intent(MainSearchActivity.this, SearchActivity.class);
		intent.putExtra(SearchActivity.EXTRA_QUERY, query);
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			overridePendingTransition(0, 0);
		}
	}
}
