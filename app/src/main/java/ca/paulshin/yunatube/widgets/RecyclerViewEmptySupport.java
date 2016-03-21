package ca.paulshin.yunatube.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by paulshin on 16-03-19.
 */
public class RecyclerViewEmptySupport extends RecyclerView {
	private View emptyView;

	private AdapterDataObserver emptyObserver = new AdapterDataObserver() {
		@Override
		public void onChanged() {
			Adapter<?> adapter = getAdapter();
			if(adapter != null && emptyView != null) {
				if (adapter.getItemCount() == 0) {
					emptyView.setVisibility(View.VISIBLE);
				} else {
					emptyView.setVisibility(View.GONE);
				}
			}

		}
	};

	public RecyclerViewEmptySupport(Context context) {
		super(context);
	}

	public RecyclerViewEmptySupport(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RecyclerViewEmptySupport(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setAdapter(Adapter adapter) {
		super.setAdapter(adapter);

		if (adapter != null) {
			adapter.registerAdapterDataObserver(emptyObserver);
		}

		emptyObserver.onChanged();
	}

	public void setEmptyView(View emptyView) {
		this.emptyView = emptyView;
	}
}