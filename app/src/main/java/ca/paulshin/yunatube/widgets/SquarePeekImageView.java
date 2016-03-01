package ca.paulshin.yunatube.widgets;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Helper widget to display all images in a square shape
 *
 * @author paulshin
 */
public class SquarePeekImageView extends PeekImageView {
	public SquarePeekImageView(Context context) {
		super(context);
	}

	public SquarePeekImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquarePeekImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = getMeasuredWidth();
		setMeasuredDimension(width, width);
	}
}
