package ca.paulshin.yunatube.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.firebase.AcrosticPoem;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.util.ToastUtil;
import ca.paulshin.yunatube.util.YTPreference;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static ca.paulshin.yunatube.R.id.first;

/**
 * Created by paulshin on 2016-06-05.
 */

public class AcrosticPoemActivity extends BaseActivity {

	@Bind(R.id.poems)
	public RecyclerView mMessageRecyclerView;
	@Bind(R.id.loading)
	public View mLoadingView;

	private static final String POEMS_CHILD = "acrostic_poem";
	private static final int ROW_COUNT = 6;
	private static final int MIN_LENGTH = 4;

	private LinearLayout mWriteLayout;
	private String mUsername;
	private String mAcrosticText;

	// Firebase instance variables
	private DatabaseReference mFirebaseDatabaseReference;
	private FirebaseRecyclerAdapter<AcrosticPoem, PoemViewHolder> mFirebaseAdapter;

	private LinearLayoutManager mLinearLayoutManager;
	private BottomSheetDialog mBottomSheetDialog;

	public static class PoemViewHolder extends RecyclerView.ViewHolder {
		public TextView headerView;
		public TextView nameView;
		public TextView firstView;
		public TextView secondView;
		public TextView thirdView;
		public TextView fourthView;
		public TextView fifthView;
		public TextView sixthView;
//		public CircleImageView poemImageView;

		public PoemViewHolder(View v) {
			super(v);
			headerView = (TextView) itemView.findViewById(R.id.acrostic_header);
			nameView = (TextView) itemView.findViewById(R.id.name);
			firstView = (TextView) itemView.findViewById(first);
			secondView = (TextView) itemView.findViewById(R.id.second);
			thirdView = (TextView) itemView.findViewById(R.id.third);
			fourthView = (TextView) itemView.findViewById(R.id.fourth);
			fifthView = (TextView) itemView.findViewById(R.id.fifth);
			sixthView = (TextView) itemView.findViewById(R.id.sixth);
//			poemImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
		}
	}

	@Override
	protected String getScreenName() {
		return "android - acrostic poem";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_acrostic_poem);
		ButterKnife.bind(this);

		setupToolbar();

		// Initialize acrostic text
		mAcrosticText = YTPreference.getString(MainMenuFragment.KEY_ACROSTIC_TEXT);
		if (TextUtils.isEmpty(mAcrosticText)) {
			ToastUtil.toast(this, R.string.acrostic_fail);
			finish();
			return;
		}

		// Initialize views
		mLinearLayoutManager = new LinearLayoutManager(this);
		mLinearLayoutManager.setReverseLayout(true);

		// New child entries
		mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
		mFirebaseAdapter = new FirebaseRecyclerAdapter<AcrosticPoem, PoemViewHolder>(
				AcrosticPoem.class,
				R.layout.r_acrostic_poem,
				PoemViewHolder.class,
				mFirebaseDatabaseReference.child(POEMS_CHILD)) {

			@Override
			protected void populateViewHolder(PoemViewHolder viewHolder,
											  AcrosticPoem acrosticPoem, int position) {
				mLoadingView.setVisibility(ProgressBar.INVISIBLE);

				if (position == getItemCount() - 1) {
					viewHolder.headerView.setVisibility(View.VISIBLE);
					String headerText = getString(R.string.acrostic_text) + " <b>" + mAcrosticText + "</b>";
					viewHolder.headerView.setText(Html.fromHtml(headerText));
				} else {
					viewHolder.headerView.setVisibility(View.GONE);
				}

				String name = acrosticPoem.getName();
				String text = acrosticPoem.getText();
				String first = acrosticPoem.getFirst();
				String second = acrosticPoem.getSecond();
				String third = acrosticPoem.getThird();
				String fourth = acrosticPoem.getFourth();
				String fifth = acrosticPoem.getFifth();
				String sixth = acrosticPoem.getSixth();
				String photoUrl = acrosticPoem.getPhotoUrl();

				viewHolder.nameView.setVisibility(TextUtils.isEmpty(name) ? View.GONE : View.VISIBLE);
				viewHolder.nameView.setText(acrosticPoem.getName());
				if (TextUtils.isEmpty(first)) {
					viewHolder.firstView.setVisibility(View.GONE);
				} else {
					viewHolder.firstView.setVisibility(View.VISIBLE);
					viewHolder.firstView.setText(getFirstLetterBoldText(acrosticPoem.getFirst()));
				}
				if (TextUtils.isEmpty(second)) {
					viewHolder.secondView.setVisibility(View.GONE);
				} else {
					viewHolder.secondView.setVisibility(View.VISIBLE);
					viewHolder.secondView.setText(getFirstLetterBoldText(acrosticPoem.getSecond()));
				}
				if (TextUtils.isEmpty(third)) {
					viewHolder.thirdView.setVisibility(View.GONE);
				} else {
					viewHolder.thirdView.setVisibility(View.VISIBLE);
					viewHolder.thirdView.setText(getFirstLetterBoldText(acrosticPoem.getThird()));
				}
				if (TextUtils.isEmpty(fourth)) {
					viewHolder.fourthView.setVisibility(View.GONE);
				} else {
					viewHolder.fourthView.setVisibility(View.VISIBLE);
					viewHolder.fourthView.setText(getFirstLetterBoldText(acrosticPoem.getFourth()));
				}
				if (TextUtils.isEmpty(fifth)) {
					viewHolder.fifthView.setVisibility(View.GONE);
				} else {
					viewHolder.fifthView.setVisibility(View.VISIBLE);
					viewHolder.fifthView.setText(getFirstLetterBoldText(acrosticPoem.getFifth()));
				}
				if (TextUtils.isEmpty(sixth)) {
					viewHolder.sixthView.setVisibility(View.GONE);
				} else {
					viewHolder.sixthView.setVisibility(View.VISIBLE);
					viewHolder.sixthView.setText(getFirstLetterBoldText(acrosticPoem.getSixth()));
				}

//				if (acrosticPoem.getPhotoUrl() == null) {
//					viewHolder.messengerImageView
//							.setImageDrawable(ContextCompat
//									.getDrawable(MainActivity.this,
//											R.drawable.ic_account_circle_black_36dp));
//				} else {
//					Glide.with(AcrosticPoemActivity.this)
//							.load(friendlyMessage.getPhotoUrl())
//							.into(viewHolder.messengerImageView);
//				}
			}
		};

		mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				mMessageRecyclerView.scrollToPosition(positionStart);
			}
		});

		mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
		mMessageRecyclerView.setAdapter(mFirebaseAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mUsername = YTPreference.getString(SettingsActivity.PREF_USERNAME);
	}

	private void createDialog() {
		mWriteLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.p_acrostic_poem_write, null);

		int rows = mAcrosticText.length();

		for (int i = ROW_COUNT; i > rows; i--) {
			mWriteLayout.removeViewAt(i);
		}

		for (int i = 1; i <= rows; i++) {
			// First letter
			LinearLayout currentRow = (LinearLayout)mWriteLayout.getChildAt(i);
			TextView firstLetterView = (TextView)currentRow.getChildAt(0);
			firstLetterView.setText(String.valueOf(mAcrosticText.charAt(i - 1)));
		}

		mBottomSheetDialog = new BottomSheetDialog(this);
		mBottomSheetDialog.setContentView(mWriteLayout);
		mBottomSheetDialog.show();
	}

	public void write(View view) {
		if (!TextUtils.isEmpty(mUsername)) {
			createDialog();
		} else {
			// Ask user to set user name
			new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
					.setTitleText(null)
					.setContentText(getString(R.string.set_username))
					.setConfirmText(getString(R.string.yes))
					.setConfirmClickListener((sDialog, input) -> {
						sDialog.dismissWithAnimation();

						startActivity(new Intent(AcrosticPoemActivity.this, SettingsActivity.class));
						overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
					})
					.setCancelText(getString(R.string.no))
					.setCancelClickListener((sDialog, __) -> sDialog.dismissWithAnimation())
					.show();
		}
	}

	public void submit(View view) {
		EditText firstView = ButterKnife.findById(mWriteLayout, R.id.first_input);
		EditText secondView = ButterKnife.findById(mWriteLayout, R.id.second_input);
		EditText thirdView = ButterKnife.findById(mWriteLayout, R.id.third_input);
		EditText fourthView = ButterKnife.findById(mWriteLayout, R.id.fourth_input);
		EditText fifthView = ButterKnife.findById(mWriteLayout, R.id.fifth_input);
		EditText sixthView = ButterKnife.findById(mWriteLayout, R.id.sixth_input);

		String first = getAcrosticText(0, firstView);
		String second = getAcrosticText(1, secondView);
		String third = getAcrosticText(2, thirdView);
		String fourth = getAcrosticText(3, fourthView);
		String fifth = getAcrosticText(4, fifthView);
		String sixth = getAcrosticText(5, sixthView);

		if (first != null && second != null && third != null && fourth != null && fifth != null && sixth != null) {
			AcrosticPoem poem = new AcrosticPoem(mUsername, mAcrosticText, first, second, third, fourth, fifth, sixth, "");
			mFirebaseDatabaseReference.child(POEMS_CHILD).push().setValue(poem);

			clearView(firstView);
			clearView(secondView);
			clearView(thirdView);
			clearView(fourthView);
			clearView(fifthView);
			clearView(sixthView);
			mBottomSheetDialog.dismiss();
		}
	}

	private String getAcrosticText(int index, EditText view) {
		String result;
		if (view != null) {
			result = view.getText().toString().trim();
			if (TextUtils.isEmpty(result)) {
				ToastUtil.toast(this, R.string.acrostic_empty);
				return null;
			} else if (result.length() < MIN_LENGTH) {
				ToastUtil.toast(this, R.string.acrostic_too_short);
				return null;
			} else {
				result = mAcrosticText.charAt(index) + result;
				return result;
			}
		} else {
			return "";
		}
	}

	private void clearView(EditText view) {
		if (view != null) {
			view.setText("");
		}
	}

	private Spanned getFirstLetterBoldText(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("<b>");
		sb.append(text.charAt(0));
		sb.append("</b>");
		sb.append(text.substring(1));

		return Html.fromHtml(sb.toString());
	}
}
