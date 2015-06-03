/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;

public class Prefs {

    private static final String LOG_TAG = LoggerUtil.makeLogTag(Prefs.class);

    public static final String NICKNAME_PREF = "nickname";
    public static final String EMAIL_PREF = "email";
    public static final String BEARER_TOKEN = "fxa_bearer_token";

    protected static final String PREFS_FILE = Prefs.class.getSimpleName();

    protected static Prefs sInstance;
    private final SharedPreferences mSharedPrefs;


    public static synchronized Prefs createInstance(Context c) {
        if (sInstance == null) {
            sInstance = new Prefs(c);
        }
        return sInstance;
    }

    public static synchronized Prefs getInstance() {
        return sInstance;
    }

    private Prefs(Context context) {
        mSharedPrefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
    }

    @TargetApi(9)
    protected static void apply(SharedPreferences.Editor editor) {
        if (VERSION.SDK_INT >= 9) {
            editor.apply();
        } else if (!editor.commit()) {
            Log.e(LOG_TAG, "", new IllegalStateException("commit() failed?!"));
        }
    }

    public synchronized String getBearerToken() {
        String token = getStringPref(BEARER_TOKEN);
        if (token != null) {
            token = token.trim();
        }
        return TextUtils.isEmpty(token) ? "" : token;
    }

    public synchronized void setBearerToken(String token) {
        if (token != null) {
            token = token.trim();
            setStringPref(BEARER_TOKEN, token);
        }
    }

    public synchronized String getNickname() {
        String nickname = getStringPref(NICKNAME_PREF);
        if (nickname != null) {
            nickname = nickname.trim();
        }
        return TextUtils.isEmpty(nickname) ? null : nickname;
    }

    public synchronized void setNickname(String nick) {
        if (nick != null) {
            nick = nick.trim();
            setStringPref(NICKNAME_PREF, nick);
        }
    }

    public synchronized String getEmail() {
        String email = getStringPref(EMAIL_PREF);
        if (email != null) {
            email = email.trim();
        }
        return TextUtils.isEmpty(email) ? null : email;
    }

    public synchronized void setEmail(String email) {
        if (email != null) {
            email = email.trim();
            setStringPref(EMAIL_PREF, email);
        }
    }

    ///
    /// Privates
    ///

    protected String getStringPref(String key) {
        return getPrefs().getString(key, null);
    }

    protected boolean getBoolPrefWithDefault(String key, boolean def) {
        return getPrefs().getBoolean(key, def);
    }

    protected void setBoolPref(String key, Boolean state) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putBoolean(key, state);
        apply(editor);
    }

    protected float getFloatPrefWithDefault(String key, float def) {
        return getPrefs().getFloat(key, def);
    }

    protected void setFloatPref(String key, float value) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putFloat(key, value);
        apply(editor);
    }

    protected void setLongPref(String key, long value) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putLong(key, value);
        apply(editor);
    }

    protected void setStringPref(String key, String value) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(key, value);
        apply(editor);
    }

    @SuppressLint("InlinedApi")
    protected SharedPreferences getPrefs() {
        return mSharedPrefs;
    }

    private float getFloatPref(String name, float value) {
        return getFloatPrefWithDefault(name, value);
    }


}
