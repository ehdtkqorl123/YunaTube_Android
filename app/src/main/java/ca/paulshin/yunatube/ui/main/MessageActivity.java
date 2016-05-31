package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import ca.paulshin.yunatube.util.ResourceUtil;
import ca.paulshin.yunatube.util.YTPreference;
import ca.paulshin.yunatube.util.events.DataLoadedEvent;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MessageActivity extends BaseActivity implements MessageMvpView {

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
	@Bind(R.id.comment_box)
	FrameLayout mCommentBox;
	@Bind(R.id.content)
	EditText mCommentView;

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

		mCommentView.setHint(ResourceUtil.getString(R.string.message_hint));
		mBottomSheetBehavior = BottomSheetBehavior.from(mCommentBox);

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
		if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
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
			mCommentView.setText("");
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

	public void comment(View view) {
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
	}

	public void submit(View view) {
		String commentText = mCommentView.getText().toString().trim();
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
	}

	public void closeCommentBox(View view) {
		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
	}

	@Subscribe
	public void onRefresh(DataLoadedEvent event) {
		onRefreshingStateChanged(event.refreshStarted);
	}
}
