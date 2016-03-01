package ca.paulshin.yunatube.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by paulshin on 16-02-13.
 */
public class ToastUtil {
	public static void toast(Context context, CharSequence charSequence) {
		Toast.makeText(context, charSequence, Toast.LENGTH_SHORT).show();
	}

	public static void toastOnTopRight(Context context, CharSequence charSequence) {
		Toast toast = Toast.makeText(context, charSequence, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.RIGHT, 40, 40);
		toast.show();
	}

	public static void toastOnCenter(Context context, CharSequence charSequence) {
		Toast toast = Toast.makeText(context, charSequence, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 240);
		toast.show();
	}

	public static void toast(Context context, int resId) {
		if (context != null)
			Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
}
