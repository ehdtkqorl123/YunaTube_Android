/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.paulshin.yunatube.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import ca.paulshin.yunatube.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FABFrameLayout extends FrameLayout {
	private View mRevealView;
	private float mHotSpotX, mHotSpotY;
	private int mRevealViewOffColor;

	public FABFrameLayout(Context context) {
		this(context, null, 0, 0);
	}

	public FABFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0, 0);
	}

	public FABFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, 0);
	}

	public FABFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr);

		mRevealView = new View(context);
		mRevealView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		addView(mRevealView, 0);
		mRevealViewOffColor = getResources().getColor(R.color.theme_accent_1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			mHotSpotX = event.getX();
			mHotSpotY = event.getY();
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
			@Override
			public void getOutline(View view, Outline outline) {
				outline.setOval(0, 0, view.getWidth(), view.getHeight());
			}
		};
		setOutlineProvider(viewOutlineProvider);
		setClipToOutline(true);
	}
}
