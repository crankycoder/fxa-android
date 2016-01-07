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
            } else if (intent.getAction().equals(Intents.ACCESS_TOKEN_REFRESH)) {
                processRefreshToken(intent, true);
            } else if (intent.getAction().equals(Intents.ACCESS_TOKEN_REFRESH_FAILURE)) {
                processRefreshToken(intent, false);
            } else {
                Log.w(LOG_TAG, "Unexpected intent: " + intent.toString());
            }
        }
    };

    private IFxACallbacks callbackSite = null;

    public void startIntentListening(Context ctx, IFxACallbacks iFxACallbacks, String app_name) {
        // Clobber the FxAGlobals so that the user-agent for the FxA client will be sensible
        FxAGlobals.appVersionName = BuildConfig.VERSION_NAME;
        FxAGlobals.appVersionCode = BuildConfig.VERSION_CODE;
        FxAGlobals.appName = app_name;

        callbackSite = iFxACallbacks;

        IntentFilter intentFilter = new IntentFilter();
        Intents.registerFxaIntents(intentFilter);

        LocalBroadcastManager.getInstance(ctx.getApplicationContext())
                .registerReceiver(callbackReceiver, intentFilter);
    }


    private void processReceiveBearerToken(Intent intent) {
        if (callbackSite == null) {
            return;
        }

        String jsonBlob = intent.getStringExtra("json");
        if (jsonBlob == null) {
            Log.w(LOG_TAG, "Error extracting JSON data and bearer token from auth callback.");
            return;
        }
        JSONObject authJSON = null;
        try {
            authJSON = new JSONObject(jsonBlob);
            String bearerToken = authJSON.getString("access_token");
            callbackSite.processReceiveBearerToken(bearerToken);
            callbackSite.processRawResponse(authJSON);
        } catch (JSONException jse) {
            Log.e(LOG_TAG, "Error extracting bearer token from OAuth2 server.");
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
        if (jsonBlob == null) {
            Log.w(LOG_TAG, "JSON Blob from profile read was null.");
            callbackSite.failCallback(Intents.PROFILE_READ);
            return;
        }

        try {
            JSONObject jObj = new JSONObject(jsonBlob);
            callbackSite.processProfileRead(jObj);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error marshalling JSON from profile read.");
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



    private void processRefreshToken(Intent intent, boolean success) {
        if (callbackSite == null) {
            return;
        }
        if (!success) {
            callbackSite.failCallback(Intents.ACCESS_TOKEN_REFRESH);
            return;
        }

        try {
            String jsonBlob = intent.getStringExtra("json");
            JSONObject jObj = new JSONObject(jsonBlob);
            callbackSite.processRefreshToken(jObj);
        } catch (JSONException e) {
            Log.w(LOG_TAG, "Error processing refresh token JSON.");
            callbackSite.failCallback(Intents.PROFILE_READ);
        }
    }


}


