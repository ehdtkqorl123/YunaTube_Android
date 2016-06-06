package ca.paulshin.yunatube.ui.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.firebase.AcrosticPoem;
import ca.paulshin.yunatube.ui.base.BaseActivity;

/**
 * Created by paulshin on 2016-06-05.
 */

public class AcrosticPoemActivity extends BaseActivity {

	@Bind(R.id.poems)
	public RecyclerView mMessageRecyclerView;
	@Bind(R.id.loading)
	public View mLoadingView;

	public static final String POEMS_CHILD = "acrostic_poem";

	// Firebase instance variables
	private FirebaseAuth mFirebaseAuth;
	private FirebaseUser mFirebaseUser;

	// Firebase instance variables
	private DatabaseReference mFirebaseDatabaseReference;
	private FirebaseRecyclerAdapter<AcrosticPoem, PoemViewHolder> mFirebaseAdapter;

	private LinearLayoutManager mLinearLayoutManager;


	public static class PoemViewHolder extends RecyclerView.ViewHolder {
		public TextView cardView;
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
			nameView = (TextView) itemView.findViewById(R.id.name);
			firstView = (TextView) itemView.findViewById(R.id.first);
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

		// Initialize views
		mLinearLayoutManager = new LinearLayoutManager(this);
		mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

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
				mMessageRecyclerView.scrollToPosition(0);
			}
		});

		mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
		mMessageRecyclerView.setAdapter(mFirebaseAdapter);

//		mMessageEditText = (EditText) findViewById(R.id.messageEditText);
//		mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
//				.getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
//		mMessageEditText.addTextChangedListener(new TextWatcher() {ic_launcher.png
//			@Override
//			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//			}
//
//			@Override
//			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//				if (charSequence.toString().trim().length() > 0) {
//					mSendButton.setEnabled(true);
//				} else {
//					mSendButton.setEnabled(false);
//				}
//			}
//
//			@Override
//			public void afterTextChanged(Editable editable) {
//			}
//		});
//
//		mSendButton = (Button) findViewById(R.id.sendButton);
//		mSendButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				FriendlyMessage friendlyMessage = new
//						FriendlyMessage(mMessageEditText.getText().toString(),
//						mUsername,
//						mPhotoUrl);
//				mFirebaseDatabaseReference.child(MESSAGES_CHILD)
//						.push().setValue(friendlyMessage);
//				mMessageEditText.setText("");
//			}
//		});
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