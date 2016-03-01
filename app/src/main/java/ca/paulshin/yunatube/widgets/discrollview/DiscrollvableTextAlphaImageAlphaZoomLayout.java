package ca.paulshin.yunatube.widgets.discrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;


/**
 *
 */
public class DiscrollvableTextAlphaImageAlphaZoomLayout extends LinearLayout implements Discrollvable {
	private View mView1;
	private View mView2;

	public DiscrollvableTextAlphaImageAlphaZoomLayout(Context context) {
		super(context);
	}

	public DiscrollvableTextAlphaImageAlphaZoomLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DiscrollvableTextAlphaImageAlphaZoomLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mView1 = getChildAt(0);

		if (getChildCount() == 2)
			mView2 = getChildAt(1);
	}

	@Override
	public void onResetDiscrollve() {
	}

	@Override
	public void onDiscrollve(float ratio) {
		ratio *= 1.3f;
		if (ratio < 1) {
			mView1.setAlpha(ratio);
			mView2.setAlpha(ratio);
			mView2.setScaleX(ratio);
			mView2.setScaleY(ratio);
		}
	}
}
