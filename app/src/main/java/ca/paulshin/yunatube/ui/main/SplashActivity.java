package ca.paulshin.yunatube.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import ca.paulshin.yunatube.BuildConfig;
import ca.paulshin.yunatube.R;

/**
 * Created by paulshin on 15-01-22.
 */
public class SplashActivity extends Activity {
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
}
