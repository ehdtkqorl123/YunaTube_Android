package ca.paulshin.yunatube.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by paulshin on 15-01-26.
 */
public class PeekImageView extends ImageView implements ViewTreeObserver.OnScrollChangedListener {
	private InViewportListener mInViewportListener;
	private boolean isInViewport = false;

	public PeekImageView(Context context) {
		super(context);
	}

	public PeekImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PeekImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public interface InViewportListener {
		void onViewportEnter(PeekImageView view);
		void onViewportExit(PeekImageView view);
	}

	public void setInViewportListener(InViewportListener listener) {
		mInViewportListener = listener;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		ViewTreeObserver vto = getViewTreeObserver();
		if (vto != null) {
			vto.addOnScrollChangedListener(this);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		ViewTreeObserver vto = getViewTreeObserver();
		if (vto != null) {
			vto.removeOnScrollChangedListener(this);
		}
	}

	@Override
	public void onScrollChanged() {
		Rect bounds = new Rect();
		boolean inViewport = getLocalVisibleRect(bounds);
		if (mInViewportListener != null && isInViewport != inViewport) {
			if (inViewport) {
				mInViewportListener.onViewportEnter(this);
			} else {
				mInViewportListener.onViewportExit(this);
			}
		}
		isInViewport = inViewport;
	}
}
