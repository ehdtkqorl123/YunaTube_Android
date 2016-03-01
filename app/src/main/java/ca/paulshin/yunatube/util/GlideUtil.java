package ca.paulshin.yunatube.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import ca.paulshin.yunatube.R;

/**
 * Created by paulshin on 16-02-16.
 */
public class GlideUtil {
	public static Context ctx;

	public static void init(Context context) {
		ctx = context;
	}

	public static void loadImage(String url, ImageView imageView, int placeholder) {
		Glide.with(ctx)
				.load(url)
				.placeholder(placeholder != 0 ? placeholder : R.drawable.placeholder_gray)
				.into(imageView);
	}

	public static void loadImage(String url, ImageView imageView) {
		Glide.with(ctx)
				.load(url)
				.into(imageView);
	}

	public static void loadImage(File file, ImageView imageView) {
		Glide.with(ctx)
				.load(file)
				.into(imageView);
	}
}
