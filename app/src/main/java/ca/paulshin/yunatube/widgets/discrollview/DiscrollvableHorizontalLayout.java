package ca.paulshin.yunatube.widgets.discrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 *
 */
public class DiscrollvableHorizontalLayout extends LinearLayout implements Discrollvable {

	private static final String TAG = "DiscrollvableHorizontalLayout";

	private View mView1;
	private View mView2;

	private float mView1TranslationX;
	private float mView2TranslationX;

	private float mView1TranslationY;
	private float mView2TranslationY;

	public DiscrollvableHorizontalLayout(Context context) {
		super(context);
	}

	public DiscrollvableHorizontalLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DiscrollvableHorizontalLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mView1 = getChildAt(0);

		if (getChildCount() == 2)
			mView2 = getChildAt(1);

		if (mView1 != null) {
			mView1TranslationX = mView1.getTranslationX();
			mView1TranslationY = mView1.getTranslationY();
		}

		if (mView2 != null) {
			mView2TranslationX = mView2.getTranslationX();
			mView2TranslationY = mView2.getTranslationY();
		}

	}

	@Override
	public void onResetDiscrollve() {
		if (mView1 != null) {
			mView1.setAlpha(0);
			mView1.setTranslationX(mView1TranslationX);
			mView1.setTranslationY(mView1TranslationY);
		}

		if (mView2 != null) {
			mView2.setAlpha(0);
			mView2.setTranslationX(mView2TranslationX);
			mView2.setTranslationY(mView2TranslationY);
		}
	}

	@Override
	public void onDiscrollve(float ratio) {
		if (mView1 != null) {
			mView1.setTranslationX(mView1TranslationX * (1 - ratio));
			mView1.setTranslationY(mView1TranslationY * (1 - ratio));
			mView1.setAlpha(ratio);
		}

		if (mView2 != null) {
			mView2.setTranslationX(mView2TranslationX * (1 - ratio));
			mView2.setTranslationY(mView2TranslationY * (1 - ratio));
			mView2.setAlpha(ratio);
		}
	}
}
