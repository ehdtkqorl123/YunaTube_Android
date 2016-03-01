package ca.paulshin.yunatube.util;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import ca.paulshin.yunatube.R;

/**
 * Created by paulshin on 16-02-16.
 */
public class PicassoUtil {
	public static Context ctx;

	public static void init(Context context) {
		ctx = context;
	}

	public static void loadImage(String url, ImageView imageView, int placeholder) {
		Picasso.with(ctx)
				.load(url)
				.placeholder(placeholder != 0 ? placeholder : R.drawable.placeholder_gray)
				.into(imageView);
	}

	public static void loadImage(String url, ImageView imageView) {
		Picasso.with(ctx)
				.load(url)
				.into(imageView);
	}

	public static void loadImage(String url, ImageView imageView, Callback callback) {
		Picasso.with(ctx)
				.load(url)
				.into(imageView, callback);
	}

	public static void loadImage(String url, ImageView imageView, int width, int height, int placeholder) {
		Picasso.with(ctx)
				.load(url)
				.resize(width, height)
				.placeholder(placeholder != 0 ? placeholder : R.drawable.placeholder_gray)
				.into(imageView);
	}

	public static void loadImage(String url, Target target, int placeholder) {
		Picasso.with(ctx)
				.load(url)
				.placeholder(placeholder != 0 ? placeholder : R.drawable.placeholder_gray)
				.into(target);
	}
}
