package ca.paulshin.yunatube.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import ca.paulshin.yunatube.BuildConfig;
import timber.log.Timber;

/**
 * YT Preference class (More intuitive wrapper class for android Preference)
 * Created by paulshin on 9/16/14.
 */
public class YTPreference {

    private static Context sContext;
    private static SharedPreferences sPref;
    private static Editor sEdit;
    private static Gson sGson = new Gson();

    private YTPreference() {
    }

    public static void init(Context context) {
        YTPreference.sContext = context;
        Timber.tag("YT Preference");
        initPreference();
    }

    public static void initPreference() {
        if (sPref == null || sEdit == null) {
            YTPreference.sPref = PreferenceManager.getDefaultSharedPreferences(sContext);
            YTPreference.sEdit = YTPreference.sPref.edit();
        }
    }

    public static boolean contains(final String key) {
        initPreference();
        return YTPreference.sPref.contains(key);
    }

    public static Editor getEditor() {
        return sEdit;
    }

    public static boolean put(final String key, final int value) {
        initPreference();
        if (BuildConfig.DEBUG) Timber.w(key + " : " + value);
        return YTPreference.sEdit.putInt(key, value).commit();
    }

    public static boolean put(final String key, final long value) {
        initPreference();
        if (BuildConfig.DEBUG) Timber.w(key + " : " + value);
        return YTPreference.sEdit.putLong(key, value).commit();
    }

    public static boolean put(final String key, final float value) {
        initPreference();
        if (BuildConfig.DEBUG) Timber.w(key + " : " + value);
        return YTPreference.sEdit.putFloat(key, value).commit();
    }

    public static boolean put(final String key, final String value) {
        initPreference();
        if (BuildConfig.DEBUG) Timber.w(key + " : " + value);
        return YTPreference.sEdit.putString(key, value).commit();
    }

    public static boolean put(final String key, final boolean value) {
        initPreference();
        if (BuildConfig.DEBUG) Timber.w(key + " : " + value);
        return YTPreference.sEdit.putBoolean(key, value).commit();
    }

    public static boolean put(String key, Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object is null");
        }

        if (key.equals("") || key == null) {
            throw new IllegalArgumentException("key is empty or null");
        }

        return YTPreference.sEdit.putString(key, sGson.toJson(object)).commit();
    }

    public static <T> T get(String key, Class<T> a) {
        String gson = YTPreference.sPref.getString(key, null);
        if (gson == null) {
            return null;
        } else {
            try {
                return sGson.fromJson(gson, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object stored with key " + key + " is instanceof other class");
            }
        }
    }

    public static <T> T get(String key, Type a) {
        String gson = YTPreference.sPref.getString(key, null);
        if (gson != null) {
            try {
                return sGson.fromJson(gson, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object stored with key " + key + " is instanceof other class");
            }
        }

        return null;
    }

    public static int get(final String key, final int defValue) {
        initPreference();
        return YTPreference.sPref.getInt(key, defValue);
    }

    public static long get(final String key, final long defValue) {
        initPreference();
        return YTPreference.sPref.getLong(key, defValue);
    }

    public static float get(final String key, final float defValue) {
        initPreference();
        return YTPreference.sPref.getFloat(key, defValue);
    }

    public static String get(final String key, final String defValue) {
        initPreference();
        return YTPreference.sPref.getString(key, defValue);
    }

    public static boolean get(final String key, final boolean defValue) {
        initPreference();
        return YTPreference.sPref.getBoolean(key, defValue);
    }

    public static int getInt(final String key) {
        initPreference();
        return YTPreference.sPref.getInt(key, 0);
    }

    public static long getLong(final String key) {
        initPreference();
        return YTPreference.sPref.getLong(key, 0l);
    }

    public static float getFloat(final String key) {
        initPreference();
        return YTPreference.sPref.getFloat(key, 0f);
    }

    public static String getString(final String key) {
        initPreference();
        return YTPreference.sPref.getString(key, "");
    }

    public static boolean getBoolean(final String key) {
        initPreference();
        return YTPreference.sPref.getBoolean(key, false);
    }

    public static void clear() {
        initPreference();
        YTPreference.sEdit.clear().commit();
    }

    public static void remove(final String key) {
        initPreference();
        YTPreference.sEdit.remove(key).commit();
    }
}