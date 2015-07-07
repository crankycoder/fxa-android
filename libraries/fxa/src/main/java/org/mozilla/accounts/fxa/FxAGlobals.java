/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.accounts.fxa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class FxAGlobals {

    public static String appVersionName;
    public static int appVersionCode;
    public static String appName;
    public static final String LOG_TAG = LoggerUtil.makeLogTag(FxAGlobals.class);

    private final BroadcastReceiver callbackReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intents.RECEIVE_BEARER_TOKEN)) {
                processReceiveBearerToken(intent);
            } else if (intent.getAction().equals(Intents.DISPLAY_NAME_WRITE)) {
                processDisplayNameWrite(intent, true);
            } else if (intent.getAction().equals(Intents.DISPLAY_NAME_WRITE_FAILURE)) {
                processDisplayNameWrite(intent, false);
            } else if (intent.getAction().equals(Intents.PROFILE_READ)) {
                processProfileRead(intent, true);
            } else if (intent.getAction().equals(Intents.PROFILE_READ_FAILURE)) {
                processProfileRead(intent, false);
            } else if (intent.getAction().equals(Intents.OAUTH_VERIFY)) {
                processOauthVerify(intent, true);
            } else if (intent.getAction().equals(Intents.OAUTH_VERIFY_FAILURE)) {
                processOauthVerify(intent, false);
            } else if (intent.getAction().equals(Intents.OAUTH_DESTROY)) {
                processOauthDestroy(intent, true);
            } else if (intent.getAction().equals(Intents.OAUTH_DESTROY_FAILURE)) {
                processOauthDestroy(intent, false);
            } else {
                Log.w(LOG_TAG, "Unexpected intent: " + intent);
            }
        }
    };
    private IFxACallbacks callbackSite = null;

    public void startIntentListening(Context ctx, String app_name) {
        // Clobber the FxAGlobals so that the user-agent for the FxA client will be sensible
        FxAGlobals.appVersionName = BuildConfig.VERSION_NAME;
        FxAGlobals.appVersionCode = BuildConfig.VERSION_CODE;
        FxAGlobals.appName = app_name;

        IntentFilter intentFilter = new IntentFilter();
        Intents.registerFxaIntents(intentFilter);

        LocalBroadcastManager
                .getInstance(ctx.getApplicationContext())
                .registerReceiver(callbackReceiver, intentFilter);
    }


    private void processReceiveBearerToken(Intent intent) {
        if (callbackSite == null) {
            return;
        }

        String jsonBlob = intent.getStringExtra("json");
        if (jsonBlob == null) {
            Log.w(LOG_TAG, "error extracting json data");
            return;
        }
        JSONObject authJSON = null;
        try {
            authJSON = new JSONObject(jsonBlob);
            Log.i(LOG_TAG, "Login yielded this JSON blob: " + authJSON);
            String bearerToken = authJSON.getString("access_token");
            callbackSite.processReceiveBearerToken(bearerToken);
        } catch (JSONException jse) {
            Log.e(LOG_TAG, "Error fetching bearer token. JSON = [" + authJSON + "]", jse);
        }
    }

    private void processOauthDestroy(Intent intent, boolean success) {
        if (callbackSite == null) {
            return;
        }
        if (!success) {
            callbackSite.failCallback(Intents.OAUTH_DESTROY);
            return;
        }

        callbackSite.processOauthDestroy();

    }

    private void processOauthVerify(Intent intent, boolean success) {
        if (callbackSite == null) {
            return;
        }

        if (!success) {
            callbackSite.failCallback(Intents.OAUTH_VERIFY);
        }

        callbackSite.processOauthVerify();
    }

    private void processProfileRead(Intent intent, boolean success) {
        if (callbackSite == null) {
            return;
        }

        if (!success) {
            callbackSite.failCallback(Intents.PROFILE_READ);
        }

        String jsonBlob = intent.getStringExtra("json");
        Log.i(LOG_TAG, "Profile response body: " + jsonBlob);
        if (jsonBlob == null) {
            Log.w(LOG_TAG, "error extracting json data");
            callbackSite.failCallback(Intents.PROFILE_READ);
            return;
        }

        try {
            JSONObject jObj = new JSONObject(jsonBlob);
            callbackSite.processProfileRead(jObj);
        } catch (JSONException e) {
            Log.w(LOG_TAG, "Error decoding JSON", e);
            callbackSite.failCallback(Intents.PROFILE_READ);
        }

    }


    private void processDisplayNameWrite(Intent intent, boolean success) {
        if (callbackSite == null) {
            return;
        }
        if (!success) {
            callbackSite.failCallback(Intents.DISPLAY_NAME_WRITE);
            return;
        }
        callbackSite.processDisplayNameWrite();
    }

    public void registerCallbacks(IFxACallbacks iFxACallbacks) {
        callbackSite = iFxACallbacks;
    }
}


