package ca.paulshin.yunatube.widgets;

/**
 * Created by paulshin on 2016-04-27.
 */
/** Custom your own ViewPager to extends support ViewPager. java source: */
/** Created by azi on 2013-6-21.  */

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerFixed extends ViewPager {

	public ViewPagerFixed(Context context) {
		super(context);
	}

	public ViewPagerFixed(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		try {
			return super.onTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}
}