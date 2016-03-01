package ca.paulshin.yunatube.widgets;

import android.support.v7.widget.RecyclerView;

/**
 * Created by paulshin on 14-12-02.
 */
public abstract class RecyclerViewScrollDetector extends RecyclerView.OnScrollListener {
	private int mScrollThreshold;

	abstract public void onScrollUp();

	abstract public void onScrollDown();

	@Override
	public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		boolean isSignificantDelta = Math.abs(dy) > mScrollThreshold;
		if (isSignificantDelta) {
			if (dy > 0) {
				onScrollUp();
			} else {
				onScrollDown();
			}
		}
	}

	public void setScrollThreshold(int scrollThreshold) {
		mScrollThreshold = scrollThreshold;
	}
}