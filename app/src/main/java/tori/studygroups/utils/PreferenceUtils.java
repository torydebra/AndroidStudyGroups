package tori.studygroups.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {

    public static final String PREFERENCE_KEY_USERNAME = "username";
    public static final String PREFERENCE_KEY_EMAIL = "email";
    public static final String PREFERENCE_KEY_PASSWORD = "nickname";
    public static final String PREFERENCE_KEY_CONNECTED = "connected";

    // Prevent instantiation
    private PreferenceUtils() {

    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("sendbird", Context.MODE_PRIVATE);
    }

    public static void setEmail(Context context, String userId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREFERENCE_KEY_EMAIL, userId).apply();
    }

    public static String getEmail(Context context) {
        return getSharedPreferences(context).getString(PREFERENCE_KEY_EMAIL, "");
    }

    public static void setPassword(Context context, String nickname) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREFERENCE_KEY_PASSWORD, nickname).apply();
    }

    public static String getPassword(Context context) {
        return getSharedPreferences(context).getString(PREFERENCE_KEY_PASSWORD, "");
    }

    public static void setConnected(Context context, boolean tf) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(PREFERENCE_KEY_CONNECTED, tf).apply();
    }

    public static boolean getConnected(Context context) {
        return getSharedPreferences(context).getBoolean(PREFERENCE_KEY_CONNECTED, false);
    }

    public static void clearAll(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear().apply();
    }
}
