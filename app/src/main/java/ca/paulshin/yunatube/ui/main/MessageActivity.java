package ca.paulshin.yunatube.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.message.Message;
import ca.paulshin.yunatube.ui.adapter.MessageAdapter;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.NetworkUtil;
import ca.paulshin.yunatube.util.YTPreference;
import ca.paulshin.yunatube.util.events.DataLoadedEvent;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MessageActivity extends BaseActivity implements MessageMvpView, View.OnClickListener {

	@Inject
	MessagePresenter mMessagePresenter;
	@Inject
	Bus mBus;

	private static final int MIN_MESSAGE_LENGTH = 15;

	private boolean mIsRefreshing;
	private String mNextMaxId;
	private String mUsername;
	private MessageAdapter mAdapter;

	private BottomSheetBehavior mBottomSheetBehavior;

	@Bind(R.id.grid)
	public RecyclerView mRecyclerView;
	@Bind(R.id.comment_window)
	RelativeLayout mCommentWindow;
	@Bind(R.id.content)
	EditText mCommentView;
	@Bind(R.id.fab_write)
	View mWriteView;

	@Override
	protected String getScreenName() {
		return "message - android";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_message);
		ButterKnife.bind(this);
		getActivityComponent().inject(this);
		mMessagePresenter.attachView(this);

		final Toolbar toolbar = getActionBarToolbar();
		toolbar.setNavigationIcon(R.drawable.ic_up);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mAdapter = new MessageAdapter(mRecyclerView);
		mAdapter.setOnLoadMoreListener(() -> mMessagePresenter.getMessages(mNextMaxId));
		mRecyclerView.setAdapter(mAdapter);

		mWriteView.setOnClickListener(this);

		mBottomSheetBehavior = BottomSheetBehavior.from(mCommentWindow);
		mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				boolean isCollapsed = newState == BottomSheetBehavior.STATE_COLLAPSED;
				mWriteView.setVisibility(isCollapsed ? View.VISIBLE : View.GONE);

				if (isCollapsed) {
					// Hide keyboard
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mCommentView.getWindowToken(), 0);
				}
			}
			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {
				// React to dragging events
			}
		});

		ButterKnife.findById(this, R.id.close).setOnClickListener(this);
		ButterKnife.findById(this, R.id.submit).setOnClickListener(this);

		loadData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mMessagePresenter.detachView();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mBus.register(this);
		mUsername = YTPreference.getString(SettingsActivity.PREF_USERNAME);
		((TextView) ButterKnife.findById(this, R.id.my_username)).setText(mUsername);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mBus.unregister(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.menu_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_refresh:
				requestDataRefresh();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (mCommentWindow.getVisibility() == View.VISIBLE) {
			mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void requestDataRefresh() {
		super.requestDataRefresh();
		MessageAdapter adapter = (MessageAdapter) mRecyclerView.getAdapter();
		if (adapter != null) {
			mIsRefreshing = true;
			mNextMaxId = null;
			loadData();
		}
	}

	/**
	 * Load messages by making api calls if network is connected
	 */
	private void loadData() {
		if (NetworkUtil.isNetworkConnected(this)) {
			mMessagePresenter.getMessages("");
		} else {
			//TODO
			mRecyclerView.setVisibility(View.GONE);
		}
	}

	/**
	 * Send message
	 */
	private void submitMessage() {
		if (NetworkUtil.isNetworkConnected(this)) {
			String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
			String message = mCommentView.getText().toString().trim();

			mMessagePresenter.submitMessage(mUsername, message, deviceId);
		} else {
			//TODO
			mRecyclerView.setVisibility(View.GONE);
		}
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showMessages(List<Message> messages) {
		if (!messages.isEmpty()) {
			mNextMaxId = messages.get(messages.size() - 1).id;

			if (TextUtils.equals(mNextMaxId, "0")) {
				// After reaching the last one, deactivate loadmore
				mAdapter.setOnLoadMoreListener(null);
			}
			mAdapter.addMessages(mIsRefreshing, messages);
			mAdapter.setLoaded();
			mAdapter.notifyDataSetChanged();

			mBus.post(new DataLoadedEvent(false));
			mIsRefreshing = false;
		}
	}

	@Override
	public void updateMessages(Message message) {
		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		mAdapter.insertNewMessage(message);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void showError() {
		// TODO
	}

	@Override
	public void onClick(View v) {
		String commentText = mCommentView.getText().toString().trim();

		switch (v.getId()) {
			case R.id.close:
				mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
				break;

			case R.id.submit:
				if (TextUtils.isEmpty(commentText)) {
					new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
							.setTitleText(getString(R.string.enter_content))
							.setConfirmClickListener((sDialog, __) -> sDialog.dismissWithAnimation())
							.show();
				} else if (commentText.length() < MIN_MESSAGE_LENGTH) {
					new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
							.setTitleText(getString(R.string.enter_longer_content))
							.setConfirmClickListener((sDialog, __) -> sDialog.dismissWithAnimation())
							.show();
				} else {
					submitMessage();
				}
				break;

			case R.id.fab_write:
				if (!TextUtils.isEmpty(mUsername)) {
					mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
				} else {
					// Ask user to set user name
					new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
							.setTitleText(null)
							.setContentText(getString(R.string.set_username))
							.setConfirmText(getString(R.string.yes))
							.setConfirmClickListener((sDialog, input) -> {
								sDialog.dismissWithAnimation();

								startActivity(new Intent(MessageActivity.this, SettingsActivity.class));
								overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
							})
							.setCancelText(getString(R.string.no))
							.setCancelClickListener((sDialog, __) -> sDialog.dismissWithAnimation())
							.show();
				}
				break;
		}
	}

	@Subscribe
	public void onRefresh(DataLoadedEvent event) {
		onRefreshingStateChanged(event.refreshStarted);
	}
}
