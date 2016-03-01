package ca.paulshin.yunatube.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Helper widget to display all images in a square shape
 * @author paulshin
 */
public class SquareByHeightImageView extends ImageView {
	public SquareByHeightImageView(Context context) {
		super(context);
	}

	public SquareByHeightImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareByHeightImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int height = getMeasuredHeight();
		setMeasuredDimension(height, height);
	}
}
