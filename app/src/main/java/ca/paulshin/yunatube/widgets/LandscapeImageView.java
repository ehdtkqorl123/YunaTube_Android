package ca.paulshin.yunatube.widgets;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Helper widget to display all images in a square shape
 * @author paulshin
 */
public class LandscapeImageView extends PeekImageView {
	public LandscapeImageView(Context context) {
		super(context);
	}

	public LandscapeImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LandscapeImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = getMeasuredWidth();
		setMeasuredDimension(width, (int)(width * 0.75));
	}
}
