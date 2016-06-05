package ca.paulshin.yunatube.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.paulshin.yunatube.BuildConfig;
import ca.paulshin.yunatube.R;
import timber.log.Timber;

//import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by paulshin on 15-01-22.
 */
public class SplashActivity extends Activity {
	private FirebaseAuth mAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;

	private ImageView splash;

	private static final int SPLASH_DURATION = BuildConfig.DEBUG ? 100 : 1400;
	private static final int FADE_OUT_DURATION = BuildConfig.DEBUG ? 100 : 1600;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.a_splash);
		splash = (ImageView) findViewById(R.id.splash_image);
//
//		//april fools
//		if (Utils.isAprilFools()) {
//			splash.setImageResource(R.drawable.fool_splash);
//		}

		// Firebase settings
		mAuth = FirebaseAuth.getInstance();
		mAuthListener = firebaseAuth -> {
			FirebaseUser user = firebaseAuth.getCurrentUser();
			if (user != null) {
				// User is signed in
				Timber.d("onAuthStateChanged:signed_in:" + user.getUid());
			} else {
				// User is signed out
				Timber.d("onAuthStateChanged:signed_out");

				// Sign in to firebase
				signInAnonymously();
			}
		};

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation arg0) {
				Handler handler = new Handler();
				handler.postDelayed(() -> {
						Intent main = new Intent(SplashActivity.this, MainActivity.class);
						startActivity(main);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

						finish();
				}, FADE_OUT_DURATION);
			}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationStart(Animation animation) {}
		});
		animation.setDuration(SPLASH_DURATION);
		splash.startAnimation(animation);
	}

	@Override
	public void onStart() {
		super.onStart();
		mAuth.addAuthStateListener(mAuthListener);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mAuthListener != null) {
			mAuth.removeAuthStateListener(mAuthListener);
		}
	}

	private void signInAnonymously() {
		mAuth.signInAnonymously()
				.addOnCompleteListener(this, task -> {
					Timber.d("signInAnonymously:onComplete:" + task.isSuccessful());

					// If sign in fails, display a message to the user. If sign in succeeds
					// the auth state listener will be notified and logic to handle the
					// signed in user can be handled in the listener.
					if (!task.isSuccessful()) {
						Timber.e("signInAnonymously", task.getException());
					}
				});
	}
}
