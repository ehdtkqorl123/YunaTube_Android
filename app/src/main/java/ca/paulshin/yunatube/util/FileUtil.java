package ca.paulshin.yunatube.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import ca.paulshin.yunatube.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by paulshin on 16-02-26.
 */
public class FileUtil {
	public static File getAlbumDir(Context ctx) {
		return new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), ctx.getString(R.string.app_name));
	}

	public static File downloadFile(Context ctx, String src, String prefix) {
		if (!isExternalStorageWritable()) return null;

		File outputDir = getAlbumDir(ctx);
		if (!outputDir.mkdir()) {
			if (!outputDir.exists())
				return null;
		}

		String filename = TextUtils.isEmpty(prefix) ? new File(src).getName() : prefix + new File(src).getName();
		File outputFile = new File(outputDir, filename);

		try {
			OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
			clientBuilder.connectTimeout(3, TimeUnit.SECONDS);
			clientBuilder.readTimeout(3, TimeUnit.SECONDS);
			clientBuilder.writeTimeout(3, TimeUnit.SECONDS);

			OkHttpClient client = clientBuilder.build();

			Request request = new Request.Builder()
					.url(src)
					.build();
			Response response = client.newCall(request).execute();
			InputStream is = response.body().byteStream();

			OutputStream os = new FileOutputStream(outputFile);
			copyStream(is, os);
			os.close();

			return outputFile;
		} catch (Exception e) {
			return null;
		}
	}

	private static void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
}
